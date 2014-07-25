package com.jtaky.logger.agent;

import java.util.List;
import java.util.ArrayList;

/**
 * Value object for method call.
 * TODO need to be super effective. Research ability to use aligned byte[] array.
 */
public class MethodCall {

	public final String methodName;
	public final List<String> args;

	public MethodCall(String methodName, Object[] args){
		this.methodName = methodName;
		this.args = toUnmodifableStringsList(args);
	}

	@Override
	public String toString(){
		return methodName + "(" + getFormattedArgs() + ")";
	}	

	private List<String> toUnmodifableStringsList(Object[] args){
		List<String> argsStrs = new ArrayList<>(args.length);
		for(Object curArg : args){
			argsStrs.add(String.valueOf(curArg));
		}
		return argsStrs;
	}

	private String getFormattedArgs(){
		StringBuilder formatterArgs = new StringBuilder();
		for(int i = 0; i < args.size() - 1; i++){
			formatterArgs.append(formatArg(args.get(i - 1)));
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
		if(arg instanceof Object)
			return "'" + arg.toString() + "'";
		return String.valueOf(arg);
	}

}