package com.chm.converter.utils.formatter;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Locale;
import java.util.Objects;

/**
 * from com.github.xkzhangsan:xk-time:3.1.0
 *
 * @author caihongming
 * @version v1.0
 * @since 2021-06-15
 **/
public final class DateTimeFormatterUtil {

    private DateTimeFormatterUtil() {
    }

    //===========================时区定义============================

    /**
     * 系统默认时区
     */
    private static final ZoneId ZONE = ZoneId.systemDefault();

    /**
     * 上海时区ID Asia/Shanghai
     */
    public static final String SHANGHAI_ZONE_ID = "Asia/Shanghai";

    /**
     * 上海时区  Asia/Shanghai
     */
    public static final ZoneId SHANGHAI_ZONE = ZoneId.of(SHANGHAI_ZONE_ID);

    // ==================================yyyy-MM-dd相关formatters==================================
    /**
     * yyyy-MM-dd 比如：  2020-05-23
     */
    public static final DateTimeFormatter YYYY_MM_DD_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.YYYY_MM_DD).withZone(ZONE);
    /**
     * yyyy-M-d 不补0 比如：  2020-5-23
     */
    public static final DateTimeFormatter YYYY_M_D_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.YYYY_M_D).withZone(ZONE);

    /**
     * yyyyMMdd  比如：  20200523
     */
    public static final DateTimeFormatter YYYYMMDD_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.YYYYMMDD).withZone(ZONE);

    /**
     * yyyy/MM/dd  比如：  2020/05/23
     */
    public static final DateTimeFormatter YYYY_MM_DD_EN_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.YYYY_MM_DD_EN).withZone(ZONE);

    /**
     * yyyy/M/d 不补0  比如：  2020/5/23
     */
    public static final DateTimeFormatter YYYY_M_D_EN_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.YYYY_M_D_EN).withZone(ZONE);

    /**
     * yyyy年MM月dd日  比如： 2020年05月23日
     */
    public static final DateTimeFormatter YYYY_MM_DD_CN_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.YYYY_MM_DD_CN).withZone(ZONE);

    /**
     * yyyy年M月d日  比如： 2020年5月23日
     */
    public static final DateTimeFormatter YYYY_M_D_CN_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.YYYY_M_D_CN).withZone(ZONE);

    /**
     * yyyy.MM.dd  比如：2020.05.23
     */
    public static final DateTimeFormatter YYYY_MM_DD_POINT_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.YYYY_MM_DD_POINT).withZone(ZONE);

    /**
     * yyyy.M.d 不补0  比如：2020.5.23
     */
    public static final DateTimeFormatter YYYY_M_D_POINT_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.YYYY_M_D_POINT).withZone(ZONE);

    /**
     * yy/MM/dd 不补0  比如：20/05/23
     */
    public static final DateTimeFormatter YY_MM_DD_EN_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.YY_MM_DD_EN).withZone(ZONE);

    /**
     * yy/M/d  比如：20/5/23
     */
    public static final DateTimeFormatter YY_M_D_EN_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.YY_M_D_EN).withZone(ZONE);

    /**
     * MM/dd/yy 不补0  比如：05/23/20
     */
    public static final DateTimeFormatter MM_DD_YY_EN_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.MM_DD_YY_EN).withZone(ZONE);

    /**
     * M/d/yy  比如：5/23/20
     */
    public static final DateTimeFormatter M_D_YY_EN_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.M_D_YY_EN).withZone(ZONE);

    /**
     * yyyy-MM-dd E 不补0  比如：2020-05-23 星期六
     */
    public static final DateTimeFormatter YYYY_MM_DD_E_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.YYYY_MM_DD_E).withZone(ZONE);

    /**
     * yy 年的后2位  比如： 20
     */
    public static final DateTimeFormatter YY_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.YY).withZone(ZONE);

    /**
     * yyyy  比如：2020
     */
    public static final DateTimeFormatter YYYY_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.YYYY).withZone(ZONE);

    /**
     * yyyy-MM  比如：2020-05
     */
    public static final DateTimeFormatter YYYY_MM_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.YYYY_MM).withZone(ZONE);

    /**
     * yyyyMM  比如：202005
     */
    public static final DateTimeFormatter YYYYMM_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.YYYYMM).withZone(ZONE);

    /**
     * yyyy/MM  比如：2020/05
     */
    public static final DateTimeFormatter YYYY_MM_EN_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.YYYY_MM_EN).withZone(ZONE);

    /**
     * yyyy年MM月  比如：2020年05月
     */
    public static final DateTimeFormatter YYYY_MM_CN_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.YYYY_MM_CN).withZone(ZONE);

    /**
     * yyyy年M月  比如：2020年5月
     */
    public static final DateTimeFormatter YYYY_M_CN_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.YYYY_M_CN).withZone(ZONE);

    /**
     * MM-dd  比如：05-23
     */
    public static final DateTimeFormatter MM_DD_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.MM_DD).withZone(ZONE);

    /**
     * MMdd  比如：0523
     */
    public static final DateTimeFormatter MMDD_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.MMDD).withZone(ZONE);

    /**
     * MM/dd  比如：05/23
     */
    public static final DateTimeFormatter MM_DD_EN_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.MM_DD_EN).withZone(ZONE);

    /**
     * M/d  比如：5/23
     */
    public static final DateTimeFormatter M_D_EN_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.M_D_EN).withZone(ZONE);

    /**
     * MM月dd日  比如：05月23日
     */
    public static final DateTimeFormatter MM_DD_CN_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.MM_DD_CN).withZone(ZONE);

    /**
     * M月d日 不补0  比如：5月23日
     */
    public static final DateTimeFormatter M_D_CN_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.M_D_CN).withZone(ZONE);


    // ==================================HH:mm:ss 相关formatters==================================

    /**
     * HH:mm:ss  比如：17:26:30
     */
    public static final DateTimeFormatter HH_MM_SS_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.HH_MM_SS).withZone(ZONE);

    /**
     * H:m:s  比如：17:6:30
     */
    public static DateTimeFormatter H_M_S_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.H_M_S).withZone(ZONE);

    /**
     * HHmmss  比如：170630
     */
    public static final DateTimeFormatter HHMMSS_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.HHMMSS).withZone(ZONE);

    /**
     * HH时mm分ss秒  比如：17时06分30秒
     */
    public static DateTimeFormatter HH_MM_SS_CN_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.HH_MM_SS_CN).withZone(ZONE);

    /**
     * HH:mm  比如：17:06
     */
    public static DateTimeFormatter HH_MM_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.HH_MM).withZone(ZONE);

    /**
     * H:m  比如：17:6
     */
    public static DateTimeFormatter H_M_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.H_M).withZone(ZONE);

    /**
     * HH时mm分 比如：17时06分
     */
    public static DateTimeFormatter HH_MM_CN_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.HH_MM_CN).withZone(ZONE);

    /**
     * hh:mm a 比如：05:06 下午
     */
    public static DateTimeFormatter HH_MM_A_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.HH_MM_A).withZone(ZONE);

    /**
     * hh:mm a 比如：05:06 PM  AM PM
     */
    public static DateTimeFormatter HH_MM_A_AM_PM_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.HH_MM_A, Locale.ENGLISH).withZone(ZONE);


    // ==================================HH:mm:ss.SSS 相关formatters==================================

    /**
     * HH:mm:ss.SSS  比如：17:26:30.272
     */
    public static DateTimeFormatter HH_MM_SS_SSS_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.HH_MM_SS_SSS).withZone(ZONE);

    // ==================================HH:mm:ss.SSSSSS 相关formatters==================================

    /**
     * HH:mm:ss.SSSSSS  比如：17:26:30.272150
     */
    public static DateTimeFormatter HH_MM_SS_SSSSSS_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.HH_MM_SS_SSSSSS).withZone(ZONE);

    // ==================================HH:mm:ss.SSSSSSSSS 相关formatters==================================

    /**
     * HH:mm:ss.SSSSSSSSS  比如：17:26:30.272150620
     */
    public static DateTimeFormatter HH_MM_SS_SSSSSSSSS_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.HH_MM_SS_SSSSSSSSS).withZone(ZONE);


    // ==================================yyyy-MM-dd HH:mm:ss 相关formatters==================================

    /**
     * yyyy-MM-dd HH:mm:ss 比如：2020-05-23 17:06:30
     */
    public static final DateTimeFormatter YYYY_MM_DD_HH_MM_SS_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.YYYY_MM_DD_HH_MM_SS).withZone(ZONE);

    /**
     * yyyy-M-d H:m:s 比如：2020-5-23 17:6:30
     */
    public static final DateTimeFormatter YYYY_M_D_H_M_S_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.YYYY_M_D_H_M_S).withZone(ZONE);

    /**
     * yyyyMMddHHmmss 比如：20200523170630
     */
    public static final DateTimeFormatter YYYYMMDDHHMMSS_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.YYYYMMDDHHMMSS).withZone(ZONE);

    /**
     * yyyy/MM/dd HH:mm:ss 比如：2020/05/23 17:06:30
     */
    public static final DateTimeFormatter YYYY_MM_DD_HH_MM_SS_EN_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.YYYY_MM_DD_HH_MM_SS_EN).withZone(ZONE);

    /**
     * yyyy/M/d H:m:s 比如：2020/5/23 17:6:30
     */
    public static final DateTimeFormatter YYYY_M_D_H_M_S_EN_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.YYYY_M_D_H_M_S_EN).withZone(ZONE);

    /**
     * yyyy年MM月dd日 HH:mm:ss 比如：2020年05月23日 17:06:30
     */
    public static final DateTimeFormatter YYYY_MM_DD_HH_MM_SS_CN_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.YYYY_MM_DD_HH_MM_SS_CN).withZone(ZONE);

    /**
     * yyyy年MM月dd日 HH时mm分ss秒 比如：2020年05月23日 17时06分30秒
     */
    public static final DateTimeFormatter YYYY_MM_DD_HH_MM_SS_CN_ALL_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.YYYY_MM_DD_HH_MM_SS_CN_ALL).withZone(ZONE);

    /**
     * yyyy-MM-dd HH:mm 比如：2020-05-23 17:06
     */
    public static final DateTimeFormatter YYYY_MM_DD_HH_MM_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.YYYY_MM_DD_HH_MM).withZone(ZONE);

    /**
     * yyyy-M-d H:m 比如：2020-5-23 17:6
     */
    public static final DateTimeFormatter YYYY_M_D_H_M_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.YYYY_M_D_H_M).withZone(ZONE);

    /**
     * yyyyMMddHHmm 比如：202005231706
     */
    public static final DateTimeFormatter YYYYMMDDHHMM_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.YYYYMMDDHHMM).withZone(ZONE);

    /**
     * yyyy/MM/dd HH:mm 比如：2020/05/23 17:06
     */
    public static final DateTimeFormatter YYYY_MM_DD_HH_MM_EN_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.YYYY_MM_DD_HH_MM_EN).withZone(ZONE);

    /**
     * yyyy/M/d H:m 比如：2020/5/23 17:6
     */
    public static final DateTimeFormatter YYYY_M_D_H_M_EN_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.YYYY_M_D_H_M_EN).withZone(ZONE);

    /**
     * yyyy/M/d h:m a 比如：2020/5/23 5:6 下午 跟随系统
     */
    public static final DateTimeFormatter YYYY_M_D_H_M_A_EN_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.YYYY_M_D_H_M_A_EN).withZone(ZONE);

    /**
     * yyyy/M/d h:m a 比如：2020/5/23 5:6 PM  AM，PM
     */
    public static final DateTimeFormatter YYYY_M_D_H_M_A_AM_PM_EN_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.YYYY_M_D_H_M_A_EN, Locale.ENGLISH).withZone(ZONE);
    /**
     * MM-dd HH:mm 比如：05-23 17:06
     */
    public static final DateTimeFormatter MM_DD_HH_MM_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.MM_DD_HH_MM).withZone(ZONE);

    /**
     * MM月dd日 HH:mm 比如：05月23日 17:06
     */
    public static final DateTimeFormatter MM_DD_HH_MM_CN_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.MM_DD_HH_MM_CN).withZone(ZONE);

    /**
     * MM-dd HH:mm:ss 比如：05-23 17:06:30
     */
    public static final DateTimeFormatter MM_DD_HH_MM_SS_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.MM_DD_HH_MM_SS).withZone(ZONE);

    /**
     * MM月dd日 HH:mm:ss 比如：05月23日 17:06:30
     */
    public static final DateTimeFormatter MM_DD_HH_MM_SS_CN_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.MM_DD_HH_MM_SS_CN).withZone(ZONE);

    /**
     * yyyy年MM月dd日 hh:mm:ss a 比如：2020年05月23日 05:06:30 下午
     */
    public static final DateTimeFormatter YYYY_MM_DD_HH_MM_SS_A_CN_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.YYYY_MM_DD_HH_MM_SS_A_CN).withZone(ZONE);

    /**
     * yyyy年MM月dd日 hh:mm:ss a 比如：2020年05月23日 05:06:30 PM
     */
    public static final DateTimeFormatter YYYY_MM_DD_HH_MM_SS_A_AM_PM_CN_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.YYYY_MM_DD_HH_MM_SS_A_CN, Locale.ENGLISH).withZone(ZONE);

    /**
     * yyyy年MM月dd日 hh时mm分ss秒 a 比如：2020年05月23日 17时06分30秒 下午
     */
    public static final DateTimeFormatter YYYY_MM_DD_HH_MM_SS_A_CN_ALL_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.YYYY_MM_DD_HH_MM_SS_A_CN_ALL).withZone(ZONE);

    /**
     * yyyy年MM月dd日 hh时mm分ss秒 a 比如：2020年05月23日 17时06分30秒 PM
     */
    public static final DateTimeFormatter YYYY_MM_DD_HH_MM_SS_A_AM_PM_CN_ALL_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.YYYY_MM_DD_HH_MM_SS_A_CN_ALL, Locale.ENGLISH).withZone(ZONE);


    // ==================================yyyy-MM-dd HH:mm:ss.SSS 相关formatters==================================

    /**
     * yyyy-MM-dd HH:mm:ss.SSS 比如：2020-05-23 17:06:30.272
     */
    public static final DateTimeFormatter YYYY_MM_DD_HH_MM_SS_SSS_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.YYYY_MM_DD_HH_MM_SS_SSS).withZone(ZONE);

    /**
     * yyyy-MM-dd HH:mm:ss,SSS 比如：2020-05-23 17:06:30,272
     */
    public static final DateTimeFormatter YYYY_MM_DD_HH_MM_SS_SSS_COMMA_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.YYYY_MM_DD_HH_MM_SS_SSS_COMMA).withZone(ZONE);

    /**
     * yyyyMMddHHmmssSSS 比如：20200523170630272 <br>
     * Jdk8 解析 yyyyMMddHHmmssSSS bug，推荐解决用法 :https://bugs.java.com/bugdatabase/view_bug.do?bug_id=JDK-8031085
     */
    public static final DateTimeFormatter YYYYMMDDHHMMSSSSS_FMT = new DateTimeFormatterBuilder().appendPattern(DateFormatPattern.YYYYMMDDHHMMSS).appendValue(ChronoField.MILLI_OF_SECOND, 3).toFormatter().withZone(ZONE);

    /**
     * yyyy-M-d H:m:s.SSS 比如：2020-5-23 17:6:30.272
     */
    public static final DateTimeFormatter YYYY_M_D_H_M_S_SSS_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.YYYY_M_D_H_M_S_SSS).withZone(ZONE);

    /**
     * yyyy/M/d H:m:s.SSS 比如：2020/5/23 17:6:30.272
     */
    public static final DateTimeFormatter YYYY_M_D_H_M_S_SSS_EN_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.YYYY_M_D_H_M_S_SSS_EN).withZone(ZONE);

    /**
     * yyyy-M-d H:m:s,SSS 比如：2020-5-23 17:6:30,272
     */
    public static final DateTimeFormatter YYYY_M_D_H_M_S_SSS_COMMA_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.YYYY_M_D_H_M_S_SSS_COMMA).withZone(ZONE);


    // ==================================yyyy-MM-dd HH:mm:ss.SSSSSS 相关formatters==================================
    /**
     * yyyy-MM-dd HH:mm:ss.SSSSSS 比如：2020-05-23 17:06:30.272150
     */
    public static final DateTimeFormatter YYYY_MM_DD_HH_MM_SS_SSSSSS_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.YYYY_MM_DD_HH_MM_SS_SSSSSS).withZone(ZONE);


    // ==================================yyyy-MM-dd HH:mm:ss.SSSSSSSSS 相关formatters==================================
    /**
     * yyyy-MM-dd HH:mm:ss.SSSSSSSSS 比如：2020-05-23 17:06:30.272150620
     */
    public static final DateTimeFormatter YYYY_MM_DD_HH_MM_SS_SSSSSSSSS_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.YYYY_MM_DD_HH_MM_SS_SSSSSSSSS).withZone(ZONE);


    // ==================================Iso相关formatters 包含 T （自定义）==================================

    /**
     * yyyy-MM-dd'T'HH:mm:ssZ 比如：2020-05-23T17:06:30+0800
     */
    public static final DateTimeFormatter YYYY_MM_DD_T_HH_MM_SS_Z_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.YYYY_MM_DD_T_HH_MM_SS_Z);

    /**
     * yyyy-MM-dd'T'HH:mm:ssxxx 比如：2020-05-23T17:06:30+08:00 0时区时末尾 为+00:00
     */
    public static final DateTimeFormatter YYYY_MM_DD_T_HH_MM_SS_XXX_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.YYYY_MM_DD_T_HH_MM_SS_XXX);

    /**
     * yyyy-MM-dd'T'HH:mm:ssXXX 比如：2020-05-23T17:06:30+08:00 0时区时末尾 为Z
     */
    public static final DateTimeFormatter YYYY_MM_DD_T_HH_MM_SS_XXX_Z_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.YYYY_MM_DD_T_HH_MM_SS_XXX_Z);

    /**
     * yyyy-MM-dd'T'HH:mm:ss.SSSZ 比如：2020-05-23T17:06:30.272+0800
     */
    public static final DateTimeFormatter YYYY_MM_DD_T_HH_MM_SS_SSS_Z_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.YYYY_MM_DD_T_HH_MM_SS_SSS_Z);

    /**
     * yyyy-MM-dd'T'HH:mm:ss.SSSxxx 比如：2020-05-23T17:06:30.272+08:00
     */
    public static final DateTimeFormatter YYYY_MM_DD_T_HH_MM_SS_SSS_XXX_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.YYYY_MM_DD_T_HH_MM_SS_SSS_XXX);

    /**
     * yyyy-MM-dd'T'HH:mm:ss.SSSXXX 比如：2020-05-23T17:06:30.272+08:00 0时区时末尾 为Z
     */
    public static final DateTimeFormatter YYYY_MM_DD_T_HH_MM_SS_SSS_XXX_Z_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.YYYY_MM_DD_T_HH_MM_SS_SSS_XXX_Z);

    /**
     * yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ 比如：2020-05-23T17:06:30.272150+0800 2020-05-23T09:06:30.272150+0000
     */
    public static final DateTimeFormatter YYYY_MM_DD_T_HH_MM_SS_SSSSSS_Z_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.YYYY_MM_DD_T_HH_MM_SS_SSSSSS_Z);

    /**
     * yyyy-MM-dd'T'HH:mm:ss.SSSSSSxxx 比如：2020-05-23T17:06:30.272150+08:00 2020-05-23T09:06:30.272150+00:00
     */
    public static final DateTimeFormatter YYYY_MM_DD_T_HH_MM_SS_SSSSSS_XXX_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.YYYY_MM_DD_T_HH_MM_SS_SSSSSS_XXX);

    /**
     * yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX 比如：2020-05-23T17:06:30.272150+08:00 2020-05-23T09:06:30.272150Z 0时区时末尾 为Z
     */
    public static final DateTimeFormatter YYYY_MM_DD_T_HH_MM_SS_SSSSSS_XXX_Z_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.YYYY_MM_DD_T_HH_MM_SS_SSSSSS_XXX_Z);

    /**
     * yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSSZ 比如：2020-05-23T17:06:30.272150620+0800 2020-05-23T09:06:30.272150620+0000
     */
    public static final DateTimeFormatter YYYY_MM_DD_T_HH_MM_SS_SSSSSSSSS_Z_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.YYYY_MM_DD_T_HH_MM_SS_SSSSSSSSS_Z);

    /**
     * yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSSxxx 比如：2020-05-23T17:06:30.272150620+08:00 2020-05-23T09:06:30.272150620+00:00
     */
    public static final DateTimeFormatter YYYY_MM_DD_T_HH_MM_SS_SSSSSSSSS_XXX_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.YYYY_MM_DD_T_HH_MM_SS_SSSSSSSSS_XXX);

    /**
     * yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSSXXX 比如：2020-05-23T17:06:30.272150620+08:00 2020-05-23T09:06:30.272150620Z 0时区时末尾 为Z
     */
    public static final DateTimeFormatter YYYY_MM_DD_T_HH_MM_SS_SSSSSSSSS_XXX_Z_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.YYYY_MM_DD_T_HH_MM_SS_SSSSSSSSS_XXX_Z);
    // ==================================Iso相关formatters 包含 T （Jdk）==================================

    /**
     * such as '2011-12-03' or '2011-12-03+01:00'.
     */
    public static final DateTimeFormatter ISO_DATE_FMT = DateTimeFormatter.ISO_DATE;

    /**
     * such as '2011-12-03T10:15:30','2011-12-03T10:15:30+01:00' or '2011-12-03T10:15:30+01:00[Europe/Paris]'.
     */
    public static final DateTimeFormatter ISO_DATE_TIME_FMT = DateTimeFormatter.ISO_DATE_TIME;

    /**
     * such as '2011-12-03T10:15:30Z'.
     */
    public static final DateTimeFormatter ISO_INSTANT_FMT = DateTimeFormatter.ISO_INSTANT;

    /**
     * such as '2011-12-03'.
     */
    public static final DateTimeFormatter ISO_LOCAL_DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;

    /**
     * such as '2011-12-03T10:15:30'.
     */
    public static final DateTimeFormatter ISO_LOCAL_DATE_TIME_FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    /**
     * such as '10:15' or '10:15:30'.
     */
    public static final DateTimeFormatter ISO_LOCAL_TIME_FMT = DateTimeFormatter.ISO_LOCAL_TIME;


    /**
     * such as '10:15', '10:15:30' or '10:15:30+01:00'.
     */
    public static final DateTimeFormatter ISO_TIME_FMT = DateTimeFormatter.ISO_TIME;

    /**
     * such as '2012-W48-6'.
     */
    public static final DateTimeFormatter ISO_WEEK_DATE_FMT = DateTimeFormatter.ISO_WEEK_DATE;

    /**
     * such as '2011-12-03T10:15:30+01:00[Europe/Paris]'.
     */
    public static final DateTimeFormatter ISO_ZONED_DATE_TIME_FMT = DateTimeFormatter.ISO_ZONED_DATE_TIME;

    /**
     * such as '20111203'.
     */
    public static final DateTimeFormatter BASIC_ISO_DATE_FMT = DateTimeFormatter.BASIC_ISO_DATE;


    // ==================================其他格式 formatters==================================

    /**
     * Date 默认格式 EEE MMM dd HH:mm:ss zzz yyyy 比如：  Sat May 23 17:06:30 CST 2020
     */
    public static final DateTimeFormatter EEE_MMM_DD_HH_MM_SS_ZZZ_YYYY_FMT = DateTimeFormatter.ofPattern(DateFormatPattern.EEE_MMM_DD_HH_MM_SS_ZZZ_YYYY, Locale.ENGLISH);


    // ==================================format==================================


    /**
     * 根据 formatter解析为 LocalDateTime
     *
     * @param text      待解析字符串
     * @param formatter DateTimeFormatter
     * @return LocalDateTime
     */
    public static LocalDateTime parseToLocalDateTime(String text, DateTimeFormatter formatter) {
        Objects.requireNonNull(formatter, "formatter");
        LocalDateTime localDateTime;
        try {
            localDateTime = DateTimeConverterUtil.toLocalDateTime(formatter.parse(text));
        } catch (DateTimeException e) {
            if (e.getMessage().startsWith(Constant.PARSE_LOCAL_DATE_EXCEPTION)) {
                localDateTime = DateTimeConverterUtil.toLocalDateTime(LocalDate.parse(text, formatter));
            } else {
                throw e;
            }
        }
        return localDateTime;
    }

    /**
     * 根据 formatter解析为 ZonedDateTime
     *
     * @param text      待解析字符串
     * @param formatter DateTimeFormatter
     * @return ZonedDateTime
     */
    public static ZonedDateTime parseToZonedDateTime(String text, DateTimeFormatter formatter) {
        return ZonedDateTime.parse(text, formatter);
    }

    /**
     * 解析Iso格式 包含 T 格式
     * <pre>
     * =====================Iso相关格式=====================
     * yyyy-MM-dd'T'HH:mm:ssZ			2020-05-23T17:06:30+0800
     * yyyy-MM-dd'T'HH:mm:ss'Z'		2020-05-23T17:06:30Z
     * yyyy-MM-dd'T'HH:mm:ssxxx		2020-05-23T17:06:30+08:00
     * yyyy-MM-dd'T'HH:mm:ss.SSSZ		2020-05-23T17:06:30.272+0800
     * yyyy-MM-dd'T'HH:mm:ss.SSSxxx		2020-05-23T17:06:30.272+08:00
     * </pre>
     *
     * @param text 待解析字符串
     * @return LocalDateTime
     */
    public static LocalDateTime parseIsoToLocalDateTime(String text) {
        if (StringUtil.isEmpty(text)) {
            throw new DateTimeException("text is null");
        }

        if (!text.contains("T")) {
            throw new DateTimeException("text is not supported! " + text);
        }

        text = text.trim();
        int len = text.length();
        if (!text.contains("[")) {
            if (len == 24) {
                // yyyy-MM-dd'T'HH:mm:ssZ
                return parseToLocalDateTime(text, YYYY_MM_DD_T_HH_MM_SS_Z_FMT);
            } else if (len == 28) {
                // yyyy-MM-dd'T'HH:mm:ss.SSSZ
                return parseToLocalDateTime(text, YYYY_MM_DD_T_HH_MM_SS_SSS_Z_FMT);
            } else {
                return parseToLocalDateTime(text, ISO_DATE_TIME_FMT);
            }
        } else {
            // 包含时区Id时 先转换为ZonedDateTime 再转换为LocalDateTime
            if (len == 24) {
                // yyyy-MM-dd'T'HH:mm:ssZ
                return DateTimeConverterUtil
                        .toLocalDateTime(parseToZonedDateTime(text, YYYY_MM_DD_T_HH_MM_SS_Z_FMT));
            } else if (len == 28) {
                // yyyy-MM-dd'T'HH:mm:ss.SSSZ
                return DateTimeConverterUtil
                        .toLocalDateTime(parseToZonedDateTime(text, YYYY_MM_DD_T_HH_MM_SS_SSS_Z_FMT));
            } else {
                return DateTimeConverterUtil.toLocalDateTime(parseToZonedDateTime(text, ISO_DATE_TIME_FMT));
            }
        }
    }


    /**
     * 解析Timestamp格式字符串为LocalDateTime  默认格式 yyyy-mm-dd hh:mm:ss.fffffffff 其中 fffffffff 纳秒，省略后面的0 比如：
     * <pre>
     * 2020-05-23 17:06:30.0
     * 2020-05-23 17:06:30.272
     * 2020-05-23 17:06:30.27215
     * 2020-05-23 17:06:30.27215062
     * </pre>
     *
     * @param text 待解析字符串
     * @return LocalDateTime
     */
    public static LocalDateTime parseTimestampStyleToLocalDateTime(String text) {
        //预处理
        Objects.requireNonNull(text, "text");
        text = text.trim();
        if (!text.contains(".")) {
            throw new DateTimeException("text is not supported! " + text);
        }

        //.分割成2部分，分别分析
        String[] textArr = text.split("\\.");
        String main = textArr[0];
        String nanoOfSecond = textArr[1];
        int mainLen = main.length();
        int len = nanoOfSecond.length();
        if (mainLen != DateFormatPattern.YYYY_MM_DD_HH_MM_SS.length()) {
            throw new DateTimeException("text is not supported! " + text);
        }
        if (len > 9) {
            throw new DateTimeException("text is not supported! " + text);
        }

        //纳秒部分补0
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 9 - len; i++) {
            sb.append("0");
        }
        nanoOfSecond = nanoOfSecond + sb.toString();
        text = main + "." + nanoOfSecond;

        //使用yyyy-MM-dd HH:mm:ss.SSSSSSSSS 标准格式解析
        return parseToLocalDateTime(text, DateTimeFormatterUtil.YYYY_MM_DD_HH_MM_SS_SSSSSSSSS_FMT);
    }

    /**
     * 自动解析为 LocalDateTime
     * <pre>
     * =====================yyyy-MM-dd 相关=====================
     * yyyy-MM-dd	2020-05-23 或 2020-5-23
     * yyyyMMdd		20200523
     * yyyy/MM/dd	2020/05/23 或 2020/5/23
     * yyyy年MM月dd日	2020年05月23日 或 2020年5月23日
     * yyyy.MM.dd	2020.05.23 或 2020.5.23
     *
     *
     * =====================yyyy-MM-dd HH:mm:ss 相关=====================
     * yyyy-MM-dd HH:mm:ss		2020-05-23 17:06:30
     * yyyyMMddHHmmss			20200523170630
     * yyyy年MM月dd日 HH:mm:ss		2020年05月23日 17:06:30
     * yyyy年MM月dd日 HH时mm分ss秒	2020年05月23日 17时06分30秒
     * yyyy-MM-dd HH:mm		2020-05-23 17:06
     * yyyy/MM/dd HH:mm		2020/05/23 17:06
     *
     *
     * =====================yyyy-MM-dd HH:mm:ss.SSS 相关=====================
     * yyyy-MM-dd HH:mm:ss.SSS		2020-05-23 17:06:30.272
     * yyyy-MM-dd HH:mm:ss,SSS		2020-05-23 17:06:30,272
     * yyyyMMddHHmmssSSS			20200523170630272
     *
     * =====================Iso相关格式=====================
     * yyyy-MM-dd'T'HH:mm:ssZ			2020-05-23T17:06:30+0800
     * yyyy-MM-dd'T'HH:mm:ss'Z'		2020-05-23T17:06:30Z
     * yyyy-MM-dd'T'HH:mm:ssxxx		2020-05-23T17:06:30+08:00
     * yyyy-MM-dd'T'HH:mm:ss.SSSZ		2020-05-23T17:06:30.272+0800
     * yyyy-MM-dd'T'HH:mm:ss.SSSxxx		2020-05-23T17:06:30.272+08:00
     *
     * =====================其他格式=====================
     *  EEE MMM dd HH:mm:ss zzz yyyy	 	Sat May 23 17:06:30 CST 2020
     * </pre>
     *
     * @param text 待解析字符串
     * @return LocalDateTime
     */
    public static LocalDateTime smartParseToLocalDateTime(String text) {
        // 1.字符串预检查处理
        if (StringUtil.isEmpty(text)) {
            throw new DateTimeException("text is null");
        }

        text = text.trim();
        int len = text.length();
        if (len < 8) {
            throw new DateTimeException("text is not supported! " + text);
        }

        //预处理待解析字符串
        text = preprocessText(text);

        // 2.解析字符串
        // 2.1 Date 默认格式 EEE MMM dd HH:mm:ss zzz yyyy 如：Thu May 21 22:58:05 CST
        if (StringUtil.isStartWithWord(text)) {
            return parseToLocalDateTime(text, EEE_MMM_DD_HH_MM_SS_ZZZ_YYYY_FMT);
        } else if (StringUtil.isNumeric(text)) {
            // 2.2 纯数字格式解析
            if (len == 14) {
                return parseToLocalDateTime(text, YYYYMMDDHHMMSS_FMT);
            } else if (len == 17) {
                return parseToLocalDateTime(text, YYYYMMDDHHMMSSSSS_FMT);
            } else if (len == 8) {
                return parseToLocalDateTime(text, YYYYMMDD_FMT);
            } else if (len == 12) {
                return parseToLocalDateTime(text, YYYYMMDDHHMM_FMT);
            }
        } else {
            // : 出现次数
            int colonCount = StringUtil.countWord(text, ":");
            // 2.3 yyyy-MM-dd 格式解析
            if (colonCount == 0) {
                return parseToLocalDateTime(text, YYYY_M_D_FMT);
            } else if (text.contains("T")) {
                // 2.4 ISO格式解析
                return parseIsoToLocalDateTime(text);
            } else if (colonCount > 0 && text.contains(".")) {
                if (text.split("\\.")[1].length() == 3) {
                    // 2.5 yyyy-MM-dd HH:mm:ss.SSS
                    return parseToLocalDateTime(text, YYYY_M_D_H_M_S_SSS_FMT);
                } else {
                    return parseTimestampStyleToLocalDateTime(text);
                }
            } else if (colonCount > 0 && text.contains(",")) {
                // 2.6 yyyy-MM-dd HH:mm:ss,SSS
                return parseToLocalDateTime(text, YYYY_M_D_H_M_S_SSS_COMMA_FMT);
            } else if (colonCount > 0) {
                if (colonCount == 2) {
                    // 2.7 yyyy-MM-dd HH:mm:ss
                    return parseToLocalDateTime(text, YYYY_M_D_H_M_S_FMT);
                }
                if (colonCount == 1) {
                    // 2.8 yyyy-MM-dd HH:mm
                    return parseToLocalDateTime(text, YYYY_M_D_H_M_FMT);
                }
            }
        }
        throw new DateTimeException("text is not supported! " + text);
    }

    // ==================================private method==================================

    /**
     * 预处理待解析字符串
     *
     * @param text 待解析字符串
     * @return
     */
    private static String preprocessText(String text) {
        text = convertSlashToNormal(text);
        text = convertPointToNormal(text);
        text = convertCnToNormal(text);
        return text;
    }


    /**
     * "/" 转换为 -
     *
     * @param str 字符串
     * @return
     */
    private static String convertSlashToNormal(String str) {
        if (!str.contains("[")) {
            return str.replace("/", "-");
        }
        return str;
    }

    /**
     * . 转换为 -
     *
     * @param str 字符串
     * @return
     */
    private static String convertPointToNormal(String str) {
        if (StringUtil.countWord(str, ".") == 2) {
            return str.replace(".", "-");
        }
        return str;
    }

    /**
     * 中文转换为 - 或 :
     *
     * @param str 字符串
     * @return
     */
    private static String convertCnToNormal(String str) {
        if (StringUtil.hasChinese(str)) {
            return str.replace("年", "-").replace("月", "-").replace("日", "").replace("时", ":").replace("分", ":")
                    .replace("秒", "");
        }
        return str;
    }
}
