package com.base.auth.utils;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class ConvertUtils {

    private ConvertUtils(){

    }

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

    public static String generateUsername(String name){
        try {
            String username = name.trim();
            username = Normalizer.normalize(username, Form.NFD);
            username = username.replaceAll("\\p{M}", "");
            username = username.replace("đ", "d")
                .replace("Đ", "D");
            username = username.replaceAll("\\s+", "");
            username = username.replaceAll("[^a-z0-9]", "");
            if (StringUtils.isEmpty(username)){
                return null;
            }
            return username;
        } catch (Exception e) {
            log.error("=====> Error convert username, {}", e.getMessage());
            return null;
        }
    }
}
