// Author: Scott Gay
define([
//	"assets/js/base.js",
        "assets/js/models/basews.js",
        "assets/js/libraries/jquery.min.js",
        "assets/js/libraries/jquery.atmosphere.js"
], function (Base) {
        // PARAMETERS

        ruckus.models.contestentries = function (parameters) {
                Base.call(this);
                this.init = function () {
                        this.parameters = parameters;
                        // set parameter defaults
                        this.modelData = undefined;
                };
                this.init();
        };

        ruckus.models.contestentries.prototype = Object.create(Base.prototype);

        ruckus.models.contestentries.prototype.fetch = function (fParams) {
                var _this = this;

                // RESTFUL
                var request = {
                        'type': 'GET',
                        'url': '/contestentries', // /generic.js (static file)
                        'data': 'contestId=' + fParams.id,
                        'callback': function (data) {
                                _this.modelData = JSON.parse(data);
                                _this.log({type: 'api', data: _this.modelData, msg: "CONTEST ENTRIES DATA MODEL"});
//				_this.modelData = _this.staticData();
                                _this.msgBus.publish("model.contestentries.retrieve", {data: _this.modelData});
                        },
                        'failcallback': function (data) {
                                _this.log({type: 'api', data: data, msg: 'CONTEST ENTRIES AJAX CALL FAILED'});
                        }
                };
                this.sendRequest(request);

        };

        ruckus.models.contestentries.prototype.staticData = function () {
                return {};
        };

        return ruckus.models.contestentries;
});
