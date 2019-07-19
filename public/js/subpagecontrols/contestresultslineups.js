// Author: Scott Gay
define([
        "assets/js/subpagecontrols/base.js",
        "assets/js/models/quicklineups.js",
        "assets/js/models/quicklineupenter.js",
        "assets/js/libraries/jquery.min.js",
//	"assets/js/libraries/underscore-min.js"
        "assets/js/libraries/dust-core.min.js"
], function (Base) {
        ruckus.subpagecontrols.contestresultslineups = function (parameters) {
                Base.call(this);
                this.init = function () {
                        this.parameters = parameters;
                };
                this.init();
        };

        ruckus.subpagecontrols.contestresultslineups.prototype = Object.create(Base.prototype);
        ruckus.subpagecontrols.contestresultslineups.prototype.load = function () {
                var _this = this;
                this.getContainer();
                this.container.addClass('ruckus-spc-contestresultslineups');

                this.quickLineupsModel = new ruckus.models.quicklineups({});
                this.models.push(this.quickLineupsModel);
                var sub = this.msgBus.subscribe("model.quicklineups.retrieve", function (data) {
                        sub.unsubscribe();
                        $.each(_this.quickLineupsModel.modelData, function (key, value) {
                                $.each(value.athletes, function (k, v) {
                                        v.firstInitial = v.firstName.substring(0, 1);
                                });
                        });
//                        _this.container.html('Coming Soon');
//                        _this.hidePage();
			
                        _this.require_template('contestresultslineups-tpl');
                        dust.render('dusttemplates/contestresultslineups-tpl', {data: _this.quickLineupsModel.modelData}, function (err, out) {
                                _this.container.html(out);
				_this.addScrollBars();
                                if (_this.parameters.tab != 'lineups')
                                        _this.hidePage();
	
				$.each(_this.quickLineupsModel.modelData, function(k,v){
					if (v.numEntries > 0){
						$('#quicklineupenter_' + v.lineupId).hide();
		                                $('#quicklineupentered_' + v.lineupId).show();	
					}
				});

                                $('.quicklineupenter').bind("click", function (evt) {
                                        evt.stopPropagation();
					_this.enterContest(evt.delegateTarget.id.split('_')[1]);
                                });
                        });
			
                });
                this.quickLineupsModel.fetch({contestId: this.parameters.contest.id});
        };

	ruckus.subpagecontrols.contestresultslineups.prototype.enterContest = function(id){
		var _this = this;
		var replace = true;
		if (_this.parameters.contest.allowedEntries > 1)
			replace = false;
		var params = {
			lineupId : parseInt(id),
			entries : [{contestId: _this.parameters.contest.id, multiple:1, replace:replace}]
		};
		_this.quickLineupEnterModel = new ruckus.models.quicklineupenter({});
		_this.models.push(_this.quickLineupEnterModel);
		var subSuccess = undefined;
		var subFailed = undefined;
		subSuccess = _this.msgBus.subscribe("model.quicklineupenter.success", function (data) {
			subSuccess.unsubscribe();
			subFailed.unsubscribe();
			if (data.data.payload[0].stopCode == 0){
				if (_this.parameters.contest.allowedEntries == 1){
					$('.cone_entered').hide();
					$('.cone_enter').show();
				}
				$('#quicklineupenter_' + id).hide();
				$('#quicklineupentered_' + id).show();
				_this.msgBus.publish('controls.quicklineup.reloadentries',{});
//				$('#quicklineupremove_' + id).show();
//				$('#quicklineupremove_' + id).bind('click', function(evt){
//					evt.stopPropagation();
//					_this.removeContest(evt.delegateTarget.id.split('_')[1]);
//				});
			} else {
				$('#quicklineupenter_' + id).hide();
				$('#quicklineupfailed_' + id).show();
			}
		});
		subFailed = _this.msgBus.subscribe("model.quicklineupenter.failed", function (data) {
			subSuccess.unsubscribe();
			subFailed.unsubscribe();
			$('#quicklineupenter_' + id).hide();
			$('#quicklineupfailed_' + id).show();

		});
		_this.quickLineupEnterModel.fetch(params);
	};

	ruckus.subpagecontrols.contestresultslineups.prototype.removeContest = function(id){
		var _this = this;
	};

        ruckus.subpagecontrols.contestresultslineups.prototype.unload = function () {
                this.destroyControl();
        };

        return ruckus.subpagecontrols.contestresultslineups;
});
	

