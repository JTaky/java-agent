package com.jtaky.logger.agent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.ArrayList;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

public class MethodHacker implements ClassFileTransformer {

	@SuppressWarnings("serial")
	private static final List<String> magicClassPatterns = new ArrayList<String>() {
		{
			this.add("java.lang.Shutdown.*");
			this.add("sun.reflect.*");
			this.add("sun.*");
			this.add(".*org.gradle.*");
			this.add(".*java.*");
			this.add("com.jtaky.logger.agent.*");
			this.add("org.slf4j..*");
		}
	};

	@SuppressWarnings("serial")
	private static final List<String> inspectoredClassPatterns = new ArrayList<String>() {
		{
			this.add("com.jtaky.*");
		}
	};

	private static boolean isIgnoredClassName(String className) {
		for (String magicClassPattern : magicClassPatterns) {
			if (className.matches(magicClassPattern)) {
				return true;
			}
		}
		return false;
	}

	private static boolean isInspectoredClassName(String className) {
		for (String magicClassPattern : inspectoredClassPatterns) {
			if (className.matches(magicClassPattern)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public byte[] transform(ClassLoader loader, String className,
			Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] classfileBuffer) throws IllegalClassFormatException {
		String dotClassName = getDotClassName(className);
		if (isIgnoredClassName(dotClassName)) {
			return classfileBuffer;
		}  else if (isInspectoredClassName(dotClassName)) {
			try {
				ClassPool cp = ClassPool.getDefault();
				CtClass cc = cp.get(dotClassName);
				for (CtMethod m : cc.getDeclaredMethods()) {
					if (isHackeableMethod(m)) {
						Class<?> clazz = com.jtaky.logger.agent.ParameterStorage.class; //force class load
						String beforeMethodCall =
						 "try { " +
						 	"com.jtaky.logger.agent.ParameterStorage.beforeMethod(" +
						 		"\"" + m.getLongName() + "\"" +
						 		", $args " +
						 	"); " +
						 "} catch(Exception e) { " + 
						 	"e.printStackTrace(); " +
						 "}";
						 m.insertBefore(beforeMethodCall);
						 String afterMethodCall =
						 "try { " +
						 	"com.jtaky.logger.agent.ParameterStorage.afterMethod(" +
						 		"\"" + m.getLongName() + "\"" +
						 		", ($w)$_" +
						 	"); " +
						 "} catch(Exception e) { " + 
						 	"e.printStackTrace(); " +
						 "}";						 
						 m.insertAfter(afterMethodCall);
					}
				}
				classfileBuffer = cc.toBytecode();
				cc.detach();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return classfileBuffer;
	}

	private boolean isHackeableMethod(CtMethod m) {
		return !Modifier.isAbstract(m.getModifiers())
				&& !Modifier.isNative(m.getModifiers())
				&& !m.getLongName().contains("println")
				&& !m.getLongName().matches(".*access\\$\\d+$");
	}

	private String getDotClassName(String className) {
		return className.replaceAll("/", ".");
	}

	private void log(String msg) {
		System.out.println(msg);
	}

}
