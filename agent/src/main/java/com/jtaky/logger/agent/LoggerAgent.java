package com.jtaky.logger.agent;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;

public class LoggerAgent {
	
	public static void premain(String agentArgument, Instrumentation instrumentation){
		System.out.println("Test Java Agent(premain)");
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            System.out.println("Exception in Thread - " + t.getName());
            e.printStackTrace();
        });
        retransformClasses(instrumentation);
        instrumentation.addTransformer(new MethodHacker());
//        retransformClasses(instrumentation);
    }

    public static void agentmain(String agentArgument, Instrumentation instrumentation){
		System.out.println("Test Java Agent(agentmain)");
	}

    private static void retransformClasses(Instrumentation instrumentation) {
        try {
            if(instrumentation.isRetransformClassesSupported()) {
                instrumentation.retransformClasses(Exception.class);
            }
        } catch (UnmodifiableClassException e) {
            e.printStackTrace();
        }
    }

}
