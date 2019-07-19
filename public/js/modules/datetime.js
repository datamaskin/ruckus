define([ 'jquery', 'moment', 'rg_league_def',
        'assets/js/models/timestamp.js'], function ($, moment) {

        ruckus.modules.datetime = function(){ /* constructor */ };

        ruckus.modules.datetime.getServerTimeStamp = function (callback) {
                var model = new ruckus.models.timestamp();

                model.msgBus.subscribe("model.timestamp.servertime", function () {
                        if(callback)
                                callback(model.modelData);
                        else
                                return model.modelData;
                });
                model.fetch();
        };

        ruckus.modules.datetime.diff = function(epoch_time1, epoch_time2, output_format) {
                if(_.isString(epoch_time1)) {
                        epoch_time1 = JSON.parse(epoch_time1);
                }
                if(_.isString(epoch_time2)) {
                        epoch_time2 = JSON.parse(epoch_time2);
                }
                if  (!moment(epoch_time1).isValid() || !moment(epoch_time2).isValid()) {
                        return false;
                }
                var dt1 = moment(epoch_time1);
                var dt2 = moment(epoch_time2);
                return moment(dt2).diff(moment(dt1), output_format);
        };

        ruckus.modules.datetime.subtract = function(dt, count, unit) {
                if(!moment(dt).isValid())
                        return false;

                return moment(dt).subtract(count, unit);
        };

        ruckus.modules.datetime.format = function(dt, pattern){
                return moment(dt).format(pattern);
        };

        ruckus.modules.datetime.isSameDay = function( ) {
                return true;
        };

        ruckus.modules.datetime.formatTimeActual = function (val, pattern, tz) {
                var myDate = new Date(val);
                if (tz)
                        return moment(myDate).tz(tz).format("ddd, h:mma");
                else
                        return moment(myDate).tz("America/New_York").format("ddd, h:mma");
        };

        ruckus.modules.datetime.formatTimePercentage = function (league, unitsRemaining) {
                return Math.floor(((ruckus.definition[league.toLowerCase()].timeUnits.number - unitsRemaining) / ruckus.definition[league.toLowerCase()].timeUnits.number) * 100);
        };

        ruckus.modules.datetime.now = function(){
                return Date.now();
        };

        return ruckus.modules.datetime;
});
