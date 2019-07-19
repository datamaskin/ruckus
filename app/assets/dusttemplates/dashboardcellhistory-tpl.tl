<div class="col-xs-6 dboardbox">
	<div id="dshm_cell_{contestId}_{lineupId}" class="col-xs-24 dshm_cell">
		<div class="row finishRow">
			<div class="col-xs-12 finish">Finished!</div>
			<div class="col-xs-12 finishTime">{formattedStartTime}</div>
		</div>
		<div class="row finishBox">
			<div class="col-xs-12 place" style="text-align:center;"><div class="position">{formattedPosition}</div>
			out of<br>{currentEntries}</div>

			<div class="col-xs-12 points" style="text-align:center;"><div class="fpp">{fpp}</div>
			points</div>

		</div>
		<div class="row">
                        <div id="projected_{contestId}_{lineupId}" class="col-xs-8 projected projectedHistory" style="text-align:center;">{formattedPayout}
                        <div class="projectText">payout</div></div>
                </div>
		<div class="row bottom">
			<div class="col-xs-24 tournament" style="text-align:center;">{line1}<br>{line2}</div>
		</div>
	</div>
</div>
