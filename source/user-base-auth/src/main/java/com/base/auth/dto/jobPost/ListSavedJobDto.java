package com.base.auth.dto.jobPost;

import java.util.List;
import lombok.Data;

@Data
public class ListSavedJobDto {
  private List<Long> jobPostIds;
}
