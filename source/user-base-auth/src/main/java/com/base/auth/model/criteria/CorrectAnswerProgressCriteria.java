package com.base.auth.model.criteria;

import com.base.auth.model.CorrectAnswer;
import com.base.auth.model.LessonProgress;
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
public class CorrectAnswerProgressCriteria {
  @NotNull(message = "studentSubTaskProgressId required")
  private Long studentSubTaskProgressId;
  @NotNull(message = "taskId required")
  private Long taskId;
  private Long studentId;
  private Long taskQuestionId;
  private Boolean isCorrect;

  public Specification<CorrectAnswer> getSpecification() {
    return new Specification<CorrectAnswer>() {
      private static final long serialVersionUID = 1L;

      @Override
      public Predicate toPredicate(Root<CorrectAnswer> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        Join<CorrectAnswer, LessonProgress> progressJoin = root.join("studentSubTaskProgress");
        predicates.add(cb.equal(progressJoin.get("id"), getStudentSubTaskProgressId()));
        predicates.add(cb.equal(progressJoin.get("task").get("id"), getTaskId()));

        if (getStudentId() != null){
          predicates.add(cb.equal(progressJoin.get("student").get("id"), getStudentId()));
        }

        if (getIsCorrect() != null){
          predicates.add(cb.equal(root.get("isCorrect"), getIsCorrect()));
        }

        if (getTaskQuestionId() != null){
          Join<CorrectAnswer, LessonQuestion> taskQuestionJoin = progressJoin.join("taskQuestion");
          predicates.add(cb.equal(taskQuestionJoin.get("id"), getTaskQuestionId()));
        }
        return cb.and(predicates.toArray(new Predicate[predicates.size()]));
      }
    };
  }
}
