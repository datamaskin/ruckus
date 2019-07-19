<div class="col-xs-24" style="font-size:10pt;">
	<div class="row">
		<div class="col-xs-24 athleteContainer" style="background-color:#ffffff; border: 1px solid #cccccc;">
			<div class="row">
				<div class="col-xs-24 searchHeader">AVAILABLE PLAYERS</div>
			</div>
			<div class="row">
				<div class="col-xs-24">
					<div style="float:left;"><input type="textbox" id="lbaa_playersearch" placeholder=" find a player..."></div>
					<div id="lbaa_playersearch_x" class="x">x</div>
					<div style="clear:both;"></div>
				</div>
			</div>
			<div class="row tabs">
				<div id="lbaa_all" class="col-xs-2 lbaa_positiontab tabSelected" style="cursor:pointer;">All</div>
				<div id="lbaa_p" class="col-xs-2 lbaa_positiontab tab" style="cursor:pointer;">P</div>
				<div id="lbaa_c" class="col-xs-2 lbaa_positiontab tab" style="cursor:pointer;">C</div>
				<div id="lbaa_fb" class="col-xs-2 lbaa_positiontab tab" style="cursor:pointer;">1B</div>
				<div id="lbaa_sb" class="col-xs-2 lbaa_positiontab tab" style="cursor:pointer;">2B</div>
				<div id="lbaa_tb" class="col-xs-2 lbaa_positiontab tab" style="cursor:pointer;">3B</div>
				<div id="lbaa_ss" class="col-xs-2 lbaa_positiontab tab" style="cursor:pointer;">SS</div>
				<div id="lbaa_of" class="col-xs-2 lbaa_positiontab tab" style="cursor:pointer;">OF</div>
				<div class="col-xs-5"></div>
				<div id="lbaa_sliders" class="col-xs-3" style="cursor:pointer;">VICTRON</div>
			</div>
			<div id="lbaa_header" class="row tableHead">
				<div class="col-xs-2" id="lbaa_posheader statHeader" style="cursor:pointer;padding-left:10px;">P</div>
				<div class="col-xs-7" id="lbaa_nameheader statHeader" style="cursor:pointer;padding-left:10px;">Name</div>
				<div class="col-xs-4 lbaa_statheader statHeader" id="lbaa_stat1header" style="cursor:pointer;"></div>
				<div class="col-xs-4 lbaa_statheader statHeader" id="lbaa_stat2header" style="cursor:pointer;"></div>
				<div class="col-xs-4 lbaa_statheader statHeader" id="lbaa_stat3header" style="display:none;cursor:pointer;"></div>
				<div class="col-xs-4 lbaa_statheader statHeader" id="lbaa_stat4header" style="display:none;cursor:pointer;"></div>
				<div class="col-xs-4 lbaa_statheader statHeader" id="lbaa_stat5header" style="display:none;cursor:pointer;"></div>
				<div class="col-xs-4 lbaa_statheader statHeader" id="lbaa_stat6header" style="display:none;cursor:pointer;"></div>
				<div class="col-xs-4" id="lbaa_salaryheader" style="cursor:pointer;">Salary</div>
				<div class="col-xs-2"></div>
			</div>
			<div id="lbaa_sliderslist" style="display:none;"></div>
			<div id="lbaa_athletelist" class="row">
				<div class="col-xs-24 niceScroll" style="height:505px;overflow-y:auto;">
				{#athletes}
					<div class="row lbaa_position lbaa_{firstPosition} lbaa_event_{eventId} athleteRow" id="lbaa_athleterow_{id}" style="cursor:pointer;">
						<div class="col-xs-2">{firstPositionDisplay}</div>
						<div class="col-xs-7 ellipsis">{firstNameInitial} {lastName}</div>
						<div class="col-xs-4 lbaa_stat1" id="lbaa_{id}_stat1">{stat1}</div>
						<div class="col-xs-4 lbaa_stat2" id="lbaa_{id}_stat2">{stat2}</div>
						<div class="col-xs-4 lbaa_stat3" id="lbaa_{id}_stat3" style="display:none;">{stat3}</div>
						<div class="col-xs-4 lbaa_stat4" id="lbaa_{id}_stat4" style="display:none;">{stat4}</div>
						<div class="col-xs-4 lbaa_stat5" id="lbaa_{id}_stat5" style="display:none;">{stat5}</div>
						<div class="col-xs-4 lbaa_stat6" id="lbaa_{id}_stat6" style="display:none;">{stat6}</div>
						<div id="lbaa_{id}_salary" class="col-xs-4">{salaryformatted}</div>
						<div id="lbaa_plus_{id}" class="col-xs-2 plus icons iconsplus_gray" style="cursor:pointer;"></div>
					</div>
				{/athletes}
				</div>
			</div>
		</div>
	</div>
</div>
