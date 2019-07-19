// Author: Scott Gay
define([
//	"assets/js/base.js",
        "assets/js/models/basews.js",
        "assets/js/libraries/jquery.min.js",
        "assets/js/libraries/jquery.atmosphere.js"
], function (Base) {
        // PARAMETERS

        ruckus.models.contestfilter = function (parameters) {
                Base.call(this);
                this.init = function () {
                        this.parameters = parameters;
                        // set parameter defaults
                };
                this.init();
        };

        ruckus.models.contestfilter.prototype = Object.create(Base.prototype);

        ruckus.models.contestfilter.prototype.fetch = function (fParams) {
                var _this = this;

                var request = {
                        'type': 'GET',
                        'url': '/contestfilter.js',
                        'data': '',
                        'callback': function (data) {
                                _this.modelData = JSON.parse(data);
                                _this.log({type: 'api', data: _this.modelData, msg: "CONTEST FILTER DATA MODEL"});
//				_this.modelData = _this.staticData();
                                _this.msgBus.publish("model.contestfilter.retrieve", {data: _this.modelData});
                        },
                        'failcallback': function (data) {
                                _this.log({type: 'api', data: data, msg: 'CONTEST FILTER AJAX CALL FAILED'});
                        }
                };
                this.sendRequest(request);
        };

        ruckus.models.contestfilter.prototype.staticData = function () {
                return {
                        timestamp: 1399920058,
                        sports: [
                                {
                                        id: 1001,
                                        name: "ALL",
                                        selected: true,
                                        active: true,
                                        entry_fee: [100, 200, 300, 500, 1000, 1500, 2500, 3500, 5000, 7500, 10000, 50000],
                                        num_players: [
                                                {
                                                        min: 0,
                                                        max: 100000
                                                },
                                                {
                                                        min: 2,
                                                        max: 2
                                                },
                                                {
                                                        min: 3,
                                                        max: 6
                                                },
                                                {
                                                        min: 7,
                                                        max: 10
                                                },
                                                {
                                                        min: 11,
                                                        max: 44
                                                },
                                                {
                                                        min: 45,
                                                        max: 100000
                                                }
                                        ],
                                        grouping: ['early', 'late'],
                                        salary_cap: [
                                                50000
                                        ]
                                },
                                {
                                        id: 1002,
                                        name: "NFL",
                                        selected: false,
                                        active: true,
                                        entry_fee: [100, 300, 1000, 2500, 5000, 10000, 50000],
                                        num_players: [
                                                {
                                                        min: 0,
                                                        max: 100000
                                                },
                                                {
                                                        min: 2,
                                                        max: 2
                                                },
                                                {
                                                        min: 3,
                                                        max: 6
                                                },
                                                {
                                                        min: 11,
                                                        max: 44
                                                },
                                                {
                                                        min: 45,
                                                        max: 100000
                                                }
                                        ],
                                        grouping: ['early', 'late'],
                                        salary_cap: [
                                                50000
                                        ]
                                },
                                {
                                        id: 1003,
                                        name: "MLB",
                                        selected: false,
                                        active: false,
                                        entry_fee: [100, 200, 300, 500, 1000],
                                        num_players: [
                                                {
                                                        min: 0,
                                                        max: 100000
                                                },
                                                {
                                                        min: 2,
                                                        max: 2
                                                },
                                                {
                                                        min: 7,
                                                        max: 10
                                                },
                                                {
                                                        min: 11,
                                                        max: 44
                                                },
                                                {
                                                        min: 45,
                                                        max: 100000
                                                }
                                        ],
                                        grouping: ['early', 'late'],
                                        salary_cap: [
                                                50000
                                        ]
                                },
                                {
                                        id: 1004,
                                        name: "NHL",
                                        selected: false,
                                        active: true,
                                        entry_fee: [100, 500, 1000, 1500, 5000],
                                        num_players: [
                                                {
                                                        min: 0,
                                                        max: 10000
                                                },
                                                {
                                                        min: 2,
                                                        max: 2
                                                },
                                                {
                                                        min: 3,
                                                        max: 6
                                                },
                                                {
                                                        min: 7,
                                                        max: 10
                                                }
                                        ],
                                        grouping: ['early', 'late'],
                                        salary_cap: [
                                                50000
                                        ]
                                },
                                {
                                        id: 1005,
                                        name: "NBA",
                                        selected: false,
                                        active: true,
                                        entry_fee: [100, 500, 1000, 1500],
                                        num_players: [
                                                {
                                                        min: 0,
                                                        max: 100000
                                                },
                                                {
                                                        min: 7,
                                                        max: 10
                                                },
                                                {
                                                        min: 11,
                                                        max: 44
                                                },
                                                {
                                                        min: 45,
                                                        max: 100000
                                                }
                                        ],
                                        grouping: ['early', 'late'],
                                        salary_cap: [
                                                50000
                                        ]
                                },
                                {
                                        id: 1006,
                                        name: "PGA",
                                        selected: false,
                                        active: false,
                                        entry_fee: [100, 200, 300, 500, 1000, 1500, 5000, 10000],
                                        num_players: [
                                                {
                                                        min: 0,
                                                        max: 100000
                                                },
                                                {
                                                        min: 2,
                                                        max: 2
                                                },
                                                {
                                                        min: 7,
                                                        max: 10
                                                },
                                                {
                                                        min: 45,
                                                        max: 100000
                                                }
                                        ],
                                        grouping: ['early', 'late'],
                                        salary_cap: [
                                                50000
                                        ]
                                }
                        ]
                };
        };

        return ruckus.models.contestfilter;
});
