define([
       "rg_subpage_base"
], function (Base) {
        // PARAMETERS
                ruckus.subpagecontrols.configuration = function (parameters) {
                        Base.call(this);
                        this.init = function () {
                                var _this = this;
                                this.parameters = parameters;
                                this.slots = [];
                                this.max_allowed_cost = 5000000; //salary cap
                                // set parameter defaults
                        };
                        this.init();
                };
                ruckus.subpagecontrols.configuration.prototype = Object.create(Base.prototype);

                ruckus.subpagecontrols.configuration.prototype.setSlots = function(newSlots) {
                    this.slots = newSlots;
                };

                ruckus.subpagecontrols.configuration.prototype.getSlots = function() {
                    return this.slots;
                };

                ruckus.subpagecontrols.configuration.prototype.getMaxAllowedCost = function() {
                    return this.max_allowed_cost;
                };

                ruckus.subpagecontrols.configuration.prototype.getTotalSalary = function(roster) {
                    var total = 0;
                    for(var i = 0; i < this.slots.length; i++) {
                        var slot = roster[this.slots[i]];
                        if(slot) {
                            total += slot.salary;
                        }
                    }
                    return total;
                };

                ruckus.subpagecontrols.configuration.prototype.getTotalValue = function(roster) {
                    var total = 0;
                    for(var i = 0; i < this.slots.length; i++) {
                        var slot = roster[this.slots[i]];
                        if(slot) {
                            total += slot.value;
                        }
                    }
                    return total;
                };

                ruckus.subpagecontrols.configuration.prototype.addAthlete = function(athlete, newSlot, roster, lineup) {
                    for(var i = 0; i < lineup.length; i++) {
                        // if we have this athlete in the lineup and they are locked, skip
                        if (lineup[i].id && lineup[i].id == athlete.id && lineup[i].preexisting == true) {
                            return;
                        }
                        // if we have someone in this slot and the slot is locked, skip
                        if(lineup[i].slot && lineup[i].slot == newSlot && lineup[i].preexisting == true) {
                            return;
                        }
                    }
                    for(var i = 0; i < this.slots.length; i++) {
                        var slot = roster[this.slots[i]];
                        if(slot) {
                            if (slot.id == athlete.id) {
                                // if we have this athlete in any other slot, skips
                                return;
                            }
                        }
                    }

                    roster[newSlot] = athlete;
                };

                ruckus.subpagecontrols.configuration.prototype.getRoster = function(lineup, roster) {
                    for(var j = 0; j < lineup.length; j++) {
                        var athlete = roster[this.slots[j]];
                        if(!athlete.lock) {
                            athlete.lock = false;
                        }
                        lineup[j].athlete = athlete;
                    }
                    return lineup
                };

                // this depends on us only ever having max 2 of the same slot
                ruckus.subpagecontrols.configuration.prototype.sortRoster = function(roster) {
                    //first sort FX positions into their real ones
                    for(var i = 0; i < this.slots.length; i++) {
                        var fxSlot = roster[this.slots[i]];
                        var fxPos = this.slots[i];
                        if(fxSlot.ppos != 'TE' && fxPos.indexOf('FX') != -1){
                            var pos1 = fxSlot.ppos + '1';
                            var pos2 = fxSlot.ppos + '2';
                            var slot1 = roster[pos1];
                            var slot2 = roster[pos2];
                            if(fxSlot.salary > slot1.salary){
                                roster[pos1] = fxSlot;
                                roster[fxPos] = slot1;
                                fxSlot = slot1;
                            }
                            if(fxSlot.salary > slot2.salary){
                                roster[pos2] = fxSlot;
                                roster[fxPos] = slot2;
                            }
                        } else if (fxSlot.ppos == 'TE' && fxPos.indexOf('FX') != -1) {
                            var pos = fxSlot.ppos;
                            var slot = roster[pos];
                            if(fxSlot.salary > slot.salary){
                                roster[pos] = fxSlot;
                                roster[fxPos] = slot;
                            }
                        }
                    }
                    // then sort by the slots
                    for(var i = 0; i < this.slots.length; i++) {
                        var slot1 = roster[this.slots[i]];
                        var pos1 = this.slots[i];
                        var sal1 = slot1.salary;
                        if(pos1.indexOf('1') != -1){
                            var pos2 = pos1.replace(/[0-9]/g, '') + '2';
                            var slot2 = roster[pos2];
                            var sal2 = roster[pos2].salary;
                            if(sal2 > sal1){
                                roster[pos1] = slot2;
                                roster[pos2] = slot1;
                            }
                        }
                    }
                    return roster;
                };

                ruckus.subpagecontrols.configuration.prototype.printRoster = function(msg, roster) {
                    console.log("\n\n========== " + msg + " ==========");
                    var cost = 0;
                    var value = 0;
                    for(var i = 0; i < this.slots.length; i++) {
                        var athlete = roster[this.slots[i]];
                        if(athlete){
                            cost += athlete.salary;
                            value += athlete.value;
                            console.log("[" + athlete.ppos + "] " + athlete.id + " " + this.slots[i] + " (" + athlete.value + "," + athlete.salary +  ")");
                        }
                    }
                    console.log("Total Cost = " + cost + " - Total Value = " + value);
                };
        return ruckus.subpagecontrols.configuration;
});