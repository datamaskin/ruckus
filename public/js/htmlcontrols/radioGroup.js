// Author: Scott Gay
define([
        "assets/js/htmlcontrols/base.js",
        "assets/js/libraries/jquery.min.js"
], function (Base) {
        ruckus.htmlcontrols.radioGroup = function (parameters) {
                // parameters:
                // - container
                // - items
                // - selected
                // - onchange	

                Base.call(this);
                this.init = function () {
                        this.parameters = parameters;
                        this.parameters.container.addClass('ruckus');

                        this.load();
                }
                this.init();
        }

        ruckus.htmlcontrols.radioGroup.prototype = Object.create(Base.prototype);

        ruckus.htmlcontrols.radioGroup.prototype.load = function () {
                var _this = this;
                this.getContainer();

                this.selected = undefined;
                $.each(this.parameters.items, function (key, value) {
                        var stateClass = 'unchecked';
                        if (_this.parameters.selected != undefined) {
                                if (value.value == _this.parameters.selected.value) {
                                        _this.selected = value;
                                        stateClass = 'checked';
                                }
                        }
                        _this.radio = $('<div>', {'id': 'rdo_' + value.value, 'class': 'rucradio float_left ' + stateClass}).appendTo(_this.container);
                        $('<div>', {'class': 'radiolabel float_left'}).appendTo(_this.container).html(value.name);
                        $('<div>', {'class': 'clear'}).appendTo(_this.container);

                        _this.radio.bind('click', function () {
                                _this.switchSelected(value, true);
                        });
                });
        }

        ruckus.htmlcontrols.radioGroup.prototype.switchSelected = function (value, fireCallback) {
                if (this.selected != undefined) {
                        if (value.value != this.selected.value) {
                                this.selected = value.value;
                                $('.rucradio').removeClass('checked').addClass('unchecked');
                                $('#rdo_' + value.value).removeClass('unchecked').addClass('checked');
                                if (this.parameters.onchange != undefined && fireCallback)
                                        this.parameters.onchange();
                        }
                }
                else {
                        this.selected = value.value;
                        $('#rdo_' + value.value).removeClass('unchecked').addClass('checked');
                        if (this.parameters.onchange != undefined && fireCallback)
                                this.parameters.onchange();
                }
        }

        ruckus.htmlcontrols.radioGroup.prototype.getValue = function () {
                return this.selected;
        }

        ruckus.htmlcontrols.radioGroup.prototype.setValue = function (val) {
                this.switchSelected(val, false);
        }

        return ruckus.htmlcontrols.radioGroup;

});
