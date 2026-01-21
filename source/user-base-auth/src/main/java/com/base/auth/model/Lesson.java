package com.base.auth.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "db_it_dream_lesson")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Lesson {
  @Id
  @GenericGenerator(name = "idGenerator", strategy = "com.base.auth.service.id.IdGenerator")
  @GeneratedValue(generator = "idGenerator")
  private Long id;
  private String title;
  @Column(columnDefinition = "TEXT")
  private String introduction;
  @Column(columnDefinition = "TEXT")
  private String description;
  @Column(columnDefinition = "TEXT")
  private String content;
  private String videoPath;
  private String filePath;
  private String imagePath;
  private Integer videoState;
  private Integer totalQuestion = 0;
  private Integer totalError = 0;
  @OneToOne
  @JoinColumn(name = "previous_id")
  private Lesson previous;
  @OneToOne
  @JoinColumn(name = "next_id")
  private Lesson next;
  @ManyToOne
  @JoinColumn(name = "chapter_id")
  private Chapter chapter;
}
