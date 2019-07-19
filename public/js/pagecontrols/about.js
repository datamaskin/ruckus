// Author: Scott Gay
define([
        "assets/js/pagecontrols/base.js",
        "assets/js/libraries/jquery.min.js"
], function (Base) {
        ruckus.pagecontrols.about = function (parameters) {
                Base.call(this);
                this.init = function () {
                        var _this = this;
                        this.parameters = parameters;
                };
                this.init();
        };

        ruckus.pagecontrols.about.prototype = Object.create(Base.prototype);

        ruckus.pagecontrols.about.prototype.load = function () {
                var _this = this;
                this.getContainer();
                this.container.addClass('ruckus-pc-about');

                this.require_template('about-tpl');
                dust.render('dusttemplates/about-tpl', {}, function (err, out) {
                        _this.container.html(out);
			_this.addScrollBars();
                });
        };

        ruckus.pagecontrols.about.prototype.unload = function () {
                this.destroyControl();
        };

        return ruckus.pagecontrols.about;
});


