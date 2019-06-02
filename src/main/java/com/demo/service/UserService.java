package com.demo.service;

/**
 * Interface definition for a user service, to manage users.
 */
public interface UserService {
    UserRecord getUser(final Long userId);
}
