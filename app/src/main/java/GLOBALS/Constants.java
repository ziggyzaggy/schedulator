package GLOBALS;

import StaticUtils.Utils;

/**
 * Created by kristjan on 03/03/2015.
 */
public final class Constants {

    public enum navIds{
        MyCalendar, Pepperoni, Pizza, Beef
    }

    public static String[] navIdNames() {
        navIds[] states = navIds.values();
        String[] names = new String[states.length];

        for (int i = 0; i < states.length; i++) {
            names[i] = Utils.splitCamel(states[i].name());
        }

        return names;
    }

}
