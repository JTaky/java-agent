package com.jtaky.logger.agent;

import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.instrument.Instrumentation;

public class LoggerAgent {
	
	private static MethodHacker methodHacker = new MethodHacker();
	
	public static void premain(String agentArgument,
            Instrumentation instrumentation){
		System.out.println("Test Java Agent(premain)");
		instrumentation.addTransformer(methodHacker);
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				System.out.println("Exception in Thread - " + t.getName());
				e.printStackTrace();
			}
		});
	}
	
	public static void agentmain(String agentArgs, Instrumentation inst){
		System.out.println("Test Java Agent(agentmain)");
	}

}
