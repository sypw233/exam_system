# 在线考试系统 API 测试指南

## 概述

本项目提供了完整的API测试脚本，用于测试在线考试系统的所有REST API接口。

## 文件说明

### 测试脚本

1. **`test_all_apis.sh`** - 完整API测试脚本
   - 测试所有API接口（GET、POST、PUT、DELETE）
   - 包含数据创建和修改操作
   - 适合完整功能测试

2. **`test_basic_apis_fixed.sh`** - 基础API测试脚本（推荐）
   - 只测试GET接口
   - 不需要创建测试数据
   - 适合快速验证API可用性

3. **`test_basic_apis.sh`** - 基础测试脚本（旧版本）
   - 存在macOS兼容性问题，建议使用fixed版本

### 文档文件

4. **`API_DOCUMENTATION.md`** - 完整API文档
   - 详细的API接口说明
   - 请求参数和响应格式
   - 使用示例

5. **`README_API_TESTING.md`** - 本文件
   - 测试脚本使用指南

## 快速开始

### 1. 启动服务器

确保在线考试系统服务器在 `localhost:9999` 端口运行：

```bash
# 在项目根目录运行
./gradlew bootRun
```

### 2. 运行基础测试（推荐）

```bash
# 运行基础API测试
./test_basic_apis_fixed.sh
```

### 3. 运行完整测试

```bash
# 运行完整API测试
./test_all_apis.sh
```

## 测试结果说明

### 成功输出示例
```
[INFO] 测试: 获取所有用户
[INFO] 请求: GET /api/users
响应状态码: 200
[SUCCESS] 测试通过 - 状态码: 200
响应内容预览: [{"id":1,"username":"admin",...}]
```

### 失败输出示例
```
[INFO] 测试: 获取提交详情
[INFO] 请求: GET /api/grading/submission/1
响应状态码: 500
[ERROR] 测试失败 - 状态码: 500
错误响应: {"error":"Internal Server Error"}
```

### 最终统计
```
==========================================
           测试结果统计
==========================================
总测试数: 23
通过测试: 22
失败测试: 1
成功率: 95%
==========================================
```

## 常见问题排查

### 1. 连接失败
**问题**: `无法连接到服务器 http://localhost:9999`

**解决方案**:
- 确保服务器正在运行：`./gradlew bootRun`
- 检查端口是否被占用：`lsof -i :9999`
- 访问 `http://localhost:9999/doc.html` 确认服务器状态

### 2. 认证失败
**问题**: 状态码 401 或 403

**解决方案**:
- 某些接口需要JWT token认证
- 先调用登录接口获取token
- 在请求头中添加：`Authorization: Bearer {token}`

### 3. 数据不存在
**问题**: 状态码 404 或 500

**解决方案**:
- 确保数据库中有基础测试数据
- 检查数据库连接是否正常
- 查看服务器日志了解具体错误

### 4. 脚本权限问题
**问题**: `Permission denied`

**解决方案**:
```bash
# 给脚本添加执行权限
chmod +x test_basic_apis_fixed.sh
chmod +x test_all_apis.sh
```

## API 接口分类

### 用户管理 (`/api/users`)
- 用户注册、登录、查询、管理
- 教师和学生用户分类查询

### 课程管理 (`/api/courses`)
- 课程的增删改查
- 按教师查询课程

### 选课管理 (`/api/course-selections`)
- 学生选课记录管理
- 按学生或课程查询选课情况

### 试题管理 (`/api/questions`)
- 试题的增删改查
- 支持分页查询
- 多种题型支持

### 考试管理 (`/api/exams`)
- 考试的创建和管理
- 随机组卷功能
- 考试详情查询（需认证）

### 考试提交 (`/api/exam-submissions`)
- 学生答题提交
- 提交记录查询
- 自动评分

### 阅卷管理 (`/api/grading`)
- 教师阅卷功能
- 成绩管理
- 阅卷状态跟踪

## 测试数据准备

为了确保测试顺利进行，建议在数据库中准备以下基础数据：

1. **用户数据**
   - 至少1个管理员用户
   - 至少1个教师用户
   - 至少1个学生用户

2. **课程数据**
   - 至少1个课程记录

3. **试题数据**
   - 各种题型的试题
   - 不同难度级别

4. **考试数据**
   - 至少1个考试记录
   - 关联的试题

## 进阶使用

### 自定义测试

可以基于现有脚本创建自定义测试：

```bash
# 测试特定接口
curl -X GET "http://localhost:9999/api/users" \
     -H "Content-Type: application/json"

# 带参数的GET请求
curl -X GET "http://localhost:9999/api/exams/search?key=测试&page=1&size=10"

# POST请求示例
curl -X POST "http://localhost:9999/api/users/login" \
     -H "Content-Type: application/json" \
     -d '{"username":"admin","password":"password"}'
```

### 集成到CI/CD

可以将测试脚本集成到持续集成流程中：

```yaml
# GitHub Actions 示例
- name: Run API Tests
  run: |
    ./gradlew bootRun &
    sleep 30
    ./test_basic_apis_fixed.sh
```

## 技术支持

如果遇到问题，请：

1. 查看服务器日志
2. 检查数据库状态
3. 确认API文档：`http://localhost:9999/doc.html`
4. 参考 `API_DOCUMENTATION.md` 获取详细接口说明

## 更新日志

- **v1.2** - 修复macOS兼容性问题，创建fixed版本
- **v1.1** - 添加完整API测试脚本
- **v1.0** - 初始版本，基础GET接口测试