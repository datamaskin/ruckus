// Author: Scott Gay
define([
        "assets/js/pagecontrols/base.js",
        "assets/js/libraries/jquery.min.js",
        "assets/js/libraries/jquery.flot.min.js"
], function (Base) {
        ruckus.pagecontrols.sectiontwo = function (parameters) {
                Base.call(this);
                this.init = function () {
                        var _this = this;
                        this.parameters = parameters;
                        this.parameters.container.addClass('pagecontrolhighlight');

                };
                this.init();
        };

        ruckus.pagecontrols.sectiontwo.prototype = Object.create(Base.prototype);
        ruckus.pagecontrols.sectiontwo.prototype.load = function () {
                this.getContainer();
                this.container.addClass('ruckus-pc-sectiontwo');

                var rowContainer = $("<div>", {'class': 'col-xs-24'}).appendTo(this.container);
                var cell1Container = $("<div>", {'class': 'col-xs-6'}).appendTo(rowContainer);
                var cell2Container = $("<div>", {'class': 'col-xs-6'}).appendTo(rowContainer);
                var cell3Container = $("<div>", {'class': 'col-xs-6'}).appendTo(rowContainer);
                var cell4Container = $("<div>", {'class': 'col-xs-6'}).appendTo(rowContainer);
                var graphContainer = $("<div>", {'class': 'col-xs-24', 'style': 'height:200px;border-color:#efefef;border-width:1px;border-style:solid;'}).appendTo(cell1Container);
                var graphContainer2 = $("<div>", {'class': 'col-xs-24', 'style': 'height:200px;border-color:#efefef;border-width:1px;border-style:solid;'}).appendTo(cell2Container);
                var graphContainer3 = $("<div>", {'class': 'col-xs-24', 'style': 'height:200px;border-color:#efefef;border-width:1px;border-style:solid;'}).appendTo(cell3Container);
                var graphContainer4 = $("<div>", {'class': 'col-xs-24', 'style': 'height:200px;border-color:#efefef;border-width:1px;border-style:solid;'}).appendTo(cell4Container);

                var block1 = $('<div>', {'style': 'position:absolute;opacity:.3;width:10px;background-color:#000000;margin-left:245px;'}).appendTo(cell1Container);
                var block2 = $('<div>', {'style': 'position:absolute;opacity:.3;width:10px;background-color:#000000;margin-left:245px;'}).appendTo(cell2Container);
                var block3 = $('<div>', {'style': 'position:absolute;opacity:.3;width:10px;background-color:#000000;margin-left:245px;'}).appendTo(cell3Container);
                var block4 = $('<div>', {'style': 'position:absolute;opacity:.3;width:10px;background-color:#000000;margin-left:245px;'}).appendTo(cell4Container);

                var line1 = $('<div>', {'style': 'position:absolute;width:1px;background-color:#000000;'}).appendTo(cell1Container);
                var line2 = $('<div>', {'style': 'position:absolute;width:1px;background-color:#000000;'}).appendTo(cell2Container);
                var line3 = $('<div>', {'style': 'position:absolute;width:1px;background-color:#000000;'}).appendTo(cell3Container);
                var line4 = $('<div>', {'style': 'position:absolute;width:1px;background-color:#000000;'}).appendTo(cell4Container);

                var flotOptions = {
                        series: {
                                lines: {
                                        fill: true,
                                        fillColor: { colors: ["#fefefe", "#cdcdcd"] },
                                        lineWidth: 1
                                },
                        },
                        colors: [
                                "#aaaaaa",
                                "#69F4E9"
                        ],
                        grid: {
                                minBorderMargin: 0,
                                borderWidth: 0,
//				backgroundColor: { colors: ["#efefef", "#aaaaaa"] }
                        },
                        xaxis: {
                                show: false,
                                max: 4
                        },
                        yaxis: {
                                show: false,
                                max: 100
                        }
                };

                var setBlockHeight = function (blk, pos, totPos, paidPos) {
                        var height = 200;
                        var top = 1;
                        var last = paidPos;
                        var mid = Math.floor(paidPos / 2);
                        if (pos >= top && pos <= mid) {
                                var step = .25 / (mid - top + 1);
                                var addon = (mid - pos + 1) * step;
                                blk.css('height', Math.floor(height * (.5 + addon)) + "px");
                        } else if (pos > mid && pos <= last) {
                                var step = .35 / (last - mid + 1);
                                var addon = (last - pos + 1) * step;
                                blk.css('height', Math.floor(height * (.15 + addon)) + "px");
                        } else {
                                blk.css('height', Math.floor(height * .15) + "px");
                        }
                };

                var setLine = function (lne, height, left) {
                        lne.css('height', height + 'px');
                        lne.css('marginLeft', left + 2 + 'px');
                        lne.css('marginTop', (200 - height) + "px");
                };

                var resetScale = function (data, blk, first, last, pts) {
                        if (pts > last) {
                                // in the money
                                var lastToPts = pts - last;
                                var firstToLast = first - last;
                                var per = lastToPts / firstToLast;
                                var height = blk.height() * per; // pixels from top
                                var endpoint = height + (200 - blk.height());
                                data[1][1] = endpoint / 2;
                        } else {
                                // out of the money
                        }
                };

                var autocorrectBlock = function (data, blk, first, last, pts) {
                        var lastToPts = pts - last;
                        var firstToLast = first - last;
                        var per = lastToPts / firstToLast;
                        var height = blk.height() * per; // pixels from top
                        var endpoint = height + (200 - blk.height());
                        if (pts > last) {
                                // in the money
                                if (200 - blk.height() > (pts * 2)) {
                                        blk.css('height', 200 - ((pts * 2) - 10));
                                }
                        } else {
                                // out of the money
                                if (200 - blk.height() < (pts * 2)) {
                                        blk.css('height', 200 - ((pts * 2) + 10));
                                }
                        }
                };

                var d1 = [];
                d1.push([3, 55]);
                d1.push([4, 100]);
                var d1A = [];
                d1A.push([1, 5]);
                d1A.push([2, 25]);
                d1A.push([3, 55]);
                setBlockHeight(block1, 1, 100, 10);
                setLine(line1, 110, (graphContainer.width() / 4) * 3);
                var flotOptions1 = $.extend(true, {}, flotOptions);
//		resetScale(d1, block1, 100, 65, 100);
                autocorrectBlock(d1, block1, 100, 65, 100);

                var d2 = [];
                d2.push([3, 68]);
                d2.push([4, 90]);
                var d2A = [];
                d2A.push([1, 5]);
                d2A.push([2, 45]);
                d2A.push([3, 68]);
                setBlockHeight(block2, 4, 100, 10);
                setLine(line2, 136, (graphContainer2.width() / 4) * 3);
                var flotOptions2 = $.extend(true, {}, flotOptions);
//		resetScale(d2, block2, 100, 65, 95);
                autocorrectBlock(d2, block2, 100, 65, 90);

                var d3 = [];
                d3.push([3, 55]);
                d3.push([4, 70]);
                var d3A = [];
                d3A.push([1, 5]);
                d3A.push([2, 20]);
                d3A.push([3, 55]);
                setBlockHeight(block3, 9, 100, 10);
                setLine(line3, 110, (graphContainer3.width() / 4) * 3);
                var flotOptions3 = $.extend(true, {}, flotOptions);
//		resetScale(d3, block3, 100, 65, 70);
                autocorrectBlock(d3, block3, 100, 65, 70);

                var d4 = [];
                d4.push([3, 20]);
                d4.push([4, 30]);
                var d4A = [];
                d4A.push([1, 5]);
                d4A.push([2, 13]);
                d4A.push([3, 20]);
                setBlockHeight(block4, 24, 100, 10);
                setLine(line4, 40, (graphContainer4.width() / 4) * 3);
                var flotOptions4 = $.extend(true, {}, flotOptions);
//		resetScale(d4, block4, 100, 65, 30);
                autocorrectBlock(d4, block4, 100, 65, 30);

                var plot = $.plot(graphContainer, [d1, d1A], flotOptions1);
                var plot2 = $.plot(graphContainer2, [d2, d2A], flotOptions2);
                var plot3 = $.plot(graphContainer3, [d3, d3A], flotOptions3);
                var plot4 = $.plot(graphContainer4, [d4, d4A], flotOptions4);
                /*
                 window.onresize = function(event) {
                 $.plot(graphContainer, [ d1 ], flotOptions);
                 $.plot(graphContainer2, [ d2 ], flotOptions);
                 $.plot(graphContainer3, [ d3 ], flotOptions);
                 $.plot(graphContainer4, [ d4 ], flotOptions);
                 };
                 */
                /*
                 setTimeout(function(){
                 var axes = plot.getAxes();
                 axes.yaxis.options.max = 300;

                 // Redraw
                 plot.setupGrid();
                 plot.draw();
                 }, 5000);
                 */
        };

        ruckus.pagecontrols.sectiontwo.prototype.unload = function () {
                this.destroyControl();
        };

        return ruckus.pagecontrols.sectiontwo;
});


