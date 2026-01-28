package com.base.auth.service;

import com.base.auth.model.Chapter;
import com.base.auth.model.Course;
import com.base.auth.model.Lesson;
import com.base.auth.repository.ChapterRepository;
import com.base.auth.repository.CourseRepository;
import com.base.auth.repository.LessonQuestionRepository;
import com.base.auth.repository.LessonRepository;
import java.io.File;
import java.util.List;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Transactional
public class CourseService {
  @Autowired
  CourseRepository courseRepository;

  @Autowired
  ChapterRepository chapterRepository;

  @Autowired
  LessonRepository lessonRepository;

  @Autowired
  LessonQuestionRepository lessonQuestionRepository;

  @Autowired
  UserBaseApiService userBaseApiService;

  public void deleteCourse(Course course) {
    List<Chapter> chapters = chapterRepository.findAllByCourseId(course.getId());
    for (Chapter chapter : chapters) {
      deleteChapterInCourse(chapter);
    }
    deleteCourseFiles(course);
    courseRepository.delete(course);
  }

  private void deleteChapterInCourse(Chapter chapter) {
    List<Lesson> lessons = lessonRepository.findAllByChapterId(chapter.getId());
    for (Lesson lesson : lessons){
      lessonQuestionRepository.deleteAllByLessonId(lesson.getId());
      deleteLessonFiles(lesson);
      lesson.setPrevious(null);
      lesson.setNext(null);
      lessonRepository.save(lesson);
    }
    lessonRepository.deleteAll(lessons);
    chapterRepository.delete(chapter);
  }

  private void deleteLessonFiles(Lesson lesson) {
    if (StringUtils.isNotBlank(lesson.getImagePath())
        && lesson.getImagePath().startsWith(File.separator + "image")) {
      userBaseApiService.deleteByFilePath(lesson.getImagePath());
    }

    if (StringUtils.isNotBlank(lesson.getFilePath())
        && lesson.getFilePath().startsWith(File.separator + "document")) {
      userBaseApiService.deleteByFilePath(lesson.getFilePath());
    }

    if (StringUtils.isNotBlank(lesson.getVideoPath())
        && lesson.getVideoPath().startsWith(File.separator + "video")) {
      userBaseApiService.deleteByFilePath(lesson.getVideoPath());
    }
  }


  private void deleteCourseFiles(Course course) {
    if (StringUtils.isNotBlank(course.getThumbnail())
        && course.getThumbnail().startsWith(File.separator + "image")) {
      userBaseApiService.deleteByFilePath(course.getThumbnail());
    }

    if (StringUtils.isNotBlank(course.getVideoPath())
        && course.getVideoPath().startsWith(File.separator + "video")) {
      userBaseApiService.deleteByFilePath(course.getVideoPath());
    }
  }
}
