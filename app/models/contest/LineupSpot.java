package models.contest;

import models.sports.Athlete;
import models.sports.AthleteSportEventInfo;
import models.sports.Position;

import javax.persistence.*;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"athlete_id", "lineup_id"})
})
public class LineupSpot {

    private static final String ATHLETE_ID = "athlete_id";
    private static final String POSITION_ID = "position_id";

    @Id
    private Integer id;

    @OneToOne
    @Column(nullable = false, name = ATHLETE_ID)
    private Athlete athlete;

    @OneToOne
    @Column(nullable = false, name = POSITION_ID)
    private Position position;

    @ManyToOne
    @Column(nullable = false)
    private AthleteSportEventInfo athleteSportEventInfo;

    public LineupSpot(Athlete athlete, Position position, AthleteSportEventInfo athleteSportEventInfo) {
        this.athlete = athlete;
        this.position = position;
        this.athleteSportEventInfo = athleteSportEventInfo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Athlete getAthlete() {
        return athlete;
    }

    public void setAthlete(Athlete athlete) {
        this.athlete = athlete;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public AthleteSportEventInfo getAthleteSportEventInfo() {
        return athleteSportEventInfo;
    }

    public void setAthleteSportEventInfo(AthleteSportEventInfo athleteSportEventInfo) {
        this.athleteSportEventInfo = athleteSportEventInfo;
    }

}
