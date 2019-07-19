// Author: Scott Gay
define([
        "assets/js/htmlcontrols/base.js",
        "assets/js/libraries/jquery.min.js"
], function (Base) {
        ruckus.htmlcontrols.tooltip = function (parameters) {
                // parameters:
                // - container
                // - width
                // - xOffset
                // - yOffset
                // - text

                Base.call(this);
                this.init = function () {
                        this.parameters = parameters;
                        this.parameters.container.addClass('ruckus');

                        if (this.parameters.width == undefined)
                                this.parameters.width = 300;
                        if (this.parameters.xOffset == undefined)
                                this.parameters.xOffset = 0;
                        if (this.parameters.yOffset == undefined)
                                this.parameters.yOffset = 0;

                        this.load();
                }
                this.init();
        }

        ruckus.htmlcontrols.tooltip.prototype = Object.create(Base.prototype);

        ruckus.htmlcontrols.tooltip.prototype.load = function () {
                var _this = this;
                //               this.getContainer();

                this.tooltip = $('<div>', {
                        'class': 'ructooltip',
                        'style': 'width:' + this.parameters.width + 'px;margin-top:' + this.parameters.yOffset + 'px;margin-left:' + this.parameters.xOffset + 'px;'
                }).appendTo(this.parameters.container).html(this.parameters.text);

                this.hide();
                this.parameters.container.bind('mouseenter', function () {
                        _this.show();
                });
                this.parameters.container.bind('mouseleave', function () {
                        _this.hide();
                });
        }

        ruckus.htmlcontrols.tooltip.prototype.hide = function () {
                this.tooltip.hide();
        }

        ruckus.htmlcontrols.tooltip.prototype.show = function () {
                this.tooltip.show();
        }

        return ruckus.htmlcontrols.tooltip;

});
