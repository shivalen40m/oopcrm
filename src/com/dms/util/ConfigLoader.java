package com.dms.util;

import java.io.*;
import java.util.Properties;

public class ConfigLoader {
    private static Properties properties = new Properties();
    
    static {
        loadConfig();
    }
    
    private static void loadConfig() {
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            properties.load(fis);
            System.out.println("Configuration loaded successfully");
        } catch (IOException e) {
            System.err.println("Warning: config.properties not found, using defaults");
            loadDefaults();
        }
    }
    
    private static void loadDefaults() {
        properties.setProperty("db.url", "jdbc:postgresql://localhost:5432/dealership_db");
        properties.setProperty("db.user", "postgres");
        properties.setProperty("db.password", "postgres");
        properties.setProperty("app.name", "Dealership Management System");
        properties.setProperty("app.version", "1.0");
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }
    
    public static String get(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
}
