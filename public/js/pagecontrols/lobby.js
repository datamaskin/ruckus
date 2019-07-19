define([
        "assets/js/pagecontrols/base.js",
        "assets/js/subpagecontrols/contestfilter.js",
        "assets/js/subpagecontrols/contestresults.js"
], function (Base) {
        ruckus.pagecontrols.lobby = function (parameters) {
                var _this = this;
                Base.call(_this);
                _this.parameters = parameters;
        };

        ruckus.pagecontrols.lobby.prototype = Object.create(Base.prototype);

        ruckus.pagecontrols.lobby.prototype.load = function () {
                var _this = this;
                _this.getContainer();
                _this.container.addClass('ruckus-pc-lobby');

                _this.require_template('lobby-tpl');
                dust.render('dusttemplates/lobby-tpl', {}, function (err, out) {
                        _this.container.html(out);
			_this.addScrollBars();

                        var spc_contestresults = new ruckus.subpagecontrols.contestresults({
                                'container': $('#contestresultscontainer'),
                                'lobby': _this
                        });
                        spc_contestresults.load();
                        _this.controls.push(spc_contestresults);

                        var spc_contestfilter = new ruckus.subpagecontrols.contestfilter({
                                'container': $('#contestfiltercontainer'),
                                'lobby': _this
                        });
                        spc_contestfilter.load();
                        _this.controls.push(spc_contestfilter);
                });
        };

        ruckus.pagecontrols.lobby.prototype.unload = function () {
                this.destroyControl();
        };

        return ruckus.pagecontrols.lobby;
});


