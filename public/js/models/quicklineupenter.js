// Author: Scott Gay
define([
//	"assets/js/base.js",
        "assets/js/models/basews.js",
        "assets/js/libraries/jquery.min.js",
        "assets/js/libraries/jquery.atmosphere.js"
], function (Base) {
        // PARAMETERS

        ruckus.models.quicklineupenter = function (parameters) {
                Base.call(this);
                this.init = function () {
                        this.parameters = parameters;
                        // set parameter defaults
                        this.modelData = undefined;
                };
                this.init();
        };

        ruckus.models.quicklineupenter.prototype = Object.create(Base.prototype);

        ruckus.models.quicklineupenter.prototype.fetch = function (fParams) {
                var _this = this;

                // RESTFUL
                var request = {
                        'type': 'POST',
                        'url': '/lineupEnterQuick',
                        'data': 'data='+JSON.stringify(fParams),
                        'contentType': "application/json; charset=utf-8",
                        'callback': function (data) {
                                _this.modelData = JSON.parse(data);
                                _this.log({type: 'api', data: _this.modelData, msg: "QUICK LINEUP ENTER DATA MODEL"});
//				_this.modelData = _this.staticData();
                                _this.msgBus.publish("model.quicklineupenter.success", {data: _this.modelData});
                        },
                        'failcallback': function (data) {
                                _this.log({type: 'api', data: data, msg: 'QUICK LINEUP ENTER AJAX CALL FAILED'});
                                _this.msgBus.publish("model.quicklineupenter.failed", {data: data});
                        }
                };
                this.sendRequest(request);
        };

        ruckus.models.quicklineupenter.prototype.staticData = function () {
                return {};
        };

        return ruckus.models.quicklineupenter;
});
