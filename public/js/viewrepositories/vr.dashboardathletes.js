define([
        "rg_viewrepo_base",
        "rg_pubsub",
        "assets/js/models/contestdrillin.js",
        "assets/js/testdata/dashboardcontest.athlete.js",
        "assets/js/testdata/dashboardcontest.entry.js",
        "assets/js/testdata/dashboardcontest.sportevent.js",
	"assets/js/testdata/dashboard.contest.js",
	"assets/js/models/contestliveoverview.js"
], function (Base, pubsub, drillinModel, athleteTestData, entryTestData, sportEventsTestData) {
        ruckus.views.repositories.dashboardathletes = function (parameters) {
                var _this = this;

                Base.call(_this);
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
                        athletes: [],
                        entries: [],
                        sportsEvents: []
                };
                _this.pageloadcomplete = false;

                _.bindAll(this,
                        'registerDataSubscriptions',
                        'registerViewSubscriptions',

//			"putAllContests",
                        "putContest",
//                        "getContests",
                        "getContest",
//                        "processContests",
                        "processContest",

                        'getAthlete',
                        'processAthlete',
                        'putAthlete',
                        'putAllAthletes',

			'putAthleteStatus',
			'processAthleteStatus',

                        'getEntry',
                        'processEntry',
                        'putEntry',
                        'putAllEntries',

                        'getSportEvent',
                        'processSportEvent',
                        'putSportEvent',
                        'putAllSportEvents',

                        'pushPageLoadIfReady'
                );

                // Initialize data repository
                _this.dataRepos.contestDrillinDataRepo = new ruckus.models.contestdrillin(parameters);
        };
        ruckus.views.repositories.dashboardathletes.prototype = Object.create(Base.prototype);

        // FETCH TRIGGERS
        ruckus.views.repositories.dashboardathletes.prototype.fetch = function () {
                var _this = this;
                _this.log({type: 'general', data: undefined, msg: 'VIEW REPO > FETCH'});

                // Register Subscriptions
                _this.registerDataSubscriptions();
                _this.registerViewSubscriptions();

                _this.dataRepos.contestDrillinDataRepo.fetch();
        };

        // TERMINATION
        ruckus.views.repositories.dashboardathletes.prototype.unload = function () {
                this.__destroy();
        };

        // SUBSCRIPTION REGISTRY
        ruckus.views.repositories.dashboardathletes.prototype.registerDataSubscriptions = function () {
		this.__addsubscription(ruckus.pubsub.subscriptions.models.data.contestliveoverview.update, this.putContest);
//           this.__addsubscription(ruckus.pubsub.subscriptions.models.data.contestliveoverview.all, this.putAllContests);

                this.__addsubscription(ruckus.pubsub.subscriptions.models.data.contestlivedetail.entry.update, this.putEntry);
                this.__addsubscription(ruckus.pubsub.subscriptions.models.data.contestlivedetail.sportevent.update, this.putSportEvent);
                this.__addsubscription(ruckus.pubsub.subscriptions.models.data.contestlivedetail.athlete.sporteventinfoupdate, this.putAthlete);
                this.__addsubscription(ruckus.pubsub.subscriptions.models.data.contestlivedetail.athlete.statusupdate, this.putAthleteStatus);
        };
        ruckus.views.repositories.dashboardathletes.prototype.registerViewSubscriptions = function () {
                //this.__addsubscription(ruckus.pubsub.subscriptions.view.dashboardathletes.put.contest, this.putContest);

                this.__addsubscription("model.liveallathletes.retrieve", this.putAllAthletes);
                this.__addsubscription(ruckus.pubsub.subscriptions.view.dashboardathletes.get.athlete, this.getAthlete);

                this.__addsubscription(ruckus.pubsub.subscriptions.view.dashboardathletes.put.allentries, this.putAllEntries);
                this.__addsubscription(ruckus.pubsub.subscriptions.view.dashboardathletes.get.entry, this.getEntry);

//                this.__addsubscription("model.contestliveoverview.all", this.putContest);
                this.__addsubscription(ruckus.pubsub.subscriptions.view.dashboardathletes.get.sportevent, this.getSportEvent);
        };

        ruckus.views.repositories.dashboardathletes.prototype.putContest = function (data) {
                var _this = this;
                $.each(data.contests.contests, function (key, val) {
                        if (val.contestId === _this.local_data.contestId)
                                _this.local_data.contest = val;
                });
        };

	// CONTEST - Contest Updates
/*
        ruckus.views.repositories.dashboardathletes.prototype.putAllContests = function (data) {
                this.processContests(data.contests.contests);
        };
*/
        ruckus.views.repositories.dashboardathletes.prototype.putContest = function (data) {
                this.processContest(data.data);
        };

        ruckus.views.repositories.dashboardathletes.prototype.getContests = function () {
                this.msgBus.publish(ruckus.pubsub.subscriptions.view.dashboard.contest.all, _this.local_data.contests);
        };
        ruckus.views.repositories.dashboardathletes.prototype.getContest = function (id) {
                var _this = this;
                var contest = null;

                for (var i = 0; i < _this.local_data.contests.length; i++) {
                        var e = _this.local_data.contests[i];
                        if (e.contestId === id) {
                                contest = e;
                        }
                }
                if (contest)
                        _this.processContest(contest);  // FIXME:  Should we return a value if no record exists?
        };
/*
        ruckus.views.repositories.dashboardathletes.prototype.processContests = function (contests) {
                var _this = this;
                if (Array.isArray(contests) === false) return false;

                $.each(contests, function(k, contest){
                        _this.processContest(contest);
                });

                this.msgBus.publish(ruckus.pubsub.subscriptions.view.dashboard.contest.all, _this.local_data.contests);
        };
*/
	ruckus.views.repositories.dashboardathletes.prototype.processContest = function (contestupdate) {
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
                        if (_this.mockdata) {
                                var interval = (Math.floor(Math.random() * 20) * 100) + 5000;
                                _this.pushTestData(ruckus.pubsub.subscriptions.models.data.contestliveoverview.update, new contestMockData(contest), interval);
                        }
                        _this.local_data.contests.push(contest);
                }

                // Set active contest to local data for easier access.
                if(_this.local_data.contestId === contest.contestId) {
                        _this.local_data.contest = contest;
                }

                if (oldcontest) {
                        // We don't want to send out duplicate records.
                        if (oldcontest === contest) {
                                _this.log({type: 'datavalidation', level: 2, data: contest, msg: 'DASHBOARD VIEW REPO > Duplicate record received'});
                                return;
                        }
			if (contest.fpp !== oldcontest.fpp) {
                                contest.fpp_change = contest.fpp - oldcontest.fpp;
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
	ruckus.views.repositories.dashboardathletes.prototype.formatContest = function (contestupdate) {
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

        // ATHLETES - Athlete Info Updates
        ruckus.views.repositories.dashboardathletes.prototype.putAllAthletes = function (athletes) {
                var _this = this;
                _this.local_data.athletes = athletes.data;

                if (_this.mockdata) {
                        $.each(_this.local_data.athletes, function (key, val) {
                                var interval = (Math.floor(Math.random() * 20) * 100) + _this.interval;
                                _this.pushTestData(ruckus.pubsub.subscriptions.models.data.contestlivedetail.athlete.sporteventinfoupdate, new athleteTestData(val), interval);
                        });
                }

        };
        ruckus.views.repositories.dashboardathletes.prototype.getAthlete = function (id) {
                var _this = this;
                var athlete = null;
                for (var i = 0; i < _this.local_data.athletes.length; i++) {
                        var e = _this.local_data.athletes[i];
                        if (e.athleteSportEventInfoId === id) {
                                athlete = e;
                        }
                }
                if (athlete) _this.processAthlete(athlete);  // FIXME:  Should we return a value if no record exists?
        };
        ruckus.views.repositories.dashboardathletes.prototype.processAthlete = function (athlete) {
                var _this = this;
                _this.log({type: 'general', data: athlete, msg: 'DASHBOARD ENTRY VIEW REPO > processAthlete'});

                // DATA VALIDATION
                if (athlete.indicator < 0 || athlete.indicator > 2)  _this.log({type: 'error', data: athlete, msg: '>>>>>>>>>>>>>>>>>>>>>>>>>>>>>ATHLETE UPDATE VALIDATION > Invalid indicator value ' + athlete.indicator});

                if (Array.isArray(athlete.stats) === false) {
                        _this.log({type: 'error', level: 3, data: athlete, msg: '>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ATHLETE UPDATE VALIDATION > Athlete status is not a valid array.'});
                }
                if (Array.isArray(athlete.stats) === true && athlete.stats.length === 0) {
                        _this.log({type: 'error', level: 3, data: athlete, msg: '>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ATHLETE UPDATE VALIDATION > Athlete status are empty for id ' + athlete.athleteSportEventInfoId});
                }
                if (Array.isArray(athlete.timeline) === false) {
                        _this.log({type: 'error', level: 3, data: athlete, msg: '>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ATHLETE UPDATE VALIDATION > Athlete timeline is not a valid array.'});
                        athlete.timeline = [];
                }

                // FIXME: Testing purposes only.  Remove when simulator is working and business logic is in place.
//                var eventsenabled = true;

//                if (eventsenabled) {
                        // FIXME:  Add logic to determine update in current payout
                        _this.log({type: 'general', data: athlete, msg: 'DASHBOARD ENTRY VIEW REPO > pushAthleteStatusUpdate'});
                        _this.msgBus.publish(ruckus.pubsub.subscriptions.view.dashboardathletes.contest.athlete.statsupdate, athlete);
//                }
//                if (eventsenabled) {
                        // FIXME:  Add logic to determine update in current position
                        _this.log({type: 'general', data: athlete, msg: 'DASHBOARD ENTRY VIEW REPO > pushAthleteUnitsRemainingUpdate'});
                        _this.msgBus.publish(ruckus.pubsub.subscriptions.view.dashboardathletes.contest.athlete.unitsremaining, athlete);
//                }
//                if (eventsenabled) {
			// FIXME:  Add logic to determine update in current position
                        _this.log({type: 'general', data: athlete, msg: 'DASHBOARD ENTRY VIEW REPO > pushAthleteTimelineUpdate'});
                        _this.msgBus.publish(ruckus.pubsub.subscriptions.view.dashboardathletes.contest.athlete.timeline, athlete);
//                }
        };
        ruckus.views.repositories.dashboardathletes.prototype.putAthleteStatus = function (data) {
                var _this = this;
                _this.log({type: 'general', data: data, msg: 'DASHBOARD ENTRY VIEW REPO > putAthleteStatus'});
		var athleteupdate = data.data;
                var oldathlete;
                var athlete;
                var isupdate = false;

                for (var i = 0; i < _this.local_data.athletes.length; i++) {
                        var e = _this.local_data.athletes[i];
                        if (athleteupdate.athleteSportEventInfoId === e.athleteSportEventInfoId) {
                                oldathlete = JSON.parse(JSON.stringify(e));

                                for (var key in athleteupdate) {
                                        if (athleteupdate.hasOwnProperty(key)) {
                                                e[key] = athleteupdate[key];
                                        }
                                }
                                athlete = e;
                                isupdate = true;
                        }
                }
                if (!isupdate) {
                        athlete = athleteupdate;
                        _this.local_data.athletes.push(athlete);
                }
                this.processAthleteStatus(athlete, oldathlete);
        };
	ruckus.views.repositories.dashboardathletes.prototype.processAthleteStatus = function (athlete) {
                var _this = this;
                _this.log({type: 'general', data: athlete, msg: 'DASHBOARD ENTRY VIEW REPO > processAthleteStatus'});

		_this.log({type: 'general', data: athlete, msg: 'DASHBOARD ENTRY VIEW REPO > pushAthleteUnitsRemainingUpdate'});
                _this.msgBus.publish(ruckus.pubsub.subscriptions.view.dashboardathletes.contest.athlete.unitsremaining, athlete);
	};
        ruckus.views.repositories.dashboardathletes.prototype.putAthlete = function (data) {
		var _this = this;
                var athleteupdate = data.data;
                var oldathlete;
                var athlete;
                var isupdate = false;

                for (var i = 0; i < _this.local_data.athletes.length; i++) {
                        var e = _this.local_data.athletes[i];
                        if (athleteupdate.athleteSportEventInfoId === e.athleteSportEventInfoId) {
                                oldathlete = JSON.parse(JSON.stringify(e));

                                for (var key in athleteupdate) {
                                        if (athleteupdate.hasOwnProperty(key)) {
                                                e[key] = athleteupdate[key];
                                        }
                                }
                                athlete = e;
                                isupdate = true;
                        }
                }
                if (!isupdate) {
                        athlete = athleteupdate;
                        _this.local_data.athletes.push(athlete);
                }
                this.processAthlete(athlete, oldathlete);
        };
        // ENTRY - Entry Updates
        ruckus.views.repositories.dashboardathletes.prototype.putAllEntries = function (data) {
                var _this = this;
                //_this.local_data.athletes.clear();
                _this.local_data.entries = data;

                if (_this.mockdata) {
                        $.each(_this.local_data.entries, function (key, val) {
                                var interval = (Math.floor(Math.random() * 20) * 100) + 1000;
                                _this.pushTestData(ruckus.pubsub.subscriptions.models.data.contestlivedetail.entry.update, new entryTestData(val.entryId), interval);
                        });
                }
        };
        ruckus.views.repositories.dashboardathletes.prototype.getEntry = function (id) {
                var _this = this;
                var entry = null;

                for (var i = 0; i < _this.local_data.entries.length; i++) {
                        var e = _this.local_data.entries[i];
                        if (e.id === id) {
                                entry = e;
                        }
                }
                if (entry) _this.processEntry(entry);  // FIXME:  Should we return a value if no record exists?
        };
        ruckus.views.repositories.dashboardathletes.prototype.processEntry = function (entryupdate, oldentry) {
                var _this = this;
                _this.log({type: 'method', level: 4, data: entryupdate, msg: 'DASHBOARD ENTRY VIEW REPO > processEntry'});

                if (oldentry) {
                        // We don't want to send out duplicate records.
                        if (oldentry === entryupdate) {
                                _this.log({type: 'datavalidation', level: 2, data: entryupdate, msg: 'DASHBOARD ENTRY VIEW REPO > Duplicate record received'});
                                return;
                        }
                        if (entryupdate.fpp !== oldentry.fpp) {
                                //handle fpp change
                                _this.rankEntries();

                                _this.log({type: 'general', level: 4, data: entryupdate, msg: 'DASHBOARD ENTRY VIEW REPO > pushContestEntryFppUpdate'});
                                _this.msgBus.publish(ruckus.pubsub.subscriptions.view.dashboardathletes.contest.entry.all, _this.local_data.entries);

                                _this.log({type: 'general', level: 4, data: entryupdate, msg: 'DASHBOARD ENTRY VIEW REPO > pushContestEntryFppUpdate'});
                                _this.msgBus.publish(ruckus.pubsub.subscriptions.view.dashboardathletes.contest.entry.fpp, entryupdate);
                        }
                        if (entryupdate.unitsRemaining !== oldentry.unitsRemaining) {
                                _this.log({type: 'general', level: 4, data: entryupdate, msg: 'DASHBOARD ENTRY VIEW REPO > pushContestEntryUnitsRemaining'});
                                _this.msgBus.publish(ruckus.pubsub.subscriptions.view.dashboardathletes.contest.entry.unitsremaining, entryupdate);
                        }
                } else {
                        _this.log({type: 'general', data: entryupdate, msg: 'DASHBOARD ENTRY VIEW REPO > pushContestEntryUnitsRemaining'});
                        _this.msgBus.publish(ruckus.pubsub.subscriptions.view.dashboardathletes.contest.entry.unitsremaining, entryupdate);

                        _this.log({type: 'general', data: entryupdate, msg: 'DASHBOARD ENTRY VIEW REPO > pushContestEntryFppUpdate'});
                        _this.msgBus.publish(ruckus.pubsub.subscriptions.view.dashboardathletes.contest.entry.fpp, entryupdate);
                }
        };
        ruckus.views.repositories.dashboardathletes.prototype.putEntry = function (data) {
		var _this = this;
                var entryupdate = data.data;
                var oldentry;
                var entry;
                var isupdate = false;

                for (var i = 0; i < _this.local_data.entries.length; i++) {
                        var e = _this.local_data.entries[i];
                        if (entryupdate.id === e.entryId) {
                                oldentry = JSON.parse(JSON.stringify(e));

                                for (var key in entryupdate) {
                                        if (entryupdate.hasOwnProperty(key)) {
                                                e[key] = entryupdate[key];
                                        }
                                }
                                entry = e;
                                isupdate = true;
                        }
                }
                if (!isupdate) {
                        entry = entryupdate;
                        _this.local_data.entries.push(entry);
                }
                this.processEntry(entry, oldentry);
        };
        // SPORT EVENTS - Sport Event Info Updates
        ruckus.views.repositories.dashboardathletes.prototype.putAllSportEvents = function (entry) {
                var _this = this;
                _this.local_data.entries = entry.data;
                if (_this.mockdata) {
                        $.each(_this.local_data.sportsEvents, function (key, val) {
                                var interval = (Math.floor(Math.random() * 20) * 100) + 1000;
                                _this.pushTestData(ruckus.pubsub.subscriptions.models.data.contestlivedetail.sportevent.update, new sportEventsTestData(val.id), interval);
                        });
                }
        };
        ruckus.views.repositories.dashboardathletes.prototype.getSportEvent = function (id) {
                var _this = this;
                var sportEvent = null;
                for (var i = 0; i < _this.local_data.sportsEvents.length; i++) {
                        var e = _this.local_data.sportsEvents[i];
                        if (e.id === id) {
                                sportEvent = e;
                        }
                }
                if (sportEvent) _this.processSportEvent(sportEvent);  // FIXME:  Should we return a value if no record exists?
        };
        ruckus.views.repositories.dashboardathletes.prototype.processSportEvent = function (sportEvent) {
                var _this = this;
                _this.log({type: 'general', data: sportEvent, msg: 'DASHBOARD ENTRY VIEW REPO > processSportEvent'});

                // FIXME: Testing purposes only.  Remove when simulator is working and business logic is in place.
                var eventsenabled = true;

                if (eventsenabled) {
                        // FIXME:  Add logic to determine update in current payout
                        _this.log({type: 'general', data: sportEvent, msg: 'DASHBOARD ENTRY VIEW REPO > pushSportEventUpdate'});
                        _this.msgBus.publish(ruckus.pubsub.subscriptions.view.dashboardathletes.contest.sportevent.update, sportEvent);
                }
        };
        ruckus.views.repositories.dashboardathletes.prototype.putSportEvent = function (data) {
                var _this = this;
                // Strip off any extra wrappers.
                var sportEvent = data.data;

                for (var i = 0; i < _this.local_data.sportsEvents.length; i++) {
                        var e = _this.local_data.sportsEvents[i];
                        if (sportEvent.id === e.id) {
                                if (sportEvent == e) _this.log({type: 'error', data: sportEvent, msg: 'SPORT EVENT UPDATE VALIDATION > Duplicate record received.'});
                                _this.local_data.sportsEvents.splice(i, 1);
                        }
                }

                _this.local_data.sportsEvents.push(sportEvent);
                this.processSportEvent(sportEvent);
        };

        // PAGE LOAD
        ruckus.views.repositories.dashboardathletes.prototype.pushPageLoadIfReady = function () {
                var _this = this;
                _this.log({type: 'general', data: data, msg: 'DASHBOARD ENTRY VIEW REPO > pushPageLoadIfReady'});
                if (_this.local_data.contests !== null && _this.local_data.scoring !== null && _this.local_data.servertime !== null) {

                        var contests = [];
                        $.each(_this.local_data.contests, function (key, val) {
                                contests.push(_this.prepareViewData(val));
                        });
                        _this.local_data.contests = contests;

                        _this.msgBus.publish(ruckus.pubsub.subscriptions.view.dashboardathletes.pageload, _this.local_data);
                        this.pageloadcomplete = true;
                }
        };
        // TEST DATA
        ruckus.views.repositories.dashboardathletes.prototype.pushTestData = function (topic, data, interval) {
                var _this = this;
                var int = setInterval(function () {
                        if (data.length === 0) {
                                clearInterval(int);
                        } else {
                                _this.log({type: 'general', data: data[0], msg: 'DASHBOARD ENTRY VIEW REPO > INTERVAL DATA'});
                                _this.msgBus.publish(topic, { data: data[0] });
                                data.shift();
                        }
                }, interval);
                _this.intervals.push(int);
        };

        ruckus.views.repositories.dashboardathletes.prototype.rankEntries = function () {
                var _this = this;
                var key = 'fpp';
                var compare = function (a, b) {
                        if (a[key] < b[key])
                                return 1;
                        if (a[key] > b[key])
                                return -1;
                        return 0;
                };

                _this.local_data.entries.sort(compare);

                for (var x = 0; x < _this.local_data.entries.length; x++) {
                        _this.local_data.entries[x].pos = x + 1;
                }

                $.each(_this.local_data.entries, function (key, value) {
                        $.each(_this.local_data.contest.payouts, function (k, v) {
                                if (value.pos >= v.leadingPosition && value.pos <= v.trailingPosition)
                                        value.prize = _this.formatMoney(v.payoutAmount);
                        });
                });
        };

        return ruckus.views.repositories.dashboardathletes;
});
