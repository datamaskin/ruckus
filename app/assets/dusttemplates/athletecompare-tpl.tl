<div class="col-xs-24 playerContainer">
	<div class="row">
		<div class="col-xs-24">
			<div class="col-xs-24">
				<div class="row topRow">
					<div class="col-xs-12 image"><img src="{player.image}-100x100.png" style="width:100px;height:100px"/></div>
					<div class="compareTeamLogo">
						<img class="loRes" src="https://dm63aeeijtc75.cloudfront.net/nfl/compare/{player.teamId}-230x100.png"/>
						<img class="highRes" src="https://dm63aeeijtc75.cloudfront.net/nfl/compare/{player.teamId}-460x200.png"/>
					</div>
					<div class="col-xs-12 name ellipsis">
						<div class="row"><div class="col-xs-24 ellipsis">{player.firstName}</div></div>
						<div class="row"><div class="col-xs-24 ellipsis">{player.lastName}</div></div>
						<div class="row position">
						<div class="col-xs-12">#{player.number}</div>
						<div class="col-xs-12">{player.ppos}</div>

						</div>
					</div>
				</div>
				<div class="row salaryRow">
					<div class="col-xs-12 status">{athletecompare.injuryStatus}</div>
					<div class="col-xs-12 salary">{player.salaryformatted}<div class="salaryText">Salary</div>
					</div>
				</div>

				<div id="lbsa_comparenormal_{player.id}">
					<div class="row nav">
						<div id="lbsa_comparetaba_{player.id}" class="col-xs-8 tabSelected" style="cursor:pointer;">
							<div style="height:15px;margin-left:15px;margin-top:3px;" class="icons iconsplayer_light"></div></div>
						<div id="lbsa_comparetabb_{player.id}" class="col-xs-8 tab" style="cursor:pointer;">
							<div style="height:15px;margin-left:15px;margin-top:3px;" class="icons iconsstats_dark"></div></div>
						<div id="lbsa_comparetabc_{player.id}" class="col-xs-8 tab" style="cursor:pointer;">
							<div style="height:15px;margin-left:15px;margin-top:3px;" class="icons iconstwitter_dark"></div></div>
					</div>
					<div id="lbsa_comparea_{player.id}" class="col-xs-24 numberBox">
						<div class="col-xs-24 number">{athletecompare.fppg}<div class="text">Fantasy Points per Game</div></div>
						<div class="col-xs-24 number">{athletecompare.rank.place}<div class="text">vs. all other {player.ppos}s</div></div>
						<!--<div class="col-xs-24 number">{athletecompare.percentOwned}<div class="text">% owned in previous week</div></div>-->
						<div class="col-xs-24 number">{athletecompare.formatteddefenseVsPosition}<div class="text">opponent rank vs. {player.ppos}</div></div>
					</div>
					<div id="lbsa_compareb_{player.id}" class="col-xs-24 averageTable niceScroll" style="display:none;">
						<div class="col-xs-24 avTitle">5-game averages</div>
						{#athletecompare.averages}
							<div class="col-xs-24 averageRow">
								<div class="col-xs-18 ellipsis">{name}</div>
								<div class="col-xs-6 avValue">{value}</div>
							</div>
						{/athletecompare.averages}
					</div>
					<div id="lbsa_comparec_{player.id}" class="col-xs-24" style="display:none;">
						
						<div class="col-xs-24 twitterBackground">
							<div class="twitterComingSoon">
								<p class="comingSoonText">Twitter Coming Soon</p>
							</div>
						</div>
						
					</div>
				</div>
				<div class="row" id="lbsa_compareoverlay_{player.id}" style="display:none;">				
					
					<div class="replace" id="lbsa_comparereplace_{player.id}" style="cursor:pointer;">
						<div class="replaceIcon icons iconsreplace_light"></div>
						<div class="replaceText">REPLACE</div>
					</div>
					<div class="cancel" id="lbsa_comparecancel_{player.id}" style="cursor:pointer;">
						<div class="cancelIcon icons iconsremove_light"></div>
						<div class="cancelText">CANCEL</div>
					</div>
					
				</div>
				<div class="row">
					<div class="col-xs-12 selectContainer" id="lbsa_compareselect_{player.id}">
						<div class="icons iconsplus_green select"></div>
					</div>
					<div class="col-xs-12 removeContainer" id="lbsa_compareremove_{player.id}">
						<div class="icons iconsremove_red remove"></div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
