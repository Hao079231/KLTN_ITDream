package com.base.auth.dto.reviewSubmission;

import com.base.auth.dto.ABasicAdminDto;
import com.base.auth.dto.course.CourseDto;
import com.base.auth.dto.student.StudentDto;
import lombok.Data;

@Data
public class ReviewSubmissionDto extends ABasicAdminDto {
  private String content;
  private Boolean isReviewed;
  private CourseDto simulation;
  private StudentDto student;
}
