<div class="col-xs-24">
	<div class="row">
		<div class="col-xs-24">
			{#list}
				<div id="dhcr_rank_{entryId}" class="row dhcr_rankrow dashboardcontestranksrow">
					<div class="col-xs-3"> {pos}.</div>
					<div id="dhcr_money_{entryId}" class="col-xs-7">{user}</div>
					<div data-socket="r_unitsRemaining_{entryId}" class="col-xs-5">{unitsRemaining}</div>
					<div data-socket="r_fpp_{entryId}" class="col-xs-4">{fpp}</div>
					<div class="col-xs-5 in_the_money">{prize}</div>
				</div>	
			{/list}	
		</div>
	</div>
</div>
