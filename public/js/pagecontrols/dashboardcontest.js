define([
        'rg_page_base',
        "assets/js/models/contestdetailranks.js",
        "assets/js/models/contestidrestful.js",
        "assets/js/models/contestliveoverview.js",
        "assets/js/viewrepositories/vr.dashboardcontest.js",
        "assets/js/subpagecontrols/dashboardhistoryheader.js",
        "assets/js/subpagecontrols/dashboardcontestranks.js",
        "assets/js/subpagecontrols/dashboardcontestgraph1.js",
        "assets/js/subpagecontrols/dashboardcontestgraph2.js",
        "assets/js/subpagecontrols/dashboardcontestlist.js",
        "assets/js/subpagecontrols/dashboardcontestathletes.js",
        "assets/js/subpagecontrols/dashboardcontestteamfeed.js",
        "assets/js/subpagecontrols/dashboardcontestchat.js",
        "assets/js/subpagecontrols/dashboardcontestathletedetail.js",
        "assets/js/subpagecontrols/dashboardcontestpercentowned.js",
        "assets/js/subpagecontrols/dashboardcontestallathletes.js"
], function (Base) {
        ruckus.pagecontrols.dashboardcontest = function (parameters) {
                var _this = this;
                Base.call(_this);
                if (!parameters.contestId) {
                        _this.log({type: 'error', level: 1, data: parameters, msg: 'Dashboard contest does not have a valid contestID'});
                        return false;
                }
                if (parameters.mockdata) {
                        _this.mockdata = parameters.mockdata;
                } else {
                        _this.mockdata = false;
                }
                _this.parameters = parameters;
                _this.viewrepo = new ruckus.views.repositories.dashboardcontest(parameters);
                _this.viewdata = {
                        contestId: parameters.contestId,
                        contest: null
                };

                _.bindAll(this,
                        'onPageLoad',
                        'onEntryUnitsRemainingUpdate',
                        'onUpdateEntryFppUpdate',
                        'onAthleteStatsUpdate',
                        'onAthleteFppUpdate',
                        'onAthleteIndicatorUpdate',
                        'onAthleteUnitsRemainingUpdate',
                        'onAthleteTimelineUpdate',
                        'onSportEventUpdate',
                        'onAthletePlaceUpdate',
                        'onUpdateServerTime');
        };
        ruckus.pagecontrols.dashboardcontest.prototype = Object.create(Base.prototype);
        ruckus.pagecontrols.dashboardcontest.prototype.load = function () {
                var _this = this;

                // DATA EVENT LISTENERS
                _this.subscriptions.push(_this.msgBus.subscribe(ruckus.pubsub.subscriptions.view.dashboardcontest.put.contest, _this.onPageLoad));
                _this.subscriptions.push(_this.msgBus.subscribe(ruckus.pubsub.subscriptions.view.dashboardcontest.servertime, _this.onUpdateServerTime));
                _this.subscriptions.push(_this.msgBus.subscribe(ruckus.pubsub.subscriptions.view.dashboardcontest.contest.entry.fpp, _this.onUpdateEntryFppUpdate));
                _this.subscriptions.push(_this.msgBus.subscribe(ruckus.pubsub.subscriptions.view.dashboardcontest.contest.entry.unitsremaining, _this.onEntryUnitsRemainingUpdate));
                _this.subscriptions.push(_this.msgBus.subscribe(ruckus.pubsub.subscriptions.view.dashboardcontest.contest.athlete.statsupdate, _this.onAthleteStatsUpdate));
                _this.subscriptions.push(_this.msgBus.subscribe(ruckus.pubsub.subscriptions.view.dashboardcontest.contest.athlete.timeline, _this.onAthleteFppUpdate));
                _this.subscriptions.push(_this.msgBus.subscribe(ruckus.pubsub.subscriptions.view.dashboardcontest.contest.athlete.unitsremaining, _this.onAthleteIndicatorUpdate));
                _this.subscriptions.push(_this.msgBus.subscribe(ruckus.pubsub.subscriptions.view.dashboardcontest.contest.athlete.timeline, _this.onAthleteTimelineUpdate));
                _this.subscriptions.push(_this.msgBus.subscribe(ruckus.pubsub.subscriptions.view.dashboardcontest.contest.athlete.unitsremaining, _this.onAthleteUnitsRemainingUpdate));
                _this.subscriptions.push(_this.msgBus.subscribe(ruckus.pubsub.subscriptions.view.dashboardcontest.contest.sportevent.update, _this.onSportEventUpdate));
                _this.subscriptions.push(_this.msgBus.subscribe(ruckus.pubsub.subscriptions.view.dashboardcontest.contest.entry.all, _this.onAthletePlaceUpdate));

                _this.viewrepo.fetch();

                // FIXME - We should move the view logic into a "render" function to be consistent.
                _this.getContainer();
                _this.container.addClass('ruckus-pc-dashboardcontest');
                _this.contestModel = {};
        };

        ruckus.pagecontrols.dashboardcontest.prototype.onPageLoad = function (contest) {
                var _this = this;

                _this.contestModel.modelData = contest;
                _this.contestModel.modelData.contestId = _this.viewdata.contestId;

                this.require_template('dashboardcontest-tpl');
                dust.render('dusttemplates/dashboardcontest-tpl', {}, function (err, out) {
                        _this.container.html(out);
                        _this.addScrollBars();

                        // athletes
                        var dashboardcontestathletes = new ruckus.subpagecontrols.dashboardcontestathletes({
                                container: $('#dshc_athletes'),
                                contest: _this.contestModel.modelData
                        });
                        dashboardcontestathletes.load();
                        _this.controls.push(dashboardcontestathletes);

                        _this.contestDetailRanksModel = new ruckus.models.contestdetailranks({});
                        _this.models.push(_this.contestDetailRanksModel);

                        var sub = _this.msgBus.subscribe("model.contestdetailranks.retrieve", function (data) {
                                sub.unsubscribe();
                                // ranks
                                var dashboardcontestranks = new ruckus.subpagecontrols.dashboardcontestranks({
                                        container: $('#dshc_ranks'),
                                        dashboardcontest: _this,
                                        contest: _this.contestModel.modelData,
                                        contestdetailranks: _this.contestDetailRanksModel.modelData
                                });
                                dashboardcontestranks.load();
                                _this.controls.push(dashboardcontestranks);

                                //FIXME:  Not sure if/how this will work with the multi-entry contests.
                                var my_lineup = null;
                                $.each(_this.contestDetailRanksModel.modelData, function(key, lineup){
                                        if(lineup.isMe){
                                                my_lineup = lineup;
                                        }
                                });

                                if(my_lineup){
                                        _this.contestModel.modelData.position = my_lineup.pos;
                                        _this.contestModel.modelData.capacity = _this.contestModel.modelData.currentEntries;
                                        _this.contestModel.modelData.fpp = my_lineup.fpp;
                                        _this.contestModel.modelData.formattedPayout = my_lineup.prize;
                                        if (_this.contestModel.modelData.contestState == "complete" || _this.contestModel.modelData.contestState == "history") {
                                                var dashboardhistoryheader = new ruckus.subpagecontrols.dashboardhistoryheader({
                                                        container: $('#dshc_historyheader'),
                                                        contest: _this.contestModel.modelData
                                                });
                                                dashboardhistoryheader.load();
                                                _this.controls.push(dashboardhistoryheader);
                                        }
                                }

                                // FIXME: When all data is pulled from the view repo, this can be removed.
                                _this.msgBus.publish(ruckus.pubsub.subscriptions.view.dashboardcontest.put.allentries, _this.contestDetailRanksModel.modelData);
                                _this.dashboardcontestranksControl = dashboardcontestranks;

                                // graph2
                                var dashboardcontestgraph2 = new ruckus.subpagecontrols.dashboardcontestgraph2({
                                        container: $('#dshc_graph2'),
                                        contest: _this.contestModel.modelData,
                                        contestdetailranks: _this.contestDetailRanksModel.modelData
                                });
                                dashboardcontestgraph2.load();
                                _this.controls.push(dashboardcontestgraph2);

                                // percent owned
                                var dashboardcontestpercentowned = new ruckus.subpagecontrols.dashboardcontestpercentowned({
                                        container: $('#dshc_percentowned'),
                                        contest: _this.contestModel.modelData
                                });
                                dashboardcontestpercentowned.load();
                                _this.controls.push(dashboardcontestpercentowned);

                        });
                        _this.contestDetailRanksModel.fetch({id: _this.viewdata.contestId});

                        // graph1
                        var dashboardcontestgraph1 = new ruckus.subpagecontrols.dashboardcontestgraph1({
                                container: $('#dshc_graph1'),
                                contestId: _this.viewdata.contestId
//				contestdetailranks : _this.contestDetailRanksModel.modelData
                        });
                        dashboardcontestgraph1.load();
                        _this.controls.push(dashboardcontestgraph1);

                        // teamfeed
                        var dashboardcontestteamfeed = new ruckus.subpagecontrols.dashboardcontestteamfeed({
                                container: $('#dshc_teamfeed')
                        });
                        dashboardcontestteamfeed.load();
                        _this.controls.push(dashboardcontestteamfeed);

                        // athlete detail
                        var dashboardcontestathletedetail = new ruckus.subpagecontrols.dashboardcontestathletedetail({
                                container: $('#dshc_athletedetail'),
                                contest: _this.contestModel.modelData
                        });
                        dashboardcontestathletedetail.load();
                        _this.controls.push(dashboardcontestathletedetail);

                        // chat
                        var dashboardcontestchat = new ruckus.subpagecontrols.dashboardcontestchat({
                                container: $('#dshc_chat'),
                                contest: _this.contestModel.modelData
                        });
                        dashboardcontestchat.load();
                        _this.controls.push(dashboardcontestchat);

                        // list
                        var dashboardcontestlist = new ruckus.subpagecontrols.dashboardcontestlist({
                                container: $('#dshc_list'),
                                contestLiveOverviewModel: _this.contestLiveOverviewModel,
                                contest: _this.contestModel.modelData
                        });
                        dashboardcontestlist.load();
                        _this.controls.push(dashboardcontestlist);

                        // all athletes in contest
                        var dashboardallathletes = new ruckus.subpagecontrols.dashboardcontestallathletes({
                                container: $('#dshc_athletes_container'),
                                contest: _this.contestModel.modelData
                        });
                        dashboardallathletes.load();
                        _this.controls.push(dashboardallathletes);

                        $('#dshc_header_contest').bind('click', function (evt) {
                                evt.stopPropagation();
                                $('#dshc_contest_container').show();
                                $('#dshc_athletes_container').hide();
                        });
                        $('#dshc_header_athletes').bind('click', function (evt) {
                                evt.stopPropagation();
                                $('#dshc_contest_container').hide();
                                $('#dshc_athletes_container').show();
                        });
                        $('#dshc_switchgraph1').bind('click', function (evt) {
                                evt.stopPropagation();
                                $('#dshc_graph2').hide();
                                $('#dshc_graph1').show();
                        });
                        $('#dshc_switchgraph2').bind('click', function (evt) {
                                evt.stopPropagation();
                                $('#dshc_graph2').show();
                                $('#dshc_graph1').hide();
                        });

                        $('#dashdrillnavc').bind('click', function (evt) {
                                evt.stopPropagation();
                                window.location.href = "#dashboard";
//                                _this.msgBus.publish("nav.change", { page: 'dashboard' });
                        });
                        $('#dashdrillnava').bind('click', function (evt) {
                                evt.stopPropagation();
                                window.location.href = "#dashboardathletes";
//                                _this.msgBus.publish("nav.change", { page: 'dashboardathletes' });
                        });
                        $('#dashdrillnavl').bind('click', function (evt) {
                                evt.stopPropagation();
                                window.location.href = "#dashboardlineups";
//                                _this.msgBus.publish("nav.change", { page: 'dashboardlineups' });
                        });
                        $('#dashdrillnavh').bind('click', function (evt) {
                                evt.stopPropagation();
                                window.location.href = "#dashboardhistory";
//                                _this.msgBus.publish("nav.change", { page: 'dashboardhistory' });
                        });
                        /*
                         // hookup socket
                         _this.contestDrillinModel = new ruckus.models.contestdrillin({});
                         _this.models.push(_this.contestDrillinModel);
                         var subU = _this.msgBus.subscribe("model.contestdrillin_entry.update", function (data) {
                         _this.consolelog('model.contestdrillin_entry.update');
                         _this.consolelog(data);
                         });
                         _this.subscriptions.push(subU);
                         var sub2 = _this.msgBus.subscribe("model.contestdrillin_sportevent.update", function (data) {
                         _this.consolelog('model.contestdrillin_sportevent.update');
                         _this.consolelog(data);
                         });
                         _this.subscriptions.push(sub2);
                         var sub3 = _this.msgBus.subscribe("model.contestdrillin_athlete.update", function (data) {
                         _this.consolelog('model.contestdrillin_athlete.update');
                         _this.consolelog(data);
                         });
                         _this.subscriptions.push(sub3);
                         _this.contestDrillinModel.fetch({contestId:_this.contestModel.modelData.contestId});
                         */


			// FIXME - for demo purposes
			var checkGraphs = function(){
				if (_this.parameters.graphs){
					$('#lineComingSoon').hide();
					$('#bubbleComingSoon').hide();
					$('#lineHere').show();
					$('#bubbleHere').show();
				}
			};
			setTimeout(checkGraphs, 5000);
                });
        };

        ruckus.pagecontrols.dashboardcontest.prototype.onEntryUnitsRemainingUpdate = function (entry) {
                var _this = this;
                _this.log({type: 'general', data: entry, msg: 'DASHBOARD EVENT PAGE CONTROL > onEntryUnitsRemainingUpdate'});
                // ranks
                $("div[data-socket='r_unitsRemaining_" + entry.entryId + "']").html(entry.unitsRemaining); // .css("color", "red");
                // lineup athletes
                $("div[data-socket='la_lineupTimePercentage_" + entry.entryId + "']").css("width", _this.formatTimePercentageTeam(_this.contestModel.modelData.league, entry.unitsRemaining) + "%");
        };
        ruckus.pagecontrols.dashboardcontest.prototype.onUpdateEntryFppUpdate = function (entry) {
                var _this = this;
                _this.log({type: 'general', data: entry, msg: 'DASHBOARD EVENT PAGE CONTROL > onUpdateEntryFppUpdate'});
                // ranks
                $("div[data-socket='r_fpp_" + entry.entryId + "']").html(entry.fpp);
                // lineup athletes
                // re-ranks all lineups and reset display on ranks table and position in selected lineup
                /*
                 _this.dashboardcontestranksControl.sortMe('fpp');
                 _this.dashboardcontestranksControl.calcPrizes();
                 _this.dashboardcontestranksControl.renderRankList();
                 var pos = undefined;
                 $.each(_this.dashboardcontestranksControl.ranklist, function (key, value) {
                 if (entry.entryId == value.entryId)
                 pos = _this.formatPlace(pos);
                 });
                 $("div[data-socket='la_pos_" + entry.entryId + "']").html(pos);
                 */
                $("div[data-socket='la_fpp_" + entry.entryId + "']").html(entry.fpp);
        };
        // ATHLETES
        ruckus.pagecontrols.dashboardcontest.prototype.onAthleteIndicatorUpdate = function (athlete) {
                var _this = this;
                _this.log({type: 'general', data: athlete, msg: 'DASHBOARD EVENT PAGE CONTROL > onAthleteIndicatorUpdate'});

                // lineup athletes
                var la = $("div[data-socket='la_indicator_" + athlete.athleteSportEventInfoId + "']");
                var ad = $("div[data-socket='ad_indicator_" + athlete.athleteSportEventInfoId + "']");
                var ath = $("div[data-socket='ath_indicator_" + athlete.athleteSportEventInfoId + "']");
                la.removeClass();
                ad.removeClass();
                ath.removeClass('dot').removeClass('dotPlay').removeClass('dotRedzone');
                switch (athlete.indicator) {
                        case 0 :
                                la.addClass('dot');
                                ad.addClass('dot');
                                ath.addClass('dot');
                                break;
                        case 1 :
                                la.addClass('dotPlay');
                                ad.addClass('dotPlay');
                                ath.addClass('dotPlay');
                                break;
                        case 2 :
                                la.addClass('dotRedzone');
                                ad.addClass('dotRedzone');
                                ath.addClass('dotRedzone');
                                break;
                }
        };
        ruckus.pagecontrols.dashboardcontest.prototype.onAthleteFppUpdate = function (athlete) {
                var _this = this;
                _this.log({type: 'general', data: athlete, msg: 'DASHBOARD EVENT PAGE CONTROL > onAthleteFppUpdate'});
                // lineup athletes
                $("div[data-socket='la_fpp_" + athlete.athleteSportEventInfoId + "']").html(athlete.fpp);
                // athlete details
                $("div[data-socket='ad_fpp_" + athlete.athleteSportEventInfoId + "']").html(athlete.fpp);
                // athlete
                $("div[data-socket='ath_fpp_" + athlete.athleteSportEventInfoId + "']").html(athlete.fpp);
        };
        ruckus.pagecontrols.dashboardcontest.prototype.onAthleteStatsUpdate = function (athlete) {
                var _this = this;
                _this.log({type: 'general', data: athlete, msg: 'DASHBOARD EVENT PAGE CONTROL > onAthleteStatsUpdate'});

                // lineup athletes
                var desc = '';
                try {
                        athlete.stats = JSON.parse(athlete.stats);
                } catch (e) {
                }
                $.each(athlete.stats, function (x, y) {
                        desc += y.amount + ' ' + y.abbr + ', ';
                        $("div[data-socket='ad_statamount_" + y.id + "_" + athlete.athleteSportEventInfoId + "']").html(y.amount);
                        $("div[data-socket='ad_statfpp_" + y.id + "_" + athlete.athleteSportEventInfoId + "']").html(y.fpp);
                        $("div[data-socket='ath_statamount_" + y.id + "_" + athlete.athleteSportEventInfoId + "']").html(y.amount);
                        $("div[data-socket='ath_statfpp_" + y.id + "_" + athlete.athleteSportEventInfoId + "']").html(y.fpp);
                });
                desc = desc.substring(0, desc.length - 2);
                $("div[data-socket='la_desc_" + athlete.athleteSportEventInfoId + "']").html(desc);


        };
        ruckus.pagecontrols.dashboardcontest.prototype.onAthleteUnitsRemainingUpdate = function (athlete) {
                var _this = this;
                _this.log({type: 'general', data: athlete, msg: 'DASHBOARD EVENT PAGE CONTROL > onAthleteUnitsRemainingUpdate'});
                // lineup athletes
                $("div[data-socket='la_timePercentage_" + athlete.athleteSportEventInfoId + "']").css("width", _this.formatTimePercentage(_this.contestModel.modelData.league, athlete.unitsRemaining) + "%");
                // athlete details
                $("div[data-socket='ad_timePercentage_" + athlete.athleteSportEventInfoId + "']").css("width", _this.formatTimePercentage(_this.contestModel.modelData.league, athlete.unitsRemaining) + "%");
                // athlete
                $("div[data-socket='ath_timePercentage_" + athlete.athleteSportEventInfoId + "']").css("width", _this.formatTimePercentage(_this.contestModel.modelData.league, athlete.unitsRemaining) + "%");

        };
        ruckus.pagecontrols.dashboardcontest.prototype.onAthleteTimelineUpdate = function (athlete) {
                var _this = this;
                _this.log({type: 'general', data: athlete, msg: 'DASHBOARD EVENT PAGE CONTROL > onAthleteTimelineUpdate'});
                // athlete details
                $.each(athlete.timeline, function (k, v) {
                        v.formattedTimestamp = _this.formatTimeActual(v.timestamp);
                        v.firstInitial = athlete.firstName.substring(0, 1);
                        v.lastName = athlete.lastName;
                        if(v.firstInitial != '' && v.lastName != '') {
                                v.formattedName = v.firstInitial + ' ' + v.lastName;
                        }
                        else {
                                v.formattedName = v.lastName;
                        }
                        _this.require_template('dashboardcontestathletedetailtimeline-tpl');
                        dust.render('dusttemplates/dashboardcontestathletedetailtimeline-tpl', v, function (err, out) {
                                var container = $("div[data-socket='ad_timeline_" + athlete.athleteSportEventInfoId + "']");
                                container.prepend(out);
                        });
                        _this.require_template('dashboardcontestallathletestimeline-tpl');
                        dust.render('dusttemplates/dashboardcontestallathletestimeline-tpl', v, function (err, out) {
                                var container = $("div[data-socket='ath_timeline_" + athlete.athleteSportEventInfoId + "']");
                                container.prepend(out);
                        });

                        // team feed
                        // - test if athlete exists on the lower left lineup athletes box
                        if ($("div[data-socket='la_fpp_" + athlete.athleteSportEventInfoId + "']").length != 0) {
                                $('#teamfeeddatanone').hide();
                                _this.require_template('dashboardcontestteamfeedtimeline-tpl');
                                dust.render('dusttemplates/dashboardcontestteamfeedtimeline-tpl', v, function (err, out) {
                                        var container = $("div[data-socket='tf_timeline']");
                                        container.prepend(out);
                                });
                        }

                });
        };
        ruckus.pagecontrols.dashboardcontest.prototype.onAthletePlaceUpdate = function (list) {
                var _this = this;
                $.each(list, function (k, v) {
                        $("div[data-socket='la_pos_" + v.entryId + "']").html(_this.formatPlace(v.pos));
                });
        };

        // SPORT EVENT
        ruckus.pagecontrols.dashboardcontest.prototype.onSportEventUpdate = function (sportevent) {
                var _this = this;
                _this.log({type: 'general', data: sportevent, msg: 'DASHBOARD EVENT PAGE CONTROL > onSportEventUpdate'});
                // athlete details
                $("div[data-socket='ad_score_" + sportevent.homeId + "']").html(sportevent.homeScore);
                $("div[data-socket='ad_score_" + sportevent.awayId + "']").html(sportevent.awayScore);
                // athlete
                $("div[data-socket='ath_score_" + sportevent.homeId + "']").html(sportevent.homeScore);
                $("div[data-socket='ath_score_" + sportevent.awayId + "']").html(sportevent.awayScore);
        };
        ruckus.pagecontrols.dashboardcontest.prototype.onUpdateServerTime = function (data) {
                var _this = this;
        };

        // TERMINATION
        ruckus.pagecontrols.dashboardcontest.prototype.unload = function () {
                this.viewrepo.unload();
                delete this.viewrepo;
                this.destroyControl();
        };

        return ruckus.pagecontrols.dashboardcontest;
});
