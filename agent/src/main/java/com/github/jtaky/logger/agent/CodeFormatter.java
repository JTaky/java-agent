package com.github.jtaky.logger.agent;

public class CodeFormatter {

    private static String packageName = CodeFormatter.class.getPackage().getName();
    private static String packagePrefix = packageName + ".";

    private static String beforeMethodCallFormat =
            "try { " +
                packagePrefix + "ParameterStorage.beforeMethod(%s, \"%s\", $args ); " +
            "} catch(Exception e) { " +
                "e.printStackTrace(); " +
            "}";

    private static String afterMethodCallFormat =
            "try { " +
                packagePrefix + "ParameterStorage.afterMethod(%s, \"%s\", %s, ($w)$_); " +
            "} catch(Exception e) { " +
                "e.printStackTrace(); " +
            "}";

    private static String beforeConstructorCallFormat =
            "try { " +
                packagePrefix + "ParameterStorage.beforeMethod(%s, \"%s\", $args ); " +
            "} catch(Exception e) { " +
                "e.printStackTrace(); " +
            "}";

    private static String afterConstructorCallFormat =
            "try { " +
                packagePrefix + "ParameterStorage.afterMethod(%s, \"%s\", Void.TYPE, ($w)$_); " +
            "} catch(Exception e) { " +
                "e.printStackTrace(); " +
            "}";

    private static String catchClauseFormat =
            "{ " +
                packagePrefix + "ParameterStorage.exceptionHappened(%s, \"%s\", $args, $e); throw $e;" +
            "}";

    public String formatBeforeMethod(String className, String methodName){
        return String.format(beforeMethodCallFormat, className + ".class", methodName);
    }

    public String formatAfterMethod(String className, String methodName, String returnClassName){
        return String.format(afterMethodCallFormat, className + ".class", methodName, returnClassName);
    }

    public String formatCatchClause(String className, String methodName){
        return String.format(catchClauseFormat, className + ".class", methodName);
    }

}
