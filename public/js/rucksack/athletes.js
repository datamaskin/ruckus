define([
       "rg_subpage_base"
], function (Base) {
        // PARAMETERS
                ruckus.subpagecontrols.athletes = function (parameters) {
                        Base.call(this);
                        this.init = function () {
                            var _this = this;
                            this.allAthletes = new Array();
                            this.athlete_list = {};
                            this.position_list;
                            this.parameters = parameters;
                                // set parameter defaults
                        };
                        this.init();
                };
                ruckus.subpagecontrols.athletes.prototype = Object.create(Base.prototype);

                ruckus.subpagecontrols.athletes.prototype.setPositions = function(positions) {
                    var _this = this;
                    _this.position_list = positions;
                    for (var i = 0; i < _this.position_list.length; i++) {
                        _this.athlete_list[_this.position_list[i]] = new Array()
                    }
                };

                ruckus.subpagecontrols.athletes.prototype.addAthlete = function(athlete) {
                    var _this = this;
                    _this.allAthletes.push(athlete);
                    _this.athlete_list[athlete.ppos].push(athlete);
                };

                ruckus.subpagecontrols.athletes.prototype.getAthletes = function() {
                    var _this = this;
                    return _this.allAthletes;
                };

                ruckus.subpagecontrols.athletes.prototype.getPosition = function(position) {
                    var _this = this;
                    return _this.athlete_list[position];
                };

                ruckus.subpagecontrols.athletes.prototype.randomAthlete = function(position, lineup) {
                    var _this = this;
                    position = position.replace(/[0-9]/g, ''); // find the actual pos from lineup one ... RB1 -> RB

                    if(position == 'FX') {
                        var MIN = 0;
                        var MAX = 2;
                        var possibles = ["RB", "WR", "TE"];
                        var rand = Math.floor(Math.random()*(MAX-MIN+1)+MIN);
                        position = possibles[rand];
                    }
                    var randomSpot = Math.floor(Math.random() * (this.athlete_list[position].length - 1));
                    var athlete = _this.athlete_list[position][randomSpot];
                    for(var i = 0; i < lineup.length; i++) {
                        if(position == lineup[i].pos && athlete.athleteSportEventInfoId == lineup[i].id) {
                            return _this.randomAthlete(position, lineup);
                        }
                    }
                    return athlete;
                };
        return ruckus.subpagecontrols.athlete;
});