package com.base.auth.repository;

import com.base.auth.model.Comment;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CommentRepository extends JpaRepository<Comment, Long>, JpaSpecificationExecutor<Comment> {
  @Transactional
  void deleteAllByParent(Comment parent);

  void deleteAllByLessonId(Long lessonId);
}
