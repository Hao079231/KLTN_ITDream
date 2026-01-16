package com.base.auth.service;

import com.base.auth.constant.ITDreamConstant;
import com.base.auth.form.BaseMsgForm;
import com.base.auth.form.ProcessVideoSuccessForm;
import com.base.auth.form.RequestProcessVideoMessageForm;
import com.base.auth.model.Course;
import com.base.auth.model.Lesson;
import com.base.auth.repository.CourseRepository;
import com.base.auth.repository.LessonRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ProcessVideoService {
  @Autowired
  ObjectMapper objectMapper;

  @Autowired
  RabbitMQService rabbitService;

  @Autowired
  CourseRepository courseRepository;

  @Autowired
  LessonRepository lessonRepository;

  @Value("${rabbitmq.video.app}")
  String videoApp;

  @Value("${rabbitmq.process.video.queue}")
  String processVideoQueue;

  public void sendProcessVideoMessage(RequestProcessVideoMessageForm data) {
    rabbitService.handleSendMsg(data, ITDreamConstant.BACKEND_PROCESS_VIDEO_CMD, processVideoQueue);
  }

  @RabbitListener(queues = "${rabbitmq.media.completed.process.video.queue}")
  public void receiveMessage(String message){
    log.info("Received message: " + message);
    BaseMsgForm form;
    try {
      form = objectMapper.readValue(message,BaseMsgForm.class);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    if(form != null) {
      if(!form.getApp().equals(videoApp)) {
        log.error("===========> Invalid app: " + form.getApp());
        return;
      }
      if(form.getCmd().equals(ITDreamConstant.MEDIA_COMPLETED_PROCESS_VIDEO_CMD)){
        ProcessVideoSuccessForm data = objectMapper.convertValue(form.getData(), ProcessVideoSuccessForm.class);
        if (Objects.equals(data.getKind(), ITDreamConstant.KIND_LESSON)){
          // update lesson when received data success
          updateLessonProcessed(data);
        } else {
          updateCourseProcessed(data);
        }
      }
      else {
        log.error("===========> Invalid cmd: " + form.getCmd());
      }
    }
  }

  void updateLessonProcessed(ProcessVideoSuccessForm processVideoSuccessForm){
    log.info("Update state lesson processed.............");
    Lesson lesson = lessonRepository.findById(processVideoSuccessForm.getId()).orElse(null);
    if (lesson != null){
      if (!processVideoSuccessForm.getIsSuccess()){
        lesson.setVideoState(ITDreamConstant.STATE_LESSON_FAIL);
      } else {
        lesson.setVideoPath(processVideoSuccessForm.getContentPath());
        lesson.setVideoState(ITDreamConstant.STATE_LESSON_DONE);
      }
      lessonRepository.save(lesson);
    }
  }

  void updateCourseProcessed(ProcessVideoSuccessForm processVideoSuccessForm){
    log.info("Update state course processed.............");
    Course course = courseRepository.findById(processVideoSuccessForm.getId()).orElse(null);
    if (course != null){
      if (!processVideoSuccessForm.getIsSuccess()){
        course.setVideoState(ITDreamConstant.STATE_COURSE_FAIL);
      } else {
        course.setVideoPath(processVideoSuccessForm.getContentPath());
        course.setVideoState(ITDreamConstant.STATE_COURSE_DONE);
      }
      courseRepository.save(course);
    }
  }
}
