package com.jtaky.logger.agent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public class ParameterStorage {

	//TODO
	//add log4j
	//add implement thread local aggregation with stack logic
	private static ThreadLocal<Stack<MethodCall>> methodCallHistory = new ThreadLocal<Stack<MethodCall>>();

	public static void beforeMethod(String methodName) {
		beforeMethod(methodName, new Object[0]);
	}

	public static void beforeMethod(String methodName, Object[] args) {
		try {
            Stack<MethodCall> callHistory = methodCallHistory.get();
            if(callHistory == null){
                callHistory = new Stack<>();
                methodCallHistory.set(callHistory);
            }
            StackTraceElement stackTraceElement = StackTraceUtil.getPrevClassStackTraceElement();
            MethodCall methodCall = new MethodCall(stackTraceElement.getClassName(), stackTraceElement.getMethodName(), args);
			callHistory.push(methodCall);

            log(String.format("enter: %s", methodCall.toString()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void afterMethod(String methodName, Class<?> resultType, Object resultValue){
		String msgPattern = "leave: %s %s(%s)";
		String msg = String.format(msgPattern, String.valueOf(resultType), methodName, String.valueOf(resultValue));
		log(msg);
		Stack<MethodCall> callHistory = methodCallHistory.get();
		assert callHistory != null;
		if(callHistory != null){
			callHistory.pop();
		}
	}

	private static void log(String msg) {
		System.out.println(msg);
		try (PrintWriter out = new PrintWriter(new FileOutputStream(new File(
				"/tmp/out.log"), true))) {
			out.println(msg);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

    public static List<MethodCall> findMethodCall(String className, String methodName) {
        Stack<MethodCall> methodCallStack = methodCallHistory.get();
        if(methodCallStack == null){
            return new ArrayList<>();
        }
        return methodCallStack.stream()
                .filter((m) ->  m.className.endsWith(className) && m.methodName.equals(methodName))
                .collect(Collectors.toList());
    }
}
