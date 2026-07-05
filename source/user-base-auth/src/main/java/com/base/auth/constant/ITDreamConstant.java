package com.base.auth.constant;

import java.io.File;
import java.util.List;

public class ITDreamConstant {
    public static final String DATE_FORMAT = "dd/MM/yyyy";
    public static final String DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm:ss";

    public static final String DIRECTORY_GENERAL = File.separator + "general";

    public static final Integer USER_KIND_ADMIN = 1;
    public static final Integer USER_KIND_EDUCATOR = 2;
    public static final Integer USER_KIND_STUDENT = 3;
    public static final Integer USER_KIND_COMPANY = 4;

    public static final Integer TASK_KIND_TASK = 1;
    public static final Integer TASK_KIND_SUBTASK = 2;
    public static final List<Integer> TASK_KINDS = List.of(TASK_KIND_TASK, TASK_KIND_SUBTASK);

    public static final List<Integer> STARS = List.of(1, 2, 3, 4, 5);

    public static final Integer STATUS_ACTIVE = 1;
    public static final Integer STATUS_FORGET_PASSWORD = 0;
    public static final Integer STATUS_VERIFY = 3;
    public static final Integer STATUS_WAITING_APPROVE = 2;
    public static final Integer STATUS_LOCK = -1;
    public static final Integer STATUS_REJECT = -2;

    public static final Integer BLOG_STATUS_WAITING_APPROVE = 0;
    public static final Integer BLOG_STATUS_ACTIVE = 1;
    public static final Integer BLOG_STATUS_REJECT = -1;

    public static final Integer SIMULATION_STATUS_ACTIVE = 1;
    public static final Integer SIMULATION_STATUS_WAITING_APPROVE = 2;
    public static final Integer SIMULATION_STATUS_WAITING_APPROVE_DELETE = 3;
    public static final Integer SIMULATION_STATUS_REJECT = -1;

    public static final Integer STATE_SIMULATION_PROCESSING = 1;
    public static final Integer STATE_SIMULATION_DONE = 2;
    public static final Integer STATE_SIMULATION_FAIL = 3;

    public static final Integer STATE_TASK_PROCESSING = 1;
    public static final Integer STATE_TASK_DONE = 2;
    public static final Integer STATE_TASK_FAIL = 3;

    public static final Integer KIND_SIMULATION = 1;
    public static final Integer KIND_TASK = 2;

    public static final Integer SIMULATION_LEVEL_BEGINNER = 1;
    public static final Integer SIMULATION_LEVEL_INTERMEDIATE = 2;
    public static final Integer SIMULATION_LEVEL_ADVANCED = 3;
    public static final List<Integer> SIMULATION_LEVELS = List.of(SIMULATION_LEVEL_BEGINNER, SIMULATION_LEVEL_INTERMEDIATE, SIMULATION_LEVEL_ADVANCED);

    public static final Integer TASK_TYPE_NONE = 0;
    public static final Integer TASK_TYPE_FILE_ONLY = 1;
    public static final Integer TASK_TYPE_TEXT_ONLY = 2;
    public static final Integer TASK_TYPE_FILE_TEXT = 3;
    public static final List<Integer> TASK_SUBMISSION_TYPES = List.of(TASK_TYPE_NONE, TASK_TYPE_FILE_ONLY, TASK_TYPE_TEXT_ONLY, TASK_TYPE_FILE_TEXT);

    public static final Integer TASK_NO_ERROR = 0;

    public static final Integer CATEGORY_KIND_SPECIALIZATION = 1;
    public static final Integer CATEGORY_KIND_BLOG = 2;
    public static final List<Integer> CATEGORY_KINDS = List.of(CATEGORY_KIND_SPECIALIZATION, CATEGORY_KIND_BLOG);

    public static final Integer RESTART_ERROR_COUNT = 0;

    public static final Integer SIMULATION_ENROLLMENT_COMPLETED = 1;
    public static final Integer SIMULATION_ENROLLMENT_IN_PROGRESS = 2;

    public static final Integer SIMULATION_ENROLLMENT_REVIEW_STATUS_NOT_REVIEWED = 0;
    public static final Integer SIMULATION_ENROLLMENT_REVIEW_STATUS_REVIEWED = 1;

