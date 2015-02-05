package ENTITIES;

import StaticUtils.Utils;

/**
 * Created by Ziggyzaggy on 21/01/2015.
 * Event entity, should be populated with values from a table
 */
public class Event {



    String day, month, year;
    int numAccepted, numPending, numMinePending;
    boolean hasAccepted, hasPending, hasMinePending;


    public Event(String day, String month, String year, int numAccepted, int numPending, int numMinePending) {
        this.day = day;
        this.month = month;
        this.year = year;
        this.numAccepted = numAccepted;
        this.numPending = numPending;
        this.numMinePending = numMinePending;

        this.hasAccepted = Utils.boolConverter(numAccepted);
        this.hasPending = Utils.boolConverter(numPending);
        this.hasMinePending = Utils.boolConverter(numMinePending);


    }

    public Event(){

    }



    //region getters setters

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public int getNumAccepted() {
        return numAccepted;
    }

    public void setNumAccepted(int numAccepted) {
        this.numAccepted = numAccepted;
    }

    public int getNumPending() {
        return numPending;
    }

    public void setNumPending(int numPending) {
        this.numPending = numPending;
    }

    public int getNumMinePending() {
        return numMinePending;
    }

    public void setNumMinePending(int numMinePending) {
        this.numMinePending = numMinePending;
    }

    public boolean isHasAccepted() {
        return hasAccepted;
    }

    public void setHasAccepted(boolean hasAccepted) {
        this.hasAccepted = hasAccepted;
    }

    public boolean isHasPending() {
        return hasPending;
    }

    public void setHasPending(boolean hasPending) {
        this.hasPending = hasPending;
    }

    public boolean isHasMinePending() {
        return hasMinePending;
    }

    public void setHasMinePending(boolean hasMinePending) {
        this.hasMinePending = hasMinePending;
    }


    //endregion
}
