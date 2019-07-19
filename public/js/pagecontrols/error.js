// Author: Scott Gay
define([
        "assets/js/pagecontrols/base.js",
        "assets/js/libraries/jquery.min.js"
], function (Base) {
        ruckus.pagecontrols.error = function (parameters) {
                Base.call(this);
                this.init = function () {
                        var _this = this;
                        this.parameters = parameters;
                };
                this.init();
        };

        ruckus.pagecontrols.error.prototype = Object.create(Base.prototype);

        ruckus.pagecontrols.error.prototype.load = function () {
                var _this = this;
                this.getContainer();
                this.container.addClass('ruckus-pc-error');

                this.require_template('error-tpl');
                dust.render('dusttemplates/error-tpl', {}, function (err, out) {
                        _this.container.html(out);
			_this.addScrollBars();
                });
        };

        ruckus.pagecontrols.error.prototype.unload = function () {
                this.destroyControl();
        };

        return ruckus.pagecontrols.error;
});


