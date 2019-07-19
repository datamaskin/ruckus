// Author: Scott Gay
define([
        "assets/js/subpagecontrols/base.js",
        "assets/js/libraries/jquery.min.js",
//        "assets/js/libraries/underscore-min.js"
        "assets/js/libraries/dust-core.min.js"
], function (Base) {
        ruckus.subpagecontrols.green = function (parameters) {
                Base.call(this);
                this.init = function () {
                        this.parameters = parameters;
                };
                this.init();
        };

        ruckus.subpagecontrols.green.prototype = Object.create(Base.prototype);
        ruckus.subpagecontrols.green.prototype.load = function () {
                var _this = this;
                this.getContainer();
                this.container.addClass('ruckus-spc-green');

//                this.require_template('green-template');

//                var template = _.template($('#green-template').html());
//                this.container.html(template);

                this.require_template('green-tpl');
                dust.render('dusttemplates/green-tpl', {}, function (err, out) {
                        _this.container.html(out);
			_this.addScrollBars();
                });
        };

        ruckus.subpagecontrols.green.prototype.unload = function () {
                this.destroyControl();
        };

        return ruckus.subpagecontrols.green;
});


