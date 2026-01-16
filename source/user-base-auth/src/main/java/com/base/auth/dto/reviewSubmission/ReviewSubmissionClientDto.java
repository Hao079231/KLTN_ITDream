package com.base.auth.dto.reviewSubmission;

import com.base.auth.dto.course.CourseDisplayDto;
import com.base.auth.dto.student.ProfileStudentDto;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ReviewSubmissionClientDto {
  private Long id;
  private String content;
  private CourseDisplayDto simulation;
  private ProfileStudentDto student;
  private Boolean isReviewed;
  private LocalDateTime modifiedDate;
}
