define([ 'jquery',
        'easypiechart',
        'assets/js/modules/datetime.js'], function () {

        ruckus.modules.counters.circle = function(el, options){
                return new EasyPieChart(el, options);
        };

        ruckus.modules.counters.circle.render = function(container, chart_class, timeremaining, options){
                if(!container || !chart_class || !timeremaining) return false;

                var chart_el = container.find("." + chart_class);
                if (chart_el.length != 1) return false;  // no chart found in markup.

                // default options.  Should make this more sophisticated to replace on a property basis.
                if(!options) options = {
                        scaleColor: false,
                        trackColor: 'rgba(255,255,255,0.3)',
                        barColor: '#7cdfa4',
                        lineWidth: 4,
                        lineCap: 'butt',
                        size: 60
                };

                var counterData = this.parseDisplayData(timeremaining); // value | percentage | label


                var counter_text = ruckus.modules.counters.text.longNumeric(timeremaining);
                chart_el.html(counter_text);

//                chart_el.easyPieChart(options);
//                chart_el.data('easyPieChart').enableAnimation().update(counterData[1]);
//
//                // If the UI has the default labels in place, update them.
//                var number_el = container.find("." + chart_class + "_number");
//                if (number_el.length === 1) number_el.html(counterData[0]);
//
//                var label_el = container.find("." + chart_class + "_label");
//                if (label_el.length === 1) label_el.html(counterData[2]);

                return counterData;
        };

        ruckus.modules.counters.circle.update = function(container, chart_class, timeremaining){
                if(!container || !chart_class || !timeremaining) return false;

                var chart_el = container.find("." + chart_class);
                if (chart_el.length != 1) return false;  // no chart found in markup.

//                var chart = chart_el.data('easyPieChart');
//                if (chart === undefined) return false;  // no chart found in markup.

                var counterData = this.parseDisplayData(timeremaining); // value | percentage | label
                var counter_text = ruckus.modules.counters.text.longNumeric(timeremaining);
                chart_el.html(counter_text);

//                chart.update(counterData[1]);
//
//                // If the UI has the default labels in place, update them.
//                var number_el = container.find("." + chart_class + "_number");
//                if (number_el.length === 1) number_el.html(counterData[0]);
//
//                var label_el = container.find("." + chart_class + "_label");
//                if (label_el.length === 1) label_el.html(counterData[2]);

                return counterData;
        };

        ruckus.modules.counters.circle.parseDisplayData = function (total_seconds){
                var days = parseInt( total_seconds / 86400 ) % 365;
                var hours = parseInt( total_seconds / 3600 ) % 24;
                var minutes = (parseInt( total_seconds / 60 ) % 60) + 1;
                var seconds = total_seconds % 60;

                var ret_arr = [];
                if(total_seconds > 0 && total_seconds < 61) {
                        ret_arr[0] = (/*seconds < 10 ? "0" + seconds :*/ seconds);
                        ret_arr[1] = (ret_arr[0] / 60) * 100;
                        ret_arr[2] = 'seconds remaining';
                }
                if(total_seconds > 60 && total_seconds < 3601) {
                        ret_arr[0] = (/*minutes < 10 ? "0" + minutes :*/ minutes);
                        ret_arr[1] = (ret_arr[0] / 60) * 100;
                        ret_arr[2] = 'minutes remaining';
                }
                if(total_seconds > 3600 && total_seconds < 86400)
                {
                        ret_arr[0] = (/*hours < 10 ? "0" + hours :*/ hours);
                        ret_arr[1] = (ret_arr[0] / 24) * 100;
                        ret_arr[2] = 'hours remaining';
                }
                if(total_seconds > 86400) {
                        ret_arr[0] = (/*days < 10 ? "0" + days :*/ days);
                        ret_arr[1] = (ret_arr[0] / 7) * 100;
                        ret_arr[2] = 'days remaining';
                }
                if(total_seconds < 1) {
                        ret_arr[0] = "Kickoff Soon";
                        ret_arr[1] = 0;
                        ret_arr[2] = '';
                }
                return ret_arr;
        };

        return ruckus.modules.counters.circle;
});