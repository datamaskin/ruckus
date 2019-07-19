package utils;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Created by mwalsh on 6/30/14.
 */
public class TimeService implements ITimeService {

    @Override
    public Instant getNow() {
        return getNowAsZonedDateTime().toInstant();
    }

    @Override
    public Instant getNowEST() {
        return getNowAsZonedDateTimeEST().toInstant();
    }

    @Override
    public ZonedDateTime getNowAsZonedDateTime() {
        return ZonedDateTime.now(ZoneId.of("UTC"));
    }

    @Override
    public ZonedDateTime getNowAsZonedDateTimeEST() {
        return ZonedDateTime.now(ZoneId.of("America/New_York"));
    }

    @Override
    public long getMinutesFromTargetTimeEST(int hour) {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("America/New_York"));

        ZonedDateTime adjusted;
        if (now.getHour() >= hour) {
            adjusted = now.plusDays(1).withHour(hour).withMinute(0).withSecond(0);
        } else {
            adjusted = now.withHour(hour).withMinute(0).withSecond(0);
        }

        return Duration.between(now, adjusted).toMinutes();
    }

}
