// Author: Scott Gay
define([
        "assets/js/pagecontrols/base.js",
        "assets/js/libraries/jquery.min.js",
        "assets/js/libraries/jquery.tablesorter.min.js", // ability to click column header to sort
        "assets/js/libraries/dhtmlxslider/dhtmlxcommon.js", // dhtmlxslider (also in shim)
//	"assets/js/libraries/dhtmlxslider/dhtmlxslider.js", // dhtmlxslider (also in shim)
        "assets/js/libraries/nouislider/jquery.nouislider.min.js", // nouislider
        "assets/js/libraries/jquery-ui/jquery-ui-1.10.4.custom.min.js", // jquery slider
        "assets/js/libraries/jquery-ui/jquery.ui.touch-punch.min.js", // jquery slider hack for mobile drag - http://touchpunch.furf.com/
        "assets/js/libraries/ionrangeslider/ion.rangeSlider.js"//,
//	"assets/js/libraries/bootstrap-slider/bootstrap-slider.js"
], function (Base) {
        ruckus.pagecontrols.sectionone = function (parameters) {
                Base.call(this);
                this.init = function () {
                        var _this = this;
                        this.parameters = parameters;

                };
                this.init();
        };

        ruckus.pagecontrols.sectionone.prototype = Object.create(Base.prototype);

        ruckus.pagecontrols.sectionone.prototype.load = function () {
                var _this = this;
                this.getContainer();
                this.container.addClass('ruckus-pc-sectionone');

                var myContainer = $('<div>', {'class': 'col-xs-24'}).appendTo(
                        $("<div>", {'class': 'row'}).appendTo(
                                $('<div>', {'class': 'col-xs-24'}).appendTo(this.container)
                        )
                );
                /*
                 // dhtmlxSlider http://dhtmlx.com/docs/products/dhtmlxSlider/
                 $('<div>').appendTo(myContainer).html('dhtmlxSlider');
                 $("<div>", {'id' : 'dhtmlxslideroutput', 'style' : 'float:left;width:75px;'}).appendTo(myContainer).html('&nbsp;');
                 $("<div>", {'id' : 'dhtmlxslider', 'style' : 'float:left;'}).appendTo(myContainer);
                 $("<div>", {'style' : 'clear:both;'}).appendTo(myContainer);
                 var slider = new dhtmlxSlider('dhtmlxslider', {
                 'size' : 400,
                 'skin' : 'arrowgreen',
                 'vertical' : false,
                 'step' : 1,
                 'min' : 0,
                 'max' : 100,
                 'value' : 70
                 });
                 slider.setImagePath('/assets/images/libraries/dhtmlxslider/');
                 slider.init();
                 slider.attachEvent("onchange", function(newValue, sliderObj){
                 $('#dhtmlxslideroutput').html(newValue);
                 });
                 */
                // jquery ui range example
                $('<div>').appendTo(myContainer).html('jquery ui slider');
                $("<div>", {'id': 'jqueryuislideroutput', 'style': 'float:left;width:75px;'}).appendTo(myContainer).html('&nbsp;');
                $("<div>", {'id': 'jqueryuislider', 'style': 'float:left;width:400px;'}).appendTo(myContainer);
                $("<div>", {'style': 'clear:both;'}).appendTo(myContainer);
                $("#jqueryuislider").slider({
                        range: true,
                        values: [10, 25],
                        min: 0,
                        max: 100,
                        step: 10,
                        change: function (event, ui) {
                                $('#jqueryuislideroutput').html(ui.values[0] + '-' + ui.values[1]);
                        }
                });

                // nouislider http://refreshless.com/nouislider/
                $('<div>').appendTo(myContainer).html('nouislider');
                $("<div>", {'id': 'nouislideroutput', 'style': 'float:left;width:75px;'}).appendTo(myContainer).html('&nbsp;');
                $("<div>", {'id': 'nouislider', 'style': 'float:left;width:400px;'}).appendTo(myContainer);
                $("<div>", {'style': 'clear:both;'}).appendTo(myContainer);
                $("#nouislider").noUiSlider({
                        start: [10, 25],
                        step: 10,
                        margin: 20,
                        connect: true,
                        direction: 'rtl',
                        orientation: 'horizontal',
                        behaviour: 'tap-drag',
                        range: {
                                'min': 0,
                                'max': 100
                        }
                });
                $("#nouislider").change(function (event, newValue) {
                        $('#nouislideroutput').html(newValue[0].split('.')[0] + '-' + newValue[1].split('.')[0]);
                });

                // ion slider http://ionden.com/a/plugins/ion.rangeSlider/en.html https://github.com/IonDen/ion.rangeSlider
                // https://github.com/IonDen/ion.rangeSlider/issues/44
                $('<div>').appendTo(myContainer).html('ionslider');
                $("<div>", {'id': 'ionslideroutput', 'style': 'float:left;width:75px;'}).appendTo(myContainer).html('&nbsp;');
                $("<div>", {'id': 'ionslider', 'style': 'float:left;width:400px;'}).appendTo(myContainer);
                $("<input>", {'id': 'ionsliderid', 'name': 'ionslidername'}).appendTo($('#ionslider'));
                $("<div>", {'style': 'clear:both;'}).appendTo(myContainer);
                var vals = [1, 2, 3, 5, 10, 15, 20, 50, 100, 500];
                var fromNumber = 0;
                var toNumber = 9;
                $("#ionsliderid").ionRangeSlider({
                        min: 0,
                        max: 10,
                        step: 10,
                        values: vals,
                        type: 'double',
                        prefix: "$",
//		    	maxPostfix: "+",
                        prettify: false,
                        hasGrid: true,
                        onChange: function (obj) {
//				console.log(obj);
                                $('#ionslideroutput').html(obj.fromValue + '-' + obj.toValue);
                                if (fromNumber != obj.fromNumber || toNumber != obj.toNumber) {
                                        $('#ionsliderid').ionRangeSlider("update", {
                                                from: obj.fromNumber,
                                                to: obj.toNumber
                                        });
                                }
                                fromNumber = obj.fromNumber;
                                toNumber = obj.toNumber;
                        }
                });
                /*
                 // bootstrap-slider http://www.eyecon.ro/bootstrap-slider/
                 $('<div>').appendTo(myContainer).html('bootstrap slider');
                 $("<div>", {'id' : 'bootstrapslideroutput', 'style' : 'float:left;width:75px;'}).appendTo(myContainer).html('&nbsp;');
                 $("<div>", {'id' : 'bootstrapslider', 'style' : 'float:left;width:400px;'}).appendTo(myContainer);
                 $("<input>", {"id":"dp5", "type":"text", "class":"span2", "value":"", "data-slider-min":"10", "data-slider-max":"1000", "data-slider-step":"5", "data-slider-value":"[250,450]", "id":"sl2"}).appendTo(myContainer);
                 $('#dp5').slider();
                 */

                // table example
                this.require_template('exampletable-tpl');
                dust.render('dusttemplates/exampletable-tpl', {}, function (err, out) {
                        var tableContainer = $("<div>").appendTo(_this.container);
                        tableContainer.html(out);
			_this.addScrollBars();
                        $("#myTable").tablesorter();
                });
        };

        ruckus.pagecontrols.sectionone.prototype.unload = function () {
                this.destroyControl();
        };

        return ruckus.pagecontrols.sectionone;
});


