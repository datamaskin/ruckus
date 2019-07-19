<div class="col-xs-24" style="font-size:10pt;">
	<div class="row">
		<div class="quickL">Quick Roster</div>
		<div class="row" style="overflow-x:hidden;overflow-y:hidden;height:165px">
			<div class="col-xs-24 niceScroll niceScroll-horiz">
				<div>
				<ul class="quickLineupsContainer">
				{#data}
					<li class="quickLineupBox">
						<div class="lineupName ellipsis">{lineupName}</div>
						{#athletes}
							<div style="float:left;width:100px;" class="ellipsis">{position} {firstInitial} {lastName}</div>
						{/athletes}
						<div style="clear:both"></div>
						<div id="quicklineupenter_{lineupId}" class="cone_enter quicklineupenter enter enterLineup">Enter</div>
						<div id="quicklineupentered_{lineupId}" class="cone_entered quicklineupsuccess enter success qlSuccess">Entered</div>
						<div id="quicklineupfailed_{lineupId}" class="quicklineupsuccess enter fail qlFail">Failed</div>
<!--						<div id="quicklineupremove_{lineupId}" class="quicklineupremove enter qlRemove">Remove</div>-->
					</li>
					
				{/data}
				</ul>
				</div>
			</div>
			
		</div>
	</div>
</div>
