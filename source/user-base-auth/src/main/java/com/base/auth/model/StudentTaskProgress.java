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
@Table(name = "db_it_dream_student_task_progress")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class StudentTaskProgress extends Auditable<String>{
  @Id
  @GenericGenerator(name = "idGenerator", strategy = "com.base.auth.service.id.IdGenerator")
  @GeneratedValue(generator = "idGenerator")
  private Long id;
  @Column(name = "error_count")
  private Integer errorCount = 0;
  @ManyToOne
  @JoinColumn(name = "task_id")
  private Task task;
  @ManyToOne
  @JoinColumn(name = "simulation_enrollment_id")
  private SimulationEnrollment simulationEnrollment;
}
