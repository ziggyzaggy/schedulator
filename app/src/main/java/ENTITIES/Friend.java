package ENTITIES;

/**
 * Created by kristjan on 12/02/2015.
 */
public class Friend extends User {

    private boolean checkedInList;

    public Friend(int userId, boolean isDeleted, String name, String email) {
        super(userId, isDeleted, name, email);
        checkedInList = false;
    }


    public boolean isCheckedInList() {
        return checkedInList;
    }

    public void setCheckedInList(boolean checkedInList) {
        this.checkedInList = checkedInList;
    }
}
