// Author: Scott Gay
define([
//	"assets/js/base.js",
        "assets/js/models/basews.js",
        "assets/js/libraries/jquery.min.js",
        "assets/js/libraries/jquery.atmosphere.js"
], function (Base) {
        // PARAMETERS

        ruckus.models.lineupenter = function (parameters) {
                Base.call(this);
                this.init = function () {
                        this.parameters = parameters;
                        // set parameter defaults
                        this.modelData = undefined;
                };
                this.init();
        };

        ruckus.models.lineupenter.prototype = Object.create(Base.prototype);

        ruckus.models.lineupenter.prototype.fetch = function (fParams) {
                var _this = this;

                // RESTFUL
                var request = {
                        'type': 'POST',
                        'url': '/lineupEnter',
                        'data': 'data=' + JSON.stringify(fParams),
                        'contentType': "application/json; charset=utf-8",
                        'callback': function (data) {
                                _this.modelData = JSON.parse(data);
                                _this.log({type: 'api', data: _this.modelData, msg: "LINEUP ENTER SAVED"});
                                _this.msgBus.publish("model.lineupenter.success", {data: _this.modelData});
                        },
                        'failcallback': function (data) {
                                _this.log({type: 'api', data: data, msg: 'LINEUP AJAX CALL FAILED'});
                                _this.msgBus.publish("model.lineupenter.failed", {error: data});
                        }
                };
                this.sendRequest(request);

        };

        ruckus.models.lineupenter.prototype.staticData = function () {
                return {};
        };

        return ruckus.models.lineupenter;
});
