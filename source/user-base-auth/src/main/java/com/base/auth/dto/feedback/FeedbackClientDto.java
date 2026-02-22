package com.base.auth.dto.feedback;

import com.base.auth.dto.course.CourseDisplayDto;
import com.base.auth.dto.student.ProfileStudentDto;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class FeedbackClientDto {
  private Long id;
  private Integer star;
  private String content;
  private LocalDateTime createdDate;
  private LocalDateTime modifiedDate;
  private ProfileStudentDto student;
  private CourseDisplayDto course;
}
