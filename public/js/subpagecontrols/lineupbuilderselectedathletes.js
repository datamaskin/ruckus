// Author: Scott Gay
define([
        "rg_subpage_base",
        "jquery",
        "dust",
        "assets/js/models/lineupenter.js",
        "assets/js/models/athletecompare.js"
], function (Base) {
        ruckus.subpagecontrols.lineupbuilderselectedathletes = function (parameters) {
                Base.call(this);
                this.init = function () {
			//use two variables for tracking which compared player was selected to lineup by clicking row
			this.playerASelectedNumber = 0;
			this.playerBSelectedNumber = 0;
			
			//Function for adding salary container that attaches to bottom of header and scrolls with window
			//so that the user can see their salary figures at any time when scrolling down the roster list
			$(window).scroll(function(){
				var scrollbar_position = $(window).scrollTop();
				if (scrollbar_position > 145)
				{
					$('.salaryScrollContainer').show();
				}
				else
				{
					$('.salaryScrollContainer').hide();
				}
			});
			
                        var _this = this;
                        this.parameters = parameters;

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

                        this.selected = {};
			_this.numFlex = 0;
                        $.each(this.lineupTemplate, function (k, v) {
				if (v.indexOf('FX') != -1)
					_this.numFlex++;
                                _this.selected[v] = undefined;
                        });
                        this.compared = {
                                a: undefined,
                                b: undefined
                        };
					
			_this.blockCompare = false;
			_this.blockFXReplacement = false;
                };
                this.init();
        };

        ruckus.subpagecontrols.lineupbuilderselectedathletes.prototype = Object.create(Base.prototype);
        ruckus.subpagecontrols.lineupbuilderselectedathletes.prototype.load = function () {
                var _this = this;
                this.getContainer();
                this.container.addClass('ruckus-spc-lineupbuilderselectedathletes');

                this.require_template('lineupbuilderselectedathletes' + _this.parameters.contest.league + 'flex-tpl');
                dust.render('dusttemplates/lineupbuilderselectedathletes' + _this.parameters.contest.league + 'flex-tpl', {}, function (err, out) {
                        _this.container.html(out);
			_this.addScrollBars();
                        _this.removePlayerEvents();
//			_this.compareEvents();
                        _this.updateSalary();
                        $('#lbsa_contestenter').bind('click', function (evt) {
                                evt.stopPropagation();
	                        _this.enterContest();
                        });

                        $('#lbsa_entererrorclear').bind('click', function (evt) {
                                evt.stopPropagation();
                                $('#lbsa_entererror').hide();
                        });

                        $('#lbsa_clearallselected').bind('click', function (evt) {
                                evt.stopPropagation();
				for (var x = 1;x <= _this.numFlex;x++){
					if (_this.selected['FX'+x] != undefined) {
						_this.removePlayer('FX'+x);
					}
				}
                                $.each(_this.selected, function (k, v) {
                                        _this.removePlayer(k);
                                });
                        });

			// clear all 
			var subImportClear = _this.msgBus.subscribe("controls.import.clearall", function(){
				for (var x = 1;x <= _this.numFlex;x++){
                                        if (_this.selected['FX'+x] != undefined) {
                                                _this.removePlayer('FX'+x);
                                        }
                                }
                                $.each(_this.selected, function (k, v) {
                                        _this.removePlayer(k);
                                });
			});
			_this.subscriptions.push(subImportClear);
			
			$('.lbsa_lock').bind('click', function(evt){
				evt.stopPropagation();
				var pos = evt.delegateTarget.id.split('_')[1].replace('normallock','').toUpperCase();
				if ($('#'+evt.delegateTarget.id).hasClass('iconslock_dark')){
                        $('#'+evt.delegateTarget.id).removeClass('iconslock_dark');
                        $('#'+evt.delegateTarget.id).addClass('iconsunlock_light');
                        $('#'+evt.delegateTarget.id).removeClass('locked');
                        _this.selected[pos].lock = false;
                } else {
                        $('#'+evt.delegateTarget.id).removeClass('iconsunlock_light');
                        $('#'+evt.delegateTarget.id).addClass('iconslock_dark');
                        $('#'+evt.delegateTarget.id).addClass('locked');
                        _this.selected[pos].lock = true;
                }

			});
                });

                // listen for when lineupbuilderavailableathletes fires event for athlete selected
                var sub = this.msgBus.subscribe('control.lbaa.selectplayer', function (data) {
                        _this.log({type: 'event', data: data, msg: 'PLAYER SELECTED'});
			if (!_this.blockCompare){
	                        _this.selectPlayer(data);
			} else {
				$('#lbaa_athleterow_' + data.id).show();
			}
//                        _this.selectPlayer(data);
                });
                this.subscriptions.push(sub);
                var sub2 = this.msgBus.subscribe('control.lbaa.compareplayer', function (data) {
                        _this.log({type: 'event', data: data, msg: 'PLAYER COMPARE'});
                        _this.comparePlayer(data);
                });
                this.subscriptions.push(sub2);
                var sub3 = this.msgBus.subscribe('control.lbaa.swapcolumns', function (data) {
                        _this.log({type: 'event', data: data, msg: 'SWAP COLUMNS'});
                        _this.swapColumns(data);
                });
                this.subscriptions.push(sub3);
                var sub4 = this.msgBus.subscribe('control.lbaa.slideron', function (data) {
                        _this.log({type: 'event', data: data, msg: 'SLIDER ON'});
                        $.each(_this.lineupTemplate, function (k, v) {
                                $('#lbsa_' + v.toLowerCase() + 'normalx').hide();
                                if (_this.selected[v] != undefined) {
                                        _this.selected[v].lock = true;
                                        $('#lbsa_' + v.toLowerCase() + 'normallock').show();
					                    $('#lbsa_' + v.toLowerCase() + 'normallock').addClass('iconslock_dark');
					                    $('#lbsa_' + v.toLowerCase() + 'normallock').addClass('locked');
//					                    $('#lbsa_' + v.toLowerCase() + 'normallock').html('L');
                                }
                        });
                });
                this.subscriptions.push(sub4);

                var sub5 = this.msgBus.subscribe('control.lbaa.slideroff', function (data) {
                        _this.log({type: 'event', data: data, msg: 'SLIDER OFF'});
                        $.each(_this.lineupTemplate, function (k, v) {
                                $('#lbsa_' + v.toLowerCase() + 'normallock').hide();
                                $('#lbsa_' + v.toLowerCase() + 'normallock').html('');
                                if (_this.selected[v] != undefined) {
                                        _this.selected[v].lock = undefined;
                                        $('#lbsa_' + v.toLowerCase() + 'normalx').show();
                                }
                        });
                });
                this.subscriptions.push(sub5);
        };


        ruckus.subpagecontrols.lineupbuilderselectedathletes.prototype.swapColumns = function (data) {
                var _this = this;
                $.each(_this.lineupTemplate, function (key, value) {
                        $('#lbsa_' + value.toLowerCase() + 'normalstat' + data.hide).hide();
                        $('#lbsa_' + value.toLowerCase() + 'normalstat' + data.show).show();
                        $('#lbsa_' + value.toLowerCase() + 'overlaystat' + data.hide).hide();
                        $('#lbsa_' + value.toLowerCase() + 'overlaystat' + data.show).show();
                        $('#lbsa_' + value.toLowerCase() + 'normalstat' + data.show).insertAfter($('#lbsa_' + value.toLowerCase() + 'normalstat' + data.hide));
                        $('#lbsa_' + value.toLowerCase() + 'overlaystat' + data.show).insertAfter($('#lbsa_' + value.toLowerCase() + 'overlaystat' + data.hide));
                });
        };

        ruckus.subpagecontrols.lineupbuilderselectedathletes.prototype.enterContest = function () {
                var _this = this;

                var invert = function (obj) {
                        var new_obj = {};
                        for (var prop in obj) {
                                if (obj.hasOwnProperty(prop)) {
                                        new_obj[obj[prop].abbreviation] = prop;
                                }
                        }
                        return new_obj;
                };
                var revMapping = invert(this.parameters.lineuprules);

                var validPositions = true;
                var validEvents = false;
                var validSalary = false;
                var cap = 0;
                var eventId = undefined;
                var athletes = [];
                $.each(this.lineupTemplate, function (k, v) {
                        var truePos = v.replace(/[0-9]/g, ''); // find the actual pos from lineup one ... RB1 -> RB
                        if (_this.selected[v] != undefined) {
                                cap += _this.selected[v].salary;
                                if (eventId != undefined) {
                                        if (eventId != _this.selected[v].eventId)
                                                validEvents = true;
                                } else {
                                        eventId = _this.selected[v].eventId;
                                }
                                athletes.push({id: _this.selected[v].id, pos: revMapping[truePos], athleteSportEventInfoId: _this.selected[v].athleteSportEventInfoId});
                        } else {
                                validPositions = false;
                        }
                });

                if (cap <= this.parameters.contest.salaryCap)
                        validSalary = true;

                if (validPositions && validEvents && validSalary) {
			if (_this.parameters.editLineup == undefined){
				var subSuccess = undefined;
				var subFailed = undefined;
				subSuccess = _this.msgBus.subscribe("model.lineupenter.success", function (data) {
					subSuccess.unsubscribe();
					subFailed.unsubscribe();
					_this.msgBus.publish("control.lbsa.lineupentersuccess", {contestId: _this.parameters.contest.id, athletes: athletes, result: data});
				});
				subFailed = _this.msgBus.subscribe("model.lineupenter.failed", function (data) {
					subSuccess.unsubscribe();
					subFailed.unsubscribe();
					_this.msgBus.publish("control.lbsa.lineupenterfailed", {contestId: _this.parameters.contest.id, athletes: athletes, result: data});
				});
				this.lineupEnterModel = new ruckus.models.lineupenter({});
				this.models.push(this.lineupEnterModel);
				this.lineupEnterModel.fetch({entries:[{contestId:_this.parameters.contest.id,multiple:1}], athletes: athletes});
			} else {
				_this.msgBus.publish('control.lbsa.editlineup',{data:_this.selected});
			}
                }
                if (!validPositions) {
                        // FIXME - display error screen for lineup incomplete here
                        $('#lbsa_entererror').show();
                        $('#lbsa_entererrortext').html('There are not enough players in your lineup.');
                }
                if (!validEvents) {
                        $('#lbsa_entererror').show();
                        $('#lbsa_entererrortext').html('There must be players selected from at least 2 sport events.');
                }
                if (!validSalary) {
                        $('#lbsa_entererror').show();
                        $('#lbsa_entererrortext').html('Your selected players exceed the salary cap for this contest.');
                }


        };

        ruckus.subpagecontrols.lineupbuilderselectedathletes.prototype.renderPlayer = function (data, div) {
                var _this = this;
                $('#lbsa_' + div + 'normalposition').html(data.firstPositionDisplay);
                $('#lbsa_' + div + 'normalname').html(data.firstName + ' ' + data.lastName);
                $('#lbsa_' + div + 'normalstat1').html(data.matchup);
                $('#lbsa_' + div + 'normalstat2').html(data.stat2);
                $('#lbsa_' + div + 'normalstat3').html(data.stat3);
                $('#lbsa_' + div + 'normalstat4').html(data.stat4);
                $('#lbsa_' + div + 'normalstat5').html(data.stat5);
                $('#lbsa_' + div + 'normalstat6').html(data.stat6);
                $('#lbsa_' + div + 'normalsalary').html(_this.formatMoney(data.salary));
                $('#lbsa_' + div + 'normalimage').html('<img src="' + data.image + '-40x40.png" style="width:40px;height:40px;" />');
                $('#lbsa_' + div + 'overlayname').html(data.firstName + ' ' + data.lastName);
                $('#lbsa_' + div + 'overlaystat1').html(data.stat1);
                $('#lbsa_' + div + 'overlaystat2').html(data.stat2);
                $('#lbsa_' + div + 'overlaystat3').html(data.stat3);
                $('#lbsa_' + div + 'overlaystat4').html(data.stat4);
                $('#lbsa_' + div + 'overlaystat5').html(data.stat5);
                $('#lbsa_' + div + 'overlaystat6').html(data.stat6);
                $('#lbsa_' + div + 'overlaysalary').html(_this.formatMoney(data.salary));
                $('#lbsa_' + div + 'overlayimage').html('<img src="' + data.image + '-40x40.png" style="width:40px;height:40px;" />');

                // small list
                $('#lbsa_' + div + 'lsnormalposition').html(data.firstPositionDisplay);
                $('#lbsa_' + div + "lsnormalimage").html('<img src="' + data.image + '-40x40.png" style="width:40px;height:40px;" /></div>');
                $('#lbsa_' + div + "lsnormalname").html(data.firstName.substring(0, 1) + ' ' + data.lastName);
                $('#lbsa_' + div + "lsnormalmatchup").html(data.stat1);
                $('#lbsa_' + div + "lsnormalsalary").html(_this.formatMoney(data.salary));

                $('#lbsa_' + div + 'lsoverlayposition').html(data.firstPositionDisplay);
                $('#lbsa_' + div + "lsoverlayimage").html('<img src="' + data.image + '-40x40.png" style="width:40px;height:40px;" /></div>');
                $('#lbsa_' + div + "lsoverlayname").html(data.firstName.substring(0, 1) + ' ' + data.lastName);
                $('#lbsa_' + div + "lsoverlaymatchup").html(data.stat1);
                $('#lbsa_' + div + "lsoverlaysalary").html(_this.formatMoney(data.salary));

//		$('#lbsa_'+div+"lsnormal").html('<div class="row"><img src="'+data.image+'-40x40.png" style="width:40px;height:40px;" /></div>');
//		$('#lbsa_'+div+"lsoverlay").html('<div class="row"><img src="'+data.image+'-40x40.png" style="width:40px;height:40px;" /></div>');
        };

        ruckus.subpagecontrols.lineupbuilderselectedathletes.prototype.clearPlayerDivs = function (div) {
                $('#lbsa_' + div + 'normalposition').html('');
                $('#lbsa_' + div + 'normalname').html('');
                $('#lbsa_' + div + 'normalstat1').html('');
                $('#lbsa_' + div + 'normalstat2').html('');
                $('#lbsa_' + div + 'normalstat3').html('');
                $('#lbsa_' + div + 'normalstat4').html('');
                $('#lbsa_' + div + 'normalstat5').html('');
                $('#lbsa_' + div + 'normalstat6').html('');
                $('#lbsa_' + div + 'normalsalary').html('');
                $('#lbsa_' + div + 'normalimage').html('');
                $('#lbsa_' + div + 'overlayname').html('');
                $('#lbsa_' + div + 'overlaystat1').html('');
                $('#lbsa_' + div + 'overlaystat2').html('');
                $('#lbsa_' + div + 'overlaystat3').html('');
                $('#lbsa_' + div + 'overlaystat4').html('');
                $('#lbsa_' + div + 'overlaystat5').html('');
                $('#lbsa_' + div + 'overlaystat6').html('');

                // small list
                $('#lbsa_' + div + 'lsnormalposition').html('');
                $('#lbsa_' + div + "lsnormalimage").html('');
                $('#lbsa_' + div + "lsnormalname").html('');
                $('#lbsa_' + div + "lsnormalmatchup").html('');
                $('#lbsa_' + div + "lsnormalsalary").html('');

                $('#lbsa_' + div + 'lsoverlayposition').html('');
                $('#lbsa_' + div + "lsoverlayimage").html('');
                $('#lbsa_' + div + "lsoverlayname").html('');
                $('#lbsa_' + div + "lsoverlaymatchup").html('');
                $('#lbsa_' + div + "lsoverlaysalary").html('');

//                $('#lbsa_'+div+'overlaysalary').html('');
//                $('#lbsa_'+div+'overlayimage').html('');
        };

        ruckus.subpagecontrols.lineupbuilderselectedathletes.prototype.removePlayerEvents = function () {
                var _this = this;
                $.each(this.lineupTemplate, function (k, v) {
                        $('#lbsa_' + v.toLowerCase() + 'normalx').click(function (evt) {
                                evt.stopPropagation();
                                _this.removePlayer(v);
                        });
                        $('#lbsa_' + v.toLowerCase() + 'lsnormalx').click(function (evt) {
                                evt.stopPropagation();
                                _this.removePlayer(v);
                        });
                });

        };

        ruckus.subpagecontrols.lineupbuilderselectedathletes.prototype.removePlayer = function (v) {
                var _this = this;
                _this.selected[v] = undefined;
                _this.msgBus.publish('control.lbsa.showplayer', _this.selected[v]);
                _this.clearPlayerDivs(v.toLowerCase());
//		$('#lbsa_'+v.toLowerCase()+'lsnormal').html('');
//		$('#lbsa_'+v.toLowerCase()+'lsoverlay').html('');
                $('#lbsa_' + v.toLowerCase() + 'normalx').hide();
                $('#lbsa_' + v.toLowerCase() + 'lsnormalx').hide();
                $('#lbsa_' + v.toLowerCase() + 'normallock').hide();
                $('#lbsa_' + v.toLowerCase() + 'normallock').html('');
		$('#lbsa_' + v.toLowerCase() + 'lsoverlay').hide();
		$('#lbsa_' + v.toLowerCase() + 'lsnormal').show();
		$('#lbsa_' + v.toLowerCase() + 'overlayrow').hide();
		$('#lbsa_' + v.toLowerCase() + 'normalrow').show();
                _this.updateSalary();
                if (_this.flexTemplate.indexOf(v.replace(/[0-9]/g, '')) != -1) {
			if (!_this.blockFXReplacement)
				_this.checkFX(v);
                }
        };

        ruckus.subpagecontrols.lineupbuilderselectedathletes.prototype.checkFX = function (openPos) {
                var _this = this;
		$.each(this.selected, function(k,v){
                         if (k.indexOf('FX') != -1){
                                 if (_this.selected[k] != undefined){
                                         if (_this.selected[k].firstPositionDisplay == openPos.replace(/[0-9]/g,'')) {
                                                 _this.selectPlayer(_this.selected[k]);
                                                 _this.removePlayer(k);
                                         }
                                 }
                         }
                });

/*
                if (this.selected.FX != undefined) {
                        if (this.selected.FX.firstPositionDisplay == openPos.replace(/[0-9]/g, '')) {
                                this.selectPlayer(this.selected.FX);
                                this.removePlayer('FX');
                        }
                }
*/
        };

        ruckus.subpagecontrols.lineupbuilderselectedathletes.prototype.updateSalary = function () {
                var _this = this;
                var totalSpent = 0;
                var totalFilled = 0;
                $.each(this.lineupTemplate, function (k, v) {
                        if (_this.selected[v] != undefined) {
                                totalSpent += _this.selected[v].salary;
                                totalFilled++;
                        }
                });
                var totalRemaining = this.parameters.contest.salaryCap - totalSpent;
		
		//Add salary remaining to corresponding unit on salary scroll container (#ssc_salaryremaining)
                $('#lbsa_salaryremaining, #ssc_salaryremaining').html(_this.formatMoney(totalRemaining));
		
		//make salary scroll container element also turn red if salary < 0
		if (totalRemaining < 0)
		{
			$('#lbsa_salaryremaining, #ssc_salaryremaining').css('color','#ff0000');
		}
		//make salary scroll container element also turn black if salary > 0
		else
		{
			$('#lbsa_salaryremaining, #ssc_salaryremaining').css('color','#000000');
		}
		
		//Add salary scroll container element of salary remaining per player to list of elements to use
                var el_lbsa_salaryremainingplayer = $('#lbsa_salaryremainingplayer, #ssc_salaryremainingplayer');
                var num_positions = _this.lineupTemplate.length;  // should be variable based on league

                if (totalFilled != num_positions) {
//                        var remainingSalaryPlayer = Math.floor((totalRemaining / (num_positions - totalFilled)) / 100);
                        var remainingSalaryPlayer = Math.floor((totalRemaining / (num_positions - totalFilled)));
                        if (totalRemaining > 0)
                                if (remainingSalaryPlayer < 0)
                                        el_lbsa_salaryremainingplayer.html(_this.formatMoney(0).split('.')[0]);
                                else
                                        el_lbsa_salaryremainingplayer.html(_this.formatMoney(remainingSalaryPlayer).split('.')[0]);
                        else
                                el_lbsa_salaryremainingplayer.html(_this.formatMoney(0).split('.')[0]);
                }
                else {
                        el_lbsa_salaryremainingplayer.html(_this.formatMoney(0).split('.')[0]);
                }

/*
                if (totalFilled != num_positions) {
                        var remainingSalaryPlayer = Math.floor((totalRemaining / (num_positions - totalFilled)) / 100);
                        if (totalRemaining > 0)
                                if (remainingSalaryPlayer < 0)
                                        el_lbsa_salaryremainingplayer.html('$0');
                                else
                                        el_lbsa_salaryremainingplayer.html('$' + remainingSalaryPlayer);
                        else
                                el_lbsa_salaryremainingplayer.html('$0');
                }
                else {
                        el_lbsa_salaryremainingplayer.html('$0');
                }
*/
        };

	ruckus.subpagecontrols.lineupbuilderselectedathletes.prototype.checkCompareRemove = function(id){
		var _this = this;
		if (_this.compared.a != undefined){
			if (id == _this.compared.a.player.id){
				_this.compared.a = undefined;
				$('#lbsa_comparenormala').html('');
			} 
		}

		if (_this.compared.b != undefined){
			if (id == _this.compared.b.player.id){
				_this.compared.b = undefined;
				$('#lbsa_comparenormalb').html('');
			}
		}
		this.checkVisibleSections();
	};

        ruckus.subpagecontrols.lineupbuilderselectedathletes.prototype.selectPlayer = function (data) {
                var _this = this;
                // hide all overlays first
                $.each(this.lineupTemplate, function (k, v) {
                        $('#lbsa_' + v.toLowerCase() + 'normalrow').show();
                        $('#lbsa_' + v.toLowerCase() + 'overlayrow').hide();
                        $('#lbsa_' + v.toLowerCase() + 'lsnormal').show();
                        $('#lbsa_' + v.toLowerCase() + 'lsoverlay').hide();
                });
		
		/*The following lines remove highlight and change icon color in the instance that a
		* player is selected by clicking on the row while being compared.  These things need to happen
		* so that when a player is removed from the lineup after being selected, his row is not still highlighted
		* in the list of available athletes.
		*/
		//un-highlight row if player is selcted by clicking row while in player compare
		$('#lbaa_athleterow_' + data.id).removeClass('athleteRowHighlight');
		//change info icon back to dark when row un-highlighted
		$('#lbaa_plus_' + data.id).removeClass('iconsinfo_white').addClass('iconsinfo_dark');
		
		//if this player's number equals playerASelected Number, show the outline on the left
		if(_this.playerASelectedNumber == data.id)
		{
			$('.compareOutline').addClass('compareOutlineLeft').show();
		}
		//otherwise, if this player's number equals playerBSelected Number, show the outline on the right
		else if(_this.playerBSelectedNumber == data.id)
		{
			$('.compareOutline').removeClass('compareOutlineLeft').show();
		}
		
                var playerSet = false;
                $.each(this.parameters.lineuprules, function (key, value) {  // loop through standard positions
                        if (data.firstPosition == value.abbreviation) {  // find player position in question
                                $.each(_this.lineupTemplate, function (k, v) {  // loop through lineup positions
                                        if (v.indexOf(value.abbreviation) != -1) {  // find if lineup position is of type of the standard position in question
                                                if (_this.selected[v] == undefined && !playerSet) {
                                                        playerSet = true;
                                                        _this.selected[v] = data;
                                                        _this.renderPlayer(data, v.toLowerCase());
                                                        $('#lbsa_' + v.toLowerCase() + 'normalx').show();
                                                        $('#lbsa_' + v.toLowerCase() + 'lsnormalx').show();

                                                        _this.msgBus.publish('control.lbsa.hideplayer', data);
							_this.checkCompareRemove(data.id);
                                                        _this.updateSalary();
                                                }
                                        }
                                });
                        }
                });
                if (!playerSet) { // handle flex
                        if (_this.flexTemplate.indexOf(data.firstPosition) != -1) {
				for (var x = 1;x <= _this.numFlex;x++){
					if (_this.selected['FX'+x] == undefined && !playerSet) {
						playerSet = true;
						_this.selected['FX'+x] = data;
						_this.renderPlayer(data, 'fx'+x);
						$('#lbsa_fx'+x+'normalx').show();
						$('#lbsa_fx'+x+'lsnormalx').show();
						_this.msgBus.publish('control.lbsa.hideplayer', data);
						_this.checkCompareRemove(data.id);
						_this.updateSalary();
					}
				}
                        }
                }
                if (!playerSet) { // handle overlay
                        $.each(_this.lineupTemplate, function (k, v) {
                                if (v.indexOf(data.firstPosition) != -1) {
                                        $('#lbsa_' + v.toLowerCase() + 'normalrow').hide(); // lower list
                                        $('#lbsa_' + v.toLowerCase() + 'overlayrow').show();
                                        $('#lbsa_' + v.toLowerCase() + 'lsnormal').hide(); // top list
                                        $('#lbsa_' + v.toLowerCase() + 'lsoverlay').show();
					
					/*unhighlight all rows that are not being compared
					* (if they are being compared, the row(s) should remain highlighted)
					*/
					$('.athleteRowHighlight').each(function () {
						if(!$(this).hasClass('row_being_compared'))
						{
						   $(this).removeClass('athleteRowHighlight');
						   $(this).children('.iconsinfo_white').removeClass('iconsinfo_white').addClass('iconsinfo_dark');
						}
					});
					//Highlight row if player is selcted by clicking row while with overlay active
					$('#lbaa_athleterow_' + data.id).addClass('athleteRowHighlight');
					//change info icon back to dark when row un-highlighted
					$('#lbaa_plus_' + data.id).addClass('iconsinfo_white').removeClass('iconsinfo_dark');
                                        
					$('#lbsa_' + v.toLowerCase() + 'overlayrow').bind('click', function (evt) {
                                                
						//un-highlight row if player is replaced by clicking overlay
						$('#lbaa_athleterow_' + data.id).removeClass('athleteRowHighlight row_being_compared');
						//change info icon back to dark when row un-highlighted
						$('#lbaa_plus_' + data.id).removeClass('iconsinfo_white').addClass('iconsinfo_dark');
						
						evt.stopPropagation();
                                                _this.selected[v] = data;
						_this.checkCompareRemove(data.id);
                                                _this.msgBus.publish('control.lbsa.hideplayer', data);
                                                _this.msgBus.publish('control.lbsa.showplayer', _this.selected[v]);
                                                _this.renderPlayer(data, v.toLowerCase());
                                                $('#lbsa_' + v.toLowerCase() + 'normalx').show();
                                                $('#lbsa_' + v.toLowerCase() + 'lsnormalx').show();
                                                var truePos = v.replace(/[0-9]/g, '');
                                                $.each(_this.lineupTemplate, function (a, b) { // loop through all instances of same position
                                                        if (b.indexOf(truePos) != -1) {
                                                                $('#lbsa_' + b.toLowerCase() + 'normalrow').show();
                                                                $('#lbsa_' + b.toLowerCase() + 'overlayrow').hide();
                                                                $('#lbsa_' + b.toLowerCase() + 'lsnormal').show();
                                                                $('#lbsa_' + b.toLowerCase() + 'lsoverlay').hide();
                                                                $('#lbsa_' + b.toLowerCase() + 'overlayrow').unbind('click');
                                                                $('#lbsa_' + b.toLowerCase() + 'lsoverlay').unbind('click');
                                                        }
                                                });
                                                if (_this.flexTemplate.indexOf(truePos) != -1) { // handle if flex position
							for (var x = 1;x <= _this.numFlex;x++){
								$('#lbsa_fx'+x+'normalrow').show();
								$('#lbsa_fx'+x+'overlayrow').hide();
								$('#lbsa_fx'+x+'lsnormal').show();
								$('#lbsa_fx'+x+'lsoverlay').hide();
								$('#lbsa_fx'+x+'overlayrow').unbind('click');
								$('#lbsa_fx'+x+'lsoverlay').unbind('click');
							}
                                                }
                                                _this.updateSalary();
                                        });
                                        $('#lbsa_' + v.toLowerCase() + 'lsoverlay').bind('click', function (evt) {
                                                
						//un-highlight row if player is replaced by clicking overlay
						$('#lbaa_athleterow_' + data.id).removeClass('athleteRowHighlight row_being_compared');
						//change info icon back to dark when row un-highlighted
						$('#lbaa_plus_' + data.id).removeClass('iconsinfo_white').addClass('iconsinfo_dark');
						
						evt.stopPropagation();
                                                _this.selected[v] = data;
						_this.checkCompareRemove(data.id);
                                                _this.msgBus.publish('control.lbsa.hideplayer', data);
                                                _this.msgBus.publish('control.lbsa.showplayer', _this.selected[v]);
                                                _this.renderPlayer(data, v.toLowerCase());
                                                $('#lbsa_' + v.toLowerCase() + 'normalx').show();
                                                $('#lbsa_' + v.toLowerCase() + 'lsnormalx').show();
                                                var truePos = v.replace(/[0-9]/g, '');
                                                $.each(_this.lineupTemplate, function (a, b) { // loop through all instances of same position
                                                        if (b.indexOf(truePos) != -1) {
                                                                $('#lbsa_' + b.toLowerCase() + 'normalrow').show();
                                                                $('#lbsa_' + b.toLowerCase() + 'overlayrow').hide();
                                                                $('#lbsa_' + b.toLowerCase() + 'lsnormal').show();
                                                                $('#lbsa_' + b.toLowerCase() + 'lsoverlay').hide();
                                                                $('#lbsa_' + b.toLowerCase() + 'overlayrow').unbind('click');
                                                                $('#lbsa_' + b.toLowerCase() + 'lsoverlay').unbind('click');
                                                        }
                                                });
                                                if (_this.flexTemplate.indexOf(truePos) != -1) { // handle if flex position
							for (var x = 1;x <= _this.numFlex;x++){
								$('#lbsa_fx'+x+'normalrow').show();
								$('#lbsa_fx'+x+'overlayrow').hide();
								$('#lbsa_fx'+x+'lsnormal').show();
								$('#lbsa_fx'+x+'lsoverlay').hide();
								$('#lbsa_fx'+x+'overlayrow').unbind('click');
								$('#lbsa_fx'+x+'lsoverlay').unbind('click');
							}
                                                }
                                                _this.updateSalary();
                                        });
                                }
                        });
                        if (_this.flexTemplate.indexOf(data.firstPosition) != -1) { // handle flex overlay
				for (var x = 1;x <= _this.numFlex;x++){
					$('#lbsa_fx'+x+'normalrow').hide();
					$('#lbsa_fx'+x+'overlayrow').show();
					$('#lbsa_fx'+x+'lsnormal').hide();
					$('#lbsa_fx'+x+'lsoverlay').show();
					$('#lbsa_fx'+x+'overlayrow').bind('click', {key:x}, function (evt) {
						
						//un-highlight row if player is replaced by clicking overlay
						$('#lbaa_athleterow_' + data.id).removeClass('athleteRowHighlight row_being_compared');
						//change info icon back to dark when row un-highlighted
						$('#lbaa_plus_' + data.id).removeClass('iconsinfo_white').addClass('iconsinfo_dark');
						
						evt.stopPropagation();
						_this.selected['FX'+evt.data.key] = data;
						_this.checkCompareRemove(data.id);
						_this.msgBus.publish('control.lbsa.hideplayer', data);
						_this.msgBus.publish('control.lbsa.showplayer', _this.selected['FX'+evt.data.key]);
						_this.renderPlayer(data, 'fx'+evt.data.key);
						$('#lbsa_fx'+evt.data.key+'normalx').show();
						$('#lbsa_fx'+evt.data.key+'lsnormalx').show();
						$.each(_this.lineupTemplate, function (l, k) {
							var truePos = k.replace(/[0-9]/g, '');
							if (_this.flexTemplate.indexOf(truePos) != -1) {
								$('#lbsa_' + k.toLowerCase() + 'normalrow').show();
								$('#lbsa_' + k.toLowerCase() + 'overlayrow').hide();
								$('#lbsa_' + k.toLowerCase() + 'lsnormal').show();
								$('#lbsa_' + k.toLowerCase() + 'lsoverlay').hide();
								$('#lbsa_' + k.toLowerCase() + 'overlayrow').unbind('click');
								$('#lbsa_' + k.toLowerCase() + 'lsoverlay').unbind('click');
							}
						});
						for (var p = 1;p <= _this.numFlex;p++){
							$('#lbsa_fx'+p+'normalrow').show();
							$('#lbsa_fx'+p+'overlayrow').hide();
							$('#lbsa_fx'+p+'lsnormal').show();
							$('#lbsa_fx'+p+'lsoverlay').hide();
							$('#lbsa_fx'+p+'overlayrow').unbind('click');
							$('#lbsa_fx'+p+'lsoverlay').unbind('click');
						}
						_this.updateSalary();
					});
					$('#lbsa_fx'+x+'lsoverlay').bind('click', {key:x}, function (evt) {
						
						//un-highlight row if player is replaced by clicking overlay
						$('#lbaa_athleterow_' + data.id).removeClass('athleteRowHighlight row_being_compared');
						//change info icon back to dark when row un-highlighted
						$('#lbaa_plus_' + data.id).removeClass('iconsinfo_white').addClass('iconsinfo_dark');
						
						evt.stopPropagation();
						_this.selected['FX'+evt.data.key] = data;
						_this.checkCompareRemove(data.id);
						_this.msgBus.publish('control.lbsa.hideplayer', data);
						_this.msgBus.publish('control.lbsa.showplayer', _this.selected['FX'+evt.data.key]);
						_this.renderPlayer(data, 'fx'+evt.data.key);
						$('#lbsa_fx'+evt.data.key+'normalx').show();
						$('#lbsa_fx'+evt.data.key+'lsnormalx').show();
						$.each(_this.lineupTemplate, function (l, k) {
							var truePos = k.replace(/[0-9]/g, '');
							if (_this.flexTemplate.indexOf(truePos) != -1) {
								$('#lbsa_' + k.toLowerCase() + 'normalrow').show();
								$('#lbsa_' + k.toLowerCase() + 'overlayrow').hide();
								$('#lbsa_' + k.toLowerCase() + 'lsnormal').show();
								$('#lbsa_' + k.toLowerCase() + 'lsoverlay').hide();
								$('#lbsa_' + k.toLowerCase() + 'overlayrow').unbind('click');
								$('#lbsa_' + k.toLowerCase() + 'lsoverlay').unbind('click');
							}
						});
						for (var p = 1;p <= _this.numFlex;p++){
							$('#lbsa_fx'+p+'normalrow').show();
							$('#lbsa_fx'+p+'overlayrow').hide();
							$('#lbsa_fx'+p+'lsnormal').show();
							$('#lbsa_fx'+p+'lsoverlay').hide();
							$('#lbsa_fx'+p+'overlayrow').unbind('click');
							$('#lbsa_fx'+p+'lsoverlay').unbind('click');
						}
						_this.updateSalary();
					});
				}
                        }
                }
                _this.msgBus.publish('control.lbsa.showplayer', data);

        };

        ruckus.subpagecontrols.lineupbuilderselectedathletes.prototype.comparePlayer = function (data) {
                var _this = this;
		// block while one is processing
		if (!_this.blockCompare){
			_this.blockCompare = true;
			var proceed = true;
			if (_this.compared.a != undefined) {
				if (_this.compared.a.player.id == data.id){
					proceed = false;
//					_this.blockCompare = false;
				}
			}
			if (_this.compared.b != undefined) {
				if (_this.compared.b.player.id == data.id){
					proceed = false;
//					_this.blockCompare = false;
				}
			}
			if (proceed) {
				_this.athleteCompareModel = new ruckus.models.athletecompare({});
				_this.models.push(_this.athleteCompareModel);
				var sub = _this.msgBus.subscribe("model.athletecompare.retrieve", function (dataAC) {
					sub.unsubscribe();
//					_this.blockCompare = false;
					if (_this.compared.a == undefined) {
						_this.compared.a = {player: data, athletecompare: _this.athleteCompareModel.modelData};
						_this.renderCompare(_this.compared.a, 'a');
						
						//track a's player number
						_this.playerASelectedNumber = _this.compared.a.player.id;
						$('.compareOutline').show();//remove dashed container
						_this.checkVisibleSections();
					} else if (_this.compared.b == undefined) {
						_this.compared.b = {player: data, athletecompare: _this.athleteCompareModel.modelData};
						_this.renderCompare(_this.compared.b, 'b');
						
						//track b's player number
						_this.playerBSelectedNumber = _this.compared.b.player.id;
						$('.compareOutline').hide();//remove dashed container
						_this.checkVisibleSections();
					} else {
						// ask where to put them
						$('#lbsa_listlarge').fadeOut(1000);
						setTimeout(function () {
							$('#lbsa_listsmall').fadeIn();
							$('#lbsa_compare').fadeIn();
						}, 1000);
						
						//Hide the select and remove buttons at the bottom of athlete container
						//when a third player is selected (replace and cancel buttons show)
						$('.selectContainer, .removeContainer').hide();
						
						$('#lbsa_comparenormal_' + _this.compared.a.player.id).hide();
						$('#lbsa_comparenormal_' + _this.compared.b.player.id).hide();
						$('#lbsa_compareoverlay_' + _this.compared.a.player.id).show();
						$('#lbsa_compareoverlay_' + _this.compared.b.player.id).show();
						$('#lbsa_comparereplace_' + _this.compared.a.player.id).unbind();
						$('#lbsa_comparereplace_' + _this.compared.a.player.id).bind('click', function (evt) {
							//un-highlight row if player is removed from compare
							$('#lbaa_athleterow_' + _this.compared.a.player.id).removeClass('athleteRowHighlight row_being_compared'); 
							//change info icon back to dark when row un-highlighted
							$('#lbaa_plus_' + _this.compared.a.player.id).removeClass('iconsinfo_white').addClass('iconsinfo_dark');
							//hide outline if player is replaced
							$('.compareOutline').hide();
							
							evt.stopPropagation();
							_this.compared.a = undefined;
							$('#lbsa_comparenormal_' + _this.compared.b.player.id).show();
							$('#lbsa_compareoverlay_' + _this.compared.b.player.id).hide();
							_this.comparePlayer(data);
						});
						$('#lbsa_comparereplace_' + _this.compared.b.player.id).unbind();
						$('#lbsa_comparereplace_' + _this.compared.b.player.id).bind('click', function (evt) {
							//un-highlight row if player is removed from compare
							$('#lbaa_athleterow_' + _this.compared.b.player.id).removeClass('athleteRowHighlight row_being_compared'); 
							//change info icon back to dark when row un-highlighted
							$('#lbaa_plus_' + _this.compared.b.player.id).removeClass('iconsinfo_white').addClass('iconsinfo_dark');
							//hide outline if player is replaced
							$('.compareOutline').hide();
							
							evt.stopPropagation();
							_this.compared.b = undefined;
							$('#lbsa_comparenormal_' + _this.compared.a.player.id).show();
							$('#lbsa_compareoverlay_' + _this.compared.a.player.id).hide();
							_this.comparePlayer(data);
						});
						_this.blockCompare = false;
					}
				});
				_this.athleteCompareModel.fetch({contestId: _this.parameters.contest.id, athleteSportEventInfoId: data.athleteSportEventInfoId});
			} else {
				_this.blockCompare = false;
			}
		}
        };

        ruckus.subpagecontrols.lineupbuilderselectedathletes.prototype.renderCompare = function (data, letter) {
                var _this = this;
		data.athletecompare.formatteddefenseVsPosition = _this.formatPlace(data.athletecompare.defenseVsPosition);
		data.athletecompare.injuryStatus = data.athletecompare.injuryStatus.split('|')[0];
                _this.require_template('athletecompare-tpl');
                dust.render('dusttemplates/athletecompare-tpl', data, function (err, out) {
                        $('#lbsa_comparenormal' + letter).html(out);
			_this.addScrollBars();
				
			//show the select and remove buttons when rendering compare
			$('.selectContainer, .removeContainer').show();
			//show the outline on the right
			$('.compareOutline').removeClass('compareOutlineLeft');
			//change row background and text colors when selected
			$('#lbaa_athleterow_' + data.player.id).addClass('athleteRowHighlight row_being_compared');
			//change info icon to white when row highlighted
			$('#lbaa_plus_' + data.player.id).removeClass('iconsinfo_dark').addClass('iconsinfo_white');
                        
			$('#lbsa_comparetaba_' + data.player.id).bind('click', function (evt) {
                                evt.stopPropagation();
				$('#lbsa_comparetaba_' + data.player.id).removeClass('tab').addClass('tabSelected');
				$('#lbsa_comparetabb_' + data.player.id).removeClass('tabSelected').addClass('tab');
				$('#lbsa_comparetabc_' + data.player.id).removeClass('tabSelected').addClass('tab');
				$(' > .iconsplayer_dark', this).removeClass('iconsplayer_dark').addClass('iconsplayer_light'); //change icon to white when tab selected
				$('#lbsa_comparetabb_' + data.player.id + '> .iconsstats_light').removeClass('iconsstats_light').addClass('iconsstats_dark'); //change icon of tab b to dark when tab selected
				$('#lbsa_comparetabc_' + data.player.id + '> .iconstwitter_light').removeClass('iconstwitter_light').addClass('iconstwitter_dark'); //change icon of tab b to dark when tab selected
                                $('#lbsa_comparea_' + data.player.id).show();
                                $('#lbsa_compareb_' + data.player.id).hide();
                                $('#lbsa_comparec_' + data.player.id).hide();
                        });
                        $('#lbsa_comparetabb_' + data.player.id).bind('click', function (evt) {
                                evt.stopPropagation();
				$('#lbsa_comparetaba_' + data.player.id).removeClass('tabSelected').addClass('tab');
				$('#lbsa_comparetabb_' + data.player.id).removeClass('tab').addClass('tabSelected');
				$('#lbsa_comparetabc_' + data.player.id).removeClass('tabSelected').addClass('tab');
				$(' > .iconsstats_dark', this).removeClass('iconsstats_dark').addClass('iconsstats_light'); //change icon to white when tab selected
				$('#lbsa_comparetaba_' + data.player.id + '> .iconsplayer_light').removeClass('iconsplayer_light').addClass('iconsplayer_dark'); //change icon of tab b to dark when tab selected
				$('#lbsa_comparetabc_' + data.player.id + '> .iconstwitter_light').removeClass('iconstwitter_light').addClass('iconstwitter_dark'); //change icon of tab b to dark when tab selected
                                $('#lbsa_comparea_' + data.player.id).hide();
                                $('#lbsa_compareb_' + data.player.id).show();
                                $('#lbsa_comparec_' + data.player.id).hide();
                        });
                        $('#lbsa_comparetabc_' + data.player.id).bind('click', function (evt) {
                                evt.stopPropagation();
				$('#lbsa_comparetaba_' + data.player.id).removeClass('tabSelected').addClass('tab');
				$('#lbsa_comparetabb_' + data.player.id).removeClass('tabSelected').addClass('tab');
				$('#lbsa_comparetabc_' + data.player.id).removeClass('tab').addClass('tabSelected');
				$('> .iconstwitter_dark', this).removeClass('iconstwitter_dark').addClass('iconstwitter_light'); //change icon of tab b to dark when tab selected
				$('#lbsa_comparetaba_' + data.player.id + '> .iconsplayer_light').removeClass('iconsplayer_light').addClass('iconsplayer_dark'); //change icon of tab b to dark when tab selected
				$('#lbsa_comparetabb_' + data.player.id + '> .iconsstats_light').removeClass('iconsstats_light').addClass('iconsstats_dark'); //change icon of tab b to dark when tab selected
				
                                $('#lbsa_comparea_' + data.player.id).hide();
                                $('#lbsa_compareb_' + data.player.id).hide();
                                $('#lbsa_comparec_' + data.player.id).show();
                        });

                        $('#lbsa_comparecancel_' + data.player.id).bind('click', function (evt) {
                                evt.stopPropagation();
                                $('#lbsa_comparenormal_' + _this.compared.a.player.id).show();
                                $('#lbsa_comparenormal_' + _this.compared.b.player.id).show();
                                $('#lbsa_compareoverlay_' + _this.compared.a.player.id).hide();
                                $('#lbsa_compareoverlay_' + _this.compared.b.player.id).hide();
				
				//show the select and remove buttons when clicking cancel
				$('.selectContainer, .removeContainer').show();
                        });
                        $('#lbsa_compareselect_' + data.player.id).bind('click', function (evt) {
                                //un-highlight row if player is selected from compare
				$('#lbaa_athleterow_' + data.player.id).removeClass('athleteRowHighlight row_being_compared');
				//change info icon back to dark when row un-highlighted
				$('#lbaa_plus_' + data.player.id).removeClass('iconsinfo_white').addClass('iconsinfo_dark');
				
				//if the player b is selected, show the outline on the right
				if(letter == 'b')
				{
					$('.compareOutline').show();
				}
				else //if player a is selected, show the outline on the left
				{
					$('.compareOutline').addClass('compareOutlineLeft').show();
				}
				evt.stopPropagation();
                                var sub = _this.msgBus.subscribe('control.lbsa.hideplayer', function (data) {
                                        if (_this.compared[letter] != undefined) {
                                                if (data.id == _this.compared[letter].player.id) {
                                                        _this.compared[letter] = undefined;
                                                        $('#lbsa_comparenormal' + letter).html('');
                                                        _this.checkVisibleSections();
                                                }
                                        }
                                });
                                _this.subscriptions.push(sub);
                                _this.selectPlayer(_this.compared[letter].player);
				
				
                        });
						
			//function to change select icon color on hover (mouseenter and mouseleave)
			$('#lbsa_compareselect_' + data.player.id).bind({
				mouseenter: function () {
					$(' > .iconsplus_green', this).removeClass('iconsplus_green').addClass('iconsplus_light');
				},
				mouseleave: function() {
					$(' > .iconsplus_light', this).removeClass('iconsplus_light').addClass('iconsplus_green');
				}
			});
						
                        $('#lbsa_compareremove_' + data.player.id).bind('click', function (evt) {
                                //if player b is removed, show the outline on the right
				if(letter == 'b')
				{
					$('.compareOutline').removeClass('compareOutlineLeft').addClass('compareOutline').show();
				}
				else if(letter == 'a')//if player a is removed, show the outline on the left
				{
					$('.compareOutline').addClass('compareOutlineLeft').show();
				}
				//un-highlight row if player is removed from compare
				$('#lbaa_athleterow_' + data.player.id).removeClass('athleteRowHighlight row_being_compared');
				//change info icon back to dark when row un-highlighted
				$('#lbaa_plus_' + data.player.id).removeClass('iconsinfo_white').addClass('iconsinfo_dark');
				evt.stopPropagation();
                                _this.compared[letter] = undefined;
                                $('#lbsa_comparenormal' + letter).html('');
								
				
                                
				_this.checkVisibleSections();
                        });
						
						//function to change remove icon color on hover (mouseenter and mouseleave)
						$('#lbsa_compareremove_' + data.player.id).bind({
							mouseenter: function () {
								$(' > .iconsremove_red', this).removeClass('iconsremove_red').addClass('iconsremove_light');
							},
							mouseleave: function() {
								$(' > .iconsremove_light', this).removeClass('iconsremove_light').addClass('iconsremove_red');
							}
                        });
			setTimeout(function(){
				_this.blockCompare = false;
			},1000);
                });

        };

        ruckus.subpagecontrols.lineupbuilderselectedathletes.prototype.checkVisibleSections = function () {
                if (this.compared.a == undefined && this.compared.b == undefined) {
                        $('#lbsa_listsmall').fadeOut(10);
                        $('#lbsa_compare').fadeOut();
                        setTimeout(function () {
                                $('#lbsa_listlarge').fadeIn();
                        }, 10);
                } else {
                        $('#lbsa_listlarge').fadeOut(10);
                        setTimeout(function () {
                                $('#lbsa_listsmall').fadeIn();
                                $('#lbsa_compare').fadeIn();
                        }, 10);
                }
        };

        ruckus.subpagecontrols.lineupbuilderselectedathletes.prototype.unload = function () {
                this.destroyControl();
        };

        return ruckus.subpagecontrols.lineupbuilderselectedathletes;
});
	

