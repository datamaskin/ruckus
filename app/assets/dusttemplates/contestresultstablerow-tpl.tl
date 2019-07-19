<!--{!<div class="col-xs-24" id="conr__{id}" style="cursor:pointer;opacity:0;margin-left:-120px;">!}
	{!<div class="row">!}
        {!<div class="col-xs-7 contest columnlink" id="conr_sport_{id}">{sport} {type}</div>!}
        {!<div class="col-xs-2 columnlink" id="conr_currententries_{id}" class="columnlink">{current_entries}</div>!}
        {!<div class="col-xs-2 columnlink" id="conr_size_{id}" class="columnlink">{size}</div>!}
        {!<div class="col-xs-2 columnlink" id="conr_entryfee_{id}">${entry_fee}</div>!}
        {!<div class="col-xs-2 columnlink" id="conr_entrypool_{id}" class="columnlink">${entry_pool}</div>!}
        {!<div class="col-xs-4 columnlink" id="conr_starttime_{id}">{formattedstart_time}</div>!}
        {!<div class="col-xs-5" id="conr_enterouter_{id}">!}
            {!<div class="col-xs-12 icons iconslightning_light" id="conr_quick_{id}" style="display:none;"></div>!}
            {!<div class="col-xs-12 pull-right" id="conr_enter_{id}">ENTER</div>!}
        {!</div>!}
    {!</div>!}
{!</div>!}

<div class="col-xs-24 tableRow conr_visible conr_row_show conr_row conr_row_type_{contestType.abbr} conr_row_league_{league} conr_row_entryfee_{entryFee} conr_row_capacity_{capacity} conr_row_grouping_{grouping} conr_row_salarycap_{salaryCap}" id="conr_{id}" style="cursor:pointer;">
    <div class="row">
        <div class="col-xs-7 contest" id="conr_sport_{id}">{line1}<div class="icons iconsarrow_down" style="position:absolute;top:6px;left:80px;"></div></div>
        <div class="col-xs-2 columnlink" id="conr_currententries_{id}">{currentEntries}</div>
        <div class="col-xs-2 columnlink" id="conr_size_{id}">{capacity}</div>
        <div class="col-xs-2 columnlink" id="conr_entryfee_{id}">{formattedentryFee}</div>
        <div class="col-xs-2 columnlink" id="conr_entrypool_{id}">{formattedprizePool}</div>
        <div class="col-xs-4 columnlink" id="conr_starttime_{id}">{formattedstartTime}</div>
        <div class="col-xs-5" id="conr_enterouter_{id}">
		<div class="col-xs-4 rowGPPMIcons" style="text-align:right;">
			<span id="rowGPPIcon_{id}" class="rowGPPIcon" style="">G</span>
		</div>
		<div class="col-xs-8 rowGPPMIcons">
			<span id="rowMIcon_{id}" class="rowMIcon" style="">M</span>
		</div>
		<div class="col-xs-12 quick" id="conr_quick_{id}" style="display:none;">
			<div class="icons iconslightning_light" style="height:18px;background-position:0 -720px;"></div>
		</div>
		<div class="col-xs-12 pull-right enter" id="conr_enter_{id}">ENTER</div>
        </div>
    </div>
</div>-->
