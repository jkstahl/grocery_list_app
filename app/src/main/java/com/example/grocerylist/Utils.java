package com.example.grocerylist;

public class Utils {

	public static String[] concatArrays(String[] array1,
			String[] array2) {
		String[] newArray = new String[array1.length + array2.length];
		for (int i = 0; i < array1.length; i++)
			newArray[i] = array1[i];
		for (int i = 0; i < array2.length; i++)
			newArray[array1.length + i] = array2[i];
		
		return newArray;
	}

}
