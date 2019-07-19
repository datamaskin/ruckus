// Author: Scott Gay
define([
//	"assets/js/base.js",
        "assets/js/models/basews.js",
        "assets/js/libraries/jquery.min.js",
        "assets/js/libraries/jquery.atmosphere.js"
], function (Base) {
        // PARAMETERS

        ruckus.models.walletProfile = function (parameters) {
                Base.call(this);
                this.init = function () {
                        this.parameters = parameters;
                        // set parameter defaults
                        this.modelData = undefined;
                };
                this.init();
        };

        ruckus.models.walletProfile.prototype = Object.create(Base.prototype);

        ruckus.models.walletProfile.prototype.fetch = function (fParams) {
                var _this = this;

                // RESTFUL
                var request = {
                        'type': 'GET',
                        'url': '/wallet/getUserProfiles', // /walletProfile.js (static file)
                        'data': '',
                        'callback': function (data) {
                                _this.modelData = JSON.parse(data);
                                _this.log({type: 'api', data: _this.modelData, msg: "WALLETPROFILE DATA MODEL"});
                                _this.msgBus.publish("model.walletProfile.retrieve", _this.modelData.payload);
                        },
                        'failcallback': function (data) {
                                _this.log({type: 'api', data: data, msg: 'WALLETPROFILE AJAX CALL FAILED'});
                        }
                };
                this.sendRequest(request);
        };

        ruckus.models.walletProfile.prototype.staticData = function () {
                return {};
        };

        return ruckus.models.walletProfile;
});
