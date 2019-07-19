<!-- dashboardcontestathletedetail-tpl.tl -->
<div class="col-xs-24">
    <div class="row">
        <div class="col-xs-24">
            <div class="row" style="background-color:#555;">
                <div class="col-xs-10 athleteDetailImage">
                    <img src="{data.image}-70x70.png" style="height:70px;width:70px;margin-left:-15px;"/>
                </div>
		<div class="athleteDetailTeamLogo">
		    <img class="loRes" src="https://dm63aeeijtc75.cloudfront.net/nfl/athletedetail/{data.matchup.athleteTeamId}-218x70.png"/>
		    <img class="highRes" src="https://dm63aeeijtc75.cloudfront.net/nfl/athletedetail/{data.matchup.athleteTeamId}-436x140.png"/>
		</div>
                <div class="col-xs-14 player_data">
                    <div class="player_name fname ellipsis col-xs-24">{data.firstName}</div>
                    <div class="player_name ellipsis col-xs-24">{data.lastName}</div>
                    <div class="player_pos">#{data.uniform} {data.position}</div>
                </div>
            </div>
            <div class="row player_points">
                <div class="col-xs-24">
                    <div class="row">
                        <div class="col-xs-8 center_fpp_points">
                            <div data-socket="ad_fpp_{data.athleteSportEventInfoId}" class="row fpp_points">{data.fpp}</div>
                            <div class="row">points</div>
                        </div>
                        <div class="col-xs-12 matchup">
				 <div style="float:left;">{data.matchup.awayTeam}</div>
				<div data-socket="ad_score_{data.matchup.awayId}" id="sevt_{data.matchup.awayId}" style="float:right;" class="matchupPts">{data.matchup.awayScore}</div>
				<div style="clear:both;"></div>
				<div style="float:left;">@{data.matchup.homeTeam}</div>
				<div data-socket="ad_score_{data.matchup.homeId}" id="sevt_{data.matchup.homeId}" style="float:right;" class="matchupPts">{data.matchup.homeScore}</div>
				<div style="clear:both;"></div>
                        </div>
                        <div class="col-xs-4">
                            <div class="row active_status">
                                <div data-socket="ad_indicator_{data.athleteSportEventInfoId}" class="{data.indicatorClass}"></div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="row" style="background-color:#9A9A9A;">
                <div id="dhad_timepercentage_{data.athleteSportEventInfoId}" data-socket="ad_timePercentage_{data.athleteSportEventInfoId}" style="width:{data.formattedTimePercentage}%" class="progress_bar player_progress_bar"></div>
            </div>
            <div class="row player_2tabs">
                <div id="dhad_tabplayerfeed" class="col-xs-12 tabSelected">
                    <div style="margin-top:15px;margin-left:30px;border:none;" class="icons iconsplayer_dark"></div>
                    <div style="margin-top:15px;margin-left:30px;border:none;" class="icons iconsplayer_light"></div>
                </div>
                <div id="dhad_tabstats" class="col-xs-12 tab">
                    <div style="margin-top:15px;margin-left:30px;border:none;" class="icons iconsstats_dark"></div>
                    <div style="margin-top:15px;margin-left:30px;border:none;" class="icons iconsstats_light"></div>
                </div>
            </div>

            <div id="dhad_playerfeed" class="row playerfeed niceScroll" style="">
                <div class="col-xs-24" data-socket="ad_timeline_{data.athleteSportEventInfoId}">
                    {#data.timeline}
                        <div class="row playerfeed_item">
                            <div class="col-xs-6 playerfeed_fpchange">
                                {fpChange}
                            </div>
                            <div class="col-xs-18 playerfeed_fpdescription">
                                <div>{description}</div>
                            </div>
                        </div>
                        <div class="row playerfeed_item_timestamp">
                            <div class="col-xs-24 ">
                                <span>{formattedTimestamp}</span>
                            </div>
                        </div>
                    {/data.timeline}
                </div>
            </div>

            <div id="dhad_stats" class="row playerstats" style="display:none;">
                <div class="col-xs-24 playerstatsheader">
                    <div class="row">
                        <div class="col-xs-12">Type</div>
                        <div class="col-xs-6">Stats</div>
                        <div class="col-xs-6">Points</div>
                    </div>
                </div>
                <div class="col-xs-24 playerstatsrows niceScroll">
                    {#data.stats}
                        <div class="row playerstatsrow">
                            <div class="col-xs-12 ellipsis">
                                {name}
                            </div>
                            <div data-socket="ad_statamount_{id}_{data.athleteSportEventInfoId}" class="col-xs-6 amount">
                                {amount}
                            </div>
                            <div data-socket="ad_statfpp_{id}_{data.athleteSportEventInfoId}" class="col-xs-6 points">
                                {fpp}
                            </div>
                        </div>
                    {/data.stats}
                </div>
            </div>
        </div>
        <div id="dhad_teamfeed" class="teamfeedbutton">Team Feed<div class="icons iconsarrow_up" style="position:absolute;bottom:-48px;left:158px;"></div></div>
    </div>
</div>
