define([
        "rg_subpage_base",
        "rg_pubsub",
        'assets/js/modules/datetime.js',
        'assets/js/modules/counters.circle.js',
        'assets/js/modules/counters.text.js',
        'assets/js/modules/counters.contest.js',
        'assets/js/modules/scrollbars.js',
        "jquery",
        "dust",
        "assets/js/viewrepositories/lobby.contestresults.js",
        "assets/js/libraries/gsap/TweenMax.min.js",
        "assets/js/pagecontrols/contestentry.js",
        "assets/js/subpagecontrols/contestresultsinfo.js",
        "assets/js/subpagecontrols/contestresultsentries.js",
        "assets/js/subpagecontrols/contestresultsprizes.js",
        "assets/js/subpagecontrols/contestresultslineups.js"
], function (Base) {
        ruckus.subpagecontrols.contestresults = function (parameters) {
                var _this = this;
                Base.call(_this);
                _this.parameters = parameters;

                // Code Added for Counters
                _this.timezone = "America/New_York";

                _this.sortDetails = {
                        type: 'desc',
                        currentEntries: 'asc',
                        capacity: 'desc',
                        entryFee: 'desc',
                        entryPool: 'desc',
                        startTime: 'desc'
                };
                _this.filterData = {
                        topFilter: undefined,
                        tabFilter: 'ALL'
                };
                _this.viewrepo = null;
                _this.viewdata = null;
                _this.opendetailcontrol = null;
                _.bindAll(_this,
                        "receiveContestFilterChange",
                        "receiveContestScoringUpdate",
                        "receiveContestUpdate",
			"receiveContestRemove",
                        //"receiveContestsAll",
                        "receiveContestAdd",
                        "receivePageLoadData",
                        "receiveUpdateServerTime",
                        "updateCounters",

                        "render",
                        "rowClickEvents",
                        "expandDetails",
                        "closeDetails"
                );
                _this.viewrepo = new ruckus.views.repositories.lobby_contestresults();
        };
        ruckus.subpagecontrols.contestresults.prototype = Object.create(Base.prototype);
        ruckus.subpagecontrols.contestresults.prototype.load = function () {
                var _this = this;

                _this.__addsubscription("control.contestfilter.change", _this.receiveContestFilterChange);

                // DATA EVENT LISTENERS
                _this.__addsubscription(ruckus.pubsub.subscriptions.view.lobby.contestresults.servertime, _this.receiveUpdateServerTime);
                _this.__addsubscription(ruckus.pubsub.subscriptions.view.lobby.contestresults.contestscoring.update, _this.receiveContestScoringUpdate);
                _this.__addsubscription(ruckus.pubsub.subscriptions.view.lobby.contestresults.contests.add, _this.receiveContestAdd);
                //_this.__addsubscription(ruckus.pubsub.subscriptions.view.lobby.contestresults.contests.all, _this.receiveContestsAll);
                _this.__addsubscription(ruckus.pubsub.subscriptions.view.lobby.contestresults.contests.update, _this.receiveContestUpdate);
                _this.__addsubscription(ruckus.pubsub.subscriptions.view.lobby.contestresults.contests.remove, _this.receiveContestRemove);
                _this.__addsubscription(ruckus.pubsub.subscriptions.view.lobby.contestresults.pageload, _this.receivePageLoadData);

                // INITIALIZE THE VIEW REPOSITORY
                _this.viewrepo.fetch();
        };

        // SUBSCRIBER EVENTS
        ruckus.subpagecontrols.contestresults.prototype.receivePageLoadData = function (data, envelope) {
                var _this = this;
                _this.unsubscribeFrom(envelope.channel, envelope.topic);
                _this.viewdata = data;
                _this.render();
        };
        ruckus.subpagecontrols.contestresults.prototype.receiveContestFilterChange = function (data) {
                var _this = this;
                _this.log({type: 'api', data: data, msg: 'CONTEST FILTER CHANGED - RELOAD CONTEST RESULTS'});
                // reset tabs
//                $('.conr_tabselected').removeClass('conr_tabselected');
//                $('#conr_tab_all').addClass('conr_tabselected');

                // filter out rows based on filter data
                _this.filterData.topFilter = data;
                _this.filterResults();
        };
        ruckus.subpagecontrols.contestresults.prototype.receiveContestUpdate = function (data) {
		var applyClass = undefined;
                var conr_currententries = $('#conr_currententries_' + data.id);

		if (data.currentEntries > conr_currententries.html()){
			applyClass = 'increaseEntries';
		} else {
			applyClass = 'decreaseEntries';
		}

                conr_currententries.html(data.currentEntries);
                conr_currententries.addClass(applyClass);
                conr_currententries.addClass(applyClass);

                if(data.isEntered){
                        $("#conr_enter_" + data.id).addClass("contestSuccess");
			$("#conr_enter_" + data.id).html('ENTERED');
                } else {
                        $("#conr_enter_" + data.id).removeClass("contestSuccess");
			$("#conr_enter_" + data.id).html('ENTER');
                }

        	setTimeout(function(){
                        $('#conr_currententries_' + data.id).removeClass(applyClass);
                }, 3000);
        };
	ruckus.subpagecontrols.contestresults.prototype.receiveContestAdd = function (data) {
                var _this = this;
                _this.viewdata.contests.push(data);

                var curr_val = $('#lobbycontestplus').html();
                try {
                        curr_val = parseInt(curr_val) + 1;
                } catch (e) {}
                $('#lobbycontestplus').html(curr_val);

//                _this.addRow(data);
        };
        ruckus.subpagecontrols.contestresults.prototype.receiveContestRemove = function (data) {
                var _this = this;

                $('#conr_currententries_' + data.id).html(data.currentEntries);
                _this.disableRow(data.id);

                var curr_val = $('#lobbycontestminus').html();
                try {
                        curr_val = parseInt(curr_val) + 1;
                } catch (e) {}
                $('#lobbycontestminus').html(curr_val);

                if(data.isEntered){
                        $("#conr_enter_" + data.id).addClass("contestSuccess");
                } else {
                        $("#conr_enter_" + data.id).removeClass("contestSuccess");
                }
//                _this.removeRow(data);
        };
        ruckus.subpagecontrols.contestresults.prototype.receiveContestScoringUpdate = function (data, envelope) {
                this.contestScoringModel = data;
        };
        ruckus.subpagecontrols.contestresults.prototype.receiveUpdateServerTime = function (data) {
                var _this = this;
                _this.viewdata.servertime = JSON.parse(data);
                _this.viewdata.servertime_updated = ruckus.modules.datetime.now();
                $.each( _this.viewdata.contests, function(key, val) {
                        val.remainingTime = ruckus.modules.datetime.diff(_this.viewdata.servertime, val.startTime, 'seconds');
                });
        };

        // FILTERS
        ruckus.subpagecontrols.contestresults.prototype.filterResults = function () {
                var _this = this;
//		$('.conr_row').hide(); // hide all rows
                $('.conr_details').remove();
                $('.conr_details_blank').remove();
                $('.conr_row').each(function (key, value) {
                        var arrClassList = value.className.split(' ');
                        var show = true; // assume we'll show it
                        $.each(arrClassList, function (k, v) {
                                var arrC = v.split('_');
                                var val = arrC[arrC.length - 1]; // value of class to check against
                                if (_this.filterData.topFilter != undefined) {
                                        if (v.indexOf('conr_row_league_') != -1) { // check sport
                                                if (!(val == _this.filterData.topFilter.sport || _this.filterData.topFilter.sport == 'ALL')) {
                                                        show = false;
                                                }
                                        } else if (v.indexOf('conr_row_capacity_') != -1) { // check num players
						var subShow = false;
						$.each(_this.filterData.topFilter.numPlayers, function(k,v){
							if (v.minimum <= val && v.maximum >= val)
								subShow = true;
						});
						if (show == true && subShow == false)
							show = false;
/*						
                                                if (!(_this.filterData.topFilter.numPlayers.minimum <= val && _this.filterData.topFilter.numPlayers.maximum >= val)) {
                                                        show = false;
                                                }
*/
                                        } else if (v.indexOf('conr_row_entryfee_') != -1) { // check entry fee
                                                if (!(_this.filterData.topFilter.entryFeeSelected.min <= val && _this.filterData.topFilter.entryFeeSelected.max >= val)) {
                                                        show = false;
                                                }
                                        }
                                }
                                if (v.indexOf('conr_row_type_') != -1 && _this.filterData.tabFilter != 'ALL') {
                                        if (val != _this.filterData.tabFilter)
                                                show = false;
                                }
                        });

                        if (!($('#' + value.id).is(':visible')) && show == true) {
                                $('#' + value.id).addClass('conr_visible');
                                _this.showRow(value.id);
                        } else if ($('#' + value.id).is(':visible') && show == false) {
                                $('#' + value.id).removeClass('conr_visible');
                                _this.hideRow(value.id);
                        }
                });

                _this.stripe();
        };

        // UI RENDERING
        ruckus.subpagecontrols.contestresults.prototype.render = function () {
                var _this = this;
                _this.getContainer();
                _this.container.addClass('ruckus-spc-contestresults');
                _this.require_template('contestresults-tpl');
                dust.render('dusttemplates/contestresults-tpl', {}, function (err, containerMarkup) {
                        _this.container.html(containerMarkup);
                        _this.renderTabs();
                        // get contestresultstable template
                        _this.require_template('contestresultstable-tpl');
                        dust.render('dusttemplates/contestresultstable-tpl', _this.viewdata, function (err, resultsMarkup) {
				if (_this.viewdata.contests.length == 0){
					$('#conr_tablecontainer').html('No contests available at this time.').addClass('noContest');
				} else {
					var conr_tablecontainer = $('#conr_tablecontainer');
					conr_tablecontainer.html('');
					var tableContainer = conr_tablecontainer.first('div');
					tableContainer.html(resultsMarkup);

					// resorting should close any open details
					$('#conr_header').click(function (evt) {
						evt.stopPropagation();
						$('.conr_details').remove();
					});

					// resorting events
					$('#conr_header_contest').click(function (evt) {
						_this.sort('contest', 'type');
					});
					$('#conr_header_entries').click(function (evt) {
						_this.sort('current_entries', 'currentEntries');
					});
					$('#conr_header_size').click(function (evt) {
						_this.sort('size', 'capacity');
					});
					$('#conr_header_entry').click(function (evt) {
						_this.sort('entry', 'entryFee');
					});
					$('#conr_header_prizes').click(function (evt) {
						_this.sort('prizes', 'prizePool');
					});
					$('#conr_header_starts').click(function (evt) {
						_this.sort('current_starts', 'startTime');
					});

					// click events
					$.each(_this.viewdata.contests, function (key, value) {
						_this.rowClickEvents(value);

						// hide/show multientry and guaranteed
						if (value.allowedEntries < 2)
							$('#rowMIcon_'+value.id).hide();
						if (value.contestType.abbr != 'GPP')
							$('#rowGPPIcon_'+value.id).hide();

						if (value.isEntered != undefined){
							if (value.isEntered){
								if (value.allowedEntries == 1)
									$('#conr_enter_'+value.id).addClass('contestSuccess');
								else 
									$('#conr_enter_'+value.id).addClass('contestSuccessMulti');
								$('#conr_enter_'+value.id).html('ENTERED');
							}
						}
					});

					$('#lobbycontestrefresh').bind('click', function(evt){
						evt.stopPropagation();
						_this.consolelog('refresh');
						$('#lobbycontestplus').html('0');
						$('#lobbycontestminus').html('0');
						_this.render();
					});

				}
				_this.filterResults();
                        });
                        _this.stripe();
                        _this.addScrollBars();
                        _this.startIntervals();
                });
        };
        ruckus.subpagecontrols.contestresults.prototype.sort = function(name, type){
                var _this = this;
                
		// column sorting function (array sorting / re-render)
                var dir1 = undefined;
                var dir2 = undefined;
                if (_this.sortDetails[type] == 'asc') {
                        _this.sortDetails[type] = 'desc';
                        dir1 = 1;
                        dir2 = -1;
                } else {
                        _this.sortDetails[type] = 'asc';
                        dir1 = -1;
                        dir2 = 1;
                }
                var compare = function (a, b) {
                        if (a[type] < b[type])
                                return dir1;
                        if (a[type] > b[type])
                                return dir2;
                        if (a[type] === b[type]) {
                                if (a.line1 < b.line1)
                                        return -1;
                                if (a.line1 > b.line1)
                                        return 1;
                        }
                        return 0;
                };
                _this.viewdata.contests.sort(compare);

		// change arrow display
		$('.columnheaderarrow').removeClass('iconsarrow_up').removeClass('iconsarrow_down');
		var arrowClass = '';
		if (_this.sortDetails[type] == 'desc')
			arrowClass = 'iconsarrow_up';
		else
			arrowClass = 'iconsarrow_down';
		var column = '';
		if (name == 'contest'){
			column = 'conr_header_contestarrow';
		} else if (name == 'current_entries'){
			column = 'conr_header_entriesarrow';
		} else if (name == 'size') {
			column = 'conr_header_sizearrow';
		} else if (name == 'entry') {	
			column = 'conr_header_entryarrow';
		} else if (name == 'prizes') {
			column = 'conr_header_prizesarrow';
		} else if (name == 'current_starts') {
			column = 'conr_header_startsarrow';
		} 
		// FIXME - this is a hack in the interest of time.  Should go back and change the template structure so that the list can change without the headers being re-rendered, thus setting this to run in a half second would be unnecessary.
		var runme = function(){
			$('#'+column).addClass(arrowClass);
		};
		setTimeout(runme, 500);

                _this.render();
        };
        ruckus.subpagecontrols.contestresults.prototype.rowClickEvents = function (value) {
                var _this = this;
                // row
                $('#conr_' + value.id).click(function (evt) {
                        evt.stopPropagation();
                        if ($('#conr_details_' + value.id).html() != undefined){
//                                _this.closeDetails(value);
                        } else {
                                _this.expandDetails(value, 'info');
			}
                });
		// column item
		$('#conr_sport_'+value.id).click(function(evt){
			evt.stopPropagation();
			if ($('#conr_details_' + value.id).html() != undefined){
                                _this.closeDetails(value);
//				$('#conr_sportarrow_'+value.id).removeClass('iconsarrow_down');
			} else {
//				$('.conr_sportarrow').removeClass('iconsarrow_down');
                                _this.expandDetails(value, 'info');
//				$('#conr_sportarrow_'+value.id).addClass('iconsarrow_down');
			}
		});
		// column item
                $('#conr_entryfee_' + value.id).click(function (evt) {
                        evt.stopPropagation();
                        if ($('#conr_details_' + value.id).html() != undefined){
//                                _this.closeDetails(value);
                                $('#conr_tabinfo_' + value.id).addClass('tabSelected');
                                $('#conr_tabentries_' + value.id).removeClass('tabSelected');
                                $('#conr_tabprizes_' + value.id).removeClass('tabSelected');
                                $('#conr_tablineups_' + value.id).removeClass('tabSelected');
                                _this.spcInfo.showPage();
                                _this.spcEntries.hidePage();
                                _this.spcPrizes.hidePage();
                                _this.spcLineups.hidePage();
                        } else {
                                _this.expandDetails(value, 'prizes');
                        }
                });
		// column item
                $('#conr_starttime_' + value.id).click(function (evt) {
                        evt.stopPropagation();
                        if ($('#conr_details_' + value.id).html() != undefined){
//                                _this.closeDetails(value);
                                $('#conr_tabinfo_' + value.id).addClass('tabSelected');
                                $('#conr_tabentries_' + value.id).removeClass('tabSelected');
                                $('#conr_tabprizes_' + value.id).removeClass('tabSelected');
                                $('#conr_tablineups_' + value.id).removeClass('tabSelected');
                                _this.spcInfo.showPage();
                                _this.spcEntries.hidePage();
                                _this.spcPrizes.hidePage();
                                _this.spcLineups.hidePage();
                        } else {
                                _this.expandDetails(value, 'info');
                        }
                });
                // column item
                $('#conr_currententries_' + value.id).click(function (evt) {
                        evt.stopPropagation();
                        if ($('#conr_details_' + value.id).html() != undefined){
//                                _this.closeDetails(value);
				if (value.contestType.abbr == 'ANON'){
					$('#conr_tabinfo_' + value.id).addClass('tabSelected');
					$('#conr_tabentries_' + value.id).removeClass('tabSelected');
					$('#conr_tabprizes_' + value.id).removeClass('tabSelected');
					$('#conr_tablineups_' + value.id).removeClass('tabSelected');
					_this.spcInfo.showPage();
					_this.spcEntries.hidePage();
					_this.spcPrizes.hidePage();
					_this.spcLineups.hidePage();
				} else {
					$('#conr_tabinfo_' + value.id).removeClass('tabSelected');
					$('#conr_tabentries_' + value.id).addClass('tabSelected');
					$('#conr_tabprizes_' + value.id).removeClass('tabSelected');
					$('#conr_tablineups_' + value.id).removeClass('tabSelected');
					_this.spcInfo.hidePage();
					_this.spcEntries.showPage();
					_this.spcPrizes.hidePage();
					_this.spcLineups.hidePage();
				}
                        } else {
				if (value.contestType.abbr == 'ANON'){
					_this.expandDetails(value, 'info');
				} else {
					_this.expandDetails(value, 'entries');
				}
			}
                });
                // column item
                $('#conr_size_' + value.id).click(function (evt) {
                        evt.stopPropagation();
                        if ($('#conr_details_' + value.id).html() != undefined){
//                                _this.closeDetails(value);
				if (value.contestType.abbr == 'ANON'){
                                        $('#conr_tabinfo_' + value.id).addClass('tabSelected');
                                        $('#conr_tabentries_' + value.id).removeClass('tabSelected');
                                        $('#conr_tabprizes_' + value.id).removeClass('tabSelected');
                                        $('#conr_tablineups_' + value.id).removeClass('tabSelected');
                                        _this.spcInfo.showPage();
                                        _this.spcEntries.hidePage();
                                        _this.spcPrizes.hidePage();
                                        _this.spcLineups.hidePage();
                                } else {
					$('#conr_tabinfo_' + value.id).removeClass('tabSelected');
					$('#conr_tabentries_' + value.id).addClass('tabSelected');
					$('#conr_tabprizes_' + value.id).removeClass('tabSelected');
					$('#conr_tablineups_' + value.id).removeClass('tabSelected');
					_this.spcInfo.hidePage();
					_this.spcEntries.showPage();
					_this.spcPrizes.hidePage();
					_this.spcLineups.hidePage();
				}
                        } else {
				if (value.contestType.abbr == 'ANON'){
					_this.expandDetails(value, 'info');
				} else {
					_this.expandDetails(value, 'entries');
				}
			}
                });
                // column item
                $('#conr_entrypool_' + value.id).click(function (evt) {
                        evt.stopPropagation();
                        if ($('#conr_details_' + value.id).html() != undefined){
//                                _this.closeDetails(value);
				$('#conr_tabinfo_' + value.id).removeClass('tabSelected');
                                $('#conr_tabentries_' + value.id).removeClass('tabSelected');
                                $('#conr_tabprizes_' + value.id).addClass('tabSelected');
                                $('#conr_tablineups_' + value.id).removeClass('tabSelected');
                                _this.spcInfo.hidePage();
                                _this.spcEntries.hidePage();
                                _this.spcPrizes.showPage();
                                _this.spcLineups.hidePage();
                        } else {
                                _this.expandDetails(value, 'prizes');
			}
                });

                // column item enter
                $('#conr_enterouter_' + value.id).bind('mouseenter', function (evt) {
                        evt.stopPropagation();
                        $('#conr_quick_' + value.id).show();
                });
                $('#conr_enterouter_' + value.id).bind('mouseleave', function (evt) {
                        evt.stopPropagation();
                        $('#conr_quick_' + value.id).hide();
                });
                $('#conr_enter_' + value.id).bind('click', function (evt) {
                        evt.stopPropagation();
                        _this.parameters.lobby.hidePage();
                        var contestentry = new ruckus.pagecontrols.contestentry({
                                'container': _this.parameters.lobby.parameters.container,
                                'lobby': _this.parameters.lobby,
                                'data': _this.viewdata.contests,
                                'contest': value
                        });
                        _this.unload();
                        window.location.href = "#contestentry/" + value.id;
//                        contestentry.load();
//                        _this.controls.push(contestentry);
                });
                $('#conr_quick_' + value.id).click(function (evt) {
                        evt.stopPropagation();
                        if ($('#conr_details_' + value.id).html() != undefined) {
//				_this.closeDetails(value);
                                _this.spcInfo.hidePage();
                                _this.spcEntries.hidePage();
                                _this.spcPrizes.hidePage();
                                _this.spcLineups.showPage();
                                $('#conr_tabinfo_' + value.id).removeClass('tabSelected');
                                $('#conr_tabentries_' + value.id).removeClass('tabSelected');
                                $('#conr_tabprizes_' + value.id).removeClass('tabSelected');
                                $('#conr_tablineups_' + value.id).addClass('tabSelected');
                        } else {
				$('.niceScroll-horiz').remove();
                                _this.expandDetails(value, 'lineups');
                        }
                });
        };

        ruckus.subpagecontrols.contestresults.prototype.expandDetails = function (contest, tab) {
                var _this = this;
                this.log({type: 'general', data: contest, msg: "CONTEST DATA MODEL"});
                this.log({type: 'general', data: tab, msg: "TAB TO DEFAULT TO"});

		$('.conr_sportarrow').removeClass('iconsarrow_down');
		$('#conr_sportarrow_'+contest.id).addClass('iconsarrow_down');
							
                this.closeAllDetails(contest.id);
                var contestformat = _this.formatContestName(contest, '2line');
                contest.line1 = contestformat.line1;
                contest.line2 = contestformat.line2;
                this.require_template('contestresultstabledetails-tpl');
                dust.render('dusttemplates/contestresultstabledetails-tpl', contest, function (err, out) {
                        $('#conr_' + contest.id).after(out);
                        _this.addScrollBars();

                        // hide/show multientry and guaranteed
                        if (contest.allowedEntries < 2)
                                $('.Mcont').hide();
                        if (contest.contestType.abbr != 'GPP')
                                $('.GPPcont').hide();

                        // highlight tab
                        $('#conr_tab' + tab + '_' + contest.id).addClass('tabSelected');

                        _this.spcInfo = new ruckus.subpagecontrols.contestresultsinfo({
                                'container': $('#conr_info_' + contest.id),
                                'data': _this.viewdata.contests,
                                'contest': contest,
                                'contestscoring': _this.viewdata.scoring,
                                'tab': tab
                        });
                        _this.spcInfo.load();
                        _this.controls.push(_this.spcInfo);
                        _this.spcEntries = new ruckus.subpagecontrols.contestresultsentries({
                                'container': $('#conr_entries_' + contest.id),
                                'data': _this.viewdata.contests,
                                'contest': contest,
                                'tab': tab
                        });
                        _this.spcEntries.load();
                        _this.controls.push(_this.spcEntries);
                        _this.spcPrizes = new ruckus.subpagecontrols.contestresultsprizes({
                                'container': $('#conr_prizes_' + contest.id),
                                'data': _this.viewdata.contests,
                                'contest': contest,
                                'tab': tab
                        });
                        _this.spcPrizes.load();
                        _this.controls.push(_this.spcPrizes);
                        _this.spcLineups = new ruckus.subpagecontrols.contestresultslineups({
                                'container': $('#conr_lineups_' + contest.id),
                                'data': _this.viewdata.contests,
                                'contest': contest,
                                'tab': tab
                        });
                        _this.spcLineups.load();
                        _this.controls.push(_this.spcLineups);
                        $('#conr_tabinfo_' + contest.id).click(function (evt) {
                                evt.stopPropagation();
                                $('#conr_tabinfo_' + contest.id).addClass('tabSelected');
                                $('#conr_tabentries_' + contest.id).removeClass('tabSelected');
                                $('#conr_tabprizes_' + contest.id).removeClass('tabSelected');
                                $('#conr_tablineups_' + contest.id).removeClass('tabSelected');
                                _this.spcInfo.showPage();
                                _this.spcEntries.hidePage();
                                _this.spcPrizes.hidePage();
                                _this.spcLineups.hidePage();
                        });
                        $('#conr_tabentries_' + contest.id).click(function (evt) {
                                evt.stopPropagation();
                                $('#conr_tabinfo_' + contest.id).removeClass('tabSelected');
                                $('#conr_tabentries_' + contest.id).addClass('tabSelected');
                                $('#conr_tabprizes_' + contest.id).removeClass('tabSelected');
                                $('#conr_tablineups_' + contest.id).removeClass('tabSelected');
                                _this.spcInfo.hidePage();
                                _this.spcEntries.showPage();
                                _this.spcPrizes.hidePage();
                                _this.spcLineups.hidePage();
                        });
                        $('#conr_tabprizes_' + contest.id).click(function (evt) {
                                evt.stopPropagation();
                                $('#conr_tabinfo_' + contest.id).removeClass('tabSelected');
                                $('#conr_tabentries_' + contest.id).removeClass('tabSelected');
                                $('#conr_tabprizes_' + contest.id).addClass('tabSelected');
                                $('#conr_tablineups_' + contest.id).removeClass('tabSelected');
                                _this.spcInfo.hidePage();
                                _this.spcEntries.hidePage();
                                _this.spcPrizes.showPage();
                                _this.spcLineups.hidePage();
                        });
                        $('#conr_tablineups_' + contest.id).click(function (evt) {
                                evt.stopPropagation();
                                $('#conr_tabinfo_' + contest.id).removeClass('tabSelected');
                                $('#conr_tabentries_' + contest.id).removeClass('tabSelected');
                                $('#conr_tabprizes_' + contest.id).removeClass('tabSelected');
                                $('#conr_tablineups_' + contest.id).addClass('tabSelected');
                                _this.spcInfo.hidePage();
                                _this.spcEntries.hidePage();
                                _this.spcPrizes.hidePage();
                                _this.spcLineups.showPage();
                        });


                        var cell = $('#conr_details_' + contest.id);

			// hides entries for H2H anonymous
			if (contest.contestType.abbr == 'ANON'){
				$('#conr_tabentries_' + contest.id).hide();
			}

                        // show pretty circle counter
                        ruckus.modules.counters.circle.render(cell, "conr_details_chart", contest.remainingTime, null);

                        var tl = new TimelineMax({
                                onComplete: function () {
                                }
                        });
                        tl.to(cell, 0.5, {height: 240});
                });
                ruckus.modules.scrollbars.resize();
        };
        ruckus.subpagecontrols.contestresults.prototype.closeDetails = function (value) {
		var _this = this;
		if (_this.spcEntries != undefined){
			_this.spcEntries.unload();
			_this.spcInfo.unload();
			_this.spcPrizes.unload();
			_this.spcLineups.unload();
		}
		$('#conr_sportarrow_'+value.id).removeClass('iconsarrow_down');
                var cell = $("#conr_details_" + value.id);
                var tl = new TimelineMax({
                        onComplete: function () {
                                val = $("#conr_details_" + value.id);
                                val.remove();
                                $("#conr_details_" + value.id + "_blank").remove();
                        }
                });
                tl.to(cell, 0.5, {height: 0});

                ruckus.modules.scrollbars.resize();
        };
        ruckus.subpagecontrols.contestresults.prototype.closeAllDetails = function (keepOpenId) {
		var _this = this;
		if (_this.spcEntries != undefined){
			_this.spcEntries.unload();
			_this.spcInfo.unload();
			_this.spcPrizes.unload();
			_this.spcLineups.unload();
		}
                $.each($(".conr_details"), function (key, value) {
                        var closeMe = function () {
                                var tl = new TimelineMax({
                                        onComplete: function () {
                                                value.remove();
                                        }
                                });
                                tl.to(value, 0.5, {height: 0});
                        };
                        if (keepOpenId != undefined) {
                                if (keepOpenId != value.id)
                                        closeMe();
                        } else {
                                closeMe();
                        }
                });
        };

        ruckus.subpagecontrols.contestresults.prototype.stripe = function () {
                this.tablestripe('.conr_visible');
        };
        ruckus.subpagecontrols.contestresults.prototype.showRow = function (id) {
                var _this = this;
                var cell = $('#' + id);
                cell.css('display', 'inline');
                var tl = new TimelineMax({
                        onComplete: function () {
//				_this.rowClickEvents(contest);
                        }
                });

                tl.to(cell, 0.5, {opacity: 1});
                tl.to(cell, 0.5, {marginLeft: 0}, "-=1.0");
                tl.play();
        };
        ruckus.subpagecontrols.contestresults.prototype.disableRow = function (id) {
                $('#conr_' + id).addClass('rowClosed');
                this.closeDetails({ id: id });
        };
        ruckus.subpagecontrols.contestresults.prototype.hideRow = function (id) {
                var cell = $('#' + id);
                var tl = new TimelineMax({
                        onComplete: function () {
                                cell.css('display', 'none');
                        }
                });
                tl.to(cell, 0.5, {opacity: 0});
                tl.to(cell, 0.5, {marginLeft: -120}, "-=1.0");
                tl.play();

        };
        ruckus.subpagecontrols.contestresults.prototype.addRow = function (contest) {
                var _this = this;
                this.log({type: 'general', data: contest, msg: 'ADD ROW'});
                // FIXME - add item to modelData also
                this.require_template('contestresultstablerow-tpl');
                dust.render('dusttemplates/contestresultstablerow-tpl', contest, function (err, out) {
                        $('#conr_table').append(out);
                        _this.addScrollBars();

                        var cell = $('#conr_' + contest.id);
                        var tl = new TimelineMax({
                                onComplete: function () {
                                        _this.rowClickEvents(contest);
                                }
                        });
                        tl.to(cell, 0.5, {opacity: 1});
                        tl.to(cell, 0.5, {marginLeft: 0}, "-=1.0");
                        tl.play();
                        _this.stripe();
                });
        };
        ruckus.subpagecontrols.contestresults.prototype.removeRow = function (contest) {
                this.log({type: 'general', data: contest, msg: 'REMOVE ROW'});
                //  FIXME - remove item from modelData also

                var cell = $('#conr_' + contest.id);
                var tl = new TimelineMax({
                        onComplete: function () {
                                cell.remove();
                        }
                });
                tl.to(cell, 0.5, {opacity: 0});
                tl.to(cell, 0.5, {marginLeft: -120}, "-=1.0");
                tl.play();

        };
        ruckus.subpagecontrols.contestresults.prototype.renderTabs = function () {
                var _this = this;
                $('#conr_tab_all').click(function (evt) {
                        evt.stopPropagation();
                        _this.filterData.tabFilter = 'ALL';
                        _this.filterResults();
                        $('.conr_tabselected').removeClass('conr_tabselected');
                        $('#conr_tab_all').addClass('conr_tabselected');
                        $('.conr_details').remove();
                });
                $('#conr_tab_headtohead').click(function (evt) {
                        evt.stopPropagation();
                        _this.filterData.tabFilter = 'H2H';
                        _this.filterResults();
                        $('.conr_tabselected').removeClass('conr_tabselected');
                        $('#conr_tab_headtohead').addClass('conr_tabselected');
                        $('.conr_details').remove();
                });
                $('#conr_tab_standard').click(function (evt) {
                        evt.stopPropagation();
                        _this.filterData.tabFilter = 'NRM';
                        _this.filterResults();
                        $('.conr_tabselected').removeClass('conr_tabselected');
                        $('#conr_tab_standard').addClass('conr_tabselected');
                        $('.conr_details').remove();
                });
                $('#conr_tab_doubleup').click(function (evt) {
                        evt.stopPropagation();
                        _this.filterData.tabFilter = 'DU';
                        _this.filterResults();
                        $('.conr_tabselected').removeClass('conr_tabselected');
                        $('#conr_tab_doubleup').addClass('conr_tabselected');
                        $('.conr_details').remove();
                });
                $('#conr_tab_guaranteed').click(function (evt) {
                        evt.stopPropagation();
                        _this.filterData.tabFilter = 'GPP';
                        _this.filterResults();
                        $('.conr_tabselected').removeClass('conr_tabselected');
                        $('#conr_tab_guaranteed').addClass('conr_tabselected');
                        $('.conr_details').remove();
                });
                $('#conr_tab_satellite').click(function (evt) {
                        evt.stopPropagation();
                        _this.filterData.tabFilter = 'SAT';
                        _this.filterResults();
                        $('.conr_tabselected').removeClass('conr_tabselected');
                        $('#conr_tab_satellite').addClass('conr_tabselected');
                        $('.conr_details').remove();
                });

        };

        // COUNTERS
        ruckus.subpagecontrols.contestresults.prototype.updateCounters = function(){
                var _this = this;
                if(!_this.viewdata.contests) return false;

                $.each( _this.viewdata.contests, function(key, val){
                        // decrement the time by one second
                        val.remainingTime = val.remainingTime - 1;
                        val.formattedstartTime = ruckus.modules.counters.contest.getContestTextCounter(val.startTime, val.remainingTime);

                        // TextCounter
                        var counter_val = ruckus.modules.counters.contest.getContestTextCounter(val.startTime, val.remainingTime);
                        $('#conr_starttime_' + val.id).html(counter_val);
                        $('#contestentryrowstarttime_' + val.id).html(counter_val);

                        // Detail Counter
                        var detail_container = $('#conr_details_' + val.id);
                        if(detail_container.length > 0){
                                if(detail_container.find(".conr_details_chart").length === 1) {
                                        ruckus.modules.counters.circle.update(detail_container, "conr_details_chart", val.remainingTime);
                                }
                        }
                });
        };
        ruckus.subpagecontrols.contestresults.prototype.startIntervals = function(){
                var _this = this;
                if(this.serverTimeInterval === undefined) {
                        this.serverTimeInterval = setInterval(_this.viewrepo.fetchServerTime, 10000);
                        _this.intervals.push(this.serverTimeInterval);
                }
                if(this.counterInterval === undefined) {
                        this.counterInterval = setInterval(_this.updateCounters, 1000);
                        _this.intervals.push(this.counterInterval);
                }
        };

        // TERMINATION
        ruckus.subpagecontrols.contestresults.prototype.unload = function () {
                var _this = this;

                _this.viewrepo.unload();

//                _.unbindAll(this,
//                        "render",
//                        "receiveContestFilterChange",
//                        "receiveContestScoringUpdate",
//                        "receiveContestScoringUpdate",
//                        "receiveContestUpdate",
//                        "receiveContestsAll",
//                        "receiveContestAdd",
//                        "receiveUpdateServerTime",
//                        "updateCounters"
//                );

                this.destroyControl();
        };

        return ruckus.subpagecontrols.contestresults;
});
	

