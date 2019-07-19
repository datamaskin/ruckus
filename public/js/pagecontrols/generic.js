// Author: Scott Gay
define([
        "assets/js/pagecontrols/base.js",
        "assets/js/libraries/jquery.min.js"
], function (Base) {
        ruckus.pagecontrols.generic = function (parameters) {
                Base.call(this);
                this.init = function () {
                        var _this = this;
                        this.parameters = parameters;
                };
                this.init();
        };

        ruckus.pagecontrols.generic.prototype = Object.create(Base.prototype);

        ruckus.pagecontrols.generic.prototype.load = function () {
                var _this = this;
                this.getContainer();
                this.container.addClass('ruckus-pc-generic');

                this.require_template('generic-tpl');
                dust.render('dusttemplates/generic-tpl', {}, function (err, out) {
                        _this.container.html(out);
			_this.addScrollBars();
                });
        };

        ruckus.pagecontrols.generic.prototype.unload = function () {
                this.destroyControl();
        };

        return ruckus.pagecontrols.generic;
});


