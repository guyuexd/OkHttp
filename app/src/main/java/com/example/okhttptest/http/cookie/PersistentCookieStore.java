package com.example.okhttptest.http.cookie;


import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import org.w3c.dom.Text;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

/**
 * Cookie缓存持久化实现类
 */
public class PersistentCookieStore implements CookieStore{

    private static final String LOG_TAG = "PersistentCookieStore";
    private static final String COOKIE_PREFS = "CookiePrefsFile";
    private static final String HOST_NAME_PREFIX = "host_";
    private static final String COOKIE_NAME_PREFIX = "cookie_";
    private final HashMap<String, ConcurrentHashMap<String, Cookie>> cookies;
    private final SharedPreferences cookiePrefs;
    private boolean omitNonPersistentCookies = false;

    public PersistentCookieStore(Context context) {
        this.cookiePrefs = context.getSharedPreferences(COOKIE_PREFS,Context.MODE_PRIVATE);
        this.cookies = new HashMap<String, ConcurrentHashMap<String, Cookie>>();

        Map tempCookieMap = new HashMap<Object, Object>(cookiePrefs.getAll());
        for(Object key : tempCookieMap.keySet()) {
            if(!(key instanceof String) || !((String) key).contains(HOST_NAME_PREFIX)) {
                continue;
            }

            String cookieNames = (String) tempCookieMap.get(key);
            if(TextUtils.isEmpty(cookieNames)) {
                continue;
            }

            if(!this.cookies.containsKey(key)) {
                this.cookies.put( (String) key, new ConcurrentHashMap<String, Cookie>());
            }

            String[] cookieNameArr = cookieNames.split(",");
            for(String name : cookieNameArr) {
                String encodedCookie = this.cookiePrefs.getString("cookie_"+name, null);
                if(encodedCookie == null){
                    continue;
                }

                Cookie decodedCookie = this.decodeCookie(encodedCookie);
                if(decodedCookie != null) {
                    this.cookies.get(key).put(name, decodedCookie);
                }
            }

        }
        tempCookieMap.clear();

        clearExpired();
    }


    /**
     * clear expired cookies
     */
    private void clearExpired() {
        SharedPreferences.Editor prefsWriter = cookiePrefs.edit();

        for(String key : this.cookies.keySet()) {
            boolean changeFlag = false;

            for(ConcurrentHashMap.Entry<String, Cookie> entry : cookies.get(key).entrySet()) {
                String name = entry.getKey();
                Cookie cookie = entry.getValue();
                if(isCookieExpired(cookie)) {
                    // clear cookie from local store
                    cookies.get(key).remove(name);

                    // clear cookie form persistent store
                    prefsWriter.remove(COOKIE_NAME_PREFIX + name);

                    //we've cleared at least one
                    changeFlag = true;
                }

            }

            // update names in persistent store
            if(changeFlag) {
                prefsWriter.putString(key, TextUtils.join(",", cookies.keySet()));
            }
        }

        prefsWriter.apply();
    }


    /**
     * add cookie
     *
     * @param httpUrl
     * @param cookie
     */
    @Override
    public void add(HttpUrl httpUrl, Cookie cookie) {
        if(omitNonPersistentCookies && !cookie.persistent()) {
            return;
        }

        String name = this.cookieName(cookie);
        String hostKey = this.hostName(httpUrl);

        //save cookie into local store or remove if expired
        if(!this.cookies.containsKey(hostKey)) {
            this.cookies.put(hostKey, new ConcurrentHashMap<String, Cookie>());
        }
        cookies.get(hostKey).put(name, cookie);

        //save cookie into persistent store
        SharedPreferences.Editor prefsWriter = cookiePrefs.edit();
        //save httpUrl conspondent cookie's name
        prefsWriter.putString(hostKey, TextUtils.join(",", cookies.get(hostKey).keySet()));
        //save cookie
        prefsWriter.apply();
    }

    /**
     * add specific httpurl to cookie set
     *
     * @param httpUrl
     * @param cookies
     */
    @Override
    public void add(HttpUrl httpUrl, List<Cookie> cookies) {
        for(Cookie cookie : cookies) {
            if(isCookieExpired(cookie)) {
                continue;
            }
            this.add(httpUrl, cookie);
        }
    }

    /**
     * get cookie list based on httpurl
     *
     * @param httpUrl
     */
    @Override
    public List<Cookie> get(HttpUrl httpUrl) {
        return this.get(this.hostName(httpUrl));
    }

