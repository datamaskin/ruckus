define([
        "assets/js/pagecontrols/base.js",
        "assets/js/models/contestliveoverview.js",
        "assets/js/libraries/jquery.flot.min.js",
        'assets/js/modules/navigation.js',
        'assets/js/modules/counters.circle.js',
        "assets/js/viewrepositories/vr.dashboard.js"
], function (Base) {
        ruckus.pagecontrols.dashboard = function (parameters) {
                var _this = this;
                Base.call(this);
                _this.parameters = parameters;
                _this.viewrepo = null;
                _this.viewdata = {
                        contests: null
                };

                if(parameters.contestId) _this.viewdata.contestId = parameters.contestId;

                // Initialize repository
                _this.viewrepo = new ruckus.views.repositories.dashboard(parameters);

                // Context bindings
                _.bindAll(this,
                        'updateCounters',
                        'onUpdateCurrentPayout',
                        'onUpdateCurrentPosition',
                        'onUpdateCurrentPoints',
                        'onUpdateProjectedPayout',
                        'onUpdateCurrentUnitsRemaining',
                        'onUpdateServerTime');
        };

        ruckus.pagecontrols.dashboard.prototype = Object.create(Base.prototype);

        ruckus.pagecontrols.dashboard.prototype.load = function () {
                var _this = this;

                // DATA EVENT LISTENERS
                _this.subscriptions.push(_this.msgBus.subscribe(ruckus.pubsub.subscriptions.view.dashboard.servertime, _this.onUpdateServerTime));
                _this.subscriptions.push(_this.msgBus.subscribe(ruckus.pubsub.subscriptions.view.dashboard.contest.active.currentpayout, _this.onUpdateCurrentPayout));
                _this.subscriptions.push(_this.msgBus.subscribe(ruckus.pubsub.subscriptions.view.dashboard.contest.active.currentposition, _this.onUpdateCurrentPosition));
                _this.subscriptions.push(_this.msgBus.subscribe(ruckus.pubsub.subscriptions.view.dashboard.contest.active.currentpoints, _this.onUpdateCurrentPoints));
                _this.subscriptions.push(_this.msgBus.subscribe(ruckus.pubsub.subscriptions.view.dashboard.contest.active.projectedpayout, _this.onUpdateProjectedPayout));
                _this.subscriptions.push(_this.msgBus.subscribe(ruckus.pubsub.subscriptions.view.dashboard.contest.active.currentunitsremaining, _this.onUpdateCurrentUnitsRemaining));


                // FIXME - We should move the view logic into a "render" function to be consistent.
                this.getContainer();
                this.container.addClass('ruckus-pc-dashboard');

                this.require_template('dashboard-tpl');
                dust.render('dusttemplates/dashboard-tpl', {}, function (err, out) {
                        _this.container.html(out);
                        _this.addScrollBars();
                        _this.cellContainer = $('#dshm_cellcontainer');
                        // FIXME - needs the correct model once created
                        _this.contestLiveOverviewModel = new ruckus.models.contestliveoverview({});
                        _this.models.push(_this.contestLiveOverviewModel);
                        var selected_contest = null;
                        var sub2 = _this.msgBus.subscribe(ruckus.pubsub.subscriptions.view.dashboard.contest.all, function (contests) {
                                _this.viewdata.contests = contests;
                                if (_this.viewdata.contests.length == 0) {
                                        $('#dshm_cellcontainer').html('You do not currently have any active, locked, or open contests.').addClass('noContest');
                                } else {
                                        $.each(_this.viewdata.contests, function (key, value) {
                                                if (value.contestState == 'active') {
                                                        _this.rendercellActive(value);
                                                } else {
                                                        _this.rendercellUpcoming(value);
                                                }
                                                if(_this.viewdata.contestId === value.contestId){
                                                        selected_contest = value.contestId;
                                                }

                                        });

                                        if(selected_contest) {
                                                var selected_contest_el = $('#dshm_cell_' + selected_contest);
                                                $('html, body').animate({
                                                        scrollTop: selected_contest_el.offset().top - 100
                                                }, 1000, function(){
                                                        selected_contest_el.addClass('outerglow');
                                                        setTimeout(function () {
                                                                selected_contest_el.removeClass('outerglow');
                                                        }, 2000);
                                                });
                                        }
                                        _this.startIntervals();
                                }
                        });
                        _this.subscriptions.push(sub2);
			_this.viewrepo.fetch();

                        $('#dashnavc').bind('click', function (evt) {
                                evt.stopPropagation();
                                ruckus.modules.navigation.toRoute('dashboard');
                        });
                        $('#dashnava').bind('click', function (evt) {
                                evt.stopPropagation();
                                ruckus.modules.navigation.toRoute('dashboardathletes');
                        });
                        $('#dashnavl').bind('click', function (evt) {
                                evt.stopPropagation();
                                ruckus.modules.navigation.toRoute('dashboardlineups');
                        });
                        $('#dashnavh').bind('click', function (evt) {
                                evt.stopPropagation();
                                ruckus.modules.navigation.toRoute('dashboardhistory');
                        });
                });
        };

        ruckus.pagecontrols.dashboard.prototype.rendercellActive = function (contest) {
                var _this = this;
                this.require_template('dashboardcell-tpl');
                dust.render('dusttemplates/dashboardcell-tpl', contest, function (err, out) {
                        //may not be an entirely valid href. should be tested.
                        $('<a>').attr("href", "#dashboardcontest/" + contest.contestId).html(out).appendTo(_this.cellContainer); //changed to a tag instead of div to bestow proper functionality.
                        _this.addScrollBars();

			var contestpayout_el = $("div[data-socket='payout_" + contest.contestId + "_" + contest.lineupId + "']");
			if(contest.payout > 0){
				contestpayout_el.parent().addClass('up');
			} else {
				contestpayout_el.parent().removeClass('up');
			}
			var contestprojectedpayout_el = $("div[data-socket='projpayout_" + contest.contestId + "_" + contest.lineupId + "']");
			if (contest.projectedPayout > 0) {
				contestprojectedpayout_el.parent().addClass('up');
			} else {
				contestprojectedpayout_el.parent().removeClass('up');
			}
                        $('#dshm_cell_' + contest.contestId + '_' + contest.lineupId).bind('click', function (evt) {
                                evt.stopPropagation();
                                ruckus.modules.navigation.toRoute("dashboardcontest/" + contest.contestId);
                        });
                });
        };
        ruckus.pagecontrols.dashboard.prototype.rendercellUpcoming = function (contest) {
                var _this = this;
                var contestformat = _this.formatContestName(contest, '2line');
                contest.line1 = contestformat.line1;
                contest.line2 = contestformat.line2;
                this.require_template('dashboardcellupcoming-tpl');
                dust.render('dusttemplates/dashboardcellupcoming-tpl', contest, function (err, out) {
                        $('<a>').attr("href", "#dashboardlineups/" + contest.contestId).html(out).appendTo(_this.cellContainer); //changed to a tag instead of div to bestow proper functionality.

                        //$('<div>').html(out).appendTo(_this.cellContainer); //for upcoming events that are not active. should not need same functionality.
                        _this.addScrollBars();

                        var detail_container = $('#dshm_cell_' + contest.contestId);
                        if (detail_container.length > 0) {
                                if (detail_container.find(".dshm_cell_upcoming_chart").length === 1) {
                                        contest.remainingTime = ruckus.modules.datetime.diff(_this.servertime, contest.startTime, 'seconds');
                                        ruckus.modules.counters.circle.render(detail_container, "dshm_cell_upcoming_chart", contest.remainingTime);
                                }
                        }
//                        detail_container.bind('click', function (evt) {
//                                evt.stopPropagation();
//                                ruckus.modules.navigation.toRoute("dashboardlineups/" + contest.contestId);
//                        });
                });
        };

        // EVENTS
        ruckus.pagecontrols.dashboard.prototype.onUpdateCurrentPayout = function (contest) {
                var _this = this;
                _this.log({type: 'general', data: contest, msg: 'DASHBOARD PAGE CONTROL > onUpdateCurrentPayout'});
                var contestpayout_el = $("div[data-socket='payout_" + contest.contestId + "_" + contest.lineupId + "']");
                contestpayout_el.html(contest.formattedPayout);
                if(contest.payout > 0){
                        contestpayout_el.parent().addClass('up');
                } else {
                        contestpayout_el.parent().removeClass('up');
                }
        };
        ruckus.pagecontrols.dashboard.prototype.onUpdateCurrentPosition = function (contest) {
                var _this = this;
                _this.log({type: 'general', data: contest, msg: 'DASHBOARD PAGE CONTROL > onUpdateCurrentPosition'});
                var contestposition_el = $("div[data-socket='position_" + contest.contestId + "_" + contest.lineupId + "']")
                contestposition_el.html(contest.formattedPosition);
                if (contest.position_direction == "up") {
                        contestposition_el.parent().addClass('up');
                        setTimeout(function () {
                                contestposition_el.parent().removeClass('up');
                        }, 3000);
                }
                if (contest.position_direction == "down") {
                        contestposition_el.parent().addClass('down');
                        setTimeout(function () {
                                contestposition_el.parent().removeClass('down');
                        }, 3000);
                }
        };
        ruckus.pagecontrols.dashboard.prototype.onUpdateCurrentPoints = function (contest) {
                var _this = this;
                _this.log({type: 'general', data: contest, msg: 'DASHBOARD PAGE CONTROL > onUpdateCurrentPoints'});
                var contestpoints_el = $("div[data-socket='fpp_" + contest.contestId + "_" + contest.lineupId + "']");
                contestpoints_el.html(contest.fpp);
                if (contest.fpp_direction == "up") {
                        contestpoints_el.parent().addClass('up');
                        contestpoints_el.html(contest.fpp_change);
                        setTimeout(function () {
                                contestpoints_el.parent().removeClass('up');
                                contestpoints_el.html(contest.fpp);
                        }, 3000);
                }
                if (contest.fpp_direction == "down") {
                        contestpoints_el.parent().addClass('down');
                        contestpoints_el.html(contest.fpp_change);
                        setTimeout(function () {
                                contestpoints_el.parent().removeClass('down');
                                contestpoints_el.html(contest.fpp);
                        }, 3000);
                }
        };
        ruckus.pagecontrols.dashboard.prototype.onUpdateProjectedPayout = function (contest) {
                var _this = this;
                _this.log({type: 'general', data: contest, msg: 'DASHBOARD PAGE CONTROL > onUpdateProjectedPayout'});
                var contestprojectedpayout_el = $("div[data-socket='projpayout_" + contest.contestId + "_" + contest.lineupId + "']");
                contestprojectedpayout_el.html(contest.formattedProjectedPayout);

                if (contest.projectedPayout > 0) {
                        contestprojectedpayout_el.parent().addClass('up');
                } else {
                        contestprojectedpayout_el.parent().removeClass('up');
                }
        };
        ruckus.pagecontrols.dashboard.prototype.onUpdateCurrentUnitsRemaining = function (contest) {
                var _this = this;
                _this.log({type: 'general', data: contest, msg: 'DASHBOARD PAGE CONTROL > onUpdateCurrentUnitsRemaining'});
                $("div[data-socket='timepercentage_" + contest.contestId + "_" + contest.lineupId + "']").css("width", _this.formatTimePercentageTeam(contest.league, contest.unitsRemaining) + "%");
        };
        ruckus.pagecontrols.dashboard.prototype.onUpdateServerTime = function (data) {
                var _this = this;
                _this.log({type: 'general', data: data, msg: 'DASHBOARD PAGE CONTROL > onUpdateServerTime'});
                _this.viewdata.servertime = JSON.parse(data);
                _this.viewdata.servertime_updated = ruckus.modules.datetime.now();

                if (!_this.viewdata.contests) return false;
                $.each(_this.viewdata.contests, function (key, val) {
                        val.remainingTime = ruckus.modules.datetime.diff(_this.viewdata.servertime, val.startTime, 'seconds');
                });
        };

        // COUNTERS
        ruckus.pagecontrols.dashboard.prototype.updateCounters = function () {
                var _this = this;
                if (!_this.viewdata.contests) return false;

                $.each(_this.viewdata.contests, function (key, val) {
                        // decrement the time by one second
                        if (val.remainingTime === 1) {
                                ruckus.modules.navigation.reload();
                        }

                        val.remainingTime = val.remainingTime - 1;
                        var detail_container = $('#dshm_cell_' + val.contestId);
                        if (detail_container.length > 0) {
                                if (detail_container.find(".dshm_cell_upcoming_chart").length === 1) {
                                        ruckus.modules.counters.circle.update(detail_container, "dshm_cell_upcoming_chart", val.remainingTime);
                                        if(val.remainingTime < 1) {
                                                detail_container.find(".timeStampSubTitle").html('');
                                        }
                                }
                        }
                });
        };
        ruckus.pagecontrols.dashboard.prototype.startIntervals = function () {
                var _this = this;
                if (this.serverTimeInterval === undefined) {
                        this.serverTimeInterval = setInterval(_this.viewrepo.fetchServerTime, 60000);
                        _this.intervals.push(this.serverTimeInterval);
                }
                if (this.counterInterval === undefined) {
                        this.counterInterval = setInterval(_this.updateCounters, 1000);
                        _this.intervals.push(this.counterInterval);
                }
        };

        // TERMINATION
        ruckus.pagecontrols.dashboard.prototype.unload = function () {
                this.viewrepo.unload();
                delete this.viewrepo;
                this.destroyControl();
        };

        return ruckus.pagecontrols.dashboard;
});
