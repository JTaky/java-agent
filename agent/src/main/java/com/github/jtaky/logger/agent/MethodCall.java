package com.github.jtaky.logger.agent;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

/**
 * Value object for method call.
 * TODO need to be super effective. Research ability to use aligned byte[] array.
 */
public class MethodCall {

    public final Class<?> clazz;
	public final StackTraceElement stackTraceElement;
	public final List<Object> args;

	public MethodCall(Class<?> clazz, StackTraceElement stackTraceElement, Object[] args){
        this.clazz = clazz;
		this.stackTraceElement = stackTraceElement;
		this.args = Arrays.asList(args);
	}

    public String className(){
        return clazz.getName();
    }

	public String methodName(){
		return stackTraceElement.getMethodName();
	}

	@Override
	public String toString(){
		return className() + "." + methodName() + "(" + getFormattedArgs() + ")";
	}	

	private List<String> toUnmodifiableStringsList(Object[] args){
		List<String> argsStr = new ArrayList<>(args.length);
		for(Object curArg : args){
			argsStr.add(String.valueOf(curArg));
		}
		return argsStr;
	}

	private String getFormattedArgs(){
		StringBuilder formatterArgs = new StringBuilder();
		for(int i = 0; i < args.size() - 1; i++){
			formatterArgs.append(formatArg(args.get(i)));
			formatterArgs.append(", ");
		}
		if(!args.isEmpty()){
			formatterArgs.append(args.get(args.size() - 1));
		}
		return formatterArgs.toString();
	}

	private String formatArg(Object arg){
		if(arg == null)
			return "'null'";
        if(arg instanceof String)
            return "\"" + arg + "\"";
		if(arg instanceof MethodCall)
			return "methodCall - " + ((MethodCall)arg).methodName();
		if(arg instanceof Object)
			return "'" + arg.toString() + "'";
		return String.valueOf(arg);
	}

}