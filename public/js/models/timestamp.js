define([ "rg_model_base","rg_pubsub", "atmosphere"
], function (Base) {
        // INIT
        ruckus.models.timestamp = function (parameters) {
                Base.call(this);
                this.init = function () {
                        this.parameters = parameters;
                        // set parameter defaults
                        this.modelData = undefined;
                };

                _.bindAll(this, "fetch", "push");
                this.init();
        };

        ruckus.models.timestamp.prototype = Object.create(Base.prototype);

        ruckus.models.timestamp.prototype.fetch = function (fParams) {
                var _this = this;
                // RESTFUL
                var request = {
                        'type': 'GET',
                        'url': '/timestamp', // /timestamp.js (static file)
                        'data': '',
                        'contentType': "application/json; charset=utf-8",
                        'callback': _this.push,
                        'failcallback': function (data) {
                                _this.log({type: 'api', data: data, msg: 'timestamp AJAX CALL FAILED'});
                        }
                };
                _this.sendRequest(request);

        };

        ruckus.models.timestamp.prototype.push = function (data) {
                var _this = this;
                _this.log({type: 'api', data: data, msg: "timestamp DATA MODEL"});
                _this.msgBus.publish(ruckus.pubsub.subscriptions.models.data.timestamp.servertime, data);
        };

        ruckus.models.timestamp.prototype.staticData = function () {
                return {};
        };

        return ruckus.models.timestamp;
});
