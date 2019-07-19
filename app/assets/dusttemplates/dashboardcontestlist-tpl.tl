<div class="col-xs-24 listContainer niceScroll">
	<div class="row">
		<div class="col-xs-24 listBox niceScroll">
			{#contests}
				<div id="dhcl_{contestId}" class="col-xs-24 dhcl_item item">
					<div class="row">
						<div class="col-xs-12 left" style="text-align:center;">
							<div data-socket="cont_position_{contestId}_{lineupId}" class="position">{formattedPosition}</div>
							<div class="outOf">/{currentEntries}</div>
						</div>

						<div class="col-xs-12 right" style="text-align:center;">
							<div data-socket="cont_fpp_{contestId}_{lineupId}" class="fantasyPoints">{fpp}</div>
							<div class="pts">pts.</div>
						</div>
					</div>
					<div class="row" style="background-color:#474747;color:#ffffff;font-size:10px;text-align:center;padding-top:16px;padding-bottom:16px;">
						<div class="col-xs-24">
							{league} {formattedBuyinAmount} in <br>{currentEntries} {contestType.name}
						</div>
					</div>
					<div class="row" style="background-color:#9a9a9a;">
						<div data-socket="cont_timepercentage_{contestId}_{lineupId}" style="width:{timePercentage}%;background-color:#7cdfa4;height:5px;"></div>
					</div>
				</div>
			{/contests}
		</div>
	</div>
</div>
