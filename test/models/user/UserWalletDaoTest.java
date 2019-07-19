package models.user;

import dao.IUserDao;
import dao.UserDao;
import models.wallet.UserWallet;
import org.junit.Before;
import org.junit.Test;
import utilities.BaseTest;

import static org.junit.Assert.assertEquals;

/**
 * Created by mwalsh on 7/18/14.
 * Modified by gislas on 8/8/14.
 */
public class UserWalletDaoTest extends BaseTest {

    private IUserDao dao;
    private User mwalsh;

    @Before
    public void setup(){
        dao = new UserDao();

        mwalsh = new User();
        mwalsh.setId(10L);
        dao.saveUser(mwalsh);
    }

    @Test
    public void basicTest(){
        dao.plusUsd(mwalsh, 5000L);
        UserWallet newWallet = dao.getUserWallet(mwalsh);
        assertEquals(newWallet.getUsd(), 5000L);
        assertEquals(0L, newWallet.getLoyaltyPoints());

        dao.updateWallet(newWallet);

        newWallet = dao.getUserWallet(mwalsh);
        assertEquals(1, newWallet.getUserBonuses().size());
        assertEquals(5000L, newWallet.getUserBonuses().get(0).getAmount());
        assertEquals(UserBonusType.FIRST_DEPOSIT, newWallet.getUserBonuses().get(0).getUserBonusType());
    }

    @Test
    public void basicMaxTest(){
        dao.plusUsd(mwalsh, 5000L);
        UserWallet newWallet = dao.getUserWallet(mwalsh);
        assertEquals(newWallet.getUsd(), 5000L);
        assertEquals(0L, newWallet.getLoyaltyPoints());

        dao.updateWallet(newWallet);

        newWallet = dao.getUserWallet(mwalsh);
        assertEquals(1, newWallet.getUserBonuses().size());
        assertEquals(5000L, newWallet.getUserBonuses().get(0).getAmount());
        assertEquals(UserBonusType.FIRST_DEPOSIT, newWallet.getUserBonuses().get(0).getUserBonusType());
    }
}
