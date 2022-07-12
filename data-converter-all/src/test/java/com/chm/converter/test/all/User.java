package com.chm.converter.test.all;

import com.chm.converter.core.annotation.FieldProperty;
import com.chm.converter.json.FastjsonConverter;
import com.chm.converter.json.GsonConverter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-06-03
 **/
public class User implements Serializable {

    /**
     * 用户
     */
    @FieldProperty(name = "user12", ordinal = 1)
    private User user;

    /**
     * 用户名
     */
    @FieldProperty(name = "gsonUserName", ordinal = 2, scope = GsonConverter.class)
    @FieldProperty(name = "fastjsonName", ordinal = 2, scope = FastjsonConverter.class)
    @FieldProperty(name = "userName", ordinal = 2)
    private String userName;

    /**
     * 密码
     */
    @FieldProperty(name = "password2", ordinal = 3)
    private String password;

    /**
     * 新型时间
     */
    @FieldProperty(name = "localDateTime", ordinal = 4, format = "yyyy-MM-dd HH:mm:ss.SSSS")
    private LocalDateTime localDateTime;

    /**
     * date
     */
    @FieldProperty(name = "date", ordinal = 5, format = "yyyy-MM-dd HH:mm:ss.SSSS")
    private Date date;

    @FieldProperty(name = "yearMonth", ordinal = 6, format = "yyyy-MM")
    private YearMonth yearMonth;

    @FieldProperty(name = "userList1", ordinal = 7)
    private List<User> userList;

    @FieldProperty(name = "userMap2", ordinal = 8)
    private Map<String, User> userMap;

    @FieldProperty(name = "testEnum", ordinal = 9)
    private Enum one;

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

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    public Map<String, User> getUserMap() {
        return userMap;
    }

    public void setUserMap(Map<String, User> userMap) {
        this.userMap = userMap;
    }

    public Enum getOne() {
        return one;
    }

    public void setOne(Enum one) {
        this.one = one;
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
                Objects.equals(date, user1.date) &&
                Objects.equals(yearMonth, user1.yearMonth) &&
                Objects.equals(userList, user1.userList) &&
                Objects.equals(userMap, user1.userMap) &&
                Objects.equals(one, user1.one);
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
                .add("userList=" + userList)
                .add("userMap=" + userMap)
                .add("one=" + one)
                .toString();
    }

    public enum Enum {
        @FieldProperty(name = "testOne")
        ONE, TWO
    }
}
