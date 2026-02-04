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
    public static final String ACCOUNT_ERROR_LOGIN = "ERROR-ACCOUNT-0007";
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

    /**
     * Starting error code Permission
     * */
    public static final String PERMISSION_ERROR_NOT_FOUND = "ERROR-PERMISSION-0000";
    public static final String PERMISSION_ERROR_EXIST = "ERROR-PERMISSION-0001";

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
     * Starting error code FILE_ERROR
     *
     */
    public static final String FILE_ERROR_UPLOAD_TYPE_INVALID = "ERROR-FILE-QUERY-0000";
    public static final String FILE_ERROR_UPLOAD_FORMAT_INVALID = "ERROR-FILE-QUERY-0001";

    /**
     * Starting error code Category
     * */
    public static final String CATEGORY_ERROR_NOT_FOUND = "CATEGORY-ERROR-0000";
    public static final String CATEGORY_ERROR_EXIST = "CATEGORY-ERROR-0001";
    public static final String CATEGORY_ERROR_DELETE = "CATEGORY-ERROR-0002";

    /**
     * Starting error code Course
     * */
    public static final String COURSE_ERROR_NOT_FOUND = "COURSE-ERROR-0000";
    public static final String COURSE_ERROR_EXIST = "COURSE-ERROR-0001";
    public static final String COURSE_ERROR_NOT_DELETE = "COURSE-ERROR-0002";
    public static final String COURSE_ERROR_APPROVE = "COURSE-ERROR-0003";
    public static final String COURSE_ERROR_NOT_AUTHORIZED = "COURSE-ERROR-0004";
    public static final String COURSE_ERROR_NOT_ACTIVE = "COURSE-ERROR-0005";

    /**
     * Starting error code Lesson
     * */
    public static final String LESSON_ERROR_NOT_FOUND = "LESSON-ERROR-0000";
    public static final String LESSON_ERROR_EXIST = "LESSON-ERROR-0001";
    public static final String LESSON_ERROR_POSITION = "LESSON-ERROR-0002";
    public static final String LESSON_ERROR_SAME_CHAPTER = "LESSON-ERROR-0003";
    public static final String LESSON_ERROR_CREATE = "LESSON-ERROR-0004";
    public static final String LESSON_ERROR_UPDATE = "LESSON-ERROR-0005";

    /**
     * Starting error code LessonQuestion
     * */
    public static final String CHAPTER_ERROR_NOT_FOUND = "CHAPTER-ERROR-0000";
    public static final String CHAPTER_ERROR_EXIST = "CHAPTER-ERROR-0001";

    /**
     * Starting error code LessonQuestion
     * */
    public static final String LESSON_QUESTION_ERROR_NOT_FOUND = "LESSON_QUESTION-ERROR-0000";
    public static final String LESSON_QUESTION_ERROR_EXIST = "LESSON_QUESTION-ERROR-0001";
    public static final String LESSON_QUESTION_ERROR_NOT_CREATE = "LESSON_QUESTION-ERROR-0002";
    public static final String LESSON_QUESTION_ERROR_NOT_UPDATE = "LESSON_QUESTION-ERROR-0003";
    public static final String LESSON_QUESTION_ERROR_NOT_CREATE_OPTION = "LESSON_QUESTION-ERROR-0004";
    public static final String LESSON_QUESTION_ERROR_OPTION_NOT_NULL = "LESSON_QUESTION-ERROR-0005";

    /**
     * Starting error code CourseEnrollment
     * */
    public static final String COURSE_ENROLLMENT_ERROR_NOT_FOUND = "COURSE-ENROLLMENT-ERROR-0000";
    public static final String COURSE_ENROLLMENT_ERROR_NOT_CREATE = "COURSE-ENROLLMENT-ERROR-0001";

    /**
     * Starting error code LessonProgress
     * */
    public static final String LESSON_PROGRESS_ERROR_NOT_FOUND = "LESSON-PROGRESS-ERROR-0000";
    public static final String LESSON_PROGRESS_ERROR_EXIST = "LESSON-PROGRESS-ERROR-0001";
    public static final String LESSON_PROGRESS_ERROR_NOT_COMPLETED = "LESSON-PROGRESS-ERROR-0002";
    public static final String LESSON_PROGRESS_ERROR_NOT_CREATE = "LESSON-PROGRESS-ERROR-0003";
    public static final String LESSON_PROGRESS_ERROR_FAIL = "LESSON-PROGRESS-ERROR-0004";

    /**
     * Starting error code CorrectAnswer
     * */
    public static final String STUDENT_LESSON_QUESTION_PROGRESS_ERROR_NOT_FOUND = "STUDENT-LESSON-QUESTION-PROGRESS-ERROR-0000";
    public static final String STUDENT_LESSON_QUESTION_PROGRESS_ERROR_EXIST = "STUDENT-LESSON-QUESTION-PROGRESS-ERROR-0001";
    public static final String STUDENT_LESSON_QUESTION_PROGRESS_ERROR_NOT_CREATE = "STUDENT-LESSON-QUESTION-PROGRESS-ERROR-0002";

    /**
     * Starting error code Feedback
     * */
    public static final String REVIEW_ERROR_NOT_FOUND = "REVIEW-ERROR-0000";
    public static final String REVIEW_ERROR_EXIST = "REVIEW-ERROR-0001";
    public static final String REVIEW_ERROR_NOT_AUTHORIZE = "REVIEW-ERROR-0002";
    public static final String REVIEW_ERROR_NOT_CREATE = "REVIEW-ERROR-0003";

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
    public static final String REVIEW_SUBMISSION_ERROR_CREATE = "REVIEW-SUBMISSION-ERROR-0002";

    /**
     * Starting error code Notification
     * */
    public static final String NOTIFICATION_ERROR_NOT_FOUND = "NOTIFICATION-ERROR-0000";
    public static final String NOTIFICATION_ERROR_EXIST = "NOTIFICATION-ERROR-0001";

    /**
     * Starting error code Correct answer
     * */
    public static final String CORRECT_ANSWER_ERROR_NOT_CREATE = "CORRECT-ANSWER-ERROR-0000";
}
