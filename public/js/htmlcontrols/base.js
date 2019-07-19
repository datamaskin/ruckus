// Author: Scott Gay
define([
        "assets/js/libraries/jquery.min.js"
], function () {

        ruckus.htmlcontrols.base = function (parameters) {
                this.init = function () {
                        this.parameters = parameters;
                }
                this.init();
        }

        ruckus.htmlcontrols.base.prototype.getContainer = function () {
                // this creates a container for each html control
                this.container = $('<div>').appendTo(this.parameters.container);
        }

        ruckus.htmlcontrols.base.prototype.destroyControl = function () {
                this.container.remove();
        }

        ruckus.htmlcontrols.base.prototype.hidePage = function () {
                this.container.hide();
        }

        ruckus.htmlcontrols.base.prototype.showPage = function () {
                this.container.show();
        }

        return ruckus.htmlcontrols.base;

});
