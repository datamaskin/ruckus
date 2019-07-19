// Author: Scott Gay
define([
        "assets/js/pagecontrols/base.js",
        "assets/js/libraries/jquery.min.js"
], function (Base) {
        ruckus.pagecontrols.referafriend = function (parameters) {
                Base.call(this);
                this.init = function () {
                        var _this = this;
                        this.parameters = parameters;
                };
                this.init();
        };

        ruckus.pagecontrols.referafriend.prototype = Object.create(Base.prototype);

        ruckus.pagecontrols.referafriend.prototype.load = function () {
                var _this = this;
                this.getContainer();
                this.container.addClass('ruckus-pc-referafriend');

                this.require_template('referafriend-tpl');
                dust.render('dusttemplates/referafriend-tpl', {}, function (err, out) {
                        _this.container.html(out);
			_this.addScrollBars();
                });
        };

        ruckus.pagecontrols.referafriend.prototype.unload = function () {
                this.destroyControl();
        };

        return ruckus.pagecontrols.referafriend;
});


