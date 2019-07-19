define(['assets/js/modules/datetime.js'], function () {

        ruckus.modules.counters.text = function() {
                /* constructor */
        };

        ruckus.modules.counters.text.shortCounter = function (total_seconds){
                var days = parseInt( total_seconds / 86400 ) % 365;
                var hours = parseInt( total_seconds / 3600 ) % 24;
                var minutes = parseInt( total_seconds / 60 ) % 60;
                var seconds = total_seconds % 60;

                // mm:ss
                if(total_seconds > 0 && total_seconds < 61) return (seconds  < 10 ? "0" + seconds : seconds) + '|s';
                // mm:ss
                if(total_seconds >= 60 && total_seconds < 3601) return (minutes < 10 ? "0" + minutes : minutes) + '|m';
                // hh:mm:ss
                if(total_seconds >= 3600 && total_seconds < 86400) return (hours < 10 ? "0" + hours : hours) + '|h' ;
                // dd:hh:mm:ss
                if(total_seconds >= 86400) return (days < 10 ? "0" + days : days) + '|d';
                return "--";
        };

        ruckus.modules.counters.text.longNumeric = function (total_seconds){
                var days = parseInt( total_seconds / 86400 ) % 365;
                var hours = parseInt( total_seconds / 3600 ) % 24;
                var minutes = parseInt( total_seconds / 60 ) % 60;
                var seconds = total_seconds % 60;

                // mm:ss
                // if(total_seconds >= 0 && total_seconds < 3601) return (minutes < 10 ? "0" + minutes : minutes) + ":" + (seconds  < 10 ? "0" + seconds : seconds);
                // hh:mm:ss
                if(total_seconds >= 0 && total_seconds < 86400) return (hours < 10 ? "0" + hours : hours) + ":" + (minutes < 10 ? "0" + minutes : minutes) + ":" + (seconds  < 10 ? "0" + seconds : seconds);
                // dd:hh:mm:ss
                if(total_seconds >= 86400) return (days < 10 ? "0" + days : days) + ':' + (hours < 10 ? "0" + hours : hours) + ":" + (minutes < 10 ? "0" + minutes : minutes) + ":" + (seconds  < 10 ? "0" + seconds : seconds);
                return "Kickoff Soon";
        };

        return ruckus.modules.counters.text;
});