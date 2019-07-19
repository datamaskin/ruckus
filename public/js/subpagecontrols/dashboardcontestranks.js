// Author: Scott Gay
define([
        "assets/js/subpagecontrols/base.js",
        "assets/js/libraries/jquery.min.js",
//	"assets/js/libraries/underscore-min.js"
        "assets/js/libraries/dust-core.min.js"
], function (Base) {
        ruckus.subpagecontrols.dashboardcontestranks = function (parameters) {
                var _this = this;
                Base.call(_this);
                _this.parameters = parameters;

                _this.sortDetails = {
                        fpp: 'asc'
                };

                _.bindAll(_this, "renderRankList");
        };

        ruckus.subpagecontrols.dashboardcontestranks.prototype = Object.create(Base.prototype);
        ruckus.subpagecontrols.dashboardcontestranks.prototype.load = function () {
                var _this = this;
                this.getContainer();
                this.container.addClass('ruckus-spc-dashboardcontestranks');
//		_this.parameters.contest.formattedEntryFee = this.formatMoney(_this.parameters.contest.entryFee);
		_this.parameters.contest.formattedPrizePool = this.formatMoney(_this.parameters.contest.prizePool);
                // dhco
                this.require_template('dashboardcontestranks-tpl');
                dust.render('dusttemplates/dashboardcontestranks-tpl', {contest: _this.parameters.contest}, function (err, out) {
                        _this.container.html(out);
			_this.addScrollBars();
                        _this.log({type: 'API', data: _this.parameters.contestdetailranks, msg: 'CONTEST DETAIL RANKS MODEL'});

                        _this.ranklist = _this.parameters.contestdetailranks;
                        _this.sortMe('fpp');
                        _this.calcPrizes();
                        _this.renderRankList();

                        // find first instance of isMe and render lineup
                        var firstLineup = undefined;
                        $.each(_this.ranklist, function (key, value) {
                                if (value.isMe && firstLineup == undefined)
                                        firstLineup = value;
                        });
                        if (firstLineup != undefined)
                                _this.msgBus.publish('controls.dhcr.selectlineup', {lineup: firstLineup});

                        $('#dhcr_back').bind('click', function (evt) {
                                evt.stopPropagation();
//                                _this.msgBus.publish('control.navigate.dashboard', {});
				window.location.href = "#dashboard";
                        });

                        $('#dhcr_onlyme').bind('click', function (evt) {
                                evt.stopPropagation();
                                _this.onlyMe();
                        });

                        $('#dhcr_search').bind('keyup', function (evt) {
                                evt.stopPropagation();
                                _this.search();
                        });
                        $('#dhcr_search').bind('focus', function (evt) {
                                evt.stopPropagation();
                                if ($('#dhcr_search').val() == 'Search_') {
                                        $('#dhcr_search').val('');
                                }
                        });
                        $('#dhcr_search').bind('blur', function (evt) {
                                evt.stopPropagation();
                                if ($('#dhcr_search').val() == '') {
                                        $('#dhcr_search').val('Search_');
                                }
                        });
                        var sub = _this.msgBus.subscribe(ruckus.pubsub.subscriptions.view.dashboardcontest.contest.entry.all, _this.renderRankList);
                        _this.subscriptions.push(sub);
                });
        };

        ruckus.subpagecontrols.dashboardcontestranks.prototype.search = function () {
                var _this = this;
                var arr = [];
                $('#dhcr_onlyme').html('You');
                var txt = $('#dhcr_search').val();
                if (txt == '') {
                        _this.ranklist = _this.parameters.contestdetailranks;
                        _this.sortMe('fpp');
                } else {
                        _this.ranklist = _this.parameters.contestdetailranks;
                        $.each(this.ranklist, function (key, value) {
                                if (value.user.toLowerCase().indexOf(txt.toLowerCase()) != -1)
                                        arr.push(value);
                        });
                        this.ranklist = arr;
                        _this.sortMe('fpp');
                }
                this.renderRankList();
        };

        ruckus.subpagecontrols.dashboardcontestranks.prototype.onlyMe = function () {
		if (!$('#dhcr_onlyme_circle').hasClass('checkcircleSelected')) {
//                        $('#dhcr_onlyme').html('You');
                        $('#dhcr_onlyme_circle').addClass('checkcircleSelected');
                        var arr = [];
                        $.each(this.ranklist, function (key, value) {
                                if (value.isMe)
                                        arr.push(value);
                        });
                        this.ranklist = arr;
                } else {
//                        $('#dhcr_onlyme').html('All');
                        $('#dhcr_onlyme_circle').removeClass('checkcircleSelected');
                        this.ranklist = this.parameters.contestdetailranks;
                        this.sortMe('fpp');
                }
                this.renderRankList();
        };

        ruckus.subpagecontrols.dashboardcontestranks.prototype.sortMe = function (key) {
                var _this = this;

                var dir1 = undefined;
                var dir2 = undefined;
                if (_this.sortDetails[key] == 'asc') {
//                        _this.sortDetails[key] = 'desc';
                        dir1 = 1;
                        dir2 = -1;
                } else {
//                        _this.sortDetails[key] = 'asc';
                        dir1 = -1;
                        dir2 = 1;
                }
                var compare = function (a, b) {
                        if (a[key] < b[key])
                                return dir1;
                        if (a[key] > b[key])
                                return dir2;
                        return 0;
                };

                _this.ranklist.sort(compare);

                for (var x = 0; x < _this.ranklist.length; x++) {
                        _this.ranklist[x].pos = x + 1;
                }
        };

        ruckus.subpagecontrols.dashboardcontestranks.prototype.calcPrizes = function () {
                var _this = this;
                $.each(this.ranklist, function (key, value) {
                        $.each(_this.parameters.contest.payouts, function (k, v) {
                                if (value.pos >= v.leadingPosition && value.pos <= v.trailingPosition)
                                        value.prize = _this.formatMoney(v.payoutAmount);
                        });
                });
        };

        ruckus.subpagecontrols.dashboardcontestranks.prototype.renderRankList = function (updatedranks) {
                var _this = this;
                if(updatedranks) _this.ranklist = updatedranks;
                this.require_template('dashboardcontestrankslist-tpl');
                dust.render('dusttemplates/dashboardcontestrankslist-tpl', {list: _this.ranklist}, function (err, out) {
                        $('#dhcr_ranklist').html(out);
			_this.addScrollBars();
	
			$.each(_this.ranklist, function(k,v){
				if (v.prize != undefined)
					$('#dhcr_money_'+v.entryId).addClass('in_the_money');
			});
				
                        $('.dhcr_rankrow').bind('click', function (evt) {
                                evt.stopPropagation();
                                var lineup = undefined;
                                $.each(_this.parameters.contestdetailranks, function (k, v) {
                                        if (evt.delegateTarget.id.split('_')[2] == v.entryId)
                                                lineup = v;
                                });
                                _this.msgBus.publish('controls.dhcr.selectlineup', {lineup: lineup});
                        });
                });
        };

        ruckus.subpagecontrols.dashboardcontestranks.prototype.unload = function () {
                this.destroyControl();
        };

        return ruckus.subpagecontrols.dashboardcontestranks;
});
	

