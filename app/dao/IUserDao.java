package dao;

import models.user.*;
import models.wallet.UserWallet;
import wallet.WalletException;

import java.util.List;

/**
 * Created by mwalsh on 7/8/14.
 */
public interface IUserDao {

    // Helper methods for adding, updating, and deleting where hooks to dependent actions can be added /////////////////

    /**
     * Saves a user to the DB
     * @param user
     */
    public void saveUser(User user);

    /**
     * Updates a user to the DB
     * @param user
     */
    public void updateUser(User user);

    /**
     * Saves a SecureSocialToken to the database
     * @param token
     */
    public void saveToken(SecureSocialToken token);

    // Helper find methods /////////////////////////////////////////////////////////////////////////////////////////////

    /**
     *
     * @param email
     * @param providerId
     * @return
     */
    public List<User> findUsers(String email, String providerId);

    User findUserByEmail(String userId);

    User findUserByUsername(String userId);

    /**
     * Finds a user by username or email address
     * @param username
     * @return AppUser by username or email address
     */
//    public User findUser(String username);

    /**
     *
     * @param id
     * @return AppUser by ID
     */
    public User findUser(Long id);

    /**
     *
     * @param tokenId
     * @return SecureSocialToken by ID
     */
    public SecureSocialToken findToken(String tokenId);

    UserWallet getUserWallet(User user);

    void updateWallet(UserWallet wallet);

    void plusUsd(User user, long amountDelta);

    void minusUsd(User user, long amountDelta) throws WalletException;

    List<UserProfile> getUserProfiles(User currentUser);

    UserProfile getUserProfile(User currentUser, Integer id);

    void saveUserProfile(UserProfile userProfile);

    void deleteCreditCard(UserProfile profile);

    void saveUserAction(UserAction userAction);

    List<UserRole> findExcludedUserRoles(String userId);

    int findCountUserRoles(String userId);

    int getUserRoleCount();

    void updateUserXRole(User user);

    void deleteUserXRole(User user);
}
