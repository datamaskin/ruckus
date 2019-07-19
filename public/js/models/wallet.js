// Author: Scott Gay
define([
//	"assets/js/base.js",
        "assets/js/models/basews.js",
        "assets/js/libraries/jquery.min.js",
        "assets/js/libraries/jquery.atmosphere.js"
], function (Base) {
    // PARAMETERS

    ruckus.models.wallet = function (parameters) {
        Base.call(this);
        this.init = function () {
                this.parameters = parameters;
                // set parameter defaults
                this.modelData = undefined;
        };
        this.init();
    };

    ruckus.models.wallet.prototype = Object.create(Base.prototype);

    ruckus.models.wallet.prototype.fetch = function (fParams) {
        var _this = this;

        // RESTFUL
        var request = {
            'type': 'GET',
            'url': '/wallet', // /generic.js (static file)
            'data': '',
            'contentType': "application/json; charset=utf-8",
            'callback': function (data) {
                    _this.modelData = JSON.parse(data);
                    _this.log({type: 'api', data: _this.modelData, msg: "wallet DATA MODEL"});
                    _this.msgBus.publish("model.wallet.retrieve", {data: _this.modelData});
            },
            'failcallback': function (data) {
                    _this.log({type: 'api', data: data, msg: 'wallet AJAX CALL FAILED'});
            }
        };
        this.sendRequest(request);
    };

    ruckus.models.wallet.prototype.staticData = function () {
            return {};
    };

    return ruckus.models.wallet;
});
