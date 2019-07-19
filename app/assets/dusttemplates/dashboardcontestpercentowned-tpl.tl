	<div class="col-xs-24 container" style="font-size:10pt;min-height:432px;background-color:#FFFFFF;">
		<div class="row" style="height:348px;">
			<div class="col-xs-24 topBar">
				{percentowned.position} RANKS	
			</div>
			<div class="col-xs-24 tabs">
				<div id="dhcp_taball" class="col-xs-8 tabSelected" style="cursor:pointer;">All
					<div class="mainTooltipContainer">
						<div class="insideTooltipContainer">
							<div class="ttBorderTopTriangle"></div>
							<div class="ttInsideTopTriangle"></div>
							Fantasy performance and ownership % at the selected athlete's position on all rosters in this contest.
						</div>
					</div>
				</div>	
				<div id="dhcp_tabten" class="col-xs-8 tab" style="cursor:pointer;">10%
					<div class="mainTooltipContainer">
						<div class="insideTooltipContainer">
							<div class="ttBorderTopTriangle"></div>
							<div class="ttInsideTopTriangle"></div>
							Performance and % ownership at the selected athlete's position on rosters within +/- 10% of your position.
						</div>
					</div>
				</div>	
				<div id="dhcp_tababove" class="col-xs-8 tab" style="cursor:pointer;">Above
					<div class="mainTooltipContainer">
						<div class="insideTooltipContainer">
							<div class="ttBorderTopTriangle"></div>
							<div class="ttInsideTopTriangle"></div>
							Fantasy performance and ownership % at the selected athlete's position on all rosters currently ahead of you in this contest.
						</div>
					</div>
				</div>	
			</div>
				<div class="col-xs-24 tableContainer niceScroll" style="height:285px;">
					<div id="dhcp_all" class="col-xs-24">
						{#percentowned.all}
						<div class="ownedRow">
							<div class="col-xs-6 points">{fpp}</div>
							<div class="col-xs-14 name ellipsis">{firstInitial} {lastName}</div>
							<div class="col-xs-4 owned">{percentOwned}%<div class="ownedText">owned</div></div>
						</div>
						{/percentowned.all}	
					</div>
					<div id="dhcp_ten" class="col-xs-24" style="display:none;">
						{#percentowned.tenPercent}
						<div class="ownedRow">
							<div class="col-xs-6 points">{fpp}</div>
							<div class="col-xs-14 name ellipsis">{firstInitial} {lastName}</div>
							<div class="col-xs-4 owned">{percentOwned}%<div class="ownedText">owned</div></div>
						</div>
						{/percentowned.tenPercent}	
					</div>
					<div id="dhcp_above" class="col-xs-24" style="display:none;">
						{#percentowned.above}
						<div class="ownedRow">
							<div class="col-xs-6 points">{fpp}</div>
							<div class="col-xs-14 name ellipsis">{firstInitial} {lastName}</div>
							<div class="col-xs-4 owned">{percentOwned}%<div class="ownedText">owned</div></div>
						</div>
						{/percentowned.above}	
					</div>
				</div>
			<div id="dhcp_chat" class="col-xs-24 chat" style="cursor:pointer;">Chat<div class="icons iconsarrow_up" style="position:absolute;bottom:-8px;left:135px;"></div></div>
		</div>
	</div>
