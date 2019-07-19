// Author: Scott Gay
define([
        "rg_subpage_base",
        "dust",
        "jqslider", // jquery slider
        "jqtouchpunch", // jquery slider hack for mobile drag - http://touchpunch.furf.com/
        "nicescroll",
        "assets/js/models/contestathletes.js",
        "assets/js/rucksack/configuration.js",
        "assets/js/rucksack/athletes.js"
], function (Base) {
        ruckus.subpagecontrols.lineupbuilderavailableathletes = function (parameters) {
                Base.call(this);
                this.init = function () {
                        var _this = this;
                        this.parameters = parameters;
                        this.posFilter = 'all';
                        this.evtFilter = ['all'];
                        this.schFilter = '';

                        this.sortDetails = {
                                firstPositionDisplay: 'desc',
                                lastName: 'desc',
                                salaryformatted: 'asc'
                        };

                        this.statOrder = [];
                        this.statVisibility = [];

                        // create lineup templates
                        this.lineupTemplate = [];
                        this.flexTemplate = [];
                        $.each(this.parameters.lineuprules, function (key, value) {
                                if (value.numberOfAthletes == 1) {
                                        _this.lineupTemplate.push(value.abbreviation);
                                } else {
                                        for (var x = 1; x <= value.numberOfAthletes; x++) {
                                                _this.lineupTemplate.push(value.abbreviation + x);
                                        }
                                }
                                if (value.flex != undefined) {
                                        if (value.flex) {
                                                _this.flexTemplate.push(value.abbreviation);
                                        }
                                }
                        });
                };
                this.init();
        };

        ruckus.subpagecontrols.lineupbuilderavailableathletes.prototype = Object.create(Base.prototype);
        ruckus.subpagecontrols.lineupbuilderavailableathletes.prototype.load = function () {
                var _this = this;
                this.getContainer();

                this.container.addClass('ruckus-spc-lineupbuilderavailableathletes');
                _this.contestAthletesModel = new ruckus.models.contestathletes({});
                _this.models.push(_this.contestAthletesModel);
                var sub = _this.msgBus.subscribe("model.contestathletes.retrieve", function (data) {
                        sub.unsubscribe();
                        // handle athlete position mapping and other formatting
                        $.each(_this.contestAthletesModel.modelData.athletes, function (key, value) {
                                value.positionsById = value.positions;
                                value.positions = undefined;
                                var arrPositions = [];
                                $.each(value.positionsById, function (k, v) {
                                        arrPositions.push(_this.parameters.lineuprules[v].abbreviation);
                                });
                                value.positions = arrPositions;
                                value.salaryformatted = value.salary.formatCurrency();
                                value.firstPosition = value.positions[0];
                                $.each(_this.parameters.lineuprules, function (k, v) {
                                        if (v.abbreviation == value.firstPosition)
                                                value.firstPositionDisplay = v.displayabbreviation;
                                });
                                value.firstNameInitial = value.firstName.substring(0, 1);
                        });

//			_this.contestAthletesModel.modelData.athletesInitial = _this.contestAthletesModel.modelData.athletes.slice(0,30);
//			_this.contestAthletesModel.modelData.athletesMore = _this.contestAthletesModel.modelData.athletes.slice(30,_this.contestAthletesModel.modelData.athletes.length);
                        _this.require_template('lineupbuilderavailableathletes' + _this.parameters.contest.league + '-tpl');
                        dust.render('dusttemplates/lineupbuilderavailableathletes' + _this.parameters.contest.league + '-tpl', _this.contestAthletesModel.modelData, function (err, out) {
                                _this.container.html(out);
/*
				var outOfProcess = function(){
					// render the rest of the athletes second
					_this.require_template('lineupbuilderavailableathleteslistmore' + _this.parameters.contest.league + '-tpl');
					dust.render('dusttemplates/lineupbuilderavailableathleteslistmore' + _this.parameters.contest.league + '-tpl', _this.contestAthletesModel.modelData, function (err, out) {
						$('#lbaa_athletelist').html($('#lbaa_athletelist').html()+out);

						_this.runFilter();
						$.each(_this.contestAthletesModel.modelData.athletes, function (key, value) {
							_this.rowClickEvents(value);
						});

						// autoselect athletes
						if (_this.parameters.editathletes != undefined){
							$.each(_this.contestAthletesModel.modelData.athletes, function (key, value) {
								$.each(_this.parameters.editathletes, function(k,v){
									if (value.athleteSportEventInfoId == v.athleteSportEventInfoId){
										 $('#lbaa_athleterow_' + value.id).hide();
										_this.msgBus.publish('control.lbaa.selectplayer', value);		
									}
								});
							});
						}

					});
				};
				setTimeout(outOfProcess,1);
*/		
				_this.addScrollBars();
				_this.runFilter();
                _this.headerEvents();
				_this.positionClickEvents();

                                $.each(_this.contestAthletesModel.modelData.athletes, function (key, value) {
                                        _this.rowClickEvents(value);
                                });

                                var sub = _this.msgBus.subscribe('control.lbsa.showplayer', function (data) {
                                        _this.runFilter();
                                });
                                _this.subscriptions.push(sub);

                                var sub2 = _this.msgBus.subscribe('control.lbsa.hideplayer', function (data) {
                                        _this.runFilter();
                                });
                                _this.subscriptions.push(sub2);

                                var sub3 = _this.msgBus.subscribe('control.cong.selectevent', function (data) {
					if (data == 'cong_all'){
						_this.evtFilter = ['all'];
					} else {
						_this.evtFilter = jQuery.grep(_this.evtFilter, function(value) {
							return value != 'all';
						});
						_this.evtFilter.push(parseInt(data.split('_')[1]));
					}
                                        _this.runFilter();
                                });
                                _this.subscriptions.push(sub3);

				var sub4 = _this.msgBus.subscribe('control.cong.unselectevent', function (data) {
					_this.evtFilter = jQuery.grep(_this.evtFilter, function(value) {
						return value != parseInt(data.split('_')[1]);
					});
					if (_this.evtFilter.length == 0)
						_this.evtFilter.push('all');
                                        _this.runFilter();
                                });
                                _this.subscriptions.push(sub4);

                                $('#lbaa_playersearch').bind('keyup', function (evt) {
                                        evt.stopPropagation();
                                        _this.schFilter = $('#lbaa_playersearch').val();
                                        _this.runFilter();
                                });
                                $('#lbaa_playersearch_x').bind('click', function (evt) {
                                        evt.stopPropagation();
                                        $('#lbaa_playersearch').val('');
                                        _this.schFilter = '';
                                        _this.runFilter();
                                });
				$('#lbaa_playersearch').bind('focus', function (evt) {
                                        evt.stopPropagation();
                                        if ($('#lbaa_playersearch').val() == 'Find a player_') {
                                                $('#lbaa_playersearch').val('');
                                        }
                                });
                                $('#lbaa_playersearch').bind('blur', function (evt) {
                                        evt.stopPropagation();
                                        if ($('#lbaa_playersearch').val() == '') {
                                                $('#lbaa_playersearch').val('Find a player_');
                                        }
                                });
                                $('#lbaa_sliders').bind('click', function (evt) {
                                        evt.stopPropagation();
                                        $('#lbaa_header').hide();
                                        $('#lbaa_athletelist').hide();
                                        $('#lbaa_sliderslist').show();
                                        _this.renderSliders();
                                        _this.msgBus.publish("control.lbaa.slideron", {});
                                });
                                $('.lbaa_positiontab').bind('click', function (evt) {
                                        evt.stopPropagation();
                                        $('#lbaa_sliderslist').hide();
                                        $('#lbaa_header').show();
                                        $('#lbaa_athletelist').show().scrollTop(0);
                                        _this.msgBus.publish("control.lbaa.slideroff", {});

			        });

				// autoselect athletes
				if (_this.parameters.editathletes != undefined){
					$.each(_this.contestAthletesModel.modelData.athletes, function (key, value) {
						$.each(_this.parameters.editathletes, function(k,v){
							if (value.athleteSportEventInfoId == v.athleteSportEventInfoId){
								 $('#lbaa_athleterow_' + value.id).hide();
								_this.msgBus.publish('control.lbaa.selectplayer', value);		
							}
						});
					});
				}

				var subImport = _this.msgBus.subscribe("control.import.selectplayer", function(data){
					$.each(_this.contestAthletesModel.modelData.athletes, function (key, value) {
						if (data.data.athleteSportEventInfoId == value.athleteSportEventInfoId){
							$('#lbaa_athleterow_' + value.id).hide();
                                                        _this.msgBus.publish('control.lbaa.selectplayer', value);
						}
					});
				});
				_this.subscriptions.push(subImport);

                        });
                        //$(".niceScroll").niceScroll();
                });
                _this.contestAthletesModel.fetch(_this.parameters.contest);
        };

        ruckus.subpagecontrols.lineupbuilderavailableathletes.prototype.clone = function (obj) {
                // Handle the 3 simple types, and null or undefined
                if (null == obj || "object" != typeof obj) return obj;

                // Handle Date
                if (obj instanceof Date) {
                        var copy = new Date();
                        copy.setTime(obj.getTime());
                        return copy;
                }

                // Handle Array
                if (obj instanceof Array) {
                        var copy = [];
                        for (var i = 0, len = obj.length; i < len; i++) {
                            copy[i] = this.clone(obj[i]);
                        }
                        return copy;
                }

                // Handle Object
                if (obj instanceof Object) {
                        var copy = {};
                        for (var attr in obj) {
                            if (obj.hasOwnProperty(attr)) copy[attr] = this.clone(obj[attr]);
                        }
                        return copy;
                }

                throw new Error("Unable to copy obj! Its type isn't supported.");
        };

        ruckus.subpagecontrols.lineupbuilderavailableathletes.prototype.rucksack = function (athleteList, lineup, sliders) {
                var athletes = new ruckus.subpagecontrols.athletes({});
                var config = new ruckus.subpagecontrols.configuration({});
                var SLOTS = ["FX1", "FX2", "QB", "RB1", "RB2", "WR1", "WR2", "TE", "DEF"];
                var POSITIONS = ["FX", "QB", "RB", "WR", "TE", "DEF"];
                var DEFAULT_DEPTH = 3000;
                var MIN = 1;
                var MAX = SLOTS.length;
                var roster = {};
                config.setSlots(SLOTS);
                athletes.setPositions(POSITIONS);

                var s0 = sliders[this.parameters.contest.league.toLowerCase()][0].value;
                var s1 = sliders[this.parameters.contest.league.toLowerCase()][1].value;
                var s2 = sliders[this.parameters.contest.league.toLowerCase()][2].value;
                var s3 = sliders[this.parameters.contest.league.toLowerCase()][3].value;
                var s4 = sliders[this.parameters.contest.league.toLowerCase()][4].value;
                var s5 = sliders[this.parameters.contest.league.toLowerCase()][5].value;
                var f0 = 0.5 + ((s0+1)/21);
                var f1 = 0.5 + ((s1+1)/21);
                var f2 = 0.5 + ((s2+1)/21);
                var f3 = 0.5 + ((s3+1)/21);
                var f4 = (2/3) * ((s4+1)/21);

                for(var i = 0; i < athleteList.length; i++) {
                    var stats = athleteList[i].stats;
                    var v0 = isNaN(stats[0][s5]) ? 0 : stats[0][s5];
                    var v1 = isNaN(stats[1][s5]) ? 0 : stats[1][s5];
                    var v2 = isNaN(stats[2][s5]) ? 0 : stats[2][s5];
                    var v3 = isNaN(stats[3][s5]) ? 0 : stats[3][s5];
                    var v4 = isNaN(stats[4][s5]) ? 0 : stats[4][s5];

                    var v5 = (f0 * .1 * v0) + (f1 * .1 * v1) + (f2 * .1 * v2) + (f3 * 3 * v3);
                    var value = v5;
                    //var value = (f4 * 10) + (1 - f4) * v5;
                    if(v5>v4){
                        value = (f4 * v4) + (1 - f4) * v5;
                    }

                    athleteList[i].value = value;
                    athletes.addAthlete(athleteList[i]);

                    for(var j = 0; j < lineup.length; j++) {
                        if (lineup[j].slot && lineup[j].preexisting == true && athleteList[i].athleteSportEventInfoId == lineup[j].id) {
                            roster[lineup[j].slot] = athleteList[i];
                        }
                    }
                }

                // start with random athletes
                var slots = config.getSlots();
                for(var i = 0; i < slots.length; i++) {
                    var slot = slots[i];
                    var athlete = athletes.randomAthlete(slot, lineup);
                    //console.log(athlete);
                    config.addAthlete(athlete, slot, roster, lineup);
                }

                // now try to improve the roster
                var attempt = 0;
                var time_since_last_change = 0;
                var max_roster = this.clone(roster);
                var new_roster = this.clone(roster);
                while(time_since_last_change < DEFAULT_DEPTH) {
                    new_roster = this.clone(roster);
                    time_since_last_change += 1;
                    attempt += 1;

                    //config.printRoster("CONFIG BEFORE: " + attempt, roster);
                    //console.log("\n\n");

                    var rand = Math.floor(Math.random()*(MAX-MIN+1)+MIN);
                    for(var i = 0; i < rand; i++) {
                        var rand2 = Math.floor(Math.random()*(MAX-MIN+1)+MIN) - 1;
                        var slot = SLOTS[rand2];
                        var athlete = athletes.randomAthlete(slot, lineup);
                        config.addAthlete(athlete, slot, new_roster, lineup);
                        var max_tries = 200;
                        while(config.getTotalSalary(new_roster) > config.getMaxAllowedCost() && max_tries > 0) {
                            athlete = athletes.randomAthlete(slot, lineup);
                            config.addAthlete(athlete, slot, new_roster, lineup);
                            max_tries--;
                        }
                    }


                    //config.printRoster("CONFIG AFTER: " + attempt, roster);
                    //console.log("\n\n");

                    if(config.getTotalValue(new_roster) > config.getTotalValue(roster)) {
                        //console.log("NEW: " + config.getTotalValue(new_roster));
                        //console.log("OLD: " + config.getTotalValue(roster));

                        roster = this.clone(new_roster);
                        time_since_last_change = 0;
                    }
                    if(config.getTotalValue(new_roster) > config.getTotalValue(max_roster)) {
                        //console.log("MAX: " + config.getTotalValue(new_roster));
                        max_roster = this.clone(new_roster);
                        //config.printRoster("New Max: " + attempt, max_roster);
                    }
                }
                //config.printRoster("Final Max: ", max_roster);
                //max_roster = config.sortRoster(max_roster);
                return config.getRoster(lineup, max_roster);
        };

        ruckus.subpagecontrols.lineupbuilderavailableathletes.prototype.renderSliders = function () {
                var _this = this;
                var container = $('#lbaa_sliderslist');
		container.html('');

		var lastSliderId = undefined;
				
		//create a header for VICTRON container
		var victronSlidersHeader = $('<div>', {'class':'victronHeaderContent','style':'padding:30px 130px 30px 130px;margin-left:-15px;margin-right:-15px;background-color:#474747;color:#FFF;'});
		$('<span>').appendTo(victronSlidersHeader).html('Automatically build a lineup by adjusting the sliders for the stats you find most important.');
		
//				var victronSlidersHeader = "<div class='victronHeaderContent' style='padding:50px;text-align:center;margin-left:-15px;margin-right:-15px;background-color:#474747;color:#FFF;'>"
//					+ "Create a lineup based on the stats you find most important." + "</div>";
		container.prepend(victronSlidersHeader); //add header to container
		$('.exportAthletesCSV').hide(); //hide the exportCSV link
		$('.lbaa_positiontab').removeClass('tabSelected').addClass('tab');//"unselect" position tabs
		$('#lbaa_sliders').addClass('tabSelected').removeClass('tab');//"select" victron tab
				
                _this.consolelog(_this.parameters.contest.league);
                _this.consolelog(ruckus.definition[_this.parameters.contest.league.toLowerCase()].sliders);
                _this.consolelog(ruckus.rucksack);
                var sliderState = [];
                var runRucksack = function () {
			_this.parameters.lineupbuilderselectedathletes.blockFXReplacement = true;
                        _this.consolelog('athletes');
                        _this.consolelog(_this.contestAthletesModel.modelData);
                        // manipulate into expected structure
                        _this.consolelog('formatted selected');
                        var formattedSelected = [];
                        for (var x = 1; x <= _this.parameters.lineupbuilderselectedathletes.numFlex;x++){
                                 if (_this.parameters.lineupbuilderselectedathletes.selected['FX'+x] != undefined) {
                                        // if not locked then set to undefined
                                        if (_this.parameters.lineupbuilderselectedathletes.selected['FX'+x].lock != undefined && _this.parameters.lineupbuilderselectedathletes.selected['FX'+x].lock != false){
                                                 formattedSelected.push({id: _this.parameters.lineupbuilderselectedathletes.selected['FX'+x].athleteSportEventInfoId, pos: 'FX', preexisting: true, slot: 'FX'+x});
                                        } else {
                                                if(_this.parameters.lineupbuilderselectedathletes.selected['FX'+x]){
                                                        var id = _this.parameters.lineupbuilderselectedathletes.selected['FX'+x].athleteSportEventInfoId;
                                                        _this.parameters.lineupbuilderselectedathletes.removePlayer('FX'+x);
                                                        formattedSelected.push({id: id, pos: 'FX'});
                                                } else {
                                                        _this.parameters.lineupbuilderselectedathletes.removePlayer('FX'+x);
                                                        formattedSelected.push({pos: 'FX'});
                                                }
                                        }
                                }   else {
                                         formattedSelected.push({pos: 'FX'});
                                }
                        }
//                        if (_this.parameters.lineupbuilderselectedathletes.selected.FX != undefined) {
//                                if (_this.parameters.lineupbuilderselectedathletes.selected.FX.lock == undefined)
//                                        _this.parameters.lineupbuilderselectedathletes.removePlayer('FX');
//                        }
                        $.each(_this.lineupTemplate, function (k, v) {
                                var truePos = v.replace(/[0-9]/g, ''); // find the actual pos from lineup one ... RB1 -> RB
                                var pos = undefined;
                                $.each(_this.parameters.lineuprules, function (a, b) {
                                        if (truePos == b.abbreviation)
                                                pos = b.displayabbreviation;
                                });
                                if(pos != 'FX'){
                                    if (_this.parameters.lineupbuilderselectedathletes.selected[v] != undefined) {
                                            // if not locked then set to undefined
                                            if (_this.parameters.lineupbuilderselectedathletes.selected[v].lock != undefined && _this.parameters.lineupbuilderselectedathletes.selected[v].lock != false)
                                                    formattedSelected.push({id: _this.parameters.lineupbuilderselectedathletes.selected[v].athleteSportEventInfoId, pos: pos, preexisting: true, slot: v});
                                            else {
                                                    if(_this.parameters.lineupbuilderselectedathletes.selected[v]){
                                                            var id = _this.parameters.lineupbuilderselectedathletes.selected[v].athleteSportEventInfoId;
                                                            _this.parameters.lineupbuilderselectedathletes.removePlayer(v);
                                                            formattedSelected.push({id: id, pos: pos});
                                                    } else {
                                                            _this.parameters.lineupbuilderselectedathletes.removePlayer(v);
                                                            formattedSelected.push({pos: pos});
                                                    }
                                            }
                                    } else {
                                            formattedSelected.push({pos: pos});
                                    }
                                }
                        });
                        _this.consolelog(formattedSelected);
                        _this.consolelog('sliders');
                        var ss = {nfl: sliderState};
                        _this.consolelog(ss);
                        var output = _this.rucksack(_this.contestAthletesModel.modelData.athletes, formattedSelected, ss);
                        _this.consolelog('output');
                        _this.consolelog(output);
                        // make changes
                        $.each(output, function (k, v) {
                                if (!v.preexisting) {
                                        $('#lbaa_athleterow_' + v.athlete.id).hide();
                                        _this.msgBus.publish('control.lbaa.selectplayer', v.athlete);
                                }
                        });
                        $.each($('.lbsa_lock'), function(k,v){
                            if ($('#'+v.id).html() == ''){
                                $('#'+v.id).show();
                                //$('#'+v.id).html('UL');
                                $('#'+v.id.replace('lock', 'x')).hide();
                            }
                            if ($('#'+v.id).hasClass('locked')){
                                     $('#'+v.id).removeClass('iconsunlock_light');
                                     $('#'+v.id).addClass('iconslock_dark');
                            } else {
                                    $('#'+v.id).removeClass('iconslock_dark');
                                    $('#'+v.id).addClass('iconsunlock_light');
                            }
                        });
			_this.parameters.lineupbuilderselectedathletes.blockFXReplacement = false;
                };

                $.each(ruckus.definition[_this.parameters.contest.league.toLowerCase()].sliders, function (key, value) {
			if (value.id == 5)
	                        sliderState.push({id: value.id, value: Math.round((value.max)/2)/50, initialvalue: (value.max)/2});
			else
	                        sliderState.push({id: value.id, value: Math.round((value.max)/2)/15, initialvalue: (value.max)/2});
                        //Add labels before first label and slider
                        if (value.id == 0) {
                            var slidersTopLabels = $('<div>', {'class':'row', 'style':'margin-bottom:15px;margin-top:25px;'}).appendTo(container);
                            $('<div>', {'class':'col-xs-13', 'style':'text-align:right;padding-right:75px;font-size:8pt;'}).appendTo(slidersTopLabels).html('Low');
                            $('<div>', {'class':'col-xs-11', 'style':'text-align:right;padding-right:48px;font-size:8pt;'}).appendTo(slidersTopLabels).html('High');
                        }
                        
                        //Check if last slider is being rendered, and add labels above it 
                        if (value.id == 5) {
                            var slidersBottomLabels = $('<div>', {'class':'row', 'style':'margin-bottom:15px;'}).appendTo(container);
                            $('<div>', {'class':'col-xs-13', 'style':'text-align:right;padding-right:55px;font-size:8pt;'}).appendTo(slidersBottomLabels).html('Recent');
                            $('<div>', {'class':'col-xs-11', 'style':'text-align:right;padding-right:48px;font-size:8pt;'}).appendTo(slidersBottomLabels).html('Historical');
                        }
                        
                        //add label and slider
                        var labelSliderRow = $('<div>', {'class':'row', 'style':'margin-bottom:40px'}).appendTo(container);
                        $('<div>', {'class':'col-xs-9','style':'margin-top:-7px;padding-left:50px;'}).appendTo(labelSliderRow).html(value.name);
                        $('<div>', {'id': 'slide_' + value.id, 'class': 'slider'+value.id+' col-xs-13'}).appendTo(labelSliderRow);
                        
                        //Add Reset button at bottom (after last slider has been rendered)
                        if (value.id == 5)
                        {
				var resetSlidersRow = $('<div>', {'class':'row', 'style':'text-align:center;'}).appendTo(container);
				$('<span>', {'id':'resetSlidersLink'}).appendTo(resetSlidersRow).html('RESET');
				$('#resetSlidersLink').bind('click', function(evt){
					evt.stopPropagation();
					lastSliderId = undefined;
					$.each(sliderState, function(k,v){
						$('#slide_'+v.id).slider('value',v.initialvalue);
					});
				});
                        }
                        
                        //Need to add functionality to this function for first 3 sliders to operate dependent on one another.
                        //See http://jsfiddle.net/caPAb/450/
                        //Ignore styling there, just the functionality of one slider moving changing the other two is what is wanted
                        $("#slide_" + value.id).slider({
                                range: false,
				value: value.max/2,
                                min: value.min,
                                max: value.max,
                                step: 1,
                                slide: function (event, ui) {
					ui.id = event.target.id.replace('slide_','');
					lastSliderId = ui.id;
					if (ui.id <= 2){
						var total = 0;
						for (var x = 0; x <=2; x++){
							if (ui.id != x){
								total += $('#slide_'+x).slider("option", "value");
							}
						}
						total += ui.value;
						var delta = 450 - total;
						for (var x = 0; x <=2; x++){
							if (ui.id != x){
								var t = $('#slide_'+x);
								var value = t.slider("option", "value");

								var new_value = value + (delta/2);
								
								if (new_value < 0 ) 
								    new_value = 0;
								if (new_value > 300)  
								    new_value = 300;

								t.slider('value', new_value);
								$.each(sliderState, function (k, v) {
									if (k == x)
										v.value = Math.round(new_value/15);
								});	
							}
						} 
					}	
                                },
                                change: function (event, ui) {
					if (lastSliderId != undefined){ // RESET
						ui.id = event.target.id.replace('slide_','');
						if (ui.id <= 2){
							if (lastSliderId == ui.id){
								$.each(sliderState, function (k, v) {
									if (k == value.id){
										v.value = Math.round(ui.value/15);
									} 
								});
								runRucksack();
							}
						} else {
							$.each(sliderState, function (k, v) {
								if (k == value.id){
									if (ui.id == 5)
										v.value = Math.round(ui.value/50);
									else
										v.value = Math.round(ui.value/15);
								}
							});
							runRucksack();
						}
					}
                                }
                        });
                });
                
                //this.load = runRucksack();
        };

        ruckus.subpagecontrols.lineupbuilderavailableathletes.prototype.headerEvents = function () {
                var _this = this;

                // position
                $('#lbaa_posheader').bind('click', function (evt) {
                        evt.stopPropagation();
                        _this.sortMe('firstPositionDisplay');
                });
                // name
                $('#lbaa_nameheader').bind('click', function (evt) {
                        evt.stopPropagation();
                        _this.sortMe('lastName');
                });
                // salary
                $('#lbaa_salaryheader').bind('click', function (evt) {
                        evt.stopPropagation();
                        _this.sortMe('salary');
                });

                for (var x = 1; x <= 6; x++) {
                        // set sort details
                        _this.sortDetails['stat' + x] = _this.contestAthletesModel.modelData.stats['stat' + x].dir;

                        // set header name
                        $('#lbaa_stat' + x + 'header').html(_this.contestAthletesModel.modelData.stats['stat' + x].name);

                        // set click event
                        $('#lbaa_stat' + x + 'header').bind('click', {y: x}, function (evt) {
                                evt.stopPropagation();
                                _this.sortMe('stat' + evt.data.y);
                        });

                        // set hover event
                        $('#lbaa_stat' + x + 'header').bind('mouseenter', {y: x}, function (evt) {
                                evt.stopPropagation();
                                _this.popup = true;
                                var runme = function () {
                                        if (_this.popup) {
                                                if ($('#lbaa_statpopup') != undefined)
                                                        $('#lbaa_statpopup').remove();
                                                _this.renderStatsPopup(evt.data.y);
                                        }
                                };
                                setTimeout(runme, 250);
                        });
                        $('#lbaa_stat' + x + 'header').bind('mouseleave', function (evt) {
                                evt.stopPropagation();
                                _this.popup = false;
                                $('#lbaa_statpopup').remove();
                        });
                }

        };

        ruckus.subpagecontrols.lineupbuilderavailableathletes.prototype.renderStatsPopup = function (key) {
                var _this = this;
                _this.require_template('lineupbuilderavailableathletespopup-tpl');
                dust.render('dusttemplates/lineupbuilderavailableathletespopup-tpl', _this.contestAthletesModel.modelData, function (err, out) {
                        var cont = $('<div>', {'id': 'lbaa_statpopup'}).appendTo($('#lbaa_stat' + key + 'header'));
                        cont.html(out);
			_this.addScrollBars();
			$('#lbaa_statpopupdesc').html(_this.contestAthletesModel.modelData.stats['stat' + key].desc);
                        for (var x = 1; x <= 6; x++) {
                                $('#lbaa_statpopup' + x).bind('mouseover', {y: x}, function (evt) {
                                        evt.stopPropagation();
                                        $('#lbaa_statpopupdesc').html(_this.contestAthletesModel.modelData.stats['stat' + evt.data.y].desc);
                                });
                                $('#lbaa_statpopup' + x).bind('click', {y: x}, function (evt) {
                                        evt.stopPropagation();
                                        if (!$('#lbaa_stat' + evt.data.y + 'header').is(":visible")) {
                                                // swap column
                                                $('#lbaa_stat' + key + 'header').hide();
                                                $('.lbaa_stat' + key).hide();
                                                $('#lbaa_stat' + evt.data.y + 'header').show();
                                                $('.lbaa_stat' + evt.data.y).show();
                                                // move shown header column after hidden header column
                                                $('#lbaa_stat' + evt.data.y + 'header').insertAfter($('#lbaa_stat' + key + 'header'));
                                                // move shown stat column after hidden stat column
                                                $.each(_this.contestAthletesModel.modelData.athletes, function (k, v) {
                                                        $('#lbaa_' + v.id + '_stat' + evt.data.y).insertAfter($('#lbaa_' + v.id + '_stat' + key));
                                                });
                                                _this.msgBus.publish('control.lbaa.swapcolumns', {show: evt.data.y, hide: key});
                                        }
                                });
                        }
                });
        };

        ruckus.subpagecontrols.lineupbuilderavailableathletes.prototype.sortMe = function (key) {
                var _this = this;

                var dir1 = undefined;
                var dir2 = undefined;
                if (_this.sortDetails[key] == 'asc') {
                        _this.sortDetails[key] = 'desc';
                        dir1 = 1;
                        dir2 = -1;
                } else {
                        _this.sortDetails[key] = 'asc';
                        dir1 = -1;
                        dir2 = 1;
                }

                _this.contestAthletesModel.modelData.athletes.sort(function (a, b) {
                        if (a[key] < b[key])
                                return dir1;
                        if (a[key] > b[key])
                                return dir2;
                        if (a[key] === b[key]) {
                                if (a.lastname < b.lastname)
                                        return -1;
                                if (a.lastname > b.lastname)
                                        return 1;
                        }
                        return 0;
                });
                _this.renderList();
        };

        ruckus.subpagecontrols.lineupbuilderavailableathletes.prototype.renderList = function () {
                var _this = this;
                _this.recordStatOrder();
		_this.contestAthletesModel.modelData.athletesInitial = _this.contestAthletesModel.modelData.athletes.slice(0,30);
                _this.contestAthletesModel.modelData.athletesMore = _this.contestAthletesModel.modelData.athletes.slice(30,_this.contestAthletesModel.modelData.athletes.length);
                
		_this.require_template('lineupbuilderavailableathleteslist-tpl');
                dust.render('dusttemplates/lineupbuilderavailableathleteslist-tpl', _this.contestAthletesModel.modelData, function (err, out) {
                        $('#lbaa_athletelist').html(out);
			_this.addScrollBars();
			
/*
			var outOfProcess = function(){
				// render the rest of the athletes second
				_this.require_template('lineupbuilderavailableathleteslistmore' + _this.parameters.contest.league + '-tpl');
				dust.render('dusttemplates/lineupbuilderavailableathleteslistmore' + _this.parameters.contest.league + '-tpl', _this.contestAthletesModel.modelData, function (err, out) {
					$('#lbaa_athletelist').html($('#lbaa_athletelist').html()+out);
					_this.applyStatOrder();
					_this.runFilter();
					$.each(_this.contestAthletesModel.modelData.athletes, function (key, value) {
						_this.rowClickEvents(value);
					});

				});
			};
			setTimeout(outOfProcess,1);
*/                
			_this.applyStatOrder();
                        _this.runFilter();
                        $.each(_this.contestAthletesModel.modelData.athletes, function (key, value) {
                                _this.rowClickEvents(value);
                        });
		
                });

        };

        ruckus.subpagecontrols.lineupbuilderavailableathletes.prototype.recordStatOrder = function () {
                var _this = this;
                var children = $('#lbaa_header').children();
                this.statOrder = [];
                this.statVisibility = [];
                $.each(children, function (k, v) {
                        if (v.id.indexOf('stat') != -1) {
                                _this.statOrder.push(v.id.replace('lbaa_', '').replace('header', ''));
                                if ($('#' + v.id).is(":visible"))
                                        _this.statVisibility.push(v.id.replace('lbaa_', '').replace('header', ''));
                        }
                });
        };

        ruckus.subpagecontrols.lineupbuilderavailableathletes.prototype.applyStatOrder = function () {
                var _this = this;
                $.each(_this.contestAthletesModel.modelData.athletes, function (key, value) {
                        $.each(_this.statOrder, function (k, v) {
                                $('#lbaa_' + value.id + '_' + v).insertBefore($('#lbaa_' + value.id + '_salary'));
                                if (_this.statVisibility.indexOf(v) != -1)
                                        $('#lbaa_' + value.id + '_' + v).show();
                                else
                                        $('#lbaa_' + value.id + '_' + v).hide();
                        });
                });
        };

        ruckus.subpagecontrols.lineupbuilderavailableathletes.prototype.runFilter = function () {
                var _this = this;
                $.each(_this.contestAthletesModel.modelData.athletes, function (key, value) {
                        var show = true;
                        // check posFilter (position tab)
                        if (_this.posFilter != 'all') {
                                if (_this.posFilter == 'FX') {
                                        var flexfound = false;
                                        $.each(_this.flexTemplate, function (k, v) {
                                                if ($('#lbaa_athleterow_' + value.id).hasClass('lbaa_' + v))
                                                        flexfound = true;
                                        });
                                        if (!flexfound)
                                                show = false;
                                } else if (!$('#lbaa_athleterow_' + value.id).hasClass('lbaa_' + _this.posFilter)) {
                                        show = false;
                                }
                        }

                        // check evtFilter (event filter)
                        if (_this.evtFilter[0] != 'all') {
				if ($.inArray(value.eventId, _this.evtFilter) == -1)
					show = false;
                        }

                        // check schFilter (name search)
                        var arrSchFilter = _this.schFilter.split(' ');
                        if (arrSchFilter.length > 2) {
                                var first = arrSchFilter[0];
                                var last = '';
                                for (var x = 1; x < arrSchFilter.length; x++)
                                        last += arrSchFilter[x] + ' ';
                                last = last.substring(0, last.length - 1);
                                arrSchFilter = [first, last];
                        }
                        if (arrSchFilter.length == 1) { // check last name
                                if (value.lastName.toLowerCase().indexOf(arrSchFilter[0].toLowerCase()) == -1 && value.firstName.toLowerCase().indexOf(arrSchFilter[0].toLowerCase()) == -1) {
                                        show = false;
                                }
                        } else { // check first and last
                                if (!(value.firstName.toLowerCase().indexOf(arrSchFilter[0].toLowerCase()) != -1 && value.lastName.toLowerCase().indexOf(arrSchFilter[1].toLowerCase()) != -1)) {
                                        show = false;
                                }
                        }

                        // check if already selected in lbsa
                        $.each(_this.lineupTemplate, function (k, v) {
                                if (v.indexOf(value.positions[0]) != -1) {
                                        if (_this.parameters.lineupbuilderselectedathletes.selected[v] != undefined) {
                                                if (_this.parameters.lineupbuilderselectedathletes.selected[v].id == value.id)
                                                        show = false;
                                        }
                                }
                        });
			
			            for (var x = 1; x <= _this.parameters.lineupbuilderselectedathletes.numFlex;x++){
                                 if (_this.parameters.lineupbuilderselectedathletes.selected['FX'+x] != undefined) {
                                        if (_this.parameters.lineupbuilderselectedathletes.selected['FX'+x].id == value.id)
                                                show = false;
                                }
                        }
/*
                        if (_this.parameters.lineupbuilderselectedathletes.selected.FX != undefined) {
                                if (_this.parameters.lineupbuilderselectedathletes.selected.FX.id == value.id)
                                        show = false;
                        }
*/
                        // hide or show the athlete
                        if (show) {
                                $('#lbaa_athleterow_' + value.id).show().addClass("lbavail_visible");
                        } else {
                                $('#lbaa_athleterow_' + value.id).hide().removeClass("lbavail_visible");
                        }
                });
                _this.stripe();
        };

        ruckus.subpagecontrols.lineupbuilderavailableathletes.prototype.positionClickEvents = function () {
                var _this = this;
                $('.lbaa_positiontab').bind('click', function (evt) {
                        evt.stopPropagation();
						$('#lbaa_sliders').removeClass('tabSelected').addClass('tab');//"unselect" VICTRON tab
						$('.exportAthletesCSV').show();//show the exportCSV link
                        $('.lbaa_positiontab').removeClass('tab');
                        $('.lbaa_positiontab').removeClass('tabSelected');
                        $('.lbaa_positiontab').addClass('tab');
                        var pos = evt.target.id.split('_')[1];
                        $('#lbaa_' + pos).removeClass('tab');
                        $('#lbaa_' + pos).addClass('tabSelected');
                        _this.posFilter = undefined;
                        $.each(_this.parameters.lineuprules, function (k, v) {
                                if (pos == v.abbreviation.toLowerCase())
                                        _this.posFilter = v.abbreviation;
                        });
                        if (_this.posFilter == undefined)
                                _this.posFilter = 'all';
                        _this.runFilter();
                });
        };

        ruckus.subpagecontrols.lineupbuilderavailableathletes.prototype.rowClickEvents = function (value) {
                var _this = this;
                $('#lbaa_athleterow_' + value.id).click(function (evt) {
                        evt.stopPropagation();
                        $('#lbaa_athleterow_' + value.id).hide();
                        _this.msgBus.publish('control.lbaa.selectplayer', value);
                });
                $('#lbaa_plus_' + value.id).click(function (evt) {
                        evt.stopPropagation();
                        _this.msgBus.publish('control.lbaa.compareplayer', value);
                });

/*
                $('#lbaa_plus_' + value.id).click(function (evt) {
                        evt.stopPropagation();
                        $('#lbaa_athleterow_' + value.id).hide();
                        _this.msgBus.publish('control.lbaa.selectplayer', value);
                });
                $('#lbaa_athleterow_' + value.id).click(function (evt) {
                        evt.stopPropagation();
                        _this.msgBus.publish('control.lbaa.compareplayer', value);
                });
*/
        };

        ruckus.subpagecontrols.lineupbuilderavailableathletes.prototype.stripe = function () {
                this.tablestripe(".lbavail_visible");
        };

        ruckus.subpagecontrols.lineupbuilderavailableathletes.prototype.unload = function () {
                this.destroyControl();
        };

        return ruckus.subpagecontrols.lineupbuilderavailableathletes;
});
