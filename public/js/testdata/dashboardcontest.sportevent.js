define(function () {
        return function (sportEventId) {

                var sampledata = [];
                var number_of_records = 20;
                var max_fpp = 100;
                var max_unitsremaining = 60;

                for (var i = 0; i < number_of_records + 1; i++) {

                        var indicator = Math.floor((Math.random() * 3));
                        var curr_fpp = ((max_fpp / number_of_records) * i);
                        var curr_unitsremaining = max_unitsremaining - ((max_unitsremaining / number_of_records) * i);
                        var record = {"homeId": "345", "homeTeam": "Sea", "awayId": "362", "awayTeam": "Min", "homeScore": 7, "awayScore": 3, "id": sportEventId};

                        sampledata.push(record);
                }
                return sampledata;
        }
});