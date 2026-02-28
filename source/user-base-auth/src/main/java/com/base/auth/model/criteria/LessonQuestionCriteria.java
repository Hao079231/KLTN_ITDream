package com.base.auth.model.criteria;

import com.base.auth.model.Chapter;
import com.base.auth.model.Course;
import com.base.auth.model.Lesson;
import com.base.auth.model.LessonQuestion;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

@Data
public class LessonQuestionCriteria {
  @NotNull(message = "lessonId required")
  private Long lessonId;
  private Integer status;

  public Specification<LessonQuestion> getSpecification() {
    return new Specification<LessonQuestion>() {
      private static final long serialVersionUID = 1L;

      @Override
      public Predicate toPredicate(Root<LessonQuestion> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        Join<LessonQuestion, Lesson> lessonJoin = root.join("lesson");

        predicates.add(cb.equal(lessonJoin.get("id"), getLessonId()));

        if (getStatus() != null){
          Join<Lesson, Chapter> chapterJoin = lessonJoin.join("chapter");
          Join<Chapter, Course> courseJoin = chapterJoin.join("course");
          predicates.add(cb.equal(courseJoin.get("status"), getStatus()));
        }
        return cb.and(predicates.toArray(new Predicate[predicates.size()]));
      }
    };
  }
}
