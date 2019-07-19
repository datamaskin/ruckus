// Author: Scott Gay
define([
//	"assets/js/base.js",
        "assets/js/models/basews.js",
        "assets/js/libraries/jquery.min.js",
        "assets/js/libraries/jquery.atmosphere.js"
], function (Base) {
        // PARAMETERS

        ruckus.models.liveallathletes = function (parameters) {
                Base.call(this);
                this.init = function () {
                        this.parameters = parameters;
                        // set parameter defaults
                        this.modelData = undefined;
                };
                this.init();
        };

        ruckus.models.liveallathletes.prototype = Object.create(Base.prototype);

        ruckus.models.liveallathletes.prototype.fetch = function (fParams) {
                var _this = this;

                // RESTFUL
                var request = {
                        'type': 'GET',
                        'url': '/contestliveathletes/',
                        'data': '',
                        'contentType': "application/json; charset=utf-8",
                        'callback': function (data) {
                                _this.modelData = JSON.parse(data);
                                _this.log({type: 'api', data: _this.modelData, msg: "LIVE ALL ATHLETES DATA MODEL"});
//				_this.modelData = _this.staticData();
                                _this.msgBus.publish("model.liveallathletes.retrieve", {data: _this.modelData});
                        },
                        'failcallback': function (data) {
                                _this.log({type: 'api', data: data, msg: 'LIVE ALL ATHLETES AJAX CALL FAILED'});
                        }
                };
                this.sendRequest(request);

        };

        ruckus.models.liveallathletes.prototype.staticData = function () {
                return {};
        };

        return ruckus.models.liveallathletes;
});
