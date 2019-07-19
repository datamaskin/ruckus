package models.sports;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;

@Entity
@SuppressWarnings("serial")
public class Position {

    public static final Position BS_PITCHER = new Position(1, "Pitcher", "P", Sport.BASEBALL);
    public static final Position BS_CATCHER = new Position(2, "Catcher", "C", Sport.BASEBALL);
    public static final Position BS_FIRST_BASE = new Position(3, "First base", "1B", Sport.BASEBALL);
    public static final Position BS_SECOND_BASE = new Position(4, "Second base", "2B", Sport.BASEBALL);
    public static final Position BS_THIRD_BASE = new Position(5, "Third base", "3B", Sport.BASEBALL);
    public static final Position BS_SHORT_STOP = new Position(6, "Shortstop", "SS", Sport.BASEBALL);
    public static final Position BS_OUTFIELD = new Position(7, "Outfield", "OF", Sport.BASEBALL);
    // MLB does not *currently* have a FLEX or DH position
    public static final Position BS_FLEX = new Position(8, "Flex", "FX", Sport.BASEBALL);
    //public static final Position BS_DESIGNATED_HITTER = new Position(9, "Designated hitter", "DH", Sport.BASEBALL);
    public static final Position[] ALL_BASEBALL = new Position[]{BS_PITCHER, BS_CATCHER, BS_FIRST_BASE,
            BS_SECOND_BASE, BS_THIRD_BASE, BS_SHORT_STOP, BS_OUTFIELD, BS_FLEX};

    public static final Position FB_QUARTERBACK = new Position(10, "Quarterback", "QB", Sport.FOOTBALL);
    public static final Position FB_RUNNINGBACK = new Position(11, "Running back", "RB", Sport.FOOTBALL);
    public static final Position FB_WIDE_RECEIVER = new Position(12, "Wide receiver", "WR", Sport.FOOTBALL);
    public static final Position FB_TIGHT_END = new Position(13, "Tight end", "TE", Sport.FOOTBALL);
    public static final Position FB_KICKER = new Position(14, "Kicker", "K", Sport.FOOTBALL);
    public static final Position FB_FLEX = new Position(15, "Flex", "FX", Sport.FOOTBALL);
    public static final Position FB_DEFENSE = new Position(16, "Defense", "DEF", Sport.FOOTBALL);
    public static final Position[] ALL_FOOTBALL = new Position[]{FB_QUARTERBACK, FB_RUNNINGBACK,
            FB_WIDE_RECEIVER, FB_TIGHT_END, FB_KICKER, FB_FLEX, FB_DEFENSE};

    public static final Position BK_GUARD = new Position(20, "Guard", "G", Sport.BASKETBALL);
    public static final Position BK_FORWARD = new Position(21, "Forward", "F", Sport.BASKETBALL);
    public static final Position BK_CENTER = new Position(22, "Center", "C", Sport.BASKETBALL);
    public static final Position BK_FLEX = new Position(23, "Flex", "FX", Sport.BASKETBALL);
    public static final Position[] ALL_BASKETBALL = new Position[]{BK_GUARD, BK_FORWARD,
            BK_CENTER, BK_FLEX};

    public static final String POSITION_ID = "id";
    public static final String NAME = "name";
    public static final String ABBR = "abbreviation";
    public static final String SPORT_ID = "sport_id";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = Position.POSITION_ID)
    private int id;

    @Column(nullable = false, name = Position.NAME)
    private String name;

    @Column(nullable = false, name = Position.ABBR)
    @JsonProperty("abbr")
    private String abbreviation;

    @ManyToOne
    @Column(nullable = false, name = Position.SPORT_ID)
    @JsonIgnore
    private Sport sport;

//  @ManyToMany(cascade=CascadeType.ALL)
//  @JoinTable(name = "athlete_x_position",
//    joinColumns = @JoinColumn(name = Position.POSITION_ID, referencedColumnName = Position.POSITION_ID),
//    inverseJoinColumns = @JoinColumn(name = Athlete.ATHLETE_ID, referencedColumnName = Athlete.ATHLETE_ID))
//  private List<Athlete> athletes;

    public Position(int id, String name, String abbreviation, Sport sport) {
        this.id = id;
        this.name = name;
        this.abbreviation = abbreviation;
        this.sport = sport;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public Sport getSport() {
        return sport;
    }

    public void setSport(Sport sport) {
        this.sport = sport;
    }

    @Override
    public String toString() {
        return "Position [id=" + id + ", name=" + name + ", abbreviation=" + abbreviation + ", sport="
                + sport + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((abbreviation == null) ? 0 : abbreviation.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((sport == null) ? 0 : sport.hashCode());
        result = prime * result + id;
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Position position = (Position) o;

        if (id != position.id) return false;

        return true;
    }
}
