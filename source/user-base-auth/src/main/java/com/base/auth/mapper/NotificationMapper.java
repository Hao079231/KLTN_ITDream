package com.base.auth.mapper;

import com.base.auth.dto.notification.NotificationDisplayDto;
import com.base.auth.dto.notification.NotificationDto;
import com.base.auth.model.Notification;
import java.util.List;
import org.mapstruct.BeanMapping;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface NotificationMapper {
  @Mapping(source = "id", target = "id")
  @Mapping(source = "title", target = "title")
  @Mapping(source = "readFlag", target = "readFlag")
  @BeanMapping(ignoreByDefault = true)
  @Named("fromEntityToNotificationDisplayDto")
  NotificationDisplayDto fromEntityToNotificationDisplayDto(Notification notification);

  @IterableMapping(elementTargetType = NotificationDisplayDto.class, qualifiedByName = "fromEntityToNotificationDisplayDto")
  List<NotificationDisplayDto> fromEntityToNotificationDisplayDtoList(List<Notification> notifications);


  @Mapping(source = "id", target = "id")
  @Mapping(source = "title", target = "title")
  @Mapping(source = "message", target = "message")
  @Mapping(source = "readFlag", target = "readFlag")
  @Mapping(source = "refId", target = "refId")
  @Mapping(source = "createdDate", target = "createdDate")
  @BeanMapping(ignoreByDefault = true)
  @Named("fromEntityToNotificationDto")
  NotificationDto fromEntityToNotificationDto(Notification notification);
}
