package dao;

import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlQuery;
import com.avaje.ebean.SqlRow;
import com.avaje.ebean.SqlUpdate;
import models.user.*;
import models.wallet.UserWallet;
import play.Logger;
import play.libs.F;
import wallet.WalletException;

import java.util.*;

/**
 * Created by mwalsh on 7/8/14.
 */
public class UserDao extends AbstractDao implements IUserDao {

    @Override
    public void saveUser(User user) {
        save(user);
    }

    @Override
    public void updateUser(User user) {
        update(user);
    }



    @Override
    public void saveToken(SecureSocialToken token) {
        save(token);
    }

    @Override
    public List<User> findUsers(String email, String providerId) {
        return Ebean.find(User.class)
                .fetch("userRoles")
                .where().eq("email", email).eq("provider_id", providerId).findList();
    }

    @Override
    public User findUserByEmail(String userId) {
        return Ebean.find(User.class).fetch("userRoles").where().eq("email", userId).findUnique();
    }

    @Override
    public User findUserByUsername(String userId) {
        return Ebean.find(User.class).fetch("userRoles").where().eq("user_name", userId).findUnique();
    }

//    @Override
//    public User findUser(String userId) {
//        User localUser = Ebean.find(User.class)
//                .fetch("userRoles").where().eq("email", userId).findUnique();
//        if (localUser == null) {
//            localUser = Ebean.find(User.class)
//                    .fetch("userRoles")
//                    .where().eq("user_name", userId).findUnique();
//        }
//        return localUser;
//    }

    @Override
    public User findUser(Long id) {
        return Ebean.find(User.class).fetch("userRoles").where().eq(User.ID, id).findUnique();
    }

    @Override
    public SecureSocialToken findToken(String tokenId) {
        return Ebean.find(SecureSocialToken.class).where().eq("uuid", tokenId).findUnique();
    }

    @Override
    public List<UserRole> findExcludedUserRoles(String userId) {
        String sql = "select * from user_role where name not in " +
                "(select a.name " +
                "from user_role a, user_x_role b, user c where a.id = b.role_id " +
                "and c.id = b.user_id and c.user_name = '" + userId + "')";

        List<SqlRow> excluded = Ebean.createSqlQuery(sql).findList();

        List<UserRole> excList = new ArrayList<>();
        ListIterator<SqlRow> listIterator = null;
        listIterator = excluded.listIterator();
        HashMap<Integer, String> excMap = new HashMap<>();

        while (listIterator.hasNext()) {
            excMap.put(listIterator.nextIndex(), listIterator.next().toString());
        }
        Collection<String> values = excMap.values();
        String[] valArray = values.toArray(new String[values.size()]);
        for (int i=0; i<valArray.length; i++) {
            String tmp = valArray[i];
            String[] temp;
            temp = tmp.split("[=}]+");
            UserRole userRole = new UserRole(i, temp[2]);
            excList.add(i, userRole);
        }

        return excList;
       /* return Ebean.find(UserRole.class).fetch("userRoles").where().ne("user_id", userId).findList();*/
    }

    @Override
    public int findCountUserRoles(String userId) {
        String sql = "select count(a.name) " +
                    "from user_role a, user_x_role b, user c " +
                    "where a.id = b.role_id " +
                    "and c.id = b.user_id " +
                    "and c.user_name= '" + userId + "'";

        SqlQuery sqlQuery = Ebean.createSqlQuery(sql);
        SqlRow sqlRow = sqlQuery.findUnique();
        int retVal = sqlRow.getInteger("count(a.name)").intValue();
        return retVal;

//        return Ebean.find(UserRole.class).fetch("userRoles").where().eq("user_id", userId).findRowCount();
    }

    @Override
    public int getUserRoleCount() {
        return Ebean.find(UserRole.class).findRowCount();
    }

    @Override
    public void updateUserXRole(User user) {

        List<UserRole> list = user.getUserRoles();
        String sql = "insert into user_x_role (user_id, role_id) values (:user_id, :role_id)";
        int rowsMod;
        SqlUpdate sqlUpdate = Ebean.createSqlUpdate(sql);
        for (int i=0; i<list.size(); i++) {
            sqlUpdate.setParameter("user_id", user.getId());
            sqlUpdate.setParameter("role_id", list.get(i).getId());
            rowsMod = sqlUpdate.execute();
        }
    }

