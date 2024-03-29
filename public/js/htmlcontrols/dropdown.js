// Author: Scott Gay
define([
        "assets/js/htmlcontrols/base.js",
        "assets/js/libraries/jquery.min.js"
], function (Base) {
        ruckus.htmlcontrols.dropdown = function (parameters) {
                // parameters:
                // - container
                // - data [{'name' : '', 'value' : '', 'image' : ''}]
                // - width (default: 300)
                // - onchange

                Base.call(this);
                this.init = function () {
                        this.parameters = parameters;
                        this.parameters.container.addClass('ruckus');

                        if (this.parameters.width == undefined)
                                this.parameters.width = 300;

                        this.load();
                }
                this.init();
        }

        ruckus.htmlcontrols.dropdown.prototype = Object.create(Base.prototype);

        ruckus.htmlcontrols.dropdown.prototype.load = function () {
                var _this = this;
                this.getContainer();
                this.ddlBody = $('<div>', {'class': 'ddlBody', 'style': 'width:' + this.parameters.width + 'px;'}).appendTo(this.container);
                this.ddlInput = $('<div>', {'class': 'ddlInput float_left', 'contentEditable': 'true', 'style': 'width:' + (this.parameters.width - 35) + 'px;'}).appendTo(this.ddlBody);
                this.ddlSelected = $('<div>', {'class': 'float_left'}).appendTo(this.ddlBody).hide();
                this.ddlArrow = $('<div>', {'class': 'ddlArrow float_right'}).appendTo(this.ddlBody);
                this.ddlRemove = $('<div>', {'class': 'ddlRemove float_right'}).appendTo(this.ddlBody).hide();
                $('<div>', {'class': 'clear'}).appendTo(this.ddlBody);

                if (this.parameters.placeholder != undefined)
                        this.placeholder = $('<div>', {'class': 'placeholderdd'}).appendTo(this.container).html(this.parameters.placeholder);

                this.ddlDropdown = $('<div>', {'class': 'ddlDropdown', 'style': 'width:' + (this.parameters.width - 35) + 'px;'}).appendTo(this.ddlBody).hide();

                this.ddlBody.bind('click', function (event) {
                        event.stopPropagation();
                });

                this.ddlInput.bind('keydown', function (evt) { // this blocks enter from actually recording in the input area on keydown.  must use keyup on the actual event so that values exist in the input area for processing
                        if (!evt)
                                evt = window.event;
                        var keyCode = evt.keyCode;
                        if (evt.charCode && keyCode == 0)
                                keyCode = evt.charCode;
                        if (keyCode == 13)
                                evt.preventDefault();
                });
                this.ddlInput.bind('keyup', function (evt) {
                        var arrSelected = $('.ddlSelectedItem');
                        if (!evt)
                                evt = window.event;
                        var keyCode = evt.keyCode;
                        if (evt.charCode && keyCode == 0)
                                keyCode = evt.charCode;
                        switch (keyCode) {
                                case 13 : //enter
                                        evt.preventDefault();
                                        if (arrSelected.length != 0) {
                                                var liData = undefined;
                                                $.each(_this.parameters.data, function (k, v) {
                                                        if (v.value == arrSelected.eq(0).attr('id'))
                                                                liData = v;
                                                });
                                                var li = $('<div>', {'id': liData.value, 'class': 'ddlListItem', 'style': 'width:' + (_this.parameters.width - 45) + 'px;'});
                                                var image = $('<div>', {'class': 'float_left ddlImage'}).appendTo(li);
                                                $('<div>', {'class': 'float_left ellipsis', 'style': 'width:' + (_this.parameters.width - 100) + 'px;'}).appendTo(li).html(liData.name);
                                                $('<div>', {'class': 'clear'}).appendTo(li);

                                                $('<img>', {'src': liData.image, 'style': 'height:20px;width:20px;'}).appendTo(image);
                                                _this.selectItem(li, liData, true);
                                        }
                                        break;
                                case 38 : // arrow up
                                        evt.preventDefault();
                                        if (arrSelected[0] != undefined) {
                                                if (arrSelected.eq(0).prev() != undefined) {
                                                        arrSelected.eq(0).prev().addClass('ddlSelectedItem');
                                                        arrSelected.eq(0).removeClass('ddlSelectedItem');
                                                }
                                        }
                                        break;
                                case 40 : // arrow down
                                        evt.preventDefault();
                                        if (arrSelected.length == 0) { // top of list
                                                if (_this.ddlDropdown.children().length != 0) { // select first item in list if exists
                                                        _this.ddlDropdown.children().eq(0).addClass('ddlSelectedItem');
                                                }
                                        }
                                        else {
                                                if (arrSelected.eq(0).next().length != 0) { // move to next sibling if exists
                                                        arrSelected.eq(0).next().addClass('ddlSelectedItem');
                                                        arrSelected.eq(0).removeClass('ddlSelectedItem');
                                                }
                                        }
                                        break;
                                default :
                                        _this.displayDDL();
                                        break;
                        }
                });
                this.ddlArrow.bind('click', function (event) {
                        event.stopPropagation();
                        _this.displayDDL();
                });

                this.ddlRemove.bind('click', function (event) {
                        event.stopPropagation();
                        _this.ddlSelected.empty();
                        _this.ddlSelected.hide();
                        _this.ddlInput.empty();
                        _this.ddlInput.show();
                        _this.selected = undefined;
                        _this.ddlRemove.hide();
                        _this.ddlArrow.show();
                        _this.ddlDropdown.hide();
                });

                // close the dropdown on background click
                $(document).bind('click', function (event) {
                        event.stopPropagation();
                        _this.ddlDropdown.empty();
                });
        }

        ruckus.htmlcontrols.dropdown.prototype.displayDDL = function () {
                var _this = this;
                this.ddlDropdown.empty();
                $.each(this.parameters.data, function (key, value) {
                        if (value.name.toLowerCase().indexOf(_this.ddlInput.html().replace(/\<br\>/g, '').toLowerCase()) != -1) {
                                var li = $('<div>', {'id': value.value, 'class': 'ddlListItem', 'style': 'width:' + (_this.parameters.width - 45) + 'px;'})
                                        .appendTo(_this.ddlDropdown)
                                        .bind('click', function () {
                                                li.removeClass('ddlSelectedItem');
                                                _this.selectItem(li, value, true);
                                        });
                                var image = $('<div>', {'class': 'float_left ddlImage'}).appendTo(li);
                                $('<div>', {'class': 'float_left ellipsis', 'style': 'width:' + (_this.parameters.width - 100) + 'px;'}).appendTo(li).html(value.name);
                                $('<div>', {'class': 'clear'}).appendTo(li);

                                $('<img>', {'src': value.image, 'style': 'height:20px;width:20px;'}).appendTo(image);
                                li.bind('mouseenter', function (event) {
                                        event.stopPropagation();
                                        li.addClass('ddlSelectedItem');
                                });
                                li.bind('mouseleave', function (event) {
                                        event.stopPropagation();
                                        li.removeClass('ddlSelectedItem');
                                });
                        }
                });
                if (this.ddlDropdown.html() != '')
                        this.ddlDropdown.show();
        }

        ruckus.htmlcontrols.dropdown.prototype.selectItem = function (li, value, fireOnchange) {
                this.selected = value;
                li.appendTo(this.ddlSelected);
                this.ddlDropdown.hide();
                this.ddlInput.hide();
                this.ddlInput.empty();
                this.ddlSelected.show();
                this.ddlArrow.hide();
                this.ddlRemove.show();
                if (fireOnchange) {
                        if (this.parameters.onchange != undefined)
                                this.parameters.onchange(this.selected);
                }
        }

        ruckus.htmlcontrols.dropdown.prototype.clearValue = function () {
                this.ddlSelected.empty();
                this.ddlSelected.hide();
                this.ddlInput.empty();
                this.ddlInput.show();
                this.selected = undefined;
                this.ddlRemove.hide();
                this.ddlArrow.show();
                this.ddlDropdown.hide();
        }

        ruckus.htmlcontrols.dropdown.prototype.getValue = function () {
                return this.selected;
        }

        ruckus.htmlcontrols.dropdown.prototype.setValue = function (value) {
                var li = $('<div>', {'id': value.value, 'class': 'ddlListItem', 'style': 'width:' + (this.parameters.width - 45) + 'px;'});
                var image = $('<div>', {'class': 'float_left ddlImage'}).appendTo(li);
                $('<div>', {'class': 'float_left ellipsis', 'style': 'width:' + (this.parameters.width - 100) + 'px;'}).appendTo(li).html(value.name);
                $('<div>', {'class': 'clear'}).appendTo(li);

                $('<img>', {'src': value.image, 'style': 'height:20px;width:20px;'}).appendTo(image);
                this.selectItem(li, value, false);
        }

        return ruckus.htmlcontrols.dropdown;

});
