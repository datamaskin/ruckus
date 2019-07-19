// Author: Scott Gay
define([
        "assets/js/pagecontrols/base.js",
        "assets/js/libraries/jquery.min.js"
], function (Base) {
        ruckus.pagecontrols.landing = function (parameters) {
                Base.call(this);
                this.init = function () {
                        var _this = this;
                        this.parameters = parameters;
                };
                this.init();
        };

        ruckus.pagecontrols.landing.prototype = Object.create(Base.prototype);

        ruckus.pagecontrols.landing.prototype.load = function () {
                var _this = this;
                this.getContainer();
                this.container.addClass('ruckus-pc-landing');

                this.require_template('landing-tpl');
                dust.render('dusttemplates/landing-tpl', {}, function (err, out) {
                        _this.container.html(out);
			_this.addScrollBars();
                });
        };

        ruckus.pagecontrols.landing.prototype.unload = function () {
                this.destroyControl();
        };

        return ruckus.pagecontrols.landing;
});


