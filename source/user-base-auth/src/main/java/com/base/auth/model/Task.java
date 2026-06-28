package com.base.auth.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
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
@Table(name = "db_it_dream_task")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Task {
  @Id
  @GenericGenerator(name = "idGenerator", strategy = "com.base.auth.service.id.IdGenerator")
  @GeneratedValue(generator = "idGenerator")
  private Long id;
  private String title;
  @Column(columnDefinition = "TEXT")
  private String description;
  @Column(columnDefinition = "TEXT")
  private String content;
  private Integer kind; // 1 - task, 2 - subtask
  private Integer orderInParent;
  @Column(name = "submission_type")
  private Integer submissionType; // 0 - none, 1 - file only, 2 - text only, 3 - file + text
  private String videoPath;
  private String filePath;
  private String imagePath;
  private Integer videoState;
  private Integer totalQuestion = 0;
  private Integer totalError = 0;
  /**
   * Submission requirement for subtask (kind = 2).
   * 0 = none, 1 = file only, 2 = text only, 3 = file + text.
   * Null is treated the same as 0 (no requirement).
   */
  private Integer submissionType;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_id")
  private Task parent;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "simulation_id")
  private Simulation simulation;
}
