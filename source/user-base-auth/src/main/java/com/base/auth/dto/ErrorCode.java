package com.base.auth.dto;

public class ErrorCode {
    /**
     * Starting error code Account
     * */
    public static final String ACCOUNT_ERROR_UNKNOWN = "ERROR-ACCOUNT-0000";
    public static final String ACCOUNT_ERROR_USERNAME_EXIST = "ERROR-ACCOUNT-0001";
    public static final String ACCOUNT_ERROR_NOT_FOUND = "ERROR-ACCOUNT-0002";
    public static final String ACCOUNT_ERROR_WRONG_PASSWORD = "ERROR-ACCOUNT-0003";
    public static final String ACCOUNT_ERROR_WRONG_HASH_RESET_PASS = "ERROR-ACCOUNT-0004";
    public static final String ACCOUNT_ERROR_LOCKED = "ERROR-ACCOUNT-0005";
    public static final String ACCOUNT_ERROR_OPT_INVALID = "ERROR-ACCOUNT-0006";
    public static final String ACCOUNT_ERROR_NOT_DELETE = "ERROR-ACCOUNT-0007";
    public static final String ACCOUNT_ERROR_NOT_ALLOW_DELETE_ADMIN = "ERROR-ACCOUNT-0011";
    public static final String ACCOUNT_ERROR_NOT_ALLOW_DELETE_SUPPER_ADMIN = "ERROR-ACCOUNT-0012";
    public static final String ACCOUNT_ERROR_EMAIL_EXIST = "ERROR-ACCOUNT-0013";
    public static final String ACCOUNT_ERROR_PHONE_EXIST = "ERROR-ACCOUNT-0014";
    public static final String ACCOUNT_ERROR_NOT_ACTIVE = "ERROR-ACCOUNT-0015";
    public static final String ACCOUNT_ERROR_INCORRECT_HASH_VERIFICATION = "ERROR-ACCOUNT-0016";
    public static final String ACCOUNT_ERROR_NOT_PENDING = "ERROR-ACCOUNT-0017";

    /**
     * Starting error code Group
     * */
    public static final String GROUP_ERROR_NOT_FOUND = "ERROR-GROUP-0000";
    public static final String GROUP_ERROR_EXIST = "ERROR-GROUP-0001";
    public static final String GROUP_ERROR_DELETE = "ERROR-GROUP-0002";

    /**
     * Starting error code Permission
     * */
    public static final String PERMISSION_ERROR_NOT_FOUND = "ERROR-PERMISSION-0000";
    public static final String PERMISSION_ERROR_EXIST = "ERROR-PERMISSION-0001";
    public static final String PERMISSION_ERROR_DELETE = "ERROR-PERMISSION-002";

    /**
     * Starting error code USER
     *
     */
    public static final String USER_ERROR_EXIST = "ERROR-USER-0000";
    public static final String USER_ERROR_NOT_FOUND = "ERROR-USER-0001";
    public static final String USER_ERROR_LOGIN_FAILED = "ERROR-USER-0002";
    public static final String USER_ERROR_VERIFY_FAILED = "ERROR-USER-0003";
    public static final String USER_ERROR_NOT_APPROVE = "ERROR-USER-0004";
    public static final String USER_ERROR_NOT_REJECT = "ERROR-USER-0005";
    public static final String USER_ERROR_NOT_EDUCATOR = "ERROR-USER-0006";
    public static final String USER_ERROR_NOT_STUDENT = "ERROR-USER-0007";
    public static final String USER_ERROR_NOT_ADMIN = "ERROR-USER-0008";

    /**
     * Starting error code DATABASE_ERROR
     *
     */
    public static final String  ERROR_DB_QUERY = "ERROR-DB-QUERY-0000";

    /**
     * Starting error code Category
     * */
    public static final String CATEGORY_ERROR_NOT_FOUND = "CATEGORY-ERROR-0000";
    public static final String CATEGORY_ERROR_EXIST = "CATEGORY-ERROR-0001";
    public static final String CATEGORY_ERROR_DELETE = "CATEGORY-ERROR-0002";

