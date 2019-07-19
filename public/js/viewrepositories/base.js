// Author: Scott Gay
define([
        'rg_global_base'
], function (Base) {
        ruckus.views.repositories.base = function () {
                Base.call(this);
                this.subscriptions = [];
                this.dataRepos = {};
                this.intervals = [];
        };

        ruckus.views.repositories.base.prototype = Object.create(Base.prototype);

        // UTILITY - To be moved to a base class
        ruckus.views.repositories.base.prototype.__addsubscription = function (topic, callback) {
                var _this = this;
                var sub = _this.msgBus.subscribe(topic, callback);
                _this.subscriptions.push(sub);
        };

        ruckus.views.repositories.base.prototype.__destroy = function () {
                var _this = this;
                $.each(_this.subscriptions, function (key, value) {
                        _this.log({type: 'unload', data: value, msg: 'UNLOAD SUBSCRIPTION'});
                        value.unsubscribe();
                });
                $.each(_this.intervals, function (key, value) {
                        _this.log({type: 'unload', data: value, msg: 'UNLOAD INTERVAL'});
                        clearInterval(value);
                });
                for (var key in _this.dataRepos) {
                        if (_this.dataRepos.hasOwnProperty(key)) {
                                _this.dataRepos[key].unload();
                        }
                }
        };

        return ruckus.views.repositories.base;
});