package com.base.auth.dto.comment;

import com.base.auth.dto.ABasicAdminDto;
import com.base.auth.dto.account.AccountDto;
import com.base.auth.dto.lesson.LessonDto;
import lombok.Data;

@Data
public class CommentDto extends ABasicAdminDto {
  private String content;
  private AccountDto user;
  private LessonDto lesson;
  private Long parentId;
  private Long rootId;
  private String replyToUser;
}
