// Author: Scott Gay
define([
        "assets/js/subpagecontrols/base.js",
        "assets/js/libraries/jquery.min.js",
        "assets/js/models/contestevents.js",
//	"assets/js/libraries/underscore-min.js"
        "assets/js/libraries/dust-core.min.js"
], function (Base) {
        ruckus.subpagecontrols.contestresultsinfo = function (parameters) {
                Base.call(this);
                this.init = function () {
                        this.parameters = parameters;
                };
                this.init();
        };

        ruckus.subpagecontrols.contestresultsinfo.prototype = Object.create(Base.prototype);
        ruckus.subpagecontrols.contestresultsinfo.prototype.load = function () {
                var _this = this;
                this.getContainer();
                this.container.addClass('ruckus-spc-contestresultsinfo');

                this.require_template('contestresultsinfo-tpl');

                _this.contestEventsModel = new ruckus.models.contestevents({});
                _this.models.push(_this.contestEventsModel);
                var sub = _this.msgBus.subscribe("model.contestevents.retrieve", function (data) {
                        sub.unsubscribe();
                        $.each(_this.contestEventsModel.modelData, function (k, v) {
                                v.formattedStartTime = _this.formatTimeActual(v.startTime);
                        });
                        dust.render('dusttemplates/contestresultsinfo-tpl', {events: _this.contestEventsModel.modelData, contest: _this.parameters.contest, scoring: _this.parameters.contestscoring.data[_this.parameters.contest.league.toLowerCase()]}, function (err, out) {
                                _this.container.html(out);
				_this.addScrollBars();
                                if (_this.parameters.tab != 'info')
                                        _this.hidePage();
                        });
                });
                _this.contestEventsModel.fetch({contestId: _this.parameters.contest.id});
        };

        ruckus.subpagecontrols.contestresultsinfo.prototype.unload = function () {
                this.destroyControl();
        };

        return ruckus.subpagecontrols.contestresultsinfo;
});
	

