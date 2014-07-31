package com.jtaky.logger.agent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.lang.reflect.Modifier;
import java.text.MessageFormat;
import java.util.List;
import java.util.ArrayList;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtPrimitiveType;
import javassist.CtMethod;

/**
 * TODO:
 * 1. Read and inject configuration
 * 2. Move code to formatter
 */
public class MethodHacker implements ClassFileTransformer {

	private static String beforeMethodCallFormat =
		"try { " +
			"com.jtaky.logger.agent.ParameterStorage.beforeMethod(\"%s\", $args ); " +
		"} catch(Exception e) { " + 
			"e.printStackTrace(); " +
		"}";

	private static String afterMethodCallFormat =
		"try { " +
			"com.jtaky.logger.agent.ParameterStorage.afterMethod(\"%s\", %s, ($w)$_); " +
		"} catch(Exception e) { " + 
			"e.printStackTrace(); " +
		"}";

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
			this.add("com.jtaky.demo.*");
            // this.add("java.util.logging.Logger");
            // this.add("ch.qos.logback.classic.Logger");
            //this.add("org.apache.log4j.Logger");
            //this.add("org.apache.logging.log4j.Logger");
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
		}  else
        if (isInspectoredClassName(dotClassName)) {
			try {
                System.out.println("Transform class - " + dotClassName);
				ClassPool cp = ClassPool.getDefault();
				CtClass cc = cp.get(dotClassName);
				for (CtMethod m : cc.getDeclaredMethods()) {
					if (isHackeableMethod(m)) {
						String beforeMethodCall = String.format(beforeMethodCallFormat, m.getLongName());
						m.insertBefore(beforeMethodCall);
						String retClassName = getClassOrWrapperName(m.getReturnType());
						String afterMethodCall = String.format(afterMethodCallFormat, m.getLongName(), retClassName + ".class");
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

	private String getClassOrWrapperName(CtClass cc) throws Exception {
		if(cc.isPrimitive()){
			return Class.forName(((CtPrimitiveType)cc).getWrapperName()).getName();
		} else {
            return cc.getName();
		}
	}

	private boolean isHackeableMethod(CtMethod m) {
		return !Modifier.isAbstract(m.getModifiers())
				&& !Modifier.isNative(m.getModifiers())
				&& !m.getLongName().matches(".*access\\$\\d+$");
	}

	private String getDotClassName(String className) {
		return className.replaceAll("/", ".");
	}

	private void log(String msg) {
		System.out.println(msg);
	}

}
