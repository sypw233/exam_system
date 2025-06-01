# 在线考试系统 API 文档

## 概述

本文档描述了在线考试系统的所有 REST API 接口。系统运行在 `http://localhost:9999` 端口。

## 基础信息

- **基础URL**: `http://localhost:9999`
- **内容类型**: `application/json`
- **认证方式**: JWT Bearer Token（部分接口需要）
- **API文档**: `http://localhost:9999/doc.html`

## API 接口列表

### 1. 用户管理 API (`/api/users`)

#### 1.1 获取所有用户
- **接口**: `GET /api/users`
- **描述**: 获取系统中所有用户的列表
- **参数**: 无
- **响应**: 用户列表

#### 1.2 获取所有教师
- **接口**: `GET /api/users/teachers`
- **描述**: 获取系统中所有教师用户的列表
- **参数**: 无
- **响应**: 教师用户列表

#### 1.3 获取所有学生
- **接口**: `GET /api/users/students`
- **描述**: 获取系统中所有学生用户的列表
- **参数**: 无
- **响应**: 学生用户列表

#### 1.4 根据ID获取用户
- **接口**: `GET /api/users/{id}`
- **描述**: 根据用户ID获取单个用户的详细信息
- **参数**: 
  - `id` (路径参数): 用户ID
- **响应**: 用户详细信息

#### 1.5 创建用户
- **接口**: `POST /api/users`
- **描述**: 创建一个新的用户账户
- **请求体**:
```json
{
  "username": "string",
  "password": "string",
  "email": "string",
  "role": "STUDENT|TEACHER|ADMIN"
}
```

#### 1.6 用户注册
- **接口**: `POST /api/users/register`
- **描述**: 新用户注册账户
- **请求体**: 同创建用户

#### 1.7 用户登录
- **接口**: `POST /api/users/login`
- **描述**: 用户使用用户名和密码进行登录认证
- **请求体**:
```json
{
  "username": "string",
  "password": "string"
}
```
- **响应**: 包含JWT token的登录信息

#### 1.8 更新用户信息
- **接口**: `PUT /api/users/{id}`
- **描述**: 根据用户ID更新用户的详细信息
- **参数**: 
  - `id` (路径参数): 用户ID
- **请求体**: 用户更新信息

#### 1.9 删除用户
- **接口**: `DELETE /api/users/{id}`
- **描述**: 根据用户ID删除指定用户
- **参数**: 
  - `id` (路径参数): 用户ID

### 2. 课程管理 API (`/api/courses`)

#### 2.1 获取所有课程
- **接口**: `GET /api/courses`
- **描述**: 获取系统中所有课程的列表
- **参数**: 无
- **响应**: 课程列表

#### 2.2 根据教师ID获取课程
- **接口**: `GET /api/courses/teacher/{id}`
- **描述**: 根据教师ID获取该教师的所有课程
- **参数**: 
  - `id` (路径参数): 教师ID
- **响应**: 课程列表

#### 2.3 根据ID获取课程
- **接口**: `GET /api/courses/{id}`
- **描述**: 根据课程ID获取单个课程的详细信息
- **参数**: 
  - `id` (路径参数): 课程ID
- **响应**: 课程详细信息

#### 2.4 创建课程
- **接口**: `POST /api/courses`
- **描述**: 创建一个新的课程
- **请求体**:
```json
{
  "name": "string",
  "description": "string",
  "teacherId": "number"
}
```

#### 2.5 更新课程
- **接口**: `PUT /api/courses/{id}`
- **描述**: 根据课程ID更新课程信息
- **参数**: 
  - `id` (路径参数): 课程ID
- **请求体**: 课程更新信息

#### 2.6 删除课程
- **接口**: `DELETE /api/courses/{id}`
- **描述**: 根据课程ID删除指定课程
- **参数**: 
  - `id` (路径参数): 课程ID

### 3. 选课管理 API (`/api/course-selections`)

#### 3.1 获取所有选课记录
- **接口**: `GET /api/course-selections`
- **描述**: 获取系统中所有的选课记录
- **参数**: 无
- **响应**: 选课记录列表

#### 3.2 根据学生ID获取选课记录
- **接口**: `GET /api/course-selections/student/{id}`
- **描述**: 根据学生ID获取该学生的所有选课记录
- **参数**: 
  - `id` (路径参数): 学生ID
- **响应**: 选课记录列表

#### 3.3 根据课程ID获取选课记录
- **接口**: `GET /api/course-selections/course/{id}`
- **描述**: 根据课程ID获取选择该课程的所有学生记录
- **参数**: 
  - `id` (路径参数): 课程ID
- **响应**: 选课记录列表