    /**
     * get all cookies
     */
    @Override
    public List<Cookie> getCookies() {
        ArrayList<Cookie> result = new ArrayList<>();
        for(String hostKey: this.cookies.keySet()) {
            result.addAll(this.get(hostKey));
        }
        return result;
    }


    private List<Cookie> get(String hostKey) {
        ArrayList<Cookie> result = new ArrayList<>();

        if(this.cookies.containsKey(hostKey)) {
            Collection<Cookie> cookies = this.cookies.get(hostKey).values();
            for(Cookie cookie : cookies) {
                if(isCookieExpired(cookie)){
                    this.remove(hostKey, cookie);
                }
                else {
                    result.add(cookie);
                }
            }
        }
        return result;
    }

    /**
     * remove cookie based on httpurl
     *
     * @param httpUrl
     * @param cookie
     */
    @Override
    public boolean remove(HttpUrl httpUrl, Cookie cookie) {
       return this.remove(this.hostName(httpUrl), cookie);
    }


    /**
     * remove specific cookie form cache
     *
     * @param hostKey hostkey
     * @param cookie cookie
     * @return
     */
    private boolean remove(String hostKey, Cookie cookie) {
        String name = this.cookieName(cookie);
        if(this.cookies.containsKey(hostKey) && this.cookies.get(hostKey).contains(name)) {
            //remove httpUrl conspondent cookie from rom
            this.cookies.get(hostKey).remove(name);

            SharedPreferences.Editor prefsWriter = cookiePrefs.edit();
            // remove cookie from local cache
            prefsWriter.remove(COOKIE_NAME_PREFIX + name);

            //save httpUrl conspondent cookie name
            prefsWriter.putString(hostKey, TextUtils.join(",", this.cookies.get(hostKey).keySet()));

            prefsWriter.apply();
        }
        return false;
    }


    /**
     * remove all cookie
     */
    @Override
    public boolean removeAll() {
        SharedPreferences.Editor prefsWriter = cookiePrefs.edit();
        prefsWriter.clear();
        prefsWriter.apply();
        // clear cookies from local store
        this.cookies.clear();
        return true;
    }

    public void setOmitNonPersistentCookies(boolean omitNonPersistentCookies) {
        this.omitNonPersistentCookies = omitNonPersistentCookies;
    }

    /**
     * 判断cookie是否失效
     * @param cookie
     * @return
     */
    private boolean isCookieExpired(Cookie cookie) {
        return cookie.expiresAt() < System.currentTimeMillis();
    }

    private String hostName(HttpUrl httpUrl) {
        return httpUrl.host().startsWith(HOST_NAME_PREFIX) ? httpUrl.host() : HOST_NAME_PREFIX + httpUrl.host();
    }

    private String cookieName(Cookie cookie) {
        return cookie == null ? null : cookie.name() + cookie.domain();
    }

    /**
     * Serializes cookie object into string
     *
     * @param cookie cookie to be encoded, can be null
     * @return cookie encoded as String
     */
    protected String encodeCookie(SerializableCookie cookie) {
        if(cookie == null) {
            return null;
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try{
            ObjectOutputStream outputStream = new ObjectOutputStream(os);
            outputStream.writeObject(cookie);
        } catch (IOException e) {
            Log.d(LOG_TAG, "IOException in encodeCookie", e);
            return null;
        }
        return byteArrayToHexString(os.toByteArray());
    }

    /**
     * return cookie decoded from cookie string
     *
     * @param cookieString string of cookie as returned from http request
     * @return decoded cookie or null if exception occured
     */
    protected Cookie decodeCookie(String cookieString) {
        byte[] bytes = hexStringToByteArray(cookieString);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        Cookie cookie = null;
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            cookie = ((SerializableCookie) objectInputStream.readObject()).getCookie();
        } catch (IOException e) {
            Log.d(LOG_TAG, "IOException in decodeCookie", e);
        } catch (ClassNotFoundException e) {
            Log.d(LOG_TAG, "ClassNotFoundException in decodeCookie", e);
        }
        return cookie;
    }

    protected String byteArrayToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for(byte element : bytes) {
            int v = element & 0xff;
            if(v < 16) {
                sb.append('0');
            }
            sb.append((Integer.toHexString(v)));
        }
        return sb.toString().toUpperCase(Locale.US);
    }

    protected byte[] hexStringToByteArray(String hexString) {
        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for(int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4) + Character.digit(hexString.charAt(i + 1), 16));
        }
        return  data;
    }
}
