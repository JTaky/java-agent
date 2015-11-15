package com.jtaky.demo.logger.agent;

import com.jtaky.logger.agent.HookRegister;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

public class TriggerTest {

    private static org.slf4j.Logger log = LoggerFactory.getLogger(TriggerTest.class);

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
         Assert.assertTrue("trigger was executed", appenderInvoked.get());
    }

}