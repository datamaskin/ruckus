// Author: Scott Gay
define([
        "assets/js/htmlcontrols/base.js",
        "assets/js/libraries/jquery.min.js"
], function (Base) {
        ruckus.htmlcontrols.textbox = function (parameters) {
                // parameters:
                // - container
                // - placeholder
                // - width (default 300)
                // - validationRegex

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

        ruckus.htmlcontrols.textbox.prototype = Object.create(Base.prototype);

        ruckus.htmlcontrols.textbox.prototype.load = function () {
                var _this = this;
                this.getContainer();

                var dvInput = $('<div>', {'class': 'float_left'}).appendTo(this.container);
                this.txtbox = $('<input>', {'type': 'textbox', 'class': 'txtbox', 'style': 'width:' + this.parameters.width + 'px;'}).appendTo(dvInput);
                if (this.parameters.validationRegex != undefined) {
                        this.dvValidation = $('<div>', {'class': 'float_left valFail'}).appendTo(this.container);
                }
                $('<div>', {'class': 'clear', 'style': 'margin-bottom:10px;'}).appendTo(this.container);

                if (this.parameters.placeholder != undefined)
                        this.placeholder = $('<div>', {'class': 'placeholder'}).appendTo(this.container).html(this.parameters.placeholder);

                // validation
                if (this.parameters.validationRegex != undefined) {
                        this.txtbox.bind('keyup', function () {
                                _this.runValidation();
                        });
                }
        }

        ruckus.htmlcontrols.textbox.prototype.runValidation = function () {
                var valid = this.parameters.validationRegex.test(this.getValue());
                if (valid) {
                        this.dvValidation.removeClass('valFail');
                        this.dvValidation.addClass('valPass');
                }
                else {
                        this.dvValidation.removeClass('valPass');
                        this.dvValidation.addClass('valFail');
                }
        }

        ruckus.htmlcontrols.textbox.prototype.isValid = function () {
                return this.parameters.validationRegex.test(this.getValue());
        }

        ruckus.htmlcontrols.textbox.prototype.setValue = function (val) {
                this.txtbox.val(val);
                if (this.parameters.validationRegex != undefined) {
                        this.runValidation();
                }
        }

        ruckus.htmlcontrols.textbox.prototype.getValue = function () {
                return this.txtbox.val();
        }

        ruckus.htmlcontrols.textbox.prototype.unload = function () {
                this.destroyControl();
        }

        return ruckus.htmlcontrols.textbox;

});
