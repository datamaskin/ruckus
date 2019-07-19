package audit;

/**
 * Created by mwalsh on 8/1/14.
 */
public interface IAuditService {
    void audit(IAuditable auditMessage);
}
