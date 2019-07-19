// Author: Scott Gay
define([
        "assets/js/subpagecontrols/base.js",
	"simpleweather",
        "assets/js/libraries/jquery.min.js",
//	"assets/js/libraries/underscore-min.js"
        "assets/js/libraries/dust-core.min.js"
], function (Base) {
        ruckus.subpagecontrols.contestgames = function (parameters) {
                Base.call(this);
                this.init = function () {
                        this.parameters = parameters;
                };
                this.init();
        };

        ruckus.subpagecontrols.contestgames.prototype = Object.create(Base.prototype);
        ruckus.subpagecontrols.contestgames.prototype.load = function () {
                var _this = this;
                this.getContainer();
                this.container.addClass('ruckus-spc-contestgames');

                _this.contestEventsModel = new ruckus.models.contestevents({});
                _this.models.push(_this.contestEventsModel);
                var sub = _this.msgBus.subscribe("model.contestevents.retrieve", function (data) {
                        sub.unsubscribe();
                        _this.contestEventsModel = data;
                        $.each(_this.contestEventsModel.data, function (key, value) {
                                value.formattedstartTime = _this.formatTimeActual(value.startTime);
                        });
                        _this.require_template('contestgames-tpl');
                        dust.render('dusttemplates/contestgames-tpl', {games: _this.contestEventsModel.data}, function (err, out) {
                                _this.container.html(out);
				_this.addScrollBars();
                                $('.cong_event').bind('click', function (evt) {
                                        evt.stopPropagation();
					if (evt.delegateTarget.id == 'cong_all'){
	                                        $('.gameSelected').removeClass('gameSelected');
						$('#cong_all').addClass('gameSelected');
						_this.msgBus.publish('control.cong.selectevent', evt.delegateTarget.id);
					} else {
						$('#cong_all').removeClass('gameSelected');
						if ($('#' + evt.delegateTarget.id).hasClass('gameSelected')){
		                                        $('#' + evt.delegateTarget.id).removeClass('gameSelected');
							if ($('.gameSelected').length == 0)
								$('#cong_all').addClass('gameSelected');
							_this.msgBus.publish('control.cong.unselectevent', evt.delegateTarget.id);
						} else {
		                                        $('#' + evt.delegateTarget.id).addClass('gameSelected');
							_this.msgBus.publish('control.cong.selectevent', evt.delegateTarget.id);
						}
					}
                                });
				$.each(_this.contestEventsModel.data, function (key, value) {
					$.simpleWeather({
						zipcode: '',
						location: value.location,
						unit: 'f',
						success: function(weather) {
							var myclass = '';
							var temp = '';
							var day = value.formattedstartTime.split(',')[0];
							$.each(weather.forecast, function(k,v){
								if (v.day == day){
									myclass = v.text.replace(/ /g,'').toLowerCase();
									temp = v.high+"&deg;"+"&#xf0"+v.code.toString(16)+";"; //Using existing nomenclature for weather code. concatenate the string of the weather here for preview since it is a font.
								}
							});
							$('#cong_temp_'+value.eventId).html(temp);
							$('#cong_forecast_'+value.eventId).addClass(myclass);
						},
						error: function(error) {
						}
					});
				});
                        });
                });
                _this.contestEventsModel.fetch({contestId: _this.parameters.contest.id});
        };

        ruckus.subpagecontrols.contestgames.prototype.unload = function () {
                this.destroyControl();
        };

        return ruckus.subpagecontrols.contestgames;
});
	

