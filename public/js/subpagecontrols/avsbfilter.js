// Author: Scott Gay
define([
        "assets/js/subpagecontrols/base.js",
        "assets/js/libraries/jquery.min.js",
//	"assets/js/libraries/underscore-min.js"
        "assets/js/libraries/dust-core.min.js"
], function (Base) {
        ruckus.subpagecontrols.avsbfilter = function (parameters) {
                Base.call(this);
                this.init = function () {
                        this.parameters = parameters;
                };
                this.init();
        };

        ruckus.subpagecontrols.avsbfilter.prototype = Object.create(Base.prototype);
        ruckus.subpagecontrols.avsbfilter.prototype.load = function () {
                var _this = this;
                this.getContainer();
                this.container.addClass('ruckus-spc-avsbfilter');

                this.require_template('avsbfilter-tpl');
                dust.render('dusttemplates/avsbfilter-tpl', {}, function (err, out) {
                        _this.container.html(out);
			_this.addScrollBars();
                });
        };

        ruckus.subpagecontrols.avsbfilter.prototype.unload = function () {
                this.destroyControl();
        };

        return ruckus.subpagecontrols.avsbfilter;
});
	

