<div id="dshl_{lineupId}" class="col-xs-7 cell dshl_cell">
	<div class="col-xs-24">
		<div class="row topBar">
			<div class="col-xs-24 ellipsis name">
                {lineupName}
			</div>
		</div>

		<div class="row upcoming_row">
			<div class="col-xs-12 salary">
				{formattedRemainingSalary}<div class="salaryText">Remaining Salary</div>
			</div>
			<div class="col-xs-12 ellipsis time" id="dshl_starttime_{lineupId}">
                <div class="dshl_cell_upcoming_chart"></div>
                <div class="timeText lineup_cell_chart">until contest starts</div>
                <div class="dshl_cell_upcoming_chart_number time"></div>
                <div class="dshl_cell_upcoming_chart_label timeText"></div>
			</div>
		</div>

        <div class="row active_row">
			<div class="col-xs-24 fpp">
				<div class="fppNumber">{fpp}</div>
				<div class="fppText">Points</div>
			</div>
		</div>
		<div class="row">
			<div class="col-xs-24 timeBar"></div>
		</div>

		<div class="row tableHead">
			<div class="col-xs-4">Pos</div>
			<div class="col-xs-14">Player</div>
			<div class="col-xs-6 fppg">FPPG</div>
		</div>
		{#athletes}
			<div class="row lineupEditRow">
				<div class="col-xs-4 position">{position}</div>
				<div class="col-xs-14 ellipsis">{firstName} {lastName}</div>
				<div class="col-xs-6 points">{fppg}</div>
			</div>
		{/athletes}
		<div class="row edit">
			<div id="dshl_lineup_{lineupId}" class="col-xs-8 dshl_edit editTab" style="cursor:pointer;">
				<div class="icons iconslineup_edit_dark"></div>
				<div class="mainTooltipContainer">
					<div class="insideTooltipContainer">
						<div class="ttBorderTopTriangle"></div>
						<div class="ttInsideTopTriangle"></div>
						Edit your roster anytime before the first games starts.
					</div>
				</div>
			</div>
			<div class="col-xs-8 dshl_edit editTab" style="border-left: 1px solid #ebebeb;">
				<div class="icons iconslineup_switch_light"></div>
				<div class="mainTooltipContainer">
					<div class="insideTooltipContainer">
						<div class="ttBorderTopTriangle"></div>
						<div class="ttInsideTopTriangle"></div>
						COMING SOON: Global swap allows you to remove an athlete from all of your rosters.
					</div>
				</div>
			</div>
			<div class="col-xs-8 dshl_edit editTab" style="border-left: 1px solid #ebebeb;">
				<div class="icons iconslineup_add_light"></div>
				<div class="mainTooltipContainer">
					<div class="insideTooltipContainer">
						<div class="ttBorderTopTriangle"></div>
						<div class="ttInsideTopTriangle"></div>
						Add this roster to additional contests.
					</div>
				</div>
			</div>
		</div>
		<div class="row darkHead">
			<div class="col-xs-12 contests">{totalContests}<div class="contestsText">contest(s)</div></div>
			<div class="col-xs-12 fees">{totalEntryFees}<div class="feesText">entered</div></div>
		</div>
		<div class="contestTable niceScroll" style="overflow-x:hidden">
			{#contests}
				<div class="row contestRow">
					<div id="dshl_contest_{id}_{lineupId}" class="col-xs-18 contestName ellipsis">{line1}</div>
					<div id="dshl_remove_{id}_{lineupId}" class="col-xs-6 dshl_remove leave" style="cursor:pointer;">Leave</div>
					<div id="dshl_lock_{id}_{lineupId}" class="col-xs-6 icons iconslock_white floatRight" style="display:none;"></div>
				</div>
			{/contests}
		</div>
	</div>
</div>
