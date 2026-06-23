package com.base.auth.dto.simulationEnrollment;

import com.base.auth.dto.student.ProfileStudentDto;
import lombok.Data;

@Data
public class StudentLessonViewsDto {
  private Long id;
  private ProfileStudentDto student;
  private Boolean isReviewed;      // backward compat: true nếu tất cả subtask đã có review
  private Integer reviewStatus;    // 0 = Chưa nhận xét, 1 = Đã nhận xét hoàn tất (từ DB)
}
