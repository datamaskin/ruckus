// Author: Scott Gay
define([
        "assets/js/subpagecontrols/base.js",
        "assets/js/libraries/jquery.min.js",
//	"assets/js/libraries/underscore-min.js"
        "assets/js/libraries/dust-core.min.js",
        "assets/js/models/athletepercentowned.js"
], function (Base) {
        ruckus.subpagecontrols.dashboardcontestpercentowned = function (parameters) {
                Base.call(this);
                this.init = function () {
                        this.parameters = parameters;
                };
                this.init();
        };

        ruckus.subpagecontrols.dashboardcontestpercentowned.prototype = Object.create(Base.prototype);
        ruckus.subpagecontrols.dashboardcontestpercentowned.prototype.load = function () {
                var _this = this;
                this.getContainer();
                this.container.addClass('ruckus-spc-dashboardcontestpercentowned');

                this.msgBus.subscribe('controls.dhca.selectplayer', function (data) {
                        _this.render(data);
                        $('#dshc_chat').hide();
                        $('#dshc_percentowned').show();
                });

                this.msgBus.subscribe('controls.dhcr.selectlineup', function (data) {
                        $('#dshc_percentowned').hide();
                        $('#dshc_chat').show();
                });
        };

        ruckus.subpagecontrols.dashboardcontestpercentowned.prototype.render = function (data) {
                var _this = this;
                this.athletePercentOwnedModel = new ruckus.models.athletepercentowned({});
                this.models.push(_this.athletePercentOwnedModel);
                var sub = _this.msgBus.subscribe("model.athletepercentowned.retrieve", function (data) {
                        sub.unsubscribe();
                        $.each(_this.athletePercentOwnedModel.modelData.all, function (key, value) {
                                value.firstInitial = value.firstName.substring(0, 1);
                                value.percentOwned = value.percentOwned * 100;
                        });
                        $.each(_this.athletePercentOwnedModel.modelData.tenPercent, function (key, value) {
                                value.firstInitial = value.firstName.substring(0, 1);
                                value.percentOwned = value.percentOwned * 100;
                        });
                        $.each(_this.athletePercentOwnedModel.modelData.above, function (key, value) {
                                value.firstInitial = value.firstName.substring(0, 1);
                                value.percentOwned = value.percentOwned * 100;
                        });
                        _this.require_template('dashboardcontestpercentowned-tpl');
                        dust.render('dusttemplates/dashboardcontestpercentowned-tpl', {percentowned: _this.athletePercentOwnedModel.modelData}, function (err, out) {
                                _this.container.html(out);
				_this.addScrollBars();

                                $('#dhcp_taball').bind('click', function (evt) {
                                        evt.stopPropagation();
                                        $('#dhcp_taball').removeClass('tab');
                                        $('#dhcp_taball').addClass('tabSelected');
                                        $('#dhcp_tabten').removeClass('tabSelected');
                                        $('#dhcp_tabten').addClass('tab');
                                        $('#dhcp_tababove').removeClass('tabSelected');
                                        $('#dhcp_tababove').addClass('tab');
                                        $('#dhcp_all').show();
                                        $('#dhcp_ten').hide();
                                        $('#dhcp_above').hide();
                                });
                                $('#dhcp_tabten').bind('click', function (evt) {
                                        evt.stopPropagation();
                                        $('#dhcp_taball').removeClass('tabSelected');
                                        $('#dhcp_taball').addClass('tab');
                                        $('#dhcp_tabten').removeClass('tab');
                                        $('#dhcp_tabten').addClass('tabSelected');
                                        $('#dhcp_tababove').removeClass('tabSelected');
                                        $('#dhcp_tababove').addClass('tab');
                                        $('#dhcp_all').hide();
                                        $('#dhcp_ten').show();
                                        $('#dhcp_above').hide();
                                });
                                $('#dhcp_tababove').bind('click', function (evt) {
                                        evt.stopPropagation();
                                        $('#dhcp_taball').removeClass('tabSelected');
                                        $('#dhcp_taball').addClass('tab');
                                        $('#dhcp_tabten').removeClass('tabSelected');
                                        $('#dhcp_tabten').addClass('tab');
                                        $('#dhcp_tababove').removeClass('tab');
                                        $('#dhcp_tababove').addClass('tabSelected');
                                        $('#dhcp_all').hide();
                                        $('#dhcp_ten').hide();
                                        $('#dhcp_above').show();
                                });

                                $('#dhcp_chat').bind('click', function (evt) {
                                        evt.stopPropagation();
                                        $('#dshc_percentowned').hide();
                                        $('#dshc_chat').show();
                                });

                        });
                });
                this.athletePercentOwnedModel.fetch({contestId: _this.parameters.contest.contestId, athleteSportEventInfoId: data.athleteSportEventInfoId, entryId: data.lineup.lineup.entryId});
        };

        ruckus.subpagecontrols.dashboardcontestpercentowned.prototype.unload = function () {
                this.destroyControl();
        };

        return ruckus.subpagecontrols.dashboardcontestpercentowned;
});
	

