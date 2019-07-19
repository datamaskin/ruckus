// Author: Scott Gay
define([
        "assets/js/pagecontrols/base.js",
        "assets/js/libraries/jquery.min.js"
], function (Base) {
        ruckus.pagecontrols.howitworks = function (parameters) {
                Base.call(this);
                this.init = function () {
                        var _this = this;
                        this.parameters = parameters;
                };
                this.init();
        };

        ruckus.pagecontrols.howitworks.prototype = Object.create(Base.prototype);

        ruckus.pagecontrols.howitworks.prototype.load = function () {
                var _this = this;
                this.getContainer();
                this.container.addClass('ruckus-pc-howitworks');

                this.require_template('howitworks-tpl');
                dust.render('dusttemplates/howitworks-tpl', {}, function (err, out) {
                        _this.container.html(out);
			_this.addScrollBars();
                });
        };

        ruckus.pagecontrols.howitworks.prototype.unload = function () {
                this.destroyControl();
        };

        return ruckus.pagecontrols.howitworks;
});


