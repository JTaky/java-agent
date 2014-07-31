package com.jtaky.logger.agent;

public class TriggerFactory {

    public static ITriggerCase exactMethodTrigger(String className, String methodName){
        return methodCall -> className.equals(methodCall.className) && methodName.equals(methodCall.methodName);
    }

}
