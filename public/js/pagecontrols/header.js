// Author: Scott Gay
define([
        "assets/js/pagecontrols/base.js",
        "assets/js/libraries/postal.js",
        "assets/js/libraries/dust-core.min.js",
        "assets/js/libraries/bootstrap.min.js",
        "assets/js/models/wallet.js",
        "assets/js/modules/navigation.js"
], function (Base, postal) {
        ruckus.pagecontrols.header = function (parameters) {
                Base.call(this);
                this.init = function () {
                        this.parameters = parameters;
                        this.parameters.container.addClass('pagecontrolhighlight');
                };
                this.init();
        };

        ruckus.pagecontrols.header.prototype = Object.create(Base.prototype);
        ruckus.pagecontrols.header.prototype.load = function () {
                var _this = this;
                this.getContainer();
                this.container.addClass('ruckus-pc-header');

//                this.require_template('header-template');

//                var template = _.template($('#header-template').html());
//                this.container.html(template);

                this.require_template('header-tpl');
                dust.render('dusttemplates/header-tpl', {}, function (err, out) {
                        _this.container.html(out);
                        _this.addScrollBars();
                        var sub = _this.msgBus.subscribe("model.wallet.retrieve", function (data) {
                                $("#walletAmountUsd").html(_this.formatMoney(data.data.payload.usd));
                                $("#application_username").html(data.data.payload.username);
                        });
                        _this.subscriptions.push(sub);
                        _this.wallet = new ruckus.models.wallet({});
                        _this.wallet.fetch();
                        var wallet_interval = setInterval(function(){
                                _this.wallet.fetch();
                        }, 30000);

                        _this.intervals.push(wallet_interval);
                });

                $('#mainlink').bind('click', function () {
                        ruckus.modules.navigation.toRoute('main');
                });
                $('#lobbylink').bind('click', function () {
                        ruckus.modules.navigation.toRoute('lobby');
                });
                $('#lobbyavsblink').bind('click', function () {
                        ruckus.modules.navigation.toRoute('lobbyavsb');
                });
                $('#dashboardlink').bind('click', function () {
                        ruckus.modules.navigation.toRoute('dashboard');
                });
                $('#dashboardlineupslink').bind('click', function () {
                        ruckus.modules.navigation.toRoute('dashboardlineups');
                });
                $('#dashboardathleteslink').bind('click', function () {
                        ruckus.modules.navigation.toRoute('dashboardathletes');
                });
                $('#dashboardhistorylink').bind('click', function () {
                        ruckus.modules.navigation.toRoute('dashboardhistory');
                });
                $('#sectiononelink').bind('click', function () {
                        ruckus.modules.navigation.toRoute('sectionone');
                });
                $('#sectiontwolink').bind('click', function () {
                        ruckus.modules.navigation.toRoute('sectiontwo');
                });
                $('#infolink').bind('click', function () {
                        ruckus.modules.navigation.toRoute('info');
                });
                $('#sectiondatalink').bind('click', function () {
                        ruckus.modules.navigation.toRoute('sectiondata');
                });
//                $('#depositlink').bind('click', function () {
//                        ruckus.modules.navigation.toRoute('deposit');
//                });
//                $('#balancedepositlink').bind('click', function () {
//                        ruckus.modules.navigation.toRoute('deposit');
//                });
//                $('#withdrawllink').bind('click', function () {
//                        ruckus.modules.navigation.toRoute('withdrawl');
//                });
                $('#transactionhistorylink').bind('click', function () {
                        ruckus.modules.navigation.toRoute('transactionhistory');
                });
                $('#referafriendlink').bind('click', function () {
                        ruckus.modules.navigation.toRoute('referafriend');
                });
                $('#loyaltybonuslink').bind('click', function () {
                        ruckus.modules.navigation.toRoute('loyaltybonus');
                });
                $('#settingslink').bind('click', function () {
                        ruckus.modules.navigation.toRoute('settings');
                });
                $('#supportlink').bind('click', function () {
                        ruckus.modules.navigation.toRoute('support');
                });
                $('#howitworkslink').bind('click', function () {
                        ruckus.modules.navigation.toRoute('howitworks');
                });
                $('#termsandconditionslink').bind('click', function () {
                        ruckus.modules.navigation.toRoute('termsandconditions');
                });
                $('#privacylink').bind('click', function () {
                        ruckus.modules.navigation.toRoute('privacy');
                });
                $('#faqlink').bind('click', function () {
                        ruckus.modules.navigation.toRoute('faq');
                });
        };

        ruckus.pagecontrols.header.prototype.unload = function () {
                this.destroyControl();
        };

        return ruckus.pagecontrols.header;
});


