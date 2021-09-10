package com.chm.converter.core.reflect;

/**
 * @author caihongming
 * @version v1.0
 * @since 2021-09-07
 **/
public final class JavaVersion {

    private static final int majorJavaVersion = determineMajorJavaVersion();

    private static int determineMajorJavaVersion() {
        String javaVersion = System.getProperty("java.version");
        return getMajorJavaVersion(javaVersion);
    }

    /**
     * 仅用于测试可见
     *
     * @param javaVersion
     * @return
     */
    static int getMajorJavaVersion(String javaVersion) {
        int version = parseDotted(javaVersion);
        if (version == -1) {
            version = extractBeginningInt(javaVersion);
        }
        if (version == -1) {
            // Choose minimum supported JDK version as default
            return 8;
        }
        return version;
    }

    /**
     * 解析旧的 1.8 样式和更新的 9.0.4 样式
     * @param javaVersion
     * @return
     */
    private static int parseDotted(String javaVersion) {
        try {
            String[] parts = javaVersion.split("[._]");
            int firstVer = Integer.parseInt(parts[0]);
            if (firstVer == 1 && parts.length > 1) {
                return Integer.parseInt(parts[1]);
            } else {
                return firstVer;
            }
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static int extractBeginningInt(String javaVersion) {
        try {
            StringBuilder num = new StringBuilder();
            for (int i = 0; i < javaVersion.length(); ++i) {
                char c = javaVersion.charAt(i);
                if (Character.isDigit(c)) {
                    num.append(c);
                } else {
                    break;
                }
            }
            return Integer.parseInt(num.toString());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * @return 主要的 Java 版本，即“8”代表 Java 1.8，“9”代表 Java 9 等。
     */
    public static int getMajorJavaVersion() {
        return majorJavaVersion;
    }

    /**
     * @return {@code true} 应用程序在 Java 9 或更高版本上运行; {@code false} 否则相反
     */
    public static boolean isJava9OrLater() {
        return majorJavaVersion >= 9;
    }

    private JavaVersion() {
    }
}