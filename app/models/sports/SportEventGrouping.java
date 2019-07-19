package models.sports;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by mwalsh on 7/8/14.
 */
@Entity
public class SportEventGrouping {

    public static final String ID = "id";
    public static final String SPORT_EVENT_GROUPING_TYPE_ID = "sport_event_grouping_type_id";
    public static final String DATE = "event_date";

    @Id
    @Column(name = ID)
    private int id;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "sport_event_grouping_x_sport_event",
            joinColumns = @JoinColumn(name = "sport_event_grouping_id", referencedColumnName = SportEventGrouping.ID),
            inverseJoinColumns = @JoinColumn(name = "sport_event_id", referencedColumnName = SportEvent.ID))
    private List<SportEvent> sportEvents;

    @ManyToOne
    @Column(name = SPORT_EVENT_GROUPING_TYPE_ID)
    private SportEventGroupingType sportEventGroupingType;

    @Column(name = DATE)
    private Date eventDate;

    public SportEventGrouping(List<SportEvent> sportEvents, SportEventGroupingType sportEventGroupingType) {
        this.sportEvents = sportEvents;
        this.sportEventGroupingType = sportEventGroupingType;
        this.eventDate = getEarliestGame(sportEvents);
    }

    private Date getEarliestGame(List<SportEvent> sportsEvents) {
        Date earliest = null;

        if (sportsEvents == null || sportsEvents.size() == 0) {
            return null;
        }

        for (SportEvent sportsEvent : sportsEvents) {
            if (earliest == null) {
                earliest = sportsEvent.getStartTime();
            } else if (earliest.after(sportsEvent.getStartTime())) {
                earliest = sportsEvent.getStartTime();
            }
        }
        return earliest;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<SportEvent> getSportEvents() {
        return sportEvents;
    }

    public void setSportEvents(List<SportEvent> sportEvents) {
        this.sportEvents = sportEvents;
    }

    public SportEventGroupingType getSportEventGroupingType() {
        return sportEventGroupingType;
    }

    public void setSportEventGroupingType(SportEventGroupingType sportEventGroupingType) {
        this.sportEventGroupingType = sportEventGroupingType;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }
}
