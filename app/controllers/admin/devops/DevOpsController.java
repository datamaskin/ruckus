package controllers.admin.devops;

import com.avaje.ebean.Ebean;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.Member;
import controllers.AbstractController;
import controllers.admin.AdminSecuredActionResponse;
import dao.DaoFactory;
import dao.IUserDao;
import dao.UserDao;
import distributed.DistributedServices;
import models.user.User;
import models.user.UserRole;
import play.Play;
import play.mvc.Http;
import play.mvc.Result;
import securesocial.core.java.SecuredAction;
import service.EdgeCacheService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * All routes that start with /admin are restricted at the network layer
 * Created by davidb on 8/11/14.
 */
public class DevOpsController extends AbstractController {

    private static final String default_ebean_server = "default";

    public static Result getHazelcastInfo() {
        HazelcastInstance instance = DistributedServices.getInstance();
        Map<String, Map<String, String>> info = new HashMap<>();
        Map<String, String> members = new HashMap<>();
        int i = 1;
        for (Member member : instance.getCluster().getMembers()) {
            String memberInfo = "Member " + i;
            if (member.equals(instance.getCluster().getLocalMember())) {
                memberInfo += " (this)";
            }
            members.put(memberInfo, member.getSocketAddress().getAddress() + ":" + member.getSocketAddress().getPort());
            i++;
        }
        info.put("members", members);
        return jok(members);
    }

    @SecuredAction(responses = AdminSecuredActionResponse.class, authorization = UserRole.class, params = {"admin"})
    public static Result flushAllCaches() {
        EdgeCacheService.flushAllCaches();
        return jok("OK");
    }

    @SecuredAction(responses = AdminSecuredActionResponse.class, authorization = UserRole.class, params = {"admin"})
    public static Result updateInjuries() {
        DaoFactory.getStatsDao().updateNflInjuries();
        return jok("OK");
    }

    public static Result version() throws Exception {
        return ok(Play.application().configuration().getString("app.version"));
    }

    @SecuredAction(responses = AdminSecuredActionResponse.class, authorization = UserRole.class, params = {"admin"})
    public static Result clearPredictionTables() throws Exception {
        String result = DaoFactory.getStatsDao().clearNflPredictionTable() + "\n";
        result += DaoFactory.getStatsDao().clearNflDefPredictionTable();
        return ok(result);
    }

    @SecuredAction(responses = AdminSecuredActionResponse.class, authorization = UserRole.class, params = {"admin"})
    public static Result clearStatsTables() throws Exception {
        String result = DaoFactory.getStatsDao().clearNflStatsTable() + "\n";
        result += DaoFactory.getStatsDao().clearNflDefStatsTable();
        return ok(result);
    }

    @SecuredAction(responses = AdminSecuredActionResponse.class, authorization = UserRole.class, params = {"admin"})
    public static Result getNflPredictionCount() throws Exception {
        return ok(DaoFactory.getStatsDao().getNflPredictionCount() + "");
    }

    public static User findUser(String userStr) {
        User user;
        IUserDao dao = new UserDao();
        user = dao.findUserByUsername(userStr);
        return user;
    }

    @SecuredAction(responses = AdminSecuredActionResponse.class, authorization = UserRole.class, params = {"admin"})
    public static Result searchUser() {
        String[] userStr = request().body().asFormUrlEncoded().get("search");
        User user = findUser(userStr[0]);

        List<User> userList = new ArrayList<User>();
        userList.add(user);
        IUserDao dao = new UserDao();
        List<UserRole> userRoles = dao.findExcludedUserRoles(user.getUserName());
        if (user!=null)
            return ok(views.html.devops.updateUserRoles.render(user, userRoles));
        else
            return jerr(user);
    }

    @SecuredAction(responses = AdminSecuredActionResponse.class, authorization = UserRole.class, params = {"admin"})
    public static Result updateUserRoles() {
        return ok(views.html.devops.searchUser.render());
    }

    @SecuredAction(responses = AdminSecuredActionResponse.class, authorization = UserRole.class, params = {"admin"})
    public static Result updateUser() {
        Http.RequestBody body           = request().body();
        String[] roleNames              = request().body().asFormUrlEncoded().get("rolename");
        String[] selectedRoles          = request().body().asFormUrlEncoded().get("userrole");
        String[] users                  = request().body().asFormUrlEncoded().get("user");
        UserDao dao                     = new UserDao();
        User user                       = dao.findUserByUsername(users[0]);
        List<UserRole> currentRoles     = user.getUserRoles(); // is this zero when no user roles?

        List<String> roleList           = new ArrayList<>();

        if (roleNames!=null) {
            roleList = new ArrayList<String>();
        }

        List<String> userSelectedList   = new ArrayList<>();
        List<UserRole> userRoleList = new ArrayList<>();

        if (selectedRoles!=null) {
           userSelectedList             = new ArrayList<String>();
        }

        if (userSelectedList.size() < currentRoles.size()) {
            for (int i=0; i<currentRoles.size(); i++) {
                userRoleList = getUserRoleList(currentRoles.get(i).getName(), userRoleList);
            }
            user.setUserRoles(userRoleList);
            dao.deleteUserXRole(user);
        }

        if (roleNames!=null) {
            for (int i=0; i<roleNames.length; i++) {
                roleList.add(roleNames[i]);
            }
        }

        if (selectedRoles!=null) {
            for (int i=0; i<selectedRoles.length; i++) {
                userSelectedList.add(selectedRoles[i]);
            }
        }

        userRoleList = new ArrayList<>();

        for (int i=0; i<userSelectedList.size(); i++) {
            userRoleList = getUserRoleList(userSelectedList.get(i), userRoleList);
        }

        for (int i=0; i<roleList.size(); i++) {
            userRoleList = getUserRoleList(roleList.get(i), userRoleList);
        }

        user.setUserRoles(userRoleList);

        /*dao.updateUser(user);*/

        /*forceInsert(user);*/ // TODO need to find a suitable Ebean solution

        dao.updateUserXRole(user);

        return ok(views.html.devops.searchUser.render());
    }

    private static List<UserRole> getUserRoleList(String role, List<UserRole> userRoleList) {

            switch (role) {
                case "admin"    : userRoleList.add(UserRole.ADMIN_ROLE);
                    break;
                case "mgmt"     : userRoleList.add(UserRole.MGMT_ROLE);
                    break;
                case "devops"   : userRoleList.add(UserRole.DEVOPS_ROLE);
                    break;
                case "cs"       : userRoleList.add(UserRole.CS_ROLE);
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        return userRoleList;
    }

    private static void forceInsert(Object object) {
        Ebean.getServer(default_ebean_server).insert(object);
    }
}
