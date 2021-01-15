package io.springboot.survey.utils;


import java.util.Arrays;
import java.util.List;

import static io.springboot.survey.utils.Constants.CommonConstant.*;


public class Constants {

    private Constants() {
        //do nothing
    }
    public static class CorsConfigurationConstant {
        private CorsConfigurationConstant() {
            //Do nothing
        }
        public static final String ALLOW_METHOD = "Access-Control-Allow-Methods";
        public static final String ALLOW_HEADER = "Access-Control-Allow-Headers";
        public static final String MAX_AGE = "Access-Control-Max-Age";
        public static final String ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";
        public static final String EXPOSE_HEADER = "Access-Control-Expose-Headers";
        public static final String ASTERISK = "*";
        public static final String ALL_MAPPING = "POST, PUT, GET, OPTIONS, DELETE";
        public static final String ALL_HEADERS = "Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With,observe";
        public static final String MAX_AGE_3600 = "3600";
        public static final String BOOLEAN_TRUE = "true";
        public static final String HEADER_AUTHORIZATION = "Authorization";
        public static final String HEADER_RESPONSE_TYPE = "responseType";
        public static final String HEADER_OBSERVE = "observe";
        public static final String OPTION_MAPPING = "OPTIONS";
        public static final String MAPPINGS = "POST,GET,DELETE,PUT";
        public static final String OPTION_HEADER = EXPOSE_HEADER + "Authorization, content-type," +
                USER_ID + "ROLE" + "access-control-request-headers,access-control-request-method,accept,origin,authorization,x-requested-with,responseType,observe";
        public static final String CORS_PATTERN = "/**";
        public static final String ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    }

    public static class SurveyModuleConstants
    { private SurveyModuleConstants() {
            //Do Nothing
        }

        public static final String EMPLOYEE_DETAILS = "employeeDetails";
        public static final String SURVEY_NOT_FOUND_DEBUG = "Survey {} not found";
        public static final String NO_SURVEY_FOUND = "No io.springboot.survey(s) found";
        public static final String SURVEY_DESCRIPTION = "surveyDescription";
        public static final String SURVEY_PENDING_COUNT = "surveyPendingCount";
        public static final String SURVEY_TAKEN_COUNT = "surveyTakenCount";
        public static final String NUMBER_OF_QUESTION = "noOfQuestion";
        public static final String EXPIRATION_DATE ="expirationDate";
        public static final String PENDING_TAKEN_RESPONSE = "pendingTakenResponses";
        public static final String RESPONSE_LIST="responsesList";
        public static final String SURVEY_TAKEN ="surveyTaken" ;
        public static final String SURVEY_LEFT = "surveyLeft";
        public static final String SURVEY_ID = "surveyId";
        public static final String OTHERS = "Others" ;
        public static final String SURVEY_THIS_WEEK_BY_ME = "surveyCreatedByMeThisWeek";
        public static final String TEAM_MANAGED_BY_ME = "teamManagedByMe";
        public static final String TOTAL_SURVEY_CREATED = "totalSurveyCreated";
        public static final String SURVEY_CREATED_THIS_WEEK = "surveyCreatedThisWeek";
        public static final String SURVEY_CREATED_BY_ME = "surveyCreatedByMe";
        public static final String AVERAGE_RESPONSE_PER_SURVEY = "averageUserResponsePerSurvey";
        public static final String TEAM_ID="teamId";
        public static final String GENERAL_COUNT ="generalCount";
        public static final String DAY_FILTER="Day(s)";
        public static final String WEEK_FILTER="Week(s)";
        public static final String MONTH_FILTER="Month(s)";
        public static final String YEAR_FILTER="Year(s)";
        public static final String UNEXPECTED_VALUE = "Unexpected Value";

    }

    public static class FilterConstants
    {
        private FilterConstants() {
            //Do Nothing
        }
        public static final String STATUS_RESPONSE_FILTER ="statusResponseFilter";
        public static final String SURVEY_PAGINATION_FILTER ="surveyPaginationFilter";
        public static final String ALL_RESPONSE_FILTER = "AllResponseFilter" ;
        public static final String PAGINATION_FILTER = "PaginationFilter";
    }

