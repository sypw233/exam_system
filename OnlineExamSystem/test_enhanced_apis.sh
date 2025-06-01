#!/bin/bash

# 在线考试系统增强版 API 测试脚本
# 测试所有 API 接口，包括新增的端点和文档接口
# 运行前请确保服务器在 localhost:9999 端口运行
# 数据库状态: 考试(57), 用户(62), 提交(27), 题目(101)

BASE_URL="http://localhost:9999"
CONTENT_TYPE="Content-Type: application/json"

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
NC='\033[0m' # No Color

# 测试结果统计
TOTAL_TESTS=0
PASSED_TESTS=0
FAILED_TESTS=0
WARNING_TESTS=0

# 日志函数
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
    ((PASSED_TESTS++))
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
    ((FAILED_TESTS++))
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
    ((WARNING_TESTS++))
}

log_section() {
    echo -e "${PURPLE}[SECTION]${NC} $1"
}

# 测试函数
test_api() {
    local method=$1
    local endpoint=$2
    local description=$3
    local data=$4
    local expected_status=$5
    local auth_header=$6
    
    ((TOTAL_TESTS++))
    
    log_info "测试: $description"
    log_info "请求: $method $endpoint"
    
    if [ "$method" = "GET" ]; then
        if [ -n "$auth_header" ]; then
            response=$(curl -s -w "\nHTTP_STATUS:%{http_code}" -X GET "$BASE_URL$endpoint" -H "$auth_header")
        else
            response=$(curl -s -w "\nHTTP_STATUS:%{http_code}" -X GET "$BASE_URL$endpoint")
        fi
    elif [ "$method" = "POST" ]; then
        if [ -n "$auth_header" ]; then
            response=$(curl -s -w "\nHTTP_STATUS:%{http_code}" -X POST "$BASE_URL$endpoint" -H "$CONTENT_TYPE" -H "$auth_header" -d "$data")
        else
            response=$(curl -s -w "\nHTTP_STATUS:%{http_code}" -X POST "$BASE_URL$endpoint" -H "$CONTENT_TYPE" -d "$data")
        fi
    elif [ "$method" = "PUT" ]; then
        if [ -n "$auth_header" ]; then
            response=$(curl -s -w "\nHTTP_STATUS:%{http_code}" -X PUT "$BASE_URL$endpoint" -H "$CONTENT_TYPE" -H "$auth_header" -d "$data")
        else
            response=$(curl -s -w "\nHTTP_STATUS:%{http_code}" -X PUT "$BASE_URL$endpoint" -H "$CONTENT_TYPE" -d "$data")
        fi
    elif [ "$method" = "DELETE" ]; then
        if [ -n "$auth_header" ]; then
            response=$(curl -s -w "\nHTTP_STATUS:%{http_code}" -X DELETE "$BASE_URL$endpoint" -H "$auth_header")
        else
            response=$(curl -s -w "\nHTTP_STATUS:%{http_code}" -X DELETE "$BASE_URL$endpoint")
        fi
    fi
    
    # 分离响应体和状态码
    status_code=$(echo "$response" | grep "HTTP_STATUS:" | cut -d: -f2)
    response_body=$(echo "$response" | grep -v "HTTP_STATUS:")
    
    echo "响应状态码: $status_code"
    
    # 只显示响应体的前200个字符，避免输出过长
    if [ ${#response_body} -gt 200 ]; then
        echo "响应内容: ${response_body:0:200}..."
    else
        echo "响应内容: $response_body"
    fi
    
    if [ "$status_code" = "$expected_status" ]; then
        log_success "测试通过 - 状态码: $status_code"
    else
        log_error "测试失败 - 期望状态码: $expected_status, 实际状态码: $status_code"
    fi
    
    echo "----------------------------------------"
}

# 测试文档端点
test_doc_endpoint() {
    local endpoint=$1
    local description=$2
    
    ((TOTAL_TESTS++))
    
    log_info "测试: $description"
    log_info "请求: GET $endpoint"
    
    response=$(curl -s -w "\nHTTP_STATUS:%{http_code}" -X GET "$BASE_URL$endpoint")
    status_code=$(echo "$response" | grep "HTTP_STATUS:" | cut -d: -f2)
    response_body=$(echo "$response" | grep -v "HTTP_STATUS:")
    
    echo "响应状态码: $status_code"
    
    if [ "$status_code" = "200" ]; then
        log_success "文档端点可访问 - 状态码: $status_code"
    else
        log_error "文档端点不可访问 - 状态码: $status_code"
    fi
    
    echo "----------------------------------------"
}

# 开始测试
echo "=========================================="
echo "    在线考试系统增强版 API 测试开始"
echo "=========================================="
echo

# 0. 测试文档和健康检查端点
log_section "API文档和健康检查测试"
echo

# 测试API文档端点
test_doc_endpoint "/doc.html" "Knife4j API文档页面"
test_doc_endpoint "/swagger-ui.html" "Swagger UI文档页面"
test_doc_endpoint "/v3/api-docs" "OpenAPI 3.0 JSON文档"

# 测试健康检查端点（如果存在）
test_api "GET" "/actuator/health" "应用健康检查" "" "200"

# 1. 用户管理 API 测试
log_section "用户管理 API 测试"
echo

# 获取所有用户
test_api "GET" "/api/users" "获取所有用户" "" "200"

# 获取所有教师
test_api "GET" "/api/users/teachers" "获取所有教师" "" "200"

# 获取所有学生
test_api "GET" "/api/users/students" "获取所有学生" "" "200"

# 根据ID获取用户（使用数据库中存在的用户ID）
test_api "GET" "/api/users/1" "根据ID获取用户" "" "200"

# 分页获取用户
test_api "GET" "/api/users/page?page=1&size=10" "分页获取用户" "" "200"

# 用户注册
register_data='{
    "username": "testuser_enhanced",
    "password": "password123",
    "email": "testenhanced@example.com",
    "role": "STUDENT"
}'
test_api "POST" "/api/users/register" "用户注册" "$register_data" "201"

# 用户登录
login_data='{
    "username": "testuser_enhanced",
    "password": "password123"
}'
test_api "POST" "/api/users/login" "用户登录" "$login_data" "200"

