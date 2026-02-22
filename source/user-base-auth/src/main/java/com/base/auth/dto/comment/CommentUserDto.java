package com.base.auth.dto.comment;

import com.base.auth.dto.account.ProfileAccountDto;
import com.base.auth.dto.lesson.LessonDisplayDto;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class CommentUserDto {
  private Long id;
  private String content;
  private ProfileAccountDto user;
  private LessonDisplayDto lesson;
  private LocalDateTime createdDate;
  private Long parentId;
  private Long rootId;
  private String replyToUser;
}
