// Author: Scott Gay
define([
//	"assets/js/base.js",
        "assets/js/models/basews.js",
        "assets/js/libraries/jquery.min.js",
        "assets/js/libraries/jquery.atmosphere.js"
], function (Base) {
        // PARAMETERS

        ruckus.models.contestscoring = function (parameters) {
                Base.call(this);
                this.init = function () {
                        this.parameters = parameters;
                        // set parameter defaults
                        this.modelData = undefined;
                };
                this.init();
        };

        ruckus.models.contestscoring.prototype = Object.create(Base.prototype);

        ruckus.models.contestscoring.prototype.fetch = function (fParams) {
                var _this = this;

                // RESTFUL
                var request = {
                        'type': 'GET',
                        'url': '/scoring.js',
                        'data': '',
                        'callback': function (data) {
                                _this.modelData = JSON.parse(data);
                                _this.log({type: 'api', data: _this.modelData, msg: "CONTEST SCORING DATA MODEL"});
//				_this.modelData = _this.staticData();
                                _this.msgBus.publish("model.contestscoring.retrieve", {data: _this.modelData});
                        },
                        'failcallback': function (data) {
                                _this.log({type: 'api', data: data, msg: 'CONTEST SCORING AJAX CALL FAILED'});
                        }
                };
                this.sendRequest(request);
        };

        ruckus.models.contestscoring.prototype.staticData = function () {
                return {};
        };

        return ruckus.models.contestscoring;
});