    public static class TeamConstants{
        private TeamConstants(){
            //Do nothing
        }
        public static final String TEAM_NOT_FOUND_DEBUG = "Team {} not found";
        public static final String DELETE_TEAM_ERROR = "Error occurred while deleting team :: ";
        public static final String CREATOR_NAME = "creatorName";
        public static final String MANAGER_NAME = "managerName";
        public static final String MANAGER_EMAIL = "managerEmail";
        public static final String MEMBER_COUNT = "memberCount";
        public static final String PROJECT_STATUS = "projectStatus";
        public static final String PROJECT_NAME = "projectName";
        public static final String TEAM_MEMBER_NOT_FOUND = "Team members not found.";
        public static final String TEAM_NOT_FOUND="Team not found.";
        public static final String TEAM ="team";
        public static final String ACTIVE = "active";
        public static final String MANAGED_TEAM_NOT_FOUND ="No Managed Team Found";
        public static final String CREATED_TEAM_NOT_FOUND ="No Created Team Found";
        public static final String TEAM_CREATED="A new team is created.";
        public static final String TEAM_ALREADY_CREATED="Team already exists.";
        public static final String ALREADY_EXIST_TEAM = " already exists in the team.";
        public static final String STATUS_UPDATED="Status is updated successfully.";
        public static final String STATUS="status";
        public static final String PROJECT="projectName";
        public static final String MANAGER_ID="managerId";
    }


    public static class SchedulingConstants {
        private SchedulingConstants() {
            //Do nothing
        }
        public static final String ERROR_REMINDING_EMAIL = "Error occurred while sending reminding-mail :: ";
        public static final String SLASH = "/";
        public static final String SLASH_NULL = "/null";
        public static final String EXPIRY_HOURS = "expiryHours";
        public static final String EMAIL_JOBS = "email-jobs";
        public static final String SEND_EMAIL_JOBS = "Send Email Job";
        public static final String WEEKLY_EMAIL_TRIGGER = "weekly-email-triggers";
        public static final String EMAIL_WEEKLY ="Email scheduled weekly" ;
        public static final String MONTHLY_EMAIL_TRIGGER = "monthly-email-triggers";
        public static final String EMAIL_MONTHLY ="Email scheduled monthly" ;
        public static final String QUARTERLY_EMAIL_TRIGGER ="quarterly-email-triggers";
        public static final String EMAIL_QUARTERLY = "Email scheduled quarterly";
        public static final String CRON_QUARTERLY = "0 0/1 * 1/1 * ? *";
        public static final String YEARLY_EMAIL_TRIGGER = "yearly-email-triggers";
        public static final String EMAIL_YEARLY ="Email scheduled yearly";
        public static final String CRON_YEARLY = "0 0/2 * 1/1 * ? *";
        public static final String EMAIL_TRIGGER = "email-triggers";
        public static final String SEND_EMAIL_ONCE = "Send Email once";
        public static final String LIST_OF_MAILS ="listOfMails" ;
        public static final String ONCE = "Once";
        public static final String WEEKLY ="Weekly";
        public static final String MONTHLY ="Monthly";
        public static final String QUARTERLY ="Quarterly";
        public static final String YEARLY ="Yearly";
        public static final String EMAIL_SCHEDULED_SUCCESSFULLY ="Email Scheduled Successfully!" ;
        public static final String ERROR_EMAIL_SCHEDULING ="Error scheduling email. Please try later!";
        public static final String LIST_OF_TEAM_NAME ="listOfTeamName" ;
        public static final String REMINDER ="reminder";
        public static final String SURVEY_LINK_JOB = "surveyLink";
        public static final String NEW_SURVEY_MESSAGE = "A New Survey has arrived!";
        public static final String REMINDER_SURVEY_MESSAGE ="Reminder to take io.springboot.survey";
        public static final String EMAIL_TEMPLATE = "EmailTemplate";
        public static final String REMINDER_EMAIL = "ReminderEmail";
        public static final String GREETING = "greeting";
        public static final String HI = "Hi ";
        public static final String SURVEY_NAME_EMAIL = "survey_name" ;
        public static final String ORG_NAME ="org_name" ;
        public static final String ORG_EMAIL ="org_email";
        public static final String TEAM_NAME_EMAIL = "team_name";
        public static final String SURVEY_LINK = "survey_link";
    }

