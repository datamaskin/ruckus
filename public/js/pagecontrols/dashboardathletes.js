// Author: Scott Gay
define([
        "assets/js/pagecontrols/base.js",
        "assets/js/libraries/jquery.min.js",
        "assets/js/viewrepositories/vr.dashboardathletes.js",
        "assets/js/models/contestliveoverview.js",
        "assets/js/models/liveallathletes.js"
], function (Base) {
        ruckus.pagecontrols.dashboardathletes = function (parameters) {
                var _this = this;
                Base.call(_this);
                if (parameters.mockdata) {
                        _this.mockdata = parameters.mockdata;
                } else {
                        _this.mockdata = false;
                }
                _this.parameters = parameters;

                // INITIALIZE THE VIEW REPOSITORY
                _this.viewrepo = new ruckus.views.repositories.dashboardathletes(parameters);
                _this.viewdata = {
                        contestId: parameters.contestId,
                        contest: null
                };

                _.bindAll(this,
                        'onEntryUnitsRemainingUpdate',
                        'onUpdateEntryFppUpdate',
                        'onAthleteStatsUpdate',
                        'onAthleteFppUpdate',
                        'onAthleteIndicatorUpdate',
                        'onAthleteUnitsRemainingUpdate',
                        'onAthleteTimelineUpdate',
                        'onSportEventUpdate');
        };

        ruckus.pagecontrols.dashboardathletes.prototype = Object.create(Base.prototype);

        ruckus.pagecontrols.dashboardathletes.prototype.load = function () {
                var _this = this;
                this.getContainer();
                this.container.addClass('ruckus-pc-dashboardathletes');

                // DATA EVENT LISTENERS
                _this.subscriptions.push(_this.msgBus.subscribe(ruckus.pubsub.subscriptions.view.dashboardathletes.contest.entry.fpp, _this.onUpdateEntryFppUpdate));
                _this.subscriptions.push(_this.msgBus.subscribe(ruckus.pubsub.subscriptions.view.dashboardathletes.contest.entry.unitsremaining, _this.onEntryUnitsRemainingUpdate));
                _this.subscriptions.push(_this.msgBus.subscribe(ruckus.pubsub.subscriptions.view.dashboardathletes.contest.athlete.statsupdate, _this.onAthleteStatsUpdate));
                _this.subscriptions.push(_this.msgBus.subscribe(ruckus.pubsub.subscriptions.view.dashboardathletes.contest.athlete.timeline, _this.onAthleteFppUpdate));
                _this.subscriptions.push(_this.msgBus.subscribe(ruckus.pubsub.subscriptions.view.dashboardathletes.contest.athlete.unitsremaining, _this.onAthleteIndicatorUpdate));
                _this.subscriptions.push(_this.msgBus.subscribe(ruckus.pubsub.subscriptions.view.dashboardathletes.contest.athlete.timeline, _this.onAthleteTimelineUpdate));
                _this.subscriptions.push(_this.msgBus.subscribe(ruckus.pubsub.subscriptions.view.dashboardathletes.contest.athlete.unitsremaining, _this.onAthleteUnitsRemainingUpdate));
                _this.subscriptions.push(_this.msgBus.subscribe(ruckus.pubsub.subscriptions.view.dashboardathletes.contest.sportevent.update, _this.onSportEventUpdate));
                _this.viewrepo.fetch();

                this.require_template('dashboardathletes-tpl');
                dust.render('dusttemplates/dashboardathletes-tpl', {}, function (err, out) {
                        _this.container.html(out);
                        _this.addScrollBars();
                        _this.getData();
                        $('#dashnavc').bind('click', function (evt) {
                                evt.stopPropagation();
                                window.location.href = "#dashboard";
//                                _this.msgBus.publish("nav.change", { page: 'dashboard' });
                        });
                        $('#dashnava').bind('click', function (evt) {
                                evt.stopPropagation();
                                window.location.href = "#dashboardathletes";
//                                _this.msgBus.publish("nav.change", { page: 'dashboardathletes' });
                        });
                        $('#dashnavl').bind('click', function (evt) {
                                evt.stopPropagation();
                                window.location.href = "#dashboardlineups";
//                                _this.msgBus.publish("nav.change", { page: 'dashboardlineups' });
                        });
                        $('#dashnavh').bind('click', function (evt) {
                                evt.stopPropagation();
                                window.location.href = "#dashboardhistory";
//                                _this.msgBus.publish("nav.change", { page: 'dashboardhistory' });
                        });


                });

                this.renderAthletes();
        };

        ruckus.pagecontrols.dashboardathletes.prototype.getData = function () {
                var _this = this;
                _this.contestLiveOverviewModel = new ruckus.models.contestliveoverview({});
                _this.models.push(_this.contestLiveOverviewModel);
                var sub2 = _this.msgBus.subscribe("model.contestliveoverview.all", function (data) {
                        sub2.unsubscribe();
                        $.each(_this.contestLiveOverviewModel.modelData.contests, function (key, value) {
                                value.formattedPayout = _this.formatMoney(value.payout);
                                value.formattedProjectedPayout = _this.formatMoney(value.projectedPayout);
                                value.formattedBuyinAmount = _this.formatMoney(value.entryFee);
                                value.timePercentage = _this.formatTimePercentage(value.league, value.unitsRemaining);
                        });
                        _this.log({type: 'api', data: _this.contestLiveOverviewModel.modelData.contests, msg: 'CONTEST LIVE DATA'});
                        _this.render();
                });
                _this.subscriptions.push(sub2);
                _this.contestLiveOverviewModel.fetch({});
        };

        ruckus.pagecontrols.dashboardathletes.prototype.render = function () {
                var _this = this;
                // list
                var dashboardcontestlist = new ruckus.subpagecontrols.dashboardcontestlist({
                        container: $('#dhao_list'),
                        contestLiveOverviewModel: _this.contestLiveOverviewModel
                });
                dashboardcontestlist.load();
                _this.controls.push(dashboardcontestlist);
        };

        ruckus.pagecontrols.dashboardathletes.prototype.renderAthletes = function () {
                var _this = this;
                _this.liveAllAthletesModel = new ruckus.models.liveallathletes({});
                _this.models.push(_this.liveAllAthletesModel);
                var sub = _this.msgBus.subscribe("model.liveallathletes.retrieve", function (data) {
                        sub.unsubscribe();
                        $.each(_this.liveAllAthletesModel.modelData, function (key, value) {
                                value.exposure.formattedTotalEntryFees = _this.formatMoney(value.exposure.totalEntryFees);
                                value.exposure.formattedTotalExposure = _this.formatMoney(value.exposure.totalExposure);
                                // FIXME - Athlete needs to know what league its in so this isn't hardcoded.
                                value.formattedTimePercentage = _this.formatTimePercentage('NFL', value.unitsRemaining);
                                $.each(value.exposure.contestTypes, function (k, v) {
                                        v.formattedEntryFees = _this.formatMoney(v.entryFees);
                                        if (v.abbr == 'H2H')
                                                v.contestName = 'H2H';
                                        else
                                                v.contestName = v.type;
                                });
                                $.each(value.ranks, function (k, v) {
                                        v.contestName = _this.formatContestName(v, '1linesimple').line1;
                                        v.formattedRank = _this.formatPlace(v.rank);
                                });

                                $.each(value.timeline, function (k, v) {
                                        v.formattedTimestamp = _this.formatTimeActual(v.timestamp);
                                });
                                switch (value.indicator) {
                                        case 0 :
                                                value.indicatorClass = 'dot';
                                                break;
                                        case 1 :
                                                value.indicatorClass = 'dotPlay';
                                                break;
                                        case 2 :
                                                value.indicatorClass = 'dotRedzone';
                                                break;
                                }
                        });
                        _this.require_template('dashboardcontestallathletes-tpl');
                        dust.render('dusttemplates/dashboardcontestallathletes-tpl', {data: _this.liveAllAthletesModel.modelData}, function (err, out) {
                                $('#dhao_athletes').html(out);
                                _this.addScrollBars();
                                if (_this.liveAllAthletesModel.modelData.length == 0) {
                                        $('#dhao_athletes').html('You do not currently have any athletes in active contests.').addClass('noContest');
                                        $('#dhao_list').hide();
                                }


                                $('.dhao_tabplayerfeed').bind('click', function (evt) {
                                        evt.stopPropagation();
                                        $('.dhao_tab_' + evt.delegateTarget.id.split('_')[2]).removeClass('tabSelected');
                                        $('.dhao_tab_' + evt.delegateTarget.id.split('_')[2]).addClass('tab');
                                        $('#dhao_tabplayerfeed_' + evt.delegateTarget.id.split('_')[2]).addClass('tabSelected');
                                        $('#dhao_tabplayerfeed_' + evt.delegateTarget.id.split('_')[2]).removeClass('tab');
                                        $('#dhao_playerfeed_' + evt.delegateTarget.id.split('_')[2]).show();
                                        $('#dhao_playerexposure_' + evt.delegateTarget.id.split('_')[2]).hide();
                                        $('#dhao_playercontests_' + evt.delegateTarget.id.split('_')[2]).hide();
                                        $('#dhao_stats_' + evt.delegateTarget.id.split('_')[2]).hide();
                                });
                                $('.dhao_tabplayerexposure').bind('click', function (evt) {
                                        evt.stopPropagation();
                                        $('.dhao_tab_' + evt.delegateTarget.id.split('_')[2]).removeClass('tabSelected');
                                        $('.dhao_tab_' + evt.delegateTarget.id.split('_')[2]).addClass('tab');
                                        $('#dhao_tabplayerexposure_' + evt.delegateTarget.id.split('_')[2]).addClass('tabSelected');
                                        $('#dhao_tabplayerexposure_' + evt.delegateTarget.id.split('_')[2]).removeClass('tab');
                                        $('#dhao_playerfeed_' + evt.delegateTarget.id.split('_')[2]).hide();
                                        $('#dhao_playerexposure_' + evt.delegateTarget.id.split('_')[2]).show();
                                        $('#dhao_playercontests_' + evt.delegateTarget.id.split('_')[2]).hide();
                                        $('#dhao_stats_' + evt.delegateTarget.id.split('_')[2]).hide();
                                });
                                $('.dhao_tabplayercontests').bind('click', function (evt) {
                                        evt.stopPropagation();
                                        $('.dhao_tab_' + evt.delegateTarget.id.split('_')[2]).removeClass('tabSelected');
                                        $('.dhao_tab_' + evt.delegateTarget.id.split('_')[2]).addClass('tab');
                                        $('#dhao_tabplayercontests_' + evt.delegateTarget.id.split('_')[2]).addClass('tabSelected');
                                        $('#dhao_tabplayercontests_' + evt.delegateTarget.id.split('_')[2]).removeClass('tab');
                                        $('#dhao_playerfeed_' + evt.delegateTarget.id.split('_')[2]).hide();
                                        $('#dhao_playerexposure_' + evt.delegateTarget.id.split('_')[2]).hide();
                                        $('#dhao_playercontests_' + evt.delegateTarget.id.split('_')[2]).show();
                                        $('#dhao_stats_' + evt.delegateTarget.id.split('_')[2]).hide();
                                });
                                $('.dhao_tabstats').bind('click', function (evt) {
                                        evt.stopPropagation();
                                        $('.dhao_tab_' + evt.delegateTarget.id.split('_')[2]).removeClass('tabSelected');
                                        $('.dhao_tab_' + evt.delegateTarget.id.split('_')[2]).addClass('tab');
                                        $('#dhao_tabstats_' + evt.delegateTarget.id.split('_')[2]).addClass('tabSelected');
                                        $('#dhao_tabstats_' + evt.delegateTarget.id.split('_')[2]).removeClass('tab');
                                        $('#dhao_playerfeed_' + evt.delegateTarget.id.split('_')[2]).hide();
                                        $('#dhao_playerexposure_' + evt.delegateTarget.id.split('_')[2]).hide();
                                        $('#dhao_playercontests_' + evt.delegateTarget.id.split('_')[2]).hide();
                                        $('#dhao_stats_' + evt.delegateTarget.id.split('_')[2]).show();
                                });
                                $('.contest_entry').bind('click', function (evt) {
                                        evt.stopPropagation();

                                        var contestId = evt.delegateTarget.id.split('_')[2];
                                        var contestState = evt.delegateTarget.id.split('_')[3];
                                        if (contestState) {
                                                switch (contestState) {
                                                        case 'active':
                                                                ruckus.modules.navigation.toRoute('dashboardcontest/' + contestId);
                                                                break;
                                                        default:
                                                                ruckus.modules.navigation.toRoute('dashboard/' + contestId);
                                                                break;
                                                }
                                        }

                                });

                        });
                });
                _this.subscriptions.push(sub);
                _this.liveAllAthletesModel.fetch({});
        };


        /*
         // ATHLETES
         ruckus.pagecontrols.dashboardathletes.prototype.onAthleteIndicatorUpdate = function(athlete){
         var _this = this;
         _this.log({type: 'general', data: athlete, msg: 'DASHBOARD EVENT PAGE CONTROL > onAthleteIndicatorUpdate'});
         // athletes
         var ath = $("div[data-socket='ath_indicator_"+athlete.athleteSportEventInfoId+"']");
         div.removeClass();
         switch (athlete.indicator) {
         case 0 :
         ath.addClass('dot');
         break;
         case 1 :
         ath.addClass('dotPlay');
         break;
         case 2 :
         ath.addClass('dotRedzone');
         break;
         }
         };
         ruckus.pagecontrols.dashboardathletes.prototype.onAthleteFppUpdate = function(athlete){
         var _this = this;
         _this.log({type: 'general', data: athlete, msg: 'DASHBOARD EVENT PAGE CONTROL > onAthleteFppUpdate'});
         // athlete
         $("div[data-socket='ath_fpp_"+athlete.athleteSportEventInfoId+"']").html(athlete.fpp);

         };
         ruckus.pagecontrols.dashboardathletes.prototype.onAthleteStatsUpdate = function(athlete){
         var _this = this;
         _this.log({type: 'general', data: athlete, msg: 'DASHBOARD EVENT PAGE CONTROL > onAthleteStatsUpdate'});
         };
         ruckus.pagecontrols.dashboardathletes.prototype.onAthleteUnitsRemainingUpdate = function(athlete){
         var _this = this;
         _this.log({type: 'general', data: data, msg: 'DASHBOARD EVENT PAGE CONTROL > onAthleteUnitsRemainingUpdate'});
         };
         ruckus.pagecontrols.dashboardathletes.prototype.onAthleteTimelineUpdate = function(athlete){
         var _this = this;
         _this.log({type: 'general', data: athlete, msg: 'DASHBOARD EVENT PAGE CONTROL > onAthleteTimelineUpdate'});
         };
         // SPORT EVENT
         ruckus.pagecontrols.dashboardathletes.prototype.onSportEventUpdate = function(sportevent){
         var _this = this;
         _this.log({type: 'general', data: sportevent, msg: 'DASHBOARD EVENT PAGE CONTROL > onSportEventUpdate'});
         // athlete
         $("div[data-socket='ath_score_"+sportevent.homeId+"']").html(sportevent.homeScore);
         $("div[data-socket='ath_score_"+sportevent.awayId+"']").html(sportevent.awayScore);
         };
         */
        ruckus.pagecontrols.dashboardathletes.prototype.onEntryUnitsRemainingUpdate = function (entry) {
                var _this = this;
                _this.log({type: 'general', data: entry, msg: 'DASHBOARD EVENT PAGE CONTROL > onEntryUnitsRemainingUpdate'});
        };
        ruckus.pagecontrols.dashboardathletes.prototype.onUpdateEntryFppUpdate = function (entry) {
                var _this = this;
                _this.log({type: 'general', data: entry, msg: 'DASHBOARD EVENT PAGE CONTROL > onUpdateEntryFppUpdate'});
        };
        ruckus.pagecontrols.dashboardathletes.prototype.onAthleteIndicatorUpdate = function (athlete) {
                var _this = this;
                _this.log({type: 'general', data: athlete, msg: 'DASHBOARD EVENT PAGE CONTROL > onAthleteIndicatorUpdate'});
                // lineup athletes
                var ath = $("div[data-socket='ath_indicator_" + athlete.athleteSportEventInfoId + "']");
                ath.removeClass('dot').removeClass('dotPlay').removeClass('dotRedzone');
                switch (athlete.indicator) {
                        case 0 :
                                ath.addClass('dot');
                                break;
                        case 1 :
                                ath.addClass('dotPlay');
                                break;
                        case 2 :
                                ath.addClass('dotRedzone');
                                break;
                }
        };
        ruckus.pagecontrols.dashboardathletes.prototype.onAthleteFppUpdate = function (athlete) {
                var _this = this;
                _this.log({type: 'general', data: athlete, msg: 'DASHBOARD EVENT PAGE CONTROL > onAthleteFppUpdate'});
                // athlete
                $("div[data-socket='ath_fpp_" + athlete.athleteSportEventInfoId + "']").html(athlete.fpp);
        };
        ruckus.pagecontrols.dashboardathletes.prototype.onAthleteStatsUpdate = function (athlete) {
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
                        $("div[data-socket='ath_statamount_" + y.id + "_" + athlete.athleteSportEventInfoId + "']").html(y.amount);
                        $("div[data-socket='ath_statfpp_" + y.id + "_" + athlete.athleteSportEventInfoId + "']").html(y.fpp);
                });


        };
        ruckus.pagecontrols.dashboardathletes.prototype.onAthleteUnitsRemainingUpdate = function (athlete) {
                var _this = this;
                _this.log({type: 'general', data: athlete, msg: 'DASHBOARD EVENT PAGE CONTROL > onAthleteUnitsRemainingUpdate'});
                // athlete
                // FIXME - Athlete needs to know what league they are a part of so NFL isn't hardcoded
                $("div[data-socket='ath_timePercentage_" + athlete.athleteSportEventInfoId + "']").css("width", _this.formatTimePercentage('NFL', athlete.unitsRemaining) + "%");
        };
        ruckus.pagecontrols.dashboardathletes.prototype.onAthleteTimelineUpdate = function (athlete) {
                var _this = this;
                _this.log({type: 'general', data: athlete, msg: 'DASHBOARD EVENT PAGE CONTROL > onAthleteTimelineUpdate'});

                // athlete details
                $.each(athlete.timeline, function (k, v) {
                        v.formattedTimestamp = _this.formatTimeActual(v.timestamp);
                        _this.require_template('dashboardcontestallathletestimeline-tpl');
                        dust.render('dusttemplates/dashboardcontestallathletestimeline-tpl', v, function (err, out) {
                                var container = $("div[data-socket='ath_timeline_" + athlete.athleteSportEventInfoId + "']");
                                container.prepend(out);
                        });
                });
        };
        // SPORT EVENT
        ruckus.pagecontrols.dashboardathletes.prototype.onSportEventUpdate = function (sportevent) {
                var _this = this;
                _this.log({type: 'general', data: sportevent, msg: 'DASHBOARD EVENT PAGE CONTROL > onSportEventUpdate'});
                // athlete
                $("div[data-socket='ath_score_" + sportevent.homeId + "']").html(sportevent.homeScore);
                $("div[data-socket='ath_score_" + sportevent.awayId + "']").html(sportevent.awayScore);
        };

        ruckus.pagecontrols.dashboardathletes.prototype.unload = function () {
                this.viewrepo.unload();
                delete this.viewrepo;
                this.destroyControl();
        };

        return ruckus.pagecontrols.dashboardathletes;
});


