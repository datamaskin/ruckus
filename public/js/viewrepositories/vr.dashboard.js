define([
        'rg_viewrepo_base',
        'rg_pubsub',
        'assets/js/models/contestliveoverview.js',
        'assets/js/models/timestamp.js',
        "assets/js/testdata/dashboard.contest.js"
], function (Base, pubsubdef, overviewmodel, timestampModel, contestMockDataData) {
        // SETUP
        ruckus.views.repositories.dashboard = function (parameters) {
                var _this = this;
                Base.call(_this);
                _this.parameters = parameters;
                _this.pageloadcomplete = false;
		if (parameters.mockdata) {
                        _this.mockdata = parameters.mockdata;
                } else {
                        _this.mockdata = false;
                }
                if(parameters.interval) {
                        _this.interval = parameters.interval;
                }
                else {
                        _this.interval = 5000;
                }
                _this.local_data = {
                        contests: [],
                        servertime: null,
                        servertime_updated: null,
                        scoring: null
                };
                _.bindAll(_this,
                        "fetch",
                        "fetchServerTime",

                        "putAllContests",
                        "getContest",
                        "processContests",
                        "processContest",
                        "putContest",

                        "pushServerTimeUpdate",
                        "pushTestDataToTopic",
                        "pushTestDataToDelegate");

                // Initialize data repositories
                _this.dataRepos.contestLiveOverviewDataRepo = new ruckus.models.contestliveoverview(parameters);
                _this.dataRepos.timeStampRepo = new ruckus.models.timestamp(parameters);
        };
        ruckus.views.repositories.dashboard.prototype = Object.create(Base.prototype);

        // FETCH TRIGGERS
        ruckus.views.repositories.dashboard.prototype.fetch = function () {
                var _this = this;
                _this.log({type: 'general', data: undefined, msg: 'DASHBOARD VIEW REPO > FETCH'});

                // Initialise listeners
                _this.registerDataSubscriptions();

                // Load initial data sets
                _this.dataRepos.timeStampRepo.fetch();
                _this.dataRepos.contestLiveOverviewDataRepo.fetch();
        };
        ruckus.views.repositories.dashboard.prototype.fetchServerTime = function () {
                var _this = this;
                _this.log({type: 'general', data: undefined, msg: 'DASHBOARD VIEW REPO > FETCH SERVER TIME'});
                _this.dataRepos.timeStampRepo.fetch();
        };

        // SUBSCRIPTION REGISTRY
        ruckus.views.repositories.dashboard.prototype.registerDataSubscriptions = function () {
                this.__addsubscription(ruckus.pubsub.subscriptions.models.data.contestliveoverview.all, this.putAllContests);
                this.__addsubscription(ruckus.pubsub.subscriptions.models.data.contestliveoverview.update, this.putContest);
                this.__addsubscription(ruckus.pubsub.subscriptions.models.data.timestamp.servertime, this.pushServerTimeUpdate);
        };

        // CONTEST - Entry Updates
        ruckus.views.repositories.dashboard.prototype.putAllContests = function (data) {
                var _this = this;
                _this.processContests(data.contests.contests);
        };
        ruckus.views.repositories.dashboard.prototype.putContest = function (data) {
                this.processContest(data.data);
        };
        ruckus.views.repositories.dashboard.prototype.getContest = function (id) {
                var _this = this;
                var contest = null;

                for (var i = 0; i < _this.local_data.contests.length; i++) {
                        var e = _this.local_data.contests[i];
                        if (e.id === id) {
                                contest = e;
                        }
                }
                if (contest) _this.processContest(contest);  // FIXME:  Should we return a value if no record exists?
        };
        ruckus.views.repositories.dashboard.prototype.processContests = function (contests) {
                var _this = this;
                $.each(contests, function (key, newrecord) {
                        var exists = false;
                        $.each(_this.local_data.contests, function(k, existingrecord){
                                if(newrecord.contestId === existingrecord.contestId) exists = true;
                        });
                        if(exists){
                                _this.processContest(newrecord);
                        } else {
                                _this.local_data.contests.push(_this.formatContest(newrecord));
                                if (_this.mockdata) {
//                                        var interval = (Math.floor(Math.random() * 20) * 100) + 1000;
                                        _this.pushTestDataToTopic(ruckus.pubsub.subscriptions.models.data.contestliveoverview.update, new contestMockDataData(newrecord), _this.interval);
                                }
                        }
                });

                this.msgBus.publish(ruckus.pubsub.subscriptions.view.dashboard.contest.all, _this.local_data.contests);
        };
        ruckus.views.repositories.dashboard.prototype.processContest = function (contestupdate) {
                var _this = this;
                _this.log({type: 'method', level: 4, data: contestupdate, msg: 'DASHBOARD VIEW REPO > processContest'});

                var oldcontest;
                var contest;
                var isupdate = false;
                for (var i = 0; i < _this.local_data.contests.length; i++) {
                        var e = _this.local_data.contests[i];

                        if (contestupdate.contestId === e.contestId) {
                                oldcontest = JSON.parse(JSON.stringify(e));
                                for (var key in contestupdate) {
                                        if (contestupdate.hasOwnProperty(key)) {
                                                e[key] = contestupdate[key];
                                        }
                                }
                                contest = _this.formatContest(e);
                                isupdate = true;
                        }
                }
                if (!isupdate) {
                        contest = _this.formatContest(contestupdate);
                        _this.local_data.contests.push(contest);
                }

                if (oldcontest) {
                        // We don't want to send out duplicate records.
                        if (oldcontest === contest) {
                                _this.log({type: 'datavalidation', level: 2, data: contest, msg: 'DASHBOARD VIEW REPO > Duplicate record received'});
                                return;
                        }
                        if (contest.fpp !== oldcontest.fpp) {
                                contest.fpp_change = contest.fpp - oldcontest.fpp;
                                // Javascript has a floating point precision issues with subtracting decimals hence the rounding.
                                contest.fpp_change = Math.round(contest.fpp_change * 100) / 100;
                                if(contest.fpp_change > 0){
                                        contest.fpp_direction = "up";
                                }
                                if(contest.fpp_change < 0){
                                        contest.fpp_direction = "down";
                                }
                                _this.log({type: 'general', level: 4, data: contest, msg: 'DASHBOARD VIEW REPO > Current Points'});
                                _this.msgBus.publish(ruckus.pubsub.subscriptions.view.dashboard.contest.active.currentpoints, contest);
                        } else {
                                if(contest.fpp_change) delete contest.fpp_change;
                                if(contest.fpp_direction) delete contest.fpp_direction;
                        }
                        if (contest.payout !== oldcontest.payout) {
                                contest.payout_change = contest.payout - oldcontest.payout;
                                if(contest.payout_change > 0){
                                        contest.payout_direction = "up";
                                }
                                if(contest.payout_change < 0){
                                        contest.payout_direction = "down";
                                }
                                _this.log({type: 'general', level: 4, data: contest, msg: 'DASHBOARD VIEW REPO > Current Payout'});
                                _this.msgBus.publish(ruckus.pubsub.subscriptions.view.dashboard.contest.active.currentpayout, contest);
                        } else {
                                if(contest.payout_change) delete contest.payout_change;
                                if(contest.payout_direction) delete contest.payout_direction;
                        }
                        if (contest.position != oldcontest.position) {
                                contest.position_change = contest.position - oldcontest.position;
                                if(contest.position_change < 0){
                                        contest.position_direction = "up";
                                }
                                if(contest.position_change > 0){
                                        contest.position_direction = "down";
                                }
                                _this.log({type: 'general', level: 4, data: contest, msg: 'DASHBOARD VIEW REPO > Current Position'});
                                _this.msgBus.publish(ruckus.pubsub.subscriptions.view.dashboard.contest.active.currentposition, contest);
                        } else {
                                if(contest.position_change) delete contest.position_change;
                                if(contest.position_direction) delete contest.position_direction;
                        }
                        if (contest.projectedPayout !== oldcontest.projectedPayout) {
                                contest.projectedPayout_change = contest.projectedPayout - oldcontest.projectedPayout;
                                if(contest.projectedPayout_change > 0){
                                        contest.projectedPayout_direction = "up";
                                }
                                if(contest.projectedPayout_change < 0){
                                        contest.projectedPayout_direction = "down";
                                }
                                _this.log({type: 'general', level: 4, data: contest, msg: 'DASHBOARD VIEW REPO > Projected Payout'});
                                _this.msgBus.publish(ruckus.pubsub.subscriptions.view.dashboard.contest.active.projectedpayout, contest);
                        } else {
                                if(contest.projectedPayout_change) delete contest.projectedPayout_change;
                                if(contest.projectedPayout_direction) delete contest.projectedPayout_direction;
                        }
                        if (contest.unitsRemaining !== oldcontest.unitsRemaining) {
                                contest.unitsRemaining_change = contest.unitsRemaining - oldcontest.unitsRemaining;
                                if(contest.unitsRemaining_change > 0){
                                        contest.unitsRemaining_direction = "up";
                                }
                                if(contest.unitsRemaining_change < 0){
                                        contest.unitsRemaining_direction = "down";
                                }
                                _this.log({type: 'general', level: 4, data: contest, msg: 'DASHBOARD VIEW REPO > pushContestEntryUnitsRemaining'});
                                _this.msgBus.publish(ruckus.pubsub.subscriptions.view.dashboard.contest.active.currentunitsremaining, contest);
                        }
                } else {
                        _this.log({type: 'general', level: 4, data: contest, msg: 'DASHBOARD VIEW REPO > pushActiveCurrentPayout'});
                        _this.msgBus.publish(ruckus.pubsub.subscriptions.view.dashboard.contest.active.currentpayout, contest);

                        _this.log({type: 'general', level: 4, data: contest, msg: 'DASHBOARD VIEW REPO > pushActiveProjectedPayout'});
                        _this.msgBus.publish(ruckus.pubsub.subscriptions.view.dashboard.contest.active.projectedpayout, contest);

                        _this.log({type: 'general', data: contest, msg: 'DASHBOARD VIEW REPO > pushContestEntryUnitsRemaining'});
                        _this.msgBus.publish(ruckus.pubsub.subscriptions.view.dashboard.contest.active.currentunitsremaining, contest);

                        _this.log({type: 'general', data: contest, msg: 'DASHBOARD VIEW REPO > pushContestEntryFppUpdate'});
                        _this.msgBus.publish(ruckus.pubsub.subscriptions.view.dashboard.contest.active.currentpoints, contest);
                }
        };
        ruckus.views.repositories.dashboard.prototype.formatContest = function (contestupdate) {
                var _this = this;
                //handle fpp change
                contestupdate.formattedPayout = _this.formatMoney(contestupdate.payout);
                contestupdate.formattedProjectedPayout = _this.formatMoney(contestupdate.projectedPayout);
                contestupdate.formattedPosition = _this.formatPlace(contestupdate.position);
                contestupdate.formattedBuyinAmount = _this.formatMoney(contestupdate.entryFee);
                contestupdate.timePercentage = _this.formatTimePercentageTeam(contestupdate.league, contestupdate.unitsRemaining);
                var contestname = _this.formatContestName(contestupdate, '2line');
                contestupdate.line1 = contestname.line1;
                contestupdate.line2 = contestname.line2;
                return contestupdate;
        };

        // RESPONDERS - Server Time
        ruckus.views.repositories.dashboard.prototype.pushServerTimeUpdate = function (data) {
                var _this = this;
                _this.log({type: 'general', data: data, msg: 'DASHBOARD VIEW REPO > pushServerTimeUpdate'});

                _this.local_data.servertime = data;
                _this.local_data.servertime_updated = new Date();
//                if (_this.pageloadcomplete) {
                _this.msgBus.publish(ruckus.pubsub.subscriptions.view.dashboard.servertime, data);
//                } else {
//                        _this.pushPageLoadIfReady();
//                }
        };

        // RESPONDERS - Page Load
        ruckus.views.repositories.dashboard.prototype.pushPageLoadIfReady = function () {
                var _this = this;
                _this.log({type: 'general', data: {}, msg: 'DASHBOARD VIEW REPO > pushPageLoadIfReady'});

                if (_this.local_data.contests !== null && _this.local_data.scoring !== null && _this.local_data.servertime !== null) {

                        var contests = [];
                        $.each(_this.local_data.contests, function (key, val) {
                                contests.push(_this.prepareViewData(val));
                        });
                        _this.local_data.contests = contests;

                        _this.msgBus.publish(ruckus.pubsub.subscriptions.view.dashboard.contest.pageload, _this.local_data);
                        this.pageloadcomplete = true;
                }
        };

        // TEST DATA
        ruckus.views.repositories.dashboard.prototype.pushTestDataToTopic = function (topic, data, interval) {
                var _this = this;
                var int = setInterval(function () {
                        if (data.length === 0) {
                                clearInterval(int);
                        } else {
                                _this.log({type: 'general', data: data[0], msg: 'DASHBOARD VIEW REPO > INTERVAL DATA'});
                                _this.msgBus.publish(topic, { data: data[0] });
                                data.shift();
                        }
                }, interval);

                _this.intervals.push(int)
        };
        ruckus.views.repositories.dashboard.prototype.pushTestDataToDelegate = function (delegate, data, interval) {
                var _this = this;
                var int = setInterval(function () {
                        if (data.length === 0) {
                                clearInterval(int);
                        } else {
                                _this.log({type: 'general', data: data[0], msg: 'DASHBOARD VIEW REPO > INTERVAL DATA'});
                                delegate(data[0]);
                                data.shift();
                        }
                }, interval);
        };

        // TERMINATION
        ruckus.views.repositories.dashboard.prototype.unload = function () {
                delete this.local_data;
                this.__destroy();
        };

        return ruckus.views.repositories.dashboard;
});
