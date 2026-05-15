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
@Table(name = "db_it_dream_blog")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Blog extends Auditable<String>{
  @Id
  @GenericGenerator(name = "idGenerator", strategy = "com.base.auth.service.id.IdGenerator")
  @GeneratedValue(generator = "idGenerator")
  private Long id;

  private String name;

  private String subject;

  @Column(columnDefinition = "TEXT")
  private String content;

  private String notice;

  private String image;

  @ManyToOne
  @JoinColumn(name = "parent_id")
  private Blog parent;

  @ManyToOne
  @JoinColumn(name = "category_id")
  private Category category;

  @ManyToOne
  @JoinColumn(name = "educator_id")
  private Educator educator;
}
