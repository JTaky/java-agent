package com.github.jtaky.demo.logger.agent;

import com.github.jtaky.logger.agent.HookRegister;
import com.github.jtaky.logger.agent.TriggerFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

public class TriggerTest {

    interface TriggerMarkerInterface {}
    static class CustomObject implements TriggerMarkerInterface {}

    private static org.slf4j.Logger log = LoggerFactory.getLogger(TriggerTest.class);

    //we want to modify state in the lambdas
    private AtomicBoolean appenderInvoked = new AtomicBoolean();

    @Before
    public void setUp(){
        appenderInvoked.set(false);
    }

    @Test
    public void testAppenderOffTriggerSlf4j(){
        log.error("test");
        Assert.assertTrue("trigger wasn't executed", !appenderInvoked.get());
    }

    @Test
    public void testAppenderOnTriggerSlf4j(){
        HookRegister.registerAppender((methodCalls, context)-> appenderInvoked.set(true));
        log.error("test");
        Assert.assertTrue("org.slf4j.Logger#error should be registered by default", appenderInvoked.get());
    }

    @Test
    public void testAppenderOnTriggerCustomException(){
        HookRegister.registerTriggerCase(TriggerFactory.triggerAsInstanceOf(TriggerMarkerInterface.class));
        HookRegister.registerAppender((methodCalls, context)-> appenderInvoked.set(true));
        new CustomObject();
        Assert.assertTrue("exception trigger was executed", appenderInvoked.get());
    }

    @Test
    public void testWeCatchSomeException(){
        boolean exceptionIsHandled = false;
        try {
            exceptionMethod(5);
        } catch (RuntimeException e){
            exceptionIsHandled = true;
        }
        Assert.assertTrue("exception is handled", exceptionIsHandled);
    }

    @Test
    public void testExceptionIsHandled(){
        HookRegister.registerAppender((methodCalls, context) -> appenderInvoked.set(true));
        try {
            exceptionMethod(5);
        } catch (RuntimeException e){}
        Assert.assertTrue("exception trigger was handled ", appenderInvoked.get());
    }

    private void exceptionMethod(int t) {
        throw new RuntimeException(t + ", exception is here");
    }

}