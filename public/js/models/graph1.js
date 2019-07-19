// Author: Scott Gay
define([
//	"assets/js/base.js",
        "assets/js/models/basews.js",
        "assets/js/libraries/jquery.min.js",
        "assets/js/libraries/jquery.atmosphere.js"
], function (Base) {
        // PARAMETERS

        ruckus.models.graph1 = function (parameters) {
                Base.call(this);
                this.init = function () {
                        this.parameters = parameters;
                        // set parameter defaults
                        this.modelData = undefined;
                };
                this.init();
        };

        ruckus.models.graph1.prototype = Object.create(Base.prototype);

        ruckus.models.graph1.prototype.fetch = function (fParams) {
                var _this = this;

                var sub = _this.msgBus.subscribe("socket.CONTESTLIVEPROJECTIONGRAPH_ALL", function (data) {
                        sub.unsubscribe();
//			_this.modelData = {contests:data};
                        _this.modelData = data;
                        // for static data
//			_this.modelData = _this.staticData();
                        // attach any bindings here (knockout)
                        _this.msgBus.publish("model.graph1.all", {contests: _this.modelData});
                });
                this.subscriptions.push(sub);
                var sub2 = _this.msgBus.subscribe("socket.CONTESTLIVEPROJECTIONGRAPH_UPDATE", function (data) {

                });
                this.subscriptions.push(sub2);
                this.openChannel('/ws/projectiongraph/' + fParams.contest + '/' + fParams.ids);

        };

        ruckus.models.graph1.prototype.staticData = function () {
		// beginning
		return JSON.parse('{"id":0,"league":"MLB","entryFee":200,"currentEntries":2,"numPaid":1,"type":"H2H","duplicateEntries":1,"startTime":1406675100000,"unitsRemaining":99,"salaryCap":6000000,"projectedFirstMoneyPoints":100,"projectedLastMoneyPoints":80,"currentPoints":0.00,"projectedPoints":95,"currentPosition":1,"projectedPosition":1,"currentWinnings":380,"projectedWinnings":0,"currentPerformanceData":[],"projectedPerformanceData":[[0,1.12],[1,1.12],[2,2.24],[3,2.24],[4,3.36],[5,3.36],[6,4.48],[7,4.48],[8,5.60],[9,5.60],[10,6.72],[11,6.72],[12,7.84],[13,7.84],[14,8.96],[15,8.96],[16,10.08],[17,10.08],[18,10.08],[19,10.08]]}');

		// 2/3rds
//		return JSON.parse('{"id":0,"league":"MLB","entryFee":10000,"currentEntries":2,"numPaid":1,"type":"H2H","duplicateEntries":1,"startTime":1406563800000,"unitsRemaining":18,"salaryCap":6000000,"projectedFirstMoneyPoints":100,"projectedLastMoneyPoints":80,"currentPoints":11.00,"projectedPoints":95,"currentPosition":2,"projectedPosition":1,"currentWinnings":0,"projectedWinnings":0,"currentPerformanceData":[[0,0],[1,0],[2,0],[3,0],[4,0],[5,0],[6,6.00],[7,6.00],[8,6.00],[9,6.00],[10,6.00],[11,6.00],[12,11.00],[13,11.00]],"projectedPerformanceData":[[14,11.00],[15,11.00],[16,10.50],[17,10.50],[18,10.00],[19,10.00]]}');
         };

        return ruckus.models.graph1;
});
