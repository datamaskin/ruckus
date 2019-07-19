// Author: Scott Gay
define([
        "assets/js/base.js",
        "assets/js/libraries/backbone-min.js",
        "assets/js/libraries/jquery.min.js",
        "assets/js/pagecontrols/header.js",
//        "assets/js/pagecontrols/main.js",
        "assets/js/pagecontrols/lobby.js",
        "assets/js/pagecontrols/lobbyavsb.js",
        "assets/js/pagecontrols/contestentry.js",
        "assets/js/pagecontrols/dashboard.js",
        "assets/js/pagecontrols/dashboardcontest.js",
        "assets/js/pagecontrols/dashboardlineups.js",
        "assets/js/pagecontrols/dashboardathletes.js",
        "assets/js/pagecontrols/dashboardhistory.js",
        "assets/js/pagecontrols/deposit.js",
        "assets/js/pagecontrols/withdrawl.js",
        "assets/js/pagecontrols/transactionhistory.js",
        "assets/js/pagecontrols/referafriend.js",
        "assets/js/pagecontrols/loyaltybonus.js",
        "assets/js/pagecontrols/settings.js",
        "assets/js/pagecontrols/support.js",
        "assets/js/pagecontrols/howitworks.js",
        "assets/js/pagecontrols/termsofuse.js",
        "assets/js/pagecontrols/privacypolicy.js",
        "assets/js/pagecontrols/faq.js",
        "assets/js/pagecontrols/error.js",
//        "assets/js/pagecontrols/info.js",
//        "assets/js/pagecontrols/sectionone.js",
//        "assets/js/pagecontrols/sectiontwo.js",
//        "assets/js/pagecontrols/sectiondata.js",
        "assets/js/pagecontrols/footer.js",
        "assets/js/modules/scrollbars.js",
        "assets/js/pagecontrols/legal.js",
        "assets/js/pagecontrols/landing.js",
        "assets/js/pagecontrols/about.js",
        "assets/js/pagecontrols/promos.js",
        "assets/js/pagecontrols/affiliates.js"
], function (Base) {
        ruckus.controller = function (parameters) {
                Base.call(this);
                this.init = function () {
                        this.parameters = parameters;
                        this.load();
                };
                this.init();
        };

        ruckus.controller.prototype = Object.create(Base.prototype);
        ruckus.controller.prototype.load = function () {
                var _this = this;

                // define sections
                this.pcHeader = new ruckus.pagecontrols.header({
                        'container': this.parameters.divHeader
                });
                this.pcFooter = new ruckus.pagecontrols.footer({
                        'container': this.parameters.divFooter
                });

                // load initial sections
                this.pcHeader.load();
                this.pcFooter.load();
                this.liveMain = undefined;

                // backbone routes
                var Router = Backbone.Router.extend({
                        routes: {
                                '': 'lobby',
                                '_=_': 'lobby',
//                                'sectionmain': 'sectionmain',

                                'lobby?(:query)': 'lobby',
                                'lobby': 'lobby',
                                'lobbyavsb': 'lobbyavsb',
                                'contestentry/:id': 'contestentry',

                                'dashboard/:id?(:query)': 'dashboard',
                                'dashboard/:id': 'dashboard',
                                'dashboard?(:query)': 'dashboard',
                                'dashboard': 'dashboard',

                                'dashboardcontest/:id?(:query)': 'dashboardcontest',
                                'dashboardcontest/:id': 'dashboardcontest',

                                'dashboardlineups/:id?(:query)': 'dashboardlineups',
                                'dashboardlineups/:id': 'dashboardlineups',
                                'dashboardlineups?(:query)': 'dashboardlineups',
                                'dashboardlineups': 'dashboardlineups',

                                'dashboardathletes?(:query)': 'dashboardathletes',
                                'dashboardathletes': 'dashboardathletes',

                                'dashboardhistory/:id?(:query)': 'dashboardhistory',
                                'dashboardhistory/:id': 'dashboardhistory',
                                'dashboardhistory?(:query)': 'dashboardhistory',
                                'dashboardhistory': 'dashboardhistory',

                                'deposit': 'deposit',
                                'withdrawl': 'withdrawl',
                                'transactionhistory': 'transactionhistory',
                                'referafriend': 'referafriend',
                                'loyaltybonus': 'loyaltybonus',
                                'settings': 'settings',
                                'support': 'support',
                                'howitworks': 'howitworks',
                                'termsofuse': 'termsofuse',
                                'privacypolicy': 'privacypolicy',
                                'faq': 'faq',
                                'error': 'error',
//                                'sectionone': 'sectionone',
//                                'sectiontwo': 'sectiontwo',
//                                'sectioninfo': 'sectioninfo',
//                                'sectiondata': 'sectiondata',
                                'legal': 'legal',
                                'landing': 'landing',
                                'about': 'about',
                                'promos': 'promos',
                                'affiliates': 'affiliates'
                        }
                });
                this.router = new Router();

                this.router.on('route:lobby', function (query) {
                        var params = {};
                        if(query) params = parseQueryString(query);
                        _this.changeview(params, ruckus.pagecontrols.lobby, _this.parameters.divMain, "gamesdropdownlink");
                });
                this.router.on('route:lobbyavsb', function () {
                        _this.changeview(null, ruckus.pagecontrols.lobbyavsb, _this.parameters.divMain, "gamesdropdownlink");
                });
                this.router.on('route:contestentry', function (contestId) {
                        var params = { 'contestId': contestId };
                        _this.changeview(params, ruckus.pagecontrols.contestentry, _this.parameters.divMain, "lobbylink");
                });
                this.router.on('route:dashboard', function (id, query) {
                        var params = {};
                        if(query) params = parseQueryString(query);
                        if(id) params.contestId = id;
                        _this.changeview(params, ruckus.pagecontrols.dashboard, _this.parameters.divMain, "dashboarddropdownlink");
                });
                this.router.on('route:dashboardcontest', function (id, query) {
                        var params = {};
                        if(query) params = parseQueryString(query);
                        if(id) params.contestId = id;
                        _this.changeview(params, ruckus.pagecontrols.dashboardcontest, _this.parameters.divMain, "dashboarddropdownlink");
                });
                this.router.on('route:dashboardlineups', function (id, query) {
                        var params = {};
                        if(query) params = parseQueryString(query);
                        if(id) params.contestId = id;
                        _this.changeview(params, ruckus.pagecontrols.dashboardlineups, _this.parameters.divMain, "dashboarddropdownlink");
                });
                this.router.on('route:dashboardathletes', function (query) {
                        var params = {};
                        if(query) params = parseQueryString(query);
                        _this.changeview(params, ruckus.pagecontrols.dashboardathletes, _this.parameters.divMain, "dashboarddropdownlink");
                });
                this.router.on('route:dashboardhistory', function (id, query) {
                        var params = {};
                        if(query) params = parseQueryString(query);
                        if(id) params.contestId = id;
                        _this.changeview(params, ruckus.pagecontrols.dashboardhistory, _this.parameters.divMain, "dashboarddropdownlink");
                });
                this.router.on('route:deposit', function () {
                        _this.changeview(null, ruckus.pagecontrols.deposit, _this.parameters.divMain, "userdropdownlink");
                });
                this.router.on('route:withdrawl', function () {
                        _this.changeview(null, ruckus.pagecontrols.withdrawl, _this.parameters.divMain, "userdropdownlink");
                });
                this.router.on('route:transactionhistory', function () {
                        _this.changeview(null, ruckus.pagecontrols.transactionhistory, _this.parameters.divMain, "userdropdownlink");
                });
                this.router.on('route:referafriend', function () {
                        _this.changeview(null, ruckus.pagecontrols.referafriend, _this.parameters.divMain, "userdropdownlink");
                });
                this.router.on('route:loyaltybonus', function () {
                        _this.changeview(null, ruckus.pagecontrols.loyaltybonus, _this.parameters.divMain, "userdropdownlink");
                });
                this.router.on('route:settings', function () {
                        _this.changeview(null, ruckus.pagecontrols.settings, _this.parameters.divMain, "userdropdownlink");
                });
                this.router.on('route:support', function () {
                        _this.changeview(null, ruckus.pagecontrols.support, _this.parameters.divMain, "supportdropdownlink");
                });
                this.router.on('route:howitworks', function () {
                        _this.changeview(null, ruckus.pagecontrols.howitworks, _this.parameters.divMain, "supportdropdownlink");
                });
                this.router.on('route:termsofuse', function () {
                        _this.changeview(null, ruckus.pagecontrols.termsofuse, _this.parameters.divMain, "supportdropdownlink");
                });
                this.router.on('route:privacypolicy', function () {
                        _this.changeview(null, ruckus.pagecontrols.privacypolicy, _this.parameters.divMain, "supportdropdownlink");
                });
                this.router.on('route:faq', function () {
                        _this.changeview(null, ruckus.pagecontrols.faq, _this.parameters.divMain, "supportdropdownlink");
                });
                this.router.on('route:error', function () {
                        _this.changeview(null, ruckus.pagecontrols.error, _this.parameters.divMain);
                });
/*
                this.router.on('route:sectionmain', function () {
                        _this.changeview(null, ruckus.pagecontrols.main, _this.parameters.divMain, "examplepagesdropdownlink");
                });
                this.router.on('route:sectionone', function () {
                        _this.changeview(null, ruckus.pagecontrols.sectionone, _this.parameters.divMain, "examplepagesdropdownlink");
                });
                this.router.on('route:sectiontwo', function () {
                        _this.changeview(null, ruckus.pagecontrols.sectiontwo, _this.parameters.divMain, "examplepagesdropdownlink");
                });
                this.router.on('route:sectioninfo', function () {
                        _this.changeview(null, ruckus.pagecontrols.info, _this.parameters.divMain, "examplepagesdropdownlink");
                });
                this.router.on('route:sectiondata', function () {
                        _this.changeview(null, ruckus.pagecontrols.sectiondata, _this.parameters.divMain, "examplepagesdropdownlink");
                });
*/
                this.router.on('route:legal', function () {
                        _this.changeview(null, ruckus.pagecontrols.legal, _this.parameters.divMain);
                });
                this.router.on('route:landing', function () {
                        _this.changeview(null, ruckus.pagecontrols.landing, _this.parameters.divMain);
                });
                this.router.on('route:about', function () {
                        _this.changeview(null, ruckus.pagecontrols.about, _this.parameters.divMain);
                });
                this.router.on('route:promos', function () {
                        _this.changeview(null, ruckus.pagecontrols.promos, _this.parameters.divMain);
                });
                this.router.on('route:affiliates', function () {
                        _this.changeview(null, ruckus.pagecontrols.affiliates, _this.parameters.divMain);
                });

                Backbone.history.start();
        };

        ruckus.controller.prototype.changeview = function (params, control, container, activeLinkId) {
                var _this = this;
                ruckus.modules.scrollbars.remove();

                if(params === null) params = { 'container': container };  else params.container = container;

                var newControl = new control(params);

                if (_this.liveMain != undefined) {
                        _this.liveMain.unload();
                }
                newControl.load();
                $(".active").removeClass('active');
                if(activeLinkId) {
                        $('#' + activeLinkId).addClass('active');
                }
                _this.liveMain = newControl;

		        // google analytics tracking
		        //ga('send', 'event', 'page', 'loaded', 'label', location.href);
		        ga('send', 'pageview', {
                    'page': location.pathname + location.search  + location.hash
                });
        };

        // and the function that parses the query string can be something like :
        function parseQueryString(queryString){
                var params = {};
                if(queryString){
                        _.each(
                                _.map(decodeURI(queryString).split(/&/g),function(el,i){
                                        var aux = el.split('='), o = {};
                                        if(aux.length >= 1){
                                                var val = undefined;
                                                if(aux.length == 2)
                                                        val = aux[1];
                                                o[aux[0]] = val;
                                        }
                                        return o;
                                }),
                                function(o){
                                        _.extend(params,o);
                                }
                        );
                }
                return params;
        }

        ruckus.controller.prototype.unload = function () {
				
        };

        return ruckus.controller;
});
