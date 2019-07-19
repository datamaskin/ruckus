// Author: Scott Gay
define([
//	"assets/js/base.js",
        "assets/js/models/basews.js",
        "assets/js/libraries/jquery.min.js",
        "assets/js/libraries/jquery.atmosphere.js"
], function (Base) {
        // PARAMETERS

        ruckus.models.contestevents = function (parameters) {
                Base.call(this);
                this.init = function () {
                        this.parameters = parameters;
                        // set parameter defaults
                        this.modelData = undefined;
                };
                this.init();
        };

        ruckus.models.contestevents.prototype = Object.create(Base.prototype);

        ruckus.models.contestevents.prototype.fetch = function (fParams) {
                var _this = this;

                // RESTFUL
                var request = {
                        'type': 'GET',
                        'url': '/contestevents',
                        'data': 'contestId=' + fParams.contestId,
                        'callback': function (data) {
                                _this.modelData = JSON.parse(data);
                                _this.log({type: 'api', data: _this.modelData, msg: "CONTEST EVENTS DATA MODEL"});
//				_this.modelData = _this.staticData();
                                _this.msgBus.publish("model.contestevents.retrieve", {data: _this.modelData});
                        },
                        'failcallback': function (data) {
                                _this.log({type: 'api', data: data, msg: 'CONTEST EVENTS AJAX CALL FAILED'});
                        }
                };
                this.sendRequest(request);

        };

        ruckus.models.contestevents.prototype.staticData = function () {
                return {};
        };

        return ruckus.models.contestevents;
});
