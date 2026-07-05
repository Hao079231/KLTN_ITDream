package com.base.auth.model;

import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "db_it_dream_job_post")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class JobPost extends Auditable<String>{
  @Id
  @GenericGenerator(name = "idGenerator", strategy = "com.base.auth.service.id.IdGenerator")
  @GeneratedValue(generator = "idGenerator")
  private Long id;

  private String title;

  @Column(columnDefinition = "TEXT")
  private String content;

  @Column(columnDefinition = "TEXT")
  private String image;

  private Integer type;  // 1 - event, 2 - job, 3 - talent network

  @Column(name = "role_type")
  private Integer roleType; // 1 - internship, 2 - part-times, 3 - full-times, 4 - internship, part-times, 5 - internship, full-times, 6 - part-times, full-times, 7 - all

  @Column(name = "job_url")
  private String jobUrl;

  private String address;

  @ManyToOne
  @JoinColumn(name = "province_id")
  private Nation province;

  @ManyToOne
  @JoinColumn(name = "ward_id")
  private Nation ward;

  private Date date;

  @Column(name = "end_date")
  private Date endDate;

  private String notice;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "db_it_dream_simulation_job",
      joinColumns = @JoinColumn(name = "job_id", referencedColumnName = "id"),
      inverseJoinColumns = @JoinColumn(name = "simulation_id", referencedColumnName = "id"))
  private List<Simulation> simulations;

  @ManyToOne
  @JoinColumn(name = "educator_id")
  private Educator educator;
}
