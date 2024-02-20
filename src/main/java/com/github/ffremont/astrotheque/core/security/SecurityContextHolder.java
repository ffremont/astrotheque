package com.github.ffremont.astrotheque.core.security;

import com.github.ffremont.astrotheque.core.exception.UserNotFoundException;

import java.util.Optional;

public class SecurityContextHolder {


    private static ThreadLocal<User> myLocalThread = new ThreadLocal<>();

    private SecurityContextHolder() {
    }

    /**
     * @return
     */
    public static User getUser() {
        return Optional.ofNullable(myLocalThread.get()).orElseThrow(() -> new UserNotFoundException("Thread Local not contains user"));
    }

    public static void setUser(User user) {
        myLocalThread.set(user);
    }
}
