// Author: Scott Gay
define([
        "assets/js/subpagecontrols/base.js",
        "assets/js/libraries/jquery.min.js",
//	"assets/js/libraries/underscore-min.js"
        "assets/js/libraries/dust-core.min.js",
        "assets/js/models/contestliveathletes.js"
], function (Base) {
        ruckus.subpagecontrols.dashboardcontestathletedetail = function (parameters) {
                Base.call(this);
                this.init = function () {
                        this.parameters = parameters;
                };
                this.init();
        };

        ruckus.subpagecontrols.dashboardcontestathletedetail.prototype = Object.create(Base.prototype);
        ruckus.subpagecontrols.dashboardcontestathletedetail.prototype.load = function () {
                var _this = this;
                this.getContainer();
                this.container.addClass('ruckus-spc-dashboardcontestathletedetail');


                var sub1 = _this.msgBus.subscribe('controls.dhcr.selectlineup', function (data) {
                        $('#dshc_athletedetail').hide();
                });
                _this.subscriptions.push(sub1);


                var sub2 = _this.msgBus.subscribe('controls.dhca.selectplayer', function (data) {
                        var athleteId = data.athleteSportEventInfoId;
                        $('#dshc_athletedetail').show();
                        _this.contestLiveAthletesModel = new ruckus.models.contestliveathletes({});
                        _this.models.push(_this.contestLiveAthletesModel);
                        var sub3 = _this.msgBus.subscribe("model.contestliveathletes.retrieve", function (data) {
                                sub3.unsubscribe();
                                /*
                                 // FIXME - hack to have sample data
                                 _this.contestLiveAthletesModel.modelData.timeline = JSON.parse('[{"athleteSportEventInfoId":1, "timestamp":1405454700000, "description":"Ortiz singles to right field", "fpChange":"+3"},{"athleteSportEventInfoId":2, "timestamp":1405454700000, "description":"Pedroia doubles to deep center field", "fpChange":"+5"}]');
                                 _this.contestLiveAthletesModel.modelData.stats = JSON.parse('[{"amount":0,"fpp":0,"name":"Double"},{"amount":0,"fpp":0,"name":"Triple"},{"amount":0,"fpp":0,"name":"Home Run"},{"amount":0,"fpp":0,"name":"Single"},{"amount":0,"fpp":0,"name":"Run Batted In"},{"amount":0,"fpp":0,"name":"Run"},{"amount":0,"fpp":0,"name":"Walk"},{"amount":0,"fpp":0,"name":"Hit By Pitch"},{"amount":0,"fpp":0,"name":"Stolen Base"},{"amount":0,"fpp":0,"name":"Caught Stealing"}]');
                                 */
//console.log('**********');
// console.log(_this.contestLiveAthletesModel.modelData.matchup);
// _this.msgBus.publish("PUT SOMETHING HERE DAVE!",{matchup:_this.contestLiveAthletesModel.modelData.matchup});
                                $.each(_this.contestLiveAthletesModel.modelData.timeline, function (k, v) {
                                        v.formattedTimestamp = _this.formatTimeActual(v.timestamp);	
                                });
				_this.contestLiveAthletesModel.modelData.formattedTimePercentage = _this.formatTimePercentage(_this.parameters.contest.league, _this.contestLiveAthletesModel.modelData.unitsRemaining);
				switch (_this.contestLiveAthletesModel.modelData.indicator){
					case 0 :
						_this.contestLiveAthletesModel.modelData.indicatorClass = 'dot';
						break;
					case 1 :
						_this.contestLiveAthletesModel.modelData.indicatorClass = 'dotPlay';
						break;
					case 2 :
						_this.contestLiveAthletesModel.modelData.indicatorClass = 'dotRedzone';
						break;
				}
                                _this.require_template('dashboardcontestathletedetail-tpl');
                                dust.render('dusttemplates/dashboardcontestathletedetail-tpl', {data: _this.contestLiveAthletesModel.modelData}, function (err, out) {
                                        _this.container.html(out);
					_this.addScrollBars();
					_this.msgBus.publish(ruckus.pubsub.subscriptions.view.dashboardcontest.get.sportevent, _this.contestLiveAthletesModel.modelData.matchup.sportEventId);
					_this.msgBus.publish(ruckus.pubsub.subscriptions.view.dashboardcontest.get.athlete, _this.contestLiveAthletesModel.modelData.athleteSportEventInfoId);

                                        $('#dhad_teamfeed').bind('click', function (evt) {
                                                evt.stopPropagation();
                                                $('#dshc_athletedetail').hide();
                                                _this.msgBus.publish('controls.dhct.selectteam', {});
                                        });

                                        $('#dhad_tabplayerfeed').bind('click', function (evt) {
						evt.stopPropagation();
						$('#dhad_tabplayerfeed').addClass('tabSelected');
						$('#dhad_tabplayerfeed').removeClass('tab');
						$('#dhad_tabstats').removeClass('tabSelected');
                                                $('#dhad_tabstats').addClass('tab');
                                                $('#dhad_playerfeed').show();
                                                $('#dhad_stats').hide();
                                        });

                                        $('#dhad_tabstats').bind('click', function (evt) {
                                                evt.stopPropagation();
						$('#dhad_tabplayerfeed').addClass('tab');
						$('#dhad_tabplayerfeed').removeClass('tabSelected');
						$('#dhad_tabstats').removeClass('tab');
                                                $('#dhad_tabstats').addClass('tabSelected');
                                                $('#dhad_playerfeed').hide();
                                                $('#dhad_stats').show();
                                        });
                                });
                        });
                        _this.contestLiveAthletesModel.fetch({contestId: _this.parameters.contest.contestId, athleteId: athleteId});
                        _this.subscriptions.push(sub3);
                });
                _this.subscriptions.push(sub2);
        };

        ruckus.subpagecontrols.dashboardcontestathletedetail.prototype.unload = function () {
                this.destroyControl();
        };

        return ruckus.subpagecontrols.dashboardcontestathletedetail;
});
	

