package com.example.okhttptest.entity;

import java.io.Serializable;

/**
 * 登录信息实体类
 * @quthor huhuanpu
 * @email guyuexd@126.com
 * @blog https://www.jianshu.com/u/137d5a6ed8f2
 */

public class LoginInfo implements Serializable {

    private String username;

    private String password;

    private String signcode;

    public LoginInfo(){

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSigncode() {
        return signcode;
    }

    public void setSigncode(String signcode) {
        this.signcode = signcode;
    }

    @Override
    public String toString() {
        return "username=" + getUsername() + "&password=" + getPassword() + "&signcode=" + getSigncode();
    }

}
