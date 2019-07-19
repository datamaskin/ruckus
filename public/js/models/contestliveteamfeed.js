// Author: Scott Gay
define([
//	"assets/js/base.js",
        "assets/js/models/basews.js",
        "assets/js/libraries/jquery.min.js",
        "assets/js/libraries/jquery.atmosphere.js"
], function (Base) {
        // PARAMETERS

        ruckus.models.contestliveteamfeed = function (parameters) {
                Base.call(this);
                this.init = function () {
                        this.parameters = parameters;
                        // set parameter defaults
                        this.modelData = undefined;
                };
                this.init();
        };

        ruckus.models.contestliveteamfeed.prototype = Object.create(Base.prototype);

        ruckus.models.contestliveteamfeed.prototype.fetch = function (fParams) {
                var _this = this;

                // RESTFUL
                var request = {
                        'type': 'GET',
                        'url': '/contestliveteamfeed/' + fParams.lineupId,
                        'data': '',
                        'contentType': "application/json; charset=utf-8",
                        'callback': function (data) {
                                _this.modelData = JSON.parse(data);
                                _this.log({type: 'api', data: _this.modelData, msg: "CONTEST LIVE TEAM FEED DATA MODEL"});
                                // FIXME - pull from live data not static
//                                _this.modelData = _this.staticData();
                                _this.msgBus.publish("model.contestliveteamfeed.retrieve", {data: _this.modelData});
                        },
                        'failcallback': function (data) {
                                _this.log({type: 'api', data: data, msg: 'CONTEST LIVE TEAM FEED AJAX CALL FAILED'});
                        }
                };
                this.sendRequest(request);
        };

        ruckus.models.contestliveteamfeed.prototype.staticData = function () {
                return JSON.parse('[{"athleteSportEventInfoId":1, "timestamp":1405454700000, "description":"Ortiz singles to right field", "fpChange":"+3"},{"athleteSportEventInfoId":2, "timestamp":1405454700000, "description":"Pedroia doubles to deep center field", "fpChange":"+5"}]');
        };

        return ruckus.models.contestliveteamfeed;
});
