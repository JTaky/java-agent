package com.github.jtaky.logger.agent;

import org.junit.Assert;
import org.junit.Test;

public class StackTraceUtilTest {

    @Test
    public void testCurrentElement(){
        StackTraceElement cste = StackTraceUtil.getCurrentStackTraceElement();
        Assert.assertEquals("Expected equal class", getClass().getName(), cste.getClassName());
        Assert.assertEquals("Expected equal methods", "testCurrentElement", cste.getMethodName());
    }

    @Test
    public void testPrevMethodElement(){
        testPrevMethodElementInternal();
    }

    public void testPrevMethodElementInternal(){
        StackTraceElement cste = StackTraceUtil.getPrevMethodStackTraceElement();
        Assert.assertEquals("Expected equal class", getClass().getName(), cste.getClassName());
        Assert.assertEquals("Expected equal methods", "testPrevMethodElement", cste.getMethodName());
    }

    @Test
    public void testPrevClassElement(){
        StackTraceElement cste = StackTraceUtil.getPrevClassStackTraceElement();
        Assert.assertEquals("Expected equal class", "sun.reflect.NativeMethodAccessorImpl", cste.getClassName());
        Assert.assertEquals("Expected equal methods", "invoke0", cste.getMethodName());
    }

}
