package models.contest;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

/**
 * Created by mwalsh on 7/2/14.
 */

@Entity
public class AvbMatchup {

    public static final String DATE = "date";

    @Id
    private int id;

    @OneToMany(cascade = CascadeType.ALL)
    private List<AvbLineup> avbLineupList;

    public AvbMatchup(List<AvbLineup> avbLineupList) {
        this.avbLineupList = avbLineupList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<AvbLineup> getAvbLineupList() {
        return avbLineupList;
    }

    public void setAvbLineupList(List<AvbLineup> avbLineupList) {
        this.avbLineupList = avbLineupList;
    }
}