    /**
     * Starting error code Simulation
     * */
    public static final String SIMULATION_ERROR_NOT_FOUND = "SIMULATION-ERROR-0000";
    public static final String SIMULATION_ERROR_EXIST = "SIMULATION-ERROR-0001";
    public static final String SIMULATION_ERROR_NOT_DELETE = "SIMULATION-ERROR-0002";
    public static final String SIMULATION_ERROR_APPROVE = "SIMULATION-ERROR-0003";
    public static final String SIMULATION_ERROR_NOT_AUTHORIZED = "SIMULATION-ERROR-0004";

    /**
     * Starting error code Task
     * */
    public static final String TASK_ERROR_NOT_FOUND = "TASK-ERROR-0000";
    public static final String TASK_ERROR_EXIST = "TASK-ERROR-0001";
    public static final String TASK_ERROR_POSITION = "TASK-ERROR-0002";
    public static final String TASK_ERROR_PARENT_KIND_TASK = "TASK-ERROR-0003";
    public static final String TASK_ERROR_MUST_HAVE_PARENT = "TASK-ERROR-0004";
    public static final String TASK_ERROR_KIND_INVALID = "TASK-ERROR-0005";

    /**
     * Starting error code TaskQuestion
     * */
    public static final String TASK_QUESTION_ERROR_NOT_FOUND = "TASK_QUESTION-ERROR-0000";
    public static final String TASK_QUESTION_ERROR_EXIST = "TASK_QUESTION-ERROR-0001";

    /**
     * Starting error code Simulation enrollment
     * */
    public static final String SIMULATION_ENROLLMENT_ERROR_NOT_FOUND = "SIMULATION-ENROLLMENT-ERROR-0000";
    public static final String SIMULATION_ENROLLMENT_ERROR_NOT_CREATE = "SIMULATION-ENROLLMENT-ERROR-0001";
    public static final String SIMULATION_ENROLLMENT_ERROR_EXIST = "SIMULATION-ENROLLMENT-ERROR-0002";
    public static final String SIMULATION_ENROLLMENT_ERROR_NOT_COMPLETE = "SIMULATION-ENROLLMENT-ERROR-0003";


    /**
     * Starting error code Student task progress
     * */
    public static final String STUDENT_TASK_PROGRESS_ERROR_NOT_FOUND = "STUDENT_TASK-PROGRESS-ERROR-0000";
    public static final String STUDENT_TASK_PROGRESS_ERROR_EXIST = "STUDENT_TASK-PROGRESS-ERROR-0001";
    public static final String STUDENT_TASK_PROGRESS_ERROR_NOT_COMPLETED = "STUDENT_TASK-PROGRESS-ERROR-0002";
    public static final String STUDENT_TASK_PROGRESS_ERROR_NOT_CREATE = "STUDENT_TASK-PROGRESS-ERROR-0003";
    public static final String STUDENT_TASK_PROGRESS_ERROR_FAIL = "STUDENT_TASK-PROGRESS-ERROR-0004";
    public static final String STUDENT_TASK_PROGRESS_ERROR_NOT_RESET = "STUDENT_TASK-PROGRESS-ERROR-0005";


    /**
     * Starting error code Google
     * */
    public static final String GOOGLE_ERROR_ACCESS_TOKEN_INVALID = "GOOGLE-ERROR-0000";


    /**
     * Starting error code Achievement
     * */
    public static final String ACHIEVEMENT_ERROR_NOT_FOUND = "ACHIEVEMENT-ERROR-0000";
    public static final String ACHIEVEMENT_ERROR_NOT_AUTHORIZE = "ACHIEVEMENT-ERROR-0001";

