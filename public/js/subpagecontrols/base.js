// Author: Scott Gay
define([
        "rg_global_base"
], function (Base) {
        ruckus.subpagecontrols.base = function (parameters) {
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

        ruckus.subpagecontrols.base.prototype = Object.create(Base.prototype);

        ruckus.subpagecontrols.base.prototype.getContainer = function () {
                // this creates a container for each subpagecontrol
                var container_child = this.parameters.container.children('div').first();
                if(container_child.length === 1){
                        this.container = $(container_child[0]);
                } else {
                        this.container = $('<div>').appendTo(this.parameters.container);
                }
        };

        ruckus.subpagecontrols.base.prototype.destroyControl = function () {
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

        ruckus.subpagecontrols.base.prototype.hidePage = function () {
                this.container.hide();
        };

        ruckus.subpagecontrols.base.prototype.showPage = function () {
                this.container.show();
        };

        ruckus.subpagecontrols.base.prototype.__addsubscription = function (topic, callback) {
                var _this = this;
                var sub = _this.msgBus.subscribe(topic, callback);
                _this.subscriptions.push(sub);
        };

        return ruckus.subpagecontrols.base;
});
