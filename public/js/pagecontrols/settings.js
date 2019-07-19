// Author: Scott Gay
define([
        "assets/js/pagecontrols/base.js",
        "assets/js/libraries/jquery.min.js"
], function (Base) {
        ruckus.pagecontrols.settings = function (parameters) {
                Base.call(this);
                this.init = function () {
                        var _this = this;
                        this.parameters = parameters;
                };
                this.init();
        };

        ruckus.pagecontrols.settings.prototype = Object.create(Base.prototype);

        ruckus.pagecontrols.settings.prototype.load = function () {
                var _this = this;
                this.getContainer();
                this.container.addClass('ruckus-pc-settings');

                this.require_template('settings-tpl');
                dust.render('dusttemplates/settings-tpl', {}, function (err, out) {
                        _this.container.html(out);
			_this.addScrollBars();
                });
        };

        ruckus.pagecontrols.settings.prototype.unload = function () {
                this.destroyControl();
        };

        return ruckus.pagecontrols.settings;
});


