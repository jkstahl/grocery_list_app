package com.example.grocerylist;

import java.util.Calendar;

/**
 * Created by neoba on 1/17/2017.
 */

public class DaysTracker {
    private String[] days = { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

    public String getDayFromPosition(int position) {
        //make days of the week relative to current
        Calendar c = Calendar.getInstance();
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK)-1;


        //int shiftedPosition = position - dayOfWeek;
        //return days[ shiftedPosition < 0 ? shiftedPosition + days.length : shiftedPosition];
        return days[(dayOfWeek + position) % days.length];
    }

    public int getDaysLength() {
        return days.length;
    }



}
