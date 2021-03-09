package pl.afyaan.encryptor;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author AFYaan
 * @created 09.03.2021
 * @project JJL-Encryptor
 */

public class Util {
    public static boolean isNull(Object... objects){
        return Arrays.stream(objects).anyMatch(Objects::isNull);
    }
}
