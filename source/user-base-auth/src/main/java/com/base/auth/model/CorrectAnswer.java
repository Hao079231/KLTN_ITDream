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
@Table(name = "db_it_dream_correct_answer")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class CorrectAnswer {
  @Id
  @GenericGenerator(name = "idGenerator", strategy = "com.base.auth.service.id.IdGenerator")
  @GeneratedValue(generator = "idGenerator")
  private  Long id;
  @Column(name = "answer", columnDefinition = "TEXT")
  private String answer;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "lesson_progress_id")
  private LessonProgress lessonProgress;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "lesson_question_id")
  private LessonQuestion lessonQuestion;
}
