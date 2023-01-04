package com.chm.converter.core.constant;

import java.time.format.DateTimeFormatter;

/**
 * @author caihongming
 * @version v1.0
 * @since 2022-08-16
 **/
public interface DateFormatPattern {

    // ==================================yyyy-MM-dd相关Pattern==================================

    /**
     * yyyy-MM-dd 比如：  2020-05-23
     */
    String YYYY_MM_DD = "yyyy-MM-dd";
    DateTimeFormatter YYYY_MM_DD_FORMATTER = DateTimeFormatter.ofPattern(YYYY_MM_DD);

    /**
     * yyyy-M-d 不补0 比如：  2020-5-23
     */
    String YYYY_M_D = "yyyy-M-d";
    DateTimeFormatter YYYY_M_D_FORMATTER = DateTimeFormatter.ofPattern(YYYY_M_D);

    /**
     * yyyyMMdd  比如：  20200523
     */
    String YYYYMMDD = "yyyyMMdd";
    DateTimeFormatter YYYYMMDD_FORMATTER = DateTimeFormatter.ofPattern(YYYYMMDD);

    /**
     * yyyy/MM/dd  比如：  2020/05/23
     */
    String YYYY_MM_DD_EN = "yyyy/MM/dd";
    DateTimeFormatter YYYY_MM_DD_EN_FORMATTER = DateTimeFormatter.ofPattern(YYYY_MM_DD_EN);

    /**
     * yyyy/M/d 不补0  比如：  2020/5/23
     */
    String YYYY_M_D_EN = "yyyy/M/d";
    DateTimeFormatter YYYY_M_D_EN_FORMATTER = DateTimeFormatter.ofPattern(YYYY_M_D_EN);

    /**
     * yyyy年MM月dd日  比如： 2020年05月23日
     */
    String YYYY_MM_DD_CN = "yyyy年MM月dd日";
    DateTimeFormatter YYYY_MM_DD_CN_FORMATTER = DateTimeFormatter.ofPattern(YYYY_MM_DD_CN);

    /**
     * yyyy年M月d日 不补0  比如： 2020年5月23日
     */
    String YYYY_M_D_CN = "yyyy年M月d日";
    DateTimeFormatter YYYY_M_D_CN_FORMATTER = DateTimeFormatter.ofPattern(YYYY_M_D_CN);

    /**
     * yyyy.MM.dd  比如：2020.05.23
     */
    String YYYY_MM_DD_POINT = "yyyy.MM.dd";
    DateTimeFormatter YYYY_MM_DD_POINT_FORMATTER = DateTimeFormatter.ofPattern(YYYY_MM_DD_POINT);

    /**
     * yyyy.M.d 不补0  比如：2020.5.23
     */
    String YYYY_M_D_POINT = "yyyy.M.d";
    DateTimeFormatter YYYY_M_D_POINT_FORMATTER = DateTimeFormatter.ofPattern(YYYY_M_D_POINT);

    /**
     * yy/MM/dd  比如：20/05/23
     */
    String YY_MM_DD_EN = "yy/MM/dd";
    DateTimeFormatter YY_MM_DD_EN_FORMATTER = DateTimeFormatter.ofPattern(YY_MM_DD_EN);

    /**
     * yy/M/d  比如：20/5/23
     */
    String YY_M_D_EN = "yy/M/d";
    DateTimeFormatter YY_M_D_EN_FORMATTER = DateTimeFormatter.ofPattern(YY_M_D_EN);

    /**
     * MM/dd/yy  比如：05/23/20
     */
    String MM_DD_YY_EN = "MM/dd/yy";
    DateTimeFormatter MM_DD_YY_EN_FORMATTER = DateTimeFormatter.ofPattern(MM_DD_YY_EN);

    /**
     * M/d/yy  比如：5/23/20
     */
    String M_D_YY_EN = "M/d/yy";
    DateTimeFormatter M_D_YY_EN_FORMATTER = DateTimeFormatter.ofPattern(M_D_YY_EN);

