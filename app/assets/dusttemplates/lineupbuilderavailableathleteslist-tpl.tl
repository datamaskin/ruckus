	<div class="col-xs-24 niceScroll" style="overflow-x:hidden;">
		{#athletes}
			<div class="row lbaa_position lbaa_{firstPosition} lbaa_event_{eventId} athleteRow" id="lbaa_athleterow_{id}" style="cursor:pointer;">
				<div class="col-xs-2">{firstPositionDisplay}</div>
				<div class="col-xs-7 ellipsis">{firstNameInitial} {lastName}</div>
				<div class="col-xs-4 lbaa_stat1" id="lbaa_{id}_stat1">{matchup}</div>
				<div class="col-xs-4 lbaa_stat2" id="lbaa_{id}_stat2">{stat2}</div>
				<div class="col-xs-4 lbaa_stat3" id="lbaa_{id}_stat3" style="display:none;">{stat3}</div>
				<div class="col-xs-4 lbaa_stat4" id="lbaa_{id}_stat4" style="display:none;">{stat4}</div>
				<div class="col-xs-4 lbaa_stat5" id="lbaa_{id}_stat5" style="display:none;">{stat5}</div>
				<div class="col-xs-4 lbaa_stat6" id="lbaa_{id}_stat6" style="display:none;">{stat6}</div>
				<div id="lbaa_{id}_salary" class="col-xs-4">{salaryformatted}</div>
				<div id="lbaa_plus_{id}" class="col-xs-2 icons iconsinfo_dark" style="cursor:pointer;"></div>
			</div>	
		{/athletes}
	</div>
