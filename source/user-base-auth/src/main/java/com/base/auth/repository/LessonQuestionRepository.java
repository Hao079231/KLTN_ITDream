package com.base.auth.repository;

import com.base.auth.model.LessonQuestion;
import java.util.Optional;
import javax.transaction.Transactional;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LessonQuestionRepository extends JpaRepository<LessonQuestion, Long>,
    JpaSpecificationExecutor<LessonQuestion> {

  Boolean existsByQuestionAndLessonId(String question, Long lessonId);

  Boolean existsByQuestionAndOptionsAndLessonId(String question, String options, Long lessonId);

  @Modifying
  @Transactional
  @Query("delete from LessonQuestion lq where lq.lesson.id = :lessonId")
  void deleteAllByLessonId(@Param("lessonId") Long lessonId);

  Integer countByLessonId(Long lessonId);
}
