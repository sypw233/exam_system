#!/bin/bash

# 在线考试系统 API 测试脚本
# 测试所有 exam-api 接口
# 运行前请确保服务器在 localhost:9999 端口运行

BASE_URL="http://localhost:9999"
CONTENT_TYPE="Content-Type: application/json"

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 测试结果统计
TOTAL_TESTS=0
PASSED_TESTS=0
FAILED_TESTS=0

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
            response=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL$endpoint" -H "$auth_header")
        else
            response=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL$endpoint")
        fi
    elif [ "$method" = "POST" ]; then
        if [ -n "$auth_header" ]; then
            response=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL$endpoint" -H "$CONTENT_TYPE" -H "$auth_header" -d "$data")
        else
            response=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL$endpoint" -H "$CONTENT_TYPE" -d "$data")
        fi
    elif [ "$method" = "PUT" ]; then
        if [ -n "$auth_header" ]; then
            response=$(curl -s -w "\n%{http_code}" -X PUT "$BASE_URL$endpoint" -H "$CONTENT_TYPE" -H "$auth_header" -d "$data")
        else
            response=$(curl -s -w "\n%{http_code}" -X PUT "$BASE_URL$endpoint" -H "$CONTENT_TYPE" -d "$data")
        fi
    elif [ "$method" = "DELETE" ]; then
        if [ -n "$auth_header" ]; then
            response=$(curl -s -w "\n%{http_code}" -X DELETE "$BASE_URL$endpoint" -H "$auth_header")
        else
            response=$(curl -s -w "\n%{http_code}" -X DELETE "$BASE_URL$endpoint")
        fi
    fi
    
    # 分离响应体和状态码
    status_code=$(echo "$response" | tail -n1)
    response_body=$(echo "$response" | head -n -1)
    
    echo "响应状态码: $status_code"
    echo "响应内容: $response_body"
    
    if [ "$status_code" = "$expected_status" ]; then
        log_success "测试通过 - 状态码: $status_code"
    else
        log_error "测试失败 - 期望状态码: $expected_status, 实际状态码: $status_code"
    fi
    
    echo "----------------------------------------"
}

# 开始测试
echo "=========================================="
echo "      在线考试系统 API 测试开始"
echo "=========================================="
echo

# 1. 用户管理 API 测试
log_info "开始测试用户管理 API"
echo

# 获取所有用户
test_api "GET" "/api/users" "获取所有用户" "" "200"

# 获取所有教师
test_api "GET" "/api/users/teachers" "获取所有教师" "" "200"

# 获取所有学生
test_api "GET" "/api/users/students" "获取所有学生" "" "200"

# 根据ID获取用户
test_api "GET" "/api/users/1" "根据ID获取用户" "" "200"

# 用户注册
register_data='{
    "username": "testuser",
    "password": "password123",
    "email": "test@example.com",
    "role": "STUDENT"
}'
test_api "POST" "/api/users/register" "用户注册" "$register_data" "201"

# 用户登录
login_data='{
    "username": "testuser",
    "password": "password123"
}'
test_api "POST" "/api/users/login" "用户登录" "$login_data" "200"

# 2. 课程管理 API 测试
log_info "开始测试课程管理 API"
echo

# 获取所有课程
test_api "GET" "/api/courses" "获取所有课程" "" "200"

# 根据教师ID获取课程
test_api "GET" "/api/courses/teacher/1" "根据教师ID获取课程" "" "200"

# 根据ID获取课程
test_api "GET" "/api/courses/1" "根据ID获取课程" "" "200"

# 创建课程
course_data='{
    "name": "测试课程",
    "description": "这是一个测试课程",
    "teacherId": 1
}'
test_api "POST" "/api/courses" "创建课程" "$course_data" "201"

# 3. 选课管理 API 测试
log_info "开始测试选课管理 API"
echo

# 获取所有选课记录
test_api "GET" "/api/course-selections" "获取所有选课记录" "" "200"

# 根据学生ID获取选课记录
test_api "GET" "/api/course-selections/student/1" "根据学生ID获取选课记录" "" "200"

# 根据课程ID获取选课记录
test_api "GET" "/api/course-selections/course/1" "根据课程ID获取选课记录" "" "200"

# 创建选课记录
selection_data='{
    "studentId": 1,
    "courseId": 1
}'
test_api "POST" "/api/course-selections" "创建选课记录" "$selection_data" "201"

# 4. 试题管理 API 测试
log_info "开始测试试题管理 API"
echo

# 获取所有试题
test_api "GET" "/api/questions" "获取所有试题" "" "200"

