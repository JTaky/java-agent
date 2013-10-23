package com.jtaky.demo.logger.agent;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class MoreUnitTests {

	private static void log(String msg) {
		try (PrintWriter out = new PrintWriter(new FileOutputStream(new File(
				"/tmp/test_out1.log"), true))) {
			out.println(msg);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private double f(int a, double b){
		log(a + ", " + b);
		return a + b;
	}
	
	@Before
	public void setUp(){
		log("Before");
	}
	
	@Test
	public void testJava(){
		log(getClass().getName() + ".testJava() method");
		f(12, 0.33);
	}

}
