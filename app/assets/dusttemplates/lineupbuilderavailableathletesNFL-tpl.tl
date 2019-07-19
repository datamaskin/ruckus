<div class="col-xs-24" style="font-size:10pt;">
	<div class="row">
		<div class="col-xs-24 athleteContainer">
			<div class="row">
				<div class="col-xs-12">
					<div class="searchHeader">AVAILABLE PLAYERS</div>
					<div class="playerSearchBoxContainer">
						<input type="textbox" id="lbaa_playersearch" value="Find a player_"></input><div id="lbaa_playersearch_x" class="x icons iconsx_light"></div>
					</div>
				</div>
				<div class="col-xs-12 playerListOptions">
					<div class="autoAdvance">
						<!--<div id="lbaa_autoAdvance" class="autoAdvanceCheck"></div><label for="lbaa_autoAdvance" class="autoAdvanceLabel"> Auto Advance Position</label>-->
					</div>
				</div>
			</div>

			<div class="row tabs">
				<div id="lbaa_all" class="col-xs-2 lbaa_positiontab tabSelected" style="cursor:pointer;">All</div>
				<div id="lbaa_qb" class="col-xs-2 lbaa_positiontab tab" style="cursor:pointer;">QB</div>
				<div id="lbaa_rb" class="col-xs-2 lbaa_positiontab tab" style="cursor:pointer;">RB</div>
				<div id="lbaa_wr" class="col-xs-2 lbaa_positiontab tab" style="cursor:pointer;">WR</div>
				<div id="lbaa_te" class="col-xs-2 lbaa_positiontab tab" style="cursor:pointer;">TE</div>
				<div id="lbaa_fx" class="col-xs-2 lbaa_positiontab tab" style="cursor:pointer;">FX</div>
				<div id="lbaa_def" class="col-xs-2 lbaa_positiontab tab" style="cursor:pointer;">DEF</div>
				<!--<div id="lbaa_k" class="col-xs-2 lbaa_positiontab tab" style="cursor:pointer;">K</div>-->
				<div class="col-xs-4"></div>
				<div id="lbaa_sliders" class="col-xs-3 tab" style="cursor:pointer;">VICTRON</div>
			</div>
			<div id="lbaa_header" class="row tableHead">
				<div class="col-xs-2" id="lbaa_posheader" style="cursor:pointer;padding-left:10px;">P</div>
				<div class="col-xs-7" id="lbaa_nameheader" style="cursor:pointer;padding-left:10px;">Name</div>
				<div class="col-xs-4 lbaa_statheader statHeader" id="lbaa_stat1header" style="cursor:pointer;"></div>
				<div class="col-xs-4 lbaa_statheader statHeader" id="lbaa_stat2header" style="cursor:pointer;"></div>
				<div class="col-xs-4 lbaa_statheader statHeader" id="lbaa_stat3header" style="display:none;cursor:pointer;"></div>
				<div class="col-xs-4 lbaa_statheader statHeader" id="lbaa_stat4header" style="display:none;cursor:pointer;"></div>
				<div class="col-xs-4 lbaa_statheader statHeader" id="lbaa_stat5header" style="display:none;cursor:pointer;"></div>
				<div class="col-xs-4 lbaa_statheader statHeader" id="lbaa_stat6header" style="display:none;cursor:pointer;"></div>
				<div class="col-xs-4" id="lbaa_salaryheader" style="cursor:pointer;">Salary</div>
				<div class="col-xs-2"></div>
			</div>
			<div id="lbaa_sliderslist" style="display:none;">
			</div>
			<div id="lbaa_athletelist" class="row niceScroll " style="height:455px; width:583px; overflow-y:hidden; overflow-x:hidden; border-bottom:1px solid #dbdbdb">
				<div class="col-xs-24">
				{#athletes}
					<div class="row lbaa_position lbaa_{firstPosition} lbaa_event_{eventId} athleteRow" id="lbaa_athleterow_{id}">
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
			</div>
			<div id="lbaa_exportAthletesCSV" class="exportAthletesCSV noLink">Coming soon: Export CSV</div>
		</div>
	</div>
</div>
