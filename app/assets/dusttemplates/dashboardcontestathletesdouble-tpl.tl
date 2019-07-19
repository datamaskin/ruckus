<div class="col-xs-24" style="font-size:10pt;border:1px solid #cccccc;">
	<div class="middle"></div>
	<div class="row">
		<div class="col-xs-24">
			
			<div class="row">
				<div class="col-xs-11 topLeft ellipsis">
					{lineup1.lineup.user}
				</div>
				<div class="col-xs-2"></div>

				<div class="col-xs-11 topRight ellipsis">
					{lineup2.lineup.user}
				</div>
			</div>	

			<div class="row">
				<div class="col-xs-6 placeLeft ellipsis">
					<div data-socket="la_pos_{lineup1.lineup.entryId}" >{lineup1.lineup.formattedPosition}</div>Place					
				</div>
				<div data-socket="la_fpp_{lineup1.lineup.entryId}" class="col-xs-5 pointsLeft ellipsis" style="text-align:right;padding-right:30px;">
					{lineup1.lineup.fpp}
				</div>
				<div class="col-xs-2">
					vs.			
				</div>
				<div class="col-xs-6 placeRight ellipsis">
					<div data-socket="la_pos_{lineup2.lineup.entryId}">{lineup2.lineup.formattedPosition}</div>Place
				</div>
				<div data-socket="la_fpp_{lineup2.lineup.entryId}" class="col-xs-5 pointsRight ellipsis" style="text-align:right;">
                                        {lineup2.lineup.fpp}
                </div>
			</div>	
			
			<div class="row">
				<div class="col-xs-11" style="border-bottom: 1px solid #ebebeb;">
					<div data-socket="la_lineupTimePercentage_{lineup1.lineup.entryId}" class="row bigTimeBar" style="width:{lineup1.lineup.timePercentage}%;"></div>				
				</div>
				<div class="col-xs-2"></div>

				<div class="col-xs-11" style="border-bottom: 1px solid #ebebeb;margin-left:0;">
					<div data-socket="la_lineupTimePercentage_{lineup2.lineup.entryId}" class="row bigTimeBar" style="width:{lineup2.lineup.timePercentage}%;"></div>				
				</div>
			</div>	


			{#sharedPos}
				<div class="row rowColor">
					<div id="dhca_{athlete1.athleteSportEventInfoId}" class="col-xs-7  dhca_item ellipsis name" style="cursor:pointer;">
						{athlete1.firstInitial} {athlete1.lastName}	
					</div>
					<div data-socket="la_fpp_{athlete1.athleteSportEventInfoId}" class="col-xs-3 playerPointsDoubleLeft" style="padding-right:0;">
						{athlete1.fpp}
					</div>
					<div class="col-xs-5 position" style="z-index:9000;">
						{position}
					</div>
					<div id="dhca_{athlete2.athleteSportEventInfoId}" class="col-xs-7  dhca_item ellipsis name" style="cursor:pointer;padding-left:10px;">
						{athlete2.firstInitial} {athlete2.lastName}
					</div>
					<div data-socket="la_fpp_{athlete2.athleteSportEventInfoId}" class="col-xs-3 playerPointsDoubleRight">
						{athlete2.fpp}
                	</div>

					<div style="width:100%">
						<div class="col-xs-10" style="padding-right:0px;">
							<div data-socket="la_timePercentage_{athlete1.athleteSportEventInfoId}" class="row timeBar" style="width:{athlete1.timePercentage}%;margin-top:4px;">
							</div>			
						</div>
						<div class="col-xs-4">
						</div>
						<div class="col-xs-10" style="padding-right:0px;">
							<div data-socket="la_timePercentage_{athlete2.athleteSportEventInfoId}" class="row timeBar" style="margin-left:-25px;width:{athlete2.timePercentage}%;margin-top:4px;">
							</div>					
						</div>
					</div>	
                </div>

			{/sharedPos}

			{#other}
				<div class="row rowColor">
					<div id="dhca_{athlete1.athleteSportEventInfoId}" class="col-xs-7  dhca_item ellipsis name" style="cursor:pointer;">
						{athlete1.firstInitial} {athlete1.lastName}	
					</div>
					<div data-socket="la_fpp_{athlete1.athleteSportEventInfoId}" class="col-xs-3 playerPointsDoubleLeft" style="padding-right:0;">
						{athlete1.fpp}
					</div>
					<div class="col-xs-5 position" style="z-index:9000;">
						{position}
					</div>
					<div id="dhca_{athlete2.athleteSportEventInfoId}" class="col-xs-7  dhca_item ellipsis name" style="cursor:pointer;padding-left:10px;">
						{athlete2.firstInitial} {athlete2.lastName}
					</div>
					<div data-socket="la_fpp_{athlete2.athleteSportEventInfoId}" class="col-xs-3 playerPointsDoubleRight">
						{athlete2.fpp}
                	</div>

					<div style="width:100%">
						<div class="col-xs-10" style="padding-right:0px;">
							<div data-socket="la_timePercentage_{athlete1.athleteSportEventInfoId}" class="row timeBar" style="width:{athlete1.timePercentage}%;margin-top:4px;">
							</div>	
						</div>
						<div class="col-xs-4">
						</div>
						<div class="col-xs-10" style="padding-right:0px;">
							<div data-socket="la_timePercentage_{athlete2.athleteSportEventInfoId}" class="row timeBar" style="margin-left:-25px;width:{athlete2.timePercentage}%;margin-top:4px;">
							</div>					
						</div>
					</div>	
                </div>

			{/other}

			<div class="line"></div>
			<div id="dhca_sharedplayerscontainer" class="sharedText">SHARED PLAYERS</div>

				{#shared}
					<div class="row" style="height:35px; padding-top:8px">
						<div id="dhca_{athlete1.athleteSportEventInfoId}" class="col-xs-7  dhca_item ellipsis name" style="cursor:pointer;">
							{athlete1.firstInitial} {athlete1.lastName}	
						</div>
						<div data-socket="la_fpp_{athlete1.athleteSportEventInfoId}" class="col-xs-3 playerPointsDoubleLeft" style="cursor:pointer;padding-right:0;">
							{athlete1.fpp}
						</div>
						<div class="col-xs-5 position" style="cursor:pointer;z-index:6000;">
							{position}
						</div>
						<div id="dhca_{athlete2.athleteSportEventInfoId}" class="col-xs-7  dhca_item ellipsis name" style="cursor:pointer;">
							{athlete2.firstInitial} {athlete2.lastName}
						</div>
						<div data-socket="la_fpp_{athlete2.athleteSportEventInfoId}" class="col-xs-3 playerPointsDoubleRight" style="cursor:pointer;">
							{athlete2.fpp}
	                    </div>
	                    
						<div style="width:100%">
							<div class="col-xs-10" style="padding-right:0px;">
								<div data-socket="la_timePercentage_{athlete1.athleteSportEventInfoId}" class="row timeBar" style="width:{athlete1.timePercentage}%;margin-top:6px;">
								</div>	
							</div>
							<div class="col-xs-4">
							</div>
							<div class="col-xs-10" style="padding-right:0px;">
								<div data-socket="la_timePercentage_{athlete2.athleteSportEventInfoId}" class="row timeBar" style="margin-left:-25px;width:{athlete2.timePercentage}%;margin-top:6px;">
								</div>					
							</div>
						</div>
					</div>

				{/shared}

		</div>
	</div>
</div>
