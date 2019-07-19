<div class="col-xs-24" style="font-size:10pt;">
	<div class="row">
		<div class="col-xs-24">
			<div class="gamesContainer" style="overflow-x:auto;padding: 15px;">
				<div id="cong_all" class="cong_event game gameSelected allGames">
					ALL<br>GAMES
				</div>
				{#games}
					<div id="cong_{eventId}" class="cong_event game">
						<div>{awayTeam}@{homeTeam}</div>
						<div id="cong_temp_{eventId}"></div>
						<div id="cong_forecast_{eventId}"></div>
						<div>{formattedstartTime}</div>
					</div>
				{/games}
				<div style="clear:both;"></div>
			</div>
		</div>
	</div>
</div>
