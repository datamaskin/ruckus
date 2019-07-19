// Author: Scott Gay
define([
        "assets/js/pagecontrols/base.js",
        "assets/js/libraries/jquery.min.js",
//        "assets/js/libraries/underscore-min.js",
        "assets/js/libraries/dust-core.min.js",
        "assets/js/subpagecontrols/blue.js",
        "assets/js/subpagecontrols/yellow.js",
        "assets/js/subpagecontrols/red.js",
        "assets/js/subpagecontrols/orange.js",
        "assets/js/subpagecontrols/green.js",
        "assets/js/htmlcontrols/textbox.js",
        "assets/js/htmlcontrols/dropdown.js",
        "assets/js/htmlcontrols/checkbox.js",
        "assets/js/htmlcontrols/radioGroup.js",
        "assets/js/htmlcontrols/toggle.js",
        "assets/js/htmlcontrols/tooltip.js",
        "assets/js/htmlcontrols/button.js"
], function (Base) {
        ruckus.pagecontrols.info = function (parameters) {
                Base.call(this);
                this.init = function () {
                        var _this = this;
                        this.parameters = parameters;
                        this.parameters.container.addClass('pagecontrolhighlight');

                        /*
                         // manage page load and unload through message bus
                         this.msgBus.subscribe( "nav.change", function ( data ) {
                         if (data.page == 'info')
                         _this.load();
                         else
                         if (_this.container != undefined)
                         _this.unload();
                         });
                         */
                };
                this.init();
        };

        ruckus.pagecontrols.info.prototype = Object.create(Base.prototype);

        ruckus.pagecontrols.info.prototype.load = function () {
                var _this = this;
                this.getContainer();
                this.container.addClass('ruckus-pc-info');

//                this.require_template('info-template');

//                var template = _.template($('#info-template').html());
//                this.container.html(template);

                this.require_template('info-tpl');
                dust.render('dusttemplates/info-tpl', {}, function (err, out) {
                        _this.container.html(out);
			_this.addScrollBars();

                        // textbox
                        var emailRegex = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
                        var txtbox = new ruckus.htmlcontrols.textbox({
                                "container": $('#textboxctrl'),
                                "placeholder": "Email Address",
                                "width": 200,
                                "validationRegex": emailRegex
                        });
                        txtbox.setValue('info@ruckus.com');

                        // dropdown
                        var dropdownData = [
                                {"name": "NY Yankees", "value": "yankees", "image": "assets/images/mlb/yankees.png"},
                                {"name": "CHI Cubs", "value": "cubs", "image": "assets/images/mlb/cubs.png"},
                                {"name": "SF Giants", "value": "giants", "image": "assets/images/mlb/giants.png"},
                                {"name": "ATL Braves", "value": "braves", "image": "assets/images/mlb/braves.png"},
                                {"name": "DET Tigers", "value": "tigers", "image": "assets/images/mlb/tigers.png"},
                                {"name": "HOU Astros", "value": "astros", "image": "assets/images/mlb/astros.png"},
                                {"name": "TX Rangers", "value": "rangers", "image": "assets/images/mlb/rangers.png"},
                                {"name": "STL Cardinals", "value": "cardinals", "image": "assets/images/mlb/cardinals.png"},
                                {"name": "ANA Angels", "value": "angels", "image": "assets/images/mlb/angels.png"},
                                {"name": "SD Padres", "value": "padres", "image": "assets/images/mlb/padres.png"},
                                {"name": "SEA Mariners", "value": "mariners", "image": "assets/images/mlb/mariners.png"},
                                {"name": "MIN Twins", "value": "twins", "image": "assets/images/mlb/twins.png"},
                                {"name": "BAL Orioles", "value": "orioles", "image": "assets/images/mlb/orioles.png"},
                                {"name": "CLE Indians", "value": "indians", "image": "assets/images/mlb/indians.png"},
                                {"name": "AZ Diamondbacks", "value": "diamondbacks", "assetsimage": "/images/mlb/diamondbacks.png"},
                                {"name": "COL Rockies", "value": "rockies", "image": "assets/images/mlb/rockies.png"},
                                {"name": "PIT Pirates", "value": "pirates", "image": "assets/images/mlb/pirates.png"}
                        ];
                        var ctrlDropdown = new ruckus.htmlcontrols.dropdown({
                                'container': $('#dropdownctrl'),
                                'data': dropdownData,
                                'placeholder': 'Pick a Team',
                                'width': 300,
                                'onchange': function (item) {
                                        alert('Selected the ' + item.name);
                                }
                        });
                        ctrlDropdown.setValue({"name": "COL Rockies", "value": "rockies", "image": "assets/images/mlb/rockies.png"});

                        // checkbox
                        var ctrlCheckbox = new ruckus.htmlcontrols.checkbox({
                                'container': $('#checkboxctrl'),
                                'label': 'STL Cardinals',
                                'onchange': function () {
                                        alert(ctrlCheckbox.getValue());
                                }
                        });

                        // radioGroup
                        var radioItems = [
                                {"name": "CHI Cubs", "value": "cubs"},
                                {"name": "NY Yankees", "value": "yankees"},
                                {"name": "TX Rangers", "value": "rangers"}
                        ];
                        var ctrlRadioGroup = new ruckus.htmlcontrols.radioGroup({
                                'container': $('#radiogroupctrl'),
                                'items': radioItems,
                                'selected': {"name": "TX Rangers", "value": "rangers"},
                                'onchange': function () {
                                        alert(ctrlRadioGroup.getValue());
                                }
                        });

                        // toggle
                        new ruckus.htmlcontrols.toggle({
                                'container': $('#togglectrl'),
                                'textOn': 'On',
                                'textOff': 'Off',
                                'checked': true
                        });

                        // tooltip
                        new ruckus.htmlcontrols.tooltip({
                                'container': $('#tooltipctrl'),
                                'text': 'The New York Yankees were established in 1901.',
                                'width': '200',
                                'xOffset': 50,
                                'yOffset': -25
                        });

                        // button
                        new ruckus.htmlcontrols.button({
                                'container': $('#buttonctrl'),
                                'buttonText': 'Small Button',
                                'buttonSize': 'small',
                                'buttonColor': 'green',
                                'onclick': function () {
                                        alert('clicked the small button');
                                }
                        });
                        new ruckus.htmlcontrols.button({
                                'container': $('#buttonctrl'),
                                'buttonText': 'Medium Button',
                                'buttonSize': 'medium',
                                'buttonColor': 'red',
                                'hoverEffect': false,
                                'onclick': function () {
                                        alert('clicked the medium button');
                                }
                        });
                        new ruckus.htmlcontrols.button({
                                'container': $('#buttonctrl'),
                                'buttonText': 'Large Button',
                                'buttonSize': 'large',
                                'buttonColor': 'blue',
                                'onclick': function () {
                                        alert('clicked the large button');
                                }
                        });
                });
        };

        ruckus.pagecontrols.info.prototype.unload = function () {
                this.destroyControl();
        };

        return ruckus.pagecontrols.info;
});


