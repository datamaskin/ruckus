package dao;

import utilities.BaseTest;

/**
 * Created by davidb on 8/20/14.
 */
public class UserDaoTest extends BaseTest {
// Unusable as is because any newly created users get wacked in the user table before further tesing can happen.
//    private IUserDao dao;
//    private User dbrown;
//    private List<UserRole> userRoles = new ArrayList<>();
//    private UserRole userRole;
//
//    @Before
//    public void setup(){
//        dao = new UserDao();
//    }
//
//    @Test
//    public void findUserByUsenameTest() {
//        dbrown = dao.findUserByUsername("aggie");
//        List<User> users = new ArrayList<>();
//
//        org.junit.Assert.assertEquals(dbrown.getUserName(), "aggie");
//    }
//
//    @Test
//    public void findUserProfilesTest() {
//        dbrown = dao.findUserByUsername("aggie");
//        List<UserProfile> profiles = dao.getUserProfiles(dbrown);
//        UserProfile userProfile = profiles.get(0);
//        org.junit.Assert.assertEquals(userProfile.getUser().getUserName(), "aggie");
//    }
}
