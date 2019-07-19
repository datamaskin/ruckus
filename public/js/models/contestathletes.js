// Author: Scott Gay
define([
//	"assets/js/base.js",
        "assets/js/models/basews.js",
        "assets/js/libraries/jquery.min.js",
        "assets/js/libraries/jquery.atmosphere.js"
], function (Base) {
        // PARAMETERS

        ruckus.models.contestathletes = function (parameters) {
                Base.call(this);
                this.init = function () {
                        this.parameters = parameters;
                        // set parameter defaults
                };
                this.init();
        };

        ruckus.models.contestathletes.prototype = Object.create(Base.prototype);

        ruckus.models.contestathletes.prototype.fetch = function (fParams) {
                var _this = this;

                // RESTFUL
                var request = {
                        'type': 'GET',
                        'url': '/contestathletes',
                        'data': 'contestId=' + fParams.id,
                        'callback': function (data) {
                                _this.modelData = {athletes: JSON.parse(data)};

                                // create stat columns
                                $.each(_this.modelData.athletes, function (k, v) {
                                        if (fParams.league == 'NFL') {
                                                v.stat1 = v.matchup; // v.awayTeam + '@' + v.homeTeam;
                                                v.stat2 = v.ffpg;
                                                v.stat3 = 'NA';
                                                v.stat4 = v.rank[0];
                                                v.stat5 = _this.formatMoney(v.dollarsPerFantasyPoint);
                                                v.stat6 = v.injuryStatus.split('|')[0];
                                        } else if (fParams.league == 'MLB') {
                                                v.stat1 = v.awayTeam + '@' + v.homeTeam;
                                                v.stat2 = v.ffpg;
                                                v.stat3 = v.number;
                                                v.stat4 = v.salary;
                                                v.stat5 = v.id;
                                                v.stat6 = v.eventId;
                                        }
                                });

                                // stats name/desc structure
                                if (fParams.league == 'NFL') {
                                        _this.modelData.stats = {
                                                stat1: {
                                                        name: 'OPP',
                                                        desc: 'Upcoming opponent.',
                                                        dir: 'desc'
                                                },
                                                stat2: {
                                                        name: 'PPG',
                                                        desc: 'Average fantasy points over the last 5 games.',
                                                        dir: 'asc'
                                                },
                                                stat3: {
                                                        name: 'EXP',
                                                        desc: 'Total buy-in amount of your locked and guaranteed contest entries that have this athlete rostered.',
                                                        dir: 'asc'
                                                },
                                                stat4: {
                                                        name: 'RANK',
                                                        desc: 'Rank of this athlete\'s average fantasy points per game at their position over the last 17 games.',
                                                        dir: 'asc'
                                                },
                                                stat5: {
                                                        name: 'VAL',
                                                        desc: 'Average salary dollar cost per fantasy point earned for the athlete over the last 5 games.',
                                                        dir: 'asc'
                                                },
                                                stat6: {
                                                        name: 'INJ',
                                                        desc: 'Injury status.',
                                                        dir: 'desc'
                                                }
                                        };
                                } else if (fParams.league == 'MLB') {
                                        _this.modelData.stats = {
                                                stat1: {
                                                        name: 'OPP',
                                                        desc: 'Opponent colored by fantasy points allowed to this position over last 25 games.',
                                                        dir: 'desc'
                                                },
                                                stat2: {
                                                        name: 'PPG',
                                                        desc: "Player's fantasy points per game over last 25 games.",
                                                        dir: 'asc'
                                                },
                                                stat3: {
                                                        name: 'PPGR',
                                                        desc: 'Rank of players fantasy points per game at their position over last 25 games.',
                                                        dir: 'asc'
                                                },
                                                stat4: {
                                                        name: 'EXP',
                                                        desc: 'Total buy-in amount of your locked and guaranteed contests that have this athlete rostered.',
                                                        dir: 'asc'
                                                },
                                                stat5: {
                                                        name: 'wOBA',
                                                        desc: 'Weighted On-Base Average combines all hitting metrics to show offensive value.',
                                                        dir: 'asc'
                                                },
                                                stat6: {
                                                        name: 'PP$',
                                                        desc: 'Avg. fantasy points per salary dollar of this athlete over the last 25 games.',
                                                        dir: 'asc'
                                                }
                                        };
                                }

                                _this.log({type: 'api', data: _this.modelData, msg: "CONTEST ATHLETES DATA MODEL"});
//				_this.modelData = _this.staticData();
                                _this.msgBus.publish("model.contestathletes.retrieve", {data: _this.modelData});
                        },
                        'failcallback': function (data) {
                                _this.log({type: 'api', data: data, msg: 'CONTEST ATHLETES AJAX CALL FAILED'});
                        }
                };
                this.sendRequest(request);

        };

        ruckus.models.contestathletes.prototype.staticData = function () {
                return {
                        athletes: [
                                {
                                        id: 1001,
                                        firstName: 'Quarterback',
                                        lastName: 'One',
                                        position: 'QB',
                                        number: '14',
                                        image: 'http://a.espncdn.com/combiner/i?img=/i/headshots/nfl/players/full/5209.png',
                                        team: 'ATL',
                                        homeTeam: 'ATL',
                                        awayTeam: 'SAN',
                                        ffpg: 12.2,
                                        salary: 670000
                                },
                                {
                                        id: 1002,
                                        firstName: 'Quarterback',
                                        lastName: 'Two',
                                        position: 'QB',
                                        number: '8',
                                        image: 'http://a.espncdn.com/combiner/i?img=/i/headshots/nfl/players/full/13215.png',
                                        team: 'DAL',
                                        homeTeam: 'ATL',
                                        awayTeam: 'DAL',
                                        ffpg: 14.2,
                                        salary: 870000
                                },
                                {
                                        id: 1003,
                                        firstName: 'Runningback',
                                        lastName: 'One',
                                        position: 'RB',
                                        number: '31',
                                        image: 'http://a.espncdn.com/combiner/i?img=/i/headshots/nfl/players/full/5209.png',
                                        team: 'DAL',
                                        homeTeam: 'ATL',
                                        awayTeam: 'DAL',
                                        ffpg: 22.2,
                                        salary: 900000
                                },
                                {
                                        id: 1004,
                                        firstName: 'Runningback',
                                        lastName: 'Two',
                                        position: 'RB',
                                        number: '34',
                                        image: 'http://a.espncdn.com/combiner/i?img=/i/headshots/nfl/players/full/13215.png',
                                        team: 'SAN',
                                        homeTeam: 'ATL',
                                        awayTeam: 'SAN',
                                        ffpg: 12.6,
                                        salary: 650000
                                },
                                {
                                        id: 1019,
                                        firstName: 'Runningback',
                                        lastName: 'Three',
                                        position: 'RB',
                                        number: '25',
                                        image: 'http://a.espncdn.com/combiner/i?img=/i/headshots/nfl/players/full/5209.png',
                                        team: 'MIA',
                                        homeTeam: 'MIA',
                                        awayTeam: 'SAN',
                                        ffpg: 16.6,
                                        salary: 780000
                                },
                                {
                                        id: 1020,
                                        firstName: 'Runningback',
                                        lastName: 'Four',
                                        position: 'RB',
                                        number: '24',
                                        image: 'http://a.espncdn.com/combiner/i?img=/i/headshots/nfl/players/full/13215.png',
                                        team: 'DEN',
                                        homeTeam: 'MIA',
                                        awayTeam: 'DEN',
                                        ffpg: 15.6,
                                        salary: 750000
                                },
                                {
                                        id: 1005,
                                        firstName: 'Wide Receiver',
                                        lastName: 'One',
                                        position: 'WR',
                                        number: '89',
                                        image: 'http://a.espncdn.com/combiner/i?img=/i/headshots/nfl/players/full/5209.png',
                                        team: 'CHI',
                                        homeTeam: 'CHI',
                                        awayTeam: 'SAN',
                                        ffpg: 11.2,
                                        salary: 600000
                                },
                                {
                                        id: 1006,
                                        firstName: 'Wide Receiver',
                                        lastName: 'Two',
                                        position: 'WR',
                                        number: '81',
                                        image: 'http://a.espncdn.com/combiner/i?img=/i/headshots/nfl/players/full/13215.png',
                                        team: 'BAL',
                                        homeTeam: 'BAL',
                                        awayTeam: 'SAN',
                                        ffpg: 17.2,
                                        salary: 770000
                                },
                                {
                                        id: 1021,
                                        firstName: 'Wide Receiver',
                                        lastName: 'Three',
                                        position: 'WR',
                                        number: '85',
                                        image: 'http://a.espncdn.com/combiner/i?img=/i/headshots/nfl/players/full/5209.png',
                                        team: 'CHI',
                                        homeTeam: 'CHI',
                                        awayTeam: 'SAN',
                                        ffpg: 13.2,
                                        salary: 610000
                                },
                                {
                                        id: 1022,
                                        firstName: 'Wide Receiver',
                                        lastName: 'Four',
                                        position: 'WR',
                                        number: '84',
                                        image: 'http://a.espncdn.com/combiner/i?img=/i/headshots/nfl/players/full/13215.png',
                                        team: 'SAN',
                                        homeTeam: 'CHI',
                                        awayTeam: 'SAN',
                                        ffpg: 11.2,
                                        salary: 600000
                                },
                                {
                                        id: 1007,
                                        firstName: 'Tight End',
                                        lastName: ' One',
                                        position: 'TE',
                                        number: '84',
                                        image: 'http://a.espncdn.com/combiner/i?img=/i/headshots/nfl/players/full/5209.png',
                                        team: 'PIT',
                                        homeTeam: 'ATL',
                                        awayTeam: 'PIT',
                                        ffpg: 13.2,
                                        salary: 640000
                                },
                                {
                                        id: 1008,
                                        firstName: 'Tight End',
                                        lastName: 'Two',
                                        position: 'TE',
                                        number: '83',
                                        image: 'http://a.espncdn.com/combiner/i?img=/i/headshots/nfl/players/full/13215.png',
                                        team: 'MIA',
                                        homeTeam: 'ATL',
                                        awayTeam: 'MIA',
                                        ffpg: 13.2,
                                        salary: 660000
                                },
                                {
                                        id: 1028,
                                        firstName: 'Tight End',
                                        lastName: 'Three',
                                        position: 'TE',
                                        number: '81',
                                        image: 'http://a.espncdn.com/combiner/i?img=/i/headshots/nfl/players/full/13215.png',
                                        team: 'MIA',
                                        homeTeam: 'ATL',
                                        awayTeam: 'MIA',
                                        ffpg: 11.2,
                                        salary: 630000
                                },

                                {
                                        id: 1009,
                                        firstName: 'New York',
                                        lastName: 'Giants',
                                        position: 'DEF',
                                        image: 'http://a.espncdn.com/combiner/i?img=/i/headshots/nfl/players/full/5209.png',
                                        team: 'NYG',
                                        homeTeam: 'ATL',
                                        awayTeam: 'NYG',
                                        ffpg: 15.2,
                                        salary: 720000
                                },
                                {
                                        id: 1010,
                                        firstName: 'Cleveland',
                                        lastName: 'Browns',
                                        position: 'DEF',
                                        image: 'http://a.espncdn.com/combiner/i?img=/i/headshots/nfl/players/full/13215.png',
                                        team: 'CLE',
                                        homeTeam: 'ATL',
                                        awayTeam: 'CLE',
                                        ffpg: 15.2,
                                        salary: 700000
                                },
                                {
                                        id: 1011,
                                        firstName: 'Kicker',
                                        lastName: 'One',
                                        position: 'K',
                                        number: '12',
                                        image: 'http://a.espncdn.com/combiner/i?img=/i/headshots/nfl/players/full/5209.png',
                                        team: 'OAK',
                                        homeTeam: 'ATL',
                                        awayTeam: 'SAN',
                                        ffpg: 8.2,
                                        salary: 550000
                                },
                                {
                                        id: 1012,
                                        firstName: 'Kicker',
                                        lastName: 'Two',
                                        position: 'K',
                                        number: '7',
                                        image: 'http://a.espncdn.com/combiner/i?img=/i/headshots/nfl/players/full/13215.png',
                                        team: 'MIN',
                                        homeTeam: 'ATL',
                                        awayTeam: 'MIN',
                                        ffpg: 4.2,
                                        salary: 420000
                                }
                        ]
                };
        };

        ruckus.models.contestathletes.prototype.staticDataSend = function () {
                return {
                        contest_id: 1001,
                        roster: [
                                {'QB': 1001},
                                {'RB1': 1002},
                                {'RB2': 1003},
                                {'WR1': 1004},
                                {'WR2': 1005},
                                {'WR3': 1006},
                                {'TE': 1007},
                                {'K': 1008},
                                {'DEF': 1009}
                        ]
                };
        };

        return ruckus.models.contestathletes;
});
