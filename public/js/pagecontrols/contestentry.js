// Author: Scott Gay
define([
        "assets/js/pagecontrols/base.js",
        "assets/js/libraries/jquery.min.js",

        "assets/js/models/contestid.js",
        "assets/js/models/mappings.js",
        "assets/js/models/lineuprules.js",
	"assets/js/models/quicklineupenter.js",
        "assets/js/models/contestssuggestions.js",

        "assets/js/subpagecontrols/contestrow.js",
        "assets/js/subpagecontrols/contestgames.js",
        "assets/js/subpagecontrols/lineupbuilderavailableathletes.js",
        "assets/js/subpagecontrols/lineupbuilderselectedathletes.js",
        "assets/js/models/contest.js"
], function (Base) {
        ruckus.pagecontrols.contestentry = function (parameters) {
                Base.call(this);
                this.init = function () {
                        this.parameters = parameters;
                };
                this.init();
        };

        ruckus.pagecontrols.contestentry.prototype = Object.create(Base.prototype);

        ruckus.pagecontrols.contestentry.prototype.load = function () {
                var _this = this;
                this.getContainer();
                this.container.addClass('ruckus-pc-contestentry');

                if (this.parameters.contestIds != undefined) {
                        // load contest socket
                        _this.contestIdModel = new ruckus.models.contestid({});
                        _this.models.push(_this.contestIdModel);
                        var sub2 = _this.msgBus.subscribe("model.contestid.all", function (data) {
                                sub2.unsubscribe();
                                _this.parameters.contest = data.data.contests[0];
                                _this.afterDataReady();
                        });
                        _this.contestIdModel.fetch({ contestid: this.parameters.contestId });
                } else if (this.parameters.contest == undefined) {
                        // load contest socket
                        _this.contestIdModel = new ruckus.models.contestid({});
                        _this.models.push(_this.contestIdModel);
                        var sub3 = _this.msgBus.subscribe("model.contestid.all", function (data) {
                                sub3.unsubscribe();
                                _this.parameters.contest = data.data.contests[0];
                                _this.afterDataReady();
                        });
                        _this.contestIdModel.fetch({ contestid: this.parameters.contestId });
                } else {
                        _this.afterDataReady();
                }

                var subSuccess = this.msgBus.subscribe("control.lbsa.lineupentersuccess", function (data) {
                        $('#contestentrydisplay').hide();
			if (data.result.data.payload[0].code == 0)
                                _this.renderSuccess(data);
			else if (data.result.data.payload[0].code == 11){
				_this.renderFailedFunds(data);
                        } else {
                                _this.renderFailed(data);
			}
                });
                _this.subscriptions.push(subSuccess);
//		var subFailed = this.msgBus.subscribe("control.lbsa.lineupenterfailed", function(data){
//			$('#contestentrydisplay').hide();
//			_this.renderFailed(data);
//		});
//		_this.subscriptions.push(subFailed);
        };

        ruckus.pagecontrols.contestentry.prototype.renderSuccess = function (data) {
                var _this = this;
                _this.contestsSuggestionsModel = new ruckus.models.contestssuggestions({});
                _this.models.push(_this.contestsSuggestionsModel);
                var sub2 = _this.msgBus.subscribe("model.contestssuggestions.retrieve", function (dataSM) {
                        sub2.unsubscribe();
                        $.each(_this.contestsSuggestionsModel.modelData.additionalContests, function (key, value) {
                                value.formattedEntryFee = _this.formatMoney(value.entryFee);
                                value.formattedPrizePool = _this.formatMoney(value.prizePool);
                                value.contestnamefull = _this.formatContestName(value, '1linefull').line1;
                        });
                        _this.require_template('contestentrysuccess-tpl');
                        dust.render('dusttemplates/contestentrysuccess-tpl', _this.contestsSuggestionsModel.modelData, function (err, out) {
                                $('#contestentrysuccess').html(out);
				_this.addScrollBars();
				/*if (_this.contestsSuggestionsModel.modelData.additionalContests.length == 0){
                                        $('<div>',{'style':'font-size:14pt;'}).appendTo($('#contestentrysuccess')).html('There are no additional contests to suggest');;
                                }*/
                                $.each(_this.contestsSuggestionsModel.modelData.additionalContests, function (key, value) {
                                        var cont = $('#lbsa_successmultiple_' + value.contestId);
                                        for (var x = 1; x <= value.remainingAllowedEntries; x++) {
                                                $('<option>', {'value': x}).html(x).appendTo(cont);
					}
					if (value.remainingAllowedEntries == 1){
						cont.hide();
					}
                                });

                                $('.lbsa_successcheck').bind('click', function (evt) {
                                        evt.stopPropagation();
                                        if ($('#' + evt.delegateTarget.id).hasClass('selected'))
                                                $('#' + evt.delegateTarget.id).removeClass('selected');
                                        else
                                                $('#' + evt.delegateTarget.id).addClass('selected');
                                });

                                $('#lbsa_successenter').bind('click', function (evt) {
                                        evt.stopPropagation();
					$('#lbsa_successenter').unbind('click');
					var params = {
						lineupId : data.result.data.payload[0].lineupId,
						entries : []
					};
                                        $.each(_this.contestsSuggestionsModel.modelData.additionalContests, function (key, value) {
						if ($( "#lbsa_successcheck_" + value.contestId).hasClass("selected")){
							params.entries.push({contestId:value.contestId,multiple:parseInt($( "#lbsa_successmultiple_" + value.contestId + " option:selected" ).val())});
						}
                                        });
					_this.consolelog(params);
					_this.quickLineupEnterModel = new ruckus.models.quicklineupenter({});
					_this.models.push(_this.quickLineupEnterModel);
					var subSuccess = undefined;
					var subFailed = undefined;
					subSuccess = _this.msgBus.subscribe("model.quicklineupenter.success", function (data) {
						subSuccess.unsubscribe();	
						subFailed.unsubscribe();
						$.each(_this.quickLineupEnterModel.modelData.payload, function(k,v){
							$('#lbsa_nomessage_'+v.contestId).hide();
							if (v.stopCode == 0 && v.code == 0){
								$('#lbsa_successmessage_'+v.contestId).show();
								$('#lbsa_successmessage_'+v.contestId).html('Entered');
							} else if (v.code != 0){
								$('#lbsa_failedmessage_'+v.contestId).show();
                                                                $('#lbsa_failedmessage_'+v.contestId).html('Failed');
							} else {
								$('#lbsa_failedmessage_'+v.contestId).show();
                                                                $('#lbsa_failedmessage_'+v.contestId).html('Failed');
							}
						});
		
					});
					subFailed = _this.msgBus.subscribe("model.quicklineupenter.failed", function (data) {
						subSuccess.unsubscribe();
						subFailed.unsubscribe();
						$.each(_this.quickLineupEnterModel.modelData.payload, function(k,v){
							$('#lbsa_failedmessage_'+v.contestId).show();
		                                        $('#lbsa_failedmessage_'+v.contestId).html('Failed');
						});
					});
					_this.quickLineupEnterModel.fetch(params);
			
                                });
                        });
                });
                _this.contestsSuggestionsModel.fetch({contestId: data.contestId, lineupId: data.result.data.payload[0].lineupId, success: 0});
        };

        ruckus.pagecontrols.contestentry.prototype.renderFailed = function (data) {
                var _this = this;
                _this.contestsSuggestionsModel = new ruckus.models.contestssuggestions({});
                _this.models.push(_this.contestsSuggestionsModel);
                var sub2 = _this.msgBus.subscribe("model.contestssuggestions.retrieve", function (dataSM) {
                        sub2.unsubscribe();
                        $.each(_this.contestsSuggestionsModel.modelData.additionalContests, function (key, value) {
                                value.formattedEntryFee = _this.formatMoney(value.entryFee);
                                value.formattedPrizePool = _this.formatMoney(value.prizePool);
                                value.contestnamefull = _this.formatContestName(value, '1linefull').line1;
                        });
                        _this.require_template('contestentryfailed-tpl');
                        dust.render('dusttemplates/contestentryfailed-tpl', _this.contestsSuggestionsModel.modelData, function (err, out) {
                                $('#contestentryfailed').html(out);
				_this.addScrollBars();
				/*if (_this.contestsSuggestionsModel.modelData.additionalContests.length == 0){
					$('<div>',{'style':'font-size:14pt;'}).appendTo($('#contestentryfailed')).html('There are no additional contests to suggest');;
				}*/
				switch (data.result.data.payload[0].code){
					case 1: 
						$('#lbsa_successfailedmessage').html('CONTEST FULL');
						break;
					case 2: 
						$('#lbsa_successfailedmessage').html('CONTEST ALREADY STARTED');
						break;
					case 3: 
						$('#lbsa_successfailedmessage').html('CONTEST NOT CURRENTLY OPEN');
						break;
					case 4: 
						$('#lbsa_successfailedmessage').html('CONTEST ENTER FAILED');
						break;
					case 5: 
						$('#lbsa_successfailedmessage').html('DUPLICATE ENTRY');
						break;
					case 6: 
						$('#lbsa_successfailedmessage').html('INCOMPATIBLE LINEUP WITH THIS CONTEST');
						break;
					case 7: 
						$('#lbsa_successfailedmessage').html('USER SESSION EXPIRED');
						break;
					case 8: 
						$('#lbsa_successfailedmessage').html('NOT ENOUGH SPORT EVENTS REPRESENTED');
						break;
					case 9: 
						$('#lbsa_successfailedmessage').html('EXCEEDED SALARY CAP');
						break;
					case 10: 
						$('#lbsa_successfailedmessage').html('CONTEST ENTER FAILED');
						break;
					case 11: 
						$('#lbsa_successfailedmessage').html('INSUFFICIENT FUNDS');
						break;
				}
                                $.each(_this.contestsSuggestionsModel.modelData.additionalContests, function (key, value) {
                                        var cont = $('#lbsa_successmultiple_' + value.contestId);
                                        for (var x = 1; x <= value.remainingAllowedEntries; x++) {
                                                $('<option>', {'value': x}).html(x).appendTo(cont);
                                        }
					if (value.remainingAllowedEntries == 1){
						cont.hide();
					}

                                });

                                $('.lbsa_successcheck').bind('click', function (evt) {
                                        evt.stopPropagation();
                                        if ($('#' + evt.delegateTarget.id).hasClass('selected'))
                                                $('#' + evt.delegateTarget.id).removeClass('selected');
                                        else
                                                $('#' + evt.delegateTarget.id).addClass('selected');
                                });

                                $('#lbsa_successenter').bind('click', function (evt) {
                                        evt.stopPropagation();
					$('#lbsa_successenter').unbind('click');
					var params = {
						lineupId : data.result.data.payload[0].lineupId,
						athletes : data.athletes, 
						entries : []
					};
                                        $.each(_this.contestsSuggestionsModel.modelData.additionalContests, function (key, value) {
						if ($( "#lbsa_successcheck_" + value.contestId).hasClass("selected")){
							params.entries.push({contestId:value.contestId,multiple:parseInt($( "#lbsa_successmultiple_" + value.contestId + " option:selected" ).val())});
						}
                                        });
					_this.consolelog(params);
//					_this.quickLineupEnterModel = new ruckus.models.quicklineupenter({});
//					_this.models.push(_this.quickLineupEnterModel);
					_this.lineupEnterModel = new ruckus.models.lineupenter({});
					_this.models.push(_this.lineupEnterModel);
					var subSuccess = undefined;
					var subFailed = undefined;
					subSuccess = _this.msgBus.subscribe("model.lineupenter.success", function (data) {
						subSuccess.unsubscribe();	
						subFailed.unsubscribe();
						$.each(_this.lineupEnterModel.modelData.payload, function(k,v){
							$('#lbsa_nomessage_'+v.contestId).hide();
							if (v.stopCode == 0 && v.code == 0){
								$('#lbsa_successmessage_'+v.contestId).show();
								$('#lbsa_successmessage_'+v.contestId).html('Entered');
							} else if (v.code != 0){
								$('#lbsa_failedmessage_'+v.contestId).show();
                                                                $('#lbsa_failedmessage_'+v.contestId).html('Failed');
							} else {
								$('#lbsa_failedmessage_'+v.contestId).show();
                                                                $('#lbsa_failedmessage_'+v.contestId).html('Failed');
							}
						});
		
					});
					subFailed = _this.msgBus.subscribe("model.quicklineupenter.failed", function (data) {
						subSuccess.unsubscribe();
						subFailed.unsubscribe();
						$.each(_this.quickLineupEnterModel.modelData.payload, function(k,v){
							$('#lbsa_failedmessage_'+v.contestId).show();
		                                        $('#lbsa_failedmessage_'+v.contestId).html('Failed');
						});
					});
					_this.lineupEnterModel.fetch(params);
//					_this.quickLineupEnterModel.fetch(params);
			
                                });
                        });
                });
                _this.contestsSuggestionsModel.fetch({contestId: data.contestId, lineupId:-1, success: data.result.data.payload[0].code});

        };

	ruckus.pagecontrols.contestentry.prototype.renderFailedFunds = function(data){
		var _this = this;
		_this.require_template('contestentryfailedfunds-tpl');
                dust.render('dusttemplates/contestentryfailedfunds-tpl', {}, function (err, out) {
			$('#contestentryfailed').html(out);
			_this.addScrollBars();
			$('#lbsa_successfailedfundsmessage').html("INSUFFICIENT FUNDS");
		});
	};

        ruckus.pagecontrols.contestentry.prototype.afterDataReady = function () {
                var _this = this;
                _this.require_template('contestentry-tpl');
                dust.render('dusttemplates/contestentry-tpl', {}, function (err, out) {
                        _this.container.html(out);
			_this.addScrollBars();
                        _this.log({type: 'general', data: _this.parameters.lobby, msg: 'CONTEST ENTRY LOADED FROM LOBBY'});

                        if (_this.parameters.contest != undefined) {
                                _this.log({type: 'general', data: _this.parameters.data, msg: 'FULL CONTEST DATA'});
                                _this.log({type: 'general', data: _this.parameters.contest, msg: 'SINGLE CONTEST DATA'});
                                _this.render(_this.parameters.data, _this.parameters.contest);
                        } else {
                                _this.log({type: 'general', data: _this.parameters.filter, msg: 'QUICK PLAY FILTER DATA'});
                                _this.contestModel = new ruckus.models.contest({});
                                var sub = _this.msgBus.subscribe("model.contestquickplay.retrieve", function (data) {
                                        sub.unsubscribe();
                                        _this.log({type: 'general', data: data, msg: 'FULL CONTEST DATA'});
                                        _this.log({type: 'general', data: data.data.contest, msg: 'SINGLE CONTEST DATA'});
                                        _this.render(data, data.data.contest);
                                });
                                _this.contestModel.fetchQuickPlay(_this.parameters.filter);
                        }
                });
        };

        ruckus.pagecontrols.contestentry.prototype.render = function (data, contest) {
                var _this = this;

                _this.lineupRulesModel = new ruckus.models.lineuprules({});
                _this.models.push(_this.lineupRulesModel);
                var sub3 = _this.msgBus.subscribe("model.lineuprules.retrieve", function (rules) {
                        sub3.unsubscribe();
                        var contestgames = new ruckus.subpagecontrols.contestgames({
                                container: $('#contestentrycontestgames'),
                                data: data,
                                contest: contest
                        });
                        contestgames.load();
                        _this.controls.push(contestgames);

                        var contestrow = new ruckus.subpagecontrols.contestrow({
                                container: $('#contestentrycontestrow'),
                                data: data,
                                contest: contest
                        });
                        contestrow.load();
                        _this.controls.push(contestrow);

                        var lineupbuilderselectedathletes = new ruckus.subpagecontrols.lineupbuilderselectedathletes({
                                container: $('#contestentryselectedathletes'),
                                data: data,
                                contest: contest,
                                lineuprules: _this.lineupRulesModel.modelData
                        });
                        lineupbuilderselectedathletes.load();
                        _this.controls.push(lineupbuilderselectedathletes);

                        var lineupbuilderavailableathletes = new ruckus.subpagecontrols.lineupbuilderavailableathletes({
                                container: $('#contestentryavailableathletes'),
                                data: data,
                                contest: contest,
                                lineupbuilderselectedathletes: lineupbuilderselectedathletes,
                                lineuprules: _this.lineupRulesModel.modelData
                        });
                        lineupbuilderavailableathletes.load();
                        _this.controls.push(lineupbuilderavailableathletes);
                });
                _this.lineupRulesModel.fetch({league: contest.league});
        };

        ruckus.pagecontrols.contestentry.prototype.unload = function () {
                this.destroyControl();
        };

        return ruckus.pagecontrols.contestentry;
});


