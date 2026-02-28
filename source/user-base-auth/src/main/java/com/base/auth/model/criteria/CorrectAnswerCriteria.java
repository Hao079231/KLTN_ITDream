package com.base.auth.model.criteria;

import com.base.auth.model.Account;
import com.base.auth.model.CorrectAnswer;
import com.base.auth.model.Course;
import com.base.auth.model.CourseEnrollment;
import com.base.auth.model.Lesson;
import com.base.auth.model.LessonProgress;
import com.base.auth.model.LessonQuestion;
import com.base.auth.model.Student;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

@Data
public class CorrectAnswerCriteria {
  private Long courseId;
  private String studentUsername;

  public Specification<CorrectAnswer> getSpecification() {
    return new Specification<CorrectAnswer>() {
      private static final long serialVersionUID = 1L;

      @Override
      public Predicate toPredicate(Root<CorrectAnswer> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        // Chỉ fetch khi không phải count query
        if (query.getResultType() != Long.class) {
          // fetch lessonQuestion
          root.fetch("lessonQuestion", JoinType.LEFT);

          // fetch lessonProgress → lesson
          Fetch<CorrectAnswer, LessonProgress> lessonProgressFetch = root.fetch("lessonProgress", JoinType.LEFT);
          lessonProgressFetch.fetch("lesson", JoinType.LEFT);
        }

        List<Predicate> predicates = new ArrayList<>();
        Join<CorrectAnswer, LessonProgress> lessonProgressJoin = root.join("lessonProgress", JoinType.INNER);
        Join<LessonProgress, CourseEnrollment> enrollmentJoin = lessonProgressJoin.join("courseEnrollment", JoinType.INNER);
        Join<CourseEnrollment, Course> courseJoin = enrollmentJoin.join("course", JoinType.INNER);
        Join<CourseEnrollment, Student> studentJoin = enrollmentJoin.join("student", JoinType.INNER);
        Join<Student, Account> accountJoin = studentJoin.join("account", JoinType.INNER);

        if (courseId != null) {
          predicates.add(cb.equal(courseJoin.get("id"), courseId));
        }

        if (StringUtils.isNotEmpty(getStudentUsername())) {
          predicates.add(cb.like(cb.lower(accountJoin.get("username")), "%" + getStudentUsername().toLowerCase().trim() + "%"));
        }
        query.distinct(true);

        return cb.and(predicates.toArray(new Predicate[0]));
      }
    };
  }
}
