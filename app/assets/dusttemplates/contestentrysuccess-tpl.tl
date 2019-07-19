<div class="col-xs-24 ruckus-spc-contestentrysuccess contestEntryMessageContainer" style="font-size:10pt;">
	<div class="row">
		<div class="col-xs-24 successStripe">SUCCESS!</div>
		<h1 class="contestEntryMessageHeader">ADDITIONAL GAMES</h1>
		<p class="contestEntryMessageBody">Like your lineup? Try your submitted lineup on these other contests we matched for you.</p>
		<div class="col-xs-24 table">
			<div class="row additionalHead">
			    <div class="col-xs-2" style="padding-right:5px;"></div>
			    <div class="col-xs-3" style="padding-right:5px;">Entries</div>
			    <div class="col-xs-9" style="padding-right:5px;">Contest</div>
			    <div class="col-xs-3" style="padding-right:5px;">Size</div>
			    <div class="col-xs-3" style="padding-right:2px;padding-left:13px;">Entry Fee</div>
			    <div class="col-xs-3" style="padding-right:0;padding-left:11px;margin-left:10px;">Prize Pool</div>
			</div>
			<div class="wrapper">
				{#additionalContests}
					<div class="row additionalRow">
						<div class="col-xs-24">
							<div class="col-xs-2" style="padding-right:5px;">
								<div id="lbsa_successcheck_{contestId}" class="lbsa_successcheck checkcircle" style="cursor:pointer;"></div>
							</div>
							<div class="col-xs-3" style="padding-right:5px;"><select id="lbsa_successmultiple_{contestId}"></select></div>
							<div class="col-xs-9" style="padding-right:5px;">{contestnamefull}</div>
							<div class="col-xs-3 border" style="padding-right:5px;">{capacity}</div>
							<div class="col-xs-3 border" style="padding-right:5px;padding-left:13px;">{formattedEntryFee}</div>
							<div class="col-xs-3 border" style="padding-right:5px;padding-left:11px;margin-left:10px;">{formattedPrizePool}</div>
							<div id="lbsa_nomessage_{contestId}" class="col-xs-4" style="display:none"></div>
							<div id="lbsa_successmessage_{contestId}" class="col-xs-10 successIcon contestEnterSuccess" style="display:none"></div>
							<div id="lbsa_failedmessage_{contestId}" class="col-xs-10 failedIcon contestEnterFail" style="display:none"></div>
						</div>
					</div>
				{/additionalContests}
			</div>
			<div id="lbsa_successenter" class="enterButton" style="cursor:pointer;">Enter</div>
			<a href="/app#lobby" class="enterButton return" style="cursor:pointer;">Lobby</a>
		</div>
	</div>
</div>
