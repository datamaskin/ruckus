// Author: Scott Gay
define([
    "assets/js/pagecontrols/base.js",
    "assets/js/libraries/jquery.min.js",
    "assets/js/models/depositbitcoin.js",
    "assets/js/models/wallet.js",
    "assets/js/models/walletProfile.js",
    "assets/js/subpagecontrols/walletProfile.js"
], function(Base){
    ruckus.pagecontrols.deposit = function(parameters){
        Base.call(this);
        this.init = function(){
            var _this = this;
            this.parameters = parameters;
        };
        this.init();
    };

    ruckus.pagecontrols.deposit.prototype = Object.create(Base.prototype);

    ruckus.pagecontrols.deposit.prototype.load = function () {
        var _this = this;
        this.getContainer();
        this.container.addClass('ruckus-pc-deposit');

        this.require_template('deposit-tpl');

        _this.wallet = new ruckus.models.wallet({});
        _this.walletProfile = new ruckus.models.walletProfile({});

        var loadProfiles = function(){
            var sub = _this.msgBus.subscribe("model.walletProfile.retrieve", function (data) {
                sub.unsubscribe();
                var profile = new ruckus.subpagecontrols.walletProfile({
                    container:$("#walletProfilesContainer"),
                    data:data
                });
                $("#walletProfilesContainer").empty();
                profile.load();
            });
            _this.walletProfile.fetch();
        }

        $.when(
            $.get('/wallet/getBitcoinButton', function (data, status) {}),
            $.get('/wallet/staticData', function (data, status) {})
        ).then(function(buttonCode, staticData){
            buttonCode = JSON.parse(buttonCode[0]).payload;
            staticData = JSON.parse(staticData[0]).payload;

            dust.render(
                'dusttemplates/deposit-tpl',
                {buttonCode:buttonCode, staticData:staticData},
                function(err, out){
                    _this.container.html(out);
                    _this.addScrollBars();

                    loadProfiles();

            });

//            $("#authorizeDepositSubmit").bind("click", function(){
//                alert("hiya!");

//            });

//            $("#addNewProfileSubmit").bind("click", function(){
//                var data = $("#addNewProfileForm").serialize();
//                $.ajax({
//                    type: "POST",
//                    url: "/wallet/newProfile",
//                    contentType: 'application/json',
//                    data: JSON.stringify({
//                        "name":$("#name").val(),
//                        "address1":$("#address1").val(),
//                        "address2":$("#address2").val(),
//                        "city":$("#city").val(),
//                        "stateProvince":$("#stateProvince").val(),
//                        "postalCode":$("#postalCode").val(),
//                    }),
//                    success: function (data, status) {
//                        loadProfiles();
//                    },
//                    error: function (data, e1, e2) {
//                        alert("failure\n"+data+"\n"+e1+"\n"+e2);
//                    }
//                });
//            });

            $("#paymentSelect_cc").bind("click", function(){
                $("#paymentDisplay_cc").show();
                $("#paymentDisplay_pp").hide();
                $("#paymentDisplay_bc").hide();
            });

            $("#paymentSelect_pp").bind("click", function(){
                $("#paymentDisplay_cc").hide();
                $("#paymentDisplay_pp").show();
                $("#paymentDisplay_bc").hide();
            });

            $("#paymentSelect_bc").bind("click", function(){
                $("#paymentDisplay_cc").hide();
                $("#paymentDisplay_pp").hide();
                $("#paymentDisplay_bc").show();
            });

//            $("#addNewCreditCardToggle").bind("click", function(){
//                $("#addNewCreditCard").toggle();
//            });

        });
    };

    ruckus.pagecontrols.deposit.prototype.unload = function () {
        this.destroyControl();
    };

    return ruckus.pagecontrols.deposit;
});


