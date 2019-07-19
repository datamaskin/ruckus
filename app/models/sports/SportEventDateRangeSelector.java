package models.sports;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.DayOfWeek;

/**
 * Created by mwalsh on 7/8/14.
 */
@Entity
public class SportEventDateRangeSelector {

    @Id
    private int id;
    private DayOfWeek startDayOfWeek;
    private int startHourOfDay;
    private int startMinuteOfHour;
    private DayOfWeek endDayOfWeek;
    private int endHourOfDay;
    private int endMinuteOfHour;

    public SportEventDateRangeSelector(DayOfWeek startDayOfWeek, int startHourOfDay, int startMinuteOfHour,
                                       DayOfWeek endDayOfWeek, int endHourOfDay, int endMinuteOfHour) {

        if (startHourOfDay < 0 || startHourOfDay > 23) {
            throw new IllegalArgumentException("startHourOfDay must be between 0 and 23 inclusively");
        }

        if (startMinuteOfHour < 0 || startMinuteOfHour > 59) {
            throw new IllegalArgumentException("startMinuteOfHour must be between 0 and 59 inclusively");
        }

        if (endHourOfDay < 0 || endHourOfDay > 23) {
            throw new IllegalArgumentException("endHourOfDay must be between 0 and 23 inclusively");
        }

        if (endMinuteOfHour < 0 || endMinuteOfHour > 59) {
            throw new IllegalArgumentException("endMinuteOfHour must be between 0 and 59 inclusively");
        }

        this.startDayOfWeek = startDayOfWeek;
        this.startHourOfDay = startHourOfDay;
        this.startMinuteOfHour = startMinuteOfHour;
        this.endDayOfWeek = endDayOfWeek;
        this.endHourOfDay = endHourOfDay;
        this.endMinuteOfHour = endMinuteOfHour;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public DayOfWeek getStartDayOfWeek() {
        return startDayOfWeek;
    }

    public void setStartDayOfWeek(DayOfWeek startDayOfWeek) {
        this.startDayOfWeek = startDayOfWeek;
    }

    public int getStartHourOfDay() {
        return startHourOfDay;
    }

    public void setStartHourOfDay(int startHourOfDay) {
        this.startHourOfDay = startHourOfDay;
    }

    public int getStartMinuteOfHour() {
        return startMinuteOfHour;
    }

    public void setStartMinuteOfHour(int startMinuteOfHour) {
        this.startMinuteOfHour = startMinuteOfHour;
    }

    public DayOfWeek getEndDayOfWeek() {
        return endDayOfWeek;
    }

    public void setEndDayOfWeek(DayOfWeek endDayOfWeek) {
        this.endDayOfWeek = endDayOfWeek;
    }

    public int getEndHourOfDay() {
        return endHourOfDay;
    }

    public void setEndHourOfDay(int endHourOfDay) {
        this.endHourOfDay = endHourOfDay;
    }

    public int getEndMinuteOfHour() {
        return endMinuteOfHour;
    }

    public void setEndMinuteOfHour(int endMinuteOfHour) {
        this.endMinuteOfHour = endMinuteOfHour;
    }
}
