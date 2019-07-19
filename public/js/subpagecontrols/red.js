// Author: Scott Gay
define([
        "assets/js/subpagecontrols/base.js",
        "assets/js/libraries/jquery.min.js",
//        "assets/js/libraries/underscore-min.js"
        "assets/js/libraries/dust-core.min.js"
], function (Base) {
        ruckus.subpagecontrols.red = function (parameters) {
                Base.call(this);
                this.init = function () {
                        this.parameters = parameters;
                };
                this.init();
        };

        ruckus.subpagecontrols.red.prototype = Object.create(Base.prototype);
        ruckus.subpagecontrols.red.prototype.load = function () {
                var _this = this;
                this.getContainer();
                this.container.addClass('ruckus-spc-red');

//                this.require_template('red-template');

//                var template = _.template($('#red-template').html());
//                this.container.html(template);

                this.require_template('red-tpl');
                dust.render('dusttemplates/red-tpl', {}, function (err, out) {
                        _this.container.html(out);
			_this.addScrollBars();
                });
        };

        ruckus.subpagecontrols.red.prototype.unload = function () {
                this.destroyControl();
        };

        return ruckus.subpagecontrols.red;
});


