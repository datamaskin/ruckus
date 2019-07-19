// Author: Scott Gay
define([
        "assets/js/pagecontrols/base.js",
        "assets/js/libraries/jquery.min.js"
], function (Base) {
        ruckus.pagecontrols.affiliates = function (parameters) {
                Base.call(this);
                this.init = function () {
                        var _this = this;
                        this.parameters = parameters;
                };
                this.init();
        };

        ruckus.pagecontrols.affiliates.prototype = Object.create(Base.prototype);

        ruckus.pagecontrols.affiliates.prototype.load = function () {
                var _this = this;
                this.getContainer();
                this.container.addClass('ruckus-pc-affiliates');

                this.require_template('affiliates-tpl');
                dust.render('dusttemplates/affiliates-tpl', {}, function (err, out) {
                        _this.container.html(out);
			_this.addScrollBars();
                });
        };

        ruckus.pagecontrols.affiliates.prototype.unload = function () {
                this.destroyControl();
        };

        return ruckus.pagecontrols.affiliates;
});


