// Author: Scott Gay
define([
        "assets/js/subpagecontrols/base.js",
        "assets/js/libraries/d3/d3.v3.min.js",
        "assets/js/libraries/jquery.min.js",
//	"assets/js/libraries/underscore-min.js"
        "assets/js/libraries/dust-core.min.js"
], function (Base, d3) {
        ruckus.subpagecontrols.dashboardcontestgraph2 = function (parameters) {
                Base.call(this);
                this.init = function () {
                        this.parameters = parameters;
                };
                this.init();
        };

        ruckus.subpagecontrols.dashboardcontestgraph2.prototype = Object.create(Base.prototype);
        ruckus.subpagecontrols.dashboardcontestgraph2.prototype.load = function () {
                var _this = this;
                this.getContainer();
                this.container.addClass('ruckus-spc-dashboardcontestgraph2');
//console.log('*****');
//console.log(this.parameters);

                this.require_template('dashboardcontestgraph2-tpl');
                dust.render('dusttemplates/dashboardcontestgraph2-tpl', {}, function (err, out) {
                        _this.container.html(out);
			_this.addScrollBars();
//			if (_this.parameters.contestdetailranks.length > 21){
//				_this.container.css('marginTop', '50px');
//				_this.container.html('Large Field Bubble Graph Coming Soon');
//			} else {
				_this.width = $("#dhg2_container").width();
				_this.height = 270;
				_this.ranklist = [];

				_this.graphranks = [];
				var allSame = true;
				var val = undefined;
				$.each(_this.parameters.contestdetailranks, function(k,v){
					_this.graphranks.push({
						entryId : v.entryId, 
						fpp : v.fpp, 
						timePercentage : v.timePercentage,
						user : v.user,
						pos : v.pos, 
						payout : v.prize,
						unitsRemaining : v.unitsRemaining
					});
					if (val != undefined){
						if (val != v.fpp)
							allSame = false;
					} else {
						val = v.fpp;
					}
				});
	//			_this.consolelog(_this.graphranks);
/*	
				_this.graphranks = [
					{ entryId: 1, fpp: 10, timePercentage: 0, user: 'joe', pos: 1, payout: '$10', unitsRemaining: 90},
					{ entryId: 2, fpp: 5, timePercentage: undefined, user: 'mike', pos: 2, payout: '$5', unitsRemaining: 80},
					{ entryId: 3, fpp: 3, timePercentage: undefined, user: 'steve', pos: 3, payout: '$3', unitsRemaining: 50},
					{ entryId: 4, fpp: 127, timePercentage: 78, user: 'al', pos: 4, payout: '$2', unitsRemaining: 45},
					{ entryId: 5, fpp: 126, timePercentage: 56, user: 'tim', pos: 5, payout: '$1', unitsRemaining: 45},
					{ entryId: 6, fpp: 120, timePercentage: 34, user: 'chris', pos: 6, payout: undefined, unitsRemaining: 45},
					{ entryId: 7, fpp: 110, timePercentage: 45, user: 'jack', pos: 7, payout: undefined, unitsRemaining: 45},
					{ entryId: 8, fpp: 100, timePercentage: 20, user: 'greg', pos: 8, payout: undefined, unitsRemaining: 45},
					{ entryId: 9, fpp: 95, timePercentage: 20, user: 'greg', pos: 9, payout: undefined, unitsRemaining: 45},
					{ entryId: 10, fpp: 90, timePercentage: 26, user: 'greg', pos: 10, payout: undefined, unitsRemaining: 45},
					{ entryId: 11, fpp: 85, timePercentage: 20, user: 'greg', pos: 11, payout: undefined, unitsRemaining: 45},
					{ entryId: 12, fpp: 80, timePercentage: 26, user: 'greg', pos: 12, payout: undefined, unitsRemaining: 45},
					{ entryId: 13, fpp: 75, timePercentage: 26, user: 'greg', pos: 13, payout: undefined, unitsRemaining: 45},
					{ entryId: 14, fpp: 70, timePercentage: 20, user: 'greg', pos: 14, payout: undefined, unitsRemaining: 45},
					{ entryId: 15, fpp: 65, timePercentage: 20, user: 'greg', pos: 15, payout: undefined, unitsRemaining: 45},
					{ entryId: 16, fpp: 60, timePercentage: 10, user: 'greg', pos: 16, payout: undefined, unitsRemaining: 45},
					{ entryId: 17, fpp: 55, timePercentage: 6, user: 'greg', pos: 17, payout: undefined, unitsRemaining: 45},
					{ entryId: 18, fpp: 50, timePercentage: 76, user: 'greg', pos: 18, payout: undefined, unitsRemaining: 45},
					{ entryId: 19, fpp: 40, timePercentage: 74, user: 'greg', pos: 19, payout: undefined, unitsRemaining: 45},
					{ entryId: 20, fpp: 39, timePercentage: 49, user: 'greg', pos: 20, payout: undefined, unitsRemaining: 45},
					{ entryId: 21, fpp: 35, timePercentage: 1, user: 'greg', pos: 21, payout: undefined, unitsRemaining: 45}
				];
				allSame = false;
*/

				// set time percentage (FIXME - include this above to avoid this loop.  putting it here to test static data for now.)
				$.each(_this.graphranks, function(k,v){
					v.timePercentage = _this.formatTimePercentageTeam(_this.parameters.contest.league, v.unitsRemaining);
				});
				_this.spacing = _this.width / _this.graphranks.length;

				// reverse order by position
				_this.graphranks = _this.sortMe('fpp', _this.graphranks);

				// use timePercentage to find radius
				var radMax = (_this.spacing / 2) - 1;
				if (radMax > (_this.height/2))
					radMax = Math.floor(_this.height/2)-5;
				var percentLookup = [
					{ min: 0, max: 25, radius: Math.floor(radMax)},
					{ min: 26, max: 50, radius: Math.floor(radMax * .8)},
					{ min: 51, max: 75, radius: Math.floor(radMax * .6)},
					{ min: 76, max: 100, radius: Math.floor(radMax * .4)}
				];

				// find top player multiplier
				var topFPP = _this.graphranks[_this.graphranks.length - 1].fpp;

				var dataset = [];
				var datasetUser = [];
				var datasetFpp = [];
				var datasetPos = [];
				var datasetUnitsRemaining = [];
				$.each(_this.graphranks, function (k, v) {

					var radius = undefined;
					var x = undefined;
					var y = undefined;
					var color = undefined;

					$.each(percentLookup, function (k1, v1) {
						if (v1.min <= v.timePercentage && v1.max >= v.timePercentage)
							radius = v1.radius;
					});

					x = Math.floor(_this.spacing * (k + .5));
					var ratio = 0;
					if (allSame){
						y = _this.height/2; 
					} else {
						y = Math.floor(_this.height - ((_this.height - Math.ceil(radMax)) * (v.fpp / topFPP)));
						if ((y+Math.floor(radMax)) > _this.height){
							y = _this.height-Math.ceil(radMax);
						}
					}

					if (v.payout != undefined)
						color = '#A8F3ED';
					else
						color = '#666666';

					dataset.push({
						id: v.entryId,
						radius: radius,
						x: x,
						y: y,
						color: color//,
					});
					datasetUser.push({id: v.entryId, val: v.user, type: 'user', yoffset: 15});
					datasetFpp.push({id: v.entryId, val: v.fpp + ' points', type: 'fpp', yoffset: 30});
					datasetPos.push({id: v.entryId, val: _this.formatPlace(v.pos) + ' place', type: 'pos', yoffset: 45});
					datasetUnitsRemaining.push({id: v.entryId, val: v.unitsRemaining + ' min left', type: 'unitsRemaining', yoffset: 60});
				});

				var bodySelection = d3.select("#dhg2_container");
				var svgSelection = bodySelection.append("svg")
					.attr("width", _this.width)
					.attr("height", _this.height)
					.attr("id", "svg");


				d3.select('svg').selectAll('circle')
					.data(dataset)
					.enter()
					.append('circle')
					.on('click', function (d) {
					})
					.on('mouseenter', function (d) {
						d3.select("#dhg2_rect").style("display", "inline");
						d3.select("#dhg2_text_user_" + d.id).style("display", "inline");
						d3.select("#dhg2_text_fpp_" + d.id).style("display", "inline");
						d3.select("#dhg2_text_pos_" + d.id).style("display", "inline");
						d3.select("#dhg2_text_unitsRemaining_" + d.id).style("display", "inline");
					})
					.on('mouseleave', function (d) {
						d3.select("#dhg2_rect").style("display", "none");
						d3.select("#dhg2_text_user_" + d.id).style("display", "none");
						d3.select("#dhg2_text_fpp_" + d.id).style("display", "none");
						d3.select("#dhg2_text_pos_" + d.id).style("display", "none");
						d3.select("#dhg2_text_unitsRemaining_" + d.id).style("display", "none");
					});

				// style circles
				d3.selectAll('circle')
					.attr('r', function (d) {
						return d.radius;
					})
					.attr('cx', function (d) {
						return d.x;
					})
					.attr('cy', function (d) {
						return d.y;
					})
					.style("cursor", "pointer")
					.style('fill', function (d) {
						return d.color;
					});

				svgSelection.append("rect")
					.attr("x", 5)
					.attr("y", 5)
					.attr("width", 70)
					.attr("height", 60)
					.attr("id", "dhg2_rect")
					.attr("display", "none")
					.attr("fill", "#666666");
								var arr = datasetUser;
				arr = $.merge(arr, datasetFpp);
				arr = $.merge(arr, datasetPos);
				arr = $.merge(arr, datasetUnitsRemaining);
				_this.displayText(arr);

//				d3.select("#dhg2_container").style("background-color", "#333333");
				$('#dshc_graph2').hide();

//			}
                });
        };

        ruckus.subpagecontrols.dashboardcontestgraph2.prototype.displayText = function (dataset) {
                d3.select('svg').selectAll('text')
                        .data(dataset)
                        .enter()
                        .append('text')
                        .attr('id', function (d) {
                                return 'dhg2_text_' + d.type + '_' + d.id;
                        })
                        .attr("x", function (d) {
                                return 10;
                        })
                        .attr("y", function (d) {
                                return d.yoffset;
                        })
                        .text(function (d) {
                                return d.val;
                        })
                        .attr("font-family", "arial")
//				.attr("text-anchor", "middle")
                        .attr("font-size", "8pt")
                        .attr("display", "none")
                        .attr("fill", "#A8F3ED");


        };

        ruckus.subpagecontrols.dashboardcontestgraph2.prototype.sortMe = function (key, arr) {
                var _this = this;

                var dir1 = undefined;
                var dir2 = undefined;
//                if (_this.sortDetails[key] == 'asc'){
//                        _this.sortDetails[key] = 'desc';
//                        dir1 = 1;
//                        dir2 = -1;
//                } else {
//                        _this.sortDetails[key] = 'asc';
                dir1 = -1;
                dir2 = 1;
//                }
                var compare = function (a, b) {
                        if (a[key] < b[key])
                                return dir1;
                        if (a[key] > b[key])
                                return dir2;
                        return 0;
                };

                arr.sort(compare);

                for (var x = 0; x < arr.length; x++) {
                        arr[x].fpp = x + 1;
                }

                return arr;
        };

        ruckus.subpagecontrols.dashboardcontestgraph2.prototype.unload = function () {
                this.destroyControl();
        };

        return ruckus.subpagecontrols.dashboardcontestgraph2;
});
	

