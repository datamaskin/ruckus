// Author: Scott Gay
define([
//	"assets/js/base.js",
        "assets/js/models/basews.js",
        "assets/js/libraries/jquery.min.js",
        "assets/js/libraries/jquery.atmosphere.js"
], function (Base) {
        // PARAMETERS

        ruckus.models.generic = function (parameters) {
                Base.call(this);
                this.init = function () {
                        this.parameters = parameters;
                        // set parameter defaults
                        this.modelData = undefined;
                };
                this.init();
        };

        ruckus.models.generic.prototype = Object.create(Base.prototype);

        ruckus.models.generic.prototype.fetch = function (fParams) {
                var _this = this;

                // RESTFUL
                var request = {
                        'type': 'GET',
                        'url': '/generic', // /generic.js (static file)
                        'data': '',
                        'contentType': "application/json; charset=utf-8",
                        'callback': function (data) {
                                _this.modelData = JSON.parse(data);
                                _this.log({type: 'api', data: _this.modelData, msg: "GENERIC DATA MODEL"});
//				_this.modelData = _this.staticData();
                                _this.msgBus.publish("model.generic.retrieve", {data: _this.modelData});
                        },
                        'failcallback': function (data) {
                                _this.log({type: 'api', data: data, msg: 'GENERIC AJAX CALL FAILED'});
                        }
                };
                this.sendRequest(request);

                // SOCKET
                var sub = _this.msgBus.subscribe("socket.GENERIC_ALL", function (data) {
                        sub.unsubscribe();
                        _this.modelData = data;
                        // for static data
//			_this.modelData = _this.staticData();
                        // attach any bindings here (knockout)
                        _this.msgBus.publish("model.generic.all", {data: _this.modelData});
                });
                this.subscriptions.push(sub);
                var sub2 = _this.msgBus.subscribe("socket.GENERIC_UPDATE", function (data) {

                });
                this.subscriptions.push(sub2);
                this.openChannel('/generic');

        };

        ruckus.models.generic.prototype.staticData = function () {
                return {};
        };

        return ruckus.models.generic;
});