    public static class PrivilegesConstant
    {
        private PrivilegesConstant() {
        //Do nothing
        }
        protected static final List<String> EMPLOYEE_MANAGEMENT=Arrays.asList("employee","employees");
        protected static final List<String> SURVEY_MODULE=Arrays.asList("survey","surveys");
        protected static final List<String> TEAM_MANAGEMENT=Arrays.asList("team","teams");
        protected static final List<String> TEMPLATE_MODULE=Arrays.asList("template","templates");
        public static final String TEMPLATE_REPORT="template-report";
        public static final String TAKE_SURVEY= "user";
        public static final String VIEW_TEAM= "managed-teams";
        protected static final List<String> EDIT_ROLE=Arrays.asList("role","roles");
        public static final String COMBINED_TEAM= "team-members";
        public static final String EMPLOYEE_MANAGEMENT_PRIVILEGE="employeeManagement";
        public static final String TEAM_MANAGEMENT_PRIVILEGE="teamManagement";
        public static final String SURVEY_MODULE_PRIVILEGE="surveyModule";
        public static final String TEMPLATE_MODULE_PRIVILEGE="templateModule";
        public static final String TAKE_SURVEY_PRIVILEGE="takeSurvey";
        public static final String EDIT_ROLE_PRIVILEGE="editRole";
        public static final String EMPLOYEE_MANAGEMENT_TEXT="Employee Management";
        public static final String TEAM_MANAGEMENT_TEXT="Team Management";
        public static final String SURVEY_MODULE_TEXT="Survey Module";
        public static final String TEMPLATE_MODULE_TEXT="Template Module";
        public static final String TAKE_SURVEY_TEXT="Take Survey";
        public static final String EDIT_ROLE_TEXT="Edit Role";
        public static final String VIEW_TEAM_TEXT="View Team";
        public static final String TEMPLATE_REPORT_TEXT="Template Report";

        public static List<String> getEmployeeManagement() {
            return EMPLOYEE_MANAGEMENT;
        }

        public static List<String> getSurveyModule() {
            return SURVEY_MODULE;
        }

        public static List<String> getTeamManagement() {
            return TEAM_MANAGEMENT;
        }

        public static List<String> getTemplateModule() {
            return TEMPLATE_MODULE;
        }

        public static List<String> getEditRole() {
            return EDIT_ROLE;
        }
    }

    public static class NullEmptyConstant
    {
        private NullEmptyConstant() {
            //Do Nothing
        }
        public static final String FIELD_CANNOT_BE_NULL="Field cannot be null.";
        public static final String SURVEY_DATA_LIST_NOT_NULL = "Survey Data List cannot be null.";
        public static final String MAIL_LIST_NOT_NULL = "Mail list cannot be empty.";
        public static final String ROLE_NAME_NOT_NULL="Role cannot be null.";
        public static final String TEAM_NAME_NOT_NULL = "Team name cannot be null.";
        public static final String MEMBER_LIST_NOT_NULL ="Member list cannot be empty.";
        public static final String DATE_TIME_CONDITION = "Date Time must be after current time.";
        public static final String FREQUENCY_NOT_NULL ="Frequency cannot be null.";
        public static final String SENDER_MAIL_NOT_NULL = "Sender's email cannot be null.";
        public static final String OTP_NOT_NULL ="Otp cannot be null." ;
        public static final String NAME_NOT_NULL ="Name cannot be null.";
        public static final String LINK_NOT_NULL="link cannot be null.";
        public static final String JWT_NULL ="JWT Token is null.";
        public static final String PROJECT_NAME_NOT_NULL = "Project name cannot be null.";
        public static final String STATUS_NOT_NULL = "Status cannot be null.";
        public static final String MANGER_EMAIL_NOT_NULL = "Manager's email cannot be null.";
        public static final String CREATOR_EMAIL_NOT_NULL = "Creator's email cannot be null.";
        public static final String EMPLOYEE_LIST_NOT_NULL = "Employee details list cannot be empty.";
        public static final String EMAIL_LIST_NOT_NULL= "Email list cannot be empty.";
        public static final String TEMPLATE_NAME_NOT_NULL = "Template name cannot be null.";
        public static final String EMAIL_NOT_NULL="Email cannot be null.";
        public static final String PAGE_NOT_NULL="Page cannot be null.";
        public static final String SURVEY_NOT_NULL = "Survey name cannot be null.";
        public static final String PRIVILEGES_LIST_EMPTY="Privileges list cannot be empty.";
    }

