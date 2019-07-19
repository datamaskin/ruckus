// Author: Scott Gay
define([
        "assets/js/subpagecontrols/base.js",
        "assets/js/libraries/jquery.min.js",
//	"assets/js/libraries/underscore-min.js"
        "assets/js/libraries/dust-core.min.js"
], function (Base) {
        ruckus.subpagecontrols.dashboardhistoryheader = function (parameters) {
                Base.call(this);
                this.init = function () {
                        this.parameters = parameters;
                };
                this.init();
        };

        ruckus.subpagecontrols.dashboardhistoryheader.prototype = Object.create(Base.prototype);
        ruckus.subpagecontrols.dashboardhistoryheader.prototype.load = function () {
                var _this = this;
                this.getContainer();
                this.container.addClass('ruckus-spc-dashboardhistoryheader');

		_this.parameters.contest.formattedStartTime = _this.formatTimeActual(_this.parameters.contest.startTime);
		_this.parameters.contest.formattedPosition = _this.formatPlace(_this.parameters.contest.position);
                
		this.require_template('dashboardhistoryheader-tpl');
                dust.render('dusttemplates/dashboardhistoryheader-tpl', _this.parameters.contest, function (err, out) {
                        _this.container.html(out);
			_this.addScrollBars();
                });
        };

        ruckus.subpagecontrols.dashboardhistoryheader.prototype.unload = function () {
                this.destroyControl();
        };

        return ruckus.subpagecontrols.dashboardhistoryheader;
});
	

