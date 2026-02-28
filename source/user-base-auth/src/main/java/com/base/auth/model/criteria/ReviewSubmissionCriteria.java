package com.base.auth.model.criteria;

import com.base.auth.model.Account;
import com.base.auth.model.Chapter;
import com.base.auth.model.CorrectAnswer;
import com.base.auth.model.Course;
import com.base.auth.model.Lesson;
import com.base.auth.model.LessonProgress;
import com.base.auth.model.ReviewSubmission;
import com.base.auth.model.Student;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

@Data
public class ReviewSubmissionCriteria {
  private Long courseId;
  private String studentUsername;
  private Long studentId;

  public Specification<ReviewSubmission> getSpecification() {
    return new Specification<ReviewSubmission>() {
      private static final long serialVersionUID = 1L;

      @Override
      public Predicate toPredicate(Root<ReviewSubmission> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        Join<ReviewSubmission, CorrectAnswer> correctAnswerJoin = root.join("correctAnswer", JoinType.INNER);
        Join<CorrectAnswer, LessonProgress> lessonProgressJoin = correctAnswerJoin.join("lessonProgress", JoinType.INNER);
        Join<LessonProgress, Lesson> lessonJoin = lessonProgressJoin.join("lesson", JoinType.INNER);
        Join<Lesson, Chapter> chapterJoin = lessonJoin.join("chapter", JoinType.INNER);
        Join<Chapter, Course> courseJoin = chapterJoin.join("course", JoinType.INNER);

        if (getCourseId() != null) {
          predicates.add(cb.equal(courseJoin.get("id"), getCourseId()));
        }

        if (StringUtils.isNotEmpty(getStudentUsername())) {
          Join<ReviewSubmission, Student> studentJoin = root.join("student", JoinType.INNER);
          Join<Student, Account> accountJoin = studentJoin.join("account", JoinType.INNER);
          predicates.add(cb.like(cb.lower(accountJoin.get("username")), "%" + getStudentUsername().toLowerCase().trim() + "%"));
        }

        if (getStudentId() != null){
          Join<ReviewSubmission, Student> studentJoin = root.join("student", JoinType.INNER);
          Join<Student, Account> accountJoin = studentJoin.join("account", JoinType.INNER);
          predicates.add(cb.equal(accountJoin.get("id"), getStudentId()));
        }

        query.distinct(true);
        return cb.and(predicates.toArray(new Predicate[predicates.size()]));
      }
    };
  }
}
