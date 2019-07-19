// Author: Scott Gay
define([
        "assets/js/subpagecontrols/base.js",
        "assets/js/libraries/jquery.min.js",
//        "assets/js/libraries/underscore-min.js"
        "assets/js/libraries/dust-core.min.js"
], function (Base) {
        ruckus.subpagecontrols.yellow = function (parameters) {
                Base.call(this);
                this.init = function () {
                        this.parameters = parameters;
                };
                this.init();
        };

        ruckus.subpagecontrols.yellow.prototype = Object.create(Base.prototype);
        ruckus.subpagecontrols.yellow.prototype.load = function () {
                var _this = this;
                this.getContainer();
                this.container.addClass('ruckus-spc-yellow');

//                this.require_template('yellow-template');

//                var template = _.template($('#yellow-template').html());
//                this.container.html(template);

                this.require_template('yellow-tpl');
                dust.render('dusttemplates/yellow-tpl', {}, function (err, out) {
                        _this.container.html(out);
			_this.addScrollBars();
                });
        };

        ruckus.subpagecontrols.yellow.prototype.unload = function () {
                this.destroyControl();
        };

        return ruckus.subpagecontrols.yellow;
});


