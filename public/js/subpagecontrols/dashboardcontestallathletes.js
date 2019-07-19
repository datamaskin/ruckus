// Author: Scott Gay
define([
        "assets/js/subpagecontrols/base.js",
        "assets/js/libraries/jquery.min.js",
//	"assets/js/libraries/underscore-min.js"
        "assets/js/libraries/dust-core.min.js",
        "assets/js/models/contestliveallathletes.js"
], function (Base) {
        ruckus.subpagecontrols.dashboardcontestallathletes = function (parameters) {
                Base.call(this);
                this.init = function () {
                        this.parameters = parameters;
                };
                this.init();
        };

        ruckus.subpagecontrols.dashboardcontestallathletes.prototype = Object.create(Base.prototype);
        ruckus.subpagecontrols.dashboardcontestallathletes.prototype.load = function () {
                var _this = this;
                this.getContainer();
                this.container.addClass('ruckus-pc-dashboardathletes');

                _this.contestLiveAllAthletesModel = new ruckus.models.contestliveallathletes({});
                _this.models.push(_this.contestLiveAllAthletesModel);
                var sub = _this.msgBus.subscribe("model.contestliveallathletes.retrieve", function (data) {
                        sub.unsubscribe();
                        $.each(_this.contestLiveAllAthletesModel.modelData, function (key, value) {
                                value.exposure.formattedTotalEntryFees = _this.formatMoney(value.exposure.totalEntryFees);
                                value.exposure.formattedTotalExposure = _this.formatMoney(value.exposure.totalExposure);
				value.formattedTimePercentage = _this.formatTimePercentage(_this.parameters.contest.league, value.unitsRemaining);
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
                                /*
                                 // FIXME - harded coded data for testing
                                 value.timeline = JSON.parse('[{"athleteSportEventInfoId":1, "timestamp":1405454700000, "description":"Ortiz singles to right field", "fpChange":"+3"},{"athleteSportEventInfoId":2, "timestamp":1405454700000, "description":"Pedroia doubles to deep center field", "fpChange":"+5"}]');
                                 value.stats = JSON.parse('[{"amount":0,"fpp":0,"name":"Double"},{"amount":0,"fpp":0,"name":"Triple"},{"amount":0,"fpp":0,"name":"Home Run"},{"amount":0,"fpp":0,"name":"Single"},{"amount":0,"fpp":0,"name":"Run Batted In"},{"amount":0,"fpp":0,"name":"Run"},{"amount":0,"fpp":0,"name":"Walk"},{"amount":0,"fpp":0,"name":"Hit By Pitch"},{"amount":0,"fpp":0,"name":"Stolen Base"},{"amount":0,"fpp":0,"name":"Caught Stealing"}]');
                                 */
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
                        dust.render('dusttemplates/dashboardcontestallathletes-tpl', {data: _this.contestLiveAllAthletesModel.modelData}, function (err, out) {
                                _this.container.html(out);
                                _this.addScrollBars();

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
                        });
                });
                _this.subscriptions.push(sub);
                _this.contestLiveAllAthletesModel.fetch({contestId: _this.parameters.contest.contestId});
        };

        ruckus.subpagecontrols.dashboardcontestallathletes.prototype.unload = function () {
                this.destroyControl();
        };

        return ruckus.subpagecontrols.dashboardcontestallathletes;
});
	

