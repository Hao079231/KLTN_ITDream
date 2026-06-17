package com.base.auth.repository;

import com.base.auth.model.Comment;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long>, JpaSpecificationExecutor<Comment> {
  @Transactional
  void deleteAllByParent(Comment parent);

  void deleteAllByTaskId(Long taskId);

  @Transactional
  @Modifying
  @Query("UPDATE Comment c SET c.user = null WHERE c.user.id = :accountId")
  void clearUser(@Param("accountId") Long accountId);
}