#### 3.4 创建选课记录
- **接口**: `POST /api/course-selections`
- **描述**: 创建一个新的选课记录
- **请求体**:
```json
{
  "studentId": "number",
  "courseId": "number"
}
```

#### 3.5 删除选课记录
- **接口**: `DELETE /api/course-selections/{id}`
- **描述**: 根据选课记录ID删除指定的选课记录
- **参数**: 
  - `id` (路径参数): 选课记录ID

#### 3.6 删除学生选课
- **接口**: `DELETE /api/course-selections/{courseId}/students/{studentId}`
- **描述**: 根据课程ID和学生ID删除指定的选课记录
- **参数**: 
  - `courseId` (路径参数): 课程ID
  - `studentId` (路径参数): 学生ID

### 4. 试题管理 API (`/api/questions`)

#### 4.1 获取所有试题
- **接口**: `GET /api/questions`
- **描述**: 获取系统中所有试题的列表
- **参数**: 无
- **响应**: 试题列表

#### 4.2 分页获取试题
- **接口**: `GET /api/questions/{page}/{size}`
- **描述**: 分页获取试题列表
- **参数**: 
  - `page` (路径参数): 页码
  - `size` (路径参数): 每页大小
- **响应**: 分页试题列表

#### 4.3 根据ID获取试题
- **接口**: `GET /api/questions/{id}`
- **描述**: 根据试题ID获取单个试题的详细信息
- **参数**: 
  - `id` (路径参数): 试题ID
- **响应**: 试题详细信息

#### 4.4 创建试题
- **接口**: `POST /api/questions`
- **描述**: 创建一个新的试题
- **请求体**:
```json
{
  "content": "string",
  "type": "SINGLE_CHOICE|MULTIPLE_CHOICE|TRUE_FALSE|FILL_BLANK|ESSAY",
  "options": "string",
  "answer": "string",
  "score": "number",
  "difficulty": "EASY|MEDIUM|HARD"
}
```

#### 4.5 删除试题
- **接口**: `DELETE /api/questions/{id}`
- **描述**: 根据试题ID删除指定试题
- **参数**: 
  - `id` (路径参数): 试题ID

### 5. 考试管理 API (`/api/exams`)

#### 5.1 分页获取考试列表
- **接口**: `GET /api/exams/{page}/{size}`
- **描述**: 分页获取考试列表
- **参数**: 
  - `page` (路径参数): 页码
  - `size` (路径参数): 每页大小
- **响应**: 分页考试列表

#### 5.2 获取学生考试列表
- **接口**: `GET /api/exams/student/{studentId}/{page}/{size}`
- **描述**: 根据学生ID分页获取该学生的考试列表
- **参数**: 
  - `studentId` (路径参数): 学生ID
  - `page` (路径参数): 页码
  - `size` (路径参数): 每页大小
- **响应**: 分页考试列表

#### 5.3 获取所有考试
- **接口**: `GET /api/exams/all`
- **描述**: 获取系统中所有考试的列表
- **参数**: 无
- **响应**: 考试列表

#### 5.4 获取考试详情
- **接口**: `GET /api/exams/{examId}/details`
- **描述**: 根据考试ID获取考试的详细信息，包括题目列表
- **参数**: 
  - `examId` (路径参数): 考试ID
- **请求头**: 
  - `Authorization: Bearer {token}` (必需)
- **响应**: 考试详细信息

#### 5.5 搜索考试
- **接口**: `GET /api/exams/search`
- **描述**: 根据关键字搜索考试，支持分页
- **参数**: 
  - `key` (查询参数): 搜索关键字
  - `page` (查询参数): 页码
  - `size` (查询参数): 每页大小
- **响应**: 分页搜索结果

#### 5.6 创建考试
- **接口**: `POST /api/exams`
- **描述**: 创建一个新的考试
- **请求体**:
```json
{
  "title": "string",
  "description": "string",
  "startTime": "2024-12-25T10:00:00",
  "endTime": "2024-12-25T12:00:00",
  "creatorId": "number",
  "totalScore": "number"
}
```

#### 5.7 随机组卷
- **接口**: `POST /api/exams/random`
- **描述**: 根据题型分布自动生成考试试卷
- **请求体**:
```json
{
  "title": "string",
  "description": "string",
  "startTime": "2024-12-25T14:00:00",
  "endTime": "2024-12-25T16:00:00",
  "creatorId": "number",
  "singleChoiceCount": "number",
  "multipleChoiceCount": "number",
  "trueFalseCount": "number",
  "fillBlankCount": "number",
  "essayCount": "number"
}
```

### 6. 考试提交管理 API (`/api/exam-submissions`)

