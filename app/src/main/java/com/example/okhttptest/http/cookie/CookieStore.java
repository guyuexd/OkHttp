package com.example.okhttptest.http.cookie;

import java.util.List;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

public interface CookieStore {

    /**
     * add cookie
     */
    void add(HttpUrl httpUrl, Cookie cookie);

    /**
     * add specific httpurl to cookie set
     */
    void add(HttpUrl httpUrl, List<Cookie> cookies);

    /**
     * get cookie list based on httpurl
     */
    List<Cookie> get(HttpUrl httpUrl);

    /**
     * get all cookies
     */
    List<Cookie> getCookies();

    /**
     * remove cookie based on httpurl
     */
    boolean remove(HttpUrl httpUrl, Cookie cookie);

    /**
     * remove all cookie
     */
    boolean removeAll();

}