    /**
     * yyyy-MM-dd E  比如：2020-05-23 星期六
     */
    String YYYY_MM_DD_EEE = "yyyy-MM-dd EEE";
    DateTimeFormatter YYYY_MM_DD_EEE_FORMATTER = DateTimeFormatter.ofPattern(YYYY_MM_DD_EEE);

    /**
     * yy 年的后2位  比如： 20
     */
    String YY = "yy";
    DateTimeFormatter YY_FORMATTER = DateTimeFormatter.ofPattern(YY);

    /**
     * yyyy  比如：2020
     */
    String YYYY = "yyyy";
    DateTimeFormatter YYYY_FORMATTER = DateTimeFormatter.ofPattern(YYYY);

    /**
     * yyyy-MM  比如：2020-05
     */
    String YYYY_MM = "yyyy-MM";
    DateTimeFormatter YYYY_MM_FORMATTER = DateTimeFormatter.ofPattern(YYYY_MM);

    /**
     * yyyyMM  比如：202005
     */
    String YYYYMM = "yyyyMM";
    DateTimeFormatter YYYYMM_FORMATTER = DateTimeFormatter.ofPattern(YYYYMM);

    /**
     * yyyy/MM  比如：2020/05
     */
    String YYYY_MM_EN = "yyyy/MM";
    DateTimeFormatter YYYY_MM_EN_FORMATTER = DateTimeFormatter.ofPattern(YYYY_MM_EN);

    /**
     * yyyy年MM月  比如：2020年05月
     */
    String YYYY_MM_CN = "yyyy年MM月";
    DateTimeFormatter YYYY_MM_CN_FORMATTER = DateTimeFormatter.ofPattern(YYYY_MM_CN);

    /**
     * yyyy年M月  比如：2020年5月
     */
    String YYYY_M_CN = "yyyy年M月";
    DateTimeFormatter YYYY_M_CN_FORMATTER = DateTimeFormatter.ofPattern(YYYY_M_CN);

    /**
     * MM-dd  比如：05-23
     */
    String MM_DD = "MM-dd";
    DateTimeFormatter MM_DD_FORMATTER = DateTimeFormatter.ofPattern(MM_DD);

    /**
     * MMdd  比如：0523
     */
    String MMDD = "MMdd";
    DateTimeFormatter MMDD_FORMATTER = DateTimeFormatter.ofPattern(MMDD);

    /**
     * MM/dd  比如：05/23
     */
    String MM_DD_EN = "MM/dd";
    DateTimeFormatter MM_DD_EN_FORMATTER = DateTimeFormatter.ofPattern(MM_DD_EN);

    /**
     * M/d 不补0  比如：5/23
     */
    String M_D_EN = "M/d";
    DateTimeFormatter M_D_EN_FORMATTER = DateTimeFormatter.ofPattern(M_D_EN);

    /**
     * MM月dd日  比如：05月23日
     */
    String MM_DD_CN = "MM月dd日";
    DateTimeFormatter MM_DD_CN_FORMATTER = DateTimeFormatter.ofPattern(MM_DD_CN);

    /**
     * M月d日 不补0  比如：5月23日
     */
    String M_D_CN = "M月d日";
    DateTimeFormatter M_D_CN_FORMATTER = DateTimeFormatter.ofPattern(M_D_CN);

    // ==================================HH:mm:ss 相关Pattern==================================

    /**
     * HH:mm:ss  比如：17:26:30
     */
    String HH_MM_SS = "HH:mm:ss";
    DateTimeFormatter HH_MM_SS_FORMATTER = DateTimeFormatter.ofPattern(HH_MM_SS);

    /**
     * H:m:s  比如：17:6:30
     */
    String H_M_S = "H:m:s";
    DateTimeFormatter H_M_S_FORMATTER = DateTimeFormatter.ofPattern(H_M_S);

    /**
     * HHmmss  比如：170630
     */
    String HHMMSS = "HHmmss";
    DateTimeFormatter HHMMSS_FORMATTER = DateTimeFormatter.ofPattern(HHMMSS);

    /**
     * HH时mm分ss秒  比如：17时06分30秒
     */
    String HH_MM_SS_CN = "HH时mm分ss秒";
    DateTimeFormatter HH_MM_SS_CN_FORMATTER = DateTimeFormatter.ofPattern(HH_MM_SS_CN);

