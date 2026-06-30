package com.base.auth.service;

import com.base.auth.model.Comment;
import com.base.auth.repository.CommentRepository;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CommentService {

  @Autowired
  CommentRepository commentRepository;

  @Transactional
  public void deleteReplyComment(Comment deletedComment) {

    List<Comment> children =
        commentRepository.findAllByParent(deletedComment);

    for (Comment child : children) {
      child.setParent(deletedComment.getParent());
    }

    commentRepository.saveAll(children);
    commentRepository.flush();
    commentRepository.delete(deletedComment);
  }

  @Transactional
  public void deleteRootComment(Comment deletedRoot) {
    List<Comment> thread = commentRepository.findWholeThread(deletedRoot.getId());
    Map<Long, List<Comment>> childrenMap = buildChildrenMap(thread);
    List<Comment> firstLevel = childrenMap.get(deletedRoot.getId());

    if (firstLevel != null) {
      for (Comment child : firstLevel) {
        child.setParent(null);
        child.setRoot(null);
        updateRoot(child, childrenMap);
      }
    }

    List<Comment> commentsNeedUpdate = thread.stream()
        .filter(c -> !c.getId().equals(deletedRoot.getId()))
        .collect(Collectors.toList());

    commentRepository.saveAll(commentsNeedUpdate);
    commentRepository.flush();
    commentRepository.delete(deletedRoot);
  }

  private void updateRoot(Comment newRoot, Map<Long, List<Comment>> childrenMap) {
    Deque<Comment> stack = new ArrayDeque<>();
    stack.push(newRoot);

    while (!stack.isEmpty()) {
      Comment current = stack.pop();
      List<Comment> children = childrenMap.get(current.getId());

      if (children == null) {
        continue;
      }

      for (Comment child : children) {
        child.setRoot(newRoot);
        stack.push(child);
      }
    }
  }

  private Map<Long, List<Comment>> buildChildrenMap(List<Comment> comments) {
    Map<Long, List<Comment>> map = new HashMap<>();

    for (Comment comment : comments) {
      if (comment.getParent() == null) {
        continue;
      }

      Long parentId = comment.getParent().getId();
      List<Comment> children = map.get(parentId);

      if (children == null) {
        children = new ArrayList<>();
        map.put(parentId, children);
      }

      children.add(comment);
    }
    return map;
  }

  @Transactional
  public void deleteCommentsByUser(Long userId) {
    List<Comment> comments = commentRepository.findAllByUserId(userId);

    comments.sort((c1, c2) -> {
      if (c1.getParent() == null && c2.getParent() != null) {
        return 1;
      }

      if (c1.getParent() != null && c2.getParent() == null) {
        return -1;
      }
      return 0;
    });

    for (Comment comment : comments) {
      if (!commentRepository.existsById(comment.getId())) {
        continue;
      }

      if (comment.getParent() == null) {
        deleteRootComment(comment);
      } else {
        deleteReplyComment(comment);
      }
    }
  }
}