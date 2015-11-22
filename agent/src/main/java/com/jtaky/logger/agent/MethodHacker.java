package com.jtaky.logger.agent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.ArrayList;

import com.sun.org.apache.bcel.internal.classfile.Code;
import javassist.*;
import javassist.bytecode.ExceptionsAttribute;

/**
 * TODO:
 * 1. Read and inject configuration
 */
public class MethodHacker implements ClassFileTransformer {

	private CodeFormatter codeFormatter = new CodeFormatter();

	@SuppressWarnings("serial")
	private static final List<String> magicClassPatterns = new ArrayList<String>() {
		{
			this.add("sun.*");
            this.add("sun.reflect.*");
			this.add("org.gradle.*");
			this.add("java.*");
            this.add("java.lang.Shutdown.*");
			this.add("com.jtaky.logger.agent.*");
		}
	};

	@SuppressWarnings("serial")
	private static final List<String> inspectoredClassPatterns = new ArrayList<String>() {
		{
			this.add("com.jtaky.demo.*");
            this.add("ch.qos.logback..*");
            this.add("org.apache.log4j..*");
            this.add("org.apache.logging.log4j..*");
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
                log("Transform class - " + dotClassName);
				ClassPool cp = ClassPool.getDefault();
				CtClass cc = cp.get(dotClassName);
				for (CtMethod m : cc.getDeclaredMethods()) {
					if (isHackeableMethod(m)) {
						m.insertBefore(
								codeFormatter.formatBeforeMethod(dotClassName, m.getLongName())
						);

						m.insertAfter(
								codeFormatter.formatAfterMethod(dotClassName, m.getLongName(), getClassOrWrapperName(m.getReturnType()) + ".class")
						);

						m.addCatch(
								codeFormatter.formatCatchClause(dotClassName, m.getLongName()), cp.get("java.lang.Exception")
						);
					}
				}
                for (CtConstructor ct : cc.getConstructors()) {
                    if (isHackeableMethod(ct)) {
                        ct.insertBefore(
                                codeFormatter.formatBeforeMethod(dotClassName, ct.getLongName())
                        );

                        ct.insertAfter(
                                codeFormatter.formatAfterMethod(dotClassName, ct.getLongName(), "Void.TYPE")
                        );

                        ct.addCatch(
                                codeFormatter.formatCatchClause(dotClassName, ct.getLongName()), cp.get("java.lang.Exception")
                        );
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

	private boolean isHackeableMethod(CtBehavior m) {
		return !Modifier.isAbstract(m.getModifiers())
				&& !Modifier.isNative(m.getModifiers())
				&& !m.getLongName().matches(".*access\\$\\d+$");
	}

	private String getDotClassName(String className) {
		return className.replaceAll("/", ".");
	}

	private void log(String msg) {
		//System.out.println(msg);
	}

}
