// Author: Scott Gay
define([
        "assets/js/pagecontrols/base.js",
        "assets/js/libraries/jquery.min.js",
        "assets/js/subpagecontrols/avsbfilter.js",
        "assets/js/subpagecontrols/avsbresults.js"
], function (Base) {
        ruckus.pagecontrols.lobbyavsb = function (parameters) {
                Base.call(this);
                this.init = function () {
                        var _this = this;
                        this.parameters = parameters;
                };
                this.init();
        };

        ruckus.pagecontrols.lobbyavsb.prototype = Object.create(Base.prototype);

        ruckus.pagecontrols.lobbyavsb.prototype.load = function () {
                var _this = this;
                this.getContainer();
                this.container.addClass('ruckus-pc-lobbyavsb');

                this.require_template('lobbyavsb-tpl');
                dust.render('dusttemplates/lobbyavsb-tpl', {}, function (err, out) {
                        _this.container.html(out);
			_this.addScrollBars();
                        var spc_avsbresults = new ruckus.subpagecontrols.avsbresults({
                                'container': $('#avsbresultscontainer')
                        });
                        spc_avsbresults.load();
                        _this.controls.push(spc_avsbresults);

                        var spc_avsbfilter = new ruckus.subpagecontrols.avsbfilter({
                                'container': $('#avsbfiltercontainer')
                        });
                        spc_avsbfilter.load();
                        _this.controls.push(spc_avsbfilter);

                });
        };

        ruckus.pagecontrols.lobbyavsb.prototype.unload = function () {
                this.destroyControl();
        };

        return ruckus.pagecontrols.lobbyavsb;
});


