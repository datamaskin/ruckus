<!-- dashboardcontestteamfeed-tpl.tl -->
<div class="col-xs-24">
    <div class="row">
        <div class="col-xs-24">
            <div class="row">
                <div class="col-xs-24 teamfeedtitle">Team Feed</div>
            </div>
        </div>
    </div>
    <div class="row teamfeedbody niceScroll">
        <div class="col-xs-24">
            <div class="teamfeeddata">
                <div class="col-xs-24" data-socket="tf_timeline">
			<div id="teamfeeddatanone" style="display:none;margin-top:15px;">No points scored yet...</div>
			{#data}
				<div class="row teamfeeddataitem">
				    <div class="col-xs-6 fpchange">
					{fpChange}
				    </div>
				    <div class="col-xs-18 fpdescription">
					<div class="fpdescriptionname">{formattedName}</div>
					<div class="fpdescriptiontext">{description}</div>
				    </div>
				</div>
				<div class="row teamfeeddataitemtimestamp">
				    <div class="col-xs-24">
					<span>{formattedTimestamp}</span>
				    </div>
				</div>
			{/data}
                </div>
            </div>
        </div>
    </div>
</div>
