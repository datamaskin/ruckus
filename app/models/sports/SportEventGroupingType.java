package models.sports;

import javax.persistence.*;
import java.util.List;

/**
 * Created by mwalsh on 7/8/14.
 */
@Entity
public class SportEventGroupingType {

    @Id
    private int id;

    @ManyToOne
    private League league;

    @Column(name = "name")
    private String name;

    @OneToMany(cascade = CascadeType.ALL)
    @Column(name = "date")
    private List<SportEventDateRangeSelector> dateCriteria;

    public SportEventGroupingType(League league, String name, List<SportEventDateRangeSelector> dateCriteria) {
        this.league = league;
        this.name = name;
        this.dateCriteria = dateCriteria;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public League getLeague() {
        return league;
    }

    public void setLeague(League league) {
        this.league = league;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<SportEventDateRangeSelector> getDateCriteria() {
        return dateCriteria;
    }

    public void setDateCriteria(List<SportEventDateRangeSelector> dateCriteria) {
        this.dateCriteria = dateCriteria;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SportEventGroupingType that = (SportEventGroupingType) o;

        if (id != that.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
