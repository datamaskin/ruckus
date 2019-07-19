package audit;

import com.avaje.ebean.Ebean;

import java.time.LocalDateTime;

/**
 * Created by mwalsh on 7/1/14.
 */
public class AuditService implements IAuditService {

    private static final IAuditService instance = new AuditService();

    private AuditService() {}

    public static IAuditService getInstance(){
        return instance;
    }

    @Override
    public void audit(IAuditable auditable) {
        play.Logger.info(LocalDateTime.now() + "begin AUDITING");
        Ebean.save(auditable);
        play.Logger.info(LocalDateTime.now() + "end AUDITING");
    }
}
