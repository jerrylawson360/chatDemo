package util;

import java.nio.charset.Charset;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class TestUtil {

    public static Random getRandom() {
        return ThreadLocalRandom.current();
    }

    public static String randomString() {
        final byte[] array = new byte[Math.max(8, getRandom().nextInt(20))];
        int range = 'z' - '0';

        for (int i = 0; i < array.length; i++) {
            array[i] = '0';
            array[i] += (byte) getRandom().nextInt(range);
        }
        final String value = new String(array, Charset.forName("UTF-8"));
        System.out.println("random string="+value);
        return value;
    }

    public static Long randomLong() {
        return new Long(getRandom().nextLong());
    }
}
