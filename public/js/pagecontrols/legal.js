// Author: Scott Gay
define([
        "assets/js/pagecontrols/base.js",
        "assets/js/libraries/jquery.min.js"
], function (Base) {
        ruckus.pagecontrols.legal = function (parameters) {
                Base.call(this);
                this.init = function () {
                        var _this = this;
                        this.parameters = parameters;
                };
                this.init();
        };

        ruckus.pagecontrols.legal.prototype = Object.create(Base.prototype);

        ruckus.pagecontrols.legal.prototype.load = function () {
                var _this = this;
                this.getContainer();
                this.container.addClass('ruckus-pc-legal');

                this.require_template('legal-tpl');
                dust.render('dusttemplates/legal-tpl', {}, function (err, out) {
                        _this.container.html(out);
			_this.addScrollBars();
                });
        };

        ruckus.pagecontrols.legal.prototype.unload = function () {
                this.destroyControl();
        };

        return ruckus.pagecontrols.legal;
});

