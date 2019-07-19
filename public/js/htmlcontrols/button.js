// Author: Scott Gay
define([
        "assets/js/htmlcontrols/base.js",
        "assets/js/libraries/jquery.min.js"
], function (Base) {
        ruckus.htmlcontrols.button = function (parameters) {
                // parameters:
                // - container
                // - buttonText
                // - buttonSize [small, medium, large] default small
                // - buttonColor  [red, green, blue] default blue
                // - hoverEffect  (default true)
                // - onclick

                Base.call(this);
                this.init = function () {
                        this.parameters = parameters;
                        this.parameters.container.addClass('ruckus');

                        if (this.parameters.buttonSize == undefined)
                                this.parameters.buttonSize = 'small';
                        if (this.parameters.buttonColor == undefined)
                                this.parameters.buttonColor = 'blue';
                        if (this.parameters.hoverEffect == undefined)
                                this.parameters.hoverEffect = true;

                        this.load();
                }
                this.init();
        }

        ruckus.htmlcontrols.button.prototype = Object.create(Base.prototype);

        ruckus.htmlcontrols.button.prototype.load = function () {
                var _this = this;
                this.getContainer();

                this.container.addClass('float_left');

                this.button = $('<button>', {'class': 'button ' + this.parameters.buttonSize + ' ' + this.parameters.buttonColor}).appendTo(this.container).html(this.parameters.buttonText);

                if (this.parameters.hoverEffect) {
                        this.button.hover(
                                function () {
                                        $(this).addClass("hover");
                                        $(this).removeClass(_this.parameters.buttonColor);
                                },
                                function () {
                                        $(this).removeClass("hover");
                                        $(this).addClass(_this.parameters.buttonColor);
                                }
                        );
                }

                this.button.bind('click', function (event) {
                        _this.parameters.onclick();
                });
        }

        return ruckus.htmlcontrols.button;

});
