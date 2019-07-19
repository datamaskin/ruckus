// Author: Scott Gay
define([
        "assets/js/pagecontrols/base.js",
        "assets/js/libraries/jquery.min.js",
//        "assets/js/libraries/underscore-min.js",
        "assets/js/subpagecontrols/blue.js",
        "assets/js/subpagecontrols/yellow.js",
        "assets/js/subpagecontrols/red.js",
        "assets/js/subpagecontrols/orange.js",
        "assets/js/subpagecontrols/green.js"
], function (Base) {
        ruckus.pagecontrols.main = function (parameters) {
                Base.call(this);
                this.init = function () {
                        var _this = this;
                        this.parameters = parameters;
                        this.parameters.container.addClass('pagecontrolhighlight');

                        /*
                         // manage page load and unload through message bus
                         this.msgBus.subscribe( "nav.change", function ( data ) {
                         if (data.page == 'main')
                         _this.load();
                         else
                         if (_this.container != undefined)
                         _this.unload();
                         });
                         */
                };
                this.init();
        };

        ruckus.pagecontrols.main.prototype = Object.create(Base.prototype);
        ruckus.pagecontrols.main.prototype.load = function () {
                this.getContainer();
                this.container.addClass('ruckus-pc-main');

                new ruckus.subpagecontrols.blue({
                        'container': this.container
                }).load();
                new ruckus.subpagecontrols.green({
                        'container': this.container
                }).load();
                new ruckus.subpagecontrols.red({
                        'container': this.container
                }).load();
                new ruckus.subpagecontrols.yellow({
                        'container': this.container
                }).load();
                new ruckus.subpagecontrols.orange({
                        'container': this.container
                }).load();
        };

        ruckus.pagecontrols.main.prototype.unload = function () {
                this.destroyControl();
        };

        return ruckus.pagecontrols.main;
});


