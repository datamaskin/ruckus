<div class="col-xs-24" style="font-size:10pt;">
	<div class="row">
		<div class="col-xs-24 athleteContainer">
			<div class="row topBar">
				<div class="col-xs-8 user ellipsis">{lineup.lineup.user}</div>
				<div class="col-xs-8 place" style="text-align:center;"><div data-socket="la_pos_{lineup.lineup.entryId}">{lineup.lineup.formattedPosition}</div>
					<div class="placeText">place</div>
				</div>
				<div class="col-xs-8 points" style="text-align:right;"><div data-socket="la_fpp_{lineup.lineup.entryId}">{lineup.lineup.fpp}</div>
					<div class="pointsText">points</div>
				</div>

			</div>

			<div class="row" style="background-color:#ebebeb;">

			<div data-socket="la_lineupTimePercentage_{lineup.lineup.entryId}" class="row bigTimeBar" style="width:{lineup.lineup.timePercentage}%;margin-left:0;"></div>
			</div>
			{#athletes}
				<div id="dhca_{athleteSportEventInfoId}" class="row rowColor dhca_item" style="cursor:pointer;">
					<div class="col-xs-2 position">{position}</div>
					<div class="col-xs-1 dots">
						<div data-socket="la_indicator_{athleteSportEventInfoId}" class="{indicatorClass}"></div>
					</div>
					<div class="col-xs-6 ellipsis name">{firstInitial} {lastName}</div>
					<div data-socket="la_desc_{athleteSportEventInfoId}" class="col-xs-6 ellipsis desc">{desc}</div>
					<div class="col-xs-5 ellipsis matchup">{matchup}</div>
					<div data-socket="la_fpp_{athleteSportEventInfoId}" class="col-xs-4 playerPoints">{fpp}</div>
					<div data-socket="la_timePercentage_{athleteSportEventInfoId}" class="col-xs-24 timeBar" style="width:{timePercentage}%; padding:0;"></div>
				</div>
			{/athletes}
			<div class="row">
				<div class="col-xs-24 legend">
					<div class="col-xs-1"><div class="dot"></div></div>
					<div class="col-xs-5">Off field</div>
					<div class="col-xs-1"><div class="dotPlay"></div></div>
					<div class="col-xs-5">On Field</div>
					<div class="col-xs-1"><div class="dotRedzone"></div></div>
					<div class="col-xs-5">Redzone</div>
				</div>
			</div>
		</div>
	</div>
</div>
