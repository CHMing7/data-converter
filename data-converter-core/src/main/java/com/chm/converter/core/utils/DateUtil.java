package com.chm.converter.core.utils;

import com.chm.converter.core.constant.DateFormatPattern;
import com.chm.converter.core.constant.TemporalQuerys;
import com.chm.converter.core.exception.CodecException;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQueries;
import java.time.temporal.TemporalQuery;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

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
     * Date 对象转换为{@link Instant}对象
     *
     * @param temporalAccessor Date对象
     * @return {@link Instant}对象
     */
    public static Date toDate(TemporalAccessor temporalAccessor) {
        Instant instant = toInstant(temporalAccessor);
        return new Date(instant.toEpochMilli());
    }

    /**
     * Date 对象转换为{@link Instant}对象
     *
     * @param date Date对象
     * @return {@link Instant}对象
     */
    public static Instant toInstant(Date date) {
        return null == date ? null : date.toInstant();
    }

    /**
     * TemporalAccessor 对象转换为{@link Instant}对象
     *
     * @param temporalAccessor TemporalAccessor 对象
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
        } else if (temporalAccessor instanceof MonthDay) {
            result = ((MonthDay) temporalAccessor).atYear(Year.now().getValue()).atStartOfDay(ZoneId.systemDefault()).toInstant();
        } else if (temporalAccessor instanceof YearMonth) {
            result = ((YearMonth) temporalAccessor).atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        } else if (temporalAccessor instanceof Year) {
            result = ((Year) temporalAccessor).atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        } else {
            result = fromInstant(temporalAccessor);
        }
        return result;
    }

    /**
     * 从 {@link TemporalAccessor} 对象中获取 {@link Instant}对象
     *
     * @param temporalAccessor TemporalAccessor 对象
     * @return {@link Instant}对象
     */
    public static Instant fromInstant(TemporalAccessor temporalAccessor) {
        if (temporalAccessor == null) {
            return null;
        }

        if (temporalAccessor instanceof Instant) {
            return (Instant) temporalAccessor;
        }
        Objects.requireNonNull(temporalAccessor, "temporal");
        try {
            long instantSecs = temporalAccessor.getLong(ChronoField.INSTANT_SECONDS);
            int nanoOfSecond = temporalAccessor.get(ChronoField.NANO_OF_SECOND);
            return Instant.ofEpochSecond(instantSecs, nanoOfSecond);
        } catch (DateTimeException ex) {
            // Parsed
            ZoneId zone = temporalAccessor.query(TemporalQueries.zoneId());
            if (zone == null) {
                zone = TemporalQueries.zone().queryFrom(temporalAccessor);
            }
            if (zone == null) {
                zone = TemporalQueries.offset().queryFrom(temporalAccessor);
            }
            LocalDate date = LocalDate.from(temporalAccessor);
            LocalTime time = temporalAccessor.query(TemporalQueries.localTime());
            if (time == null) {
                time = LocalTime.MIN;
            }
            ZonedDateTime zonedDateTime = ZonedDateTime.of(date, time, zone);
            return zonedDateTime.toInstant();
        }
    }

    /**
     * Date对象转换为{@link LocalDate}对象
     *
     * @param date Date对象
     * @return {@link LocalDate}对象
     */
    public static LocalDate toLocalDate(Date date) {
        if (null == date) {
            return null;
        }

        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * TemporalAccessor 对象转换为{@link LocalDate}对象
     *
     * @param temporalAccessor TemporalAccessor 对象
     * @return {@link LocalDate}对象
     */
    public static LocalDate toLocalDate(TemporalAccessor temporalAccessor) {
        if (null == temporalAccessor) {
            return null;
        }

        LocalDate result;
        if (temporalAccessor instanceof Instant) {
            result = ((Instant) temporalAccessor).atZone(ZoneId.systemDefault()).toLocalDate();
        } else if (temporalAccessor instanceof LocalDateTime) {
            result = ((LocalDateTime) temporalAccessor).toLocalDate();
        } else if (temporalAccessor instanceof ZonedDateTime) {
            result = ((ZonedDateTime) temporalAccessor).toLocalDate();
        } else if (temporalAccessor instanceof OffsetDateTime) {
            result = ((OffsetDateTime) temporalAccessor).toLocalDate();
        } else if (temporalAccessor instanceof LocalDate) {
            result = (LocalDate) temporalAccessor;
        } else if (temporalAccessor instanceof MonthDay) {
            result = Year.now().atMonthDay((MonthDay) temporalAccessor);
        } else if (temporalAccessor instanceof YearMonth) {
            result = ((YearMonth) temporalAccessor).atDay(1);
        } else if (temporalAccessor instanceof Year) {
            result = ((Year) temporalAccessor).atDay(1);
        } else {
            result = fromLocalDate(temporalAccessor);
        }
        return result;
    }

    /**
     * 从 {@link TemporalAccessor} 对象中获取 {@link LocalDate}对象
     *
     * @param temporalAccessor TemporalAccessor 对象
     * @return {@link LocalDate}对象
     */
    public static LocalDate fromLocalDate(TemporalAccessor temporalAccessor) {
        if (null == temporalAccessor) {
            return null;
        }

        if (temporalAccessor instanceof LocalDate) {
            return ((LocalDate) temporalAccessor);
        }

        Integer year = getAccessorField(temporalAccessor, ChronoField.YEAR);
        Integer monthOfYear = getAccessorField(temporalAccessor, ChronoField.MONTH_OF_YEAR);
        Integer dayOfMonth = getAccessorField(temporalAccessor, ChronoField.DAY_OF_MONTH);
        if (!ArrayUtil.isAllNull(year, monthOfYear, dayOfMonth)) {
            return LocalDate.of(ObjectUtil.defaultIfNull(year, Year.now().getValue()),
                    ObjectUtil.defaultIfNull(monthOfYear, 1),
                    ObjectUtil.defaultIfNull(dayOfMonth, 1));
        }
        return LocalDate.from(temporalAccessor);
    }

    /**
     * Date 对象转换为{@link LocalDateTime}对象
     *
     * @param date Date对象
     * @return {@link LocalDateTime}对象
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        if (null == date) {
            return null;
        }

        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    /**
     * TemporalAccessor 对象转换为{@link LocalDateTime}对象
     *
     * @param temporalAccessor TemporalAccessor 对象
     * @return {@link LocalDateTime}对象
     */
    public static LocalDateTime toLocalDateTime(TemporalAccessor temporalAccessor) {
        if (null == temporalAccessor) {
            return null;
        }
        LocalDateTime result;
        if (temporalAccessor instanceof Instant) {
            result = ((Instant) temporalAccessor).atZone(ZoneId.systemDefault()).toLocalDateTime();
        } else if (temporalAccessor instanceof LocalDateTime) {
            result = (LocalDateTime) temporalAccessor;
        } else if (temporalAccessor instanceof ZonedDateTime) {
            result = ((ZonedDateTime) temporalAccessor).toLocalDateTime();
        } else if (temporalAccessor instanceof OffsetDateTime) {
            result = ((OffsetDateTime) temporalAccessor).toLocalDateTime();
        } else if (temporalAccessor instanceof LocalDate) {
            result = ((LocalDate) temporalAccessor).atStartOfDay();
        } else if (temporalAccessor instanceof LocalTime) {
            // 指定本地时间转换 为Instant，取当天日期
            result = ((LocalTime) temporalAccessor).atDate(LocalDate.now());
        } else if (temporalAccessor instanceof OffsetTime) {
            // 指定本地时间转换 为Instant，取当天日期
            result = ((OffsetTime) temporalAccessor).atDate(LocalDate.now()).toLocalDateTime();
        } else if (temporalAccessor instanceof MonthDay) {
            result = ((MonthDay) temporalAccessor).atYear(Year.now().getValue()).atStartOfDay();
        } else if (temporalAccessor instanceof YearMonth) {
            result = ((YearMonth) temporalAccessor).atDay(1).atStartOfDay();
        } else if (temporalAccessor instanceof Year) {
            result = ((Year) temporalAccessor).atDay(1).atStartOfDay();
        } else if (temporalAccessor instanceof ZoneOffset) {
            result = OffsetDateTime.of(LocalDateTime.now(), (ZoneOffset) temporalAccessor).toLocalDateTime();
        } else {
            result = fromLocalDateTime(temporalAccessor);
        }
        return result;
    }

    /**
     * 从 {@link TemporalAccessor} 对象中获取 {@link LocalDateTime}对象
     *
     * @param temporalAccessor TemporalAccessor 对象
     * @return {@link LocalDateTime}对象
     */
    public static LocalDateTime fromLocalDateTime(TemporalAccessor temporalAccessor) {
        if (null == temporalAccessor) {
            return null;
        }

        if (temporalAccessor instanceof LocalDateTime) {
            return (LocalDateTime) temporalAccessor;
        }

        if (temporalAccessor instanceof LocalDate) {
            return ((LocalDate) temporalAccessor).atStartOfDay();
        }

        Integer year = getAccessorField(temporalAccessor, ChronoField.YEAR);
        Integer monthOfYear = getAccessorField(temporalAccessor, ChronoField.MONTH_OF_YEAR);
        Integer dayOfMonth = getAccessorField(temporalAccessor, ChronoField.DAY_OF_MONTH);
        Integer hourOfDay = getAccessorField(temporalAccessor, ChronoField.HOUR_OF_DAY);
        Integer minuteOfHour = getAccessorField(temporalAccessor, ChronoField.MINUTE_OF_HOUR);
        Integer secondOfMinute = getAccessorField(temporalAccessor, ChronoField.SECOND_OF_MINUTE);
        Integer nanoOfSecond = getAccessorField(temporalAccessor, ChronoField.NANO_OF_SECOND);
        if (!ArrayUtil.isAllNull(year, monthOfYear, dayOfMonth, hourOfDay, minuteOfHour, secondOfMinute, nanoOfSecond)) {
            return LocalDateTime.of(ObjectUtil.defaultIfNull(year, Year.now().getValue()),
                    ObjectUtil.defaultIfNull(monthOfYear, 1),
                    ObjectUtil.defaultIfNull(dayOfMonth, 1),
                    ObjectUtil.defaultIfNull(hourOfDay, 0),
                    ObjectUtil.defaultIfNull(minuteOfHour, 0),
                    ObjectUtil.defaultIfNull(secondOfMinute, 0),
                    ObjectUtil.defaultIfNull(nanoOfSecond, 0)
            );
        }
        return LocalDateTime.from(temporalAccessor);
    }

    /**
     * Date 对象转换为{@link LocalTime}对象
     *
     * @param date Date对象
     * @return {@link LocalTime}对象
     */
    public static LocalTime toLocalTime(Date date) {
        return null == date ? null : date.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
    }

    /**
     * TemporalAccessor 对象转换为{@link LocalTime}对象
     *
     * @param temporalAccessor TemporalAccessor 对象
     * @return {@link LocalTime}对象
     */
    public static LocalTime toLocalTime(TemporalAccessor temporalAccessor) {
        if (null == temporalAccessor) {
            return null;
        }

        LocalTime result;
        if (temporalAccessor instanceof Instant) {
            result = ((Instant) temporalAccessor).atZone(ZoneId.systemDefault()).toLocalTime();
            ;
        } else if (temporalAccessor instanceof LocalDateTime) {
            result = ((LocalDateTime) temporalAccessor).toLocalTime();
        } else if (temporalAccessor instanceof ZonedDateTime) {
            result = ((ZonedDateTime) temporalAccessor).toLocalTime();
        } else if (temporalAccessor instanceof OffsetDateTime) {
            result = ((OffsetDateTime) temporalAccessor).toLocalTime();
        } else if (temporalAccessor instanceof LocalTime) {
            // 指定本地时间转换 为Instant，取当天日期
            result = (LocalTime) temporalAccessor;
        } else if (temporalAccessor instanceof OffsetTime) {
            // 指定本地时间转换 为Instant，取当天日期
            result = ((OffsetTime) temporalAccessor).toLocalTime();
        } else if (temporalAccessor instanceof ZoneOffset) {
            result = OffsetDateTime.of(LocalDateTime.now(), (ZoneOffset) temporalAccessor).toLocalTime();
        } else {
            result = fromLocalTime(temporalAccessor);
        }
        return result;
    }

    /**
     * 从 {@link TemporalAccessor} 对象中获取 {@link LocalTime}对象
     *
     * @param temporalAccessor TemporalAccessor 对象
     * @return {@link LocalTime}对象
     */
    public static LocalTime fromLocalTime(TemporalAccessor temporalAccessor) {
        if (null == temporalAccessor) {
            return null;
        }

        if (temporalAccessor instanceof LocalTime) {
            return (LocalTime) temporalAccessor;
        }

        Integer hourOfDay = getAccessorField(temporalAccessor, ChronoField.HOUR_OF_DAY);
        Integer minuteOfHour = getAccessorField(temporalAccessor, ChronoField.MINUTE_OF_HOUR);
        Integer secondOfMinute = getAccessorField(temporalAccessor, ChronoField.SECOND_OF_MINUTE);
        Integer nanoOfSecond = getAccessorField(temporalAccessor, ChronoField.NANO_OF_SECOND);
        if (!ArrayUtil.isAllNull(hourOfDay, minuteOfHour, secondOfMinute, nanoOfSecond)) {
            return LocalTime.of(ObjectUtil.defaultIfNull(hourOfDay, 0),
                    ObjectUtil.defaultIfNull(minuteOfHour, 0),
                    ObjectUtil.defaultIfNull(secondOfMinute, 0),
                    ObjectUtil.defaultIfNull(nanoOfSecond, 0)
            );
        }
        return LocalTime.from(temporalAccessor);
    }

    /**
     * Date 对象转换为{@link OffsetDateTime}对象
     *
     * @param date Date对象
     * @return {@link OffsetDateTime}对象
     */
    public static OffsetDateTime toOffsetDateTime(Date date) {
        return null == date ? null : OffsetDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    /**
     * TemporalAccessor 对象转换为{@link OffsetDateTime}对象
     *
     * @param temporalAccessor TemporalAccessor 对象
     * @return {@link OffsetDateTime}对象
     */
    public static OffsetDateTime toOffsetDateTime(TemporalAccessor temporalAccessor) {
        if (null == temporalAccessor) {
            return null;
        }

        OffsetDateTime result;
        if (temporalAccessor instanceof Instant) {
            result = ((Instant) temporalAccessor).atZone(ZoneId.systemDefault()).toOffsetDateTime();
        } else if (temporalAccessor instanceof LocalDateTime) {
            result = ((LocalDateTime) temporalAccessor).atZone(ZoneId.systemDefault()).toOffsetDateTime();
        } else if (temporalAccessor instanceof ZonedDateTime) {
            result = ((ZonedDateTime) temporalAccessor).toOffsetDateTime();
        } else if (temporalAccessor instanceof OffsetDateTime) {
            result = (OffsetDateTime) temporalAccessor;
        } else if (temporalAccessor instanceof LocalDate) {
            result = ((LocalDate) temporalAccessor).atStartOfDay(ZoneId.systemDefault()).toOffsetDateTime();
        } else if (temporalAccessor instanceof LocalTime) {
            // 指定本地时间转换 为Instant，取当天日期
            result = ((LocalTime) temporalAccessor).atDate(LocalDate.now()).atZone(ZoneId.systemDefault()).toOffsetDateTime();
        } else if (temporalAccessor instanceof OffsetTime) {
            // 指定本地时间转换 为Instant，取当天日期
            result = ((OffsetTime) temporalAccessor).atDate(LocalDate.now());
        } else if (temporalAccessor instanceof MonthDay) {
            result = ((MonthDay) temporalAccessor).atYear(Year.now().getValue()).atStartOfDay(ZoneId.systemDefault()).toOffsetDateTime();
        } else if (temporalAccessor instanceof YearMonth) {
            result = ((YearMonth) temporalAccessor).atDay(1).atStartOfDay(ZoneId.systemDefault()).toOffsetDateTime();
        } else if (temporalAccessor instanceof Year) {
            result = ((Year) temporalAccessor).atDay(1).atStartOfDay(ZoneId.systemDefault()).toOffsetDateTime();
        } else if (temporalAccessor instanceof ZoneOffset) {
            result = OffsetDateTime.of(LocalDateTime.now(), (ZoneOffset) temporalAccessor);
        } else {
            result = fromOffsetDateTime(temporalAccessor);
        }
        return result;
    }

    /**
     * 从 {@link TemporalAccessor} 对象中获取 {@link OffsetDateTime}对象
     *
     * @param temporalAccessor TemporalAccessor 对象
     * @return {@link OffsetDateTime}对象
     */
    public static OffsetDateTime fromOffsetDateTime(TemporalAccessor temporalAccessor) {
        if (null == temporalAccessor) {
            return null;
        }

        if (temporalAccessor instanceof OffsetDateTime) {
            return (OffsetDateTime) temporalAccessor;
        }

        ZoneOffset zoneOffset = temporalAccessor.query(TemporalQueries.offset());
        ZoneId zoneId = temporalAccessor.query(TemporalQueries.zoneId());

        Integer year = getAccessorField(temporalAccessor, ChronoField.YEAR);
        Integer monthOfYear = getAccessorField(temporalAccessor, ChronoField.MONTH_OF_YEAR);
        Integer dayOfMonth = getAccessorField(temporalAccessor, ChronoField.DAY_OF_MONTH);
        Integer hourOfDay = getAccessorField(temporalAccessor, ChronoField.HOUR_OF_DAY);
        Integer minuteOfHour = getAccessorField(temporalAccessor, ChronoField.MINUTE_OF_HOUR);
        Integer secondOfMinute = getAccessorField(temporalAccessor, ChronoField.SECOND_OF_MINUTE);
        Integer nanoOfSecond = getAccessorField(temporalAccessor, ChronoField.NANO_OF_SECOND);
        if (!ArrayUtil.isAllNull(year, monthOfYear, dayOfMonth, hourOfDay, minuteOfHour, secondOfMinute, nanoOfSecond)) {
            LocalDateTime localDateTime = LocalDateTime.of(
                    ObjectUtil.defaultIfNull(year, Year.now().getValue()),
                    ObjectUtil.defaultIfNull(monthOfYear, 1),
                    ObjectUtil.defaultIfNull(dayOfMonth, 1),
                    ObjectUtil.defaultIfNull(hourOfDay, 0),
                    ObjectUtil.defaultIfNull(minuteOfHour, 0),
                    ObjectUtil.defaultIfNull(secondOfMinute, 0),
                    ObjectUtil.defaultIfNull(nanoOfSecond, 0));
            return zoneOffset != null ?
                    OffsetDateTime.of(localDateTime, zoneOffset) :
                    localDateTime.atZone(zoneId != null ? zoneId : ZoneId.systemDefault())
                            .toOffsetDateTime();
        }
        return OffsetDateTime.from(temporalAccessor);
    }

    /**
     * Date 对象转换为{@link OffsetTime}对象
     *
     * @param date Date对象
     * @return {@link OffsetTime}对象
     */
    public static OffsetTime toOffsetTime(Date date) {
        return null == date ? null : OffsetTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    /**
     * TemporalAccessor 对象转换为{@link OffsetTime}对象
     *
     * @param temporalAccessor TemporalAccessor 对象
     * @return {@link OffsetTime}对象
     */
    public static OffsetTime toOffsetTime(TemporalAccessor temporalAccessor) {
        if (null == temporalAccessor) {
            return null;
        }

        OffsetTime result;
        if (temporalAccessor instanceof Instant) {
            result = ((Instant) temporalAccessor).atZone(ZoneId.systemDefault()).toOffsetDateTime().toOffsetTime();
        } else if (temporalAccessor instanceof LocalDateTime) {
            result = ((LocalDateTime) temporalAccessor).atZone(ZoneId.systemDefault()).toOffsetDateTime().toOffsetTime();
        } else if (temporalAccessor instanceof ZonedDateTime) {
            result = ((ZonedDateTime) temporalAccessor).toOffsetDateTime().toOffsetTime();
        } else if (temporalAccessor instanceof OffsetDateTime) {
            result = ((OffsetDateTime) temporalAccessor).toOffsetTime();
        } else if (temporalAccessor instanceof LocalDate) {
            result = ((LocalDate) temporalAccessor).atStartOfDay(ZoneId.systemDefault()).toOffsetDateTime().toOffsetTime();
        } else if (temporalAccessor instanceof LocalTime) {
            // 指定本地时间转换 为Instant，取当天日期
            result = ((LocalTime) temporalAccessor).atDate(LocalDate.now()).atZone(ZoneId.systemDefault()).toOffsetDateTime().toOffsetTime();
        } else if (temporalAccessor instanceof OffsetTime) {
            // 指定本地时间转换 为Instant，取当天日期
            result = (OffsetTime) temporalAccessor;
        } else if (temporalAccessor instanceof MonthDay) {
            result = ((MonthDay) temporalAccessor).atYear(Year.now().getValue()).atStartOfDay(ZoneId.systemDefault()).toOffsetDateTime().toOffsetTime();
        } else if (temporalAccessor instanceof YearMonth) {
            result = ((YearMonth) temporalAccessor).atDay(1).atStartOfDay(ZoneId.systemDefault()).toOffsetDateTime().toOffsetTime();
        } else if (temporalAccessor instanceof Year) {
            result = ((Year) temporalAccessor).atDay(1).atStartOfDay(ZoneId.systemDefault()).toOffsetDateTime().toOffsetTime();
        } else if (temporalAccessor instanceof ZoneOffset) {
            result = OffsetTime.of(LocalTime.now(), (ZoneOffset) temporalAccessor);
        } else {
            result = fromOffsetTime(temporalAccessor);
        }
        return result;
    }

    /**
     * 从 {@link TemporalAccessor} 对象中获取 {@link OffsetTime}对象
     *
     * @param temporalAccessor TemporalAccessor 对象
     * @return {@link OffsetTime}对象
     */
    public static OffsetTime fromOffsetTime(TemporalAccessor temporalAccessor) {
        if (null == temporalAccessor) {
            return null;
        }

        if (temporalAccessor instanceof OffsetTime) {
            return (OffsetTime) temporalAccessor;
        }

        ZoneOffset zoneOffset = temporalAccessor.query(TemporalQueries.offset());
        ZoneId zoneId = temporalAccessor.query(TemporalQueries.zoneId());

        Integer hourOfDay = getAccessorField(temporalAccessor, ChronoField.HOUR_OF_DAY);
        Integer minuteOfHour = getAccessorField(temporalAccessor, ChronoField.MINUTE_OF_HOUR);
        Integer secondOfMinute = getAccessorField(temporalAccessor, ChronoField.SECOND_OF_MINUTE);
        Integer nanoOfSecond = getAccessorField(temporalAccessor, ChronoField.NANO_OF_SECOND);
        if (!ArrayUtil.isAllNull(hourOfDay, minuteOfHour, secondOfMinute, nanoOfSecond)) {
            LocalTime localTime = LocalTime.of(
                    ObjectUtil.defaultIfNull(hourOfDay, 0),
                    ObjectUtil.defaultIfNull(minuteOfHour, 0),
                    ObjectUtil.defaultIfNull(secondOfMinute, 0),
                    ObjectUtil.defaultIfNull(nanoOfSecond, 0));
            return zoneOffset != null ?
                    OffsetTime.of(localTime, zoneOffset) :
                    localTime.atDate(LocalDate.now())
                            .atZone(zoneId != null ? zoneId : ZoneId.systemDefault())
                            .toOffsetDateTime().toOffsetTime();
        }
        return OffsetTime.from(temporalAccessor);
    }


    /**
     * Date 对象转换为{@link ZonedDateTime}对象
     *
     * @param date Date对象
     * @return {@link ZonedDateTime}对象
     */
    public static ZonedDateTime toZonedDateTime(Date date) {
        return null == date ? null : date.toInstant().atZone(ZoneId.systemDefault());
    }

    /**
     * TemporalAccessor 对象转换为{@link ZonedDateTime}对象
     *
     * @param temporalAccessor TemporalAccessor 对象
     * @return {@link ZonedDateTime}对象
     */
    public static ZonedDateTime toZonedDateTime(TemporalAccessor temporalAccessor) {
        if (null == temporalAccessor) {
            return null;
        }

        ZonedDateTime result;
        if (temporalAccessor instanceof Instant) {
            result = ((Instant) temporalAccessor).atZone(ZoneId.systemDefault());
        } else if (temporalAccessor instanceof LocalDateTime) {
            result = ((LocalDateTime) temporalAccessor).atZone(ZoneId.systemDefault());
        } else if (temporalAccessor instanceof ZonedDateTime) {
            result = (ZonedDateTime) temporalAccessor;
        } else if (temporalAccessor instanceof OffsetDateTime) {
            result = ((OffsetDateTime) temporalAccessor).toZonedDateTime();
        } else if (temporalAccessor instanceof LocalDate) {
            result = ((LocalDate) temporalAccessor).atStartOfDay(ZoneId.systemDefault());
        } else if (temporalAccessor instanceof LocalTime) {
            // 指定本地时间转换 为Instant，取当天日期
            result = ((LocalTime) temporalAccessor).atDate(LocalDate.now()).atZone(ZoneId.systemDefault());
        } else if (temporalAccessor instanceof OffsetTime) {
            // 指定本地时间转换 为Instant，取当天日期
            result = ((OffsetTime) temporalAccessor).atDate(LocalDate.now()).toZonedDateTime();
        } else if (temporalAccessor instanceof MonthDay) {
            result = ((MonthDay) temporalAccessor).atYear(Year.now().getValue()).atStartOfDay(ZoneId.systemDefault());
        } else if (temporalAccessor instanceof YearMonth) {
            result = ((YearMonth) temporalAccessor).atDay(1).atStartOfDay(ZoneId.systemDefault());
        } else if (temporalAccessor instanceof Year) {
            result = ((Year) temporalAccessor).atDay(1).atStartOfDay(ZoneId.systemDefault());
        } else if (temporalAccessor instanceof ZoneOffset) {
            result = OffsetDateTime.of(LocalDateTime.now(), (ZoneOffset) temporalAccessor).toZonedDateTime();
        } else {
            result = fromZonedDateTime(temporalAccessor);
        }
        return result;
    }

    /**
     * 从 {@link TemporalAccessor} 对象中获取 {@link ZonedDateTime}对象
     *
     * @param temporalAccessor TemporalAccessor 对象
     * @return {@link ZonedDateTime}对象
     */
    public static ZonedDateTime fromZonedDateTime(TemporalAccessor temporalAccessor) {
        if (temporalAccessor == null) {
            return null;
        }

        if (temporalAccessor instanceof ZonedDateTime) {
            return (ZonedDateTime) temporalAccessor;
        }

        ZoneId zoneId = temporalAccessor.query(TemporalQueries.zoneId());

        Integer year = getAccessorField(temporalAccessor, ChronoField.YEAR);
        Integer monthOfYear = getAccessorField(temporalAccessor, ChronoField.MONTH_OF_YEAR);
        Integer dayOfMonth = getAccessorField(temporalAccessor, ChronoField.DAY_OF_MONTH);
        Integer hourOfDay = getAccessorField(temporalAccessor, ChronoField.HOUR_OF_DAY);
        Integer minuteOfHour = getAccessorField(temporalAccessor, ChronoField.MINUTE_OF_HOUR);
        Integer secondOfMinute = getAccessorField(temporalAccessor, ChronoField.SECOND_OF_MINUTE);
        Integer nanoOfSecond = getAccessorField(temporalAccessor, ChronoField.NANO_OF_SECOND);
        if (!ArrayUtil.isAllNull(year, monthOfYear, dayOfMonth, hourOfDay, minuteOfHour, secondOfMinute, nanoOfSecond)) {
            return ZonedDateTime.of(
                    ObjectUtil.defaultIfNull(year, Year.now().getValue()),
                    ObjectUtil.defaultIfNull(monthOfYear, 1),
                    ObjectUtil.defaultIfNull(dayOfMonth, 1),
                    ObjectUtil.defaultIfNull(hourOfDay, 0),
                    ObjectUtil.defaultIfNull(minuteOfHour, 0),
                    ObjectUtil.defaultIfNull(secondOfMinute, 0),
                    ObjectUtil.defaultIfNull(nanoOfSecond, 0),
                    zoneId != null ? zoneId : ZoneId.systemDefault());
        }
        return ZonedDateTime.from(temporalAccessor);
    }

    /**
     * Date 对象转换为{@link MonthDay}对象
     *
     * @param date Date对象
     * @return {@link MonthDay}对象
     */
    public static MonthDay toMonthDay(Date date) {
        if (null == date) {
            return null;
        }
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return MonthDay.of(localDate.getMonth(), localDate.getDayOfMonth());
    }

    /**
     * TemporalAccessor 对象转换为{@link MonthDay}对象
     *
     * @param temporalAccessor TemporalAccessor 对象
     * @return {@link MonthDay}对象
     */
    public static MonthDay toMonthDay(TemporalAccessor temporalAccessor) {
        if (null == temporalAccessor) {
            return null;
        }

        MonthDay result;
        if (temporalAccessor instanceof Instant) {
            LocalDate localDate = ((Instant) temporalAccessor).atZone(ZoneId.systemDefault()).toLocalDate();
            result = MonthDay.of(localDate.getMonth(), localDate.getDayOfMonth());
        } else if (temporalAccessor instanceof LocalDateTime) {
            LocalDateTime localDateTime = (LocalDateTime) temporalAccessor;
            result = MonthDay.of(localDateTime.getMonth(), localDateTime.getDayOfMonth());
        } else if (temporalAccessor instanceof ZonedDateTime) {
            ZonedDateTime zonedDateTime = (ZonedDateTime) temporalAccessor;
            result = MonthDay.of(zonedDateTime.getMonth(), zonedDateTime.getDayOfMonth());
        } else if (temporalAccessor instanceof OffsetDateTime) {
            OffsetDateTime offsetDateTime = (OffsetDateTime) temporalAccessor;
            result = MonthDay.of(offsetDateTime.getMonth(), offsetDateTime.getDayOfMonth());
        } else if (temporalAccessor instanceof LocalDate) {
            LocalDate localDate = (LocalDate) temporalAccessor;
            result = MonthDay.of(localDate.getMonth(), localDate.getDayOfMonth());
        } else if (temporalAccessor instanceof MonthDay) {
            result = (MonthDay) temporalAccessor;
        } else if (temporalAccessor instanceof YearMonth) {
            LocalDate localDate = ((YearMonth) temporalAccessor).atDay(1);
            result = MonthDay.of(localDate.getMonth(), localDate.getDayOfMonth());
        } else if (temporalAccessor instanceof Year) {
            LocalDate localDate = ((Year) temporalAccessor).atDay(1);
            result = MonthDay.of(localDate.getMonth(), localDate.getDayOfMonth());
        } else {
            result = fromMonthDay(temporalAccessor);
        }
        return result;
    }

    /**
     * 从 {@link TemporalAccessor} 对象中获取 {@link MonthDay}对象
     *
     * @param temporalAccessor TemporalAccessor 对象
     * @return {@link MonthDay}对象
     */
    public static MonthDay fromMonthDay(TemporalAccessor temporalAccessor) {
        if (temporalAccessor == null) {
            return null;
        }

        if (temporalAccessor instanceof MonthDay) {
            return (MonthDay) temporalAccessor;
        }

        Integer monthOfYear = getAccessorField(temporalAccessor, ChronoField.MONTH_OF_YEAR);
        Integer dayOfMonth = getAccessorField(temporalAccessor, ChronoField.DAY_OF_MONTH);
        if (!ArrayUtil.isAllNull(monthOfYear, dayOfMonth)) {
            return MonthDay.of(
                    ObjectUtil.defaultIfNull(monthOfYear, 1),
                    ObjectUtil.defaultIfNull(dayOfMonth, 1));
        }
        return MonthDay.from(temporalAccessor);
    }

    /**
     * Date 对象转换为{@link YearMonth}对象
     *
     * @param date Date对象
     * @return {@link YearMonth}对象
     */
    public static YearMonth toYearMonth(Date date) {
        if (null == date) {
            return null;
        }
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return YearMonth.of(localDate.getYear(), localDate.getMonth());
    }

    /**
     * TemporalAccessor 对象转换为{@link YearMonth}对象
     *
     * @param temporalAccessor TemporalAccessor 对象
     * @return {@link YearMonth}对象
     */
    public static YearMonth toYearMonth(TemporalAccessor temporalAccessor) {
        if (null == temporalAccessor) {
            return null;
        }

        YearMonth result;
        if (temporalAccessor instanceof Instant) {
            LocalDate localDate = ((Instant) temporalAccessor).atZone(ZoneId.systemDefault()).toLocalDate();
            result = YearMonth.of(localDate.getYear(), localDate.getMonth());
        } else if (temporalAccessor instanceof LocalDateTime) {
            LocalDateTime localDateTime = (LocalDateTime) temporalAccessor;
            result = YearMonth.of(localDateTime.getYear(), localDateTime.getMonth());
        } else if (temporalAccessor instanceof ZonedDateTime) {
            ZonedDateTime zonedDateTime = (ZonedDateTime) temporalAccessor;
            result = YearMonth.of(zonedDateTime.getYear(), zonedDateTime.getMonth());
        } else if (temporalAccessor instanceof OffsetDateTime) {
            OffsetDateTime offsetDateTime = (OffsetDateTime) temporalAccessor;
            result = YearMonth.of(offsetDateTime.getYear(), offsetDateTime.getMonth());
        } else if (temporalAccessor instanceof LocalDate) {
            LocalDate localDate = (LocalDate) temporalAccessor;
            result = YearMonth.of(localDate.getYear(), localDate.getMonth());
        } else if (temporalAccessor instanceof YearMonth) {
            result = (YearMonth) temporalAccessor;
        } else if (temporalAccessor instanceof Year) {
            result = ((Year) temporalAccessor).atMonth(1);
        } else {
            result = fromYearMonth(temporalAccessor);
        }
        return result;
    }

    /**
     * 从 {@link TemporalAccessor} 对象中获取 {@link YearMonth}对象
     *
     * @param temporalAccessor TemporalAccessor 对象
     * @return {@link YearMonth}对象
     */
    public static YearMonth fromYearMonth(TemporalAccessor temporalAccessor) {
        if (temporalAccessor == null) {
            return null;
        }

        if (temporalAccessor instanceof YearMonth) {
            return (YearMonth) temporalAccessor;
        }

        Integer year = getAccessorField(temporalAccessor, ChronoField.YEAR);
        Integer monthOfYear = getAccessorField(temporalAccessor, ChronoField.MONTH_OF_YEAR);
        if (!ArrayUtil.isAllNull(year, monthOfYear)) {
            return YearMonth.of(
                    ObjectUtil.defaultIfNull(year, Year.now().getValue()),
                    ObjectUtil.defaultIfNull(monthOfYear, 1));
        }
        return YearMonth.from(temporalAccessor);
    }

    /**
     * Date 对象转换为{@link Year}对象
     *
     * @param date Date对象
     * @return {@link Year}对象
     */
    public static Year toYear(Date date) {
        if (null == date) {
            return null;
        }
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return Year.of(localDate.getYear());
    }

    /**
     * TemporalAccessor 对象转换为{@link Year}对象
     *
     * @param temporalAccessor TemporalAccessor 对象
     * @return {@link Year}对象
     */
    public static Year toYear(TemporalAccessor temporalAccessor) {
        if (null == temporalAccessor) {
            return null;
        }

        Year result;
        if (temporalAccessor instanceof Instant) {
            LocalDate localDate = ((Instant) temporalAccessor).atZone(ZoneId.systemDefault()).toLocalDate();
            result = Year.of(localDate.getYear());
        } else if (temporalAccessor instanceof LocalDateTime) {
            LocalDateTime localDateTime = (LocalDateTime) temporalAccessor;
            result = Year.of(localDateTime.getYear());
        } else if (temporalAccessor instanceof ZonedDateTime) {
            ZonedDateTime zonedDateTime = (ZonedDateTime) temporalAccessor;
            result = Year.of(zonedDateTime.getYear());
        } else if (temporalAccessor instanceof OffsetDateTime) {
            OffsetDateTime offsetDateTime = (OffsetDateTime) temporalAccessor;
            result = Year.of(offsetDateTime.getYear());
        } else if (temporalAccessor instanceof LocalDate) {
            LocalDate localDate = (LocalDate) temporalAccessor;
            result = Year.of(localDate.getYear());
        } else if (temporalAccessor instanceof YearMonth) {
            result = Year.of(((YearMonth) temporalAccessor).getYear());
        } else if (temporalAccessor instanceof Year) {
            result = (Year) temporalAccessor;
        } else {
            result = fromYear(temporalAccessor);
        }
        return result;
    }

    /**
     * 从 {@link TemporalAccessor} 对象中获取 {@link Year}对象
     *
     * @param temporalAccessor TemporalAccessor 对象
     * @return {@link Year}对象
     */
    public static Year fromYear(TemporalAccessor temporalAccessor) {
        if (temporalAccessor == null) {
            return null;
        }

        if (temporalAccessor instanceof Year) {
            return (Year) temporalAccessor;
        }

        Integer year = getAccessorField(temporalAccessor, ChronoField.YEAR);
        if (year != null) {
            return Year.of(year);
        }
        return Year.from(temporalAccessor);
    }

    /**
     * TemporalAccessor 对象转换为{@link ZoneOffset}对象
     *
     * @param temporalAccessor TemporalAccessor 对象
     * @return {@link ZoneOffset}对象
     */
    public static ZoneOffset toZoneOffset(TemporalAccessor temporalAccessor) {
        if (null == temporalAccessor) {
            return null;
        }

        ZoneOffset result;
        if (temporalAccessor instanceof ZonedDateTime) {
            result = ((ZonedDateTime) temporalAccessor).getOffset();
        } else if (temporalAccessor instanceof OffsetDateTime) {
            result = ((OffsetDateTime) temporalAccessor).getOffset();
        } else if (temporalAccessor instanceof OffsetTime) {
            // 指定本地时间转换 为Instant，取当天日期
            result = ((OffsetTime) temporalAccessor).getOffset();
        } else if (temporalAccessor instanceof ZoneOffset) {
            result = (ZoneOffset) temporalAccessor;
        } else {
            result = fromZoneOffset(temporalAccessor);
        }
        return result;
    }

    /**
     * 从 {@link TemporalAccessor} 对象中获取 {@link ZoneOffset}对象
     *
     * @param temporalAccessor TemporalAccessor 对象
     * @return {@link ZoneOffset}对象
     */
    public static ZoneOffset fromZoneOffset(TemporalAccessor temporalAccessor) {
        if (temporalAccessor == null) {
            return null;
        }

        if (temporalAccessor instanceof ZoneOffset) {
            return (ZoneOffset) temporalAccessor;
        }

        ZoneOffset zoneOffset = temporalAccessor.query(TemporalQueries.offset());
        if (zoneOffset == null) {
            throw new DateTimeException("Unable to obtain ZoneOffset from TemporalAccessor: " +
                    temporalAccessor + " of type " + temporalAccessor.getClass().getName());
        }
        return zoneOffset;
    }

    /**
     * 安全获取时间的某个属性，属性不存在返回0
     *
     * @param temporalAccessor 需要获取的时间对象
     * @param field            需要获取的属性
     * @return
     */
    public static Integer getAccessorField(TemporalAccessor temporalAccessor, TemporalField field) {
        return temporalAccessor.isSupported(field) ? temporalAccessor.get(field) : null;
    }

    public static void main(String[] args) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss zzz");
        System.out.println(dateTimeFormatter.format(ZonedDateTime.now()));
        String dateStr = "2022-02-02";
        LocalDateTime parse = DateUtil.parse(dateStr, TemporalQuerys.LOCAL_DATE_TIME_QUERY);
        System.out.println(parse);

        String dateStr2 = "2022-12-12 12:12:12";
        LocalDate localDate = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").parse(dateStr2, LocalDate::from);
        System.out.println(localDate);
    }
}
