package com.jtaky.logger.agent.config;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class AgentConfig {

    private static List<String> getStringList(Properties properties, String keyName) {
        return getStringList(properties, keyName, new ArrayList<String>());
    }
    private static List<String> getStringList(Properties properties, String keyName, List<String> defaultList) {
        String values = properties.getProperty(keyName);
        return values != null ? Arrays.asList(values.split(",")) : defaultList;
    }

    private static Reader createReader(String configPath) throws FileNotFoundException {
        if(configPath == null || "".equals(configPath)){
            return new InputStreamReader(AgentConfig.class.getResourceAsStream("agent.default.config"));
        }
        return new FileReader(configPath);
    }

    private List<String> inspectoredClassPatterns;

    public AgentConfig(String configPath) throws IOException {
        this(createReader(configPath));
    }

    public AgentConfig(Reader reader) throws IOException {
        Properties properties = new Properties(); properties.load(reader);
        //fail-early initialization of the properties
        inspectoredClassPatterns = getStringList(properties, "inspectored.class.patterns");
    }

    public List<String> getInspectoredClassPaterns(){
        return inspectoredClassPatterns;
    }

}
