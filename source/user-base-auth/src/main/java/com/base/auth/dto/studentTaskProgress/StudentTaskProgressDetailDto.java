package com.base.auth.dto.studentTaskProgress;

import com.base.auth.dto.ResponseListDto;
import com.base.auth.dto.studentSubmission.StudentSubmissionDisplayDto;
import com.base.auth.dto.task.TaskDisplayDto;
import java.util.List;
import lombok.Data;

@Data
public class StudentTaskProgressDetailDto {
  private Long id;
  private Integer errorCount;
  private TaskDisplayDto task;
  private ResponseListDto<List<StudentSubmissionDisplayDto>> studentSubmission;
}