    public static final Integer STUDENT_TASK_PROGRESS_COMPLETED = 1;
    public static final Integer STUDENT_TASK_PROGRESS_IN_PROGRESS = 2;

    public static final Integer ORGANIZATION_TYPE_UNIVERSITY = 1;
    public static final Integer ORGANIZATION_TYPE_COMPANY = 2;
    public static final List<Integer> ORGANIZATION_TYPES = List.of(ORGANIZATION_TYPE_UNIVERSITY, ORGANIZATION_TYPE_COMPANY);

    public static final Integer NATION_KIND_PROVINCE = 1;
    public static final Integer NATION_KIND_WARD = 2;

    public static final List<Integer> NATION_KINDS = List.of(NATION_KIND_PROVINCE, NATION_KIND_WARD);

    public static final Integer JOB_POST_TYPE_EVENT = 1;
    public static final Integer JOB_POST_TYPE_JOB = 2;
    public static final Integer JOB_POST_TYPE_TALENT_NETWORK = 3;
    public static final List<Integer> JOB_POST_TYPES = List.of(JOB_POST_TYPE_EVENT, JOB_POST_TYPE_JOB, JOB_POST_TYPE_TALENT_NETWORK);

    public static final Integer JOB_POST_ROLE_TYPE_INTERNSHIP = 1;
    public static final Integer JOB_POST_ROLE_TYPE_PART_TIMES = 2;
    public static final Integer JOB_POST_ROLE_TYPE_FULL_TIMES = 3;
    public static final Integer JOB_POST_ROLE_TYPE_INTERNSHIP_PART_TIMES = 4;
    public static final Integer JOB_POST_ROLE_TYPE_INTERNSHIP_FULL_TIMES = 5;
    public static final Integer JOB_POST_ROLE_TYPE_PART_TIMES_FULL_TIMES = 6;
    public static final Integer JOB_POST_ROLE_TYPE_ALL = 7;
    public static final List<Integer> JOB_POST_ROLE_TYPES = List.of(JOB_POST_ROLE_TYPE_INTERNSHIP, JOB_POST_ROLE_TYPE_PART_TIMES, JOB_POST_ROLE_TYPE_FULL_TIMES, JOB_POST_ROLE_TYPE_INTERNSHIP_PART_TIMES, JOB_POST_ROLE_TYPE_INTERNSHIP_FULL_TIMES, JOB_POST_ROLE_TYPE_PART_TIMES_FULL_TIMES, JOB_POST_ROLE_TYPE_ALL);

    public static final Integer JOB_POST_STATUS_ACTIVE = 1;
    public static final Integer JOB_POST_STATUS_HIDE = 0;
    public static final Integer JOB_POST_STATUS_BLOCK = -1;
    public static final List<Integer> JOB_POST_STATUSES = List.of(JOB_POST_STATUS_ACTIVE, JOB_POST_STATUS_HIDE, JOB_POST_STATUS_BLOCK);

    public static final Boolean READ = true;

    public static final String BACKEND_PROCESS_VIDEO_CMD = "BACKEND_PROCESS_VIDEO";
    public static final String MEDIA_COMPLETED_PROCESS_VIDEO_CMD = "MEDIA_COMPLETED_PROCESS_VIDEO";
    public static final String BACKEND_POST_NOTIFICATION_CMD = "BACKEND_POST_NOTIFICATION";

    public static final Integer MAX_ATTEMPT_FORGET_PWD = 5;
    public static final int MAX_TIME_FORGET_PWD = 5 * 60 * 1000; //5 minutes
    public static final Integer MAX_ATTEMPT_LOGIN = 5;

    public static final String PASSWORD_PATTERN = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,15}$";
    public static final String EMAIL_PATTERN = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    public static final String PHONE_PATTERN = "^0\\d{9}$";
    public static final String FILE_PATH_PATTERN = "^https?://.*$";
    public static final String VIETNAM_HOTLINE_PATTERN = "^(0(2[0-9]{9}|[3|5|7|8|9][0-9]{8})|(1[8|9]00[0-9]{4,6}))$";

    public static final String NOTIFICATION_TYPE_REVIEW_SUBMISSION = "REVIEW_SUBMISSION";

    private ITDreamConstant(){
        throw new IllegalStateException("Utility class");
    }
}
