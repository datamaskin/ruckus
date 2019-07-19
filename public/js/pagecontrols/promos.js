// Author: Scott Gay
define([
        "assets/js/pagecontrols/base.js",
        "assets/js/libraries/jquery.min.js"
], function (Base) {
        ruckus.pagecontrols.promos = function (parameters) {
                Base.call(this);
                this.init = function () {
                        var _this = this;
                        this.parameters = parameters;
                };
                this.init();
        };

        ruckus.pagecontrols.promos.prototype = Object.create(Base.prototype);

        ruckus.pagecontrols.promos.prototype.load = function () {
                var _this = this;
                this.getContainer();
                this.container.addClass('ruckus-pc-promos');

                this.require_template('promos-tpl');
                dust.render('dusttemplates/promos-tpl', {}, function (err, out) {
                        _this.container.html(out);
			_this.addScrollBars();
                });
        };

        ruckus.pagecontrols.promos.prototype.unload = function () {
                this.destroyControl();
        };

        return ruckus.pagecontrols.promos;
});


