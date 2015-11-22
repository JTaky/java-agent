package com.github.jtaky.logger.agent;

public class TriggerFactory {

    public static ITriggerCase newMethodTrigger(String className, String methodName){
        return methodCall -> className.equals(methodCall.className()) && methodName.equals(methodCall.methodName());
    }

    public static ITriggerCase triggerAsInstanceOf(Class<?> clazz){
        return methodCall -> clazz.isAssignableFrom(methodCall.clazz) && "<init>".equals(methodCall.methodName());
    }

}
