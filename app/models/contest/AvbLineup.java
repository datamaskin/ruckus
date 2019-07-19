package models.contest;

import models.sports.AthleteSportEventInfo;

import javax.persistence.*;
import java.util.List;

/**
 * Created by mwalsh on 7/2/14.
 */

@Entity
public class AvbLineup {

    public static final String ID = "id";

    @Id
    @Column(name = ID)
    private int id;
    @ManyToMany
    @JoinTable(name = "avblineup_x_athlete_sport_event_info",
            joinColumns = @JoinColumn(name = "avb_lineup_id", referencedColumnName = AvbLineup.ID),
            inverseJoinColumns = @JoinColumn(name = "athlete_sport_event_info_id", referencedColumnName = AthleteSportEventInfo.ID))
    private List<AthleteSportEventInfo> athleteSportEventInfoList;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<AthleteSportEventInfo> getAthleteSportEventInfoList() {
        return athleteSportEventInfoList;
    }

    public void setAthleteSportEventInfoList(List<AthleteSportEventInfo> athleteSportEventInfoList) {
        this.athleteSportEventInfoList = athleteSportEventInfoList;
    }
}
