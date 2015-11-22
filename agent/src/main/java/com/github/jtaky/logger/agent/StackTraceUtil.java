package com.github.jtaky.logger.agent;

public class StackTraceUtil {

    private static final int currentSteIndex = 2;

    public static StackTraceElement getCurrentStackTraceElement(){
        StackTraceElement[] stes = Thread.currentThread().getStackTrace();
        assert stes.length > 1;
        return stes[currentSteIndex];
    }

    public static StackTraceElement getPrevMethodStackTraceElement(){
        StackTraceElement[] stes = Thread.currentThread().getStackTrace();
        assert stes.length > 1;
        StackTraceElement curSte = stes[currentSteIndex];
        for(int i = currentSteIndex; i < stes.length; i++){
            if(!curSte.getMethodName().equals(stes[i].getMethodName())){
                return stes[i];
            }
        }
        return new StackTraceElement("Unknown class", "Unknown method", "Unknown file", -1);
    }

    public static StackTraceElement getPrevClassStackTraceElement(){
        StackTraceElement[] stes = Thread.currentThread().getStackTrace();
        assert stes.length > 1;
        StackTraceElement curSte = stes[currentSteIndex];
        for(int i = currentSteIndex; i < stes.length; i++){
            if(!curSte.getClassName().equals(stes[i].getClassName())){
                return stes[i];
            }
        }
        return new StackTraceElement("Unknown class", "Unknown method", "Unknown file", -1);
    }

}
