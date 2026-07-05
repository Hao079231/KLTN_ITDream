package com.base.auth.service.schedule;

import com.base.auth.repository.JobPostRepository;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class JobPostScheduler {
  @Autowired
  JobPostRepository jobPostRepository;

  @Scheduled(cron = "0 0 0 * * *")
  public void updateJobPostStatus(){
    log.info("===> Start checking expired JobPost at {}", new Date());
    jobPostRepository.expireJobPosts(new Date());
  }
}
