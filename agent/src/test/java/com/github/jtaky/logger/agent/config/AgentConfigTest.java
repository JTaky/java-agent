package com.github.jtaky.logger.agent.config;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStreamReader;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class AgentConfigTest {

    private AgentConfig agentConfig;

    @Before
    public void setUp() throws IOException {
        agentConfig = new AgentConfig(new InputStreamReader(getClass().getResourceAsStream("agent.config")));
    }

    @Test
    public void testGetInspectoredClassPaterns() throws Exception {
        String reason = "expected agent config parsed correctly";

        assertThat(reason, agentConfig.getInspectoredClassPaterns(), hasItem("com.github.jtaky.demo.*"));
        assertThat(reason, agentConfig.getInspectoredClassPaterns(), hasItem("ch.qos.logback..*"));
        assertThat(reason, agentConfig.getInspectoredClassPaterns(), hasItem("org.apache.log4j..*"));
        assertThat(reason, agentConfig.getInspectoredClassPaterns(), hasItem("org.apache.logging.log4j..*"));
    }
}