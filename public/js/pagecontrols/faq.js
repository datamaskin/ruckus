// Author: Scott Gay
define([
        "assets/js/pagecontrols/base.js",
        "assets/js/libraries/jquery.min.js"
], function (Base) {
        ruckus.pagecontrols.faq = function (parameters) {
                Base.call(this);
                this.init = function () {
                        var _this = this;
                        this.parameters = parameters;
                };
                this.init();
        };

        ruckus.pagecontrols.faq.prototype = Object.create(Base.prototype);

        ruckus.pagecontrols.faq.prototype.load = function () {
                var _this = this;
                this.getContainer();
                this.container.addClass('ruckus-pc-faq');

                this.require_template('faq-tpl');
                dust.render('dusttemplates/faq-tpl', {}, function (err, out) {
                        _this.container.html(out);
			_this.addScrollBars();
                });
        };

        ruckus.pagecontrols.faq.prototype.unload = function () {
                this.destroyControl();
        };

        return ruckus.pagecontrols.faq;
});


