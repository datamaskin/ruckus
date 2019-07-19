// Author: Scott Gay
define([
        "assets/js/subpagecontrols/base.js",
        "assets/js/libraries/jquery.min.js",
//	"assets/js/libraries/underscore-min.js"
        "assets/js/libraries/dust-core.min.js"
], function (Base) {
        ruckus.subpagecontrols.contestresultsprizes = function (parameters) {
                Base.call(this);
                this.init = function () {
                        this.parameters = parameters;
                };
                this.init();
        };

        ruckus.subpagecontrols.contestresultsprizes.prototype = Object.create(Base.prototype);
        ruckus.subpagecontrols.contestresultsprizes.prototype.load = function () {
                var _this = this;
                this.getContainer();
                this.container.addClass('ruckus-spc-contestresultsprizes');

                $.each(this.parameters.contest.payout, function (k, v) {
                        if (v.leadingPosition == v.trailingPosition)
                                v.displayPosition = _this.formatPlace(v.leadingPosition);
                        else
                                v.displayPosition = _this.formatPlace(v.leadingPosition) + '-' + _this.formatPlace(v.trailingPosition);
                        v.formattedPayoutAmount = _this.formatMoney(v.payoutAmount);
                });

                this.require_template('contestresultsprizes-tpl');
                dust.render('dusttemplates/contestresultsprizes-tpl', this.parameters.contest, function (err, out) {
                        _this.container.html(out);
			_this.addScrollBars();
                        if (_this.parameters.tab != 'prizes')
                                _this.hidePage();
                });
        };

        ruckus.subpagecontrols.contestresultsprizes.prototype.unload = function () {
                this.destroyControl();
        };

        return ruckus.subpagecontrols.contestresultsprizes;
});
	

