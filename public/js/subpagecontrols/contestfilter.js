// Author: Scott Gay
define([
        "assets/js/subpagecontrols/base.js",
        "assets/js/libraries/jquery.min.js",
//	"assets/js/libraries/underscore-min.js"
        "assets/js/libraries/dust-core.min.js",
        "assets/js/models/contestfilter.js",
        "assets/js/models/contestquickplay.js",
        "assets/js/pagecontrols/contestentry.js",
        "assets/js/libraries/gsap/TweenMax.min.js",
        "assets/js/libraries/jquery-ui/jquery-ui-1.10.4.custom.min.js", // jquery slider
        "assets/js/libraries/jquery-ui/jquery.ui.touch-punch.min.js" // jquery slider hack for mobile drag - http://touchpunch.furf.com/
], function (Base) {
        ruckus.subpagecontrols.contestfilter = function (parameters) {
                Base.call(this);
                this.init = function () {
                        this.parameters = parameters;

                        this.selected = {
                                sport: undefined,
                                entryFee: undefined,
                                entryFeeSelected: {min: undefined, max: undefined},
                                numPlayers: [],
                                grouping: 0,
                                salaryCap: 5000000
                        };
                };
                this.init();
        };

        ruckus.subpagecontrols.contestfilter.prototype = Object.create(Base.prototype);
        ruckus.subpagecontrols.contestfilter.prototype.load = function () {
                var _this = this;
                this.getContainer();
                this.container.addClass('ruckus-spc-contestfilter');

                // get data
                this.contestFilter = new ruckus.models.contestfilter({});
                this.models.push(this.contestFilter);
                var sub = this.msgBus.subscribe("model.contestfilter.retrieve", function (data) {
                        sub.unsubscribe();
                        _this.require_template('contestfilter-tpl');
                        dust.render('dusttemplates/contestfilter-tpl', {}, function (err, out) {
                                var templateRender = function () {
                                        _this.container.html(out);
					_this.addScrollBars();
                                        $('#conf_quickplay').click(function (evt) {
                                                evt.stopPropagation();
                                                if ($('#conf_quickplay').hasClass('quickplayenabled')) {
                                                        _this.contestQuickPlayModel = new ruckus.models.contestquickplay({});
                                                        _this.models.push(_this.contestQuickPlayModel);
                                                        var sub2 = _this.msgBus.subscribe("model.contestquickplay.retrieve", function (dataqp) {
                                                                sub2.unsubscribe();
                                                                if (_this.contestQuickPlayModel.modelData.length != 0) {
                                                                        _this.parameters.lobby.hidePage();
                                                                        var contestentry = new ruckus.pagecontrols.contestentry({
                                                                                'container': _this.parameters.lobby.parameters.container,
                                                                                'lobby': _this.parameters.lobby,
                                                                                'data': undefined,
                                                                                'contest': undefined,
                                                                                'contestIds': _this.contestQuickPlayModel.modelData
                                                                        });
                                                                        contestentry.load();
                                                                        _this.controls.push(contestentry);
                                                                }
                                                        });
                                                        _this.contestQuickPlayModel.fetch(_this.selected);
                                                }
                                        });
                                        _this.render();
                                        $('#conf_clear').click(function () {
                                                templateRender();
                                                _this.msgBus.publish("control.contestfilter.change", _this.selected);
                                        });
                                };
                                templateRender();
                        });
                });
                this.contestFilter.fetch();

        };

        ruckus.subpagecontrols.contestfilter.prototype.render = function () {
                var _this = this;
//		this.log({type:'api',data:this.contestFilter.modelData,msg:"CONTEST FILTER MODEL DATA"});
                $.each(this.contestFilter.modelData, function (key, value) {
//			if (value.active){
                        // sportbox
                        var spt = $('<div>', {
                                'id': 'conf_sport_' + value.name,
                                'class': 'col-xs-3 conf_sport sportbox'
                        }).appendTo($('#conf_sports'));
                        $('<div>', {'class': 'sportcircle'}).appendTo(spt);
                        $('<div>', {'class': 'sportname'}).appendTo(spt).html(value.name);
                        
                        /*				$('<div>', {
                         'id' : 'conf_sport_'+value.name,
                         'class' : 'col-xs-3 conf_sport sportbox'
                         }).appendTo($('#conf_sports')).html(value.name);*/
                        // sportbox click event
                        $('#conf_sport_' + value.name).click(function () {
                                $('.conf_sport').removeClass('sportselected');
                                $('#conf_sport_' + value.name).addClass('sportselected');
                                if (value.name != 'ALL') {
                                        $('#conf_quickplay').removeClass('quickplaydisabled');
                                        $('#conf_quickplay').addClass('quickplayenabled');
                                } else {
                                        $('#conf_quickplay').removeClass('quickplayenabled');
                                        $('#conf_quickplay').addClass('quickplaydisabled');
                                }
                                _this.selected.sport = value.name;
                                _this.selected.entryFee = value.entryFee;
                                _this.selected.entryFeeSelected.min = value.entryFee[0];
                                _this.selected.entryFeeSelected.max = value.entryFee[value.entryFee.length - 1];
                                _this.renderSlider();
                                _this.renderPlayers(value);
                                _this.populateAdvanced(value);
                                _this.change();
                        });
                        // sportbox handle default
                        if (value.name == 'ALL') {
                                $('#conf_sport_' + value.name).addClass('sportselected');
                                _this.selected.sport = value.name;
                                _this.selected.entryFee = value.entryFee;
                                _this.selected.entryFeeSelected.min = value.entryFee[0];
                                _this.selected.entryFeeSelected.max = value.entryFee[value.entryFee.length - 1];
                                _this.renderPlayers(value);
                                _this.renderAdvanced(value);
                        }
//			} else {
//				 // sportbox inactive
                        //                              $('<div>', {
                        //                                    'id' : 'conf_sport_'+value.id,
//                                        'class' : 'col-xs-3 conf_sport sportboxinactive'
//                                }).appendTo($('#conf_sports')).html(value.name);
//			}		
                });
                $('<div>', {'style': 'clear:both;'}).appendTo($('#conf_sports'));
                this.renderSlider();
//		this.msgBus.publish("control.contestfilter.change", this.selected);
        };

        ruckus.subpagecontrols.contestfilter.prototype.renderAdvanced = function (data) {
                var _this = this;
                $('#conf_advancedbutton').click(function (evt) {
                        evt.stopPropagation();
                        var cell = $('#conf_advancedoptions');
                        cell.show();
                        var tl = new TimelineMax({
                                onComplete: function () {
                                }
                        });
                        tl.to(cell, 2.5, {opacity: 1});
                        tl.to(cell, 2.5, {marginLeft: 0}, "-=2.5");
                        tl.play();
                });
                $.each(data.grouping, function (key, value) {
                        $('<option>', {'value': value.id}).appendTo($('#conf_grouping')).html(value.name);
                });
                $('#conf_grouping').change(function (evt) {
                        evt.stopPropagation();
                        _this.selected.grouping = $('#conf_grouping').val();
                        _this.change();
                });
                $.each(data.salaryCap, function (key, value) {
                        var displayValue = _this.formatMoney(value);
                        $('<option>', {'value': value}).appendTo($('#conf_salarycap')).html(displayValue);
                });
                $('#conf_salarycap').change(function (evt) {
                        evt.stopPropagation();
                        _this.selected.salaryCap = $('#conf_salarycap').val();
                        _this.change();
                });
        };

        ruckus.subpagecontrols.contestfilter.prototype.populateAdvanced = function (data) {
                var _this = this;
                $('#conf_grouping').html('');
                $('<option>', {'value': 0}).appendTo($('#conf_grouping')).html('ALL');
                $.each(data.grouping, function (key, value) {
                        $('<option>', {'value': value.id}).appendTo($('#conf_grouping')).html(value.name);
                });
                $('#conf_salarycap').html('');
                $.each(data.salaryCap, function (key, value) {
                        var displayValue = _this.formatMoney(value);
                        $('<option>', {'value': value}).appendTo($('#conf_salarycap')).html(displayValue);
                });
        };

        ruckus.subpagecontrols.contestfilter.prototype.renderSlider = function () {
                var _this = this;
                $("#conf_entryfeeslider").html('');
                $('<div>', {'id': 'slide', 'class': 'col-xs-24'}).appendTo($("#conf_entryfeeslider"));
                var values = [];
                var count = 0;
                $.each(_this.selected.entryFee, function (key, value) {
                        values.push(count);
                        count++;
                });
                $("#slide").slider({
                        range: true,
                        values: [values[0], values[values.length - 1]],
                        min: values[0],
                        max: values[values.length - 1],
                        step: 1,
                        slide: function (event, ui) {
				var min = _this.formatMoney(_this.selected.entryFee[ui.values[0]]);
				var max = _this.formatMoney(_this.selected.entryFee[ui.values[1]]);
				if (min == "$0")
					min = "Free";
				$('#conf_entryfeedisplay').html(min+' - '+max);
//                                $('#conf_entryfeedisplay').html(_this.formatMoney(_this.selected.entryFee[ui.values[0]]) + ' - ' + _this.formatMoney(_this.selected.entryFee[ui.values[1]]));
                        },
                        change: function (event, ui) {
				var min = _this.formatMoney(_this.selected.entryFee[ui.values[0]]);
				var max = _this.formatMoney(_this.selected.entryFee[ui.values[1]]);
				if (min == "$0")
					min = "Free";
				$('#conf_entryfeedisplay').html(min+' - '+max);
//                                $('#conf_entryfeedisplay').html(_this.formatMoney(_this.selected.entryFee[ui.values[0]]) + ' - ' + _this.formatMoney(_this.selected.entryFee[ui.values[1]]));
                                _this.selected.entryFeeSelected.min = _this.selected.entryFee[ui.values[0]];
                                _this.selected.entryFeeSelected.max = _this.selected.entryFee[ui.values[1]];
                                _this.change();
                        }
                });
		var min = _this.formatMoney(_this.selected.entryFee[0]);
		var max = _this.formatMoney(_this.selected.entryFee[_this.selected.entryFee.length - 1]);
		if (min == "$0")
			min = "Free";
		$('#conf_entryfeedisplay').html(min+' - '+max);
//                $('#conf_entryfeedisplay').html(_this.formatMoney(_this.selected.entryFee[0]) + ' - ' + _this.formatMoney(_this.selected.entryFee[_this.selected.entryFee.length - 1]));

                // find button points
                var slideStruct = {
                        low: {
                                min: undefined,
                                max: undefined
                        },
                        med: {
                                min: undefined,
                                max: undefined
                        },
                        high: {
                                min: undefined,
                                max: undefined
                        }
                };
                $.each(this.selected.entryFee, function (k, v) {
                        if (slideStruct.low.min == undefined)
                                slideStruct.low.min = k;
                        if (slideStruct.med.min == undefined && v >= 1001) {
                                slideStruct.low.max = k - 1;
                                slideStruct.med.min = k;
                        }
                        if (slideStruct.high.min == undefined && v >= 10000) {
                                slideStruct.med.max = k - 1;
                                slideStruct.high.min = k;
                        }
                        slideStruct.high.max = k;
                });

                // <= $10
                $('#conf_sliderlow').click(function (evt) {
                        evt.stopPropagation();
                        // FIXME - setting the slider left and right handle values fires 2 change events
                        $("#slide").slider("values", [slideStruct.low.min, slideStruct.low.max]);
                });
                $('#conf_slidermed').click(function (evt) {
                        evt.stopPropagation();
                        $("#slide").slider("values", [slideStruct.med.min, slideStruct.med.max]);
                });
                // >= $100
                $('#conf_sliderhigh').click(function (evt) {
                        evt.stopPropagation();
                        $("#slide").slider("values", [slideStruct.high.min, slideStruct.high.max]);
                });
        };

        ruckus.subpagecontrols.contestfilter.prototype.renderPlayers = function (data) {
                var _this = this;
		var allObject = undefined;
                $('#conf_size').html('');
                $.each(data.numPlayers, function (key, value) {
                        var display = undefined;
                        if (value.minimum == value.maximum)
                                display = value.minimum;
                        else
                                display = value.minimum + '-' + value.maximum;
                        if (value.minimum == 0){
                                display = "ALL";
				allObject = value;
			}
                        else if (value.maximum > 10000)
                                display = value.minimum + '+';
                        var sze = $('<div>', {
				'id': 'numplayers_'+display,
                                'class': 'col-xs-4 conf_size sportbox'
                        }).appendTo($('#conf_size'));
                        $('<div>', {'class': 'sizecircle'}).appendTo(sze);
                        $('<div>', {'class': 'sizename'}).appendTo(sze).html(display);
                        sze.click(function () {
				if (sze.hasClass('sportselected')){
					sze.removeClass('sportselected');
					var arr = [];
					$.each(_this.selected.numPlayers, function(k,v){
						if (v !== value){
							arr.push(v);
						}
					});				
					_this.selected.numPlayers = arr;
					if (_this.selected.numPlayers.length == 0){
						_this.selected.numPlayers.push(allObject);
						$('#numplayers_ALL').addClass('sportselected');
					}
					_this.change();
				} else {
//					$('.conf_size').removeClass('sportselected');
					// remove all
					if ($('#numplayers_ALL').hasClass('sportselected')){
						$('#numplayers_ALL').removeClass('sportselected');
						var arr = [];
						$.each(_this.selected.numPlayers, function(k,v){
							if (v.minimum != 0)
								arr.push(v);
						});
						_this.selected.numPlayers = arr;
					}
					if (value.minimum == 0){
						_this.selected.numPlayers = [];
						$('.conf_size').removeClass('sportselected');
					}
					sze.addClass('sportselected');
					_this.selected.numPlayers.push(value);
					_this.change();
				}
                        });
                        if (key == 0) {
                                sze.addClass('sportselected');
                                _this.selected.numPlayers.push(value);
                        }
                });

        };

        ruckus.subpagecontrols.contestfilter.prototype.change = function () {
                // fires when any filter is changed
                this.msgBus.publish("control.contestfilter.change", this.selected);
        };

        ruckus.subpagecontrols.contestfilter.prototype.unload = function () {
                this.destroyControl();
        };

        return ruckus.subpagecontrols.contestfilter;
});
	

