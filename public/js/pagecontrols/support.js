// Author: Scott Gay
define([
        "assets/js/pagecontrols/base.js",
        "assets/js/libraries/jquery.min.js"
], function (Base) {
        ruckus.pagecontrols.support = function (parameters) {
                Base.call(this);
                this.init = function () {
                        var _this = this;
                        this.parameters = parameters;
                };
                this.init();
        };

        ruckus.pagecontrols.support.prototype = Object.create(Base.prototype);

        ruckus.pagecontrols.support.prototype.load = function () {
                var _this = this;
                this.getContainer();
                this.container.addClass('ruckus-pc-support');

                this.require_template('support-tpl');
                dust.render('dusttemplates/support-tpl', {}, function (err, out) {
                        _this.container.html(out);
			_this.addScrollBars();
                });
        };

        ruckus.pagecontrols.support.prototype.unload = function () {
                this.destroyControl();
        };

        return ruckus.pagecontrols.support;
});


