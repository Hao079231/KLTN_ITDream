package com.base.auth.controller;

import com.base.auth.constant.ITDreamConstant;
import com.base.auth.dto.ApiMessageDto;
import com.base.auth.dto.ErrorCode;
import com.base.auth.dto.ResponseListDto;
import com.base.auth.dto.notification.NotificationDisplayDto;
import com.base.auth.dto.notification.NotificationDto;
import com.base.auth.exception.NotFoundException;
import com.base.auth.exception.UnauthorizationException;
import com.base.auth.form.notification.UpdateReadFlagNotificationForm;
import com.base.auth.mapper.NotificationMapper;
import com.base.auth.model.Notification;
import com.base.auth.model.criteria.NotificationCriteria;
import com.base.auth.repository.NotificationRepository;
import java.util.List;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/notification")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class NotificationController extends ABasicController{
  @Autowired
  NotificationRepository notificationRepository;

  @Autowired
  NotificationMapper notificationMapper;

  @GetMapping(value = "/student_list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('NO_ST_L')")
  public ApiMessageDto<ResponseListDto<List<NotificationDisplayDto>>> listByStudent(NotificationCriteria criteria, Pageable pageable){
    ApiMessageDto<ResponseListDto<List<NotificationDisplayDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<NotificationDisplayDto>> responseListDto = new ResponseListDto<>();
    Page<Notification> notifications = notificationRepository.findAll(criteria.getSpecification(), pageable);
    responseListDto.setContent(notificationMapper.fromEntityToNotificationDisplayDtoList(notifications.getContent()));
    responseListDto.setTotalElements(notifications.getTotalElements());
    responseListDto.setTotalPages(notifications.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Lấy danh sách thông báo thành công");
    return apiMessageDto;
  }

  @PutMapping(value = "/update_read_flag", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('NO_ST_U')")
  public ApiMessageDto<NotificationDto> update(@Valid @RequestBody UpdateReadFlagNotificationForm form, BindingResult bindingResult){
    ApiMessageDto<NotificationDto> apiMessageDto = new ApiMessageDto<>();
    Notification notification = notificationRepository.findById(form.getId())
        .orElseThrow(() -> new NotFoundException("Không tìm thấy thông báo", ErrorCode.NOTIFICATION_ERROR_NOT_FOUND));
    notification.setReadFlag(true);
    NotificationDto notificationDto = notificationMapper.fromEntityToNotificationDto(notification);
    notificationRepository.save(notification);
    apiMessageDto.setData(notificationDto);
    apiMessageDto.setMessage("Cập nhật trạng thái đọc thành công");
    return apiMessageDto;
  }

  @DeleteMapping(value = "/clear_all", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('NO_ST_CA')")
  public ApiMessageDto<String> clearAll(){
    if (!isStudent()){
      throw new UnauthorizationException("Người dùng không phải là học viên");
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    notificationRepository.deleteAllByReceiverIdAndReadFlag(getCurrentUser(), ITDreamConstant.READ);
    apiMessageDto.setMessage("Xóa tất cả thông báo đã đọc thành công");
    return apiMessageDto;
  }
}