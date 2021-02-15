package com.alatauBot.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
public class PropertiesUtil {

    public static String getProperty(String key) {
        try (InputStream stream = PropertiesUtil.class.getResourceAsStream("/config.properties")) {
            Properties properties = new Properties();
            properties.load(stream);
            return properties.getProperty(key);
        } catch (IOException e) { log.info("Can't get property for: " + key, e); }
        return null;
    }
}
