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
@Table(name = "db_it_dream_comment")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Comment extends Auditable<String>{
  @Id
  @GenericGenerator(name = "idGenerator", strategy = "com.base.auth.service.id.IdGenerator")
  @GeneratedValue(generator = "idGenerator")
  private Long id;
  @Column(columnDefinition = "TEXT")
  private String content;
  @ManyToOne
  @JoinColumn(name = "lesson_id")
  private Lesson lesson;
  @ManyToOne
  @JoinColumn(name = "user_id")
  private Account user;
  @ManyToOne
  @JoinColumn(name = "parent_id")
  private Comment parent;
  @ManyToOne
  @JoinColumn(name = "root_id")
  private Comment root;
}
