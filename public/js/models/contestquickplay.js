// Author: Scott Gay
define([
//	"assets/js/base.js",
        "assets/js/models/basews.js",
        "assets/js/libraries/jquery.min.js",
        "assets/js/libraries/jquery.atmosphere.js"
], function (Base) {
        // PARAMETERS

        ruckus.models.contestquickplay = function (parameters) {
                Base.call(this);
                this.init = function () {
                        this.parameters = parameters;
                        // set parameter defaults
                        this.modelData = undefined;
                };
                this.init();
        };

        ruckus.models.contestquickplay.prototype = Object.create(Base.prototype);

        ruckus.models.contestquickplay.prototype.fetch = function (fParams) {
                var _this = this;

                fParams.league = fParams.sport;
                fParams.sport = undefined;

                // RESTFUL
                var request = {
                        'type': 'POST',
                        'url': '/quickplay',
                        'data': 'data=' + JSON.stringify(fParams),
                        'contentType': "application/json; charset=utf-8",
                        'callback': function (data) {
                                _this.modelData = JSON.parse(data);
                                _this.log({type: 'api', data: _this.modelData, msg: "CONTEST QUICK PLAY DATA MODEL"});
//				_this.modelData = _this.staticData();
                                _this.msgBus.publish("model.contestquickplay.retrieve", {data: _this.modelData});
                        },
                        'failcallback': function (data) {
                                _this.log({type: 'api', data: data, msg: 'CONTEST QUICK PLAY AJAX CALL FAILED'});
                        }
                };
                this.sendRequest(request);

        };

        ruckus.models.contestquickplay.prototype.staticData = function () {
                return {};
        };

        return ruckus.models.contestquickplay;
});
