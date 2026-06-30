package com.base.auth.service;

import com.base.auth.constant.ITDreamConstant;
import com.base.auth.model.Achievement;
import com.base.auth.model.Feedback;
import com.base.auth.model.Simulation;
import com.base.auth.model.Student;
import com.base.auth.repository.AchievementRepository;
import com.base.auth.repository.CommentRepository;
import com.base.auth.repository.FeedbackRepository;
import com.base.auth.repository.QuestionQuizHistoryRepository;
import com.base.auth.repository.ReviewSubmissionRepository;
import com.base.auth.repository.SimulationEnrollmentRepository;
import com.base.auth.repository.SimulationRepository;
import com.base.auth.repository.StudentSubmissionRepository;
import com.base.auth.repository.StudentTaskProgressRepository;
import java.util.List;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class StudentService {
  @Autowired
  StudentTaskProgressRepository studentTaskProgressRepository;

  @Autowired
  QuestionQuizHistoryRepository questionQuizHistoryRepository;

  @Autowired
  StudentSubmissionRepository studentSubmissionRepository;

  @Autowired
  SimulationEnrollmentRepository simulationEnrollmentRepository;

  @Autowired
  AchievementRepository achievementRepository;

  @Autowired
  ReviewSubmissionRepository reviewSubmissionRepository;

  @Autowired
  FeedbackRepository feedbackRepository;

  @Autowired
  SimulationRepository simulationRepository;

  @Autowired
  UserBaseApiService userBaseApiService;

  @Autowired
  CommentService commentService;

  @Transactional
  public void deleteByStudent(Student student){
    List<Achievement> achievements = achievementRepository.findAllByStudentId(student.getId());
    for (Achievement achievement : achievements){
      if (StringUtils.isNotBlank(achievement.getFilePath())){
        userBaseApiService.deleteByFilePath(achievement.getFilePath());
      }
    }
    List<Feedback> feedbacks = feedbackRepository.findAllByStudentId(student.getId());
    for (Feedback feedback : feedbacks) {
      Simulation simulation = feedback.getSimulation();
      Long totalFeedback = simulation.getTotalFeedback();
      Float avgStar = simulation.getAvgStar();

      if (totalFeedback == null || totalFeedback <= 1) {
        simulation.setTotalFeedback(0L);
        simulation.setAvgStar(0F);
      } else {
        float totalStar = avgStar * totalFeedback;
        totalStar -= feedback.getStar();
        long newTotalFeedback = totalFeedback - 1;
        simulation.setTotalFeedback(newTotalFeedback);
        simulation.setAvgStar(totalStar / newTotalFeedback);
      }
      simulation.setTotalParticipant(simulation.getTotalParticipant() - 1);
      simulationRepository.save(simulation);
    }

    achievementRepository.deleteAllByStudentId(student.getId());
    commentService.deleteCommentsByUser(student.getId());
    feedbackRepository.deleteAllByStudentId(student.getId());
    reviewSubmissionRepository.deleteAllByStudentId(student.getId());
    studentSubmissionRepository.deleteAllByStudentId(student.getId());
    questionQuizHistoryRepository.deleteAllByStudentId(student.getId());
    studentTaskProgressRepository.deleteAllByStudentId(student.getId());
    simulationEnrollmentRepository.deleteAllByStudentId(student.getId());
    if (StringUtils.isNotBlank(student.getAccount().getAvatarPath())
        && !student.getAccount().getAvatarPath().matches(ITDreamConstant.FILE_PATH_PATTERN)){
      userBaseApiService.deleteByFilePath(student.getAccount().getAvatarPath());
    }
  }
}