    public static class ValidationConstant
    {
        private ValidationConstant() {
        //Do Nothing
        }
        public static final String PAGE_SIZE_8 = "8";
        public static final String PAGE_SIZE_4 = "4";
        public static final String CREATION_DATE = "creationDate";
        public static final String CREATOR_EMAIL = "creatorEmail";
        public static final String SURVEY_NAME ="surveyName" ;
        public static final String EMAIL="email";
        public static final String HASH_MAP_LIST = "hashMapList";
        public static final String TEAM_MODEL_LIST ="teamModelList";
        public static final String PAGE_SIZE_10="10";
        public static final String TEMPLATE_RESPONSE_LIST = "templateResponsesList";
        public static final String INVALID_CREATOR_EMAIL = "Invalid Creator Email";
        public static final String INVALID_EMAIL="Invalid Email";
        public static final String SENDER_EMAIL = "senderEmail";
        public static final String ROLE_NAME="roleName";
        public static final String ROLE="role";
        public static final String PAGE="page";
        public static final String TEMPLATE_NAME = "templateName";
        public static final String FILE = "file";
        public static final String LINK = "link";
        public static final String TEAM_NAME = "teamName";
        public static final String NAME = "name";
        public static final String MOCK_TOKEN="Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ2aXNoYWwuamhhQG5pbmVsZWFwcy5jb20iLCJleHAiOjE1OTY3OTExOTEsImlhdCI6MTU5NjcyNjM4OX0.LcwGqTQKErkgBE0-XxfCfngWNJDjAMmhIrpr31sCVJI";
        public static final String INVALID_FILE_TYPE ="Invalid image file.";
        public static final String ALLOWED_FILE_TYPE = "Only PNG,JPG,PDF are allowed.";

    }


    public static class TemplateModuleConstant {
        private TemplateModuleConstant() {
        }
        public static final byte BYTE_ONE =1;
        public static final byte BYTE_MINUS_ONE =-1;
        public static final byte BYTE_ZERO =0;
        public static final String TOTAL_TEMPLATE_RESPONSE = "totalTemplateResponses";
        public static final String SURVEY_DESC ="Survey Description";
        public static final String PERCENTAGE ="%";
    }


    public static class CommonConstant{



        private CommonConstant() {
        }
        public static final String TEMPLATE_NOT_DELETED ="Template cannot be deleted as it is already used to create io.springboot.survey" ;

