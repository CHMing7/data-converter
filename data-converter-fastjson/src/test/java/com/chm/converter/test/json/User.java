package com.chm.converter.test.json;

import com.chm.converter.core.annotation.FieldProperty;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Date;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-06-03
 **/
public class User {

    /**
     * 用户
     */
    @FieldProperty(ordinal = 4)
    public User user;

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
    @FieldProperty(ordinal = 2,  format = "yyyy-MM-dd HH:mm:ss.SSSS")
    public LocalDateTime localDateTime;

    /**
     * date
     */
    @FieldProperty(ordinal = 1, format = "yyyy-MM-dd HH:mm:ss.SSSS")
    public Date date;

    @FieldProperty(ordinal = 6, format = "yyyy-MM")
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

        User other = (User) o;

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
