// Author: Scott Gay
// *** replaced underscore templating with dust templating
define([
        "assets/js/pagecontrols/base.js",
        "assets/js/libraries/jquery.min.js",
//        "assets/js/libraries/underscore-min.js",
        "assets/js/libraries/dust-core.min.js",
        //"assets/js/libraries/socket.io.min.js",
        "assets/js/libraries/jquery.atmosphere.js",
//	"assets/js/models/info.js",
        "assets/js/models/contest.js",
//	"assets/js/subpagecontrols/blue.js",
//        "assets/js/subpagecontrols/yellow.js",
//        "assets/js/subpagecontrols/red.js",
//        "assets/js/subpagecontrols/orange.js",
//        "assets/js/subpagecontrols/green.js"
], function (Base) {
        ruckus.pagecontrols.sectiondata = function (parameters) {
                Base.call(this);
                this.init = function () {
                        var _this = this;
                        this.parameters = parameters;
                        this.parameters.container.addClass('pagecontrolhighlight');
                        /*
                         this.infoModel = new ruckus.models.info({
                         'container' : this.container,
                         'dataTTL' : 20
                         });
                         */
                };
                this.init();
        };

        ruckus.pagecontrols.sectiondata.prototype = Object.create(Base.prototype);

        ruckus.pagecontrols.sectiondata.prototype.load = function () {
                var _this = this;
                this.getContainer();
                this.container.addClass('ruckus-pc-sectiondata');

                this.contestModel = new ruckus.models.contest({});
                var sub = this.msgBus.subscribe("model.contest.retrieve", function (data) {
                        sub.unsubscribe();
                        _this.log({type: 'api', data: data, msg: "CONTEST MODEL DATA"});
                });
                this.contestModel.fetch();

                /*
                 // WITH MODELS
                 this.require_template('data-tpl');
                 // sample params (don't really exist for this api or limit data).  Just using to test model/localStorage caching
                 var fParams = {
                 'start' : 10,
                 'limit' : 11
                 };
                 var infoPromise = this.infoModel.fetch(fParams);
                 infoPromise.done(function(data){
                 dust.render('dusttemplates/data-tpl', data, function(err, out){
                 $('#restful').html(out);
				_this.addScrollBars();
                 });
                 });
                 */
        };

        ruckus.pagecontrols.sectiondata.prototype.unload = function () {
                this.destroyControl();
        };

        return ruckus.pagecontrols.sectiondata;
});


