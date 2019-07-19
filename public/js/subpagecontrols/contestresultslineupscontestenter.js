// Author: Scott Gay
define([
        "assets/js/subpagecontrols/base.js",
        "assets/js/models/quicklineups.js",
        "assets/js/models/quicklineupenter.js",
        "assets/js/libraries/jquery.min.js",
//	"assets/js/libraries/underscore-min.js"
        "assets/js/libraries/dust-core.min.js"
], function (Base) {
        ruckus.subpagecontrols.contestresultslineupscontestenter = function (parameters) {
                Base.call(this);
                this.init = function () {
                        this.parameters = parameters;
                };
                this.init();
        };

        ruckus.subpagecontrols.contestresultslineupscontestenter.prototype = Object.create(Base.prototype);
        ruckus.subpagecontrols.contestresultslineupscontestenter.prototype.load = function () {
                var _this = this;
                this.getContainer();
                this.container.addClass('ruckus-spc-contestresultslineupscontestenter');

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
			
                        _this.require_template('contestresultslineupscontestenter-tpl');
                        dust.render('dusttemplates/contestresultslineupscontestenter-tpl', {data: _this.quickLineupsModel.modelData}, function (err, out) {
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
					_this.importContest(evt.delegateTarget.id.split('_')[1]);
                                });
                        });
			
                });
                this.quickLineupsModel.fetch({contestId: this.parameters.contest.id});
        };

	ruckus.subpagecontrols.contestresultslineupscontestenter.prototype.importContest = function(id){
		var _this = this;
		var importLineup = undefined;
		$.each(_this.quickLineupsModel.modelData, function(k,v){
			if (v.lineupId == id){
				importLineup = v;
			}
		});
		_this.msgBus.publish("controls.import.clearall", {});
		$.each(importLineup.athletes, function(k,v){
			_this.msgBus.publish('control.import.selectplayer', {data:v});
		});	
	};

        ruckus.subpagecontrols.contestresultslineupscontestenter.prototype.unload = function () {
                this.destroyControl();
        };

        return ruckus.subpagecontrols.contestresultslineupscontestenter;
});
	