        public static final String GET_TEMPLATE_FILE = "File";
        public static final String GET_TEMPLATE_TEXT = "Text";
        public static final String GET_TEMPLATE_RATING = "Rating";
        public static final String QUESTION_RESPONSE = "questionResponses";
        public static final String TOTAL_SURVEY = "totalSurveys";
        public static final String N_A = "N/A";
        public static final String ASSIGNED_TO ="assignedTo";
        public static final String ASSIGNED_BY_EMAIL ="assignedByEmail";
        public static final String ASSIGNED_BY = "assignedBy";
        public static final String QUES_TYPE_RADIO="radio";
        public static final String QUES_TYPE_CHECKBOX="check";
        public static final String QUES_TYPE_RATING="rating";
        public static final String QUES_TYPE_TEXT="text";
        public static final String QUES_TYPE_FILE="file";
        public static final String USER_NAME="userName";
        public static final String USER_EMAIL="userEmail";
        public static final String ORG_ID="orgId";
        public static final String GENDER="gender";
        public static final String DESIGNATION="designation";
        public static final String PAGE_REQUIRED="pageRequired";
        public static final String ROLE_ID="roleId";
        public static final String USER_ID="userId";
        public static final String HR_DASHBOARD_GRAPH = "hrDashboardGraph";
        public static final String HR= "HR";
        public static final String MANAGER= "Manager";
        public static final String EMPLOYEE="Employee";
        public static final String TEAM_COUNT = "teamCount";
        public static final String EMPTY_STRING = "";
        public static final String STRING_COMMA = ",";
        public static final String INVALID_PAGE_SIZE = "invalid page size: ";
        public static final String BACK_SLASH = "/";
        public static final String NULL_POINTER_EXCEPTION = "Null Pointer Exception";
        public static final String LOGGER_OTP_MESSAGE = "OTP : {}";
        public static final String OTP_IS = "Your OTP is - ";
        public static final String OTP = "OTP";
        public static final String STRING_FORMAT = "%04d";
        public static final Integer SHORT_BOUND = 10000;
        public static final String TEMPLATE_NOT_USED = "Template Have Not Been Used To Create Survey";
        public static final String NULL = "null";
    }

    public static  class AuthorizationModuleConstant{
        private AuthorizationModuleConstant() {
        }
        public static final String JWT="Authorization";
        public static final String INVALID_URI ="Invalid URI";
        protected static final String [] EXCLUDED_URI={"/surveyManagement/v1/login/email", "/surveyManagement/v1/login/email/otp"
                ,"/surveyManagement/v1/login/google","/surveyManagement/v1/all-roles","/surveyManagement/v1/employees/filter/role",
                "/surveyManagement/v1/filter/info","/surveyManagement/v1/decode-link","/surveyManagement/v1/template/report/filter"
                ,"/swagger-ui.html","/error","/csrf","/surveyManagement/v1/employee/role/privileges",
                "/surveyManagement/v1/io.springboot.survey-link","/surveyManagement/v1/io.springboot.survey-preview","/surveyManagement/v1/user/submit-io.springboot.survey","/surveyManagement/v1/test"};
        protected static final List<String> URI_ARRAY= Arrays.asList("hrDashboard","managerDashboard", HR_DASHBOARD_GRAPH
                , TOTAL_SURVEY,"surveysThisWeek","mySurveysThisWeek");
         protected static final List<String> HR_URI= Arrays.asList("hrDashboard",HR_DASHBOARD_GRAPH,
                                                    TOTAL_SURVEY,"surveysThisWeek");
        protected static final List<String> MANAGER_URI= Arrays.asList("managerDashboard",HR_DASHBOARD_GRAPH,
                                                        "mySurveysThisWeek");

        public static List<String> getUriArray() {
            return URI_ARRAY;
        }

        public static List<String> getHrUri() {
            return HR_URI;
        }

        public static List<String> getManagerUri() {
            return MANAGER_URI;
        }

        public static String[] getExcludedUri() {
            return EXCLUDED_URI;
        }
        public static final String BEARER = "Bearer ";
        public static final String SECRET_KEY = "surveymanagement";
        public static final String GET_MAPPING = "GET";
    }

    public static class SwaggerConstant{
        private SwaggerConstant() {
        }
        public static final String HEADER = "header";
        public static final String TYPE_STRING = "string";
        public static final String SURVEY_MANAGEMENT_TOOL = "Survey Management Tool";
        public static final String APPLICATION_VERSION ="2.0" ;
        public static final String APPLICATION_DESCRIPTION = "List Of All The APIs Of Survey Management Tool Through Swagger UI";
    }
    public static class ApiResponseConstant{
        private ApiResponseConstant() {
        }