#### 6.1 获取考试提交记录
- **接口**: `GET /api/exam-submissions/exam/{examId}`
- **描述**: 根据考试ID获取该考试的所有提交记录
- **参数**: 
  - `examId` (路径参数): 考试ID
- **响应**: 提交记录列表

#### 6.2 获取所有提交记录
- **接口**: `GET /api/exam-submissions/all`
- **描述**: 获取系统中所有的考试提交记录
- **参数**: 无
- **响应**: 提交记录列表

#### 6.3 获取用户提交记录
- **接口**: `GET /api/exam-submissions/user/{userId}`
- **描述**: 根据用户ID获取该用户的所有考试提交记录
- **参数**: 
  - `userId` (路径参数): 用户ID
- **响应**: 提交记录列表

#### 6.4 删除提交记录
- **接口**: `DELETE /api/exam-submissions/{id}`
- **描述**: 根据提交记录ID删除指定的考试提交记录
- **参数**: 
  - `id` (路径参数): 提交记录ID

#### 6.5 提交考试
- **接口**: `POST /api/exam-submissions/submit`
- **描述**: 提交考试答案并自动评分
- **请求体**:
```json
{
  "examId": "number",
  "userId": "number",
  "answers": {
    "1": "A",
    "2": "B"
  },
  "submitTime": "2024-12-25T11:30:00"
}
```

### 7. 阅卷管理 API (`/api/grading`)

#### 7.1 获取考试的所有提交
- **接口**: `GET /api/grading/exam/{examId}/submissions`
- **描述**: 获取指定考试的所有学生提交记录
- **参数**: 
  - `examId` (路径参数): 考试ID
- **响应**: 提交记录列表

#### 7.2 获取提交详情
- **接口**: `GET /api/grading/submission/{submissionId}`
- **描述**: 获取指定提交的详细信息，包括试题和答案
- **参数**: 
  - `submissionId` (路径参数): 提交ID
- **响应**: 提交详细信息

#### 7.3 获取需要阅卷的提交
- **接口**: `GET /api/grading/exam/{examId}/needs-grading`
- **描述**: 获取指定考试中需要阅卷的提交记录
- **参数**: 
  - `examId` (路径参数): 考试ID
- **响应**: 需要阅卷的提交记录列表

#### 7.4 更新单题得分
- **接口**: `PUT /api/grading/submission/{submissionId}/score`
- **描述**: 更新指定提交中某道题的得分
- **参数**: 
  - `submissionId` (路径参数): 提交ID
- **请求体**:
```json
{
  "questionId": "number",
  "score": "number"
}
```

#### 7.5 完成阅卷
- **接口**: `PUT /api/grading/submission/{submissionId}/complete`
- **描述**: 标记指定提交的阅卷为完成状态
- **参数**: 
  - `submissionId` (路径参数): 提交ID

## 测试脚本使用说明

### 1. 完整测试脚本 (`test_all_apis.sh`)
包含所有API接口的测试，包括GET、POST、PUT、DELETE操作。

```bash
# 给脚本添加执行权限
chmod +x test_all_apis.sh

# 运行完整测试
./test_all_apis.sh
```

### 2. 基础测试脚本 (`test_basic_apis.sh`)
只测试基础的GET接口，不需要创建数据。

```bash
# 给脚本添加执行权限
chmod +x test_basic_apis.sh

# 运行基础测试
./test_basic_apis.sh
```

## 注意事项

1. **服务器状态**: 运行测试前请确保服务器在 `localhost:9999` 端口正常运行
2. **数据库状态**: 确保数据库中有基础测试数据
3. **认证Token**: 部分接口需要有效的JWT token，需要先登录获取
4. **测试顺序**: 建议先运行基础测试脚本，再运行完整测试脚本
5. **错误处理**: 如果测试失败，请检查服务器日志和数据库状态

## 常见问题

### Q: 测试脚本显示连接失败
A: 请检查服务器是否在9999端口运行，可以通过浏览器访问 `http://localhost:9999/doc.html` 确认

### Q: 认证相关的接口测试失败
A: 需要先通过登录接口获取有效的JWT token，然后在请求头中添加 `Authorization: Bearer {token}`

### Q: 数据相关的测试失败
A: 请确保数据库中有基础的测试数据，如用户、课程、试题等

## API 状态码说明

- **200 OK**: 请求成功
- **201 Created**: 资源创建成功
- **204 No Content**: 请求成功，无返回内容
- **400 Bad Request**: 请求参数错误
- **401 Unauthorized**: 未授权访问
- **403 Forbidden**: 禁止访问
- **404 Not Found**: 资源不存在
- **409 Conflict**: 资源冲突
- **500 Internal Server Error**: 服务器内部错误