package ENTITIES;

/**
 * Created by Ziggyzaggy on 21/01/2015.
 * Event entity, should be populated with values from a table
 */
public class Event {



    int day, month, year;

    public Event(int day, int month, int year){
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public Event(){

    }







    //getters setters
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

}
