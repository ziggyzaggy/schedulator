package ENTITIES;

/**
 * Created by ziggyzaggy on 09/02/2015.
 */
public class User {
    private int userId;
    private boolean isDeleted;
    private String name;
    private String email;

    public User(int userId, boolean isDeleted, String name, String email) {
        this.userId = userId;
        this.isDeleted = isDeleted;
        this.name = name;
        this.email = email;
    }

    public User(){

    }



    //region setter getter block

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    //endregion
}
