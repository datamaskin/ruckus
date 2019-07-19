define(function () {
        return function(entryId) {

                var sampledata = [];
                var number_of_records = 20;
                var max_fpp = 100;
                var max_unitsremaining = 60;


                for (var i = 0; i < number_of_records+1; i++) {
                        //var curr_fpp = ((max_fpp / number_of_records) * i);

                        var curr_fpp = Math.floor((Math.random() * 100) + 1);

                        var curr_unitsremaining = max_unitsremaining - ((max_unitsremaining / number_of_records) * i);
                        var record = {"unitsRemaining": curr_unitsremaining, "fpp": curr_fpp, "id": entryId };

                        sampledata.push(record);
                }

                return sampledata;
        }
});
