package com.base.auth.service;

import com.base.auth.model.Chapter;
import com.base.auth.model.Course;
import com.base.auth.model.Lesson;
import com.base.auth.repository.ChapterRepository;
import com.base.auth.repository.CommentRepository;
import com.base.auth.repository.CorrectAnswerRepository;
import com.base.auth.repository.CourseEnrollmentRepository;
import com.base.auth.repository.CourseRepository;
import com.base.auth.repository.FeedbackRepository;
import com.base.auth.repository.LessonProgressRepository;
import com.base.auth.repository.LessonQuestionRepository;
import com.base.auth.repository.LessonRepository;
import com.base.auth.repository.QuestionQuizHistoryRepository;
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
  LessonProgressRepository lessonProgressRepository;

  @Autowired
  CorrectAnswerRepository correctAnswerRepository;

  @Autowired
  QuestionQuizHistoryRepository questionQuizHistoryRepository;

  @Autowired
  CourseEnrollmentRepository courseEnrollmentRepository;

  @Autowired
  FeedbackRepository feedbackRepository;

  @Autowired
  CommentRepository commentRepository;

  @Autowired
  UserBaseApiService userBaseApiService;

  @Autowired
  LessonService lessonService;

  public void deleteCourse(Course course) {
    List<Chapter> chapters = chapterRepository.findAllByCourseId(course.getId());
    for (Chapter chapter : chapters) {
      deleteChapterWithLinkedList(chapter);
    }
    deleteCourseFiles(course);
    feedbackRepository.deleteAllByCourseId(course.getId());
    courseEnrollmentRepository.deleteAllByCourseId(course.getId());
    courseRepository.delete(course);
  }

  private void deleteChapterWithLinkedList(Chapter chapter) {
    Lesson current = lessonRepository.findFirstByChapterIdAndPreviousIsNull(chapter.getId()).orElse(null);

    while (current != null) {
      Lesson next = current.getNext();

      // rollback điểm
      lessonService.rollbackStudentScoreWhenDeleteLesson(current);

      // delete correct answer
      correctAnswerRepository.deleteAllByLessonProgressLessonId(current.getId());

      // delete quiz history
      questionQuizHistoryRepository.deleteAllByLessonProgressLessonId(current.getId());

      // delete lesson progress
      lessonProgressRepository.deleteAllByLessonId(current.getId());

      // delete question
      lessonQuestionRepository.deleteAllByLessonId(current.getId());

      // delete comment
      commentRepository.deleteAllByLessonId(current.getId());

      // unlink
      if (next != null) {
        next.setPrevious(null);
        lessonRepository.save(next);
      }

      current.setPrevious(null);
      current.setNext(null);

      // delete files
      deleteLessonFiles(current);

      // delete lesson
      lessonRepository.delete(current);

      current = next;
    }

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
