<div class="col-xs-6 dboardbox">
	<div id="dshm_cell_{contestId}_{lineupId}" class="col-xs-24 dshm_cell">
		<div class="row">
			<div class="col-xs-8 pay" style="text-align:center;">current<div data-socket="payout_{contestId}_{lineupId}">{formattedPayout}</div></div>
		</div>
		<div class="row square">
			<div class="col-xs-12 place" style="text-align:center;"><div data-socket="position_{contestId}_{lineupId}" class="position">{formattedPosition}</div>out of<br>{currentEntries}<div class="icons iconsarrow_up_light"></div><div class="icons iconsarrow_down_light"></div></div>


			<div class="col-xs-12 points" style="text-align:center;"><div data-socket="fpp_{contestId}_{lineupId}" class="fpp">{fpp}</div>points<div class="plus">+</div><div class="minus">-</div></div>

		</div>
		<div class="row">
            <div class="col-xs-8 projected" style="text-align:center;"><div data-socket="projpayout_{contestId}_{lineupId}">{formattedProjectedPayout}</div>
            <div class="projectText">projected</div></div>
                </div>
		<div class="row bottom">
			<div class="col-xs-24 tournament" style="text-align:center;">{line1}<br>{line2}</div>
		</div>
		<div class="row" style="background-color:#9A9A9A;">
			<div class="timeBar" data-socket="timepercentage_{contestId}_{lineupId}" style="width:{timePercentage}%;">&nbsp;</div>
		</div>
	</div>
</div>
