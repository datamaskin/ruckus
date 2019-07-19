// Author: Scott Gay
define([
    "assets/js/pagecontrols/base.js",
    "assets/js/libraries/jquery.min.js",
	"assets/js/models/depositbitcoin.js",
    "assets/js/models/wallet.js"
], function (Base) {
    ruckus.pagecontrols.withdrawl = function (parameters) {
        Base.call(this);
        this.init = function () {
            var _this = this;
            this.parameters = parameters;
        };
        this.init();
    };

    ruckus.pagecontrols.withdrawl.prototype = Object.create(Base.prototype);

    ruckus.pagecontrols.withdrawl.prototype.load = function () {
        var _this = this;
        this.getContainer();
        this.container.addClass('ruckus-pc-withdrawl');

        this.require_template('withdrawl-tpl');

        $.when(
            $.get('/wallet', function (data, status) {})
        ).then(function(wallet){
            wallet = JSON.parse(wallet).payload;
            wallet.usd = _this.formatMoney(wallet.usd);
            dust.render('dusttemplates/withdrawl-tpl',{wallet:wallet},function(err, out){
                _this.container.html(out);
		_this.addScrollBars();
                $("#withdrawalSubmit").bind("click", function(){
                    console.log($("#withdrawalAmount").val());
                    console.log($("#withdrawalBitcoinAddress").val());
                    $.ajax({
                        type: "POST",
                        url: "/wallet/bitcoinWithdrawal",
                        contentType: 'application/json',
                        data: JSON.stringify({
                            "withdrawalAmount":$("#withdrawalAmount").val(),
                            "withdrawalBitcoinAddress":$("#withdrawalBitcoinAddress").val()
                        }),
                        success: function (data, status) {
                            alert("success");
                        },
                        error: function (data, e1, e2) {
                        console.log(data);
                            alert("failure\n"+data+"\n"+e1+"\n"+e2);
                        }
                    });
                });
            });
        });
    };

    ruckus.pagecontrols.withdrawl.prototype.unload = function () {
        this.destroyControl();
    };

    return ruckus.pagecontrols.withdrawl;
});


