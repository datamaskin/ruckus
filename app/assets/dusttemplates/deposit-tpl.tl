<div class="col-xs-24 ruckus-pc-deposit" style="font-size:10pt;">
    <div class="col-xs-11">
        <h1>DEPOSIT</h1>
        <p class="headText">100% deposit bonus up to $1,000! Deposit now and receive your bonus.</p>

        <div class="col-xs-8 first">
            <div class="moneyBox">
                <div class="amount">$25</div>
                <div class="desc">$25 Bonus Free</div>
                <div class="button">SELECT</div>
            </div>
        </div>
        <div class="col-xs-8">
            <div class="moneyBox">
                <div class="amount">$100</div>
                <div class="desc">$100 Bonus Free</div>
                <div class="button">SELECT</div>
            </div>
        </div>
        <div class="col-xs-8">
            <div class="moneyBox">
                <div class="amount"><input /></div>
                <div class="desc">$ Bonus Free</div>
                <div class="button">SELECT</div>
            </div>
        </div>
    </div>
<div class="col-xs-2"><div class="vdivide"></div></div>
    <div class="col-xs-11 payment">
        <h1>PAYMENT INFO</h1>
        <ul class="inline">
            <li>
                <a id="paymentSelect_cc" class="selected"><div class="circleSelect"></div>CREDIT CARD</a>
            </li>
            <li>
                <a id="paymentSelect_pp"><div class="circleSelect"></div>PAYPAL</a>
            </li>
            <li>
                <a id="paymentSelect_bc"><div class="circleSelect"></div>BITCOIN</a>
            </li>
        </ul>
        <div class="clearfix"></div>
        <div class="row" id="paymentDisplay_pp" style="display:none">
            <h2>PAYPAL</h2>
            <div>
                <form action="/paypal/start" method="post" target="_top">
                    <div>Amount to deposit</div>
                    <div><input type="text" name="amount"/></div>
                    <div>
                        <input type="image"
                           src="https://www.paypalobjects.com/webstatic/mktg/logo/bdg_now_accepting_pp_2line_w.png"
                           border="0"
                           name="submit"
                           alt="PayPal - The safer, easier way to pay online!">
                    </div>
                </form>
            </div>
        </div>
        <div class="row" id="paymentDisplay_cc" style="display:block">
            <div id="addNewCreditCard" class="address">
                <h2>BILLING ADDRESS</h2>
                <iframe src="/wallet/getCamsForm" style="height:350px; width:399px; border:0px"></iframe>
                <div id="walletProfilesContainer"></div>
            </div>
        </div>
        <div class="row" id="paymentDisplay_bc" style="display:none">
            <h2>BITCOIN</h2>
            <iframe src="https://coinbase.com/inline_payments/{buttonCode}"
                    style="width: 500px; height: 160px; border: none; box-shadow: 0 1px 3px rgba(0,0,0,0.25); overflow: hidden;"
                    scrolling="no" allowtransparency="true" frameborder="0"></iframe>
        </div>
    </div>
</div>
