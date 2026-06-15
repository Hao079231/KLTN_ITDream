package com.base.auth.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class JsonUitls {
  public static ObjectMapper getMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    return objectMapper;
  }

  private JsonUitls(){}

  public static String convertObjectToString(Object value){
    try{
      return getMapper().writeValueAsString(value);
    } catch (JsonProcessingException ex){
      log.error("====> Error convert object to string: {}", ex.getMessage());
      return null;
    }
  }

  public static <T> List<T> convertJsonStringToClass(String value, Class<T> clazz){
    try {
      if (StringUtils.isBlank(value)){
        return Collections.emptyList();
      }
      return getMapper().readValue(
          value,
          getMapper()
              .getTypeFactory()
              .constructCollectionType(List.class, clazz)
      );
    } catch (Exception ex) {
      log.error("====> Error convert string to object: {}", ex.getMessage());
      return null;
    }
  }
}
