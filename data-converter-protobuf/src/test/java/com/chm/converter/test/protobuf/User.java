package com.chm.converter.test.protobuf;

import com.chm.converter.core.annotation.FieldProperty;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Date;
import java.util.Objects;

/**
 * @author caihongming
 * @version v1.0
 * @date 2021-06-03
 **/
public class User {

    /**
     * 用户
     */
    @FieldProperty(name = "user12", ordinal = 1)
    public User user;

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
    @FieldProperty(name = "localDateTime", ordinal = 4, format = "yyyy-MM-dd HH:mm:ss.SSSS")
    public LocalDateTime localDateTime;

    /**
     * date
     */
    @FieldProperty(name = "date", ordinal = 5, format = "yyyy-MM-dd HH:mm:ss.SSSS")
    public Date date;

    @FieldProperty(name = "yearMonth", ordinal = 6, format = "yyyy-MM")
    public YearMonth yearMonth;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
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

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public YearMonth getYearMonth() {
        return yearMonth;
    }

    public void setYearMonth(YearMonth yearMonth) {
        this.yearMonth = yearMonth;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user1 = (User) o;
        return Objects.equals(user, user1.user) &&
                Objects.equals(userName, user1.userName) &&
                Objects.equals(password, user1.password) &&
                Objects.equals(localDateTime, user1.localDateTime) &&
                Objects.equals(date, user1.date) && Objects.equals(yearMonth, user1.yearMonth);
    }
}
