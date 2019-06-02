package com.demo.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

public class ContactsConfig {
    final Map<String, List<Number>> contactsMap;

    public ContactsConfig(final Map<String, List<Number>> contactsMap) {
        this.contactsMap = contactsMap;
    }

    public Map<String, List<Number>> getContacts() {
        return contactsMap;
    }

    public String toString() {
        try {
            return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(contactsMap);
        } catch (Exception e) {
            return "";
        }
    }
}
