package models.user;

import com.avaje.ebean.Ebean;
import dao.IUserDao;
import dao.UserDao;
import org.junit.Before;
import org.junit.Test;
import utilities.BaseTest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by mwalsh on 7/29/14.
 *
 *
 */
public class UserProfileTest extends BaseTest {

    private IUserDao dao;
    private User dbrown;
    private List<UserRole> userRoles = new ArrayList<>();
    private UserRole userRole;

    @Before
    public void setup(){
        dao = new UserDao();
    }

    @Test
    public void testUserProfile(){
        User dbrown = new User();
        dbrown.setFirstName("David");
        dbrown.setLastName("Brown");
        dbrown.setUserName("aggie");
        dbrown.setPassword("lutefisk");
        dbrown.setProviderId("xxx");
        dbrown.setVerified(new Date());
        dbrown.setVersion(new Date());
        dbrown.setEmail("david@davidwbrown.name");
        List<UserRole> userRoles = new ArrayList<>();
        userRoles.add(UserRole.ADMIN_ROLE);
        userRoles.add(UserRole.MGMT_ROLE);
        dbrown.setUserRoles(userRoles);

        Ebean.save(dbrown);

        UserProfile userProfile = new UserProfile(dbrown,
                                                    "919 Navidad",
                                                    "upstairs bedroom",
                                                    "Bryan",
                                                    StateProvince.US_TX,
                                                    "77801");
        Ebean.save(userProfile);

        dbrown = dao.findUserByUsername("aggie");
        org.junit.Assert.assertEquals(dbrown.getUserName(), "aggie");
        org.junit.Assert.assertEquals(dbrown.getUserRoles().get(0).getName(), UserRole.ADMIN_NAME);
        org.junit.Assert.assertEquals(dbrown.getUserRoles().get(1).getName(), UserRole.MGMT_NAME);
        List<UserProfile> profiles = dao.getUserProfiles(dbrown);
        userProfile = profiles.get(0);
        org.junit.Assert.assertEquals(userProfile.getUser().getUserName(), "aggie");

//        numOfRoles = dao.findCountUserRoles("dufus");
//        org.junit.Assert.assertEquals(dao.getUserRoleCount(), numOfRoles);
        userRoles.add(UserRole.DEVOPS_ROLE);
        userRoles.add(UserRole.CS_ROLE);
        dbrown.setUserRoles(userRoles);
        org.junit.Assert.assertEquals(dbrown.getUserRoles().get(2).getName(), UserRole.DEVOPS_NAME);
        org.junit.Assert.assertEquals(dbrown.getUserRoles().get(3).getName(), UserRole.CS_NAME);
        dao.updateUser(dbrown);
        int numOfRoles = dao.findCountUserRoles("aggie");
        org.junit.Assert.assertEquals(dao.getUserRoleCount(), numOfRoles);
    }

}
