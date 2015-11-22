package com.jtaky.logger.agent;

public class CodeFormatter {

    private static String beforeMethodCallFormat =
            "try { " +
                "com.jtaky.logger.agent.ParameterStorage.beforeMethod(%s, \"%s\", $args ); " +
            "} catch(Exception e) { " +
                "e.printStackTrace(); " +
            "}";

    private static String afterMethodCallFormat =
            "try { " +
                "com.jtaky.logger.agent.ParameterStorage.afterMethod(%s, \"%s\", %s, ($w)$_); " +
            "} catch(Exception e) { " +
                "e.printStackTrace(); " +
            "}";

    private static String beforeConstructorCallFormat =
            "try { " +
                "com.jtaky.logger.agent.ParameterStorage.beforeMethod(%s, \"%s\", $args ); " +
            "} catch(Exception e) { " +
                "e.printStackTrace(); " +
            "}";

    private static String afterConstructorCallFormat =
            "try { " +
                "com.jtaky.logger.agent.ParameterStorage.afterMethod(%s, \"%s\", Void.TYPE, ($w)$_); " +
            "} catch(Exception e) { " +
                "e.printStackTrace(); " +
            "}";

    private static String catchClauseFormat =
            "{ " +
                "com.jtaky.logger.agent.ParameterStorage.exceptionHappened(%s, \"%s\", $args, $e); throw $e;" +
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
