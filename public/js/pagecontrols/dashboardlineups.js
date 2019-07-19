define([
        "assets/js/pagecontrols/base.js",
        "assets/js/libraries/jquery.min.js",
        "assets/js/models/livelineups.js",
        "assets/js/models/lineuprules.js",
        "assets/js/models/lineupupdate.js",
        "assets/js/models/lineupremove.js",
        "assets/js/subpagecontrols/lineupbuilderavailableathletes.js",
        "assets/js/subpagecontrols/lineupbuilderselectedathletes.js",
        "assets/js/viewrepositories/vr.dashboardlineups.js",
        "assets/js/models/contestid.js",
        'assets/js/modules/navigation.js',
        'assets/js/modules/counters.circle.js'
], function (Base) {
        ruckus.pagecontrols.dashboardlineups = function (parameters) {
                var _this = this;
                Base.call(_this);
                _this.parameters = parameters;
                _this.viewrepo = new ruckus.views.repositories.dashboard(parameters);
                _this.viewdata = {
                        lineups: null
                };

                if(parameters.contestId) _this.viewdata.contestId = parameters.contestId;

                // Context bindings
                _.bindAll(_this,
                        'updateCounters',
                        'onUpdateServerTime');
        };

        ruckus.pagecontrols.dashboardlineups.prototype = Object.create(Base.prototype);

        ruckus.pagecontrols.dashboardlineups.prototype.load = function () {
                var _this = this;

                // DATA EVENT LISTENERS
                _this.getContainer();
                _this.container.addClass('ruckus-pc-dashboardlineups');
                _this.require_template('dashboardlineups-tpl');
                dust.render('dusttemplates/dashboardlineups-tpl', {}, function (err, out) {
                        _this.container.html(out);
                        _this.addScrollBars();
                        _this.getData();

                        $('#dashnavc').bind('click', function (evt) {
                                evt.stopPropagation();
                                ruckus.modules.navigation.toRoute('dashboard');
                        });
                        $('#dashnava').bind('click', function (evt) {
                                evt.stopPropagation();
                                ruckus.modules.navigation.toRoute('dashboardathletes');
                        });
                        $('#dashnavl').bind('click', function (evt) {
                                evt.stopPropagation();
                                ruckus.modules.navigation.toRoute('dashboardlineups');
                        });
                        $('#dashnavh').bind('click', function (evt) {
                                evt.stopPropagation();
                                ruckus.modules.navigation.toRoute('dashboardhistory');
                        });
                });
        };

        ruckus.pagecontrols.dashboardlineups.prototype.getData = function () {
                var _this = this;
                this.liveLineupsModel = new ruckus.models.livelineups({});
                this.models.push(_this.liveLineupsModel);

                var selected_lineup = null;
                var sub = _this.msgBus.subscribe("model.livelineups.retrieve", function (data) {
                        sub.unsubscribe();

                        _this.viewdata.lineups = _this.liveLineupsModel.modelData;
                        var page_container = $('#dshl_container');

                        if (_this.liveLineupsModel.modelData.length == 0) {
                                page_container.html('You do not currently have any lineups in upcoming, active, locked, or open contests.').addClass('noContest');
                        }
                        $.each(_this.liveLineupsModel.modelData, function (key, lineup) {
                                _this.render(lineup);
                                lineup.remainingTime = ruckus.modules.datetime.diff(_this.servertime, lineup.startTime, 'seconds');

                                var contests_active = false;
                                $.each(lineup.contests, function (k, contest) {
                                        if(contest.contestState === "active"){
                                                contests_active = true;
                                        }
                                        if(contest.id === _this.viewdata.contestId){
                                                selected_lineup = lineup.lineupId;
                                        }
                                });

                                // set empty container after each row
                                if ((key + 1) % 3 == 0) {
                                        $('<div>', {'style': 'clear:both;'}).appendTo(page_container);
                                        $('<div>', {'id': 'dshl_empty_' + (key + 1), 'class': 'dshl_empty'}).appendTo(page_container);
                                }

                                if(contests_active) {
                                        $('#dshl_lineup_' + lineup.lineupId)
                                                .css( 'cursor', 'default' )
                                                .find('.icons').css('opacity', '0.2');
                                } else {
                                        $('#dshl_lineup_' + lineup.lineupId).bind('click', function (evt) {
                                                evt.stopPropagation();
                                                _this.editLineupStep1(lineup.lineupId);
                                        });
                                }
                        });

                        // set the final empty container
                        var key = _this.liveLineupsModel.modelData.length - 1;
                        if ((key + 1) % 3 != 0) {
                                for (var x = 2; x <= 4; x++) {
                                        if ((key + x) % 3 == 0) {
                                                $('<div>', {'style': 'clear:both;'}).appendTo(page_container);
                                                $('<div>', {'id': 'dshl_empty_' + (key + x), 'class': 'dshl_empty'}).appendTo(page_container);
                                        }
                                }
                        }

                        $('.dshl_remove').bind('click', function (evt) {
                                evt.stopPropagation();
                                _this.removeContest(evt.delegateTarget.id.split('_')[2], evt.delegateTarget.id.split('_')[3]);
                        });
                        _this.__addsubscription(ruckus.pubsub.subscriptions.view.dashboard.servertime, _this.onUpdateServerTime);

                        if(selected_lineup) {
                                var selected_lineup_el = $('#dshl_' + selected_lineup);
                                $('html, body').animate({
                                        scrollTop: selected_lineup_el.offset().top - 100
                                }, 1000, function(){
                                        selected_lineup_el.addClass('outerglow');
                                        setTimeout(function () {
                                                selected_lineup_el.removeClass('outerglow');
                                        }, 2000);
                                });
                        }
                        _this.startIntervals();
                });
                _this.liveLineupsModel.fetch({});
        };
        ruckus.pagecontrols.dashboardlineups.prototype.render = function (lineup) {
                var _this = this;
                // preprocess data
                lineup.formattedRemainingSalary = _this.formatMoney(lineup.remainingSalary);
                lineup.totalContests = lineup.contests.length;
                lineup.totalEntryFees = 0;
                lineup.active = false;
                $.each(lineup.contests, function (k, contest) {
                        if(contest.contestState === 'active') {
                                lineup.active = true;
                        }
                        lineup.totalEntryFees += contest.entryFee;
                        contest.line1 = _this.formatContestName(contest, '1linefull').line1;
                });

                lineup.totalEntryFees = _this.formatMoney(lineup.totalEntryFees);

                this.require_template('dashboardlineupscell-tpl');
                dust.render('dusttemplates/dashboardlineupscell-tpl', lineup, function (err, out) {
                        var lineup_container = $('#dshl_container');
                        lineup_container.append(out);
                        var dshl_el = $("#dshl_" + lineup.lineupId);
                        if(lineup.active) {
                                dshl_el.find(".upcoming_row").remove();
                        } else {
                                dshl_el.find(".active_row").remove();
                        }

                        _this.addScrollBars();

                        $.each(lineup.contests, function (k, contest) {
                                if (contest.contestState != 'open') {
                                        $('#dshl_remove_' + contest.id + '_' + lineup.lineupId).hide();
                                        $('#dshl_lock_' + contest.id + '_' + lineup.lineupId).show();
                                }

                                var contest_row = $('#dshl_contest_' + contest.id + '_' + lineup.lineupId);
                                contest_row.bind('click', function (evt) {
                                        evt.stopPropagation();
                                        switch (contest.contestState){
                                                case 'active':
                                                        ruckus.modules.navigation.toRoute('dashboardcontest/' + contest.id);
                                                        break;
                                                case 'complete':
                                                        ruckus.modules.navigation.toRoute('dashboardhistory/' + contest.id + '_' + lineup.lineupId);
                                                        break;
                                                case 'history':
                                                        ruckus.modules.navigation.toRoute('dashboardhistory/' + contest.id + '_' + lineup.lineupId);
                                                        break;
                                                default:
                                                        ruckus.modules.navigation.toRoute('dashboard/' + contest.id);
                                                        break;
                                        }
                                });
                        });

                        var counter_container = lineup_container.find('#dshl_starttime_' + lineup.lineupId);

                        if (counter_container.find(".dshl_cell_upcoming_chart").length === 1) {
                                lineup.remainingTime = ruckus.modules.datetime.diff(_this.servertime, lineup.startTime, 'seconds');
                                ruckus.modules.counters.circle.render(counter_container, "dshl_cell_upcoming_chart", lineup.remainingTime);
                        }
                });
        };
        ruckus.pagecontrols.dashboardlineups.prototype.editLineupStep1 = function (id) {
                var _this = this;

                _this.step1Controls = [];

                // find empty cell
                var div = $('#dshl_' + id);
                for (var x = 1; x < 4; x++) {
                        if (div.next().hasClass('dshl_empty'))
                                break;
                        else
                                div = div.next();
                }
                var emptyDiv = div.next();

                // clear all other empty cells
                _this.clearEditMode();

                // find any valid contest for the lineup
                var contestId = undefined;
                var lineup = undefined;
                $.each(_this.liveLineupsModel.modelData, function (key, value) {
                        if (value.lineupId == id) {
                                contestId = value.contests[0].id;
                                lineup = value;
                        }
                });

                // FIXME ... use when/then so these calls are stacked after each other
                // load contest socket
                _this.contestIdModel = new ruckus.models.contestid({});
                _this.models.push(_this.contestIdModel);
                var sub2 = _this.msgBus.subscribe("model.contestid.all", function (data) {
                        sub2.unsubscribe();
                        var contest = data.data.contests[0];

                        // load controls
                        _this.lineupRulesModel = new ruckus.models.lineuprules({});
                        _this.models.push(_this.lineupRulesModel);
                        var sub3 = _this.msgBus.subscribe("model.lineuprules.retrieve", function (rules) {
                                sub3.unsubscribe();

                                _this.require_template('dashboardlineupsedit-tpl');
                                dust.render('dusttemplates/dashboardlineupsedit-tpl', data, function (err, out) {
                                        emptyDiv.append(out);
                                        _this.addScrollBars();
                                        var containerStep1 = $('#dshl_step1container');
                                        var containerStep2 = $('#dshl_step2container');
                                        containerStep2.hide();
                                        var contestentryavailableathletes = $('#dshl_contestentryavailableathletes');
                                        var contestentryselectedathletes = $('#dshl_contestentryselectedathletes');

                                        var lineupbuilderselectedathletes = new ruckus.subpagecontrols.lineupbuilderselectedathletes({
                                                container: contestentryselectedathletes,
                                                //					data: data,
                                                editLineup: lineup,
                                                contest: contest,
                                                lineuprules: _this.lineupRulesModel.modelData
                                        });
                                        lineupbuilderselectedathletes.load();
                                        _this.controls.push(lineupbuilderselectedathletes);
                                        _this.step1Controls.push(lineupbuilderselectedathletes);

                                        var lineupbuilderavailableathletes = new ruckus.subpagecontrols.lineupbuilderavailableathletes({
                                                container: contestentryavailableathletes,
                                                //					data: data,
                                                editathletes: lineup.athletes,
                                                contest: contest,
                                                lineupbuilderselectedathletes: lineupbuilderselectedathletes,
                                                lineuprules: _this.lineupRulesModel.modelData
                                        });
                                        lineupbuilderavailableathletes.load();
                                        _this.controls.push(lineupbuilderavailableathletes);
                                        _this.step1Controls.push(lineupbuilderavailableathletes);

                                        _this.msgBus.subscribe('control.lbsa.editlineup', function (data) {
                                                _this.editLineupStep2(id, data, emptyDiv, containerStep1, containerStep2);
                                        });

                                        // close events
                                        $('#dshl_closestep1').bind('click', function (evt) {
                                                evt.stopPropagation();
                                                _this.clearEditMode();
                                        });
                                        $('#dshl_closestep2').bind('click', function (evt) {
                                                evt.stopPropagation();
                                                _this.clearEditMode();
                                        });
                                        $('html, body').animate({
                                                scrollTop: emptyDiv.offset().top
                                        }, 2000);
                                });
                        });
                        _this.lineupRulesModel.fetch({league: contest.league});
                });
                _this.contestIdModel.fetch({ contestid: contestId });
        };
        ruckus.pagecontrols.dashboardlineups.prototype.editLineupStep2 = function (id, selected, emptyDiv, containerStep1, containerStep2) {
                var _this = this;
                var lineup = undefined;
                $.each(_this.liveLineupsModel.modelData, function (key, value) {
                        if (value.lineupId == id) {
                                lineup = value;
                        }
                });
		$.each(lineup.contests, function (key, contest) {
                        var formatted = _this.formatContestName(contest, '2line');
                        contest.line1 = formatted.line1 +  ' ' + formatted.line2;
                });

                containerStep1.hide();
                containerStep2.show();

                var arrSelected = [];
                $.each(selected.data, function (k, v) {
                        arrSelected.push(v);
                });

//		_this.consolelog(arrSelected);
                _this.require_template('dashboardlineupseditstep2-tpl');
                dust.render('dusttemplates/dashboardlineupseditstep2-tpl', {lineup: lineup, selected: arrSelected}, function (err, out) {
                        containerStep2.append(out);

                        var contestlist = [];
                        $('#dshl_returntoedit').bind('click', function (evt) {
                                evt.stopPropagation();
                                containerStep1.show();
                                containerStep2.html('');
                        });

                        $('.dshl_contest').bind('click', function (evt) {
                                evt.stopPropagation();
                                var contest_el = $('#dshl_contest_' + evt.delegateTarget.id.split('_')[2]);
                                if (contest_el.hasClass('checkcircleSelected')) {
                                        contestlist = jQuery.grep(contestlist, function (value) {
                                                return value != evt.delegateTarget.id.split('_')[2];
                                        });
                                        contest_el.removeClass('checkcircleSelected');
                                        contest_el.addClass('checkcircle');
                                } else {
                                        contestlist.push(evt.delegateTarget.id.split('_')[2]);
                                        contest_el.removeClass('checkcircle');
                                        contest_el.addClass('checkcircleSelected');
                                }
                        });

                        $('#dshl_submitchanges').bind('click', function (evt) {
                                evt.stopPropagation();
//				_this.consolelog('MAKE CHANGES');
//				_this.consolelog(selected);
//				_this.consolelog(contestlist);

                                // this is a little inefficient ... if we could pass contestid straight to endpoint rather than trying to find all the entryIds
                                var entryIds = [];
                                $.each(lineup.contests, function (key, value) {
                                        $.each(contestlist, function (k, v) {
                                                if (value.id == v) {
                                                        $.each(value.entries, function (x, y) {
                                                                entryIds.push(y);
                                                        });
                                                }
                                        });
                                });

                                var athletes = _this.convertAthletes(selected.data, _this.lineupRulesModel.modelData);
//				_this.consolelog(athletes);

                                var subSuccess = undefined;
                                var subFailed = undefined;
                                subSuccess = _this.msgBus.subscribe("model.lineupupdate.success", function (data) {
                                        subSuccess.unsubscribe();
                                        subFailed.unsubscribe();
                                        Backbone.history.loadUrl(Backbone.history.fragment);
                                });
                                subFailed = _this.msgBus.subscribe("model.lineupenter.failed", function (data) {
                                        subSuccess.unsubscribe();
                                        subFailed.unsubscribe();
                                });
                                _this.lineupUpdateModel = new ruckus.models.lineupupdate({});
                                _this.models.push(_this.lineupUpdateModel);
                                _this.lineupUpdateModel.fetch({lineupId: lineup.lineupId, lineupName: lineup.lineupName, entryIds: entryIds, athletes: athletes});
                        });
                });
        };
        ruckus.pagecontrols.dashboardlineups.prototype.convertAthletes = function (selected, lineuprules) {
                lineupTemplate = [];
                $.each(lineuprules, function (key, value) {
                        if (value.numberOfAthletes == 1) {
                                lineupTemplate.push(value.abbreviation);
                        } else {
                                for (var x = 1; x <= value.numberOfAthletes; x++) {
                                        lineupTemplate.push(value.abbreviation + x);
                                }
                        }
                });
                var invert = function (obj) {
                        var new_obj = {};
                        for (var prop in obj) {
                                if (obj.hasOwnProperty(prop)) {
                                        new_obj[obj[prop].abbreviation] = prop;
                                }
                        }
                        return new_obj;
                };
                var revMapping = invert(lineuprules);

                var athletes = [];
                $.each(lineupTemplate, function (k, v) {
                        var truePos = v.replace(/[0-9]/g, ''); // find the actual pos from lineup one ... RB1 -> RB
                        athletes.push({id: selected[v].id, pos: revMapping[truePos], athleteSportEventInfoId: selected[v].athleteSportEventInfoId});
                });
                return athletes;
        };
        ruckus.pagecontrols.dashboardlineups.prototype.removeContest = function (contestId, lineupId) {
                var _this = this;

                var subSuccess = undefined;
                var subFailed = undefined;
                subSuccess = _this.msgBus.subscribe("model.lineupremove.success", function (data) {
                        subSuccess.unsubscribe();
                        subFailed.unsubscribe();
                        Backbone.history.loadUrl(Backbone.history.fragment);
                });
                subFailed = _this.msgBus.subscribe("model.lineupremove.failed", function (data) {
                        subSuccess.unsubscribe();
                        subFailed.unsubscribe();
                });
                _this.lineupRemoveModel = new ruckus.models.lineupremove({});
                _this.models.push(this.lineupRemoveModel);
                _this.lineupRemoveModel.fetch({lineupId: lineupId, contestId: contestId});
        };
        ruckus.pagecontrols.dashboardlineups.prototype.clearEditMode = function () {
                var _this = this;
                $('.dshl_empty').html('');
                $.each(_this.step1Controls, function (k, v) {
                        v.unload();
                });
        };

        // EVENTS
        ruckus.pagecontrols.dashboardlineups.prototype.onUpdateServerTime = function (data) {
                var _this = this;
                _this.log({type: 'general', data: data, msg: 'DASHBOARD PAGE CONTROL > onUpdateServerTime'});
                _this.viewdata.servertime = JSON.parse(data);
                _this.viewdata.servertime_updated = ruckus.modules.datetime.now();

                if (!_this.viewdata.contests) return false;
                $.each(_this.viewdata.contests, function (key, val) {
                        val.remainingTime = ruckus.modules.datetime.diff(_this.viewdata.servertime, val.startTime, 'seconds');
                });
        };

        // COUNTERS
        ruckus.pagecontrols.dashboardlineups.prototype.updateCounters = function () {
                var _this = this;
                if (!_this.viewdata.lineups) return false;

                $.each(_this.viewdata.lineups, function (key, val) {
                        // decrement the time by one second
                        if (val.remainingTime === 1) {
                                ruckus.modules.navigation.reload();
                        }

                        val.remainingTime = val.remainingTime - 1;
                        var counter_container = $('#dshl_starttime_' + val.lineupId);
                        if (counter_container.length > 0) {
                                if (counter_container.find(".dshl_cell_upcoming_chart").length === 1) {
                                        ruckus.modules.counters.circle.update(counter_container, "dshl_cell_upcoming_chart", val.remainingTime);
                                        if (val.remainingTime < 1) {
                                                counter_container.find(".timeStampSubTitle").html('');
                                        }
                                }
                        }
                });
        };
        ruckus.pagecontrols.dashboardlineups.prototype.startIntervals = function () {
                var _this = this;
                if (this.serverTimeInterval === undefined) {
                        this.serverTimeInterval = setInterval(_this.viewrepo.fetchServerTime, 60000);
                        /**/
                        _this.intervals.push(this.serverTimeInterval);
                }
                if (this.counterInterval === undefined) {
                        this.counterInterval = setInterval(_this.updateCounters, 1000);
                        _this.intervals.push(this.counterInterval);
                }
        };

        ruckus.pagecontrols.dashboardlineups.prototype.unload = function () {
                this.viewrepo.unload();
                delete this.viewrepo;
                this.destroyControl();
        };

        return ruckus.pagecontrols.dashboardlineups;
});
