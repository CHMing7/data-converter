package com.chm.converter.test.jsonb.fastjson2;

import com.chm.converter.core.annotation.FieldProperty;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * @author caihongming
 * @version v1.0
 * @since 2022-07-07
 **/
public class TestUser {

    /**
     * 用户
     */
    @FieldProperty(ordinal = 4)
    public TestUser user;

    /**
     * 用户名
     */
    @FieldProperty(ordinal = 5)
    public String userName;

    /**
     * 密码
     */
    @FieldProperty(ordinal = 3)
    public String password;

    /**
     * 新型时间
     */
    @FieldProperty(ordinal = 2)
    public String localDateTime;

    /**
     * date
     */
    @FieldProperty(ordinal = 1)
    public String date;

    @FieldProperty(ordinal = 6)
    public String yearMonth;

    public TestUser getUser() {
        return user;
    }

    public void setUser(TestUser user) {
        this.user = user;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(String localDateTime) {
        this.localDateTime = localDateTime;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getYearMonth() {
        return yearMonth;
    }

    public void setYearMonth(String yearMonth) {
        this.yearMonth = yearMonth;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestUser other = (TestUser) o;

        if (!Objects.equals(this.user, other.user)) return false;
        if (!Objects.equals(this.userName, other.userName)) return false;
        if (!Objects.equals(this.password, other.password)) return false;
        if (!Objects.equals(this.localDateTime, other.localDateTime)) return false;
        if (!Objects.equals(this.date, other.date)) return false;
        return Objects.equals(this.yearMonth, other.yearMonth);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", User.class.getSimpleName() + "[", "]")
                .add("user=" + user)
                .add("userName='" + userName + "'")
                .add("password='" + password + "'")
                .add("localDateTime=" + localDateTime)
                .add("date=" + date)
                .add("yearMonth=" + yearMonth)
                .toString();
    }
}
