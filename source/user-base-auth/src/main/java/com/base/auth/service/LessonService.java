package com.base.auth.service;

import com.base.auth.constant.ITDreamConstant;
import com.base.auth.exception.NotFoundException;
import com.base.auth.model.Chapter;
import com.base.auth.model.CorrectAnswer;
import com.base.auth.model.Lesson;
import com.base.auth.model.LessonProgress;
import com.base.auth.model.Student;
import com.base.auth.repository.ChapterRepository;
import com.base.auth.repository.CorrectAnswerRepository;
import com.base.auth.repository.LessonProgressRepository;
import com.base.auth.repository.LessonRepository;
import com.base.auth.repository.QuestionQuizHistoryRepository;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LessonService {
  @Autowired
  LessonRepository lessonRepository;

  @Autowired
  ChapterRepository chapterRepository;

  @Autowired
  LessonProgressRepository lessonProgressRepository;

  @Autowired
  CorrectAnswerRepository correctAnswerRepository;

  @Autowired
  UserBaseApiService userBaseApiService;

  public Page<Lesson> getLessonsByChapterOrdered(Long chapterId, Pageable pageable) {

    List<Lesson> orderedLessons = new ArrayList<>();
    Lesson current = lessonRepository.findFirstByChapterIdAndPreviousIsNull(chapterId).orElse(null);

    while (current != null) {
      orderedLessons.add(current);
      current = current.getNext();
    }

    int start = (int) pageable.getOffset();
    int end = Math.min(start + pageable.getPageSize(), orderedLessons.size());

    List<Lesson> pageContent = start > orderedLessons.size() ? List.of() : orderedLessons.subList(start, end);

    return new PageImpl<>(pageContent, pageable, orderedLessons.size());
  }

  public Page<Lesson> getLessonsByStudent(Long chapterId, Pageable pageable){
    Chapter chapter = chapterRepository.findById(chapterId).orElseThrow(() -> new NotFoundException("Chapter not found"));

    if (chapter.getCourse().getStatus() != ITDreamConstant.COURSE_STATUS_ACTIVE) {
      return Page.empty(pageable);
    }

    return getLessonsByChapterOrdered(chapterId, pageable);
  }

  @Transactional
  public void rollbackStudentScoreWhenDeleteLesson(Lesson lesson){
    List<LessonProgress> lessonProgresses = lessonProgressRepository.findAllByLessonId(lesson.getId());
    List<CorrectAnswer> correctAnswers = correctAnswerRepository.findAllByLessonQuestionLessonId(lesson.getId());
    Map<Student, Long> questionCountByStudent = correctAnswers.stream()
            .collect(Collectors.groupingBy(
                ca -> ca.getLessonProgress().getCourseEnrollment().getStudent(),
                Collectors.counting()));
    for (LessonProgress lp : lessonProgresses) {
      Student student = lp.getCourseEnrollment().getStudent();
      long completedQuestion = questionCountByStudent.getOrDefault(student, 0L);
      long scoreToMinus = completedQuestion * ITDreamConstant.SCORE_COMPLETE_QUESTION + ITDreamConstant.SCORE_COMPLETE_LESSON;
      student.setScore(Math.max(0, student.getScore() - scoreToMinus));
    }
  }

  public void deleteLessonFiles(Lesson lesson){
    if (StringUtils.isNotBlank(lesson.getImagePath()) &&
        lesson.getImagePath().toLowerCase().startsWith(File.separator + "image")){
      userBaseApiService.deleteByFilePath(lesson.getImagePath());
    }

    if (StringUtils.isNotBlank(lesson.getFilePath()) &&
        lesson.getFilePath().toLowerCase().startsWith(File.separator + "document")){
      userBaseApiService.deleteByFilePath(lesson.getFilePath());
    }

    if (StringUtils.isNotBlank(lesson.getVideoPath()) &&
        lesson.getVideoPath().toLowerCase().startsWith(File.separator + "video")){
      userBaseApiService.deleteByFilePath(lesson.getVideoPath());
    }
  }
}
