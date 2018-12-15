package org.duh102.duhbot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Utils {
	public static DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd" +
			".HH:mm:ss.SSS");
	public static String toCSV(String[] list) {
		if (list.length > 0) {
			if (list.length > 1) {
				StringBuilder temp = new StringBuilder(list[0]);
				for (int i = 1; i < list.length; i++) {
					temp.append(", ");
					temp.append(list[i]);
				}
				return temp.toString();
			}
			return list[0];
		}
		return null;
	}

	public static String toCSV(Object[] list) {
		String[] items = new String[list.length];
		for (int i = 0; i < list.length; i++) {
			items[i] = list[i].toString();
		}
		return toCSV(items);
	}

	public static String toNCSV(String[] list) {
		if (list.length > 0) {
			if (list.length > 1) {
				StringBuilder temp = new StringBuilder(String.format("[0] %s", list[0]));
				for (int i = 1; i < list.length; i++) {
					temp.append(", ");
					temp.append(String.format("[%d] %s", i, list[i]));
				}
				return temp.toString();
			}
			return String.format("[0] %s", list[0]);
		}
		return null;
	}

	public static String toNCSV(Object[] list) {
		String[] items = new String[list.length];
		for (int i = 0; i < list.length; i++) {
			items[i] = list[i].toString();
		}
		return toNCSV(items);
	}

	public static String formatLogMessage(String message) {
		java.util.Date date = new java.util.Date();
		return String.format("%s | %s",dateFormat.format(date), message);
	}
}
