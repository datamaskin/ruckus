// Author: Scott Gay
define([
        "assets/js/pagecontrols/base.js",
        "assets/js/libraries/jquery.min.js",
        "assets/js/models/contestliveoverviewhistory.js",
        "assets/js/libraries/jquery.flot.min.js",
        'assets/js/modules/navigation.js'
], function (Base) {
        ruckus.pagecontrols.dashboardhistory = function (parameters) {
                Base.call(this);
                this.init = function () {
                        var _this = this;
                        _this.parameters = parameters;
                };

//                _.bindAll(this, "updateServerTime", "updateCounters");

                this.init();
        };

        ruckus.pagecontrols.dashboardhistory.prototype = Object.create(Base.prototype);

        ruckus.pagecontrols.dashboardhistory.prototype.load = function () {
                var _this = this;
                this.getContainer();
                this.container.addClass('ruckus-pc-dashboard');

                this.require_template('dashboardhistory-tpl');
                dust.render('dusttemplates/dashboardhistory-tpl', {}, function (err, out) {
                        _this.container.html(out);
			_this.addScrollBars();
                        _this.cellContainer = $('#dshm_cellcontainer');
                        // FIXME - needs the correct model once created
                        _this.contestLiveOverviewHistoryModel = new ruckus.models.contestliveoverviewhistory({});
                        _this.models.push(_this.contestLiveOverviewHistoryModel);
                        var sub2 = _this.msgBus.subscribe("model.contestliveoverviewhistory.all", function (data) {
                                sub2.unsubscribe();
				_this.prepareViewData();
				if (_this.contestLiveOverviewHistoryModel.modelData.contests.length == 0)
					$('#dshm_cellcontainer').html('You do not currently have any closed contests.').addClass('noContest');		
                                $.each(_this.contestLiveOverviewHistoryModel.modelData.contests, function (key, value) {
                                        if (value.contestState == 'history') {
                                                _this.rendercellClosed(value);
					}
                                });

//                                _this.startIntervals();
                        });
                        _this.contestLiveOverviewHistoryModel.fetch({});

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
        };

        ruckus.pagecontrols.dashboardhistory.prototype.prepareViewData = function(callback){
                var _this = this;

                _this.log({type: 'api', data: _this.contestLiveOverviewHistoryModel.modelData.contests, msg: 'CONTEST HISTORY DATA'});
                $.each(_this.contestLiveOverviewHistoryModel.modelData.contests, function (key, value) {
                        value.formattedPayout = _this.formatMoney(value.payout);
                        value.formattedProjectedPayout = _this.formatMoney(value.projectedPayout);
			value.formattedPosition = _this.formatPlace(value.position);
                        value.formattedBuyinAmount = _this.formatMoney(value.entryFee);
			value.formattedStartTime = _this.formatTimeActual(value.startTime);
                        value.timePercentage = _this.formatTimePercentage(value.league, value.unitsRemaining);
                        var contestname = _this.formatContestName(value, '2line');
                        value.line1 = contestname.line1;
                        value.line2 = contestname.line2;
                });

                if(callback) callback();
        };

        ruckus.pagecontrols.dashboardhistory.prototype.rendercellClosed = function (contest) {
                var _this = this;
                this.require_template('dashboardcellhistory-tpl');
                dust.render('dusttemplates/dashboardcellhistory-tpl', contest, function (err, out) {
                        $('<div>').html(out).appendTo(_this.cellContainer);
			_this.addScrollBars();
			
			if (contest.payout > 0){
				$('#projected_'+contest.contestId + '_' + contest.lineupId).removeClass('projectedHistory');
				$('#projected_'+contest.contestId + '_' + contest.lineupId).addClass('up');
			}

                        $('#dshm_cell_' + contest.contestId + '_' + contest.lineupId).bind('click', function (evt) {
                                evt.stopPropagation();
                                _this.hidePage();
                                ruckus.modules.navigation.toRoute('dashboardcontest/' + contest.contestId);


//                                var dashboardcontest = new ruckus.pagecontrols.dashboardcontest({
//                                        'container': _this.parameters.container,
//                                        'dashboard': _this,
//                                        'contestId': contest.contestId,
//                                        'contestLiveOverviewModel': _this.contestLiveOverviewHistoryModel
//                                });
//                                dashboardcontest.load();
//                                _this.controls.push(dashboardcontest);
//				location = "/app?id="+contest.id+"#dashboardcontest";
                        });
                });
        };
/*
        ruckus.pagecontrols.dashboardhistory.prototype.updateCounters = function(){
                var _this = this;
                if(!_this.contestLiveOverviewModel) return false;
                if(!_this.contestLiveOverviewModel.modelData) return false;

                $.each( _this.contestLiveOverviewModel.modelData.contests, function(key, val){
                        // decrement the time by one second
                        val.remainingTime = val.remainingTime - 1;

                        _this.updateDetailChartCounter(val.contestId, val.startTime, val.remainingTime);
                });
        };

        ruckus.pagecontrols.dashboardhistory.prototype.updateDetailChartCounter = function(id, startTime, remainingTime){
                var detail_container = $('#dshm_cell_' + id);
                if(detail_container.length > 0){
                        if(detail_container.find(".dshm_cell_upcoming_chart").length === 1) {
                                ruckus.modules.counters.circle.update(detail_container, "dshm_cell_upcoming_chart", remainingTime);
                        }
                }
        };

        ruckus.pagecontrols.dashboardhistory.prototype.updateServerTime = function(server_time){
                var _this = this;

                _this.server_time = server_time;
                _this.server_time_updated = ruckus.modules.datetime.now();

                if(!_this.contestLiveOverviewModel) return false;
                if(!_this.contestLiveOverviewModel.modelData) return false;

                $.each( _this.contestLiveOverviewModel.modelData.contests, function(key, val) {
                        // set remaining times
                        val.remainingTime = ruckus.modules.datetime.diff(_this.server_time, val.startTime, 'seconds');
                });
        };

        ruckus.pagecontrols.dashboardhistory.prototype.startIntervals = function(){
                var _this = this;
                var serverTimeInterval = setInterval(ruckus.modules.datetime.getServerTimeStamp(_this.updateServerTime), 60000);
                _this.intervals.push(serverTimeInterval);

                var counterInterval = setInterval(_this.updateCounters, 1000);
                _this.intervals.push(counterInterval);
        };
*/
        ruckus.pagecontrols.dashboardhistory.prototype.unload = function () {
                this.destroyControl();
        };

        return ruckus.pagecontrols.dashboardhistory;
});


