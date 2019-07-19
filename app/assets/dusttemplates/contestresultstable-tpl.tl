<div class="col-xs-24">
	<div class="row">
		<div class="col-xs-24">
			<div id="conr_header" class="row lobbyHead">
				<div id="conr_header_contest" class="col-xs-7 columnheader" style="cursor:pointer;">Contest<div id="conr_header_contestarrow" class="icons columnheaderarrow" style="position:absolute;top:6px;left:80px;"></div></div>
				<div id="conr_header_entries" class="col-xs-2 columnheader" style="cursor:pointer;">Entries<div id="conr_header_entriesarrow" class="icons columnheaderarrow" style="position:absolute;top:6px;left:70px;"></div></div>
				<div id="conr_header_size" class="col-xs-2 columnheader" style="cursor:pointer;">Size<div id="conr_header_sizearrow" class="icons columnheaderarrow" style="position:absolute;top:6px;left:60px;"></div></div>
				<div id="conr_header_entry" class="col-xs-2 columnheader" style="cursor:pointer;">Buy-in<div id="conr_header_entryarrow" class="icons columnheaderarrow" style="position:absolute;top:6px;left:70px;"></div></div>
				<div id="conr_header_prizes" class="col-xs-2 columnheader" style="cursor:pointer;">Payouts<div id="conr_header_prizesarrow" class="icons columnheaderarrow" style="position:absolute;top:6px;left:80px;"></div></div>
				<div id="conr_header_starts" class="col-xs-4 columnheader" style="cursor:pointer;">Start Time ET<div id="conr_header_startsarrow" class="icons columnheaderarrow" style="position:absolute;top:6px;left:110px;"></div></div>
				<div class="col-xs-5 columnheader"></div>
			</div>
			<div id="conr_table" class="row niceScroll">
				{#contests}
					<div class="col-xs-24 tableRow conr_visible conr_row_show conr_row conr_row_type_{contestType.abbr} conr_row_league_{league} conr_row_entryfee_{entryFee} conr_row_capacity_{capacity} conr_row_grouping_{grouping} conr_row_salarycap_{salaryCap}" id="conr_{id}" style="cursor:pointer;">
						<div class="row">
								<div class="col-xs-7 contest" id="conr_sport_{id}">{line1}<div id="conr_sportarrow_{id}" class="icons conr_sportarrow" style="position:absolute;top:15px;left:315px;"></div></div>
								<div class="col-xs-2 columnlink" id="conr_currententries_{id}">{formattedEntries}</div>
								<div class="col-xs-2 columnlink" id="conr_size_{id}">{formattedCapacity}</div>
								<div class="col-xs-2 columnlink" id="conr_entryfee_{id}">{formattedentryFee}</div>
								<div class="col-xs-2 columnlink" id="conr_entrypool_{id}">{formattedprizePool}</div>
								<div class="col-xs-3 columnlink" id="conr_starttime_{id}">{formattedstartTime}</div>
								<div class="col-xs-6" id="conr_enterouter_{id}">
									<div class="col-xs-4 rowGPPMIcons" style="text-align:right;">
										<span id="rowGPPIcon_{id}" class="rowGPPIcon" style="">G</span>
									</div>
									<div class="col-xs-8 rowGPPMIcons">
										<span id="rowMIcon_{id}" class="rowMIcon" style="">M</span>
									</div>
									<div class="col-xs-6 quick" id="conr_quick_{id}" style="display:none;">
										<div class="icons iconslightning_light" style="height:18px;background-position:0 -720px;"></div>
									</div>
								<div class="col-xs-6 pull-right enter" id="conr_enter_{id}">ENTER</div>
							</div>
						</div>
					</div>
				{/contests}
			</div>
		</div>
	</div>
</div>
