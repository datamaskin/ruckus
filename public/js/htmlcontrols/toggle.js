// Author: Scott Gay
define([
        "assets/js/htmlcontrols/base.js",
        "assets/js/libraries/jquery.min.js"
], function (Base) {
        ruckus.htmlcontrols.toggle = function (parameters) {
                // parameters:
                // - container
                // - textOn
                // - textOff
                // - checked

                Base.call(this);
                this.init = function () {
                        this.parameters = parameters;
                        this.parameters.container.addClass('ruckus');

                        if (this.parameters.textOn != undefined && this.parameters.textOff != undefined) {
                                this.on = this.parameters.textOn;
                                this.off = this.parameters.textOff;
                        }
                        else {
                                this.on = 'Yes';
                                this.off = 'No';
                        }

                        this.load();

                        if (this.parameters.checked != undefined) {
                                if (this.parameters.checked == '1') {
                                        this.setValue(1);
                                }
                                else {
                                        this.setValue(0);
                                }
                        }
                }
                this.init();
        }

        ruckus.htmlcontrols.toggle.prototype = Object.create(Base.prototype);

        ruckus.htmlcontrols.toggle.prototype.load = function () {
                var _this = this;
                this.getContainer();

                this.divOuter = $('<div>', {'class': 'toggle outer float_left'}).appendTo(this.container);
                this.divInnerCtrl = $('<div>', {'class': 'inner on'}).appendTo(this.divOuter);
                this.divInnerText = $('<div>', {'class': 'innertext float_right'}).appendTo(this.divOuter).html(this.on);
                $('<div>', {'class': 'clear'}).appendTo(this.container);
                $('<div>', {'style': 'height:5px;'}).appendTo(this.container);
                $('<div>', {'class': 'clear'}).appendTo(this.container);

                var _this = this;
                this.divOuter.bind('click',
                        function (event) {
                                if (_this.divInnerCtrl.hasClass('on')) {
                                        _this.divInnerCtrl.addClass('off');
                                        _this.divInnerCtrl.removeClass('on');
                                        _this.divInnerText.html(_this.off);
                                        _this.divInnerText.removeClass('float_right');
                                        _this.divInnerText.addClass('float_left');
                                }
                                else {
                                        _this.divInnerCtrl.addClass('on');
                                        _this.divInnerCtrl.removeClass('off');
                                        _this.divInnerText.removeClass('float_left');
                                        _this.divInnerText.addClass('float_right');
                                        _this.divInnerText.html(_this.on);
                                }
                        }
                );
        }

        ruckus.htmlcontrols.toggle.prototype.getValue = function () {
                if (this.divInnerCtrl.hasClass('on'))
                        return 1;
                else
                        return 0;
        }

        ruckus.htmlcontrols.toggle.prototype.setValue = function (val) {
                if (val == 1) {
                        this.divInnerCtrl.addClass('on');
                        this.divInnerCtrl.removeClass('off');
                        this.divInnerText.removeClass('float_left');
                        this.divInnerText.addClass('float_right');
                        this.divInnerText.html(this.on);
                }
                else {
                        this.divInnerCtrl.addClass('off');
                        this.divInnerCtrl.removeClass('on');
                        this.divInnerText.removeClass('float_right');
                        this.divInnerText.addClass('float_left');
                        this.divInnerText.html(this.off);
                }
        }

        return ruckus.htmlcontrols.toggle;

});
