define([
        "assets/js/subpagecontrols/base.js",
        "assets/js/libraries/jquery.min.js",
//	"assets/js/libraries/underscore-min.js"
        "assets/js/libraries/dust-core.min.js"
], function (Base) {
        ruckus.subpagecontrols.dashboardcontestlist = function (parameters) {
                var _this = this;
                Base.call(_this);
                _this.parameters = parameters;

                _.bindAll(_this,
//                        "onUpdateContestCurrentPayout",
                        "onUpdateContestCurrentPosition",
                        "onUpdateContestCurrentPoints",
//                        "onUpdateContestProjectedPayout",
                        "onUpdateContestCurrentUnitsRemaining"
                );

  //              _this.subscriptions.push(_this.msgBus.subscribe(ruckus.pubsub.subscriptions.view.dashboard.contest.active.currentpayout, _this.onUpdateContestCurrentPayout));
                _this.subscriptions.push(_this.msgBus.subscribe(ruckus.pubsub.subscriptions.view.dashboard.contest.active.currentposition, _this.onUpdateContestCurrentPosition));
                _this.subscriptions.push(_this.msgBus.subscribe(ruckus.pubsub.subscriptions.view.dashboard.contest.active.currentpoints, _this.onUpdateContestCurrentPoints));
  //              _this.subscriptions.push(_this.msgBus.subscribe(ruckus.pubsub.subscriptions.view.dashboard.contest.active.projectedpayout, _this.onUpdateContestProjectedPayout));
                _this.subscriptions.push(_this.msgBus.subscribe(ruckus.pubsub.subscriptions.view.dashboard.contest.active.currentunitsremaining, _this.onUpdateContestCurrentUnitsRemaining));
        };

        ruckus.subpagecontrols.dashboardcontestlist.prototype = Object.create(Base.prototype);
        ruckus.subpagecontrols.dashboardcontestlist.prototype.load = function () {
                var _this = this;

		_this.getContainer();
		_this.container.addClass('ruckus-spc-dashboardcontestlist');

		_this.contestLiveOverviewModel = new ruckus.models.contestliveoverview({});
                _this.models.push(_this.contestLiveOverviewModel);
                var sub2 = _this.msgBus.subscribe('model.contestliveoverview.all', function (data) {
			sub2.unsubscribe();
			_this.render(data.contests.contests);
//			_this.render(_this.contestLiveOverviewModel.modelData.contests);
		});
		_this.contestLiveOverviewModel.fetch({});

/*
                var sub = _this.msgBus.subscribe(ruckus.pubsub.subscriptions.view.dashboard.contest.all, function (contests) {
			_this.render(contests);
                });
                _this.subscriptions.push(sub);

                _this.msgBus.publish(ruckus.pubsub.subscriptions.view.dashboard.contest.get.all);
*/
        };

	ruckus.subpagecontrols.dashboardcontestlist.prototype.render = function(contests){
		var _this = this;
		$.each(contests, function(k,v){
			v.formattedPosition = _this.formatPlace(v.position);
			v.timePercentage =  _this.formatTimePercentageTeam(v.league, v.unitsRemaining);
		});
		var listData = {
			contests : []
		};
		$.each(contests, function(k,v){
			if (v.contestState == 'active')
				listData.contests.push(v);
		});
		 _this.require_template('dashboardcontestlist-tpl');
		 dust.render('dusttemplates/dashboardcontestlist-tpl', listData, function (err, out) {
			_this.container.html(out);
			_this.addScrollBars();

			if (_this.parameters.contest != undefined) {
				$('#dhcl_' + _this.parameters.contest.contestId).addClass('miniSelect');
			}

			$('.dhcl_item').bind('click', function (evt) {
				evt.stopPropagation();
				location = "#dashboardcontest/" + evt.delegateTarget.id.split('_')[1];
			});
		});
	};

        ruckus.subpagecontrols.dashboardcontestlist.prototype.unload = function () {
                this.destroyControl();
        };

        // CONTESTS
/*
        ruckus.subpagecontrols.dashboardcontestlist.prototype.onUpdateContestCurrentPayout = function (contest) {
                var _this = this;
                _this.log({type: 'general', data: contest, msg: 'DASHBOARD PAGE CONTROL > onUpdateContestCurrentPayout'});
                var contestpayout_el = $("div[data-socket='cont_payout_" + contest.contestId + "_" + contest.lineupId + "']");
                contestpayout_el.html(contest.formattedPayout);
                if (contest.payout_direction == "up") {
                        contestpayout_el.parent().addClass('up');
                        setTimeout(function () {
                                contestpayout_el.parent().removeClass('up');
                        }, 3000);
                }
                if (contest.payout_direction == "down") {
                        contestpayout_el.parent().addClass('down');
                        setTimeout(function () {
                                contestpayout_el.parent().removeClass('down');
                        }, 3000);
                }
        };
*/
        ruckus.subpagecontrols.dashboardcontestlist.prototype.onUpdateContestCurrentPosition = function (contest) {
                var _this = this;
                _this.log({type: 'general', data: contest, msg: 'DASHBOARD PAGE CONTROL > onUpdateContestCurrentPosition'});
                var contestposition_el = $("div[data-socket='cont_position_" + contest.contestId + "_" + contest.lineupId + "']")
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
        ruckus.subpagecontrols.dashboardcontestlist.prototype.onUpdateContestCurrentPoints = function (contest) {
                var _this = this;
                _this.log({type: 'general', data: contest, msg: 'DASHBOARD PAGE CONTROL > onUpdateContestCurrentPoints'});
                var contestpoints_el = $("div[data-socket='cont_fpp_" + contest.contestId + "_" + contest.lineupId + "']");
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
/*
                contestpoints_el.html(contest.fpp);
                if (contest.fpp_direction == "up") {
                        contestpoints_el.parent().addClass('up');
                        setTimeout(function () {
                                contestpoints_el.parent().removeClass('up');
                        }, 3000);
                }
                if (contest.fpp_direction == "down") {
                        contestpoints_el.parent().addClass('down');
                        setTimeout(function () {
                                contestpoints_el.parent().removeClass('down');
                        }, 3000);
                }
*/
        };
/*
        ruckus.subpagecontrols.dashboardcontestlist.prototype.onUpdateContestProjectedPayout = function (contest) {
                var _this = this;
                _this.log({type: 'general', data: contest, msg: 'DASHBOARD PAGE CONTROL > onUpdateContestProjectedPayout'});
                var contestprojectedpayout_el = $("div[data-socket='cont_projpayout_" + contest.contestId + "_" + contest.lineupId + "']");
                contestprojectedpayout_el.html(contest.formattedProjectedPayout);

                if (contest.projectedPayout_direction == "up") {
                        contestprojectedpayout_el.parent().addClass('up');
                        setTimeout(function () {
                                contestprojectedpayout_el.parent().removeClass('up');
                        }, 3000);
                }
                if (contest.projectedPayout_direction == "down") {
                        contestprojectedpayout_el.parent().addClass('down');
                        setTimeout(function () {
                                contestprojectedpayout_el.parent().removeClass('down');
                        }, 3000);
                }
        };
*/
        ruckus.subpagecontrols.dashboardcontestlist.prototype.onUpdateContestCurrentUnitsRemaining = function (contest) {
                var _this = this;
                _this.log({type: 'general', data: contest, msg: 'DASHBOARD PAGE CONTROL > onUpdateContestCurrentUnitsRemaining'});
                $("div[data-socket='cont_timepercentage_" + contest.contestId + "_" + contest.lineupId + "']").css("width", _this.formatTimePercentageTeam(contest.league, contest.unitsRemaining) + "%");
        };

        return ruckus.subpagecontrols.dashboardcontestlist;
});
