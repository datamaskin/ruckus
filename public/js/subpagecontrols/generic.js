// Author: Scott Gay
define([
        "assets/js/subpagecontrols/base.js",
        "assets/js/libraries/jquery.min.js",
//	"assets/js/libraries/underscore-min.js"
        "assets/js/libraries/dust-core.min.js"
], function (Base) {
        ruckus.subpagecontrols.generic = function (parameters) {
                Base.call(this);
                this.init = function () {
                        this.parameters = parameters;
                };
                this.init();
        };

        ruckus.subpagecontrols.generic.prototype = Object.create(Base.prototype);
        ruckus.subpagecontrols.generic.prototype.load = function () {
                var _this = this;
                this.getContainer();
                this.container.addClass('ruckus-spc-generic');

                this.require_template('generic-tpl');
                dust.render('dusttemplates/generic-tpl', {}, function (err, out) {
                        _this.container.html(out);
			_this.addScrollBars();
                });
        };

        ruckus.subpagecontrols.generic.prototype.unload = function () {
                this.destroyControl();
        };

        return ruckus.subpagecontrols.generic;
});
	

