package com.jtaky.logger.agent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Arrays;

public class ParameterStorage {

	public static void beforeMethod(String methodName) {
		beforeMethod(methodName, new Object[0]);
	}

	public static void beforeMethod(String methodName, Object[] args) {
		try {
			String msg = String.format("enter: %s(", methodName);
			if (args != null) {
				for (Object o : args) {
					 if(o != null){
					 	msg += o.toString() + ", ";
					 } else {
						msg += "null, ";
					 }
				}
			}
			msg += ")";
			log(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void afterMethod(String methodName, Object resultValue){
		String msg = String.format("leave: %s(", methodName);
		msg += String.valueOf(resultValue);
		msg += ")";
		log(msg);
	}	

	private static void log(String msg) {
		try (PrintWriter out = new PrintWriter(new FileOutputStream(new File(
				"/tmp/out.log"), true))) {
			out.println(msg);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