        public static final String FORBIDDEN_MESSAGE = "Forbidden";
        public static final String CREATED = "Created";
        public static final String CONFLICT_MESSAGE = "Conflict Error";
        public static final String UNAUTHORIZED_MESSAGE = "Authorization Error";
        public static final String OK_MESSAGE="ok";
        public static final String RESOURCE_NOT_FOUND ="Resource Not Found";
        public static final String BAD_REQUEST ="Bad Request";
        public static final String UNPROCESSABLE_ENTITY_MESSAGE="Input Parameter Is Null/Empty Or Invalid";
        public static final String INTERNAL_SERVER_ERROR ="Internal Server Error";
    }


    public static class ErrorMessageConstant{
        private ErrorMessageConstant() {
        }
        public static final String ERROR_ADDING_USER = "Error occurred while adding user :: ";
        public static final String ERROR_GOOGLE_LOGIN = "Error occurred while google login :: ";
        public static final String ERROR_SENDING_EMAIL = "Error occurred while sending email ::";
        public static final String ERROR_DELETE_USER = "Error occurred while deleting user :: ";
        public static final String ERROR_IMPORTING_USER = "Error occurred while importing user :: ";
        public static final String WRONG_INPUT = "Wrong input.";
        public static final String NO_SURVEY_CREATED = "No io.springboot.survey has been created using this template.";
        public static final String INVALID_JWT_BEARER_FORMAT = "Bearer is either missing or in wrong format in JWT Token";
        public static final String ROLE_ALREADY_EXIST = "Role already exist in the database";
        public static final String USER_WITH_EMAILS = "User(s) with email(s) ";
        public static final String NOT_FOUND ="Not Found" ;
        public static final String NO_ASSIGNED_USER_SURVEY = "No user found";
        public static final String NO_ACTIVE_SURVEY = "No active io.springboot.survey found.";
        public static final String NO_SURVEYS_FOUND = "No io.springboot.survey found.";
        public static final String TEMPLATE_NOT_FOUND = "No template found.";
        public static final String NO_PENDING_SURVEY ="No pending io.springboot.survey found.";
        public static final String NO_PENDING_USER_SURVEY ="Everyone has taken the io.springboot.survey.";
        public static final String NO_TAKEN_USER_SURVEY ="No one has taken the io.springboot.survey.";
        public static final String NO_TAKEN_SURVEY = "No taken io.springboot.survey information found.";
        public static final String NO_TEMPLATE_FOUND ="Template does not exist.";
        public static final String NO_TEMPLATE_USED ="No template has been used to create io.springboot.survey more than once.";
        public static final String ROLE_NOT_FOUND = "Role does not exist.";
        public static final String ERROR_MESSAGE ="Error";
        public static final String ROLE_CANNOT_BE_DELETED="Default roles cannot be deleted.";
        public static final String USER_UNAUTHORIZED="USER NOT AUTHORIZED";
        public static final String SURVEY_NOT_FOUND="Survey does not exist.";
        public static final String SUCCESS = "Success";
        public static final String USER_ARCHIVED="User Archived";
        public static final String LOGIN_SUCCESS = "Login Successful";
        public static final String EMAIL_RESPONSE ="User(s) with email(s) ";
        public static final String ID_RESPONSE ="User(s) with same Nineleaps Id ";
        public static final String ADDED="Added Successfully";
        public static final String TEMPLATE_ALREADY_EXIST="Template of the same name already exists.";
        public static final String RESPONSE_STORED="Response is submitted successfully.";
        public static final String ARCHIVED="Successfully Archived";
        public static final String UNARCHIVED="Successfully Unarchived";
        public static final String SAME_SURVEY="Survey of same name exists.";
        public static final String OTP_VALID="Otp is valid.";
        public static final String PRIVILEGES_ADDED ="Privileges has been added successfully.";
        public static final String ROLE_ADDED="New role is added successfully";
        public static final String USER_PRESENT="User is a member of the organization.";
        public static final String USER_NOT_PRESENT="User is not a member of the organization.";
        public static final String ALREADY_EXIST=" already exists.";
        public static final String ROLE_UPDATED="Role is updated successfully.";
        public static final String NOT_DELETED=" not deleted.";
        public static final String USER_ACTIVATED="Activated Successfully";
        public static final String FILE_UPLOADED="File is uploaded successfully.";
        public static final String OTP_INVALID = "Invalid Otp";
    }