    /**
     * Starting error code Feedback submission
     * */
    public static final String REVIEW_SUBMISSION_ERROR_NOT_FOUND = "REVIEW-SUBMISSION-ERROR-0000";
    public static final String REVIEW_SUBMISSION_ERROR_EXIST = "REVIEW-SUBMISSION-ERROR-0001";
    public static final String REVIEW_SUBMISSION_ERROR_NOT_COMPLETE = "REVIEW-SUBMISSION-ERROR-0002";
    public static final String REVIEW_SUBMISSION_ERROR_NOT_CREATE = "REVIEW-SUBMISSION-ERROR-0003";

    /**
     * Starting error code Notification
     * */
    public static final String NOTIFICATION_ERROR_NOT_FOUND = "NOTIFICATION-ERROR-0000";
    public static final String NOTIFICATION_ERROR_EXIST = "NOTIFICATION-ERROR-0001";

    /**
     * Starting error code Student submission
     * */
    public static final String STUDENT_SUBMISSION_ERROR_NOT_FOUND = "STUDENT-SUBMISSION-ERROR-0000";
    public static final String STUDENT_SUBMISSION_ERROR_NOT_CREATE = "STUDENT-SUBMISSION-ERROR-0001";

    /**
     * Starting error code Comment
     * */
    public static final String COMMENT_ERROR_NOT_FOUND = "COMMENT-ERROR-0000";
    public static final String COMMENT_ERROR_NOT_UPDATE = "COMMENT-ERROR-0001";
    public static final String COMMENT_ERROR_NOT_DELETE = "COMMENT-ERROR-0002";
    public static final String COMMENT_ERROR_INVALID_PARENT = "COMMENT-ERROR-0003";

    /**
     * Starting error code Feedback
     * */
    public static final String FEEDBACK_ERROR_NOT_FOUND = "FEEDBACK-ERROR-0000";
    public static final String FEEDBACK_ERROR_EXIST = "FEEDBACK-ERROR-0001";

    /**
     * Starting error code Organization
     * */
    public static final String ORGANIZATION_ERROR_NOT_FOUND = "ORGANIZATION-ERROR-0000";
    public static final String ORGANIZATION_ERROR_EXIST = "ORGANIZATION-ERROR-0001";

    /**
     * Starting error code Blog
     * */
    public static final String BLOG_ERROR_NOT_FOUND = "BLOG-ERROR-0000";
    public static final String BLOG_ERROR_EXIST = "BLOG-ERROR-0001";
    public static final String BLOG_ERROR_NAME_SUBJECT_NOT_NULL = "BLOG-ERROR-0002";
    public static final String BLOG_ERROR_CATEGORY_PARENT_BOTH_NULL = "BLOG-ERROR-0003";
    public static final String BLOG_ERROR_NAME_NOT_NULL = "BLOG-ERROR-0004";
    public static final String BLOG_ERROR_SUBJECT_NOT_NULL = "BLOG-ERROR-0005";
    public static final String BLOG_ERROR_NAME_SUBJECT_EXIST = "BLOG-ERROR-0006";
    public static final String BLOG_ERROR_CATEGORY_PARENT_BOTH_NOT_NULL = "BLOG-ERROR-0007";

    /**
     * Starting error code Nation
     * */
    public static final String NATION_ERROR_NOT_FOUND = "NATION-ERROR-0000";
    public static final String NATION_ERROR_EXIST = "NATION-ERROR-0001";
    public static final String NATION_ERROR_NOT_PARENT = "NATION-ERROR-0002";
    public static final String NATION_ERROR_NOT_PARENT_DISTRICT = "NATION-ERROR-0003";

    /**
     * Starting error code Job post
     * */
    public static final String JOB_POST_ERROR_NOT_FOUND = "JOB-POST-ERROR-0000";
    public static final String JOB_POST_ERROR_EXIST = "JOB-POST-ERROR-0001";
    public static final String JOB_POST_ERROR_DATE_NULL = "JOB-POST-ERROR-0002";
}
