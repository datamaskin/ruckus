// Author: Scott Gay
define([
        "assets/js/base.js",
        "assets/js/libraries/dust-core.min.js",
        "assets/js/libraries/jquery.min.js",
        "nicescroll"
], function (Base) {
        ruckus.pagecontrols.base = function (parameters) {
                Base.call(this);
                this.init = function () {
                        this.parameters = parameters;
                        this.subscriptions = [];
                        this.controls = [];
                        this.models = [];
                        this.intervals = [];
                };
                this.init();
        };

        ruckus.pagecontrols.base.prototype = Object.create(Base.prototype);
        ruckus.pagecontrols.base.prototype.getContainer = function () {
                // this creates a container for each page control
                this.container = $('<div>').appendTo(this.parameters.container);
        };
        ruckus.pagecontrols.base.prototype.destroyControl = function () {
                var _this = this;
                $.each(_this.controls, function (key, value) {
                        _this.log({type: 'unload', data: value, msg: 'UNLOAD CONTROL'});
                        value.unload();
                });
                $.each(_this.subscriptions, function (key, value) {
                        _this.log({type: 'unload', data: value, msg: 'UNLOAD SUBSCRIPTION'});
                        value.unsubscribe();
                });
                $.each(_this.models, function (key, value) {
                        _this.log({type: 'unload', data: value, msg: 'UNLOAD MODEL'});
                        value.unload();
                });
                $.each(_this.intervals, function (key, value) {
                        _this.log({type: 'unload', data: value, msg: 'UNLOAD INTERVAL'});
                        clearInterval(value);
                });
                this.container.remove();
        };
        ruckus.pagecontrols.base.prototype.__addsubscription = function (topic, callback) {
                var _this = this;
                var sub = _this.msgBus.subscribe(topic, callback);
                _this.subscriptions.push(sub);
        };
        ruckus.pagecontrols.base.prototype.hidePage = function () {
                this.container.hide();
        };
        ruckus.pagecontrols.base.prototype.showPage = function () {
                this.container.show();
        };

        return ruckus.pagecontrols.base;
});


