// Author: Scott Gay
define([
//	"assets/js/base.js",
        "assets/js/models/basews.js",
        "assets/js/libraries/jquery.min.js",
        "assets/js/libraries/jquery.atmosphere.js"
], function (Base) {
        // PARAMETERS

        ruckus.models.quicklineups = function (parameters) {
                Base.call(this);
                this.init = function () {
                        this.parameters = parameters;
                        // set parameter defaults
                        this.modelData = undefined;
                };
                this.init();
        };

        ruckus.models.quicklineups.prototype = Object.create(Base.prototype);

        ruckus.models.quicklineups.prototype.fetch = function (fParams) {
                var _this = this;

                // RESTFUL
                var request = {
                        'type': 'GET',
                        'url': '/quicklineups/' + fParams.contestId,
                        'data': '',
                        'contentType': "application/json; charset=utf-8",
                        'callback': function (data) {
                                _this.modelData = JSON.parse(data);
                                _this.log({type: 'api', data: _this.modelData, msg: "QUICK LINEUPS DATA MODEL"});
//				_this.modelData = _this.staticData();
                                _this.msgBus.publish("model.quicklineups.retrieve", {data: _this.modelData});
                        },
                        'failcallback': function (data) {
                                _this.log({type: 'api', data: data, msg: 'QUICK LINEUPS AJAX CALL FAILED'});
                        }
                };
                this.sendRequest(request);
        };

        ruckus.models.quicklineups.prototype.staticData = function () {
                return {};
        };

        return ruckus.models.quicklineups;
});