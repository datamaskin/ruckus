// Author: Scott Gay
define([
        "assets/js/pagecontrols/base.js",
        "assets/js/libraries/jquery.min.js"
], function (Base) {
        ruckus.pagecontrols.privacypolicy = function (parameters) {
                Base.call(this);
                this.init = function () {
                        var _this = this;
                        this.parameters = parameters;
                };
                this.init();
        };

        ruckus.pagecontrols.privacypolicy.prototype = Object.create(Base.prototype);

        ruckus.pagecontrols.privacypolicy.prototype.load = function () {
                var _this = this;
                this.getContainer();
                this.container.addClass('ruckus-pc-privacypolicy');

                this.require_template('privacypolicy-tpl');
                dust.render('dusttemplates/privacypolicy-tpl', {}, function (err, out) {
                        _this.container.html(out);
			_this.addScrollBars();
                });
        };

        ruckus.pagecontrols.privacypolicy.prototype.unload = function () {
                this.destroyControl();
        };

        return ruckus.pagecontrols.privacypolicy;
});


