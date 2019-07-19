// Author: Scott Gay
define([
//	"assets/js/base.js",
        "assets/js/models/basews.js",
        "assets/js/libraries/jquery.min.js",
        "assets/js/libraries/jquery.atmosphere.js"
], function (Base) {
        // PARAMETERS

        ruckus.models.lineupupdate = function (parameters) {
                Base.call(this);
                this.init = function () {
                        this.parameters = parameters;
                        // set parameter defaults
                        this.modelData = undefined;
                };
                this.init();
        };

        ruckus.models.lineupupdate.prototype = Object.create(Base.prototype);

        ruckus.models.lineupupdate.prototype.fetch = function (fParams) {
                var _this = this;

                // RESTFUL
                var request = {
                        'type': 'POST',
                        'url': '/lineupUpdate', 
                        'data': 'data=' + JSON.stringify(fParams),
                        'contentType': "application/json; charset=utf-8",
                        'callback': function (data) {
                                _this.modelData = JSON.parse(data);
                                _this.log({type: 'api', data: _this.modelData, msg: "LINEUP UPDATE DATA MODEL"});
//				_this.modelData = _this.staticData();
                                _this.msgBus.publish("model.lineupupdate.success", {data: _this.modelData});
                        },
                        'failcallback': function (data) {
                                _this.log({type: 'api', data: data, msg: 'LINEUP UPDATE AJAX CALL FAILED'});
                                _this.msgBus.publish("model.lineupupdate.failed", {error:data});
                        }
                };
                this.sendRequest(request);
        };

        ruckus.models.lineupupdate.prototype.staticData = function () {
                return {};
        };

        return ruckus.models.lineupupdate;
});
