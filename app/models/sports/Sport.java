package models.sports;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
@SuppressWarnings("serial")
public class Sport {

    public static final Sport FOOTBALL = new Sport(1, "football");
    public static final Sport BASEBALL = new Sport(2, "baseball");
    public static final Sport BASKETBALL = new Sport(3, "basketball");
    public static final Sport[] ALL_SPORTS = new Sport[]{FOOTBALL, BASEBALL, BASKETBALL};
    private static final String NAME = "name";
    private static final String SPORT_ID = "id";

    @Id
    @Column(name = SPORT_ID)
    private Integer id;

    @Column(nullable = false, name = NAME)
    private String name;

    @OneToMany
    @JsonIgnore
    private List<Position> positions;

    public Sport(String name) {
        this.name = name;
    }

    public Sport(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Sport [id=" + id + ", name=" + name + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Sport sport = (Sport) o;

        if (id != null ? !id.equals(sport.id) : sport.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public List<Position> getPositions() {
        return positions;
    }

    public void setPositions(List<Position> positions) {
        this.positions = positions;
    }
}
