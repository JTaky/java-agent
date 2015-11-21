package com.jtaky.logger.agent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public class ParameterStorage {

	//TODO
	//add log4j
	//add implement thread local aggregation with stack logic
	private static ThreadLocal<Stack<MethodCall>> methodCallHistory = new ThreadLocal<>();
    //in order to stop hooks when agent code is executed
    private static ThreadLocal<Boolean> isHookInTheStack = new ThreadLocal<>();

    public static void beforeMethod(Class<?> clazz, String methodName) {
		beforeMethod(clazz, methodName, new Object[0]);
	}

	public static void beforeMethod(Class<?> clazz, String methodName, Object[] args) {
        if(isHookInTheStack()){
            //was executed by agent
            return;
        }
		try {
            enterInTheHook();
            Stack<MethodCall> callHistory = methodCallHistory.get();
            if(callHistory == null){
                callHistory = new Stack<>();
                methodCallHistory.set(callHistory);
            }
            MethodCall methodCall = new MethodCall(clazz, StackTraceUtil.getPrevClassStackTraceElement(), args);
			callHistory.push(methodCall);

            log(String.format("enter: %s", methodCall.toString()));
            checkTriggerCase(methodCall);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
            leaveHook();
        }
    }

    private static void checkTriggerCase(MethodCall methodCall) {
        if(HookRegister.isTriggerCase(methodCall)){
            HookRegister.triggerHook(methodCallHistory.get(), new IContext.MethodContext(methodCall));
        }
    }

    public static void afterMethod(Class<?> clazz, String methodName, Class<?> resultType, Object resultValue) {
        if (isHookInTheStack()) {
            return;
        }
        try {
            enterInTheHook();
            String msgPattern = "leave: %s %s(return %s)";
            String msg = String.format(msgPattern, String.valueOf(resultType), methodName, String.valueOf(resultValue));
            log(msg);
            Stack<MethodCall> callHistory = methodCallHistory.get();
            assert callHistory != null;
            callHistory.pop();
        } finally {
            leaveHook();
        }
    }

    public static void exceptionHappened(Class<?> clazz, String methodName, Object[] args, Exception e) {
        try {
            enterInTheHook();
            HookRegister.triggerHook(methodCallHistory.get(), new IContext.ExceptionContext(e));
        } finally {
            leaveHook();
        }
    }

    public static List<MethodCall> findMethodCall(String className, String methodName) {
        Stack<MethodCall> methodCallStack = methodCallHistory.get();
        if(methodCallStack == null){
            return new ArrayList<>();
        }
        return methodCallStack.stream()
                .filter((m) ->  m.className().endsWith(className) && m.methodName().equals(methodName))
                .collect(Collectors.toList());
    }

    public static boolean isHookInTheStack() {
        return isHookInTheStack.get() == null? false : isHookInTheStack.get();
    }

    private static boolean enterInTheHook() {
        Boolean isHookInTheStackLocal = isHookInTheStack.get();
        isHookInTheStack.set(true);
        return isHookInTheStackLocal == null? false : isHookInTheStackLocal;
    }

    private static boolean leaveHook() {
        Boolean isHookInTheStackLocal = isHookInTheStack.get();
        isHookInTheStack.set(false);
        return isHookInTheStackLocal == null? false : isHookInTheStackLocal;
    }

    private static void log(String msg) {
//		System.out.println(msg);
		try (PrintWriter out = new PrintWriter(new FileOutputStream(new File(
				"/tmp/out.log"), true))) {
			out.println(msg);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
