// Author: Scott Gay
define([
        "assets/js/pagecontrols/base.js",
        "assets/js/libraries/jquery.min.js"
], function (Base) {
        ruckus.pagecontrols.loyaltybonus = function (parameters) {
                Base.call(this);
                this.init = function () {
                        var _this = this;
                        this.parameters = parameters;
                };
                this.init();
        };

        ruckus.pagecontrols.loyaltybonus.prototype = Object.create(Base.prototype);

        ruckus.pagecontrols.loyaltybonus.prototype.load = function () {
                var _this = this;
                this.getContainer();
                this.container.addClass('ruckus-pc-loyaltybonus');

                this.require_template('loyaltybonus-tpl');
                dust.render('dusttemplates/loyaltybonus-tpl', {}, function (err, out) {
                        _this.container.html(out);
			_this.addScrollBars();
                });
        };

        ruckus.pagecontrols.loyaltybonus.prototype.unload = function () {
                this.destroyControl();
        };

        return ruckus.pagecontrols.loyaltybonus;
});


