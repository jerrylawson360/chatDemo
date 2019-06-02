package com.demo.config;

import com.demo.server.RestServer;
import com.demo.server.impl.RestServerImpl;
import com.demo.service.ChatService;
import com.demo.service.UserService;
import com.demo.service.impl.ChatServiceMemoryImpl;
import com.demo.service.impl.UserServiceMemoryImpl;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Placeholder for singletons. This object itself is a singleton, accessed via the getInstance() static method.
 * TODO: use standard framework for singletons
 */
public class AppConfig {
    private static AppConfig _instance;
    private final ObjectMapper yamlMapper;
    private final ObjectMapper jsonMapper;
    private ChatService chatService;
    private UserService userService;

    private AppConfig() {
        jsonMapper = new ObjectMapper().configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        yamlMapper = new ObjectMapper(new YAMLFactory()).configure(JsonParser.Feature.ALLOW_COMMENTS, true);
    }

    public static AppConfig getInstance() {
        if (_instance == null) {
            _instance = new AppConfig();
        }
        return _instance;
    }

    // TODO: get from config file and/or command line args
    public String getRootPath() {
        return "/";
    }

    // TODO: get from config file and/or command line args
    public Integer getListenPort() {
        return 8080;
    }

    // TODO: get from config file and/or command line args
    public Integer getThreadPoolSize() {
        return 100;
    }

    // TODO: get executor type from config file and/or command line args
    public Executor getExecutor() {
        return Executors.newFixedThreadPool(getThreadPoolSize());
    }

    public RestServer getRestServer() {
        return new RestServerImpl(getRootPath())
            .setListenPort(getListenPort())
            .setExecutor(getExecutor());
    }

    public ObjectMapper getObjectMapper() {
        return jsonMapper;
    }

    public ObjectMapper getYamlMapper() {
        return yamlMapper;
    }

    public UserService getUserService() {
        if (userService == null) {
            userService = new UserServiceMemoryImpl(getContactsConfig());
        }
        return userService;
    }

    public ChatService getChatService() {
        if (chatService == null) {
            chatService = new ChatServiceMemoryImpl();
        }
        return chatService;
    }

    public ContactsConfig getContactsConfig() {
        try {
            // Read configuration from resource in jar
            // TODO: allow for reading from external (outside of jar) file.
            final File file = new File(getClass().getClassLoader().getResource("contacts.json").getFile());
            final Map<String, List<Number>> map = getYamlMapper().readValue(file, Map.class);
            return new ContactsConfig(map);
        } catch (Exception e) {
            System.out.println("Caught exception reading file:" + e.getLocalizedMessage());
            e.printStackTrace();
            return new ContactsConfig(Collections.emptyMap());
        }

    }
}
