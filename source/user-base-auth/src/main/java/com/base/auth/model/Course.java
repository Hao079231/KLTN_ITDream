package com.base.auth.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
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
@Table(name = "db_it_dream_course")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Course extends Auditable<String>{
  @Id
  @GenericGenerator(name = "idGenerator", strategy = "com.base.auth.service.id.IdGenerator")
  @GeneratedValue(generator = "idGenerator")
  private Long id;
  private String title;
  @Column(columnDefinition = "TEXT")
  private String overview;
  @Column(columnDefinition = "TEXT")
  private String description;
  private Integer level; // 1 - beginner, 2 - intermediate, 3 - advanced
  private String duration;
  private String thumbnail;
  private String videoPath;
  private Integer videoState; // 1 - process, 2 - done, 3 - failed
  private Float avgStar = 0F;
  private Long totalParticipant = 0L;
  private Integer totalLesson = 0;
  @Column(columnDefinition = "TEXT")
  private String notice;
  @ManyToOne
  @JoinColumn(name = "category_id")
  private Category category;
  @ManyToOne
  @JoinColumn(name = "educator_id")
  private Educator educator;
}
