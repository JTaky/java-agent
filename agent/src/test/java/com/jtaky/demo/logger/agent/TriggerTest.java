package com.jtaky.demo.logger.agent;

import com.jtaky.logger.agent.HookRegister;
import com.jtaky.logger.agent.TriggerFactory;
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
        HookRegister.registerAppender(methodCalls -> appenderInvoked.set(true));
        log.error("test");
        Assert.assertTrue("org.slf4j.Logger#error should be registered by default", appenderInvoked.get());
    }

    @Test
    public void testAppenderOnTriggerCustomException(){
        HookRegister.registerTriggerCase(TriggerFactory.triggerAsInstanceOf(TriggerMarkerInterface.class));
        HookRegister.registerAppender(methodCalls -> appenderInvoked.set(true));
        new CustomObject();
        Assert.assertTrue("exception trigger was executed", appenderInvoked.get());
    }

}