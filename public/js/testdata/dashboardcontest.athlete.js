define(function () {
        return function (athlete) {

                var sampledata = [];
                var number_of_records = 20;
                var max_fpp = 100;
                var max_unitsremaining = 60;

                for (var i = 0; i < number_of_records + 1; i++) {

                        var indicator = Math.floor((Math.random() * 3));
                        var curr_fpp = i; //((max_fpp / number_of_records) * i);
                        var curr_unitsremaining = max_unitsremaining - ((max_unitsremaining / number_of_records) * i);

                        var record = { "indicator": indicator, "firstName": athlete.firstName, "lastName": athlete.lastName, "stats": [
                                {"amount": 0, "fpp": curr_fpp, "name": "Receiving TD", "abbr": "TDs", "id":"NFL_6"},
                                {"amount": 0, "fpp": curr_fpp, "name": "Rushing TD", "abbr": "TDs", "id":"NFL_5"},
                                {"amount": 0, "fpp": curr_fpp, "name": "Kick Return TD", "abbr": "TDs", "id":"NFL_12"},
                                {"amount": 0, "fpp": curr_fpp, "name": "Passing TD", "abbr": "PTDs", "id":"NFL_4"},
                                {"amount": 0, "fpp": curr_fpp, "name": "Two Point Conversion", "abbr": "2PT", "id":"NFL_17"},
                                {"amount": 0, "fpp": curr_fpp, "name": "Passing Yards", "abbr": "PYDs", "id":"NFL_1"},
                                {"amount": 0, "fpp": curr_fpp, "name": "Reception", "abbr": "REC", "id":"NFL_8"},
                                {"amount": 0, "fpp": curr_fpp, "name": "Receiving Yards", "abbr": "REYDs", "id":"NFL_3"},
                                {"amount": 0, "fpp": curr_fpp, "name": "Rushing Yards", "abbr": "RUYDs", "id":"NFL_2"},
                                {"amount": 0, "fpp": curr_fpp, "name": "Lost Fumble", "abbr": "FUM", "id":"NFL_9"},
                                {"amount": 0, "fpp": curr_fpp, "name": "Interception", "abbr": "INT", "id":"NFL_10"}
                        ], "fpp": curr_fpp, "unitsRemaining": curr_unitsremaining, "timeline": [
                                {"fpChange": "+" + Math.floor(curr_fpp / (i + 1)), "description": athlete.lastName + " did something cool!", "athleteSportEventInfoId": athlete.athleteSportEventInfoId, "timestamp": 1408038877070}
                        ], "athleteSportEventInfoId": athlete.athleteSportEventInfoId};

                        sampledata.push(record);
                }
                return sampledata;
        }
});
