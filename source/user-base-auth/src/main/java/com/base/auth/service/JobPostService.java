package com.base.auth.service;

import com.base.auth.constant.ITDreamConstant;
import com.base.auth.model.JobPost;
import com.base.auth.repository.JobPostRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class JobPostService {
  @Autowired
  JobPostRepository jobPostRepository;

  @Autowired
  UserBaseApiService userBaseApiService;

  public void deleteAllByJobPost(JobPost jobPost) {
    if (StringUtils.isNotBlank(jobPost.getImage())
        && !jobPost.getImage().matches(ITDreamConstant.FILE_PATH_PATTERN)) {
      userBaseApiService.deleteByFilePath(jobPost.getImage());
    }
    jobPostRepository.deleteSimulationJobByJobId(jobPost.getId());
    jobPostRepository.delete(jobPost);
  }
}
