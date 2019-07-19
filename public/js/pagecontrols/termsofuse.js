// Author: Scott Gay
define([
        "assets/js/pagecontrols/base.js",
        "assets/js/libraries/jquery.min.js"
], function (Base) {
        ruckus.pagecontrols.termsofuse = function (parameters) {
                Base.call(this);
                this.init = function () {
                        var _this = this;
                        this.parameters = parameters;
                };
                this.init();
        };

        ruckus.pagecontrols.termsofuse.prototype = Object.create(Base.prototype);

        ruckus.pagecontrols.termsofuse.prototype.load = function () {
                var _this = this;
                this.getContainer();
                this.container.addClass('ruckus-pc-termsofuse');

                this.require_template('termsofuse-tpl');
                dust.render('dusttemplates/termsofuse-tpl', {}, function (err, out) {
                        _this.container.html(out);
			_this.addScrollBars();
                });
        };

        ruckus.pagecontrols.termsofuse.prototype.unload = function () {
                this.destroyControl();
        };

        return ruckus.pagecontrols.termsofuse;
});