# 分页获取试题
test_api "GET" "/api/questions/1/10" "分页获取试题" "" "200"

# 根据ID获取试题
test_api "GET" "/api/questions/1" "根据ID获取试题" "" "200"

# 创建试题
question_data='{
    "content": "测试题目内容",
    "type": "SINGLE_CHOICE",
    "options": "A.选项1|B.选项2|C.选项3|D.选项4",
    "answer": "A",
    "score": 10,
    "difficulty": "EASY"
}'
test_api "POST" "/api/questions" "创建试题" "$question_data" "201"

# 5. 考试管理 API 测试
log_info "开始测试考试管理 API"
echo

# 分页获取考试列表
test_api "GET" "/api/exams/1/10" "分页获取考试列表" "" "200"

# 获取学生考试列表
test_api "GET" "/api/exams/student/1/1/10" "获取学生考试列表" "" "200"

# 获取所有考试
test_api "GET" "/api/exams/all" "获取所有考试" "" "200"

# 搜索考试
test_api "GET" "/api/exams/search?key=测试&page=1&size=10" "搜索考试" "" "200"

# 创建考试
exam_data='{
    "title": "测试考试",
    "description": "这是一个测试考试",
    "startTime": "2024-12-25T10:00:00",
    "endTime": "2024-12-25T12:00:00",
    "creatorId": 1,
    "totalScore": 100
}'
test_api "POST" "/api/exams" "创建考试" "$exam_data" "200"

# 随机组卷
random_exam_data='{
    "title": "随机测试考试",
    "description": "随机组卷测试",
    "startTime": "2024-12-25T14:00:00",
    "endTime": "2024-12-25T16:00:00",
    "creatorId": 1,
    "singleChoiceCount": 5,
    "multipleChoiceCount": 3,
    "trueFalseCount": 2,
    "fillBlankCount": 0,
    "essayCount": 0
}'
test_api "POST" "/api/exams/random" "随机组卷" "$random_exam_data" "200"

# 6. 考试提交管理 API 测试
log_info "开始测试考试提交管理 API"
echo

# 获取考试提交记录
test_api "GET" "/api/exam-submissions/exam/1" "获取考试提交记录" "" "200"

# 获取所有提交记录
test_api "GET" "/api/exam-submissions/all" "获取所有提交记录" "" "200"

# 获取用户提交记录
test_api "GET" "/api/exam-submissions/user/1" "获取用户提交记录" "" "200"

# 提交考试
submission_data='{
    "examId": 1,
    "userId": 1,
    "answers": {
        "1": "A",
        "2": "B"
    },
    "submitTime": "2024-12-25T11:30:00"
}'
test_api "POST" "/api/exam-submissions/submit" "提交考试" "$submission_data" "201"

# 7. 阅卷管理 API 测试
log_info "开始测试阅卷管理 API"
echo

# 获取考试的所有提交
test_api "GET" "/api/grading/exam/1/submissions" "获取考试的所有提交" "" "200"

# 获取提交详情
test_api "GET" "/api/grading/submission/1" "获取提交详情" "" "200"

# 获取需要阅卷的提交
test_api "GET" "/api/grading/exam/1/needs-grading" "获取需要阅卷的提交" "" "200"

# 更新单题得分
score_update_data='{
    "questionId": 1,
    "score": 8
}'
test_api "PUT" "/api/grading/submission/1/score" "更新单题得分" "$score_update_data" "200"

# 完成阅卷
test_api "PUT" "/api/grading/submission/1/complete" "完成阅卷" "" "200"

# 测试需要认证的接口（需要先登录获取token）
log_info "开始测试需要认证的接口"
echo

# 注意：这里需要先登录获取真实的JWT token
# 示例token（实际使用时需要替换为真实token）
AUTH_TOKEN="Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

# 获取考试详情（需要认证）
test_api "GET" "/api/exams/1/details" "获取考试详情（需要认证）" "" "200" "$AUTH_TOKEN"

# 测试结果统计
echo
echo "=========================================="
echo "           测试结果统计"
echo "=========================================="
echo "总测试数: $TOTAL_TESTS"
echo -e "通过测试: ${GREEN}$PASSED_TESTS${NC}"
echo -e "失败测试: ${RED}$FAILED_TESTS${NC}"
echo -e "成功率: ${BLUE}$(( PASSED_TESTS * 100 / TOTAL_TESTS ))%${NC}"
echo "=========================================="

if [ $FAILED_TESTS -eq 0 ]; then
    log_success "所有测试通过！"
    exit 0
else
    log_error "有 $FAILED_TESTS 个测试失败，请检查API实现"
    exit 1
fi