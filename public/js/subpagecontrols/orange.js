// Author: Scott Gay
define([
        "assets/js/subpagecontrols/base.js",
        "assets/js/libraries/jquery.min.js",
//        "assets/js/libraries/underscore-min.js"
        "assets/js/libraries/dust-core.min.js"
], function (Base) {
        ruckus.subpagecontrols.orange = function (parameters) {
                Base.call(this);
                this.init = function () {
                        this.parameters = parameters;
                };
                this.init();
        };

        ruckus.subpagecontrols.orange.prototype = Object.create(Base.prototype);
        ruckus.subpagecontrols.orange.prototype.load = function () {
                var _this = this;
                this.getContainer();
                this.container.addClass('ruckus-spc-orange');

//                this.require_template('orange-template');

//                var template = _.template($('#orange-template').html());
//                this.container.html(template);

                this.require_template('orange-tpl');
                dust.render('dusttemplates/orange-tpl', {}, function (err, out) {
                        _this.container.html(out);
			_this.addScrollBars();
                });
        };

        ruckus.subpagecontrols.orange.prototype.unload = function () {
                this.destroyControl();
        };

        return ruckus.subpagecontrols.orange;
});


