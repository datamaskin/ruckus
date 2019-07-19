define([
        'rg_model_base',
        'jquery',
        'atmosphere'
], function (Base) {
        ruckus.models.contestdrillin = function (parameters) {
                var _this = this;
                Base.call(this);
                _this.modelData = undefined;
                if(parameters.mockdata) {
                        _this.mockdata = parameters.mockdata;
                }
                else {
                        _this.mockdata = false;
                }
                if(parameters.intervaldata) {
                        _this.intervaldata = parameters.intervaldata;
                }
                else {
                        _this.intervaldata = false;
                }
                if(parameters.interval) {
                        _this.interval = parameters.interval;
                }
                else {
                        _this.interval = 5000;
                }
        };

        ruckus.models.contestdrillin.prototype = Object.create(Base.prototype);

        ruckus.models.contestdrillin.prototype.fetch = function (args) {
                if(!args) args = {};

                var _this = this;
                var sub2 = _this.msgBus.subscribe(ruckus.pubsub.subscriptions.sockets.contestlivedetail.all, function (data) {
                        _this.msgBus.publish(ruckus.pubsub.subscriptions.models.data.contestlivedetail.all, {data: data});
                });
                this.subscriptions.push(sub2);

                var sub3 = _this.msgBus.subscribe(ruckus.pubsub.subscriptions.sockets.contestlivedetail.entry.update, function (data) {
                        _this.msgBus.publish(ruckus.pubsub.subscriptions.models.data.contestlivedetail.entry.update, {data: data});
                });
                this.subscriptions.push(sub3);

                var sub4 = _this.msgBus.subscribe(ruckus.pubsub.subscriptions.sockets.contestlivedetail.sportevent.update, function (data) {
                        _this.msgBus.publish(ruckus.pubsub.subscriptions.models.data.contestlivedetail.sportevent.update, {data: data});
                });
                this.subscriptions.push(sub4);

                var sub5 = _this.msgBus.subscribe(ruckus.pubsub.subscriptions.sockets.contestlivedetail.athlete.sporteventinfoupdate, function (data) {
                        _this.msgBus.publish(ruckus.pubsub.subscriptions.models.data.contestlivedetail.athlete.sporteventinfoupdate, {data: data});
                });
                this.subscriptions.push(sub5);

                var sub6 = _this.msgBus.subscribe(ruckus.pubsub.subscriptions.sockets.contestlivedetail.athlete.statusupdate, function (data) {
                        _this.msgBus.publish(ruckus.pubsub.subscriptions.models.data.contestlivedetail.athlete.statusupdate, {data: data});
                });
                this.subscriptions.push(sub6);

                if (args.contestId) {
                        this.openChannel(ruckus.pubsub.channels.contestlive + '/' + args.contestId);
                } else {
                        this.openChannel(ruckus.pubsub.channels.contestlive);
                }
        };

        ruckus.models.contestdrillin.prototype.intervalData = function () {
                var _this = this;

                var runIntervals = function () {
                        // ENTRY INTERVAL
                        var runEntry = function () {
                                _this.consolelog('INTERVAL ENTRY');
                                var data = {"unitsRemaining": 120, "fpp": 5.3, "id": 10, "user": "terrorsquid1"};
//                                _this.consolelog(data);
                                _this.msgBus.publish(ruckus.pubsub.subscriptions.models.data.contestlivedetail.entry.update, {data: data});

                                var data = {"unitsRemaining": 150, "fpp": 6.4, "id": 11, "user": "terrorsquid2"};
//                                _this.consolelog(data);
                                _this.msgBus.publish(ruckus.pubsub.subscriptions.models.data.contestlivedetail.entry.update, {data: data});

                        };
                        setTimeout(runEntry, 1);

                        var runEntry2 = function () {
                                _this.consolelog('INTERVAL ENTRY');
                                var data = {"unitsRemaining": 100, "fpp": 7.3, "id": 10, "user": "terrorsquid1"};
//                                _this.consolelog(data);
                                _this.msgBus.publish(ruckus.pubsub.subscriptions.models.data.contestlivedetail.entry.update, {data: data});

                                var data = {"unitsRemaining": 140, "fpp": 6.9, "id": 11, "user": "terrorsquid2"};
//                                _this.consolelog(data);
                                _this.msgBus.publish(ruckus.pubsub.subscriptions.models.data.contestlivedetail.entry.update, {data: data});

                        };
                        setTimeout(runEntry2, 5000);

                        // ALTHLETE INTERVAL 
                        var runAthlete = function () {
                                _this.consolelog('INTERVAL ATHLETE');
//                                _this.consolelog(data);
                                var data = {"indicator": 0, "firstName": "Tom", "lastName": "Brady", "stats": [
                                        {"amount": 1, "fpp": 1, "name": "Receiving TD", "abbr": "TDs", "id": "NFL_6"},
                                        {"amount": 1, "fpp": 1, "name": "Rushing TD", "abbr": "TDs", "id": "NFL_5"},
                                        {"amount": 1, "fpp": 1, "name": "Kick Return TD", "abbr": "TDs", "id": "NFL_12"},
                                        {"amount": 1, "fpp": 1, "name": "Passing TD", "abbr": "PTDs", "id": "NFL_4"},
                                        {"amount": 1, "fpp": 1, "name": "Two Point Conversion", "abbr": "2PT", "id": "NFL_17"},
                                        {"amount": 1, "fpp": 1, "name": "Passing Yards", "abbr": "PYDs", "id": "NFL_1"},
                                        {"amount": 1, "fpp": 1, "name": "Reception", "abbr": "REC", "id": "NFL_8"},
                                        {"amount": 1, "fpp": 1, "name": "Receiving Yards", "abbr": "REYDs", "id": "NFL_3"},
                                        {"amount": 1, "fpp": 1, "name": "Rushing Yards", "abbr": "RUYDs", "id": "NFL_2"},
                                        {"amount": 1, "fpp": 1, "name": "Lost Fumble", "abbr": "FUM", "id": "NFL_9"},
                                        {"amount": 1, "fpp": 1, "name": "Interception", "abbr": "INT", "id": "NFL_10"}
                                ], "fpp": 10.01, "unitsRemaining": 40, "timeline": [
                                        {"fpChange": "+6.0", "description": "Brady throws 50 yards for touchdown.", "athleteSportEventInfoId": 477, "timestamp": 1408038877070}
                                ], "athleteSportEventInfoId": 776};
                                _this.msgBus.publish(ruckus.pubsub.subscriptions.models.data.contestlivedetail.athlete.sporteventinfoupdate, {data: data});
                        };
                        setTimeout(runAthlete, 1);

                        var runAthlete2 = function () {
                                _this.consolelog('INTERVAL ATHLETE');
//                                _this.consolelog(data);
                                var data = {"indicator": 1, "firstName": "Tom", "lastName": "Brady", "stats": [
                                        {"amount": 2, "fpp": 2, "name": "Receiving TD", "abbr": "TDs", "id": "NFL_6"},
                                        {"amount": 2, "fpp": 2, "name": "Rushing TD", "abbr": "TDs", "id": "NFL_5"},
                                        {"amount": 2, "fpp": 2, "name": "Kick Return TD", "abbr": "TDs", "id": "NFL_12"},
                                        {"amount": 2, "fpp": 2, "name": "Passing TD", "abbr": "PTDs", "id": "NFL_4"},
                                        {"amount": 2, "fpp": 2, "name": "Two Point Conversion", "abbr": "2PT", "id": "NFL_17"},
                                        {"amount": 2, "fpp": 2, "name": "Passing Yards", "abbr": "PYDs", "id": "NFL_1"},
                                        {"amount": 2, "fpp": 2, "name": "Reception", "abbr": "REC", "id": "NFL_8"},
                                        {"amount": 2, "fpp": 2, "name": "Receiving Yards", "abbr": "REYDs", "id": "NFL_3"},
                                        {"amount": 2, "fpp": 2, "name": "Rushing Yards", "abbr": "RUYDs", "id": "NFL_2"},
                                        {"amount": 2, "fpp": 2, "name": "Lost Fumble", "abbr": "FUM", "id": "NFL_9"},
                                        {"amount": 2, "fpp": 2, "name": "Interception", "abbr": "INT", "id": "NFL_10"}
                                ], "fpp": 16.81, "unitsRemaining": 20, "timeline": [
                                        {"fpChange": "+6.0", "description": "Brady throws 50 yards for touchdown AGAIN.", "athleteSportEventInfoId": 477, "timestamp": 1408038877070}
                                ], "athleteSportEventInfoId": 776};
                                _this.msgBus.publish(ruckus.pubsub.subscriptions.models.data.contestlivedetail.athlete.sporteventinfoupdate, {data: data});
                        };
                        setTimeout(runAthlete2, 3000);

                        // non-existant player to test team feed
                        var runAthlete3 = function () {
                                _this.consolelog('INTERVAL ATHLETE');
//                                _this.consolelog(data);
                                var data = {"indicator": 1, "firstName": "Tom", "lastName": "Brady", "stats": [
                                        {"amount": 2, "fpp": 2, "name": "Receiving TD", "abbr": "TDs", "id": "NFL_6"},
                                        {"amount": 2, "fpp": 2, "name": "Rushing TD", "abbr": "TDs", "id": "NFL_5"},
                                        {"amount": 2, "fpp": 2, "name": "Kick Return TD", "abbr": "TDs", "id": "NFL_12"},
                                        {"amount": 2, "fpp": 2, "name": "Passing TD", "abbr": "PTDs", "id": "NFL_4"},
                                        {"amount": 2, "fpp": 2, "name": "Two Point Conversion", "abbr": "2PT", "id": "NFL_17"},
                                        {"amount": 2, "fpp": 2, "name": "Passing Yards", "abbr": "PYDs", "id": "NFL_1"},
                                        {"amount": 2, "fpp": 2, "name": "Reception", "abbr": "REC", "id": "NFL_8"},
                                        {"amount": 2, "fpp": 2, "name": "Receiving Yards", "abbr": "REYDs", "id": "NFL_3"},
                                        {"amount": 2, "fpp": 2, "name": "Rushing Yards", "abbr": "RUYDs", "id": "NFL_2"},
                                        {"amount": 2, "fpp": 2, "name": "Lost Fumble", "abbr": "FUM", "id": "NFL_9"},
                                        {"amount": 2, "fpp": 2, "name": "Interception", "abbr": "INT", "id": "NFL_10"}
                                ], "fpp": 16.81, "unitsRemaining": 20, "timeline": [
                                        {"fpChange": "+6.0", "description": "You shouldn't see me!.", "athleteSportEventInfoId": 321434213, "timestamp": 1408038877070}
                                ], "athleteSportEventInfoId": 321434213};
                                _this.msgBus.publish(ruckus.pubsub.subscriptions.models.data.contestlivedetail.athlete.sporteventinfoupdate, {data: data});
                        };
                        setTimeout(runAthlete3, 7000);

			// units remaining and indicator general update
			var runGeneral1 = function(){
				_this.consolelog('INTERVAL GENERAL');
				var data = {"indicator":2,"unitsRemaining":35,"athleteSportEventInfoId":776};
				_this.msgBus.publish(ruckus.pubsub.subscriptions.models.data.contestlivedetail.athlete.statusupdate, {data:data});
			};
			setTimeout(runGeneral1, 6000);
			var runGeneral2 = function(){
				_this.consolelog('INTERVAL GENERAL');
				var data = {"indicator":0,"unitsRemaining":5,"athleteSportEventInfoId":776};
				_this.msgBus.publish(ruckus.pubsub.subscriptions.models.data.contestlivedetail.athlete.statusupdate, {data:data});
			};
			setTimeout(runGeneral2, 8000);


                        // SPORT EVENT INTERVAL (MIA vs TB)
                        var runSportEvent = function () {
                                _this.consolelog('INTERVAL SPORT EVENT');
                                var data = {"homeId": "356", "homeTeam": "Sea", "awayId": "354", "awayTeam": "Min", "homeScore": 7, "awayScore": 3, "id": 1024};
//                                _this.consolelog(data);
                                _this.msgBus.publish(ruckus.pubsub.subscriptions.models.data.contestlivedetail.sportevent.update, {data: data});
                        };
                        setTimeout(runSportEvent, 1);
                };

                _this.intervals.push(setInterval(runIntervals, 10000));
        };

        ruckus.models.contestdrillin.prototype.staticData = function () {
                return {};
        };

        return ruckus.models.generic;
});
