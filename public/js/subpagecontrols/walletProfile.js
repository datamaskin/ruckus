// Author: Scott Gay
define([
        "assets/js/subpagecontrols/base.js",
        "assets/js/libraries/jquery.min.js",
        "assets/js/libraries/dust-core.min.js"
], function (Base) {
        ruckus.subpagecontrols.walletProfile = function (parameters) {
                Base.call(this);
                this.init = function () {
                        this.parameters = parameters;
                };
                this.init();
        };

        ruckus.subpagecontrols.walletProfile.prototype = Object.create(Base.prototype);
        ruckus.subpagecontrols.walletProfile.prototype.load = function () {
            var _this = this;
            this.getContainer();
            this.container.addClass('ruckus-spc-walletProfile');

            this.require_template('walletProfile-tpl');

            _this.wallet = new ruckus.models.wallet({});
            _this.walletProfile = new ruckus.models.walletProfile({});

            function formatExpInput(){
                var year = $("#billingExpYear").val();
                if(year.length > 1){
                    $("#billing-cc-exp").val(
                        $("#billingExpMonth").val() + year.substr(year.length - 2)
                    );
                }
            }

            dust.render('dusttemplates/walletProfile-tpl', {userProfiles:_this.parameters.data}, function (err, out) {
                _this.container.html(out);
                _this.addScrollBars();
                $.each(_this.parameters.data, function(i, val){

                    $("#removeBillingAddress_"+val.id).bind("click", function(){
                        $.ajax({
                            type: "POST",
                            url: "/wallet/removeBillingAddress",
                            contentType: 'application/json',
                            data: JSON.stringify({
                                "authorizeDepositAmount":$("#authorizeDepositAmount_"+val.id).val(),
                                "profile":val.id
                            }),
                            success: function (data, status) {
                                $("#profileRootNode_"+val.id).remove();
                            },
                            error: function (data, e1, e2) {
                                console.log(data)
                                alert("failure\n"+data+"\n"+e1+"\n"+e2)
                            }
                        });
                    });

                    if(val.tokenId){

                        $("#authorizeDepositSubmit_"+val.id).bind("click", function(){
                            if($("#authorizeDepositAmount_"+val.id).val() > 3000){
                                alert("amounts cannot exceed $3,000.");
                            } else {
                                $.ajax({
                                    type: "POST",
                                     url: "/wallet/authorizeDeposit",
                                    contentType: 'application/json',
                                    data: JSON.stringify({
                                        "authorizeDepositAmount":$("#authorizeDepositAmount_"+val.id).val(),
                                        "profile":val.id
                                    }),
                                    success: function (data, status) {
                                        _this.wallet.fetch();
                                    },
                                    error: function (data, e1, e2) {
                                        console.log(data)
                                        alert("failure\n"+data+"\n"+e1+"\n"+e2)
                                    }
                                });
                            }
                        });

                        $("#deleteCreditCard_"+val.id).bind("click", function(){
                            $.ajax({
                                type: "POST",
                                url: "/wallet/deleteCreditCard",
                                contentType: 'application/json',
                                data: JSON.stringify({
                                    "profile":val.id
                                }),
                                success: function (data, status) {
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
                                },
                                error: function (data, e1, e2) {
                                    console.log(data)
                                    alert("failure\n"+data+"\n"+e1+"\n"+e2)
                                }
                            });
                        });
                    } else {

                        $("#billingExpMonth").change(function(){
                            formatExpInput();
                        });

                        $("#billingExpYear").keyup(function(){
                            formatExpInput()
                        });

                    }
                });
            });
        };

        ruckus.subpagecontrols.walletProfile.prototype.unload = function () {
                this.destroyControl();
        };

        return ruckus.subpagecontrols.walletProfile;
});