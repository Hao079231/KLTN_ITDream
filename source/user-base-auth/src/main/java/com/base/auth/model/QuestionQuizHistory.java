package com.base.auth.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "db_it_dream_question_quiz_history")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class QuestionQuizHistory{
  @Id
  @GenericGenerator(name = "idGenerator", strategy = "com.base.auth.service.id.IdGenerator")
  @GeneratedValue(generator = "idGenerator")
  private Long id;
  @Column(columnDefinition = "TEXT")
  private String answer;
  private Boolean isCorrect;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "student_task_progress_id")
  private StudentTaskProgress studentTaskProgress;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "task_question_id")
  private TaskQuestion taskQuestion;
}
