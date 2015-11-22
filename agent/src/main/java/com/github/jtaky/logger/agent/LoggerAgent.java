package com.github.jtaky.logger.agent;

import com.github.jtaky.logger.agent.config.AgentConfig;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;

public class LoggerAgent {
	
	public static void premain(String agentArgument, Instrumentation instrumentation){
        try {
            System.out.println("Test Java Agent(premain), with argument - " + agentArgument);
            Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
                System.out.println("Exception in Thread - " + t.getName());
                e.printStackTrace();
            });

            AgentConfig config = new AgentConfig(agentArgument);
            retransformClasses(instrumentation);
            instrumentation.addTransformer(new MethodHacker(config), true);
            retransformClasses(instrumentation);
        } catch (IOException e) {
            new IllegalArgumentException("Cannot load agent config", e);
        }
    }

    public static void agentmain(String agentArgument, Instrumentation instrumentation){
		System.out.println("Test Java Agent(agentmain)");
	}

    private static void retransformClasses(Instrumentation instrumentation) {
        try {
            if(instrumentation.isRetransformClassesSupported()) {
                instrumentation.retransformClasses(Exception.class);
                instrumentation.retransformClasses(RuntimeException.class);
            }
        } catch (UnmodifiableClassException e) {
            e.printStackTrace();
        }
    }

}
