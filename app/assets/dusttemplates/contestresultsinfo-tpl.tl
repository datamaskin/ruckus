<div class="col-xs-24">
	<div class="row">
		<div class="col-xs-24">
			<div class="row">
				<div class="col-xs-12">
					<div class="col-xs-24 pick">Teams Playing</div>
						<div class="col-xs-24 niceScroll teamsList">
							{#events}
								<div class="col-xs-8 teams">
									<div>{awayTeam} @ {homeTeam}</div><div class="time">{formattedStartTime}</div>
								</div>
							{/events}
						</div>
				</div>
				<div class="col-xs-12">
					<div class="row scoringContainer">
						<div class="col-xs-24 scoringTitle">{contest.league} Scoring Categories</div>
							<div class="row scoring niceScroll" >
								{#scoring}
									<div class="col-xs-8 name ellipsis">{name}</div><div class="col-xs-4 scoreValue">{points}</div>
								{/scoring}
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
