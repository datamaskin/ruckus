// Author: Scott Gay
define([
        "assets/js/pagecontrols/base.js",
        "assets/js/libraries/jquery.min.js"
], function (Base) {
        ruckus.pagecontrols.transactionhistory = function (parameters) {
                Base.call(this);
                this.init = function () {
                        var _this = this;
                        this.parameters = parameters;
                };
                this.init();
        };

        ruckus.pagecontrols.transactionhistory.prototype = Object.create(Base.prototype);

        ruckus.pagecontrols.transactionhistory.prototype.load = function () {
                var _this = this;
                this.getContainer();
                this.container.addClass('ruckus-pc-transactionhistory');

                this.require_template('transactionhistory-tpl');
                dust.render('dusttemplates/transactionhistory-tpl', {}, function (err, out) {
                        _this.container.html(out);
			_this.addScrollBars();
                });
        };

        ruckus.pagecontrols.transactionhistory.prototype.unload = function () {
                this.destroyControl();
        };

        return ruckus.pagecontrols.transactionhistory;
});


