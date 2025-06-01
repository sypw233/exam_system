#!/bin/bash

# 在线考试系统基础 API 测试脚本（修复版）
# 测试所有基础的 GET 接口，不需要数据创建
# 运行前请确保服务器在 localhost:9999 端口运行

BASE_URL="http://localhost:9999"

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

# 简单测试函数
test_get_api() {
    local endpoint=$1
    local description=$2
    
    ((TOTAL_TESTS++))
    
    log_info "测试: $description"
    log_info "请求: GET $endpoint"
    
    # 使用临时文件来处理响应
    temp_file=$(mktemp)
    status_code=$(curl -s -w "%{http_code}" -X GET "$BASE_URL$endpoint" -o "$temp_file")
    response_body=$(cat "$temp_file")
    rm "$temp_file"
    
    echo "响应状态码: $status_code"
    
    # 检查是否是成功状态码 (2xx)
    if [[ $status_code =~ ^2[0-9][0-9]$ ]]; then
        log_success "测试通过 - 状态码: $status_code"
        # 显示响应内容的前200个字符
        if [ ${#response_body} -gt 200 ]; then
            echo "响应内容预览: ${response_body:0:200}..."
        else
            echo "响应内容: $response_body"
        fi
    else
        log_error "测试失败 - 状态码: $status_code"
        echo "错误响应: $response_body"
    fi
    
    echo "----------------------------------------"
}

# 检查服务器是否运行
check_server() {
    log_info "检查服务器状态..."
    if curl -s "$BASE_URL" > /dev/null 2>&1; then
        log_success "服务器运行正常"
    else
        log_error "无法连接到服务器 $BASE_URL"
        log_error "请确保服务器在端口 9999 上运行"
        exit 1
    fi
    echo
}

# 开始测试
echo "=========================================="
echo "    在线考试系统基础 API 测试开始"
echo "=========================================="
echo

# 检查服务器状态
check_server

# 1. 用户管理 API 测试
log_info "开始测试用户管理 API"
echo

test_get_api "/api/users" "获取所有用户"
test_get_api "/api/users/teachers" "获取所有教师"
test_get_api "/api/users/students" "获取所有学生"
test_get_api "/api/users/1" "根据ID获取用户"

# 2. 课程管理 API 测试
log_info "开始测试课程管理 API"
echo

test_get_api "/api/courses" "获取所有课程"
test_get_api "/api/courses/teacher/1" "根据教师ID获取课程"
test_get_api "/api/courses/1" "根据ID获取课程"

# 3. 选课管理 API 测试
log_info "开始测试选课管理 API"
echo

test_get_api "/api/course-selections" "获取所有选课记录"
test_get_api "/api/course-selections/student/1" "根据学生ID获取选课记录"
test_get_api "/api/course-selections/course/1" "根据课程ID获取选课记录"

# 4. 试题管理 API 测试
log_info "开始测试试题管理 API"
echo

test_get_api "/api/questions" "获取所有试题"
test_get_api "/api/questions/1/10" "分页获取试题"
test_get_api "/api/questions/1" "根据ID获取试题"

# 5. 考试管理 API 测试
log_info "开始测试考试管理 API"
echo

test_get_api "/api/exams/1/10" "分页获取考试列表"
test_get_api "/api/exams/student/1/1/10" "获取学生考试列表"
test_get_api "/api/exams/all" "获取所有考试"
test_get_api "/api/exams/search?key=测试&page=1&size=10" "搜索考试"

# 6. 考试提交管理 API 测试
log_info "开始测试考试提交管理 API"
echo

test_get_api "/api/exam-submissions/exam/1" "获取考试提交记录"
test_get_api "/api/exam-submissions/all" "获取所有提交记录"
test_get_api "/api/exam-submissions/user/1" "获取用户提交记录"

# 7. 阅卷管理 API 测试
log_info "开始测试阅卷管理 API"
echo

test_get_api "/api/grading/exam/1/submissions" "获取考试的所有提交"
test_get_api "/api/grading/submission/1" "获取提交详情"
test_get_api "/api/grading/exam/1/needs-grading" "获取需要阅卷的提交"

# 测试结果统计
echo
echo "=========================================="
echo "           测试结果统计"
echo "=========================================="
echo "总测试数: $TOTAL_TESTS"
echo -e "通过测试: ${GREEN}$PASSED_TESTS${NC}"
echo -e "失败测试: ${RED}$FAILED_TESTS${NC}"
if [ $TOTAL_TESTS -gt 0 ]; then
    echo -e "成功率: ${BLUE}$(( PASSED_TESTS * 100 / TOTAL_TESTS ))%${NC}"
fi
echo "=========================================="

if [ $FAILED_TESTS -eq 0 ]; then
    log_success "所有基础测试通过！"
    exit 0
else
    log_error "有 $FAILED_TESTS 个测试失败，请检查API实现"
    echo
    echo "常见问题排查:"
    echo "1. 确保服务器在 localhost:9999 端口运行"
    echo "2. 检查数据库中是否有基础测试数据"
    echo "3. 查看服务器日志了解具体错误信息"
    exit 1
fi