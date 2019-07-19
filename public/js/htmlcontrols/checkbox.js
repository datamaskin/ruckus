// Author: Scott Gay
define([
        "assets/js/htmlcontrols/base.js",
        "assets/js/libraries/jquery.min.js"
], function (Base) {
        ruckus.htmlcontrols.checkbox = function (parameters) {
                // parameters:
                // - container
                // - label
                // - checked (default false)
                // - onchange

                Base.call(this);
                this.init = function () {
                        this.parameters = parameters;
                        this.parameters.container.addClass('ruckus');

                        if (this.parameters.label == undefined)
                                this.parameters.label = '';
                        if (this.parameters.checked == undefined)
                                this.parameters.checked = false;

                        this.load();
                }
                this.init();
        }

        ruckus.htmlcontrols.checkbox.prototype = Object.create(Base.prototype);

        ruckus.htmlcontrols.checkbox.prototype.load = function () {
                var _this = this;
                this.getContainer();

                this.checked = this.parameters.checked;
                this.cbox = $('<div>', {'class': 'cbox float_left'}).appendTo(this.container);
                $('<div>', {'class': 'checkboxlabel float_left'}).appendTo(this.container).html(this.parameters.label);
                $('<div>', {'class': 'clear'}).appendTo(this.container);

                if (this.checked)
                        this.cbox.addClass('checked');
                else
                        this.cbox.addClass('unchecked');

                this.cbox.bind('click', function () {
                        if (_this.checked) {
                                _this.checked = false;
                                _this.cbox.removeClass('checked');
                                _this.cbox.addClass('unchecked');
                        }
                        else {
                                _this.checked = true;
                                _this.cbox.removeClass('unchecked');
                                _this.cbox.addClass('checked');
                        }
                        if (_this.parameters.onchange != undefined)
                                _this.parameters.onchange();
                });
        }

        ruckus.htmlcontrols.checkbox.prototype.getValue = function () {
                return this.checked;
        }

        ruckus.htmlcontrols.checkbox.prototype.setValue = function (val) {
                this.checked = val;
                if (this.checked) {
                        this.checked = true;
                        this.cbox.removeClass('unchecked');
                        this.cbox.addClass('checked');
                }
                else {
                        this.checked = false;
                        this.cbox.removeClass('checked');
                        this.cbox.addClass('unchecked');
                }
        }

        return ruckus.htmlcontrols.checkbox;

});
