define([
        "rg_model_base",
        "rg_pubsub"
], function (Base) {
        // SETUP
        ruckus.models.contest = function (parameters) {
                var _this = this;
                Base.call(_this);
                _this.parameters = parameters;
                _.bindAll(_this,
                        "pushAdd",
                        "pushAll",
                        "pushRemove",
                        "pushUpdate");
        };
        ruckus.models.contest.prototype = Object.create(Base.prototype);

        // FETCH TRIGGER
        ruckus.models.contest.prototype.fetch = function () {
                var _this = this;
                _this.registerSocketSubscriptions();
                _this.open();
        };

        // CHANNEL
        ruckus.models.contest.prototype.open = function() {
                this.log({type: 'ruckus.models.contest.prototype.open', data: undefined, msg: "DATA REPO > OPEN DATA CHANNEL"});
                this.openChannel(ruckus.pubsub.channels.contests);
        };
        ruckus.models.contest.prototype.close = function() {
                /* Currently, we can't find any functionality to explicitly close a channel.  When functionality is
                 * found, add it to this function to ensure the channel is cleaned up. */
                this.log({type: 'ruckus.models.contest.prototype.close', data: undefined, msg: "CLOSE DATA CHANNEL"});
        };

        // RESPONDERS
        ruckus.models.contest.prototype.pushAll = function (data) {
                var _this = this;
                _this.log({type: 'ruckus.models.contest.prototype.pushAll', data: data, msg: 'DATA REPO > ALL CONTESTS'});
                // FIXME:  this should be removed.  Viewmodel should be determining context for the data.
                _this.modelData = { contests: data };
                _this.msgBus.publish(ruckus.pubsub.subscriptions.models.data.contests.all, { data: data });
        };
        ruckus.models.contest.prototype.pushUpdate = function(data) {
                var _this = this;
                _this.log({type: 'ruckus.models.contest.prototype.pushUpdate', data: data, msg: 'DATA REPO > UPDATE CONTEST'});
                $.each(_this.modelData.contests, function (key, value) {
                        if (value.id == data.id) {
                                value.currentEntries = data.currentEntries;
                        }
                });
                _this.msgBus.publish(ruckus.pubsub.subscriptions.models.data.contests.update, data);
        };
        ruckus.models.contest.prototype.pushAdd = function(data) {
                var _this = this;
                _this.log({type: 'ruckus.models.contest.prototype.pushAdd', data: data, msg: 'DATA REPO > ADD CONTEST'});
                _this.msgBus.publish(ruckus.pubsub.subscriptions.models.data.contests.add, data);
        };
        ruckus.models.contest.prototype.pushRemove = function (data) {
                var _this = this;
                _this.log({type: 'ruckus.models.contest.prototype.pushRemove', data: data, msg: 'DATA REPO > REMOVE CONTEST'});
                _this.msgBus.publish(ruckus.pubsub.subscriptions.models.data.contests.remove, data);
        };

        // LISTENERS
        ruckus.models.contest.prototype.registerSocketSubscriptions = function () {
                var _this = this;
                _this.__addsubscription(ruckus.pubsub.subscriptions.sockets.contests.all, _this.pushAll);
                _this.__addsubscription(ruckus.pubsub.subscriptions.sockets.contests.add, _this.pushAdd);
                _this.__addsubscription(ruckus.pubsub.subscriptions.sockets.contests.update, _this.pushUpdate);
                _this.__addsubscription(ruckus.pubsub.subscriptions.sockets.contests.remove, _this.pushRemove);
        };

        // MOCK DATA FOR TESTING
	ruckus.models.contest.prototype.intervalData = function() {
		var _this = this;
		
		var runIntervals = function(){
			// ADD INTERVAL
			var runAdd = function(){
				_this.consolelog('INTERVAL ADD');
				var data = {"entryFee":5000,"league":"MLB","allowedEntries":1,"contestType":{"name":"Standard","abbr":"NRM"},"payout":[{"leadingPosition":1,"trailingPosition":1,"payoutAmount":17745},{"leadingPosition":2,"trailingPosition":2,"payoutAmount":9555}],"currentEntries":0,"guaranteed":false,"startTime":1405191900000,"id":"HxFVlsCY","grouping":7,"salaryCap":5000000,"capacity":6};
				_this.consolelog(data);
				_this.msgBus.publish("model.contest.add", data);
			};
			setTimeout(runAdd, 5000);

			// REMOVE INTERVAL
			var runRemove = function(){
				_this.consolelog('INTERVAL REMOVE');
				var data = {id:"Ypp9pIjI"};
				_this.consolelog(data);
				_this.msgBus.publish("model.contest.remove", data);
			};
			setTimeout(runRemove, 10000); 
		};

		setInterval(runIntervals,20000);
	};
        ruckus.models.contest.prototype.staticData = function () {
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
                                                {position: 'K', name: 'D. Bailey'}
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
                                                {position: 'K', name: 'R. Gould'}
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
        ruckus.models.contest.prototype.staticDataQuickPlay = function () {
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
                                                {position: 'K', name: 'D. Bailey'}
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
                                                {position: 'K', name: 'R. Gould'}
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

        return ruckus.models.contest;
});
