package com.base.auth.dto.student;

import com.base.auth.dto.account.ProfileAccountDto;
import java.util.Date;
import java.util.List;
import lombok.Data;

@Data
public class ProfileStudentDto {
  private ProfileAccountDto profileAccountDto;
  private Date birthday;
  private Boolean isReviewed;
  private List<StudentPreferencesDto> preferences;
}
