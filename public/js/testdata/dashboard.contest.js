define(function () {
        return function (contest) {

                var sampledata = [];
                var number_of_records = 50;
                var max_fpp = 100;
                var max_unitsremaining = 540;


                for (var i = 0; i < number_of_records + 1; i++) {
                        var curr_fpp = ((max_fpp / number_of_records) * i);
                        var curr_unitsremaining = max_unitsremaining - ((max_unitsremaining / number_of_records) * i);
                        var curr_payout = Math.floor((Math.random() * 10000) + 1);
                        var curr_projpayout = Math.floor((Math.random() * 10000) + 1);
                        var curr_position = Math.floor((Math.random() * 5) + 1);

                        var record = {
                                "projectedPayout": curr_projpayout,
                                "fpp": curr_fpp,
                                "unitsRemaining": curr_unitsremaining,
                                "contestState": "active",

                                "contestId": contest.contestId,
                                "multiplier": contest.multiplier,
                                "league": contest.league,
                                "payout": curr_payout,
                                "prizePool": contest.prizePool,
                                "payouts": contest.payouts,
                                "contestType": contest.contestType,
                                "currentEntries": contest.currentEntries,
                                "position": curr_position,
                                "entryFee": contest.entryFee,
                                "timeUntilStart": contest.timeUntilStart,
                                "startTime": contest.startTime,
                                "lineupId": contest.lineupId
                        };

                        sampledata.push(record);
                }

                return sampledata;
        }
});