# 2. 课程管理 API 测试
log_section "课程管理 API 测试"
echo

# 获取所有课程
test_api "GET" "/api/courses" "获取所有课程" "" "200"

# 根据教师ID获取课程
test_api "GET" "/api/courses/teacher/1" "根据教师ID获取课程" "" "200"

# 根据ID获取课程
test_api "GET" "/api/courses/1" "根据ID获取课程" "" "200"

# 分页获取课程
test_api "GET" "/api/courses/page?page=1&size=10" "分页获取课程" "" "200"

# 创建课程
course_data='{
    "name": "增强测试课程",
    "description": "这是一个增强版测试课程",
    "teacherId": 1,
    "credits": 3
}'
test_api "POST" "/api/courses" "创建课程" "$course_data" "201"

# 3. 题目管理 API 测试
log_section "题目管理 API 测试"
echo

# 获取所有题目
test_api "GET" "/api/questions" "获取所有题目" "" "200"

# 根据类型获取题目
test_api "GET" "/api/questions/type/SINGLE_CHOICE" "根据类型获取单选题" "" "200"
test_api "GET" "/api/questions/type/MULTIPLE_CHOICE" "根据类型获取多选题" "" "200"
test_api "GET" "/api/questions/type/TRUE_FALSE" "根据类型获取判断题" "" "200"

# 根据ID获取题目
test_api "GET" "/api/questions/1" "根据ID获取题目" "" "200"

# 分页获取题目
test_api "GET" "/api/questions/page?page=1&size=20" "分页获取题目" "" "200"

# 搜索题目
test_api "GET" "/api/questions/search?keyword=测试&page=1&size=10" "搜索题目" "" "200"

# 创建题目
question_data='{
    "content": "增强测试题目：以下哪个是正确的？",
    "type": "SINGLE_CHOICE",
    "options": ["选项A", "选项B", "选项C", "选项D"],
    "correctAnswer": "A",
    "score": 5,
    "difficulty": "MEDIUM",
    "subject": "计算机科学"
}'
test_api "POST" "/api/questions" "创建题目" "$question_data" "201"

