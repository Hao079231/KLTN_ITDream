package com.base.auth.repository;

import com.base.auth.model.JobPost;
import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface JobPostRepository extends JpaRepository<JobPost, Long>, JpaSpecificationExecutor<JobPost> {
  @Modifying
  @Transactional
  @Query(value = "DELETE FROM db_it_dream_simulation_job WHERE job_id = :jobId",
      nativeQuery = true)
  void deleteSimulationJobByJobId(@Param("jobId") Long jobId);

  List<JobPost> findAllByEducatorId(Long educatorId);

  @Modifying
  @Transactional
  @Query("UPDATE JobPost j " +
      "SET j.status = 0 " +
      "WHERE j.status <> 0 " +
      "AND ( " +
      "      (j.type = 1 AND j.date <= :now) " +
      "   OR (j.type = 2 AND j.endDate <= :now) " +
      ")")
  void expireJobPosts(@Param("now") Date now);
}
