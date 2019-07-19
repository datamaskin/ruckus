// Author: Scott Gay
define([
        "assets/js/pagecontrols/base.js",
        "assets/js/libraries/jquery.min.js",
//        "assets/js/libraries/underscore-min.js",
        "assets/js/libraries/dust-core.min.js"
], function (Base) {
        ruckus.pagecontrols.footer = function (parameters) {
                Base.call(this);
                this.init = function () {
                        this.parameters = parameters;
                        this.parameters.container.addClass('pagecontrolhighlight');
                };
                this.init();
        };

        ruckus.pagecontrols.footer.prototype = Object.create(Base.prototype);
        ruckus.pagecontrols.footer.prototype.load = function () {
                var _this = this;
                this.getContainer();
                this.container.addClass('ruckus-pc-footer');

//                this.require_template('footer-template');

//                var template = _.template($('#footer-template').html());
//                this.container.html(template);

                this.require_template('footer-tpl');
                dust.render('dusttemplates/footer-tpl', {}, function (err, out) {
                        _this.container.html(out);
			_this.addScrollBars();
                        $('#switchdesktop').bind('click', function () {
                                $('#viewport').attr('content', 'width=1024, initial-scale=0, maximum-scale=5.0, user-scalable=1');
                        });
                });


        };

        ruckus.pagecontrols.footer.prototype.unload = function () {
                this.destroyControl();
        };

        return ruckus.pagecontrols.footer;
});


