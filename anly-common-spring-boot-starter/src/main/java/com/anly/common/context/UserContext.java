package com.anly.common.context;

import com.anly.common.dto.LoginUser;

/**
 * @DATE: 2023/7/10
 * @USER: anlythree
 */
public class UserContext {

    private static ThreadLocal<LoginUser> userHolder = new ThreadLocal<LoginUser>();

    public static void setUser(LoginUser loginUser) {
        userHolder.set(loginUser);
    }

    public static LoginUser getUser() {
        return userHolder.get();
    }
}
