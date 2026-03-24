package com.base.auth.utils;

import org.apache.commons.lang3.StringUtils;

public class FormatUtils {
  public static String convertHotlineToStandardizedPhone(String hotline){
    if (StringUtils.isBlank(hotline)){
      return hotline;
    }

    String hotlineCleand = hotline.replaceAll("[^0-9]", "");
    if (hotline.trim().startsWith("+84") || hotline.trim().startsWith("84") && hotlineCleand.length() > 9){
      hotlineCleand = "0" + hotlineCleand.substring(2);
    }
    return hotlineCleand;
  }
}
