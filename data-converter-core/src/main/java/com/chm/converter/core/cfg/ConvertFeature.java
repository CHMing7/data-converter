package com.chm.converter.core.cfg;

/**
 * 转换特性配置
 *
 * @author caihongming
 * @version v1.0
 * @date 2022-01-25
 **/
public enum ConvertFeature {

    /**
     * 转换枚举为字符串
     */
    ENUMS_USING_TO_STRING(true),

    /**
     * 转换日期为时间戳
     */
    DATES_AS_TIMESTAMPS(false);

    /**
     * 默认状态
     */
    private final boolean defaultState;

    /**
     * 位掩码
     */
    private final int mask;

    ConvertFeature(boolean defaultState) {
        this.defaultState = defaultState;
        this.mask = (1 << ordinal());
    }

    /**
     * 用于检查此功能是否默认启用的访问器
     *
     * @return
     */
    public boolean enabledByDefault() {
        return this.defaultState;
    }

    /**
     * 返回此功能的位掩码
     *
     * @return
     */
    public int getMask() {
        return this.mask;
    }

    /**
     * 在给定的位掩码中检查特性是否启用的简易方法
     *
     * @param flags
     * @return
     */
    public boolean enabledIn(int flags) {
        return (flags & this.mask) != 0;
    }

}

