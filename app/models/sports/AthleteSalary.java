package models.sports;

import javax.persistence.*;

/**
 * Created by mwalsh on 7/10/14.
 */

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {AthleteSalary.ATHLETE_ID, AthleteSalary.SPORT_EVENT_GROUP_ID})
})
public class AthleteSalary {

    public static final String ATHLETE_ID = "athlete_id";
    public static final String SPORT_EVENT_GROUP_ID = "sport_event_grouping_id";

    @Id
    public int id;
    @ManyToOne
    @Column(name = ATHLETE_ID)
    public Athlete athlete;
    @ManyToOne
    @Column(name = SPORT_EVENT_GROUP_ID)
    public SportEventGrouping sportEventGrouping;
    public int salary;

    public AthleteSalary(Athlete athlete, SportEventGrouping sportEventGrouping, int salary) {
        this.athlete = athlete;
        this.sportEventGrouping = sportEventGrouping;
        this.salary = salary;
    }
}
