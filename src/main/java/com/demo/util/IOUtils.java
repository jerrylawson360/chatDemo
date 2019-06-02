package com.demo.util;

import com.demo.config.AppConfig;

import java.io.Closeable;
import java.util.Optional;

public class IOUtils {
    public static void close(final Closeable os) {
        if (os != null) {
            try {
                os.close();
            } catch (Exception e) {

            }
        }
    }

    public static String formatObject(final Object object) {
        return Optional.ofNullable(object)
            .map(obj -> {
                try {
                    return AppConfig.getInstance().getObjectMapper().writeValueAsString(obj);
                } catch (Exception e2) {
                    return null;
                }
            })
            .orElse("");
    }

}
