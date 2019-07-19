<div class="col-xs-24" style="font-size:10pt;background-color:#ffffff;padding:0;">
		<div class="col-xs-8 leftSide">
			<div class="row">
				<div class="col-xs-24 topBarEntries">YOUR LINEUP</div>
			</div>	
			<div class="lineupTable">	
				<div class="row tableHead">
					<div class="col-xs-3">Pos</div>
					<div class="col-xs-12">Player</div>
					<div class="col-xs-9">FPPG</div>
				</div>

				{#selected}
					<div class="row lineupEditRow">
						<div class="col-xs-3">{firstPositionDisplay}</div>
						<div class="col-xs-12 ellipsis">{firstName} {lastName}</div>
						<div class="col-xs-9">{ffpg}</div>
					</div>
				{/selected}
			</div>
			<div class="row">
				<div id="dshl_returntoedit" class="col-xs-24 returnButton" style="cursor:pointer;">Return to edit</div>
			</div>	
		</div>

		<div class="col-xs-16 rightSide">
                <div class="col-xs-24 topBarEntries">CONTEST ENTRIES</div>
                <div class="contestBox">
					{#lineup.contests}
						<div class="row">
							<div class="col-xs-2">
								<div id="dshl_contest_{id}" class="dshl_contest checkcircle" style="cursor:pointer;"></div>
							</div>
							<div class="col-xs-22 contestLine">{line1}</div>
						</div>
					{/lineup.contests}
				</div>
			<div class="row">
				<div id="dshl_submitchanges" class="col-xs-24 submitButton" style="cursor:pointer;">Submit</div>
			</div>	
		</div>
</div>
