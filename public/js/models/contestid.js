// Author: Scott Gay
define([
//	"assets/js/base.js",
        "assets/js/models/basews.js",
        "assets/js/libraries/jquery.min.js",
        "assets/js/libraries/jquery.atmosphere.js"
], function (Base) {
        // PARAMETERS

        ruckus.models.contestid = function (parameters) {
                Base.call(this);
                this.init = function () {
                        this.parameters = parameters;
                        // set parameter defaults
                };
                this.init();
        };

        ruckus.models.contestid.prototype = Object.create(Base.prototype);

        ruckus.models.contestid.prototype.fetch = function (fParams) {
                var _this = this;
                var sub = _this.msgBus.subscribe("socket.CONTESTS_ALL", function (data) {
                        sub.unsubscribe();
                        _this.modelData = {contests: data};
                        // for static data
//			_this.modelData = _this.staticData();
                        // attach any bindings here (knockout)
                        _this.msgBus.publish("model.contestid.all", {data: _this.modelData});
                });
                this.subscriptions.push(sub);
                var sub2 = _this.msgBus.subscribe("socket.CONTEST_UPDATE", function (data) {
                        // update the model (is this even necessary unless we are going to data bind to the form?) ... still probably good idea to keep in sync in case the model gets passed to a new control
                        if (_this.modelData.contests[0].id == data.id)
                                _this.modelData.contests[0].currentEntries = data.currentEntries;
                        // publish the update
                        _this.msgBus.publish("model.contestid.update", data);
                });
                this.subscriptions.push(sub2);
                this.openChannel('/ws/contests/' + fParams.contestid);
        };

        ruckus.models.contestid.prototype.staticData = function () {
                // NOTE !!! - scoring and lineups could easily be their own APIs.  Added them here only since its all going to be needed anyway and might as well make and manage one call vs several (discuss)
                return {
                        timestamp: 1399920058,
                        scoring: {
                                nfl: {
                                        rushing_yard: .2,
                                        passing_yard: .1,
                                        receiving_yard: .1,
                                        rushing_td: 6,
                                        passing_td: 4,
                                        receiving_td: 6,
                                        extra_point: 1,
                                        field_goal: 3,
                                        field_goal_50: 4
                                }
                        },
                        lineups: [
                                {
                                        id: 1001,
                                        contest_ids: [
                                                1001, 1002
                                        ],
                                        name: 'Halfback Focus',
                                        roster: [
                                                {position: 'QB', name: 'M. Stafford'},
                                                {position: 'RB', name: 'M. Lynch'},
                                                {position: 'RB', name: 'A. Foster'},
                                                {position: 'WR', name: 'D. Bryant'},
                                                {position: 'WR', name: 'T. Williams'},
                                                {position: 'WR', name: 'C. Beasley'},
                                                {position: 'TE', name: 'J. Witten'},
                                                {position: 'DEF', name: 'CHI'},
                                                {position: 'K', name: 'D. Bailey'},
                                        ]
                                },
                                {
                                        id: 1002,
                                        contest_ids: [
                                                1001, 1002
                                        ],
                                        name: 'QB Focus',
                                        roster: [
                                                {position: 'QB', name: 'P. Manning'},
                                                {position: 'RB', name: 'M. Forte'},
                                                {position: 'RB', name: 'M. Ford'},
                                                {position: 'WR', name: 'B. Marshall'},
                                                {position: 'WR', name: 'A. Jeffery'},
                                                {position: 'WR', name: 'C. Williams'},
                                                {position: 'TE', name: 'M. Bennett'},
                                                {position: 'DEF', name: 'BAL'},
                                                {position: 'K', name: 'R. Gould'},
                                        ]
                                }
                        ],
                        contests: [
                                {
                                        id: 1001,
                                        type: "Guaranteed",
                                        sport: "NFL",
                                        multi_entry: false,
                                        guaranteed: true,
                                        current_entries: 34,
                                        size: 100,
                                        entry_pool: 4000,
                                        entry_fee: 10,
                                        grouping: 'early',
                                        salary_cap: 5000000,
                                        start_time: 1400004256,
                                        entries: [
                                                {user_id: 1001, username: 'jason0123'},
                                                {user_id: 1002, username: 'george'},
                                                {user_id: 1003, username: 'frank_355'},
                                                {user_id: 1004, username: 'jake'},
                                                {user_id: 1005, username: 'chris87'}
                                        ],
                                        games: [
                                                {home_team_id: 1001, home_team_name: 'ATL', away_team_id: 1007, away_team_name: 'CHI', start_time: 1400004256},
                                                {home_team_id: 1002, home_team_name: 'LA', away_team_id: 1008, away_team_name: 'STL', start_time: 1400004256},
                                                {home_team_id: 1003, home_team_name: 'PHI', away_team_id: 1009, away_team_name: 'HOU', start_time: 1400004256},
                                                {home_team_id: 1004, home_team_name: 'MIA', away_team_id: 1010, away_team_name: 'DAL', start_time: 1400004256},
                                                {home_team_id: 1005, home_team_name: 'SF', away_team_id: 1011, away_team_name: 'MIN', start_time: 1400004256},
                                                {home_team_id: 1006, home_team_name: 'PHX', away_team_id: 1012, away_team_name: 'WAS', start_time: 1400004256}
                                        ],
                                        prizes: [
                                                {position: '1', prize: 10000},
                                                {position: '2-5', prize: 5000},
                                                {position: '6-10', prize: 1000},
                                                {position: '10-20', prize: 500},
                                                {position: '20-30', prize: 200}
                                        ]
                                },
                                {
                                        id: 1002,
                                        type: "5050",
                                        sport: "NFL",
                                        multi_entry: false,
                                        guaranteed: true,
                                        current_entries: 12,
                                        size: 50,
                                        entry_pool: 2000,
                                        entry_fee: 1,
                                        grouping: 'all',
                                        salary_cap: 5000000,
                                        start_time: 1400003661,
                                        entries: [
                                                {user_id: 1001, username: 'jason0123'},
                                                {user_id: 1002, username: 'george'},
                                                {user_id: 1003, username: 'frank_355'},
                                                {user_id: 1004, username: 'jake'},
                                                {user_id: 1005, username: 'chris87'}
                                        ],
                                        games: [
                                                {home_team_id: 1001, home_team_name: 'ATL', away_team_id: 1007, away_team_name: 'CHI', start_time: 1400004256},
                                                {home_team_id: 1002, home_team_name: 'LA', away_team_id: 1008, away_team_name: 'STL', start_time: 1400004256},
                                                {home_team_id: 1003, home_team_name: 'PHI', away_team_id: 1009, away_team_name: 'HOU', start_time: 1400004256},
                                                {home_team_id: 1004, home_team_name: 'MIA', away_team_id: 1010, away_team_name: 'DAL', start_time: 1400004256},
                                                {home_team_id: 1005, home_team_name: 'SF', away_team_id: 1011, away_team_name: 'MIN', start_time: 1400004256},
                                                {home_team_id: 1006, home_team_name: 'PHX', away_team_id: 1012, away_team_name: 'WAS', start_time: 1400004256}
                                        ],
                                        prizes: [
                                                {position: '1', prize: 10000},
                                                {position: '2-5', prize: 5000},
                                                {position: '6-10', prize: 1000},
                                                {position: '10-20', prize: 500},
                                                {position: '20-30', prize: 200}
                                        ]
                                },
                                {
                                        id: 1003,
                                        type: "Normal",
                                        sport: "NFL",
                                        multi_entry: false,
                                        guaranteed: true,
                                        current_entries: 34,
                                        size: 100,
                                        entry_pool: 4000,
                                        entry_fee: 10,
                                        grouping: 'late',
                                        salary_cap: 5000000,
                                        start_time: 1399920058,
                                        entries: [
                                                {user_id: 1001, username: 'jason0123'},
                                                {user_id: 1002, username: 'george'},
                                                {user_id: 1003, username: 'frank_355'},
                                                {user_id: 1004, username: 'jake'},
                                                {user_id: 1005, username: 'chris87'}
                                        ],
                                        games: [
                                                {home_team_id: 1001, home_team_name: 'ATL', away_team_id: 1007, away_team_name: 'CHI', start_time: 1400004256},
                                                {home_team_id: 1002, home_team_name: 'LA', away_team_id: 1008, away_team_name: 'STL', start_time: 1400004256},
                                                {home_team_id: 1003, home_team_name: 'PHI', away_team_id: 1009, away_team_name: 'HOU', start_time: 1400004256},
                                                {home_team_id: 1004, home_team_name: 'MIA', away_team_id: 1010, away_team_name: 'DAL', start_time: 1400004256},
                                                {home_team_id: 1005, home_team_name: 'SF', away_team_id: 1011, away_team_name: 'MIN', start_time: 1400004256},
                                                {home_team_id: 1006, home_team_name: 'PHX', away_team_id: 1012, away_team_name: 'WAS', start_time: 1400004256}
                                        ],
                                        prizes: [
                                                {position: '1', prize: 10000},
                                                {position: '2-5', prize: 5000},
                                                {position: '6-10', prize: 1000},
                                                {position: '10-20', prize: 500},
                                                {position: '20-30', prize: 200}
                                        ]
                                },
                                {
                                        id: 1004,
                                        type: "Satellite",
                                        sport: "NFL",
                                        multi_entry: false,
                                        guaranteed: true,
                                        current_entries: 34,
                                        size: 100,
                                        entry_pool: 4000,
                                        entry_fee: 10,
                                        grouping: 'late',
                                        salary_cap: 5000000,
                                        start_time: 1399920058,
                                        entries: [
                                                {user_id: 1001, username: 'jason0123'},
                                                {user_id: 1002, username: 'george'},
                                                {user_id: 1003, username: 'frank_355'},
                                                {user_id: 1004, username: 'jake'},
                                                {user_id: 1005, username: 'chris87'}
                                        ],
                                        games: [
                                                {home_team_id: 1001, home_team_name: 'ATL', away_team_id: 1007, away_team_name: 'CHI', start_time: 1400004256},
                                                {home_team_id: 1002, home_team_name: 'LA', away_team_id: 1008, away_team_name: 'STL', start_time: 1400004256},
                                                {home_team_id: 1003, home_team_name: 'PHI', away_team_id: 1009, away_team_name: 'HOU', start_time: 1400004256},
                                                {home_team_id: 1004, home_team_name: 'MIA', away_team_id: 1010, away_team_name: 'DAL', start_time: 1400004256},
                                                {home_team_id: 1005, home_team_name: 'SF', away_team_id: 1011, away_team_name: 'MIN', start_time: 1400004256},
                                                {home_team_id: 1006, home_team_name: 'PHX', away_team_id: 1012, away_team_name: 'WAS', start_time: 1400004256}
                                        ],
                                        prizes: [
                                                {position: '1', prize: 10000},
                                                {position: '2-5', prize: 5000},
                                                {position: '6-10', prize: 1000},
                                                {position: '10-20', prize: 500},
                                                {position: '20-30', prize: 200}
                                        ]
                                },
                                {
                                        id: 1005,
                                        type: "Promo",
                                        sport: "NFL",
                                        multi_entry: false,
                                        guaranteed: true,
                                        current_entries: 34,
                                        size: 100,
                                        entry_pool: 4000,
                                        entry_fee: 10,
                                        grouping: 'all',
                                        salary_cap: 5000000,
                                        start_time: 1400003661,
                                        entries: [
                                                {user_id: 1001, username: 'jason0123'},
                                                {user_id: 1002, username: 'george'},
                                                {user_id: 1003, username: 'frank_355'},
                                                {user_id: 1004, username: 'jake'},
                                                {user_id: 1005, username: 'chris87'}
                                        ],
                                        games: [
                                                {home_team_id: 1001, home_team_name: 'ATL', away_team_id: 1007, away_team_name: 'CHI', start_time: 1400004256},
                                                {home_team_id: 1002, home_team_name: 'LA', away_team_id: 1008, away_team_name: 'STL', start_time: 1400004256},
                                                {home_team_id: 1003, home_team_name: 'PHI', away_team_id: 1009, away_team_name: 'HOU', start_time: 1400004256},
                                                {home_team_id: 1004, home_team_name: 'MIA', away_team_id: 1010, away_team_name: 'DAL', start_time: 1400004256},
                                                {home_team_id: 1005, home_team_name: 'SF', away_team_id: 1011, away_team_name: 'MIN', start_time: 1400004256},
                                                {home_team_id: 1006, home_team_name: 'PHX', away_team_id: 1012, away_team_name: 'WAS', start_time: 1400004256}
                                        ],
                                        prizes: [
                                                {position: '1', prize: 10000},
                                                {position: '2-5', prize: 5000},
                                                {position: '6-10', prize: 1000},
                                                {position: '10-20', prize: 500},
                                                {position: '20-30', prize: 200}
                                        ]
                                },
                                {
                                        id: 1006,
                                        type: "H2H",
                                        sport: "NFL",
                                        multi_entry: false,
                                        guaranteed: false,
                                        current_entries: 1,
                                        size: 2,
                                        entry_pool: 100,
                                        entry_fee: 50,
                                        grouping: 'early',
                                        salary_cap: 5000000,
                                        start_time: 1399920058,
                                        entries: [
                                                {user_id: 1001, username: 'jason0123'},
                                                {user_id: 1002, username: 'george'},
                                                {user_id: 1003, username: 'frank_355'},
                                                {user_id: 1004, username: 'jake'},
                                                {user_id: 1005, username: 'chris87'}
                                        ],
                                        games: [
                                                {home_team_id: 1001, home_team_name: 'ATL', away_team_id: 1007, away_team_name: 'CHI', start_time: 1400004256},
                                                {home_team_id: 1002, home_team_name: 'LA', away_team_id: 1008, away_team_name: 'STL', start_time: 1400004256},
                                                {home_team_id: 1003, home_team_name: 'PHI', away_team_id: 1009, away_team_name: 'HOU', start_time: 1400004256},
                                                {home_team_id: 1004, home_team_name: 'MIA', away_team_id: 1010, away_team_name: 'DAL', start_time: 1400004256},
                                                {home_team_id: 1005, home_team_name: 'SF', away_team_id: 1011, away_team_name: 'MIN', start_time: 1400004256},
                                                {home_team_id: 1006, home_team_name: 'PHX', away_team_id: 1012, away_team_name: 'WAS', start_time: 1400004256}
                                        ],
                                        prizes: [
                                                {position: '1', prize: 10000},
                                                {position: '2-5', prize: 5000},
                                                {position: '6-10', prize: 1000},
                                                {position: '10-20', prize: 500},
                                                {position: '20-30', prize: 200}
                                        ]
                                }
                        ]
                };
        };

        ruckus.models.contestid.prototype.staticDataQuickPlay = function () {
                // NOTE !!! - scoring and lineups could easily be their own APIs.  Added them here only since its all going to be needed anyway and might as well make and manage one call vs several (discuss)
                return {
                        timestamp: 1399920058,
                        scoring: {
                                nfl: {
                                        rushing_yard: .2,
                                        passing_yard: .1,
                                        receiving_yard: .1,
                                        rushing_td: 6,
                                        passing_td: 4,
                                        receiving_td: 6,
                                        extra_point: 1,
                                        field_goal: 3,
                                        field_goal_50: 4
                                }
                        },
                        lineups: [
                                {
                                        id: 1001,
                                        name: 'Halfback Focus',
                                        roster: [
                                                {position: 'QB', name: 'M. Stafford'},
                                                {position: 'RB', name: 'M. Lynch'},
                                                {position: 'RB', name: 'A. Foster'},
                                                {position: 'WR', name: 'D. Bryant'},
                                                {position: 'WR', name: 'T. Williams'},
                                                {position: 'WR', name: 'C. Beasley'},
                                                {position: 'TE', name: 'J. Witten'},
                                                {position: 'DEF', name: 'CHI'},
                                                {position: 'K', name: 'D. Bailey'},
                                        ]
                                },
                                {
                                        id: 1001,
                                        name: 'QB Focus',
                                        roster: [
                                                {position: 'QB', name: 'P. Manning'},
                                                {position: 'RB', name: 'M. Forte'},
                                                {position: 'RB', name: 'M. Ford'},
                                                {position: 'WR', name: 'B. Marshall'},
                                                {position: 'WR', name: 'A. Jeffery'},
                                                {position: 'WR', name: 'C. Williams'},
                                                {position: 'TE', name: 'M. Bennett'},
                                                {position: 'DEF', name: 'BAL'},
                                                {position: 'K', name: 'R. Gould'},
                                        ]
                                }
                        ],
                        contest: {
                                id: 1001,
                                type: "Guaranteed",
                                sport: "NFL",
                                multi_entry: false,
                                guaranteed: true,
                                current_entries: 34,
                                size: 100,
                                entry_pool: 4000,
                                entry_fee: 10,
                                grouping: 'early',
                                salary_cap: 50000,
                                start_time: 1400004256,
                                entries: [
                                        {user_id: 1001, username: 'jason0123'},
                                        {user_id: 1002, username: 'george'},
                                        {user_id: 1003, username: 'frank_355'},
                                        {user_id: 1004, username: 'jake'},
                                        {user_id: 1005, username: 'chris87'}
                                ],
                                games: [
                                        {home_team_id: 1001, home_team_name: 'ATL', away_team_id: 1007, away_team_name: 'CHI', start_time: 1400004256},
                                        {home_team_id: 1002, home_team_name: 'LA', away_team_id: 1008, away_team_name: 'STL', start_time: 1400004256},
                                        {home_team_id: 1003, home_team_name: 'PHI', away_team_id: 1009, away_team_name: 'HOU', start_time: 1400004256},
                                        {home_team_id: 1004, home_team_name: 'MIA', away_team_id: 1010, away_team_name: 'DAL', start_time: 1400004256},
                                        {home_team_id: 1005, home_team_name: 'SF', away_team_id: 1011, away_team_name: 'MIN', start_time: 1400004256},
                                        {home_team_id: 1006, home_team_name: 'PHX', away_team_id: 1012, away_team_name: 'WAS', start_time: 1400004256}
                                ],
                                prizes: [
                                        {position: '1', prize: 10000},
                                        {position: '2-5', prize: 5000},
                                        {position: '6-10', prize: 1000},
                                        {position: '10-20', prize: 500},
                                        {position: '20-30', prize: 200}
                                ]
                        }
                };
        };

        return ruckus.models.contestid;
});
