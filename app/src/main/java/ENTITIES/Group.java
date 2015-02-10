package ENTITIES;

/**
 * Created by ziggyzaggy on 10/02/2015.
 */
public class Group {

    int groupId, soulCount;
    String groupName;

    public Group(int groupId, int soulCount, String groupName) {
        this.groupId = groupId;
        this.soulCount = soulCount;
        this.groupName = groupName;
    }

    public Group() {
    }


    //region getter setter


    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getSoulCount() {
        return soulCount;
    }

    public void setSoulCount(int soulCount) {
        this.soulCount = soulCount;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    //endregion
}
