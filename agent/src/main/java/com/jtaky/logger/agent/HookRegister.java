package com.jtaky.logger.agent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class HookRegister {

    private static final ITraceAppender systemOutAppender
            = methodCalls -> methodCalls.stream().forEach(System.out::println);

    private static List<ITraceAppender> appenders = new ArrayList<ITraceAppender>(){{
        add(systemOutAppender);
    }};
    private static List<ITriggerCase> triggers = new ArrayList<ITriggerCase>(){{
        add(TriggerFactory.exactMethodTrigger("org.slf4j.Logger", "error"));
        add(TriggerFactory.exactMethodTrigger("java.util.logging.Logger", "error"));
        add(TriggerFactory.exactMethodTrigger("ch.qos.logback.classic.Logger", "error"));
        add(TriggerFactory.exactMethodTrigger("org.apache.log4j.Logger", "error")); //log4j 1.x
        add(TriggerFactory.exactMethodTrigger("org.apache.logging.log4j.Logger", "error")); //log4j 2.x
    }};

    static boolean isTriggerCase(MethodCall methodCall){
        for(ITriggerCase triggerCase : triggers){
            if(triggerCase.isTriggerCase(methodCall)){
                return true;
            }
        }
        return false;
    }

    static void output(Stack<MethodCall> methodCalls) {
        appenders.stream().forEach((appender) -> appender.output(Collections.unmodifiableList(methodCalls)) );
    }

    public static void registerTriggerCase(ITriggerCase triggerCase){
        triggers.add(triggerCase);
    }

    public static void registerAppender(ITraceAppender appender){
        appenders.add(appender);
    }

}
