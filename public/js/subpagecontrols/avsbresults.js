// Author: Scott Gay
define([
        "assets/js/subpagecontrols/base.js",
        "assets/js/libraries/jquery.min.js",
//	"assets/js/libraries/underscore-min.js"
        "assets/js/libraries/dust-core.min.js"
], function (Base) {
        ruckus.subpagecontrols.avsbresults = function (parameters) {
                Base.call(this);
                this.init = function () {
                        this.parameters = parameters;
                };
                this.init();
        };

        ruckus.subpagecontrols.avsbresults.prototype = Object.create(Base.prototype);
        ruckus.subpagecontrols.avsbresults.prototype.load = function () {
                var _this = this;
                this.getContainer();
                this.container.addClass('ruckus-spc-avsbresults');

                this.require_template('avsbresults-tpl');
                dust.render('dusttemplates/avsbresults-tpl', {}, function (err, out) {
                        _this.container.html(out);
			_this.addScrollBars();
                });
        };

        ruckus.subpagecontrols.avsbresults.prototype.unload = function () {
                this.destroyControl();
        };

        return ruckus.subpagecontrols.avsbresults;
});
	

