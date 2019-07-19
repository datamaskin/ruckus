// Author: Scott Gay
define([
//	"assets/js/base.js",
        "assets/js/models/basews.js",
        "assets/js/libraries/jquery.min.js",
        "assets/js/libraries/jquery.atmosphere.js"
], function (Base) {
        // PARAMETERS

        ruckus.models.contestdetailranks = function (parameters) {
                Base.call(this);
                this.init = function () {
                        this.parameters = parameters;
                        // set parameter defaults
                        this.modelData = undefined;
                };
                this.init();
        };

        ruckus.models.contestdetailranks.prototype = Object.create(Base.prototype);

        ruckus.models.contestdetailranks.prototype.fetch = function (fParams) {
                var _this = this;

                // RESTFUL

                var request = {
                        'type': 'GET',
                        'url': '/contestliveranks/' + fParams.id,
//			'data' : 'contestId='+fParams.id,
                        'data': '',
                        'contentType': "application/json; charset=utf-8",
                        'callback': function (data) {
                                _this.modelData = JSON.parse(data);
                                _this.log({type: 'api', data: _this.modelData, msg: "CONTEST DETAIL RANKS DATA MODEL"});

//				_this.modelData = _this.staticData();
                                _this.msgBus.publish("model.contestdetailranks.retrieve", {data: _this.modelData});

                        },
                        'failcallback': function (data) {
                                _this.log({type: 'api', data: data, msg: 'CONTEST DETAIL RANKS AJAX CALL FAILED'});
                        }
                };
                this.sendRequest(request);

        };

        ruckus.models.contestdetailranks.prototype.staticData = function () {
                return [
                        {
                                lineupId: 12345,
                                user: "Condia",
                                unitsRemaining: 12,
                                fpp: 24.3,
                                isMe: true
                        },
                        {
                                lineupId: 12346,
                                user: "Max",
                                unitsRemaining: 45,
                                fpp: 14.3
                        },
                        {
                                lineupId: 12347,
                                user: "George",
                                unitsRemaining: 21,
                                fpp: 32.2
                        }
                ];
        };

        return ruckus.models.contestdetailranks;
});
