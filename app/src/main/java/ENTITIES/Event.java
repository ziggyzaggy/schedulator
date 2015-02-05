package ENTITIES;

import StaticUtils.Utils;

/**
 * Created by Ziggyzaggy on 21/01/2015.
 * Event entity, should be populated with values from a table
 */
public class Event {



    int day, month, year, numAccepted, numPending, numMinePending;
    boolean hasAccepted, hasPending, hasMinePending;


    public Event(int day, int month, int year, int numAccepted, int numPending, int numMinePending) {
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
    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
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
