// Author: Scott Gay
define([
        "assets/js/subpagecontrols/base.js",
        "assets/js/libraries/jquery.min.js",
//	"assets/js/libraries/underscore-min.js"
        "assets/js/libraries/dust-core.min.js"
], function (Base) {
        ruckus.subpagecontrols.blue = function (parameters) {
                Base.call(this);
                this.init = function () {
                        this.parameters = parameters;
                };
                this.init();
        };

        ruckus.subpagecontrols.blue.prototype = Object.create(Base.prototype);
        ruckus.subpagecontrols.blue.prototype.load = function () {
                var _this = this;
                this.getContainer();
                this.container.addClass('ruckus-spc-blue');

//                this.require_template('blue-template');

//                var template = _.template($('#blue-template').html());
//                this.container.html(template);

                this.require_template('blue-tpl');
                dust.render('dusttemplates/blue-tpl', {}, function (err, out) {
                        _this.container.html(out);
			_this.addScrollBars();
                });
        };

        ruckus.subpagecontrols.blue.prototype.unload = function () {
                this.destroyControl();
        };

        return ruckus.subpagecontrols.blue;
});
	

