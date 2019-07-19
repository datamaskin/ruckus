// Author: Scott Gay
define([
//	"assets/js/base.js",
        "assets/js/models/basews.js",
        "assets/js/libraries/jquery.min.js",
        "assets/js/libraries/jquery.atmosphere.js"
], function (Base) {
        // PARAMETERS

        ruckus.models.lineuprules = function (parameters) {
                Base.call(this);
                this.init = function () {
                        this.parameters = parameters;
                        // set parameter defaults
                        this.modelData = undefined;
                };
                this.init();
        };

        ruckus.models.lineuprules.prototype = Object.create(Base.prototype);

        ruckus.models.lineuprules.prototype.fetch = function (fParams) {
                var _this = this;

                // RESTFUL
                var request = {
                        'type': 'GET',
                        'url': '/lineupRules.js',
                        'data': 'league=' + fParams.league,
                        'contentType': "application/json; charset=utf-8",
                        'callback': function (data) {
                                _this.modelData = JSON.parse(data);
//				_this.modelData = _this.staticData();
                                // remove numerical characters
                                $.each(_this.modelData, function (k, v) {
                                        v.displayabbreviation = v.abbreviation;
                                        switch (v.abbreviation) {
                                                case '1B' :
                                                        v.abbreviation = 'FB';
                                                        break;
                                                case '2B' :
                                                        v.abbreviation = 'SB';
                                                        break;
                                                case '3B' :
                                                        v.abbreviation = 'TB';
                                                        break;
                                        }
                                });
                                _this.log({type: 'api', data: _this.modelData, msg: "LINEUP RULES DATA MODEL"});
//				_this.modelData = _this.staticData();
                                _this.msgBus.publish("model.lineuprules.retrieve", {data: _this.modelData});
                        },
                        'failcallback': function (data) {
                                _this.log({type: 'api', data: data, msg: 'LINEUP RULES AJAX CALL FAILED'});
                        }
                };
                this.sendRequest(request);

        };

	ruckus.models.lineuprules.prototype.staticData = function () {
                return {"16":{"numberOfAthletes":1,"abbreviation":"DEF"},"10":{"numberOfAthletes":1,"abbreviation":"QB"},"11":{"numberOfAthletes":2,"flex":true,"abbreviation":"RB"},"12":{"numberOfAthletes":2,"flex":true,"abbreviation":"WR"},"13":{"numberOfAthletes":1,"flex":true,"abbreviation":"TE"},"15":{"numberOfAthletes":2,"abbreviation":"FX"}};
//                return {"16":{"numberOfAthletes":1,"abbreviation":"DEF"},"10":{"numberOfAthletes":1,"abbreviation":"QB"},"11":{"numberOfAthletes":2,"flex":true,"abbreviation":"RB"},"12":{"numberOfAthletes":2,"flex":true,"abbreviation":"WR"},"13":{"numberOfAthletes":1,"flex":true,"abbreviation":"TE"},"14":{"numberOfAthletes":1,"abbreviation":"K"},"15":{"numberOfAthletes":2,"abbreviation":"FX"}};
        };

        return ruckus.models.lineuprules;
});
