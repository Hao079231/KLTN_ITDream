package com.base.auth.repository;

import com.base.auth.model.Comment;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long>, JpaSpecificationExecutor<Comment> {
  void deleteAllByTaskId(Long taskId);

  List<Comment> findAllByParent(Comment parent);

  @Query("SELECT c FROM Comment c WHERE c.id = :rootId OR c.root.id = :rootId")
  List<Comment> findWholeThread(@Param("rootId") Long rootId);

  List<Comment> findAllByUserId(Long userId);
}