    public  static class LoggerConstants {
        private LoggerConstants() {
        }
        public static final String STARTING_METHOD_EXECUTION = "Starting method execution";
        public static final String EXITING_METHOD_EXECUTION = "Exiting method execution";
        public static final String RESPONSE_SAVED = "Response saved : {}";

    }
    public static class ModelConstraintMessage{

        private ModelConstraintMessage() {
        }
        public static final String QUESTION_TYPE_NAME_NULL="Question type name cannot be null";
        public static final String QUESTION_TYPE_NAME_EMPTY="Question type name cannot be empty";
        public  static final String SURVEY_ID_NULL = "Survey id cannot be null";
        public  static final String QUESTION_TYPE_ID_NULL = "Question type id cannot be null";
        public static final String QUESTION_TEXT_NULL ="Question text cannot be null";
        public static final String QUESTION_TEXT_EMPTY ="Question text cannot be empty";
        public static final String QUESTION_ID_NULL ="Question id cannot be null";
        public static final String ANSWER_TEXT_NULL = "Answer text cannot be null";
        public static final String ANSWER_TEXT_EMPTY = "Answer text cannot be empty";
        public static final String RESPONSE_ID_CANNOT_BE_NULL = "Response id cannot be null";
        public static final String ANSWER_ID_NULL ="Answer id cannot be null";
        public static final String FILE_ID_NULL = "File id cannot be null";
        public static final String USER_ID_NULL = "User id cannot be null";
        public static final String ROLE_NAME_NULL = "Role name cannot be null";
        public static final String ROLE_NAME_EMPTY = "Role name cannot be empty";
        public static final String SURVEY_NAME_NULL ="Survey name cannot be null";
        public static final String SURVEY_NAME_EMPTY ="Survey name cannot be empty";
        public static final String SURVEY_DESC_NULL ="Survey description cannot be null";
        public static final String SURVEY_DESC_EMPTY ="Survey description cannot be empty";
        public static final String TEMPLATE_ID_NULL ="Template id cannot be null" ;
        public static final String LINK_NULL = "Link cannot be null";
        public static final String LINK_EMPTY = "Link cannot be empty";
        public static final String TEAM_ID_NULL ="Team id cannot be null";
        public static final String TEAM_NAME_NULL = "Team name cannot be null";
        public static final String TEAM_NAME_EMPTY =  "Team name cannot be empty" ;
        public static final String MANAGER_ID_NULL ="Manager id cannot be null";
        public static final String PROJECT_NAME_NULL ="Project name cannot be null" ;
        public static final String PROJECT_NAME_EMPTY ="Project name cannot be empty";
        public static final String STATUS_NULL = "Status cannot be null";
        public static final String STATUS_EMPTY = "Status cannot be empty";
        public static final String TEMPLATE_NAME_NULL ="Template name cannot be null";
        public static final String TEMPLATE_NAME_EMPTY ="Template name cannot be empty";
        public static final String TEMPLATE_DESC_NULL ="Template description cannot be null";
        public static final String TEMPLATE_DESC_EMPTY ="Template description cannot be empty";
        public  static final String ROLE_ID_NULL = "Role id cannot be null";
        public static final String USER_NAME_NULL ="User name cannot be null";
        public static final String USER_NAME_EMPTY ="User name cannot be empty";
        public static final String USER_EMAIL_NULL ="User email cannot be null";
        public static final String USER_EMAIL_EMPTY ="User email cannot be empty";
        public  static final String EMPLOYEE_ID_NULL = "Employee id cannot be null";
        public  static final String EMPLOYEE_ID_EMPTY = "Employee id cannot be empty";
        public  static final String EMPLOYEE_GENDER_NULL = "Employee gender cannot be null";
        public  static final String EMPLOYEE_GENDER_EMPTY = "Employee gender cannot be empty";
        public  static final String EMPLOYEE_DESIGNATION_NULL = "Employee designation cannot be null";
        public  static final String EMPLOYEE_DESIGNATION_EMPTY = "Employee designation cannot be empty";
    }


}

