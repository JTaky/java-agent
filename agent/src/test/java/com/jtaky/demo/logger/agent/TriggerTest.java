package com.jtaky.demo.logger.agent;

import com.jtaky.logger.agent.HookRegister;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

public class TriggerTest {

    private AtomicBoolean appenderInvoked = new AtomicBoolean();

    @Before
    public void setUp(){
        appenderInvoked.set(false);
        HookRegister.registerAppender(methodCalls -> appenderInvoked.set(true));
    }

    @Test
    public void testAppenderOnTriggerSlf4j(){
        org.slf4j.Logger log = LoggerFactory.getLogger(TriggerTest.class);
        log.error("test");
        // Assert.assertTrue("expected hook worked", appenderInvoked.get());    //TODO fix test
    }

    @Test
    public void testAppenderOffTriggerSlf4j(){
        org.slf4j.Logger log = LoggerFactory.getLogger(TriggerTest.class);
        log.error("test");
        // Assert.assertTrue("expected didn't hook work", !appenderInvoked.get()); //TODO fix test
    }

}