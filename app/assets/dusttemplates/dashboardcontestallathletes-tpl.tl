<div class="col-xs-24" style="font-size:10pt;">
	<div class="row">
		<div class="col-xs-24">
			{#data}
				<div class="col-xs-6 athleteContainer">
					<div class="col-xs-24 athleteBox">
						<div class="row topRows">
							<div class="col-xs-10 athleteImage" style="padding:0;">
								<img src="{image}-70x70.png" style="width:70px;height:70px;"/>
							</div>
							<div class="allAthletesTeamLogo">
								<img class="loRes" src="https://dm63aeeijtc75.cloudfront.net/nfl/athletedash/{matchup.athleteTeamId}-200x70.png"/>
								<img class="highRes" src="https://dm63aeeijtc75.cloudfront.net/nfl/athletedash/{matchup.athleteTeamId}-400x140.png"/>
							</div>
							<div class="col-xs-14 name ellipsis">
								{firstName}<br>{lastName}</div>
							<div class="col-xs-14 position">{position} #{uniform}</div>
						</div>
						<div class="row infoBar">
							<div data-socket="ath_fpp_{athleteSportEventInfoId}"class="col-xs-9 points">{fpp}</div>
								<div class="col-xs-3 pointsText">points</div>
							<div class="col-xs-7 matchup">
								<div style="float:left;margin-top:-10px;width:30px;">{matchup.awayTeam}</div>
								<div style="float:left;margin-top:-55px">@{matchup.homeTeam}</div>
							</div>
							<div class="col-xs-3" style="height:70px;text-align:right;">
								<div data-socket="ath_score_{matchup.awayId}" id="sevt_{matchup.awayId}" style="float:left;margin-top:-10px;font-weight:100;">{matchup.awayScore}</div>
								<div data-socket="ath_score_{matchup.homeId}" id="sevt_{matchup.homeId}" style="float:left;margin-top:-55px;font-weight:100;">{matchup.homeScore}</div>
								<div style="clear:both;"></div>
							</div>
							<div data-socket="ath_indicator_{athleteSportEventInfoId}" class="col-xs-4 {indicatorClass} indicator">
							</div>
						</div>
						<div class="row" style="background-color:#9A9A9A;">
							<div id="dhao_timepercentage_{athleteSportEventInfoId}" data-socket="ath_timePercentage_{athleteSportEventInfoId}" style="width:{formattedTimePercentage}%" class="progress_bar player_progress_bar"></div>
						    </div>
						<div class="col-xs-24 nav">
							<div id="dhao_tabplayerfeed_{athleteSportEventInfoId}" class="tabSelected col-xs-6 dhao_tabplayerfeed dhao_tab_{athleteSportEventInfoId}" style="cursor:pointer;">
								<div style="margin-top:6px;" class="icons iconsplayer_dark"></div>
								<div class="icons iconsplayer_light"></div>
							</div>

							<div id="dhao_tabplayerexposure_{athleteSportEventInfoId}" class="col-xs-6 dhao_tabplayerexposure dhao_tab_{athleteSportEventInfoId} tab" style="cursor:pointer;">
								<div style="height:20px;margin-top:6px;margin-left:6px;" class="icons iconsexposure_dark"></div>
								<div style="height:20px;margin-left:4px;" class="icons iconsexposure_light"></div>
							</div>

							<div id="dhao_tabplayercontests_{athleteSportEventInfoId}" class="col-xs-6 dhao_tabplayercontests dhao_tab_{athleteSportEventInfoId} tab" style="cursor:pointer;padding-top:10px;">
								<div class="icons iconsvs_dark"></div>
								<div style="margin-top:10px;" class="icons iconsvs_light"></div>
							</div>

							<div id="dhao_tabstats_{athleteSportEventInfoId}" class="col-xs-6 dhao_tabstats dhao_tab_{athleteSportEventInfoId} tab" style="cursor:pointer;">
								<div style="margin-top:5px;" class="icons iconsstats_dark"></div>
								<div class="icons iconsstats_light"></div>
							</div>
						</div>
						<div id="dhao_playerfeed_{athleteSportEventInfoId}" class="row">
							<div data-socket="ath_timeline_{athleteSportEventInfoId}" class="col-xs-24 niceScroll" style="height:181px;background-color:#474747;">
								{#timeline}
									<div class="row">
										<div class="col-xs-8 fpchange">
											{fpChange}
										</div>
										<div class="col-xs-16 description">
											{description}
										</div>
									</div>
									<div class="row timestamp">
										<div class="col-xs-24">
											<span>{formattedTimestamp}</span>
										</div>
									</div>
								{/timeline}
							</div>
						</div>
						<div id="dhao_playerexposure_{athleteSportEventInfoId}" class="row" style="display:none;">
							<div class="col-xs-24 niceScroll" style="height:181px;background-color:#474747;">
								<div class="row">
									<div class="col-xs-24 topRow">
										<div class="amount col-xs-8">{exposure.formattedTotalExposure}</div><div class="text col-xs-16"> out of {exposure.formattedTotalEntryFees}</div>
									</div>
								</div>
								<div class="row">
									<div class="col-xs-24 topRow">
										<div class="amount col-xs-8">{exposure.contestsEntered}</div><div class="text col-xs-16"> out of {exposure.totalContests} contests</div>
									</div>
								</div>
								<div class="row tableHead">
									<div class="col-xs-12">
										game type
									</div>
									<div class="col-xs-5">
										$
									</div>
									<div class="col-xs-3">
										entries
									</div>
								</div>
								{#exposure.contestTypes}
									<div class="row tableRow">
                                        <div class="col-xs-12 tableGreen ellipsis">
											{contestName}
                                        </div>
                                        <div class="col-xs-5 tableData">
											{formattedEntryFees}
                                        </div>
                                        <div class="col-xs-3 tableData">
											{numEntered}
                                        </div>
                                    </div>
								{/exposure.contestTypes}
							</div>
						</div>
						<div id="dhao_playercontests_{athleteSportEventInfoId}" class="row playercont" style="display:none;">
							 <div class="row tableHead" style="margin-left:0;">
								<div class="col-xs-14">
									Contest
								</div>
								<div class="col-xs-8">

									Place
								</div>
						</div>

                        <div class="col-xs-24 niceScroll" style="height:158px;background-color:#474747;">
                                {#ranks}
									<div class="row tableRow">
                                        <div id="dhao_contest_{contestId}_{contestState}" class="col-xs-14 tableGreen ellipsis contest_entry">{contestName}</div>
                                        <div class="col-xs-8 tableData">{formattedRank}/{currentEntries}</div>
                                    </div>
								{/ranks}
                         </div>
                         </div>
						<div id="dhao_stats_{athleteSportEventInfoId}" class="row" style="display:none;">
							<div class="row tableHead" style="margin-left:0;">
								<div class="col-xs-12">
									Type
								</div>
								<div class="col-xs-5">
									Stats
								</div>
								<div class="col-xs-5">
									Points
								</div>
							</div>

							<div class="col-xs-24 niceScroll" style="height:158px;background-color:#474747;">
								{#stats}
									<div class="row tableRow">
										<div class="col-xs-12 tableData ellipsis">
											{name}
										</div>
										<div data-socket="ath_statamount_{id}_{athleteSportEventInfoId}" class="col-xs-5 tableBold">
											{amount}
										</div>
										<div data-socket="ath_statfpp_{id}_{athleteSportEventInfoId}" class="col-xs-5 tableBold">
											{fpp}
										</div>
									</div>
								{/stats}
							</div>
						</div>
					</div>
				</div>
			{/data}
		</div>
	</div>
</div>