    /**
     * HH:mm  比如：17:06
     */
    String HH_MM = "HH:mm";
    DateTimeFormatter HH_MM_FORMATTER = DateTimeFormatter.ofPattern(HH_MM);

    /**
     * H:m  比如：17:6
     */
    String H_M = "H:m";
    DateTimeFormatter H_M_FORMATTER = DateTimeFormatter.ofPattern(H_M);

    /**
     * HH时mm分 比如：17时06分
     */
    String HH_MM_CN = "HH时mm分";
    DateTimeFormatter HH_MM_CN_FORMATTER = DateTimeFormatter.ofPattern(HH_MM_CN);

    /**
     * hh:mm a 比如：05:06 下午 如果需要 显示PM 需要设置 Locale.ENGLISH
     */
    String HH_MM_A = "hh:mm a";
    DateTimeFormatter HH_MM_A_FORMATTER = DateTimeFormatter.ofPattern(HH_MM_A);

    // ==================================HH:mm:ss.SSS 相关Pattern==================================

    /**
     * HH:mm:ss.SSS  比如：17:26:30.272
     */
    String HH_MM_SS_SSS = "HH:mm:ss.SSS";
    DateTimeFormatter HH_MM_SS_SSS_FORMATTER = DateTimeFormatter.ofPattern(HH_MM_SS_SSS);

    // ==================================HH:mm:ss.SSSSSS 相关Pattern==================================

    /**
     * HH:mm:ss.SSSSSS  比如：17:26:30.272150
     */
    String HH_MM_SS_SSSSSS = "HH:mm:ss.SSSSSS";
    DateTimeFormatter HH_MM_SS_SSSSSS_FORMATTER = DateTimeFormatter.ofPattern(HH_MM_SS_SSSSSS);

    // ==================================HH:mm:ss.SSSSSSSSS 相关Pattern==================================

    /**
     * HH:mm:ss.SSSSSSSSS  比如：17:26:30.272150620
     */
    String HH_MM_SS_SSSSSSSSS = "HH:mm:ss.SSSSSSSSS";
    DateTimeFormatter HH_MM_SS_SSSSSSSSS_FORMATTER = DateTimeFormatter.ofPattern(HH_MM_SS_SSSSSSSSS);

    // ==================================yyyy-MM-dd HH:mm:ss 相关Pattern==================================