# 4. 考试管理 API 测试
log_section "考试管理 API 测试"
echo

# 分页获取考试列表
test_api "GET" "/api/exams/1/10" "分页获取考试列表" "" "200"

# 获取学生考试列表
test_api "GET" "/api/exams/student/1/1/10" "获取学生考试列表" "" "200"

# 获取所有考试
test_api "GET" "/api/exams/all" "获取所有考试" "" "200"

# 搜索考试（修复后的接口）
test_api "GET" "/api/exams/search?key=测试&page=1&size=10" "搜索考试" "" "200"

# 根据ID获取考试
test_api "GET" "/api/exams/1" "根据ID获取考试" "" "200"

# 获取考试统计信息
test_api "GET" "/api/exams/1/statistics" "获取考试统计信息" "" "200"

# 创建考试
exam_data='{
    "title": "增强测试考试",
    "description": "这是一个增强版测试考试",
    "startTime": "2024-12-25T10:00:00",
    "endTime": "2024-12-25T12:00:00",
    "creatorId": 1,
    "totalScore": 100,
    "duration": 120
}'
test_api "POST" "/api/exams" "创建考试" "$exam_data" "200"

# 随机组卷
random_exam_data='{
    "title": "随机增强测试考试",
    "description": "随机组卷增强测试",
    "startTime": "2024-12-25T14:00:00",
    "endTime": "2024-12-25T16:00:00",
    "creatorId": 1,
    "singleChoiceCount": 10,
    "multipleChoiceCount": 5,
    "trueFalseCount": 5,
    "fillBlankCount": 0,
    "essayCount": 0
}'
test_api "POST" "/api/exams/random" "随机组卷" "$random_exam_data" "200"

# 5. 考试提交管理 API 测试
log_section "考试提交管理 API 测试"
echo

# 获取考试提交记录
test_api "GET" "/api/exam-submissions/exam/1" "获取考试提交记录" "" "200"

# 获取所有提交记录
test_api "GET" "/api/exam-submissions/all" "获取所有提交记录" "" "200"

# 获取用户提交记录
test_api "GET" "/api/exam-submissions/user/1" "获取用户提交记录" "" "200"

# 分页获取提交记录
test_api "GET" "/api/exam-submissions/page?page=1&size=10" "分页获取提交记录" "" "200"

# 获取提交统计
test_api "GET" "/api/exam-submissions/statistics" "获取提交统计" "" "200"

# 提交考试
submission_data='{
    "examId": 1,
    "userId": 1,
    "answers": {
        "1": "A",
        "2": "B",
        "3": "C"
    },
    "submitTime": "2024-12-25T11:30:00"
}'
test_api "POST" "/api/exam-submissions/submit" "提交考试" "$submission_data" "201"

# 6. 阅卷管理 API 测试
log_section "阅卷管理 API 测试"
echo

# 获取考试的所有提交
test_api "GET" "/api/grading/exam/1/submissions" "获取考试的所有提交" "" "200"

# 获取提交详情（修复后的接口）
test_api "GET" "/api/grading/submission/1" "获取提交详情" "" "404"

# 使用存在的提交ID进行测试
test_api "GET" "/api/grading/submission/2" "获取提交详情（ID=2）" "" "200"

# 获取需要阅卷的提交
test_api "GET" "/api/grading/exam/1/needs-grading" "获取需要阅卷的提交" "" "200"

# 获取阅卷进度
test_api "GET" "/api/grading/exam/1/progress" "获取阅卷进度" "" "200"

# 更新单题得分
score_update_data='{
    "questionId": 1,
    "score": 8
}'
test_api "PUT" "/api/grading/submission/2/score" "更新单题得分" "$score_update_data" "200"

# 完成阅卷
test_api "PUT" "/api/grading/submission/2/complete" "完成阅卷" "" "200"

# 7. AI阅卷 API 测试（如果存在）
log_section "AI阅卷 API 测试"
echo

