<div class="col-xs-24" style="font-size:10pt;">
	<div class="row">
		<div class="col-xs-12 players">Players Entered</div>
		<div class="col-xs-12">
			<input id="entrysearch" type="textbox" value="Search_"></input>
		</div>
		<div class="col-xs-24 niceScroll" style="height:165px;overflow-y:hidden;">
			{#entries}
				<div id="entry_{userId}" class="entry_{userId} col-xs-8">{userName}</div>
			{/entries}	
		</div>
	</div>
</div>
