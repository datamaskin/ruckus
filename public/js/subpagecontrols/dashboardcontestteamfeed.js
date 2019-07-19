// Author: Scott Gay
define([
        "rg_subpage_base",
        "dust",
        "assets/js/models/contestliveteamfeed.js"
], function (Base) {
        ruckus.subpagecontrols.dashboardcontestteamfeed = function (parameters) {
                Base.call(this);
                this.init = function () {
                        this.parameters = parameters;
                };
                this.init();
        };

        ruckus.subpagecontrols.dashboardcontestteamfeed.prototype = Object.create(Base.prototype);
        ruckus.subpagecontrols.dashboardcontestteamfeed.prototype.load = function () {
                var _this = this;
                _this.getContainer();
                _this.container.addClass('ruckus-spc-dashboardcontestteamfeed');

		 var sub1 = this.msgBus.subscribe('controls.dhca.selectplayer', function (data) {
                        $('#dshc_teamfeed').hide();
                });
                _this.subscriptions.push(sub1);

                var sub2 = this.msgBus.subscribe('controls.dhct.selectteam', function (data) {
                        $('#dshc_teamfeed').show();
                });
                _this.subscriptions.push(sub2);

                var sub3 = this.msgBus.subscribe('controls.dhcr.selectlineup', function (lineup) {
                        _this.lineupathletes = undefined; // clear out existing athletes
                        _this.lineup = lineup;  // set lineup
                        _this.testRender(); // test if we have both datapoints (this may not be necessary if it occurs first always)
                        $('#dshc_teamfeed').show();
                });
                _this.subscriptions.push(sub3);

                var sub4 = this.msgBus.subscribe("control.dhca.lineupathletes", function (lineupathletes) {
                        _this.lineupathletes = lineupathletes;  // set lineup athletes
                        _this.testRender(); // test if we have both data points
                });
                _this.subscriptions.push(sub4);
	};

        ruckus.subpagecontrols.dashboardcontestteamfeed.prototype.testRender = function () {
                if (this.lineup != undefined && this.lineupathletes != undefined)
                        this.render();
        };

        ruckus.subpagecontrols.dashboardcontestteamfeed.prototype.render = function () {
                var _this = this;
                this.contestLiveTeamFeedModel = new ruckus.models.contestliveteamfeed({});
                this.models.push(_this.contestLiveTeamFeedModel);
                var sub = _this.msgBus.subscribe("model.contestliveteamfeed.retrieve", function (data) {
                        sub.unsubscribe();
                        var forDisplay = [];
                        var contestLiveTeamFeedModelmodelData = data.data;

                        $.each(contestLiveTeamFeedModelmodelData, function (key, value) {
                                $.each(_this.lineupathletes, function (k, v) {
                                        if (v.athleteSportEventInfoId == value.athleteSportEventInfoId) {
                                                value.formattedTimestamp = _this.formatTimeActual(value.timestamp);
                                                value.firstInitial = v.firstInitial;
                                                value.lastName = v.lastName;
                                                if(value.firstInitial != '' && value.lastName != '') {
                                                        value.formattedName = value.firstInitial + ' ' + value.lastName;
                                                }
                                                else {
                                                        value.formattedName = value.lastName;
                                                }
                                                forDisplay.push(value);
                                        }
                                });
                        });
//			contestLiveTeamFeedModelmodelData = contestLiveTeamFeedModelmodelData.reverse();
                        // FIXME - use forDisplay instead of the modelData.  Its there now so there is some data to style.
                        _this.require_template('dashboardcontestteamfeed-tpl');
                        dust.render('dusttemplates/dashboardcontestteamfeed-tpl', {data: contestLiveTeamFeedModelmodelData}, function (err, out) {
                                _this.container.html(out);
				_this.addScrollBars();
				if (contestLiveTeamFeedModelmodelData.length == 0)
					$('#teamfeeddatanone').show();
                        });
                });
                this.contestLiveTeamFeedModel.fetch({lineupId: _this.lineup.lineup.lineupId});

        };

        ruckus.subpagecontrols.dashboardcontestteamfeed.prototype.unload = function () {
                this.destroyControl();
        };

        return ruckus.subpagecontrols.dashboardcontestteamfeed;
});
	