    @Override
    public void deleteUserXRole(User user) {
        List<UserRole> list = user.getUserRoles();
        String sql = "delete from user_x_role  where user_id = :user_id and role_id = :role_id";
        int rowsMod;
        SqlUpdate sqlUpdate = Ebean.createSqlUpdate(sql);
        for (int i=0; i<list.size(); i++) {
            sqlUpdate.setParameter("user_id", user.getId());
            sqlUpdate.setParameter("role_id", list.get(i).getId());
            rowsMod = sqlUpdate.execute();
        }
    }

    @Override
    public UserWallet getUserWallet(User user) {
        UserWallet userWallet = Ebean.find(UserWallet.class)
                .fetch("userBonuses")
                .where().eq(UserWallet.USER_ID, user.getId()).findUnique();
        return userWallet;
    }

    @Override
    public void updateWallet(UserWallet wallet) {
        Ebean.save(wallet);
    }

    @Override
    public void plusUsd(User user, long amountDelta) {
        UserWallet userWallet = getUserWallet(user);

        if (userWallet == null) {
            userWallet = new UserWallet(user);
            UserBonusType type = Ebean.find(UserBonusType.class, UserBonusType.FIRST_DEPOSIT.getId());
            try {
                JSONObject obj = new JSONObject(type.getParameters());
                long addedBonus = Math.min(amountDelta, Integer.parseInt(obj.getString(UserBonusType.FIRST_DEPOSIT_DATA_MAX_AMOUNT_CENTS)));
                UserBonus userBonus = new UserBonus(UserBonusType.FIRST_DEPOSIT, addedBonus);
                userWallet.addUserBonus(userBonus);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        long amountCurrent = userWallet.getUsd();
        long newAmount = amountCurrent + amountDelta;
        userWallet.updateUsd(newAmount);
        updateWallet(userWallet);
    }

    @Override
    public void minusUsd(User user, long amountDelta) throws WalletException {
        UserWallet wallet = getUserWallet(user);

        if (wallet == null) {
            throw new WalletException("Not enough funds.");
        }

        long amountCurrent = wallet.getUsd();
        long newAmount = amountCurrent - amountDelta;

        if (newAmount < 0) {
            throw new WalletException("Not enough funds.");
        } else {
            wallet.updateUsd(newAmount);
            updateWallet(wallet);
        }

    }

    @Override
    public List<UserProfile> getUserProfiles(User currentUser) {
        return Ebean.find(UserProfile.class).where()
                .eq("user", currentUser)
                .eq("active", 1)
                .findList();
    }

    @Override
    public UserProfile getUserProfile(User currentUser, Integer id) {
        return Ebean.find(UserProfile.class).where()
                .eq("user", currentUser)
                .eq("id", id).findUnique();
    }

    @Override
    public void saveUserProfile(UserProfile userProfile) {
        Ebean.save(userProfile);
    }

    @Override
    public void deleteCreditCard(UserProfile profile) {
        profile.setCamsTokenId(null);
        profile.setCcExpMonth(null);
        profile.setCcExpYear(null);
        profile.setCcNumber(null);
        profile.setCcType(null);
        Ebean.save(profile);
    }

    @Override
    public void saveUserAction(UserAction userAction) {
        F.Promise.promise(() -> {
            try {
                Ebean.save(userAction);
            } catch (Exception e) {
                Logger.error("Could not save UserAction to MySQL", e);
            }

            try {
                dynamoDBMapper.save(new DynamoUserAction(userAction));
            } catch (Exception e) {
                Logger.error("Could not save UserAction to AmazonDB", e);
            }
            return null;
        });
    }

    private static UserRole getUserRole(String roleName) {
        HashMap<String, UserRole> map    = new HashMap<>();
        map.put(UserRole.ADMIN_NAME,     UserRole.ADMIN_ROLE);
        map.put(UserRole.MGMT_NAME,      UserRole.MGMT_ROLE);
        map.put(UserRole.DEVOPS_NAME,    UserRole.DEVOPS_ROLE);
        map.put(UserRole.CS_NAME,        UserRole.CS_ROLE);

        return map.get(roleName);
    }
}
