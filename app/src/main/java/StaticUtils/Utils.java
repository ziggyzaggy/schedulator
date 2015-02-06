package StaticUtils;

/**
 * Created by ziggyzaggy on 05/02/2015.
 * "static" class with static utility functions
 */
public final class Utils {
    private Utils(){

    }

    //convert ints to boolean depending if the number is more than 0
    public static boolean boolConverter(int val) {
        return (val > 0);
    }

    //a simple implementation of C# tryparse() function,
    //returns true if string can be parsed into integer
    public static boolean intTryParse(String str){
        try
        {
            Integer.parseInt(str);
            return true;
        } catch(NumberFormatException nfe)
        {
            return false;
        }
    }

}
