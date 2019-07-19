// Author: Scott Gay
define([
        "rg_subpage_base",
        "dust",
        "assets/js/models/lineupenter.js",
        "assets/js/viewrepositories/contestentry.js",
        "assets/js/subpagecontrols/contestresultsinfo.js",
        "assets/js/subpagecontrols/contestresultsentries.js",
        "assets/js/subpagecontrols/contestresultsprizes.js",
        "assets/js/subpagecontrols/contestresultslineups.js",
        "assets/js/subpagecontrols/contestresultslineupscontestenter.js"
], function (Base) {
        ruckus.subpagecontrols.contestrow = function (parameters) {
                Base.call(this);
                this.init = function () {
                        this.parameters = parameters;
                        this.viewrepo = null;
                        this.viewdata = {};
                };
                this.init();

                _.bindAll(this,
                        "receiveUpdateServerTime",
                        "updateCounters");
        };

        ruckus.subpagecontrols.contestrow.prototype = Object.create(Base.prototype);
        ruckus.subpagecontrols.contestrow.prototype.load = function () {
                var _this = this;
                _this.viewdata = {
                        contest: _this.parameters.contest
                };
                _this.viewrepo = new ruckus.views.repositories.contestentry();

                this.getContainer();
                this.container.addClass('ruckus-spc-contestresults'); // ruckus-spc-contestrow ... so we can use the same style sheet (namespace) as from the lobby

                // DATA EVENT LISTENERS
                _this.subscriptions.push(_this.msgBus.subscribe(ruckus.pubsub.subscriptions.view.contestentry.contestrow.servertime, _this.receiveUpdateServerTime));

                // load contest scoring
                _this.contestScoringModel = new ruckus.models.contestscoring({});
                _this.models.push(_this.contestScoringModel);
                var sub4 = _this.msgBus.subscribe("model.contestscoring.retrieve", function (data) {
                        sub4.unsubscribe();
                        _this.contestScoringModel = data;
                        _this.parameters.contest.line1full = _this.formatContestName(_this.parameters.contest, '1linefull').line1;
                        _this.parameters.contest.formattedEntryFee = _this.formatMoney(_this.parameters.contest.entryFee);
                        _this.parameters.contest.formattedPrizePool = _this.formatMoney(_this.parameters.contest.prizePool);
                        _this.parameters.contest.formattedStartTime = _this.formatTimeActual(_this.parameters.contest.startTime);
                        _this.parameters.contest.formattedEntries = _this.parameters.contest.currentEntries.formatInteger();
                        _this.parameters.contest.formattedCapacity = _this.parameters.contest.capacity.formatInteger();
                        _this.require_template('contestrow-tpl');
                        dust.render('dusttemplates/contestrow-tpl', _this.parameters.contest, function (err, out) {
                                _this.container.html(out);
                                _this.addScrollBars();
                                if ($('#conr_details_' + _this.parameters.contest.id).html() != undefined) {
                                        _this.closeDetails(_this.parameters.contest);
                                }
                                $('#contestentryback').bind('click', function (evt) {
                                        evt.stopPropagation();
//                                        _this.msgBus.publish('control.navigate.lobby', {});
                                        window.location.href = '#lobby';
                                });

                                $('#reserve_' + _this.parameters.contest.id).bind('click', function (evt) {
                                        evt.stopPropagation();
                                        _this.lineupEnterModel = new ruckus.models.lineupenter({});
                                        _this.models.push(_this.lineupEnterModel);
                                        _this.lineupEnterModel.fetch({contestId: _this.parameters.contest.id});
                                });
                                $('#contestentryrow_' + _this.parameters.contest.id).bind('click', function (evt) {
                                        evt.stopPropagation();
                                        if ($('#conr_details_' + _this.parameters.contest.id).html() != undefined)
                                                _this.closeDetails(_this.parameters.contest);
                                        else
                                                _this.expandDetails(_this.parameters.contest, 'info');
                                });
                                $('#contestentryrowcurrententries_' + _this.parameters.contest.id).bind('click', function (evt) {
                                        evt.stopPropagation();
                                        if ($('#conr_details_' + _this.parameters.contest.id).html() != undefined)
                                                _this.closeDetails(_this.parameters.contest);
                                        else
                                                _this.expandDetails(_this.parameters.contest, 'entries');
                                });
                                $('#contestentryrowsize_' + _this.parameters.contest.id).bind('click', function (evt) {
                                        evt.stopPropagation();
                                        if ($('#conr_details_' + _this.parameters.contest.id).html() != undefined)
                                                _this.closeDetails(_this.parameters.contest);
                                        else
                                                _this.expandDetails(_this.parameters.contest, 'entries');
                                });
                                $('#contestentryrowentrypool_' + _this.parameters.contest.id).bind('click', function (evt) {
                                        evt.stopPropagation();
                                        if ($('#conr_details_' + _this.parameters.contest.id).html() != undefined)
                                                _this.closeDetails(_this.parameters.contest);
                                        else
                                                _this.expandDetails(_this.parameters.contest, 'prizes');
                                });
                                $('#contestentryrowquick_' + _this.parameters.contest.id).bind('click', function (evt) {
                                        evt.stopPropagation();
                                        if ($('#conr_details_' + _this.parameters.contest.id).html() != undefined)
                                                _this.closeDetails(_this.parameters.contest);
                                        else
                                                _this.expandDetails(_this.parameters.contest, 'lineups');
                                });


                                _this.startIntervals();
                        });
                });

                // INITIALIZE THE VIEW REPOSITORY
                _this.contestScoringModel.fetch();
        };

        ruckus.subpagecontrols.contestrow.prototype.expandDetails = function (contest, tab) {
                var _this = this;
                this.log({type: 'general', data: contest, msg: "CONTEST DATA MODEL"});
                this.log({type: 'general', data: tab, msg: "TAB TO DEFAULT TO"});

                this.closeAllDetails(contest.id);
                var contestformat = _this.formatContestName(contest, '2line');
                contest.line1 = contestformat.line1;
                contest.line2 = contestformat.line2;
                contest.timePercentage = 45;
                contest.minutesTillStart = 35;
                this.require_template('contestresultstabledetails-tpl');
                dust.render('dusttemplates/contestresultstabledetails-tpl', contest, function (err, out) {
                        $('#contestentryrow_' + contest.id).after(out);
                        _this.addScrollBars();

                        // hide/show multientry and guaranteed
                        if (contest.allowedEntries < 2)
                                $('.Mcont').hide();
                        if (!contest.guaranteed)
                                $('.GPPcont').hide();

                        // highlight tab
                        $('#conr_tab' + tab + '_' + contest.id).addClass('tabSelected');

                        _this.spcInfo = new ruckus.subpagecontrols.contestresultsinfo({
                                'container': $('#conr_info_' + contest.id),
//                                'data' : _this.contestModel.modelData,
                                'contest': contest,
                                'contestscoring': _this.contestScoringModel,
                                'tab': tab
                        });
                        _this.spcInfo.load();
                        _this.controls.push(_this.spcInfo);
                        _this.spcEntries = new ruckus.subpagecontrols.contestresultsentries({
                                'container': $('#conr_entries_' + contest.id),
//                                'data' : _this.contestModel.modelData,
                                'contest': contest,
                                'tab': tab
                        });
                        _this.spcEntries.load();
                        _this.controls.push(_this.spcEntries);
                        _this.spcPrizes = new ruckus.subpagecontrols.contestresultsprizes({
                                'container': $('#conr_prizes_' + contest.id),
//                                'data' : _this.contestModel.modelData,
                                'contest': contest,
                                'tab': tab
                        });
                        _this.spcPrizes.load();
                        _this.controls.push(_this.spcPrizes);
                        _this.spcLineups = new ruckus.subpagecontrols.contestresultslineupscontestenter({
                                'container': $('#conr_lineups_' + contest.id),
//                                'data' : _this.contestModel.modelData,
                                'contest': contest,
                                'tab': tab
                        });
                        _this.spcLineups.load();
                        _this.controls.push(_this.spcLineups);
                        $('#conr_tabinfo_' + contest.id).click(function (evt) {
                                evt.stopPropagation();
                                $('#conr_tabinfo_' + contest.id).addClass('tabSelected');
                                $('#conr_tabentries_' + contest.id).removeClass('tabSelected');
                                $('#conr_tabprizes_' + contest.id).removeClass('tabSelected');
                                $('#conr_tablineups_' + contest.id).removeClass('tabSelected');
                                _this.spcInfo.showPage();
                                _this.spcEntries.hidePage();
                                _this.spcPrizes.hidePage();
                                _this.spcLineups.hidePage();
                        });
                        $('#conr_tabentries_' + contest.id).click(function (evt) {
                                evt.stopPropagation();
                                $('#conr_tabinfo_' + contest.id).removeClass('tabSelected');
                                $('#conr_tabentries_' + contest.id).addClass('tabSelected');
                                $('#conr_tabprizes_' + contest.id).removeClass('tabSelected');
                                $('#conr_tablineups_' + contest.id).removeClass('tabSelected');
                                _this.spcInfo.hidePage();
                                _this.spcEntries.showPage();
                                _this.spcPrizes.hidePage();
                                _this.spcLineups.hidePage();
                        });
                        $('#conr_tabprizes_' + contest.id).click(function (evt) {
                                evt.stopPropagation();
                                $('#conr_tabinfo_' + contest.id).removeClass('tabSelected');
                                $('#conr_tabentries_' + contest.id).removeClass('tabSelected');
                                $('#conr_tabprizes_' + contest.id).addClass('tabSelected');
                                $('#conr_tablineups_' + contest.id).removeClass('tabSelected');
                                _this.spcInfo.hidePage();
                                _this.spcEntries.hidePage();
                                _this.spcPrizes.showPage();
                                _this.spcLineups.hidePage();
                        });
                        $('#conr_tablineups_' + contest.id).click(function (evt) {
                                evt.stopPropagation();
                                $('#conr_tabinfo_' + contest.id).removeClass('tabSelected');
                                $('#conr_tabentries_' + contest.id).removeClass('tabSelected');
                                $('#conr_tabprizes_' + contest.id).removeClass('tabSelected');
                                $('#conr_tablineups_' + contest.id).addClass('tabSelected');
                                _this.spcInfo.hidePage();
                                _this.spcEntries.hidePage();
                                _this.spcPrizes.hidePage();
                                _this.spcLineups.showPage();
                        });

                        var cell = $('#conr_details_' + contest.id);

			// hides entries for H2H anonymous
			if (_this.parameters.contest.contestType.abbr == 'ANON'){
				$('#conr_tabentries_' + _this.parameters.contest.id).hide();
			}

                        // show pretty circle counter
                        ruckus.modules.counters.circle.render(cell, "conr_details_chart", contest.remainingTime, null);

                        var tl = new TimelineMax({
                                onComplete: function () {
                                }
                        });
                        tl.to(cell, 0.5, {height: 240});
                });
        };

        ruckus.subpagecontrols.contestrow.prototype.closeDetails = function (value) {
                var cell = $("#conr_details_" + value.id);
                var tl = new TimelineMax({
                        onComplete: function () {
                                val = $("#conr_details_" + value.id);
                                val.remove();
                                $("#conr_details_" + value.id + "_blank").remove();
                        }
                });
                tl.to(cell, 0.5, {height: 0});
        };

        ruckus.subpagecontrols.contestrow.prototype.closeAllDetails = function (keepOpenId) {
                $.each($(".conr_details"), function (key, value) {
                        var closeMe = function () {
                                var tl = new TimelineMax({
                                        onComplete: function () {
                                                value.remove();
                                        }
                                });
                                tl.to(value, 0.5, {height: 0});
                        };
                        if (keepOpenId != undefined) {
                                if (keepOpenId != value.id)
                                        closeMe();
                        } else {
                                closeMe();
                        }
                });
        };

        // INTERVALS & COUNTERS
        ruckus.subpagecontrols.contestrow.prototype.receiveUpdateServerTime = function (data) {
                var _this = this;
                _this.viewdata.servertime = JSON.parse(data);
                _this.viewdata.servertime_updated = ruckus.modules.datetime.now();
                _this.viewdata.contest.remainingTime = ruckus.modules.datetime.diff(_this.viewdata.servertime, _this.viewdata.contest.startTime, 'seconds');
        };
        ruckus.subpagecontrols.contestrow.prototype.updateCounters = function () {
                var _this = this;
                if (!_this.viewdata.contest) return false;

                // decrement the time by one second
                _this.viewdata.contest.remainingTime = _this.viewdata.contest.remainingTime - 1;
                _this.viewdata.contest.formattedstartTime = ruckus.modules.counters.contest.getContestTextCounter(_this.viewdata.contest.startTime, _this.viewdata.contest.remainingTime);

                _this.updateContestTextCounter(_this.viewdata.contest.id, _this.viewdata.contest.startTime, _this.viewdata.contest.remainingTime);
                _this.updateDetailChartCounter(_this.viewdata.contest.id, _this.viewdata.contest.startTime, _this.viewdata.contest.remainingTime);
        };
        ruckus.subpagecontrols.contestrow.prototype.updateContestTextCounter = function (id, startTime, remainingTime) {
                var counter_val = ruckus.modules.counters.contest.getContestTextCounter(startTime, remainingTime);
                $('#conr_starttime_' + id).html(counter_val);
                $('#contestentryrowstarttime_' + id).html(counter_val);
        };
        ruckus.subpagecontrols.contestrow.prototype.updateDetailChartCounter = function (id, startTime, remainingTime) {
                var detail_container = $('#conr_details_' + id);
                if (detail_container.length > 0) {
                        if (detail_container.find(".conr_details_chart").length === 1) {
                                ruckus.modules.counters.circle.update(detail_container, "conr_details_chart", remainingTime);
                        }
                }
        };
        ruckus.subpagecontrols.contestrow.prototype.startIntervals = function () {
                var _this = this;
                if (this.serverTimeInterval === undefined) {
                        this.serverTimeInterval = setInterval(_this.viewrepo.fetchServerTime, 10000);
                        _this.intervals.push(this.serverTimeInterval);
                }
                if (this.counterInterval === undefined) {
                        this.counterInterval = setInterval(_this.updateCounters, 1000);
                        _this.intervals.push(this.counterInterval);
                }
        };

        ruckus.subpagecontrols.contestrow.prototype.unload = function () {
                var _this = this;

                _this.viewrepo.unload();
                _this.destroyControl();
        };

        return ruckus.subpagecontrols.contestrow;
});
	

