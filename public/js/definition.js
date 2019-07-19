// Author: Scott Gay
define([], function () {

        ruckus.definition = {
                mlb: {
			players: {
				number: 11
			},
                        timeUnits: {
                                name: "innings",
                                number: 9
                        },
                        sliders: [
                                {
                                        "id": 0,
                                        "name": "Singles",
                                        "min": 0,
                                        "max": 20,
                                        "display": ""
                                },
                                {
                                        "id": 1,
                                        "name": "Doubles",
                                        "min": 0,
                                        "max": 20,
                                        "display": ""
                                },
                                {
                                        "id": 2,
                                        "name": "Triples",
                                        "min": 0,
                                        "max": 20,
                                        "display": ""
                                },
                                {
                                        "id": 3,
                                        "name": "Homeruns",
                                        "min": 0,
                                        "max": 20,
                                        "display": ""
                                },
                                {
                                        "id": 4,
                                        "name": "Matchup",
                                        "min": 0,
                                        "max": 20,
                                        "display": ""
                                },
                                {
                                        "id": 5,
                                        "name": "History",
                                        "min": 0,
                                        "max": 20,
                                        "display": [
                                                1,
                                                3,
                                                5,
                                                10,
                                                15
                                        ]
                                }
                        ]

                },
                nfl: {
			players: {
				number: 9
			},
            slots: ["FX1", "FX2", "QB", "RB1", "RB2", "WR1", "WR2", "TE", "DEF"],
            positions: ["FX", "QB", "RB", "WR", "TE", "DEF"],
            default_depth: 50000,
                        timeUnits: {
                                name: "minutes",
                                number: 60
                        },
                        sliders: [
                                {
                                        "id": 0,
                                        "name": "Passing Yards",
                                        "min": 0,
                                        "max": 300,
                                        "display": ""
                                },
                                {
                                        "id": 1,
                                        "name": "Rushing Yards",
                                        "min": 0,
                                        "max": 300,
                                        "display": ""
                                },
                                {
                                        "id": 2,
                                        "name": "Receiving Yards",
                                        "min": 0,
                                        "max": 300,
                                        "display": ""
                                },
                                {
                                        "id": 3,
                                        "name": "Touchdowns",
                                        "min": 0,
                                        "max": 300,
                                        "display": ""
                                },
                                {
                                        "id": 4,
                                        "name": "Matchup",
                                        "min": 0,
                                        "max": 300,
                                        "display": ""
                                },
                                {
                                        "id": 5,
                                        "name": "Performance",
                                        "min": 0,
                                        "max": 300,
                                        "display": [
                                                1,
                                                3,
                                                5,
                                                10,
                                                15,
                                                20,
                                                25
                                        ]
                                }
                        ]
                }
        };
});
