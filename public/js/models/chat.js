// Author: Scott Gay
define([
//	"assets/js/base.js",
        "assets/js/models/basews.js",
        "assets/js/libraries/jquery.min.js",
        "assets/js/libraries/jquery.atmosphere.js"
], function (Base) {
        // PARAMETERS

        ruckus.models.chat = function (parameters) {
                Base.call(this);
                this.init = function () {
                        this.parameters = parameters;
                        // set parameter defaults
                        this.modelData = undefined;
                };
                this.init();
        };

        ruckus.models.chat.prototype = Object.create(Base.prototype);

        ruckus.models.chat.prototype.fetch = function (fParams) {
                var _this = this;

                // SOCKET
                var sub = _this.msgBus.subscribe("socket.CHAT_ALL", function (data) {
                        sub.unsubscribe();
                        _this.modelData = data;
                        // for static data
//			_this.modelData = _this.staticData();
                        // attach any bindings here (knockout)
                        _this.msgBus.publish("model.chat.all", {data: _this.modelData});
                });
                this.subscriptions.push(sub);
                var sub2 = _this.msgBus.subscribe("socket.CHAT_MSG", function (data) {
                        _this.msgBus.publish("model.chat.update", data);
                });
                this.subscriptions.push(sub2);
                this.openChannel('/ws/chat/' + fParams.id);

        };

        ruckus.models.chat.prototype.push = function (fParams) {
                this.channelPush(fParams);
        };

        ruckus.models.chat.prototype.staticData = function () {
                return {};
        };

        return ruckus.models.chat;
});
