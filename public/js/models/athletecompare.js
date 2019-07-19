// Author: Scott Gay
define([
//	"assets/js/base.js",
        "assets/js/models/basews.js",
        "assets/js/libraries/jquery.min.js",
        "assets/js/libraries/jquery.atmosphere.js"
], function (Base) {
        // PARAMETERS

        ruckus.models.athletecompare = function (parameters) {
                Base.call(this);
                this.init = function () {
                        this.parameters = parameters;
                        // set parameter defaults
                        this.modelData = undefined;
                };
                this.init();
        };

        ruckus.models.athletecompare.prototype = Object.create(Base.prototype);

        ruckus.models.athletecompare.prototype.fetch = function (fParams) {
                var _this = this;

                // RESTFUL
                var request = {
                        'type': 'GET',
                        'url': '/athletecompare/' + fParams.contestId + '/' + fParams.athleteSportEventInfoId,
                        'data': '',
                        'contentType': "application/json; charset=utf-8",
                        'callback': function (data) {
                                _this.modelData = JSON.parse(data);
                                _this.log({type: 'api', data: _this.modelData, msg: "ATHLETE COMPARE DATA MODEL"});
//				_this.modelData = _this.staticData();
                                _this.msgBus.publish("model.athletecompare.retrieve", {data: _this.modelData});
                        },
                        'failcallback': function (data) {
                                _this.log({type: 'api', data: data, msg: 'ATHLETE COMPARE AJAX CALL FAILED'});
                        }
                };
                this.sendRequest(request);
        };

        ruckus.models.athletecompare.prototype.staticData = function () {
                return {};
        };

        return ruckus.models.athletecompare;
});
