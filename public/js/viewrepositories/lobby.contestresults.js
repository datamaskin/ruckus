// Author: Scott Gay
define([
        "assets/js/viewrepositories/base.js",
        "rg_pubsub",
        "assets/js/models/contest.js",
        "assets/js/models/contestscoring.js",
        "assets/js/models/timestamp.js"
], function (Base) {
        // SETUP
        ruckus.views.repositories.lobby_contestresults = function (parameters) {
                var _this = this;

                Base.call(_this);
                _this.parameters = parameters;
                _this.listeners = [];
                _this.dataRepos = [];
                _this.data = {
                        contests: null,
                        scoring: null,
                        servertime: null
                };
                _this.pageloadcomplete = false;
                _.bindAll(_this,
                        "fetch",
                        "fetchServerTime",
                        "pushContestUpdate",
                        "pushContestAll",
                        "pushContestAdd",
                        "pushContestRemove",
                        "pushServerTimeUpdate",
                        "pushContestScoringUpdate",
                        "pushPageLoadIfReady");


                // Initialise listeners
                _this.dataRepos.timeStampRepo = new ruckus.models.timestamp(parameters);
                _this.dataRepos.contestScoringRepo = new ruckus.models.contestscoring(parameters);
                _this.dataRepos.contestDataRepo = new ruckus.models.contest(parameters);

        };
        ruckus.views.repositories.lobby_contestresults.prototype = Object.create(Base.prototype);

        // FETCH TRIGGERS
        ruckus.views.repositories.lobby_contestresults.prototype.fetch = function () {
                var _this = this;
                _this.log({type: 'general', data: undefined, msg: 'VIEW REPO > FETCH'});

                _this.registerDataSubscriptions();
                _this.dataRepos.timeStampRepo.fetch();
                _this.dataRepos.contestScoringRepo.fetch();
                _this.dataRepos.contestDataRepo.fetch();
        };
        ruckus.views.repositories.lobby_contestresults.prototype.fetchServerTime = function() {
                var _this = this;
                _this.dataRepos.timeStampRepo.fetch();
        };

        // LISTENERS - Contest
        ruckus.views.repositories.lobby_contestresults.prototype.registerDataSubscriptions = function () {
                var _this = this;
                _this.__addsubscription(ruckus.pubsub.subscriptions.models.data.contests.all, _this.pushContestAll);
                _this.__addsubscription(ruckus.pubsub.subscriptions.models.data.contests.add, _this.pushContestAdd);
                _this.__addsubscription(ruckus.pubsub.subscriptions.models.data.contests.update, _this.pushContestUpdate);
                _this.__addsubscription(ruckus.pubsub.subscriptions.models.data.contests.remove, _this.pushContestRemove);
                _this.__addsubscription(ruckus.pubsub.subscriptions.models.data.timestamp.servertime, _this.pushServerTimeUpdate);
                _this.__addsubscription(ruckus.pubsub.subscriptions.models.data.contestscoring.update, _this.pushContestScoringUpdate);
        };

        // RESPONDERS - Contest
        ruckus.views.repositories.lobby_contestresults.prototype.pushContestAll = function (data) {
                var _this = this;
                _this.log({type: 'general', data: data, msg: 'VIEW REPO > ALL CONTEST'});

                if(Array.isArray(data.data)){
                        _this.data.contests = data.data;
                } else {
                        // FIXME:  Determine why single records are coming through this channel and direct them through
                        // the appropriate event
                        _this.data.contests.push(data);
                }

                if(_this.pageloadcomplete){
                        var contests = [];
                        $.each(_this.data.contests, function (key, val) {
                                contests.push(_this.prepareViewData(val));
                        });
                        _this.data.contests = contests;
                } else {
                        _this.pushPageLoadIfReady();
                }
        };
        ruckus.views.repositories.lobby_contestresults.prototype.pushContestAdd = function (data) {
                var _this = this;
                _this.log({type: 'general', data: data, msg: 'VIEW REPO > ADD CONTEST'});

                var contest = _this.prepareViewData(data);
                _this.msgBus.publish(ruckus.pubsub.subscriptions.view.lobby.contestresults.contests.add, contest);
        };
        ruckus.views.repositories.lobby_contestresults.prototype.pushContestRemove = function (data) {
                var _this = this;
                _this.log({type: 'general', data: data, msg: 'VIEW REPO > REMOVE CONTEST'});

//                var contest = _this.prepareViewData(data);
                _this.msgBus.publish(ruckus.pubsub.subscriptions.view.lobby.contestresults.contests.remove, data);
        };
        ruckus.views.repositories.lobby_contestresults.prototype.pushContestUpdate = function (data) {
                var _this = this;
                _this.log({type: 'general', data: data, msg: 'VIEW REPO > CONTEST UPDATE'});
                _this.msgBus.publish(ruckus.pubsub.subscriptions.view.lobby.contestresults.contests.update, data);
        };
        // RESPONDERS - Server Time
        ruckus.views.repositories.lobby_contestresults.prototype.pushServerTimeUpdate = function (data) {
                var _this = this;
                _this.log({type: 'general', data: data, msg: 'VIEW REPO > UPDATE SERVER TIME'});

                _this.data.servertime = data;
                _this.data.servertime_updated = new Date();
                if (_this.pageloadcomplete) {
                        _this.msgBus.publish(ruckus.pubsub.subscriptions.view.lobby.contestresults.servertime, data);
                } else {
                        _this.pushPageLoadIfReady();
                }
        };
        // RESPONDERS - Contest Scoring
        ruckus.views.repositories.lobby_contestresults.prototype.pushContestScoringUpdate = function (data) {
                var _this = this;
                _this.log({type: 'general', data: data, msg: 'VIEW REPO > UPDATE SERVER TIME'});

                _this.data.scoring = data;
                if (_this.pageloadcomplete) {
                        _this.msgBus.publish(ruckus.pubsub.subscriptions.view.lobby.contestresults.contestscoring.update, data);
                } else {
                        _this.pushPageLoadIfReady();
                }
        };

        // RESPONDERS - Page Load
        ruckus.views.repositories.lobby_contestresults.prototype.pushPageLoadIfReady = function () {
                var _this = this;

                if(_this.data.contests !== null && _this.data.scoring !== null && _this.data.servertime !== null){

                        var contests = [];
                        $.each(_this.data.contests, function (key, val) {
                                contests.push(_this.prepareViewData(val));
                        });
                        _this.data.contests = contests;

                        _this.msgBus.publish(ruckus.pubsub.subscriptions.view.lobby.contestresults.pageload, _this.data);
                        this.pageloadcomplete = true;
                }
        };

        // DATA TRANSFORMATION
        ruckus.views.repositories.lobby_contestresults.prototype.prepareViewData = function (contest) {
                var _this = this;
                contest.line1 = _this.formatContestName(contest, '1linesimple').line1;
                contest.remainingTime = ruckus.modules.datetime.diff(_this.data.servertime, contest.startTime, 'seconds');
                contest.formattedstartTime = ruckus.modules.counters.contest.getContestTextCounter(contest.startTime, contest.remainingTime);
//		contest.formattedprizePool = _this.formatMoney(contest.payouts);
                contest.formattedentryFee = _this.formatMoney(contest.entryFee);
                contest.formattedprizePool = _this.formatMoney(contest.prizePool);
                contest.formattedEntries = contest.currentEntries.formatInteger();
                contest.formattedCapacity = contest.capacity.formatInteger();
                return contest;
        };

        // TERMINATION
        ruckus.views.repositories.lobby_contestresults.prototype.unload = function () {
                this.__destroy();
        };

        return ruckus.views.repositories.lobby_contestresults;
});
