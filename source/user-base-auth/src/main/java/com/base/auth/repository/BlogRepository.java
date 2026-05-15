package com.base.auth.repository;

import com.base.auth.model.Blog;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

public interface BlogRepository extends JpaRepository<Blog, Long>, JpaSpecificationExecutor<Blog> {

  Boolean existsByNameAndCategoryIdAndEducatorId(String name, Long categoryId, Long educatorId);

  Boolean existsBySubjectAndParentId(String subject, Long parentId);

  @Transactional
  void deleteByParentId(Long parentId);

  Boolean existsByCategoryId(Long categoryId);

  List<Blog> findAllByEducatorId(Long educatorId);

  List<Blog> findAllByParentId(Long parentId);
}
