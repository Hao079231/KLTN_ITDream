package com.base.auth.service;

import com.base.auth.constant.ITDreamConstant;
import com.base.auth.exception.NotFoundException;
import com.base.auth.model.Chapter;
import com.base.auth.model.Lesson;
import com.base.auth.repository.ChapterRepository;
import com.base.auth.repository.LessonRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
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
}
