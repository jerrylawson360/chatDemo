package com.demo.service.impl;

import com.demo.config.ContactsConfig;
import com.demo.service.UserRecord;
import com.demo.service.UserService;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UserServiceMemoryImpl implements UserService {
    final Map<Long, UserRecord> users;

    public UserServiceMemoryImpl(final ContactsConfig config) {
        users = config.getContacts().entrySet()
            .stream()
            .map(entry -> new UserRecordImpl(Long.parseLong(entry.getKey()), entry.getValue()))
            .collect(Collectors.toMap(UserRecord::getId, Function.identity()));
    }

    // this data is static for now. No need to worry about concurrent changes to this map in current design.
    @Override
    public UserRecord getUser(final Long userId) {
        return users.get(userId);
    }
}
