package com.demo.service.impl;

import com.demo.config.ContactsConfig;
import com.demo.service.UserRecord;
import com.demo.service.UserService;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class UserServiceImplTest {

    private ContactsConfig createContactsConfig() {
        final Map<String, List<Number>> contacts = new HashMap<>();
        contacts.put("12345", List.of(1, 2, 3, 4, 5));
        contacts.put("6789", List.of(6, 7, 8, 9));
        contacts.put("1234567890", List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));

        return new ContactsConfig(contacts);
    }

    @Test
    public void testConstructor() {
        final UserService unit = new UserServiceMemoryImpl(createContactsConfig());

        final UserRecord user1 = unit.getUser(12345L);
        assertNotNull(user1);
        assertEquals(5, user1.getContacts().size());
        assertEquals(Set.of(1L, 2L, 3L, 4L, 5L), user1.getContacts());

        final UserRecord user2 = unit.getUser(6789L);
        assertNotNull(user2);
        assertEquals(4, user2.getContacts().size());
        assertEquals(Set.of(6L, 7L, 8L, 9L), user2.getContacts());

        final UserRecord user3 = unit.getUser(1234567890L);
        assertNotNull(user3);
        assertEquals(10, user3.getContacts().size());
        assertEquals(Set.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L), user3.getContacts());
    }
}
