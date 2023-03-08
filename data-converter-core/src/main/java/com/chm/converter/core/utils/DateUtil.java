package com.chm.converter.core.utils;

import com.chm.converter.core.constant.DateFormatPattern;
import com.chm.converter.core.exception.CodecException;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQueries;
import java.time.temporal.TemporalQuery;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static java.time.temporal.ChronoField.INSTANT_SECONDS;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-08-26
 **/
public class DateUtil {

    /**
     * 解析日期时异常
     */
    public static final String PARSE_LOCAL_DATE_EXCEPTION = "Unable to obtain";
    private static final Map<Integer, Set<DateTimeFormatter>> DATE_TIME_FORMATTER_LENGTH_MAP = MapUtil.newConcurrentHashMap();

    static {
        // 单个的M d H m s都要额外注册一个+1，有几个就额外注册几个
        //  a要+1
        //  小写xxx要+3
        //  XXX要+3和-2（考虑0时区时-2）
        //  Z要+4
        //  'T'要-2
        registerDateTimeFormatter(DateFormatPattern.YYYY_MM_DD.length(), DateFormatPattern.YYYY_MM_DD_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.YYYY_M_D.length(), DateFormatPattern.YYYY_M_D_FORMATTER);
        registerDateTimeFormatter(DateFormatPattern.YYYY_M_D.length() + 1, DateFormatPattern.YYYY_M_D_FORMATTER);
        registerDateTimeFormatter(DateFormatPattern.YYYY_M_D.length() + 2, DateFormatPattern.YYYY_M_D_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.YYYYMMDD.length(), DateFormatPattern.YYYYMMDD_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.YYYY_MM_DD_EN.length(), DateFormatPattern.YYYY_MM_DD_EN_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.YYYY_M_D_EN.length(), DateFormatPattern.YYYY_M_D_EN_FORMATTER);
        registerDateTimeFormatter(DateFormatPattern.YYYY_M_D_EN.length() + 1, DateFormatPattern.YYYY_M_D_EN_FORMATTER);
        registerDateTimeFormatter(DateFormatPattern.YYYY_M_D_EN.length() + 2, DateFormatPattern.YYYY_M_D_EN_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.YYYY_MM_DD_CN.length(), DateFormatPattern.YYYY_MM_DD_CN_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.YYYY_M_D_CN.length(), DateFormatPattern.YYYY_M_D_CN_FORMATTER);
        registerDateTimeFormatter(DateFormatPattern.YYYY_M_D_CN.length() + 1, DateFormatPattern.YYYY_M_D_CN_FORMATTER);
        registerDateTimeFormatter(DateFormatPattern.YYYY_M_D_CN.length() + 2, DateFormatPattern.YYYY_M_D_CN_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.YYYY_MM_DD_POINT.length(), DateFormatPattern.YYYY_MM_DD_POINT_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.YYYY_M_D_POINT.length(), DateFormatPattern.YYYY_M_D_POINT_FORMATTER);
        registerDateTimeFormatter(DateFormatPattern.YYYY_M_D_POINT.length() + 1, DateFormatPattern.YYYY_M_D_POINT_FORMATTER);
        registerDateTimeFormatter(DateFormatPattern.YYYY_M_D_POINT.length() + 2, DateFormatPattern.YYYY_M_D_POINT_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.YY_MM_DD_EN.length(), DateFormatPattern.YY_MM_DD_EN_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.YY_M_D_EN.length(), DateFormatPattern.YY_M_D_EN_FORMATTER);
        registerDateTimeFormatter(DateFormatPattern.YY_M_D_EN.length() + 1, DateFormatPattern.YY_M_D_EN_FORMATTER);
        registerDateTimeFormatter(DateFormatPattern.YY_M_D_EN.length() + 2, DateFormatPattern.YY_M_D_EN_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.MM_DD_YY_EN.length(), DateFormatPattern.MM_DD_YY_EN_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.M_D_YY_EN.length(), DateFormatPattern.M_D_YY_EN_FORMATTER);
        registerDateTimeFormatter(DateFormatPattern.M_D_YY_EN.length() + 1, DateFormatPattern.M_D_YY_EN_FORMATTER);
        registerDateTimeFormatter(DateFormatPattern.M_D_YY_EN.length() + 2, DateFormatPattern.M_D_YY_EN_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.YYYY_MM_DD_EEE.length(), DateFormatPattern.YYYY_MM_DD_EEE_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.YY.length(), DateFormatPattern.YY_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.YYYY.length(), DateFormatPattern.YYYY_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.YYYY_MM.length(), DateFormatPattern.YYYY_MM_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.YYYYMM.length(), DateFormatPattern.YYYYMM_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.YYYY_MM_EN.length(), DateFormatPattern.YYYY_MM_EN_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.YYYY_MM_CN.length(), DateFormatPattern.YYYY_MM_CN_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.YYYY_M_CN.length(), DateFormatPattern.YYYY_M_CN_FORMATTER);
        registerDateTimeFormatter(DateFormatPattern.YYYY_M_CN.length() + 1, DateFormatPattern.YYYY_M_CN_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.MM_DD.length(), DateFormatPattern.MM_DD_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.MMDD.length(), DateFormatPattern.MMDD_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.MM_DD_EN.length(), DateFormatPattern.MM_DD_EN_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.M_D_EN.length(), DateFormatPattern.M_D_EN_FORMATTER);
        registerDateTimeFormatter(DateFormatPattern.M_D_EN.length() + 1, DateFormatPattern.M_D_EN_FORMATTER);
        registerDateTimeFormatter(DateFormatPattern.M_D_EN.length() + 2, DateFormatPattern.M_D_EN_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.MM_DD_CN.length(), DateFormatPattern.MM_DD_CN_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.M_D_CN.length(), DateFormatPattern.M_D_CN_FORMATTER);
        registerDateTimeFormatter(DateFormatPattern.M_D_CN.length() + 1, DateFormatPattern.M_D_CN_FORMATTER);
        registerDateTimeFormatter(DateFormatPattern.M_D_CN.length() + 2, DateFormatPattern.M_D_CN_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.HH_MM_SS.length(), DateFormatPattern.HH_MM_SS_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.H_M_S.length(), DateFormatPattern.H_M_S_FORMATTER);
        registerDateTimeFormatter(DateFormatPattern.H_M_S.length() + 1, DateFormatPattern.H_M_S_FORMATTER);
        registerDateTimeFormatter(DateFormatPattern.H_M_S.length() + 2, DateFormatPattern.H_M_S_FORMATTER);
        registerDateTimeFormatter(DateFormatPattern.H_M_S.length() + 3, DateFormatPattern.H_M_S_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.HHMMSS.length(), DateFormatPattern.HHMMSS_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.HH_MM_SS_CN.length(), DateFormatPattern.HH_MM_SS_CN_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.HH_MM.length(), DateFormatPattern.HH_MM_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.H_M.length(), DateFormatPattern.H_M_FORMATTER);
        registerDateTimeFormatter(DateFormatPattern.H_M.length() + 1, DateFormatPattern.H_M_FORMATTER);
        registerDateTimeFormatter(DateFormatPattern.H_M.length() + 2, DateFormatPattern.H_M_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.HH_MM_CN.length(), DateFormatPattern.HH_MM_CN_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.HH_MM_A.length() + 1, DateFormatPattern.HH_MM_A_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.HH_MM_SS_SSS.length(), DateFormatPattern.HH_MM_SS_SSS_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.HH_MM_SS_SSSSSS.length(), DateFormatPattern.HH_MM_SS_SSSSSS_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.HH_MM_SS_SSSSSSSSS.length(), DateFormatPattern.HH_MM_SS_SSSSSSSSS_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.YYYY_MM_DD_HH_MM_SS.length(), DateFormatPattern.YYYY_MM_DD_HH_MM_SS_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.YYYY_M_D_H_M_S.length(), DateFormatPattern.YYYY_M_D_H_M_S_FORMATTER);
        registerDateTimeFormatter(DateFormatPattern.YYYY_M_D_H_M_S.length() + 1, DateFormatPattern.YYYY_M_D_H_M_S_FORMATTER);
        registerDateTimeFormatter(DateFormatPattern.YYYY_M_D_H_M_S.length() + 2, DateFormatPattern.YYYY_M_D_H_M_S_FORMATTER);
        registerDateTimeFormatter(DateFormatPattern.YYYY_M_D_H_M_S.length() + 3, DateFormatPattern.YYYY_M_D_H_M_S_FORMATTER);
        registerDateTimeFormatter(DateFormatPattern.YYYY_M_D_H_M_S.length() + 4, DateFormatPattern.YYYY_M_D_H_M_S_FORMATTER);
        registerDateTimeFormatter(DateFormatPattern.YYYY_M_D_H_M_S.length() + 5, DateFormatPattern.YYYY_M_D_H_M_S_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.YYYYMMDDHHMMSS.length(), DateFormatPattern.YYYYMMDDHHMMSS_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.YYYY_MM_DD_HH_MM_SS_EN.length(), DateFormatPattern.YYYY_MM_DD_HH_MM_SS_EN_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.YYYY_MM_DD_POINT_HH_MM_SS_EN.length(), DateFormatPattern.YYYY_MM_DD_POINT_HH_MM_SS_EN_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.YYYY_M_D_H_M_S_EN.length(), DateFormatPattern.YYYY_M_D_H_M_S_EN_FORMATTER);
        registerDateTimeFormatter(DateFormatPattern.YYYY_M_D_H_M_S_EN.length() + 1, DateFormatPattern.YYYY_M_D_H_M_S_EN_FORMATTER);
        registerDateTimeFormatter(DateFormatPattern.YYYY_M_D_H_M_S_EN.length() + 2, DateFormatPattern.YYYY_M_D_H_M_S_EN_FORMATTER);
        registerDateTimeFormatter(DateFormatPattern.YYYY_M_D_H_M_S_EN.length() + 3, DateFormatPattern.YYYY_M_D_H_M_S_EN_FORMATTER);
        registerDateTimeFormatter(DateFormatPattern.YYYY_M_D_H_M_S_EN.length() + 4, DateFormatPattern.YYYY_M_D_H_M_S_EN_FORMATTER);
        registerDateTimeFormatter(DateFormatPattern.YYYY_M_D_H_M_S_EN.length() + 5, DateFormatPattern.YYYY_M_D_H_M_S_EN_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.YYYY_MM_DD_HH_MM_SS_CN.length(), DateFormatPattern.YYYY_MM_DD_HH_MM_SS_CN_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.YYYY_MM_DD_HH_MM_SS_CN_ALL.length(), DateFormatPattern.YYYY_MM_DD_HH_MM_SS_CN_ALL_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.YYYY_MM_DD_HH_MM.length(), DateFormatPattern.YYYY_MM_DD_HH_MM_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.YYYY_M_D_H_M.length(), DateFormatPattern.YYYY_M_D_H_M_FORMATTER);
        registerDateTimeFormatter(DateFormatPattern.YYYY_M_D_H_M.length() + 1, DateFormatPattern.YYYY_M_D_H_M_FORMATTER);
        registerDateTimeFormatter(DateFormatPattern.YYYY_M_D_H_M.length() + 2, DateFormatPattern.YYYY_M_D_H_M_FORMATTER);
        registerDateTimeFormatter(DateFormatPattern.YYYY_M_D_H_M.length() + 3, DateFormatPattern.YYYY_M_D_H_M_FORMATTER);
        registerDateTimeFormatter(DateFormatPattern.YYYY_M_D_H_M.length() + 4, DateFormatPattern.YYYY_M_D_H_M_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.YYYYMMDDHHMM.length(), DateFormatPattern.YYYYMMDDHHMM_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.YYYY_MM_DD_HH_MM_EN.length(), DateFormatPattern.YYYY_MM_DD_HH_MM_EN_FORMATTER);
        registerDateTimeFormatter(DateFormatPattern.YYYY_M_D_H_M_EN.length() + 1, DateFormatPattern.YYYY_M_D_H_M_EN_FORMATTER);
        registerDateTimeFormatter(DateFormatPattern.YYYY_M_D_H_M_EN.length() + 2, DateFormatPattern.YYYY_M_D_H_M_EN_FORMATTER);
        registerDateTimeFormatter(DateFormatPattern.YYYY_M_D_H_M_EN.length() + 3, DateFormatPattern.YYYY_M_D_H_M_EN_FORMATTER);
        registerDateTimeFormatter(DateFormatPattern.YYYY_M_D_H_M_EN.length() + 4, DateFormatPattern.YYYY_M_D_H_M_EN_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.YYYY_M_D_H_M_A_EN.length() + 1, DateFormatPattern.YYYY_M_D_H_M_A_EN_FORMATTER);
        registerDateTimeFormatter(DateFormatPattern.YYYY_M_D_H_M_A_EN.length() + 2, DateFormatPattern.YYYY_M_D_H_M_A_EN_FORMATTER);
        registerDateTimeFormatter(DateFormatPattern.YYYY_M_D_H_M_A_EN.length() + 3, DateFormatPattern.YYYY_M_D_H_M_A_EN_FORMATTER);
        registerDateTimeFormatter(DateFormatPattern.YYYY_M_D_H_M_A_EN.length() + 4, DateFormatPattern.YYYY_M_D_H_M_A_EN_FORMATTER);
        registerDateTimeFormatter(DateFormatPattern.YYYY_M_D_H_M_A_EN.length() + 5, DateFormatPattern.YYYY_M_D_H_M_A_EN_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.MM_DD_HH_MM.length(), DateFormatPattern.MM_DD_HH_MM_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.MM_DD_HH_MM_CN.length(), DateFormatPattern.MM_DD_HH_MM_CN_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.MM_DD_HH_MM_SS.length(), DateFormatPattern.MM_DD_HH_MM_SS_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.MM_DD_HH_MM_SS_CN.length(), DateFormatPattern.MM_DD_HH_MM_SS_CN_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.YYYY_MM_DD_HH_MM_SS_A_CN.length() + 1, DateFormatPattern.YYYY_MM_DD_HH_MM_SS_A_CN_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.YYYY_MM_DD_HH_MM_SS_A_CN_ALL.length() + 1, DateFormatPattern.YYYY_MM_DD_HH_MM_SS_A_CN_ALL_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.YYYY_MM_DD_HH_MM_SS_SSS.length(), DateFormatPattern.YYYY_MM_DD_HH_MM_SS_SSS_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.YYYY_MM_DD_HH_MM_SS_SSS_COMMA.length(), DateFormatPattern.YYYY_MM_DD_HH_MM_SS_SSS_COMMA_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.YYYYMMDDHHMMSSSSS.length(), DateFormatPattern.YYYYMMDDHHMMSSSSS_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.YYYY_M_D_H_M_S_SSS.length(), DateFormatPattern.YYYY_M_D_H_M_S_SSS_FORMATTER);
        registerDateTimeFormatter(DateFormatPattern.YYYY_M_D_H_M_S_SSS.length() + 1, DateFormatPattern.YYYY_M_D_H_M_S_SSS_FORMATTER);
        registerDateTimeFormatter(DateFormatPattern.YYYY_M_D_H_M_S_SSS.length() + 2, DateFormatPattern.YYYY_M_D_H_M_S_SSS_FORMATTER);
        registerDateTimeFormatter(DateFormatPattern.YYYY_M_D_H_M_S_SSS.length() + 3, DateFormatPattern.YYYY_M_D_H_M_S_SSS_FORMATTER);
        registerDateTimeFormatter(DateFormatPattern.YYYY_M_D_H_M_S_SSS.length() + 4, DateFormatPattern.YYYY_M_D_H_M_S_SSS_FORMATTER);
        registerDateTimeFormatter(DateFormatPattern.YYYY_M_D_H_M_S_SSS.length() + 5, DateFormatPattern.YYYY_M_D_H_M_S_SSS_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.YYYY_M_D_H_M_S_SSS_EN.length(), DateFormatPattern.YYYY_M_D_H_M_S_SSS_EN_FORMATTER);
        registerDateTimeFormatter(DateFormatPattern.YYYY_M_D_H_M_S_SSS_EN.length() + 1, DateFormatPattern.YYYY_M_D_H_M_S_SSS_EN_FORMATTER);
        registerDateTimeFormatter(DateFormatPattern.YYYY_M_D_H_M_S_SSS_EN.length() + 2, DateFormatPattern.YYYY_M_D_H_M_S_SSS_EN_FORMATTER);
        registerDateTimeFormatter(DateFormatPattern.YYYY_M_D_H_M_S_SSS_EN.length() + 3, DateFormatPattern.YYYY_M_D_H_M_S_SSS_EN_FORMATTER);
        registerDateTimeFormatter(DateFormatPattern.YYYY_M_D_H_M_S_SSS_EN.length() + 4, DateFormatPattern.YYYY_M_D_H_M_S_SSS_EN_FORMATTER);
        registerDateTimeFormatter(DateFormatPattern.YYYY_M_D_H_M_S_SSS_EN.length() + 5, DateFormatPattern.YYYY_M_D_H_M_S_SSS_EN_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.YYYY_M_D_H_M_S_SSS_COMMA.length(), DateFormatPattern.YYYY_M_D_H_M_S_SSS_COMMA_FORMATTER);
        registerDateTimeFormatter(DateFormatPattern.YYYY_M_D_H_M_S_SSS_COMMA.length() + 1, DateFormatPattern.YYYY_M_D_H_M_S_SSS_COMMA_FORMATTER);
        registerDateTimeFormatter(DateFormatPattern.YYYY_M_D_H_M_S_SSS_COMMA.length() + 2, DateFormatPattern.YYYY_M_D_H_M_S_SSS_COMMA_FORMATTER);
        registerDateTimeFormatter(DateFormatPattern.YYYY_M_D_H_M_S_SSS_COMMA.length() + 3, DateFormatPattern.YYYY_M_D_H_M_S_SSS_COMMA_FORMATTER);
        registerDateTimeFormatter(DateFormatPattern.YYYY_M_D_H_M_S_SSS_COMMA.length() + 4, DateFormatPattern.YYYY_M_D_H_M_S_SSS_COMMA_FORMATTER);
        registerDateTimeFormatter(DateFormatPattern.YYYY_M_D_H_M_S_SSS_COMMA.length() + 5, DateFormatPattern.YYYY_M_D_H_M_S_SSS_COMMA_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.YYYY_MM_DD_HH_MM_SS_SSSSSS.length(), DateFormatPattern.YYYY_MM_DD_HH_MM_SS_SSSSSS_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.YYYY_MM_DD_HH_MM_SS_SSSSSSSSS.length(), DateFormatPattern.YYYY_MM_DD_HH_MM_SS_SSSSSSSSS_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.YYYY_MM_DD_T_HH_MM_SS_Z.length() - 2 + 4, DateFormatPattern.YYYY_MM_DD_T_HH_MM_SS_Z_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.YYYY_MM_DD_T_HH_MM_SS_XXX.length() - 2 + 3, DateFormatPattern.YYYY_MM_DD_T_HH_MM_SS_XXX_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.YYYY_MM_DD_T_HH_MM_SS_XXX_Z.length() - 2 + 3, DateFormatPattern.YYYY_MM_DD_T_HH_MM_SS_XXX_Z_FORMATTER);
        registerDateTimeFormatter(DateFormatPattern.YYYY_MM_DD_T_HH_MM_SS_XXX_Z.length() - 2 - 2, DateFormatPattern.YYYY_MM_DD_T_HH_MM_SS_XXX_Z_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.YYYY_MM_DD_T_HH_MM_SS_SSS_Z.length() - 2 + 4, DateFormatPattern.YYYY_MM_DD_T_HH_MM_SS_SSS_Z_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.YYYY_MM_DD_T_HH_MM_SS_SSS_XXX.length() - 2 + 3, DateFormatPattern.YYYY_MM_DD_T_HH_MM_SS_SSS_XXX_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.YYYY_MM_DD_T_HH_MM_SS_SSS_XXX_Z.length() - 2 + 3, DateFormatPattern.YYYY_MM_DD_T_HH_MM_SS_SSS_XXX_Z_FORMATTER);
        registerDateTimeFormatter(DateFormatPattern.YYYY_MM_DD_T_HH_MM_SS_SSS_XXX_Z.length() - 2 - 2, DateFormatPattern.YYYY_MM_DD_T_HH_MM_SS_SSS_XXX_Z_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.YYYY_MM_DD_T_HH_MM_SS_SSSSSS_Z.length() - 2 + 4, DateFormatPattern.YYYY_MM_DD_T_HH_MM_SS_SSSSSS_Z_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.YYYY_MM_DD_T_HH_MM_SS_SSSSSS_XXX.length() - 2 + 3, DateFormatPattern.YYYY_MM_DD_T_HH_MM_SS_SSSSSS_XXX_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.YYYY_MM_DD_T_HH_MM_SS_SSSSSS_XXX_Z.length() - 2 + 3, DateFormatPattern.YYYY_MM_DD_T_HH_MM_SS_SSSSSS_XXX_Z_FORMATTER);
        registerDateTimeFormatter(DateFormatPattern.YYYY_MM_DD_T_HH_MM_SS_SSSSSS_XXX_Z.length() - 2 - 2, DateFormatPattern.YYYY_MM_DD_T_HH_MM_SS_SSSSSS_XXX_Z_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.YYYY_MM_DD_T_HH_MM_SS_SSSSSSSSS_Z.length() - 2 + 4, DateFormatPattern.YYYY_MM_DD_T_HH_MM_SS_SSSSSSSSS_Z_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.YYYY_MM_DD_T_HH_MM_SS_SSSSSSSSS_XXX.length() - 2 + 3, DateFormatPattern.YYYY_MM_DD_T_HH_MM_SS_SSSSSSSSS_XXX_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.YYYY_MM_DD_T_HH_MM_SS_SSSSSSSSS_XXX_Z.length() - 2 + 3, DateFormatPattern.YYYY_MM_DD_T_HH_MM_SS_SSSSSSSSS_XXX_Z_FORMATTER);
        registerDateTimeFormatter(DateFormatPattern.YYYY_MM_DD_T_HH_MM_SS_SSSSSSSSS_XXX_Z.length() - 2 - 2, DateFormatPattern.YYYY_MM_DD_T_HH_MM_SS_SSSSSSSSS_XXX_Z_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.EEE_MMM_DD_HH_MM_SS_ZZZ_YYYY.length(), DateFormatPattern.EEE_MMM_DD_HH_MM_SS_ZZZ_YYYY_FORMATTER);
        registerDateTimeFormatter(DateFormatPattern.EEE_MMM_DD_HH_MM_SS_ZZZ_YYYY.length() + 1, DateFormatPattern.EEE_MMM_DD_HH_MM_SS_ZZZ_YYYY_FORMATTER);

        registerDateTimeFormatter(DateFormatPattern.EEE_DD_MMM_YYYY_HH_MM_SS_ZZZ.length(), DateFormatPattern.EEE_DD_MMM_YYYY_HH_MM_SS_ZZZ_FORMATTER);
        registerDateTimeFormatter(DateFormatPattern.EEE_DD_MMM_YYYY_HH_MM_SS_ZZZ.length() + 1, DateFormatPattern.EEE_DD_MMM_YYYY_HH_MM_SS_ZZZ_FORMATTER);
    }

