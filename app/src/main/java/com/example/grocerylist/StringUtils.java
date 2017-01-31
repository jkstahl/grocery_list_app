package com.example.grocerylist;

import java.util.List;

/**
 * Created by neoba on 1/29/2017.
 */

public class StringUtils {
    public static String join(String[] array, String joinString) {
        String returnString = "";
        if (array.length > 0)
            returnString += array[0];
        for (int i=1; i<array.length; i++){
            returnString += (joinString + array[i]);
        }

        return returnString;
    }

    public static String join(List<String> array, String joinString) {
        return join((String[])array.toArray(new String[array.size()]), joinString);
    }
}
