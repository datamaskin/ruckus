{#userProfiles}
<div style="padding-bottom:15px;" id="profileRootNode_{id}">
        <div>
            <div><input type="button" class="btnA" value="Remove billing address" id="removeBillingAddress_{id}"/></div>
            <div class="sexyLine"></div>
        </div>
        <div>
            {?tokenId}
                <div>
                    {ccType} ending in "{ccNumber}" (exp: {ccExpMonth}/{ccExpYear})
                    <input type="button" value="Delete credit card" id="deleteCreditCard_{id}"/>
                </div>

                <div>Amount to deposit:</div>
                <div>
                    <input type="text" value="10.00" id="authorizeDepositAmount_{id}"/>
                    <input type="button" value="Submit" id="authorizeDepositSubmit_{id}"/>
                </div>
            {/tokenId}
            {^tokenId}
                <div class="col-xs-24">
                    <h2></h2>
                    <form method="POST" action="{formUrl}" autocomplete="off" id="payment_form">
                        <div><input autocomplete="off" name="billing-first-name" placeholder="FIRST NAME"/></div>
                        <div><input autocomplete="off" name="billing-last-name" placeholder="LAST NAME"/></div>
                        <div><input autocomplete="off" name="billing-address1" placeholder="ADDRESS #1"/></div>
                        <div><input autocomplete="off" name="billing-address2" placeholder="ADDRESS #2"/></div>
                        <div><input autocomplete="off" name="billing-city" placeholder="CITY"/></div>
                        <div><input autocomplete="off" name="billing-state" placeholder="STATE"/></div>
                        <div><input autocomplete="off" name="billing-postal" placeholder=""/></div>
                        <div>
                            Card number:
                            <input autocomplete="off" id="billing-cc-number" maxlength="19" name="billing-cc-number" size="19" />
                        </div>
                        <div>
                            <div>
                                Exp. month:
                                <input type="text" id="billingExpMonth" maxlength="2" style="width:2em;"/>
                                year:
                                <input type="text" id="billingExpYear" maxlength="2" style="width:2em;"/>
                                <input type="hidden" id="billing-cc-exp" name="billing-cc-exp"/>
                            </div>
                            <div>
                                Security Code:
                                <input autocomplete="off" id="billing-cc-cvv" maxlength="4"
                                       name="billing-cc-cvv" size="4" />
                                <input type="submit" />
                            </div>
                        </div>
                    </form>
                </div>
            {/tokenId}
        </div>
    </div>
</div>
{/userProfiles}