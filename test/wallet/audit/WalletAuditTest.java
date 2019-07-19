package wallet.audit;

import audit.AuditService;
import audit.IAuditService;
import com.avaje.ebean.Ebean;
import models.user.User;
import models.user.UserAction;
import models.wallet.WalletTransaction;
import org.junit.Test;
import utilities.BaseTest;
import utils.TimeService;

import java.util.List;

/**
 * Created by mwalsh on 8/1/14.
 */
public class WalletAuditTest extends BaseTest {

    @Test
    public void test(){
        IAuditService service = AuditService.getInstance();
        TimeService timeService = new TimeService();

        User user = new User();

        service.audit(new UserAction(user, UserAction.Type.LOGIN, "deposit initiated"));

        service.audit(new WalletTransaction(user, WalletTransaction.Type.DEPOSIT, 1000));

        List<WalletTransaction> list = Ebean.find(WalletTransaction.class).findList();
    }

}
