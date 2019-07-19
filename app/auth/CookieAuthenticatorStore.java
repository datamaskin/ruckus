package auth;

import akka.dispatch.Futures;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlRow;
import models.user.User;
import org.joda.time.DateTime;
import play.libs.F;
import scala.Option;
import scala.concurrent.Future;
import scala.reflect.ClassTag;
import scala.runtime.BoxedUnit;
import securesocial.core.authenticator.AuthenticatorStore;
import securesocial.core.authenticator.CookieAuthenticator;

import java.text.SimpleDateFormat;

/**
 * Created by mwalsh on 7/17/14.
 */
public class CookieAuthenticatorStore implements AuthenticatorStore<CookieAuthenticator<User>> {

    private final static String find = "SELECT id, user_id, expiration_date, last_used, creation_date" +
            " from user_session where id = sha('%s')";

    private final static String update = "update user_session set" +
            " user_id = %d, expiration_date = '%s', last_used = '%s', creation_date = '%s'" +
            " where id = sha('%s')";

    private final static String insert = "insert into user_session" +
            " (id, user_id, expiration_date, last_used, creation_date)" +
            " values (sha('%s'), %d, '%s', '%s', '%s')";

    private final static String delete = "delete from user_session where id = sha('%s')";

    @Override
    public Future<Option<CookieAuthenticator<User>>> find(String id, ClassTag<CookieAuthenticator<User>> ct) {
        SqlRow sqlRow = Ebean
                .createSqlQuery(String.format(find, id))
                .findUnique();

        if(sqlRow == null){
            return Futures.successful(Option.empty());
        } else {
            Integer userId = sqlRow.getInteger("user_id");
            User user = Ebean.find(User.class, userId);
            return Futures.successful(Option.apply(
                    new CookieAuthenticator<User>(
                            id, user,
                            new DateTime(sqlRow.getTimestamp("expiration_date")),
                            new DateTime(sqlRow.getTimestamp("last_used")),
                            new DateTime(sqlRow.getTimestamp("creation_date")),
                            this
                    )));
        }
    }

    @Override
    public Future<CookieAuthenticator<User>> save(CookieAuthenticator<User> authenticator, int timeoutInSeconds) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        int rowCount = Ebean.createSqlUpdate(
                String.format(update,
                authenticator.user().getId(),
                fmt.format(authenticator.expirationDate().plusSeconds(timeoutInSeconds).toDate()),
                fmt.format(authenticator.lastUsed().toDate()),
                fmt.format(authenticator.creationDate().toDate()),
                authenticator.id()
        )).execute();

        if(rowCount == 0){
            String sql = String.format(insert,
                    authenticator.id(),
                    authenticator.user().getId(),
                    fmt.format(authenticator.expirationDate().plusSeconds(timeoutInSeconds).toDate()),
                    fmt.format(authenticator.lastUsed().toDate()),
                    fmt.format(authenticator.creationDate().toDate())
            );
            Ebean.createSqlUpdate(sql).execute();
        } else {

        }
        return Futures.successful(authenticator);
    }

    @Override
    public Future<BoxedUnit> delete(String id) {
        return F.Promise.promise(new F.Function0<BoxedUnit>() {
            public BoxedUnit apply() {
                String sql = String.format(delete, id);
                Ebean.createSqlUpdate(sql).execute();
                return BoxedUnit.UNIT;
            }
        }).wrapped();
    }
    
}
