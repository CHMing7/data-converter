package com.chm.converter.test.xml;

import com.chm.converter.core.annotation.FieldProperty;
import com.chm.converter.xml.annotation.XmlRootElement;

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
@XmlRootElement(name = "testUser")
public class User2 {

    public User2() {
    }

    public User2(String userName) {
        this.userName = userName;
    }

    /**
     * 用户
     */
    @FieldProperty(name = "user", ordinal = 1)
    public User2 user;

    /**
     * 用户名
     */
    @FieldProperty(name = "userName", ordinal = 2)
    //@JacksonXmlText
    public String userName;

    /**
     * 密码
     */
    @FieldProperty(name = "password", ordinal = 3)
    public String password;

    /**
     * 新型时间
     */
    @FieldProperty(name = "localDateTime", ordinal = 4)
    public LocalDateTime localDateTime;

    /**
     * date
     */
    @FieldProperty(name = "date", ordinal = 5, format = "yyyy-MM-dd HH:mm:ss.SSSS")
    public Date date;

    @FieldProperty(name = "yearMonth", ordinal = 6, format = "yyyy-MM")
    public YearMonth yearMonth;

    public User2 getUser() {
        return user;
    }

    public void setUser(User2 user) {
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

        User2 other = (User2) o;

        if (!Objects.equals(this.user, other.user)) return false;
        if (!Objects.equals(this.userName, other.userName)) return false;
        if (!Objects.equals(this.password, other.password)) return false;
        if (!Objects.equals(this.localDateTime, other.localDateTime)) return false;
        if (!Objects.equals(this.date, other.date)) return false;
        return Objects.equals(this.yearMonth, other.yearMonth);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", User2.class.getSimpleName() + "[", "]")
                .add("user=" + user)
                .add("userName='" + userName + "'")
                .add("password='" + password + "'")
                .add("localDateTime=" + localDateTime)
                .add("date=" + date)
                .add("yearMonth=" + yearMonth)
                .toString();
    }

}
