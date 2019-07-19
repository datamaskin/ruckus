package models.contest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dao.DaoFactory;
import models.sports.Athlete;
import models.sports.AthleteSportEventInfo;
import models.user.User;
import play.Logger;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Created by dan on 5/20/14.
 */
@Entity
@SuppressWarnings("serial")
public class Entry {

    public static final String ENTRY_ID = "id";
    public static final String USER_ID = "user_id";
    public static final String CONTEST_ID = "contest_id";
    private static final String POINTS = "points";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ENTRY_ID)
    private int id;

    @ManyToOne
    @Column(nullable = false, name = USER_ID)
    private User user;

    @ManyToOne
    @Column(nullable = false, name = CONTEST_ID)
    private Contest contest;

    @Column(nullable = false, name = POINTS)
    private double points;

    @ManyToOne
    @JsonIgnore
    private Lineup lineup;

    public Entry() {
    }

    public Entry(User user, Contest contest, Lineup lineup) {
        this.user = user;
        this.contest = contest;
        this.lineup = lineup;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public double getPoints() {
        return points;
    }

    public void setPoints(double points) {
        this.points = points;
    }

    public Contest getContest() {
        return contest;
    }

    public void setContest(Contest contest) {
        this.contest = contest;
    }

    public Lineup getLineup() {
        return lineup;
    }

    public void setLineup(Lineup lineup) {
        this.lineup = lineup;
    }

    /**
     * Update the fantasy points for this entry based on the fantasy point totals of individual ASEIs.
     */
    public void updateEntryFantasyPoints() {
        if(lineup != null) {
            List<AthleteSportEventInfo> athleteSportEventInfoList = DaoFactory.getSportsDao().findAthleteSportEventInfos(lineup);

            BigDecimal fantasyPoints = new BigDecimal("0");
            for(AthleteSportEventInfo athleteSportEventInfo: athleteSportEventInfoList) {
                fantasyPoints = fantasyPoints.add(athleteSportEventInfo.getFantasyPoints());
            }

            fantasyPoints = fantasyPoints.setScale(2, RoundingMode.HALF_EVEN);
            setPoints(fantasyPoints.doubleValue());
            DaoFactory.getContestDao().saveEntry(this);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + id;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (getClass() != obj.getClass())
            return false;
        Entry other = (Entry) obj;
        if (id != other.id)
            return false;
        return true;
    }

    /**
     * Returns the opponent for a H2H contest.
     *
     * @return      The User object representing the opponent for a H2H contest.
     */
    public User determineH2HOpponent() {
        if(contest.getContestType().equals(ContestType.H2H) && contest.getCurrentEntries() == 2) {
            List<Entry> entryList = DaoFactory.getContestDao().findEntries(contest);
            if(user.equals(entryList.get(0).getUser())) {
                return entryList.get(1).getUser();
            }

            return entryList.get(0).getUser();
        }

        return null;
    }
}