    public static void registerDateTimeFormatter(Integer dateStrLen, DateTimeFormatter dateTimeFormatter) {
        Set<DateTimeFormatter> formatterSet = MapUtil.computeIfAbsent(DATE_TIME_FORMATTER_LENGTH_MAP, dateStrLen, i -> CollUtil.newLinkedHashSet());
        formatterSet.add(dateTimeFormatter);
    }

    public static void unregisterDateTimeFormatter(Integer dateStrLen, DateTimeFormatter dateTimeFormatter) {
        Set<DateTimeFormatter> formatterSet = MapUtil.computeIfAbsent(DATE_TIME_FORMATTER_LENGTH_MAP, dateStrLen, i -> CollUtil.newLinkedHashSet());
        formatterSet.remove(dateTimeFormatter);
    }

    /**
     * 根据 formatter格式化 date
     *
     * @param date      Date
     * @param formatter DateTimeFormatter
     * @return String
     */
    public static String format(Date date, DateTimeFormatter formatter) {
        Objects.requireNonNull(date, "date");
        Objects.requireNonNull(formatter, "formatter");
        if (formatter.getZone() != null) {
            return formatter.format(Instant.ofEpochMilli(date.getTime()));
        }
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).format(formatter);
    }

    /**
     * 根据 formatter格式化 temporal
     *
     * @param temporal  TemporalAccessor
     * @param formatter DateTimeFormatter
     * @return String
     */
    public static String format(TemporalAccessor temporal, DateTimeFormatter formatter) {
        Objects.requireNonNull(temporal, "temporal");
        Objects.requireNonNull(formatter, "formatter");
        if (temporal instanceof Instant && formatter.getZone() == null) {
            return ((Instant) temporal).atZone(ZoneId.systemDefault()).format(formatter);
        }
        return formatter.format(temporal);
    }

    /**
     * 根据 formatter解析为 Date
     *
     * @param text      待解析字符串
     * @param formatter DateTimeFormatter
     * @return Date
     */
    public static Date parseToDate(String text, DateTimeFormatter formatter) {
        Objects.requireNonNull(formatter, "formatter");
        Date date;
        try {
            date = DateUtil.toDate(toLocalDateTime(formatter.parse(text)));
        } catch (DateTimeException e) {
            if (e.getMessage().startsWith(PARSE_LOCAL_DATE_EXCEPTION)) {
                date = toDate(LocalDate.parse(text, formatter));
            } else {
                throw e;
            }
        }
        return date;
    }

    /**
     * 解析String为 Date
     *
     * @param text 待解析字符串
     * @return Date
     */
    public static Date parseToDate(String text) {
        int strLen = text.length();
        Set<DateTimeFormatter> formatterSet = MapUtil.computeIfAbsent(DATE_TIME_FORMATTER_LENGTH_MAP, strLen, i -> CollUtil.newLinkedHashSet());
        for (DateTimeFormatter dtf : formatterSet) {
            try {
                return DateUtil.toDate(DateUtil.toInstant(dtf.withZone(ZoneId.systemDefault()).parse(text)));
            } catch (Exception ignored) {
            }
        }
        // 没有更多匹配的时间格式
        throw new CodecException("No format fit for date String [{}] !", text);
    }

    /**
     * 解析String为 ZonedDateTime
     *
     * @param text 待解析字符串
     * @return Date
     */
    public static ZonedDateTime parse(String text) {
        return parse(text, ZonedDateTime::from);
    }

    /**
     * 根据 formatter解析为 T
     *
     * @param text          待解析字符串
     * @param temporalQuery 时间类型转换方法
     * @return Date
     */
    public static <T> T parse(String text, TemporalQuery<T> temporalQuery) {
        Objects.requireNonNull(temporalQuery, "temporalQuery");
        int strLen = text.length();
        Set<DateTimeFormatter> formatterSet = MapUtil.computeIfAbsent(DATE_TIME_FORMATTER_LENGTH_MAP, strLen, i -> CollUtil.newLinkedHashSet());
        for (DateTimeFormatter dtf : formatterSet) {
            try {
                return dtf.parse(text, temporalQuery);
            } catch (Exception ignored) {
            }
        }
        // 没有更多匹配的时间格式
        throw new CodecException("No format fit for date String [{}] !", text);
    }

    /**
     * temporal转LocalDateTime
     *
     * @param temporal TemporalAccessor
     * @return LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(TemporalAccessor temporal) {
        if (temporal instanceof LocalDateTime) {
            return (LocalDateTime) temporal;
        }
        LocalDate date = temporal.query(TemporalQueries.localDate());
        if (date == null) {
            date = LocalDate.of(0, 1, 1);
        }
        LocalTime time = temporal.query(TemporalQueries.localTime());
        if (time == null) {
            time = LocalTime.MIN;
        }
        return LocalDateTime.of(date, time);
    }


    /**
     * ZonedDateTime转LocalDateTime
     * 注意时间对应的时区和默认时区差异
     *
     * @param zonedDateTime ZonedDateTime
     * @return LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(ZonedDateTime zonedDateTime) {
        Objects.requireNonNull(zonedDateTime, "zonedDateTime");
        return zonedDateTime.toLocalDateTime();
    }


    /**
     * Date对象转换为{@link Instant}对象
     *
     * @param date Date对象
     * @return {@link Instant}对象
     */
    public static Instant toInstant(Date date) {
        return null == date ? null : date.toInstant();
    }

    /**
     * Date对象转换为{@link Instant}对象
     *
     * @param temporalAccessor Date对象
     * @return {@link Instant}对象
     */
    public static Instant toInstant(TemporalAccessor temporalAccessor) {
        if (null == temporalAccessor) {
            return null;
        }

        Instant result;
        if (temporalAccessor instanceof Instant) {
            result = (Instant) temporalAccessor;
        } else if (temporalAccessor instanceof LocalDateTime) {
            result = ((LocalDateTime) temporalAccessor).atZone(ZoneId.systemDefault()).toInstant();
        } else if (temporalAccessor instanceof ZonedDateTime) {
            result = ((ZonedDateTime) temporalAccessor).toInstant();
        } else if (temporalAccessor instanceof OffsetDateTime) {
            result = ((OffsetDateTime) temporalAccessor).toInstant();
        } else if (temporalAccessor instanceof LocalDate) {
            result = ((LocalDate) temporalAccessor).atStartOfDay(ZoneId.systemDefault()).toInstant();
        } else if (temporalAccessor instanceof LocalTime) {
            // 指定本地时间转换 为Instant，取当天日期
            result = ((LocalTime) temporalAccessor).atDate(LocalDate.now()).atZone(ZoneId.systemDefault()).toInstant();
        } else if (temporalAccessor instanceof OffsetTime) {
            // 指定本地时间转换 为Instant，取当天日期
            result = ((OffsetTime) temporalAccessor).atDate(LocalDate.now()).toInstant();
        } else {
            result = fromInstant(temporalAccessor);
        }
        return result;
    }

    public static Instant fromInstant(TemporalAccessor temporal) {
        if (temporal instanceof Instant) {
            return (Instant) temporal;
        }
        Objects.requireNonNull(temporal, "temporal");
        try {
            long instantSecs = temporal.getLong(ChronoField.INSTANT_SECONDS);
            int nanoOfSecond = temporal.get(ChronoField.NANO_OF_SECOND);
            return Instant.ofEpochSecond(instantSecs, nanoOfSecond);
        } catch (DateTimeException ex) {
            // Parsed
            ZoneId zone = temporal.query(TemporalQueries.zoneId());
            if (zone == null) {
                zone = TemporalQueries.zone().queryFrom(temporal);
            }
            if (zone == null) {
                zone = TemporalQueries.offset().queryFrom(temporal);
            }
            LocalDate date = temporal.query(TemporalQueries.localDate());
            if (date == null) {
                date = LocalDate.of(0, 1, 1);
            }
            LocalTime time = temporal.query(TemporalQueries.localTime());
            if (time == null) {
                time = LocalTime.MIN;
            }
            ZonedDateTime zonedDateTime = ZonedDateTime.of(date, time, zone);
            return zonedDateTime.toInstant();
        }
    }

    /**
     * Instant转Date
     *
     * @param instant Instant
     * @return Date
     */
    public static Date toDate(Instant instant) {
        return null == instant ? null : new Date(instant.toEpochMilli());
    }

    /**
     * LocalDate转Date
     *
     * @param localDate LocalDate
     * @return Date
     */
    public static Date toDate(LocalDate localDate) {
        Objects.requireNonNull(localDate, "localDate");
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * LocalDateTime转Date
     *
     * @param localDateTime LocalDateTime
     * @return Date
     */
    public static Date toDate(LocalDateTime localDateTime) {
        Objects.requireNonNull(localDateTime, "localDateTime");
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * LocalDateTime转Date
     *
     * @param zonedDateTime ZonedDateTime
     * @return Date
     */
    public static Date toDate(ZonedDateTime zonedDateTime) {
        Objects.requireNonNull(zonedDateTime, "zonedDateTime");
        return Date.from(zonedDateTime.toInstant());
    }


    /**
     * LocalDateTime转ZonedDateTime，时区为系统默认时区
     *
     * @param localDateTime LocalDateTime
     * @return ZonedDateTime
     */
    public static ZonedDateTime toZonedDateTime(LocalDateTime localDateTime) {
        Objects.requireNonNull(localDateTime, "localDateTime");
        return localDateTime.atZone(ZoneId.systemDefault());
    }

    /**
     * LocalDate转ZonedDateTime，时区为系统默认时区
     *
     * @param localDate LocalDate
     * @return ZonedDateTime such as 2020-02-19T00:00+08:00[Asia/Shanghai]
     */
    public static ZonedDateTime toZonedDateTime(LocalDate localDate) {
        Objects.requireNonNull(localDate, "localDate");
        return localDate.atStartOfDay().atZone(ZoneId.systemDefault());
    }

    /**
     * LocalTime转ZonedDateTime
     * 以当天的日期+LocalTime组成新的ZonedDateTime，时区为系统默认时区
     *
     * @param localTime LocalTime
     * @return ZonedDateTime
     */
    public static ZonedDateTime toZonedDateTime(LocalTime localTime) {
        Objects.requireNonNull(localTime, "localTime");
        return LocalDate.now().atTime(localTime).atZone(ZoneId.systemDefault());
    }

    /**
     * Instant转ZonedDateTime，时区为系统默认时区
     *
     * @param instant Instant
     * @return ZonedDateTime
     */
    public static ZonedDateTime toZonedDateTime(Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).atZone(ZoneId.systemDefault());
    }

    /**
     * temporal转ZonedDateTime，时区为系统默认时区
     *
     * @param temporal TemporalAccessor
     * @return ZonedDateTime
     */
    public static ZonedDateTime toZonedDateTime(TemporalAccessor temporal) {
        Objects.requireNonNull(temporal, "temporal");

        if (temporal instanceof Instant) {
            return toZonedDateTime((Instant) temporal);
        } else if (temporal instanceof LocalDate) {
            return toZonedDateTime((LocalDate) temporal);
        } else if (temporal instanceof LocalDateTime) {
            return toZonedDateTime((LocalDateTime) temporal);
        } else if (temporal instanceof LocalTime) {
            return toZonedDateTime((LocalTime) temporal);
        } else if (temporal instanceof ZonedDateTime) {
            return (ZonedDateTime) temporal;
        } else if (temporal instanceof YearMonth) {
            return toZonedDateTime((YearMonth) temporal);
        } else if (temporal instanceof OffsetDateTime) {
            return ((OffsetDateTime) temporal).toZonedDateTime();
        } else if (temporal instanceof OffsetTime) {
            return ((OffsetTime) temporal).atDate(LocalDate.now()).toZonedDateTime();
        } else {
            return fromZonedDateTime(temporal);
        }
    }

    public static ZonedDateTime fromZonedDateTime(TemporalAccessor temporal) {
        if (temporal instanceof ZonedDateTime) {
            return (ZonedDateTime) temporal;
        }
        try {
            if (temporal.isSupported(INSTANT_SECONDS)) {
                return ZonedDateTime.from(temporal);
            } else {
                ZoneId zone = ZoneId.from(temporal);
                LocalDate date = temporal.query(TemporalQueries.localDate());
                if (date == null) {
                    date = LocalDate.of(0, 1, 1);
                }
                LocalTime time = temporal.query(TemporalQueries.localTime());
                if (time == null) {
                    time = LocalTime.MIN;
                }
                return ZonedDateTime.of(date, time, zone);
            }
        } catch (DateTimeException ex) {
            throw new DateTimeException("Unable to obtain ZonedDateTime from TemporalAccessor: " +
                    temporal + " of type " + temporal.getClass().getName(), ex);
        }
    }

    public static void main(String[] args) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss zzz");
        System.out.println(dateTimeFormatter.format(ZonedDateTime.now()));
        String dateStr = "2022-02-02T12:12:12.123Z";
        ZonedDateTime parse = DateUtil.parse(dateStr);
        System.out.println(parse);

        String dateStr2 = "2022-12-12 12:12:12";
        LocalDate localDate = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").parse(dateStr2, LocalDate::from);
        System.out.println(localDate);
    }
}
