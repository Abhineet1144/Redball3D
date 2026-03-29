package engine.utils;

import java.io.File;

public class CommonUtils {
    public static String mergePath(String... dirs) {
        StringBuilder mergedPath = new StringBuilder();
        for (String dir : dirs) {
            mergedPath.append(dir).append(File.separatorChar);
        }
        return mergedPath.substring(0, mergedPath.length() - 1);
    }
}
