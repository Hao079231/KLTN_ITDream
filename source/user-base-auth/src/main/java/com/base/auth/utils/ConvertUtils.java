package com.base.auth.utils;

import java.security.SecureRandom;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class ConvertUtils {

    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private ConvertUtils(){}

    public static Long convertStringToLong(String input){
        try {
            return Long.parseLong(input);
        }catch (Exception e){
            return  Long.valueOf(0);
        }
    }

    public static int convertToCent(double b){
        int i=(int)(b);
        double k = b-(double)i;
        if(k>0.5 && k<1){
            i+=1;
        }
        return i;
    }

    public static String generateUsername(String name) {
        try {
            String username = name.trim();
            username = Normalizer.normalize(username, Form.NFD);
            username = username.replaceAll("\\p{M}", "")
                                .replaceAll("\\s+", "")
                                .replaceAll("[^a-z0-9]", "");
            username = username.replace("đ", "d")
                .replace("Đ", "D");
            username = username.toLowerCase(Locale.ROOT);
            if (StringUtils.isBlank(username)) {
                return null;
            }

            return username + generateRandomString(6);

        } catch (Exception e) {
            log.error("=====> Error convert username", e);
            return null;
        }
    }

    private static String generateRandomString(int length) {
        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            builder.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return builder.toString();
    }
}
