package com.chm.converter.test.fst;

import com.chm.converter.core.annotation.FieldProperty;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Date;
import java.util.Objects;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-06-03
 **/
public class User implements Serializable {

    /**
     * 用户
     */
    @FieldProperty(name = "user12", ordinal = 5)
    public User user;

    /**
     * 用户名
     */
    @FieldProperty(name = "userName1", ordinal = 4)
    public String userName;

    /**
     * 密码
     */
    @FieldProperty(name = "password2", ordinal = 3)
    public String password;

    /**
     * 新型时间
     */
    @FieldProperty(name = "localDateTime", ordinal = 2)
    public LocalDateTime localDateTime;

    /**
     * date
     */
    @FieldProperty(name = "date", ordinal = 1, format = "yyyy-MM-dd HH:mm:ss.SSSS")
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

        User user = (User) o;

        if (!Objects.equals(userName, user.userName)) return false;
        if (!Objects.equals(password, user.password)) return false;
        if (!Objects.equals(localDateTime, user.localDateTime)) return false;
        if (!Objects.equals(yearMonth, user.yearMonth)) return false;
        return Objects.equals(date, user.date);
    }

    @Override
    public int hashCode() {
        int result = user != null ? user.hashCode() : 0;
        result = 31 * result + (userName != null ? userName.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (localDateTime != null ? localDateTime.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (yearMonth != null ? yearMonth.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "user=" + user +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", localDateTime=" + localDateTime +
                ", date=" + date +
                ", yearMonth=" + yearMonth +
                '}';
    }
}