    /**
     * yyyy-MM-dd HH:mm:ss 比如：2020-05-23 17:06:30
     */
    String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    DateTimeFormatter YYYY_MM_DD_HH_MM_SS_FORMATTER = DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS);

    /**
     * yyyy-M-d H:m:s 比如：2020-5-23 17:6:30
     */
    String YYYY_M_D_H_M_S = "yyyy-M-d H:m:s";
    DateTimeFormatter YYYY_M_D_H_M_S_FORMATTER = DateTimeFormatter.ofPattern(YYYY_M_D_H_M_S);

    /**
     * yyyyMMddHHmmss 比如：20200523170630
     */
    String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
    DateTimeFormatter YYYYMMDDHHMMSS_FORMATTER = DateTimeFormatter.ofPattern(YYYYMMDDHHMMSS);

    /**
     * yyyy/MM/dd HH:mm:ss 比如：2020/05/23 17:06:30
     */
    String YYYY_MM_DD_HH_MM_SS_EN = "yyyy/MM/dd HH:mm:ss";
    DateTimeFormatter YYYY_MM_DD_HH_MM_SS_EN_FORMATTER = DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS_EN);

    /**
     * yyyy.MM.dd HH:mm:ss 比如：2020.05.23 17:06:30
     */
    String YYYY_MM_DD_POINT_HH_MM_SS_EN = "yyyy.MM.dd HH:mm:ss";
    DateTimeFormatter YYYY_MM_DD_POINT_HH_MM_SS_EN_FORMATTER = DateTimeFormatter.ofPattern(YYYY_MM_DD_POINT_HH_MM_SS_EN);

    /**
     * yyyy/M/d H:m:s 比如：2020/5/23 17:6:30
     */
    String YYYY_M_D_H_M_S_EN = "yyyy/M/d H:m:s";
    DateTimeFormatter YYYY_M_D_H_M_S_EN_FORMATTER = DateTimeFormatter.ofPattern(YYYY_M_D_H_M_S_EN);

    /**
     * yyyy年MM月dd日 HH:mm:ss 比如：2020年05月23日 17:06:30
     */
    String YYYY_MM_DD_HH_MM_SS_CN = "yyyy年MM月dd日 HH:mm:ss";
    DateTimeFormatter YYYY_MM_DD_HH_MM_SS_CN_FORMATTER = DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS_CN);

    /**
     * yyyy年MM月dd日 HH时mm分ss秒 比如：2020年05月23日 17时06分30秒
     */
    String YYYY_MM_DD_HH_MM_SS_CN_ALL = "yyyy年MM月dd日 HH时mm分ss秒";
    DateTimeFormatter YYYY_MM_DD_HH_MM_SS_CN_ALL_FORMATTER = DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS_CN_ALL);

    /**
     * yyyy-MM-dd HH:mm 比如：2020-05-23 17:06
     */
    String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
    DateTimeFormatter YYYY_MM_DD_HH_MM_FORMATTER = DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM);

    /**
     * yyyy-M-d H:m 比如：2020-5-23 17:6
     */
    String YYYY_M_D_H_M = "yyyy-M-d H:m";
    DateTimeFormatter YYYY_M_D_H_M_FORMATTER = DateTimeFormatter.ofPattern(YYYY_M_D_H_M);

    /**
     * yyyyMMddHHmm 比如：202005231706
     */
    String YYYYMMDDHHMM = "yyyyMMddHHmm";
    DateTimeFormatter YYYYMMDDHHMM_FORMATTER = DateTimeFormatter.ofPattern(YYYYMMDDHHMM);

    /**
     * yyyy/MM/dd HH:mm 比如：2020/05/23 17:06
     */
    String YYYY_MM_DD_HH_MM_EN = "yyyy/MM/dd HH:mm";
    DateTimeFormatter YYYY_MM_DD_HH_MM_EN_FORMATTER = DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_EN);

    /**
     * yyyy/M/d H:m 比如：2020/5/23 17:6
     */
    String YYYY_M_D_H_M_EN = "yyyy/M/d H:m";
    DateTimeFormatter YYYY_M_D_H_M_EN_FORMATTER = DateTimeFormatter.ofPattern(YYYY_M_D_H_M_EN);

    /**
     * yyyy/M/d h:m a 比如：2020/5/23 5:6 下午
     */
    String YYYY_M_D_H_M_A_EN = "yyyy/M/d h:m a";
    DateTimeFormatter YYYY_M_D_H_M_A_EN_FORMATTER = DateTimeFormatter.ofPattern(YYYY_M_D_H_M_A_EN);

    /**
     * MM-dd HH:mm 比如：05-23 17:06
     */
    String MM_DD_HH_MM = "MM-dd HH:mm";
    DateTimeFormatter MM_DD_HH_MM_FORMATTER = DateTimeFormatter.ofPattern(MM_DD_HH_MM);

    /**
     * MM月dd日 HH:mm 比如：05月23日 17:06
     */
    String MM_DD_HH_MM_CN = "MM月dd日 HH:mm";
    DateTimeFormatter MM_DD_HH_MM_CN_FORMATTER = DateTimeFormatter.ofPattern(MM_DD_HH_MM_CN);

    /**
     * MM-dd HH:mm:ss 比如：05-23 17:06:30
     */
    String MM_DD_HH_MM_SS = "MM-dd HH:mm:ss";
    DateTimeFormatter MM_DD_HH_MM_SS_FORMATTER = DateTimeFormatter.ofPattern(MM_DD_HH_MM_SS);

    /**
     * MM月dd日 HH:mm:ss 比如：05月23日 17:06:30
     */
    String MM_DD_HH_MM_SS_CN = "MM月dd日 HH:mm:ss";
    DateTimeFormatter MM_DD_HH_MM_SS_CN_FORMATTER = DateTimeFormatter.ofPattern(MM_DD_HH_MM_SS_CN);

    /**
     * yyyy年MM月dd日 hh:mm:ss a 比如：2020年05月23日 05:06:30 下午  如果需要 显示PM 需要设置 Locale.ENGLISH
     */
    String YYYY_MM_DD_HH_MM_SS_A_CN = "yyyy年MM月dd日 hh:mm:ss a";
    DateTimeFormatter YYYY_MM_DD_HH_MM_SS_A_CN_FORMATTER = DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS_A_CN);

    /**
     * yyyy年MM月dd日 hh时mm分ss秒 a 比如：2020年05月23日 17时06分30秒 下午  如果需要 显示PM 需要设置 Locale.ENGLISH
     */
    String YYYY_MM_DD_HH_MM_SS_A_CN_ALL = "yyyy年MM月dd日 hh时mm分ss秒 a";
    DateTimeFormatter YYYY_MM_DD_HH_MM_SS_A_CN_ALL_FORMATTER = DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS_A_CN_ALL);

    // ==================================yyyy-MM-dd HH:mm:ss.SSS 相关Pattern==================================

    /**
     * yyyy-MM-dd HH:mm:ss.SSS 比如：2020-05-23 17:06:30.272
     */
    String YYYY_MM_DD_HH_MM_SS_SSS = "yyyy-MM-dd HH:mm:ss.SSS";
    DateTimeFormatter YYYY_MM_DD_HH_MM_SS_SSS_FORMATTER = DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS_SSS);

    /**
     * yyyy-MM-dd HH:mm:ss,SSS 比如：2020-05-23 17:06:30,272
     */
    String YYYY_MM_DD_HH_MM_SS_SSS_COMMA = "yyyy-MM-dd HH:mm:ss,SSS";
    DateTimeFormatter YYYY_MM_DD_HH_MM_SS_SSS_COMMA_FORMATTER = DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS_SSS_COMMA);

    /**
     * yyyyMMddHHmmssSSS 比如：20200523170630272
     */
    String YYYYMMDDHHMMSSSSS = "yyyyMMddHHmmssSSS";
    DateTimeFormatter YYYYMMDDHHMMSSSSS_FORMATTER = DateTimeFormatter.ofPattern(YYYYMMDDHHMMSSSSS);

    /**
     * yyyy-M-d H:m:s.SSS 比如：2020-5-23 17:6:30.272
     */
    String YYYY_M_D_H_M_S_SSS = "yyyy-M-d H:m:s.SSS";
    DateTimeFormatter YYYY_M_D_H_M_S_SSS_FORMATTER = DateTimeFormatter.ofPattern(YYYY_M_D_H_M_S_SSS);

    /**
     * yyyy/M/d H:m:s.SSS 比如：2020/5/23 17:6:30.272
     */
    String YYYY_M_D_H_M_S_SSS_EN = "yyyy/M/d H:m:s.SSS";
    DateTimeFormatter YYYY_M_D_H_M_S_SSS_EN_FORMATTER = DateTimeFormatter.ofPattern(YYYY_M_D_H_M_S_SSS_EN);

    /**
     * yyyy-M-d H:m:s,SSS 比如：2020-5-23 17:6:30,272
     */
    String YYYY_M_D_H_M_S_SSS_COMMA = "yyyy-M-d H:m:s,SSS";
    DateTimeFormatter YYYY_M_D_H_M_S_SSS_COMMA_FORMATTER = DateTimeFormatter.ofPattern(YYYY_M_D_H_M_S_SSS_COMMA);

    // ==================================yyyy-MM-dd HH:mm:ss.SSSSSS 相关Pattern==================================

    /**
     * yyyy-MM-dd HH:mm:ss.SSSSSS 比如：2020-05-23 17:06:30.272150
     */
    String YYYY_MM_DD_HH_MM_SS_SSSSSS = "yyyy-MM-dd HH:mm:ss.SSSSSS";
    DateTimeFormatter YYYY_MM_DD_HH_MM_SS_SSSSSS_FORMATTER = DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS_SSSSSS);

    // ==================================yyyy-MM-dd HH:mm:ss.SSSSSSSSS 相关Pattern==================================

    /**
     * yyyy-MM-dd HH:mm:ss.SSSSSSSSS 比如：2020-05-23 17:06:30.272150620
     */
    String YYYY_MM_DD_HH_MM_SS_SSSSSSSSS = "yyyy-MM-dd HH:mm:ss.SSSSSSSSS";
    DateTimeFormatter YYYY_MM_DD_HH_MM_SS_SSSSSSSSS_FORMATTER = DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS_SSSSSSSSS);

    // ==================================Iso相关Pattern 包含 T==================================

    /**
     * yyyy-MM-dd'T'HH:mm:ssZ 比如：2020-05-23T17:06:30+0800 2020-05-23T09:06:30+0000
     */
    String YYYY_MM_DD_T_HH_MM_SS_Z = "yyyy-MM-dd'T'HH:mm:ssZ";
    DateTimeFormatter YYYY_MM_DD_T_HH_MM_SS_Z_FORMATTER = DateTimeFormatter.ofPattern(YYYY_MM_DD_T_HH_MM_SS_Z);

    /**
     * yyyy-MM-dd'T'HH:mm:ssxxx 比如：2020-05-23T17:06:30+08:00 2020-05-23T09:06:30+00:00
     */
    String YYYY_MM_DD_T_HH_MM_SS_XXX = "yyyy-MM-dd'T'HH:mm:ssxxx";
    DateTimeFormatter YYYY_MM_DD_T_HH_MM_SS_XXX_FORMATTER = DateTimeFormatter.ofPattern(YYYY_MM_DD_T_HH_MM_SS_XXX);

    /**
     * yyyy-MM-dd'T'HH:mm:ssXXX 比如：2020-05-23T17:06:30+08:00 2020-05-23T09:06:30Z 0时区时末尾 为Z
     */
    String YYYY_MM_DD_T_HH_MM_SS_XXX_Z = "yyyy-MM-dd'T'HH:mm:ssXXX";
    DateTimeFormatter YYYY_MM_DD_T_HH_MM_SS_XXX_Z_FORMATTER = DateTimeFormatter.ofPattern(YYYY_MM_DD_T_HH_MM_SS_XXX_Z);

    /**
     * yyyy-MM-dd'T'HH:mm:ss.SSSZ 比如：2020-05-23T17:06:30.272+0800 2020-05-23T09:06:30.272+0000
     */
    String YYYY_MM_DD_T_HH_MM_SS_SSS_Z = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    DateTimeFormatter YYYY_MM_DD_T_HH_MM_SS_SSS_Z_FORMATTER = DateTimeFormatter.ofPattern(YYYY_MM_DD_T_HH_MM_SS_SSS_Z);

    /**
     * yyyy-MM-dd'T'HH:mm:ss.SSSxxx 比如：2020-05-23T17:06:30.272+08:00 2020-05-23T09:06:30.272+00:00
     */
    String YYYY_MM_DD_T_HH_MM_SS_SSS_XXX = "yyyy-MM-dd'T'HH:mm:ss.SSSxxx";
    DateTimeFormatter YYYY_MM_DD_T_HH_MM_SS_SSS_XXX_FORMATTER = DateTimeFormatter.ofPattern(YYYY_MM_DD_T_HH_MM_SS_SSS_XXX);

    /**
     * yyyy-MM-dd'T'HH:mm:ss.SSSXXX 比如：2020-05-23T17:06:30.272+08:00 2020-05-23T09:06:30.272Z 0时区时末尾 为Z
     */
    String YYYY_MM_DD_T_HH_MM_SS_SSS_XXX_Z = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
    DateTimeFormatter YYYY_MM_DD_T_HH_MM_SS_SSS_XXX_Z_FORMATTER = DateTimeFormatter.ofPattern(YYYY_MM_DD_T_HH_MM_SS_SSS_XXX_Z);

    /**
     * yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ 比如：2020-05-23T17:06:30.272150+0800 2020-05-23T09:06:30.272150+0000
     */
    String YYYY_MM_DD_T_HH_MM_SS_SSSSSS_Z = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ";
    DateTimeFormatter YYYY_MM_DD_T_HH_MM_SS_SSSSSS_Z_FORMATTER = DateTimeFormatter.ofPattern(YYYY_MM_DD_T_HH_MM_SS_SSSSSS_Z);

    /**
     * yyyy-MM-dd'T'HH:mm:ss.SSSSSSxxx 比如：2020-05-23T17:06:30.272150+08:00 2020-05-23T09:06:30.272150+00:00
     */
    String YYYY_MM_DD_T_HH_MM_SS_SSSSSS_XXX = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSxxx";
    DateTimeFormatter YYYY_MM_DD_T_HH_MM_SS_SSSSSS_XXX_FORMATTER = DateTimeFormatter.ofPattern(YYYY_MM_DD_T_HH_MM_SS_SSSSSS_XXX);

    /**
     * yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX 比如：2020-05-23T17:06:30.272150+08:00 2020-05-23T09:06:30.272150Z 0时区时末尾 为Z
     */
    String YYYY_MM_DD_T_HH_MM_SS_SSSSSS_XXX_Z = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX";
    DateTimeFormatter YYYY_MM_DD_T_HH_MM_SS_SSSSSS_XXX_Z_FORMATTER = DateTimeFormatter.ofPattern(YYYY_MM_DD_T_HH_MM_SS_SSSSSS_XXX_Z);

    /**
     * yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSSZ 比如：2020-05-23T17:06:30.272150620+0800 2020-05-23T09:06:30.272150620+0000
     */
    String YYYY_MM_DD_T_HH_MM_SS_SSSSSSSSS_Z = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSSZ";
    DateTimeFormatter YYYY_MM_DD_T_HH_MM_SS_SSSSSSSSS_Z_FORMATTER = DateTimeFormatter.ofPattern(YYYY_MM_DD_T_HH_MM_SS_SSSSSSSSS_Z);

    /**
     * yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSSxxx 比如：2020-05-23T17:06:30.272150620+08:00 2020-05-23T09:06:30.272150620+00:00
     */
    String YYYY_MM_DD_T_HH_MM_SS_SSSSSSSSS_XXX = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSSxxx";
    DateTimeFormatter YYYY_MM_DD_T_HH_MM_SS_SSSSSSSSS_XXX_FORMATTER = DateTimeFormatter.ofPattern(YYYY_MM_DD_T_HH_MM_SS_SSSSSSSSS_XXX);

    /**
     * yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSSXXX 比如：2020-05-23T17:06:30.272150620+08:00 2020-05-23T09:06:30.272150620Z 0时区时末尾 为Z
     */
    String YYYY_MM_DD_T_HH_MM_SS_SSSSSSSSS_XXX_Z = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSSXXX";
    DateTimeFormatter YYYY_MM_DD_T_HH_MM_SS_SSSSSSSSS_XXX_Z_FORMATTER = DateTimeFormatter.ofPattern(YYYY_MM_DD_T_HH_MM_SS_SSSSSSSSS_XXX_Z);


    // ==================================其他格式 Pattern==================================

    /**
     * Date 默认格式 EEE MMM dd HH:mm:ss zzz yyyy 比如：  Sat May 23 17:06:30 CST 2020
     */
    String EEE_MMM_DD_HH_MM_SS_ZZZ_YYYY = "EEE MMM dd HH:mm:ss zzz yyyy";
    DateTimeFormatter EEE_MMM_DD_HH_MM_SS_ZZZ_YYYY_FORMATTER = DateTimeFormatter.ofPattern(EEE_MMM_DD_HH_MM_SS_ZZZ_YYYY);

    /**
     * EEE, dd MMM yyyy HH:mm:ss zzz 比如：  星期三, 24 八月 2022 15:39:15 CST
     */
    String EEE_DD_MMM_YYYY_HH_MM_SS_ZZZ = "EEE, dd MMM yyyy HH:mm:ss zzz";
    DateTimeFormatter EEE_DD_MMM_YYYY_HH_MM_SS_ZZZ_FORMATTER = DateTimeFormatter.ofPattern(EEE_DD_MMM_YYYY_HH_MM_SS_ZZZ);

}
