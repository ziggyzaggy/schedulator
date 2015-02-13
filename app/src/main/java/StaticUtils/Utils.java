package StaticUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    //compare 2 lists to eachother
    //return true if lists are same
    public static  boolean equalLists(List<Integer> one, List<Integer> two){
        if (one == null && two == null){
            return true;
        }

        if((one == null && two != null)
                || one != null && two == null
                || one.size() != two.size()){
            return false;
        }

        //to avoid messing the order of the lists we will use a copy
        //as noted in comments by A. R. S.
        one = new ArrayList<>(one);
        two = new ArrayList<>(two);

        Collections.sort(one);
        Collections.sort(two);
        return one.equals(two);
    }
}
