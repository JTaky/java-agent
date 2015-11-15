package com.jtaky.demo.logger.agent;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class Exampler {

	private static void log(String msg) {
		try (PrintWriter out = new PrintWriter(new FileOutputStream(new File(
				"/tmp/test_out.log"), true))) {
			out.println(msg);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void example(){
		log("Test is started");
		Assert.assertTrue(1 > 0);
	}

}
