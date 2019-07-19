// Author: Scott Gay
// flot.dashes https://code.google.com/p/flot/issues/attachmentText?id=61&aid=-4351874625254130968&name=jquery.flot.dashes.js&token=30636c55acd3aa96d9850f8d77953384
// flot.smoothlines https://github.com/MichaelZinsmaier/CurvedLines/blob/master/curvedLines.js
define([
        "assets/js/subpagecontrols/base.js",
        "dust",
        "assets/js/models/graph1.js",
        "assets/js/libraries/jquery.flot.min.js"
], function (Base) {
        ruckus.subpagecontrols.dashboardcontestgraph1 = function (parameters) {
                Base.call(this);
                this.init = function () {
                        this.parameters = parameters;
                        this.height = 270;
                        this.multiplier = 3;
                        this.lineups = {
                                me: undefined,
                                opponent: undefined
                        };
                };
                this.init();
        };

        ruckus.subpagecontrols.dashboardcontestgraph1.prototype = Object.create(Base.prototype);
        ruckus.subpagecontrols.dashboardcontestgraph1.prototype.load = function () {
                var _this = this;
                this.getContainer();
                this.container.addClass('ruckus-spc-dashboardcontestgraph1');

                var sub = this.msgBus.subscribe('controls.dhcr.selectlineup', function (dataSL) {
                        if (dataSL.lineup.isMe) { // render single
                                _this.lineups.me = dataSL;
                                _this.lineups.opponent = undefined;
                                _this.getData();
                        } else {
                                _this.lineups.opponent = dataSL;
                                _this.getData();
                        }
                });
		this.subscriptions.push(sub);
        };

        ruckus.subpagecontrols.dashboardcontestgraph1.prototype.getData = function () {
                var _this = this;
                _this.graph1Model = new ruckus.models.graph1({});
                _this.models.push(_this.graph1Model);
                var sub2 = _this.msgBus.subscribe("model.graph1.all", function (data) {
                        sub2.unsubscribe();
                        _this.log({type: 'api', data: _this.graph1Model.modelData, msg: 'GRAPH 1 DATA'});
                        _this.graph1Model.modelData.formattedstartTime = _this.formatTimeActual(_this.graph1Model.modelData.startTime);
                        _this.graph1Model.modelData.formattedsalaryCap = _this.formatMoney(_this.graph1Model.modelData.salaryCap);
                        _this.graph1Model.modelData.formattedentryFee = _this.formatMoney(_this.graph1Model.modelData.entryFee);
                        _this.graph1Model.modelData.formattedcurrentWinnings = _this.formatMoney(_this.graph1Model.modelData.currentWinnings);
                        _this.graph1Model.modelData.formattedprojectedWinnings = _this.formatMoney(_this.graph1Model.modelData.projectedWinnings);

                        _this.require_template('dashboardcontestgraph1-tpl');
                        dust.render('dusttemplates/dashboardcontestgraph1-tpl', _this.graph1Model.modelData, function (err, out) {
                                _this.container.html(out);
				_this.addScrollBars();

                                _this.rendercell(_this.graph1Model.modelData);

                        });
                });
                if (_this.lineups.opponent != undefined)
                        _this.graph1Model.fetch({contest: _this.parameters.contestId, ids: _this.lineups.me.lineup.lineupId + '/' + _this.lineups.opponent.lineup.lineupId});
                else
                        _this.graph1Model.fetch({contest: _this.parameters.contestId, ids: _this.lineups.me.lineup.lineupId});
        };

        ruckus.subpagecontrols.dashboardcontestgraph1.prototype.rendercell = function (contest) {
                var _this = this;
                _this.rendergraph(contest);
        };

        ruckus.subpagecontrols.dashboardcontestgraph1.prototype.rendergraph = function (contest) {
                var flotOptions = {
                        series: {
                                curvedLines: {
                                        active: true,
                                        apply: true,
                                        fit: true
                                },
                                lines: {
         //                               fill: true,
        //                                fillColor: { colors: ["#333333", "#777777"] },
                                        lineWidth: 1
                                },
                                shadowSize: 0
                        },
                        colors: [
                                "#69F4E9"
                        ],
                        grid: {
                                minBorderMargin: 0,
                                borderWidth: 0
                        },
                        xaxis: {
                                show: false
                        },
                        yaxis: {
                                show: false,
				min: -1,
                                max: 100
                        }
                };
                var flotOptionsProj = {
                        series: {
                                curvedLines: {
                                        active: true,
                                        apply: true,
                                        fit: true
                                },
                                lines: {
                                        lineWidth: 1
                                },
                                dashes: {
                                        show: true,
                                        lineWidth: 1,
                                        dashLength: [1, 2]
                                },
                                shadowSize: 0
                        },
                        colors: [
                                "#aaaaaa"
                        ],
                        grid: {
                                minBorderMargin: 0,
                                borderWidth: 0
                        },
                        xaxis: {
                                show: false
                        },
                        yaxis: {
                                show: false,
                                min: -1,
                                max: 100
                        }
                };
                var totalUnits = ruckus.definition[contest.league.toLowerCase()].timeUnits.number*ruckus.definition[contest.league.toLowerCase()].players.number;
                var wid = $('#dhg1_graph_' + contest.id).width();
                var minPerPixel = wid / totalUnits;
                var currentWidth = Math.floor(minPerPixel * (totalUnits - contest.unitsRemaining));
                var projectedWidth = Math.floor(minPerPixel * contest.unitsRemaining);
                var gcCurrent = $('<div>', {'style': 'float:left;height:' + this.height + 'px;width:' + currentWidth + 'px;'}).appendTo($('#dhg1_graph_' + contest.id));
                var gcProjected = $('<div>', {'style': 'float:left;height:' + this.height + 'px;width:' + projectedWidth + 'px;'}).appendTo($('#dhg1_graph_' + contest.id));
                $('<div>', {'style': 'clear:both;'}).appendTo($('#dhg1_graph_' + contest.id));

                var totalSegments = contest.currentPerformanceData.length + contest.projectedPerformanceData.length - 1;
                this.setLine($('#dhg1_line_' + contest.id), contest.currentPoints * this.multiplier, ($('#dhg1_graph_' + contest.id).width() / totalSegments) * contest.currentPerformanceData.length);
                this.setDot($('#dhg1_dot_' + contest.id), $('#dhg1_line_current_' + contest.id), contest.currentPoints * this.multiplier, ($('#dhg1_graph_' + contest.id).width() / totalSegments) * contest.currentPerformanceData.length);
                this.setBlockHeight($('#dhg1_block_' + contest.id), $('#dhg1_block_dollar_' + contest.id), contest.projectedPosition, contest.numEntries, contest.numPaid);
                this.autocorrectBlock($('#dhg1_block_' + contest.id), contest.projectedFirstMoneyPoints, contest.projectedLastMoneyPoints, contest.projectedPoints);
		if (currentWidth > 0)
	                var cPlot = $.plot(gcCurrent, [contest.currentPerformanceData], flotOptions);
		if (projectedWidth > 0)
	                var pPlot = $.plot(gcProjected, [contest.projectedPerformanceData], flotOptionsProj);

        };

        ruckus.subpagecontrols.dashboardcontestgraph1.prototype.setDot = function (dot, lnec, height, left) {
                dot.css('height', height + 'px');
                dot.css('marginLeft', (left + 25) + 'px');
                dot.css('marginTop', (this.height - height - 2) + "px");

                lnec.css('marginLeft', (left + 17) + 'px');
                lnec.css('marginTop', (this.height - (height / 2) - 10) + "px");
        };

        ruckus.subpagecontrols.dashboardcontestgraph1.prototype.setLine = function (lne, height, left) {
                lne.css('height', height + 'px');
                lne.css('marginLeft', (left + 27) + 'px');
                lne.css('marginTop', (this.height - height) + "px");
        };

        ruckus.subpagecontrols.dashboardcontestgraph1.prototype.setBlockHeight = function (blk, blkd, pos, totPos, paidPos) {
                var height = this.height;
                var top = 1;
                var last = paidPos;
                var mid = Math.floor(paidPos / this.multiplier);
		var step = undefined;
		var addon = undefined;
                if (pos >= top && pos <= mid) {
                        step = .25 / (mid - top + 1);
                        addon = (mid - pos + 1) * step;
                        blk.css('height', Math.floor(height * (.5 + addon)) + "px");
                        blkd.css('marginTop', Math.floor(height * (.5 + addon)) - 10 + "px");
                } else if (pos > mid && pos <= last) {
                        step = .35 / (last - mid + 1);
                        addon = (last - pos + 1) * step;
                        blk.css('height', Math.floor(height * (.15 + addon)) + "px");
                        blkd.css('marginTop', Math.floor(height * (.15 + addon)) - 10 + "px");
                } else {
                        blk.css('height', Math.floor(height * .15) + "px");
                        blkd.css('marginTop', Math.floor(height * .15) - 10 + "px");
                }
        };

        ruckus.subpagecontrols.dashboardcontestgraph1.prototype.autocorrectBlock = function (blk, first, last, pts) {
                var lastToPts = pts - last;
                var firstToLast = first - last;
                var per = lastToPts / firstToLast;
                var height = blk.height() * per; // pixels from top
                var endpoint = height + (this.height - blk.height());
                if (pts > last) {
                        // in the money
                        if (this.height - blk.height() > (pts * this.multiplier)) {
                                blk.css('height', this.height - ((pts * this.multiplier) - 10));
                        }
                } else {
                        // out of the money
                        if (this.height - blk.height() < (pts * this.multiplier)) {
                                blk.css('height', this.height - ((pts * this.multiplier) + 10));
                        }
                }
        };

        ruckus.subpagecontrols.dashboardcontestgraph1.prototype.unload = function () {
                this.destroyControl();
        };

        /*
         * jQuery.flot.dashes
         *
         * options = {
         *   series: {
         *     dashes: {
         *
         *       // show
         *       // default: false
         *       // Whether to show dashes for the series.
         *       show: <boolean>,
         *
         *       // lineWidth
         *       // default: 2
         *       // The width of the dashed line in pixels.
         *       lineWidth: <number>,
         *
         *       // dashLength
         *       // default: 10
         *       // Controls the length of the individual dashes and the amount of
         *       // space between them.
         *       // If this is a number, the dashes and spaces will have that length.
         *       // If this is an array, it is read as [ dashLength, spaceLength ]
         *       dashLength: <number> or <array[2]>
         *     }
         *   }
         * }
         */
        (function ($) {

                function init(plot) {

                        plot.hooks.processDatapoints.push(function (plot, series, datapoints) {

                                if (!series.dashes.show) return;

                                plot.hooks.draw.push(function (plot, ctx) {

                                        var plotOffset = plot.getPlotOffset(),
                                                axisx = series.xaxis,
                                                axisy = series.yaxis;

                                        function plotDashes(xoffset, yoffset) {

                                                var points = datapoints.points,
                                                        ps = datapoints.pointsize,
                                                        prevx = null,
                                                        prevy = null,
                                                        dashRemainder = 0,
                                                        dashOn = true,
                                                        dashOnLength,
                                                        dashOffLength;

                                                if (series.dashes.dashLength[0]) {
                                                        dashOnLength = series.dashes.dashLength[0];
                                                        if (series.dashes.dashLength[1]) {
                                                                dashOffLength = series.dashes.dashLength[1];
                                                        } else {
                                                                dashOffLength = dashOnLength;
                                                        }
                                                } else {
                                                        dashOffLength = dashOnLength = series.dashes.dashLength;
                                                }

                                                ctx.beginPath();

                                                for (var i = ps; i < points.length; i += ps) {

                                                        var x1 = points[i - ps],
                                                                y1 = points[i - ps + 1],
                                                                x2 = points[i],
                                                                y2 = points[i + 1];

                                                        if (x1 == null || x2 == null) continue;

                                                        // clip with ymin
                                                        if (y1 <= y2 && y1 < axisy.min) {
                                                                if (y2 < axisy.min) continue;   // line segment is outside
                                                                // compute new intersection point
                                                                x1 = (axisy.min - y1) / (y2 - y1) * (x2 - x1) + x1;
                                                                y1 = axisy.min;
                                                        } else if (y2 <= y1 && y2 < axisy.min) {
                                                                if (y1 < axisy.min) continue;
                                                                x2 = (axisy.min - y1) / (y2 - y1) * (x2 - x1) + x1;
                                                                y2 = axisy.min;
                                                        }

                                                        // clip with ymax
                                                        if (y1 >= y2 && y1 > axisy.max) {
                                                                if (y2 > axisy.max) continue;
                                                                x1 = (axisy.max - y1) / (y2 - y1) * (x2 - x1) + x1;
                                                                y1 = axisy.max;
                                                        } else if (y2 >= y1 && y2 > axisy.max) {
                                                                if (y1 > axisy.max) continue;
                                                                x2 = (axisy.max - y1) / (y2 - y1) * (x2 - x1) + x1;
                                                                y2 = axisy.max;
                                                        }

                                                        // clip with xmin
                                                        if (x1 <= x2 && x1 < axisx.min) {
                                                                if (x2 < axisx.min) continue;
                                                                y1 = (axisx.min - x1) / (x2 - x1) * (y2 - y1) + y1;
                                                                x1 = axisx.min;
                                                        } else if (x2 <= x1 && x2 < axisx.min) {
                                                                if (x1 < axisx.min) continue;
                                                                y2 = (axisx.min - x1) / (x2 - x1) * (y2 - y1) + y1;
                                                                x2 = axisx.min;
                                                        }

                                                        // clip with xmax
                                                        if (x1 >= x2 && x1 > axisx.max) {
                                                                if (x2 > axisx.max) continue;
                                                                y1 = (axisx.max - x1) / (x2 - x1) * (y2 - y1) + y1;
                                                                x1 = axisx.max;
                                                        } else if (x2 >= x1 && x2 > axisx.max) {
                                                                if (x1 > axisx.max) continue;
                                                                y2 = (axisx.max - x1) / (x2 - x1) * (y2 - y1) + y1;
                                                                x2 = axisx.max;
                                                        }

                                                        if (x1 != prevx || y1 != prevy) {
                                                                ctx.moveTo(axisx.p2c(x1) + xoffset, axisy.p2c(y1) + yoffset);
                                                        }

                                                        var ax1 = axisx.p2c(x1) + xoffset,
                                                                ay1 = axisy.p2c(y1) + yoffset,
                                                                ax2 = axisx.p2c(x2) + xoffset,
                                                                ay2 = axisy.p2c(y2) + yoffset,
                                                                dashOffset;

                                                        function lineSegmentOffset(segmentLength) {

                                                                var c = Math.sqrt(Math.pow(ax2 - ax1, 2) + Math.pow(ay2 - ay1, 2));

                                                                if (c <= segmentLength) {
                                                                        return {
                                                                                deltaX: ax2 - ax1,
                                                                                deltaY: ay2 - ay1,
                                                                                distance: c,
                                                                                remainder: segmentLength - c
                                                                        }
                                                                } else {
                                                                        var xsign = ax2 > ax1 ? 1 : -1,
                                                                                ysign = ay2 > ay1 ? 1 : -1;
                                                                        return {
                                                                                deltaX: xsign * Math.sqrt(Math.pow(segmentLength, 2) / (1 + Math.pow((ay2 - ay1) / (ax2 - ax1), 2))),
                                                                                deltaY: ysign * Math.sqrt(Math.pow(segmentLength, 2) - Math.pow(segmentLength, 2) / (1 + Math.pow((ay2 - ay1) / (ax2 - ax1), 2))),
                                                                                distance: segmentLength,
                                                                                remainder: 0
                                                                        };
                                                                }
                                                        }

                                                        //-end lineSegmentOffset

                                                        do {

                                                                dashOffset = lineSegmentOffset(
                                                                                dashRemainder > 0 ? dashRemainder :
                                                                                dashOn ? dashOnLength : dashOffLength);

                                                                if (dashOffset.deltaX != 0 || dashOffset.deltaY != 0) {
                                                                        if (dashOn) {
                                                                                ctx.lineTo(ax1 + dashOffset.deltaX, ay1 + dashOffset.deltaY);
                                                                        } else {
                                                                                ctx.moveTo(ax1 + dashOffset.deltaX, ay1 + dashOffset.deltaY);
                                                                        }
                                                                }

                                                                dashOn = !dashOn;
                                                                dashRemainder = dashOffset.remainder;
                                                                ax1 += dashOffset.deltaX;
                                                                ay1 += dashOffset.deltaY;

                                                        } while (dashOffset.distance > 0);

                                                        prevx = x2;
                                                        prevy = y2;
                                                }

                                                ctx.stroke();
                                        }

                                        //-end plotDashes

                                        ctx.save();
                                        ctx.translate(plotOffset.left, plotOffset.top);
                                        ctx.lineJoin = 'round';

                                        var lw = series.dashes.lineWidth,
                                                sw = series.shadowSize;

                                        // FIXME: consider another form of shadow when filling is turned on
                                        if (lw > 0 && sw > 0) {
                                                // draw shadow as a thick and thin line with transparency
                                                ctx.lineWidth = sw;
                                                ctx.strokeStyle = "rgba(0,0,0,0.1)";
                                                // position shadow at angle from the mid of line
                                                var angle = Math.PI / 18;
                                                plotDashes(Math.sin(angle) * (lw / 2 + sw / 2), Math.cos(angle) * (lw / 2 + sw / 2));
                                                ctx.lineWidth = sw / 2;
                                                plotDashes(Math.sin(angle) * (lw / 2 + sw / 4), Math.cos(angle) * (lw / 2 + sw / 4));
                                        }

                                        ctx.lineWidth = lw;
                                        ctx.strokeStyle = series.color;

                                        if (lw > 0) {
                                                plotDashes(0, 0);
                                        }

                                        ctx.restore();

                                });
                                //-end draw hook

                        });
                        //-end processDatapoints hook

                }

                //-end init

                $.plot.plugins.push({
                        init: init,
                        options: {
                                series: {
                                        dashes: {
                                                show: false,
                                                lineWidth: 2,
                                                dashLength: 10
                                        }
                                }
                        },
                        name: 'dashes',
                        version: '0.1'
                });

        })(jQuery);


        /* The MIT License

         Copyright (c) 2011 by Michael Zinsmaier and nergal.dev
         Copyright (c) 2012 by Thomas Ritou

         Permission is hereby granted, free of charge, to any person obtaining a copy
         of this software and associated documentation files (the "Software"), to deal
         in the Software without restriction, including without limitation the rights
         to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
         copies of the Software, and to permit persons to whom the Software is
         furnished to do so, subject to the following conditions:

         The above copyright notice and this permission notice shall be included in
         all copies or substantial portions of the Software.

         THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
         IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
         FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
         AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
         LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
         OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
         THE SOFTWARE.
         */

        /*

         ____________________________________________________

         what it is:
         ____________________________________________________

         curvedLines is a plugin for flot, that tries to display lines in a smoother way.
         The plugin is based on nergal.dev's work https://code.google.com/p/flot/issues/detail?id=226
         and further extended with a mode that forces the min/max points of the curves to be on the
         points. Both modes are achieved through adding of more data points
         => 1) with large data sets you may get trouble
         => 2) if you want to display the points too, you have to plot them as 2nd data series over the lines

         && 3) consecutive x data points are not allowed to have the same value

         This is version 0.5 of curvedLines so it will probably not work in every case. However
         the basic form of use descirbed next works (:

         Feel free to further improve the code

         ____________________________________________________

         how to use it:
         ____________________________________________________

         var d1 = [[5,5],[7,3],[9,12]];

         var options = { series: { curvedLines: {  active: true }}};

         $.plot($("#placeholder"), [{data = d1, lines: { show: true}, curvedLines: {apply: true}}], options);

         _____________________________________________________

         options:
         _____________________________________________________

         active:           bool true => plugin can be used
         apply:            bool true => series will be drawn as curved line
         fit:              bool true => forces the max,mins of the curve to be on the datapoints
         curvePointFactor  int  defines how many "virtual" points are used per "real" data point to
         emulate the curvedLines (points total = real points * curvePointFactor)
         fitPointDist:     int  defines the x axis distance of the additional two points that are used
         to enforce the min max condition.

         + line options (since v0.5 curved lines use flots line implementation for drawing
         => line options like fill, show ... are supported out of the box)

         */

        /*
         *  v0.1   initial commit
         *  v0.15  negative values should work now (outcommented a negative -> 0 hook hope it does no harm)
         *  v0.2   added fill option (thanks to monemihir) and multi axis support (thanks to soewono effendi)
         *  v0.3   improved saddle handling and added basic handling of Dates
         *  v0.4   rewritten fill option (thomas ritou) mostly from original flot code (now fill between points rather than to graph bottom), corrected fill Opacity bug
         *  v0.5   rewritten instead of implementing a own draw function CurvedLines is now based on the processDatapoints flot hook (credits go to thomas ritou).
         * 		   This change breakes existing code however CurvedLines are now just many tiny straight lines to flot and therefore all flot lines options (like gradient fill,
         * 	       shadow) are now supported out of the box
         *  v0.6   flot 0.8 compatibility and some bug fixes
         */

        (function ($) {

                var options = {
                        series: {
                                curvedLines: {
                                        active: false,
                                        apply: false,
                                        fit: false,
                                        curvePointFactor: 20,
                                        fitPointDist: undefined
                                }
                        }
                };

                function init(plot) {

                        plot.hooks.processOptions.push(processOptions);

                        //if the plugin is active register processDatapoints method
                        function processOptions(plot, options) {
                                if (options.series.curvedLines.active) {
                                        plot.hooks.processDatapoints.unshift(processDatapoints);
                                }
                        }

                        //only if the plugin is active
                        function processDatapoints(plot, series, datapoints) {
                                var nrPoints = datapoints.points.length / datapoints.pointsize;
                                var EPSILON = 0.5; //pretty large epsilon but save

                                if (series.curvedLines.apply == true && series.originSeries === undefined && nrPoints > (1 + EPSILON)) {
                                        if (series.lines.fill) {

                                                var pointsTop = calculateCurvePoints(datapoints, series.curvedLines, 1)
                                                        , pointsBottom = calculateCurvePoints(datapoints, series.curvedLines, 2); //flot makes sure for us that we've got a second y point if fill is true !

                                                //Merge top and bottom curve
                                                datapoints.pointsize = 3;
                                                datapoints.points = [];
                                                var j = 0;
                                                var k = 0;
                                                var i = 0;
                                                var ps = 2;
                                                while (i < pointsTop.length || j < pointsBottom.length) {
                                                        if (pointsTop[i] == pointsBottom[j]) {
                                                                datapoints.points[k] = pointsTop[i];
                                                                datapoints.points[k + 1] = pointsTop[i + 1];
                                                                datapoints.points[k + 2] = pointsBottom[j + 1];
                                                                j += ps;
                                                                i += ps;

                                                        } else if (pointsTop[i] < pointsBottom[j]) {
                                                                datapoints.points[k] = pointsTop[i];
                                                                datapoints.points[k + 1] = pointsTop[i + 1];
                                                                datapoints.points[k + 2] = k > 0 ? datapoints.points[k - 1] : null;
                                                                i += ps;
                                                        } else {
                                                                datapoints.points[k] = pointsBottom[j];
                                                                datapoints.points[k + 1] = k > 1 ? datapoints.points[k - 2] : null;
                                                                datapoints.points[k + 2] = pointsBottom[j + 1];
                                                                j += ps;
                                                        }
                                                        k += 3;
                                                }
                                        } else if (series.lines.lineWidth > 0) {
                                                datapoints.points = calculateCurvePoints(datapoints, series.curvedLines, 1);
                                                datapoints.pointsize = 2;
                                        }
                                }
                        }

                        //no real idea whats going on here code mainly from https://code.google.com/p/flot/issues/detail?id=226
                        //if fit option is selected additional datapoints get inserted before the curve calculations in nergal.dev s code.
                        function calculateCurvePoints(datapoints, curvedLinesOptions, yPos) {

                                var points = datapoints.points, ps = datapoints.pointsize;
                                var num = curvedLinesOptions.curvePointFactor * (points.length / ps);

                                var xdata = new Array;
                                var ydata = new Array;

                                var curX = -1;
                                var curY = -1;
                                var j = 0;

                                if (curvedLinesOptions.fit) {
                                        //insert a point before and after the "real" data point to force the line
                                        //to have a max,min at the data point.

                                        var fpDist;
                                        if (typeof curvedLinesOptions.fitPointDist == 'undefined') {
                                                //estimate it
                                                var minX = points[0];
                                                var maxX = points[points.length - ps];
                                                fpDist = (maxX - minX) / (500 * 100); //x range / (estimated pixel length of placeholder * factor)
                                        } else {
                                                //use user defined value
                                                fpDist = curvedLinesOptions.fitPointDist;
                                        }

                                        for (var i = 0; i < points.length; i += ps) {

                                                var frontX;
                                                var backX;
                                                curX = i;
                                                curY = i + yPos;

                                                //add point X s
                                                frontX = points[curX] - fpDist;
                                                backX = points[curX] + fpDist;

                                                var factor = 2;
                                                while (frontX == points[curX] || backX == points[curX]) {
                                                        //inside the ulp
                                                        frontX = points[curX] - (fpDist * factor);
                                                        backX = points[curX] + (fpDist * factor);
                                                        factor++;
                                                }

                                                //add curve points
                                                xdata[j] = frontX;
                                                ydata[j] = points[curY];
                                                j++;

                                                xdata[j] = points[curX];
                                                ydata[j] = points[curY];
                                                j++;

                                                xdata[j] = backX;
                                                ydata[j] = points[curY];
                                                j++;
                                        }
                                } else {
                                        //just use the datapoints
                                        for (var i = 0; i < points.length; i += ps) {
                                                curX = i;
                                                curY = i + yPos;

                                                xdata[j] = points[curX];
                                                ydata[j] = points[curY];
                                                j++;
                                        }
                                }

                                var n = xdata.length;

                                var y2 = new Array();
                                var delta = new Array();
                                y2[0] = 0;
                                y2[n - 1] = 0;
                                delta[0] = 0;

                                for (var i = 1; i < n - 1; ++i) {
                                        var d = (xdata[i + 1] - xdata[i - 1]);
                                        if (d == 0) {
                                                //point before current point and after current point need some space in between
                                                return [];
                                        }

                                        var s = (xdata[i] - xdata[i - 1]) / d;
                                        var p = s * y2[i - 1] + 2;
                                        y2[i] = (s - 1) / p;
                                        delta[i] = (ydata[i + 1] - ydata[i]) / (xdata[i + 1] - xdata[i]) - (ydata[i] - ydata[i - 1]) / (xdata[i] - xdata[i - 1]);
                                        delta[i] = (6 * delta[i] / (xdata[i + 1] - xdata[i - 1]) - s * delta[i - 1]) / p;
                                }

                                for (var j = n - 2; j >= 0; --j) {
                                        y2[j] = y2[j] * y2[j + 1] + delta[j];
                                }

                                //   xmax  - xmin  / #points
                                var step = (xdata[n - 1] - xdata[0]) / (num - 1);

                                var xnew = new Array;
                                var ynew = new Array;
                                var result = new Array;

                                xnew[0] = xdata[0];
                                ynew[0] = ydata[0];

                                result.push(xnew[0]);
                                result.push(ynew[0]);

                                for (j = 1; j < num; ++j) {
                                        //new x point (sampling point for the created curve)
                                        xnew[j] = xnew[0] + j * step;

                                        var max = n - 1;
                                        var min = 0;

                                        while (max - min > 1) {
                                                var k = Math.round((max + min) / 2);
                                                if (xdata[k] > xnew[j]) {
                                                        max = k;
                                                } else {
                                                        min = k;
                                                }
                                        }

                                        //found point one to the left and one to the right of generated new point
                                        var h = (xdata[max] - xdata[min]);

                                        if (h == 0) {
                                                //similar to above two points from original x data need some space between them
                                                return [];
                                        }

                                        var a = (xdata[max] - xnew[j]) / h;
                                        var b = (xnew[j] - xdata[min]) / h;

                                        ynew[j] = a * ydata[min] + b * ydata[max] + ((a * a * a - a) * y2[min] + (b * b * b - b) * y2[max]) * (h * h) / 6;

                                        result.push(xnew[j]);
                                        result.push(ynew[j]);
                                }

                                return result;
                        }

                }//end init

                $.plot.plugins.push({
                        init: init,
                        options: options,
                        name: 'curvedLines',
                        version: '0.5'
                });

        })(jQuery);

        return ruckus.subpagecontrols.dashboardcontestgraph1;
});
	

