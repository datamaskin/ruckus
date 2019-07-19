<div class="col-xs-24" style="font-size:10pt;">
    <div class="row">
        <h2>Wallet</h2>
        <div>USD: {wallet.usd}</div>
        <div>Loyalty points: {wallet.loyalty}</div>
        <div>Bonuses:</div>
        {#wallet.bonuses}
        <ul>
            <li>{type} = {amount}</li>
        </ul>
        {/wallet.bonuses}
    </div>
    <div class="row">
        <h2>Cash out bitcoin</h2>
        <div>Amount:</div>
        <div><input name="withdrawalAmount" id="withdrawalAmount"/></div>
        <div>Bitcoin address</div>
        <div><input name="withdrawalBitcoinAddress" id="withdrawalBitcoinAddress"/></div>
        <div><button name="withdrawalSubmit" id="withdrawalSubmit">Submit</button></div>
    </div>
</div>
