package stats.updateprocessor;

import models.sports.AthleteSportEventInfo;

import java.math.BigDecimal;

/**
 * Created by dmaclean on 7/20/14.
 */
public class FantasyPointAthleteUpdateEvent {
    public static final String BATTER = "batter";
    public static final String PITCHER = "pitcher";
    public static final String RUNNER = "runner";

    private AthleteSportEventInfo athleteSportEventInfo;

    private BigDecimal fantasyPoints;

    private BigDecimal fantasyPointDelta;

    private String timeline;

    private String boxscore;

    private String type;

    public FantasyPointAthleteUpdateEvent() {
    }

    public AthleteSportEventInfo getAthleteSportEventInfo() {
        return athleteSportEventInfo;
    }

    public void setAthleteSportEventInfo(AthleteSportEventInfo athleteSportEventInfo) {
        this.athleteSportEventInfo = athleteSportEventInfo;
    }

    public BigDecimal getFantasyPoints() {
        return fantasyPoints;
    }

    public void setFantasyPoints(BigDecimal fantasyPoints) {
        this.fantasyPoints = fantasyPoints;
    }

    public BigDecimal getFantasyPointDelta() {
        return fantasyPointDelta;
    }

    public void setFantasyPointDelta(BigDecimal fantasyPointDelta) {
        this.fantasyPointDelta = fantasyPointDelta;
    }

    public String getTimeline() {
        return timeline;
    }

    public void setTimeline(String timeline) {
        this.timeline = timeline;
    }

    public String getBoxscore() {
        return boxscore;
    }

    public void setBoxscore(String boxscore) {
        this.boxscore = boxscore;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