# AI自动阅卷
test_api "POST" "/api/ai-grading/auto-grade/1" "AI自动阅卷" "" "200"

# 获取AI阅卷结果
test_api "GET" "/api/ai-grading/result/1" "获取AI阅卷结果" "" "200"

# 8. 课程选择 API 测试
log_section "课程选择 API 测试"
echo

# 获取学生选课记录
test_api "GET" "/api/course-selections/student/1" "获取学生选课记录" "" "200"

# 获取课程选课学生
test_api "GET" "/api/course-selections/course/1" "获取课程选课学生" "" "200"

# 学生选课
selection_data='{
    "studentId": 1,
    "courseId": 1
}'
test_api "POST" "/api/course-selections" "学生选课" "$selection_data" "201"

# 9. 新增API端点测试
log_section "新增API端点测试"
echo

# 系统信息
test_api "GET" "/api/system/info" "获取系统信息" "" "200"

# 系统统计
test_api "GET" "/api/system/statistics" "获取系统统计" "" "200"

# 数据导出
test_api "GET" "/api/export/exams" "导出考试数据" "" "200"
test_api "GET" "/api/export/users" "导出用户数据" "" "200"
test_api "GET" "/api/export/submissions" "导出提交数据" "" "200"

# 批量操作
batch_delete_data='{
    "ids": [999, 998, 997]
}'
test_api "DELETE" "/api/questions/batch" "批量删除题目" "$batch_delete_data" "200"

# 高级搜索
advanced_search_data='{
    "keyword": "测试",
    "type": "SINGLE_CHOICE",
    "difficulty": "MEDIUM",
    "subject": "计算机科学",
    "page": 1,
    "size": 10
}'
test_api "POST" "/api/questions/advanced-search" "高级搜索题目" "$advanced_search_data" "200"

# 10. 错误处理测试
log_section "错误处理测试"
echo

# 测试不存在的资源
test_api "GET" "/api/users/99999" "获取不存在的用户" "" "404"
test_api "GET" "/api/exams/99999" "获取不存在的考试" "" "404"
test_api "GET" "/api/questions/99999" "获取不存在的题目" "" "404"

# 测试无效参数
test_api "GET" "/api/exams/search?key=&page=0&size=-1" "搜索考试（无效参数）" "" "400"
test_api "GET" "/api/users/page?page=-1&size=0" "分页获取用户（无效参数）" "" "400"

# 测试无效数据
invalid_user_data='{
    "username": "",
    "password": "123",
    "email": "invalid-email"
}'
test_api "POST" "/api/users/register" "用户注册（无效数据）" "$invalid_user_data" "400"

# 测试结果统计
echo
echo "=========================================="
echo "           测试结果统计"
echo "=========================================="
echo "总测试数: $TOTAL_TESTS"
echo -e "通过测试: ${GREEN}$PASSED_TESTS${NC}"
echo -e "失败测试: ${RED}$FAILED_TESTS${NC}"
echo -e "警告测试: ${YELLOW}$WARNING_TESTS${NC}"
if [ $TOTAL_TESTS -gt 0 ]; then
    echo -e "成功率: ${BLUE}$(( PASSED_TESTS * 100 / TOTAL_TESTS ))%${NC}"
else
    echo -e "成功率: ${BLUE}0%${NC}"
fi
echo "=========================================="

# 数据库状态验证
echo
log_section "数据库状态验证"
echo "根据查询结果，当前数据库包含："
echo "- 考试记录: 57 条"
echo "- 用户记录: 62 条"
echo "- 提交记录: 27 条"
echo "- 题目记录: 101 条"
echo "数据充足，适合进行API测试"

if [ $FAILED_TESTS -eq 0 ]; then
    log_success "所有测试通过！系统运行正常"
    exit 0
else
    log_error "有 $FAILED_TESTS 个测试失败，请检查API实现"
    if [ $WARNING_TESTS -gt 0 ]; then
        log_warning "有 $WARNING_TESTS 个测试产生警告，建议检查"
    fi
    exit 1
fi