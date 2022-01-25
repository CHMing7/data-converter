package com.chm.converter.core.exception;


import com.chm.converter.core.utils.StringUtil;

/**
 * IO运行时异常，常用于对IOException的包装
 * @author caihongming
 * @version v1.0
 * @since 2022-01-25
 **/
public class IORuntimeException extends RuntimeException {

    public IORuntimeException(Throwable e) {
        super(getMessage(e), e);
    }

    public IORuntimeException(String message) {
        super(message);
    }

    public IORuntimeException(String messageTemplate, Object... params) {
        super(StringUtil.format(messageTemplate, params));
    }

    public IORuntimeException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public IORuntimeException(Throwable throwable, String messageTemplate, Object... params) {
        super(StringUtil.format(messageTemplate, params), throwable);
    }

    /**
     * 导致这个异常的异常是否是指定类型的异常
     *
     * @param clazz 异常类
     * @return 是否为指定类型异常
     */
    public boolean causeInstanceOf(Class<? extends Throwable> clazz) {
        final Throwable cause = this.getCause();
        return null != clazz && clazz.isInstance(cause);
    }

    /**
     * 获得完整消息，包括异常名，消息格式为：{SimpleClassName}: {ThrowableMessage}
     *
     * @param e 异常
     * @return 完整消息
     */
    public static String getMessage(Throwable e) {
        if (null == e) {
            return StringUtil.NULL;
        }
        return StringUtil.format("{}: {}", e.getClass().getSimpleName(), e.getMessage());
    }
}
