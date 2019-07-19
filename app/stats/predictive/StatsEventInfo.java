package stats.predictive;

import models.sports.SportEvent;

import java.util.Date;

/**
 * Created by mgiles on 7/26/14.
 */
public class StatsEventInfo {
    private SportEvent sportEvent;
    private Integer opponentId;
    private Date startTime;

    public SportEvent getSportEvent() {
        return sportEvent;
    }

    public void setSportEvent(SportEvent sportEvent) {
        this.sportEvent = sportEvent;
    }

    public Integer getOpponentId() {
        return opponentId;
    }

    public void setOpponentId(Integer opponentId) {
        this.opponentId = opponentId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
}
