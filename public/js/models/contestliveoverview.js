define([
//	"assets/js/base.js",
        "assets/js/models/basews.js",
        "rg_pubsub"
], function (Base) {
        ruckus.models.contestliveoverview = function (parameters) {
                Base.call(this);
                this.init = function () {
                        this.parameters = parameters;
                        // set parameter defaults
                        this.modelData = undefined;
                };
                this.init();
        };

        ruckus.models.contestliveoverview.prototype = Object.create(Base.prototype);

        ruckus.models.contestliveoverview.prototype.fetch = function (fParams) {
                var _this = this;

                var sub = _this.msgBus.subscribe(ruckus.pubsub.subscriptions.sockets.contestliveoverview.all, function (data) {
                        sub.unsubscribe();
                        _this.modelData = { contests: data };
                        _this.msgBus.publish(ruckus.pubsub.subscriptions.models.data.contestliveoverview.all, {contests: _this.modelData});
                });
                this.subscriptions.push(sub);

                var sub2 = _this.msgBus.subscribe(ruckus.pubsub.subscriptions.sockets.contestliveoverview.update, function (data) {
                        _this.msgBus.publish(ruckus.pubsub.subscriptions.models.data.contestliveoverview.update, {data: data});
                });
                this.subscriptions.push(sub2);

                this.openChannel(ruckus.pubsub.channels.contestliveoverview);
                
		// TEST INTERVAL
//                this.intervalData();
        };

        ruckus.models.contestliveoverview.prototype.intervalData = function () {
                var _this = this;

                var runIntervals = function () {
                        // OVERVIEW INTERVAL
                        var runOverview = function () {
                                _this.consolelog('INTERVAL OVERVIEW');
//                                var data = [
                                       var data = {
                                                "contestId": "kCI0WbST",
                                                "projectedPayout": 10000,
                                                "multiplier": 1,
                                                "fpp": 100.0,
                                                "unitsRemaining": 600,
                                                "league": "NFL",
                                                "payout": 10000,
                                                "prizePool": 10000,
                                                "payouts": [
                                                        {
                                                                "leadingPosition": 1,
                                                                "trailingPosition": 1,
                                                                "payoutAmount": 10000
                                                        }
                                                ],
                                                "contestType": {
                                                        "id": 1,
                                                        "name": "Double up",
                                                        "abbr": "DU"
                                                },
                                                "currentEntries": 1,
                                                "contestState": "active",
                                                "position": 1,
                                                "entryFee": 100,
                                                "timeUntilStart": 3599308,
                                                "startTime": 1406675100000,
                                                "lineupId": 2
                                        };
//                                ];
//                                _this.consolelog(data);
                                _this.msgBus.publish(ruckus.pubsub.subscriptions.models.data.contestliveoverview.update, {data: data});
                        };
                        setTimeout(runOverview, 1);

			var runOverview2 = function () {
                                _this.consolelog('INTERVAL OVERVIEW');
//                                var data = [
                                       var data = {
                                                "contestId": "kCI0WbST",
                                                "projectedPayout": 10000,
                                                "multiplier": 1,
                                                "fpp": 50.0,
                                                "unitsRemaining": 300,
                                                "league": "NFL",
                                                "payout": 10000,
                                                "prizePool": 10000,
                                                "payouts": [
                                                        {
                                                                "leadingPosition": 1,
                                                                "trailingPosition": 1,
                                                                "payoutAmount": 10000
                                                        }
                                                ],
                                                "contestType": {
                                                        "id": 1,
                                                        "name": "Double up",
                                                        "abbr": "DU"
                                                },
                                                "currentEntries": 1,
                                                "contestState": "active",
                                                "position": 2,
                                                "entryFee": 100,
                                                "timeUntilStart": 3599308,
                                                "startTime": 1406675100000,
                                                "lineupId": 2
                                        };
//                                ];
//                                _this.consolelog(data);
                                _this.msgBus.publish(ruckus.pubsub.subscriptions.models.data.contestliveoverview.update, {data: data});
                        };
                        setTimeout(runOverview2, 3000);

			var runOverview3 = function () {
                                _this.consolelog('INTERVAL OVERVIEW');
//                                var data = [
                                       var data = {
                                                "contestId": "kCI0WbST",
                                                "projectedPayout": 10000,
                                                "multiplier": 1,
                                                "fpp": 50.0,
                                                "unitsRemaining": 0,
                                                "league": "NFL",
                                                "payout": 10000,
                                                "prizePool": 10000,
                                                "payouts": [
                                                        {
                                                                "leadingPosition": 1,
                                                                "trailingPosition": 1,
                                                                "payoutAmount": 10000
                                                        }
                                                ],
                                                "contestType": {
                                                        "id": 1,
                                                        "name": "Double up",
                                                        "abbr": "DU"
                                                },
                                                "currentEntries": 1,
                                                "contestState": "active",
                                                "position": 2,
                                                "entryFee": 100,
                                                "timeUntilStart": 3599308,
                                                "startTime": 1406675100000,
                                                "lineupId": 2
                                        };
//                                ];
//                                _this.consolelog(data);
                                _this.msgBus.publish(ruckus.pubsub.subscriptions.models.data.contestliveoverview.update, {data: data});
                        };
                        setTimeout(runOverview3,7000); 
                };

                setInterval(runIntervals, 10000);

        };

        ruckus.models.contestliveoverview.prototype.staticData = function () {
                return {};
        };

        return ruckus.models.contestliveoverview;
});
