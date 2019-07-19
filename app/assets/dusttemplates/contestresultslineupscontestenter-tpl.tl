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
                                                <div id="quicklineupimport_{lineupId}" class="cone_enter quicklineupenter enter enterLineup">Import</div>
                                        </li>

                                {/data}
                                </ul>
                                </div>
                        </div>

                </div>
        </div>
</div>
