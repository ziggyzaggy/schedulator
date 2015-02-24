package GLOBALS;

/**
 * Created by ziggyzaggy on 24/02/2015.
 * holds current user class singleton
 */
public class CurrentUser {
    public static String name;
    public static int userId;
    public static String email;
    public static String bDay;
    private static boolean instanced = false;


    public CurrentUser(String name, int id, String email, String bDay){
        this.name = name;
        this.userId = id;
        this.instanced = true;
    }

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        CurrentUser.name = name;
    }

    public static int getUserId() {
        return userId;
    }

    public static void setUserId(int userId) {
        CurrentUser.userId = userId;
    }

    public static String getEmail() {
        return email;
    }

    public static void setEmail(String email) {
        CurrentUser.email = email;
    }

    public static String getbDay() {
        return bDay;
    }

    public static void setbDay(String bDay) {
        CurrentUser.bDay = bDay;
    }

    public static boolean isIntanced() {
        return instanced;
    }

    public static void setIntanced(boolean intanced) {
        CurrentUser.instanced = intanced;
    }
}
