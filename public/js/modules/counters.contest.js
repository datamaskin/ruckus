/**
 * Created by dbaxter on 7/28/14.
 */
define(['assets/js/modules/counters.text.js'], function () {

        ruckus.modules.counters.contest = function () {
                /* constructor */
        };

        ruckus.modules.counters.contest.getContestTextCounter = function(startTime, remainingTime){
                counter_val = ruckus.modules.datetime.formatTimeActual(startTime, 'ddd, h:mma');

                if(remainingTime < 86400){
                        var counter_val;
                        if(remainingTime < 3600){
                                counter_val = ruckus.modules.counters.text.longNumeric(remainingTime);
                        }
                        if(remainingTime > 3600 && ruckus.modules.datetime.isSameDay) {
                                counter_val = ruckus.modules.datetime.formatTimeActual(startTime, 'h:mma');
                        }
                }

                return counter_val;
        };
});