package com.chm.converter.test.protostuff;

import com.chm.converter.core.annotation.FieldProperty;

import java.util.Objects;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-06-03
 **/
public class TestUser {

    /**
     * 用户
     */
    @FieldProperty(name = "user12", ordinal = 1)
    public TestUser user;

    /**
     * 用户名
     */
    @FieldProperty(name = "userName1", ordinal = 2)
    public String userName;

    /**
     * 密码
     */
    @FieldProperty(name = "password2", ordinal = 3)
    public String password;

    /**
     * 新型时间
     */
    @FieldProperty(name = "localDateTime", ordinal = 4)
    public String localDateTime;

    /**
     * date
     */
    @FieldProperty(name = "date", ordinal = 5)
    public String date;

    @FieldProperty(name = "yearMonth", ordinal = 6)
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
        TestUser testUser = (TestUser) o;
        return Objects.equals(user, testUser.user) &&
                Objects.equals(userName, testUser.userName) &&
                Objects.equals(password, testUser.password) &&
                Objects.equals(localDateTime, testUser.localDateTime) &&
                Objects.equals(date, testUser.date) &&
                Objects.equals(yearMonth, testUser.yearMonth);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, userName, password, localDateTime, date, yearMonth);
    }
}
