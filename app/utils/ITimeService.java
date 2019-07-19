package utils;

import java.time.Instant;
import java.time.ZonedDateTime;

/**
 * Created by mwalsh on 6/30/14.
 */
public interface ITimeService {

    public Instant getNow();

    public Instant getNowEST();

    public long getMinutesFromTargetTimeEST(int hour);

    public ZonedDateTime getNowAsZonedDateTime();

    public ZonedDateTime getNowAsZonedDateTimeEST();
}
