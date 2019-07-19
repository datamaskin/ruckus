package models.user;

import securesocial.core.java.Authorization;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by mwalsh on 7/14/14.
 */
@Entity
public class UserRole implements Authorization<User> {

    public static final String ADMIN_NAME = "admin";
    public static final String MGMT_NAME = "mgmt";
    public static final String DEVOPS_NAME = "devops";
    public static final String CS_NAME = "cs";

    public static final UserRole ADMIN_ROLE = new UserRole(1, ADMIN_NAME);
    public static final UserRole MGMT_ROLE = new UserRole(2, MGMT_NAME);
    public static final UserRole DEVOPS_ROLE = new UserRole(3, DEVOPS_NAME);
    public static final UserRole CS_ROLE = new UserRole(4, CS_NAME);

    public static final String ID = "id";
    public static final String NAME = "name";

    @Id
    private int id;

    @Column(name = NAME)
    private String name;

    public UserRole(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean isAuthorized(User user, String[] params) {
        for (UserRole role : user.getUserRoles()) {
            for (String param : params) {
                if (role.name.equals(param)) {
                    return true;
                }
            }
        }
        return false;
    }
}
