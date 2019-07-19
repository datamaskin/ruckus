// Author: Scott Gay
define([
        "rg_model_base"
], function (Base) {
        ruckus.models.contestliveoverviewhistory = function (parameters) {
                Base.call(this);
                this.init = function () {
                        this.parameters = parameters;
                        // set parameter defaults
                        this.modelData = undefined;
                };
                this.init();
        };

        ruckus.models.contestliveoverviewhistory.prototype = Object.create(Base.prototype);

        ruckus.models.contestliveoverviewhistory.prototype.fetch = function (fParams) {
                var _this = this;

                var sub = _this.msgBus.subscribe("socket.CONTESTLIVEOVERVIEW_ALL", function (data) {
                        sub.unsubscribe();
                        _this.modelData = {contests: data};
                        _this.msgBus.publish("model.contestliveoverviewhistory.all", {contests: _this.modelData});
                });
                this.subscriptions.push(sub);
                this.openChannel('/ws/contestliveoverview/history');

        };

        ruckus.models.contestliveoverviewhistory.prototype.staticData = function () {
                return {};
        };

        return ruckus.models.contestliveoverviewhistory;
});
