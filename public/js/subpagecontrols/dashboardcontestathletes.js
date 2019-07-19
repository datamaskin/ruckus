// Author: Scott Gay
define([
        "assets/js/subpagecontrols/base.js",
        "assets/js/libraries/jquery.min.js",
//	"assets/js/libraries/underscore-min.js"
        "assets/js/libraries/dust-core.min.js",
        "assets/js/models/contestlivelineups.js"
], function (Base) {
        ruckus.subpagecontrols.dashboardcontestathletes = function (parameters) {
                Base.call(this);
                this.init = function () {
                        this.parameters = parameters;
                };
                this.init();
        };

        ruckus.subpagecontrols.dashboardcontestathletes.prototype = Object.create(Base.prototype);
        ruckus.subpagecontrols.dashboardcontestathletes.prototype.load = function () {
                var _this = this;
                this.getContainer();
                this.container.addClass('ruckus-spc-dashboardcontestathletes');

                var sub = _this.msgBus.subscribe('controls.dhcr.selectlineup', function (dataSL) {
                        if (dataSL.lineup.isMe) { // render single
                                _this.log({type: 'GENERAL', data: dataSL, msg: 'LINEUP SELECTED SINGLE'});
                                _this.lineup = dataSL;
                                _this.lineup.lineup.timePercentage = _this.formatTimePercentageTeam(_this.parameters.contest.league, _this.lineup.lineup.unitsRemaining);
                                _this.lineupModel = new ruckus.models.contestlivelineups({});
                                _this.models.push(_this.lineupModel);
                                var sub2 = _this.msgBus.subscribe("model.contestlivelineups.retrieve", function (data) {
                                        sub2.unsubscribe();
                                        _this.lineupModel = data.data;
                                        $.each(_this.lineupModel, function (key, value) {
                                                value.timePercentage = _this.formatTimePercentage(_this.parameters.contest.league, value.unitsRemaining);
                                                value.firstInitial = value.firstName.substring(0, 1);
                                        });
                                        _this.msgBus.publish("control.dhca.lineupathletes", _this.lineupModel);
                                        _this.renderSingle();
                                });
                                _this.subscriptions.push(sub2);
                                _this.lineupModel.fetch({id: dataSL.lineup.lineupId});
                        } else { // render double
                                _this.log({type: 'GENERAL', data: dataSL, msg: 'LINEUP SELECTED DOUBLE'});
                                _this.lineup2 = dataSL;
                                _this.lineup2.lineup.timePercentage = _this.formatTimePercentageTeam(_this.parameters.contest.league, _this.lineup2.lineup.unitsRemaining);
                                _this.lineupModel2 = new ruckus.models.contestlivelineups({});
                                _this.models.push(_this.lineupModel2);
                                var sub3 = _this.msgBus.subscribe("model.contestlivelineups.retrieve", function (data) {
                                        sub3.unsubscribe();
                                        _this.lineupModel2 = data.data;
                                        $.each(_this.lineupModel2, function (key, value) {
                                                value.timePercentage = _this.formatTimePercentage(_this.parameters.contest.league, value.unitsRemaining);
                                                value.firstInitial = value.firstName.substring(0, 1);
                                        });
                                        _this.renderDouble();
                                });
                                _this.subscriptions.push(sub3);
                                _this.lineupModel2.fetch({id: dataSL.lineup.lineupId});
                        }
                });
                _this.subscriptions.push(sub);
        };

        ruckus.subpagecontrols.dashboardcontestathletes.prototype.renderSingle = function () {
                var _this = this;

                this.log({type: 'GENERAL', data: _this.lineup, msg: 'LINEUP SELECTED'});
                $.each(_this.lineup, function (k, v) {
                        v.formattedPosition = _this.formatPlace(v.pos);
                });
                this.log({type: 'GENERAL', data: _this.lineupModel, msg: 'LINEUP ATHLETES'});
                $.each(_this.lineupModel, function (k, v) {
                        switch (v.indicator) {
                                case 0 :
                                        v.indicatorClass = 'dot';
                                        break;
                                case 1 :
                                        v.indicatorClass = 'dotPlay';
                                        break;
                                case 2 :
                                        v.indicatorClass = 'dotRedzone';
                                        break;
                        }
			try {
                                v.stats = JSON.parse(v.stats);
                        } catch(e) {}
//                        v.stats = JSON.parse(v.stats);
                        v.desc = '';
                        $.each(v.stats, function (x, y) {
                                v.desc += y.amount + ' ' + y.abbr + ', ';
                        });
			v.desc = v.desc.substring(0,v.desc.length-2);
                });
                this.require_template('dashboardcontestathletessingle-tpl');
                dust.render('dusttemplates/dashboardcontestathletessingle-tpl', {lineup: this.lineup, athletes: _this.lineupModel}, function (err, out) {
                        _this.container.html(out);
                        _this.addScrollBars();
// console.log('SINGLE');
// console.log(_this.lineup.lineup.entryId);
                        _this.msgBus.publish(ruckus.pubsub.subscriptions.view.dashboardcontest.get.entry, _this.lineup.lineup.entryId);
                        $(".dhca_item").bind("click", function (evt) {
                                evt.stopPropagation();
                                _this.msgBus.publish('controls.dhca.selectplayer', {lineup: _this.lineup, athleteSportEventInfoId: evt.delegateTarget.id.split('_')[1]});
//                                _this.msgBus.publish('controls.dhca.selectplayer', {athleteSportEventInfoId: evt.delegateTarget.id.split('_')[1]});
                        });

                });

        };

        ruckus.subpagecontrols.dashboardcontestathletes.prototype.renderDouble = function () {
                var _this = this;
                this.log({type: 'GENERAL', data: _this.lineup2, msg: 'LINEUP SELECTED'});
                $.each(_this.lineup2, function (k, v) {
                        v.formattedPosition = _this.formatPlace(v.pos);
                });
                this.log({type: 'GENERAL', data: _this.lineupModel2, msg: 'LINEUP2 ATHLETES'});

                var arrShared = [];
                var arrSharedIds = [];
                var arrSharedPos = [];
                var arrSharedPosIds = [];
                var arrOther = [];
                // firstpass find shared players
                $.each(_this.lineupModel, function (k1, v1) {
                        $.each(_this.lineupModel2, function (k2, v2) {
                                if (v1.id == v2.id) {
                                        if (v1.position == v2.position) {
                                                arrShared.push({
                                                        athlete1: v1,
                                                        athlete2: v2,
                                                        position: v1.position
                                                });
                                                arrSharedIds.push(v1.id);
                                        } else {
                                                arrShared.push({
                                                        athlete1: v1,
                                                        athlete2: v2,
                                                        position: v1.position + '/' + v2.position
                                                });
                                                arrSharedIds.push(v1.id);
                                        }
                                }
                        });
                });
                _this.log({type: "GENERAL", data: arrShared, msg: "SHARED PLAYERS"});

                // second pass find shared positions not identified in firstpass
                $.each(_this.lineupModel, function (k1, v1) {
                        $.each(_this.lineupModel2, function (k2, v2) {
                                if (v1.position == v2.position && arrSharedIds.indexOf(v1.id) == -1 && arrSharedIds.indexOf(v2.id) == -1 && arrSharedPosIds.indexOf(v1.id) == -1 && arrSharedPosIds.indexOf(v2.id) == -1) {
                                        arrSharedPos.push({
                                                athlete1: v1,
                                                athlete2: v2,
                                                position: v1.position
                                        });
                                        arrSharedPosIds.push(v1.id);
                                        arrSharedPosIds.push(v2.id);
                                }
                        });
                });
                _this.log({type: "GENERAL", data: arrSharedPos, msg: "SHARED POSITIONS"});

                // third pass match up leftovers
                var arrOtherLineup1 = [];
                var arrOtherLineup2 = [];
                for (var x = 0; x < _this.lineupModel.length; x++) {
                        if (arrSharedIds.indexOf(_this.lineupModel[x].id) == -1 && arrSharedPosIds.indexOf(_this.lineupModel[x].id) == -1) {
                                arrOtherLineup1.push(_this.lineupModel[x]);
                        }
                        if (arrSharedIds.indexOf(_this.lineupModel2[x].id) == -1 && arrSharedPosIds.indexOf(_this.lineupModel2[x].id) == -1) {
                                arrOtherLineup2.push(_this.lineupModel2[x]);
                        }
                }
                for (var y = 0; y < arrOtherLineup1.length; y++) {
                        arrOther.push({
                                athlete1: arrOtherLineup1[y],
                                athlete2: arrOtherLineup2[y],
                                position: arrOtherLineup1[y].position + '/' + arrOtherLineup2[y].position
                        });
                }
                _this.log({type: "GENERAL", data: arrSharedPos, msg: "OTHER ATHLETES"});

                this.require_template('dashboardcontestathletesdouble-tpl');
                this.log({type: 'GENERAL', data: _this.lineup, msg: 'LINEUP 1'});
                this.log({type: 'GENERAL', data: _this.lineup2, msg: 'LINEUP 2'});
                this.log({type: 'GENERAL', data: arrSharedPos, msg: 'SHARED POSITIONS'});
                this.log({type: 'GENERAL', data: arrOther, msg: 'OTHER'});
                this.log({type: 'GENERAL', data: arrShared, msg: 'SHARED'});
                dust.render('dusttemplates/dashboardcontestathletesdouble-tpl', {lineup1: _this.lineup, lineup2: _this.lineup2, sharedPos: arrSharedPos, other: arrOther, shared: arrShared}, function (err, out) {
                        _this.container.html(out);
                        _this.addScrollBars();
                        _this.msgBus.publish(ruckus.pubsub.subscriptions.view.dashboardcontest.get.entry, _this.lineup.lineup.entryId);
                        _this.msgBus.publish(ruckus.pubsub.subscriptions.view.dashboardcontest.get.entry, _this.lineup2.lineup.entryId);

			$.each(arrSharedPos, function(k,v){
				_this.msgBus.publish(ruckus.pubsub.subscriptions.view.dashboardcontest.get.athlete, v.athlete1.athleteSportEventInfoId);
				_this.msgBus.publish(ruckus.pubsub.subscriptions.view.dashboardcontest.get.athlete, v.athlete2.athleteSportEventInfoId);
			});
			$.each(arrOther, function(k,v){
                                _this.msgBus.publish(ruckus.pubsub.subscriptions.view.dashboardcontest.get.athlete, v.athlete1.athleteSportEventInfoId);
                                _this.msgBus.publish(ruckus.pubsub.subscriptions.view.dashboardcontest.get.athlete, v.athlete2.athleteSportEventInfoId);
                        });
			$.each(arrShared, function(k,v){
                                _this.msgBus.publish(ruckus.pubsub.subscriptions.view.dashboardcontest.get.athlete, v.athlete1.athleteSportEventInfoId);
                                _this.msgBus.publish(ruckus.pubsub.subscriptions.view.dashboardcontest.get.athlete, v.athlete2.athleteSportEventInfoId);
                        });

                        if (arrShared.length == 0)
                                $('#dhca_sharedplayerscontainer').hide();

                        $(".dhca_item").bind("click", function (evt) {
                                evt.stopPropagation();
                                var selAthlete = evt.delegateTarget.id.split('_')[1];
                                var lu = undefined;
                                $.each(_this.lineupModel, function (k, v) {
                                        if (v.athleteSportEventInfoId == selAthlete)
                                                lu = _this.lineup;
                                });
                                if (lu == undefined)
                                        lu = _this.lineup2;
                                _this.msgBus.publish('controls.dhca.selectplayer', {lineup: lu, athleteSportEventInfoId: selAthlete});
                        });
                });
        };

        ruckus.subpagecontrols.dashboardcontestathletes.prototype.unload = function () {
                this.destroyControl();
        };

        return ruckus.subpagecontrols.dashboardcontestathletes;
});
	

