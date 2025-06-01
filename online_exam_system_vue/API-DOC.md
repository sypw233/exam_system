# OpenAPI definition


**简介**:OpenAPI definition


**HOST**:http://localhost:9999


**联系人**:


**Version**:v0


**接口路径**:/v3/api-docs/default


[TOC]






# 考试管理


## 创建考试


**接口地址**:`/api/exams`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>创建一个新的考试</p>



**请求示例**:


```javascript
{
  "id": 0,
  "title": "",
  "description": "",
  "startTime": "",
  "endTime": "",
  "creatorId": 0,
  "createTime": "",
  "totalScore": 0,
  "questions": [],
  "courseId": 0,
  "needsGrading": true
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|exams|Exams|body|true|Exams|Exams|
|&emsp;&emsp;id|||false|integer(int64)||
|&emsp;&emsp;title|||true|string||
|&emsp;&emsp;description|||false|string||
|&emsp;&emsp;startTime|||true|string(date-time)||
|&emsp;&emsp;endTime|||true|string(date-time)||
|&emsp;&emsp;creatorId|||true|integer(int64)||
|&emsp;&emsp;createTime|||false|string(date-time)||
|&emsp;&emsp;totalScore|||true|integer(int32)||
|&emsp;&emsp;questions|||false|array|integer(int64)|
|&emsp;&emsp;courseId|||true|integer(int64)||
|&emsp;&emsp;needsGrading|||true|boolean||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||


**响应参数**:


暂无


**响应示例**:
```javascript

```


## 获取考试详情


**接口地址**:`/api/exams/{examId}/details`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>根据考试ID获取考试的详细信息，包括题目列表</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|examId|考试ID|path|true|integer(int64)||
|Authorization|授权令牌|header|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||


**响应参数**:


暂无


**响应示例**:
```javascript

```


## 删除考试


**接口地址**:`/api/exams/{id}`


**请求方式**:`DELETE`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>根据考试ID删除指定考试</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id|考试ID|path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||


**响应参数**:


暂无


**响应示例**:
```javascript

```


## 分页获取考试列表


**接口地址**:`/api/exams/{page}/{size}`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>分页获取考试列表</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|page|页码|path|true|integer(int32)||
|size|每页大小|path|true|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||


**响应参数**:


暂无


**响应示例**:
```javascript

```


## 获取所有考试


**接口地址**:`/api/exams/all`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>获取系统中所有考试的列表</p>



**请求参数**:


暂无


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||


**响应参数**:


暂无


**响应示例**:
```javascript

```


## 随机组卷


**接口地址**:`/api/exams/random`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>根据题型分布自动生成考试试卷</p>



**请求示例**:


```javascript
{
  "title": "",
  "description": "",
  "startTime": "",
  "endTime": "",
  "creatorId": 0,
  "totalScore": 0,
  "courseId": 0,
  "questionDistribution": [
    {
      "type": "",
      "category": "",
      "difficulty": "",
      "count": 0,
      "scorePerQuestion": 0
    }
  ]
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|randomExamDto|RandomExamDto|body|true|RandomExamDto|RandomExamDto|
|&emsp;&emsp;title|||true|string||
|&emsp;&emsp;description|||false|string||
|&emsp;&emsp;startTime|||true|string||
|&emsp;&emsp;endTime|||true|string||
|&emsp;&emsp;creatorId|||true|integer(int64)||
|&emsp;&emsp;totalScore|||true|integer(int32)||
|&emsp;&emsp;courseId|||true|integer(int64)||
|&emsp;&emsp;questionDistribution|||true|array|QuestionDistribution|
|&emsp;&emsp;&emsp;&emsp;type|||true|string||
|&emsp;&emsp;&emsp;&emsp;category|||false|string||
|&emsp;&emsp;&emsp;&emsp;difficulty|||false|string||
|&emsp;&emsp;&emsp;&emsp;count|||true|integer||
|&emsp;&emsp;&emsp;&emsp;scorePerQuestion|||true|integer||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||


**响应参数**:


暂无


**响应示例**:
```javascript

```


## 搜索考试


**接口地址**:`/api/exams/search`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>根据关键字搜索考试，支持分页</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|key|搜索关键字|query|true|string||
|page|页码|query|true|integer(int32)||
|size|每页大小|query|true|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||


**响应参数**:


暂无


**响应示例**:
```javascript

```


## 获取学生考试列表


**接口地址**:`/api/exams/student/{studentId}/{page}/{size}`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>根据学生ID分页获取该学生的考试列表</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|studentId|学生ID|path|true|integer(int64)||
|page|页码|path|true|integer(int32)||
|size|每页大小|path|true|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||


**响应参数**:


暂无


**响应示例**:
```javascript

```


# 考试提交管理


## 删除提交记录


**接口地址**:`/api/exam-submissions/{id}`


**请求方式**:`DELETE`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>根据提交记录ID删除指定的考试提交记录</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id|提交记录ID|path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||


**响应参数**:


暂无


**响应示例**:
```javascript

```


## 获取所有提交记录


**接口地址**:`/api/exam-submissions/all`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>获取系统中所有的考试提交记录</p>



**请求参数**:


暂无


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ExamSubmissionResponse|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|id||integer(int64)|integer(int64)|
|userId||integer(int64)|integer(int64)|
|examId||integer(int64)|integer(int64)|
|userName||string||
|examTitle||string||
|examTotalScore||integer(int32)|integer(int32)|
|submissionScore||integer(int32)|integer(int32)|
|answers||object||


**响应示例**:
```javascript
[
	{
		"id": 0,
		"userId": 0,
		"examId": 0,
		"userName": "",
		"examTitle": "",
		"examTotalScore": 0,
		"submissionScore": 0,
		"answers": {}
	}
]
```


## 获取考试提交记录


**接口地址**:`/api/exam-submissions/exam/{examId}`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>根据考试ID获取该考试的所有提交记录</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|examId|考试ID|path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ExamSubmissionResponse|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|id||integer(int64)|integer(int64)|
|userId||integer(int64)|integer(int64)|
|examId||integer(int64)|integer(int64)|
|userName||string||
|examTitle||string||
|examTotalScore||integer(int32)|integer(int32)|
|submissionScore||integer(int32)|integer(int32)|
|answers||object||


**响应示例**:
```javascript
[
	{
		"id": 0,
		"userId": 0,
		"examId": 0,
		"userName": "",
		"examTitle": "",
		"examTotalScore": 0,
		"submissionScore": 0,
		"answers": {}
	}
]
```


## 提交考试


**接口地址**:`/api/exam-submissions/submit`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>提交考试答案并自动评分</p>



**请求示例**:


```javascript
{
  "id": 0,
  "userId": 0,
  "examId": 0,
  "answers": {},
  "submitScore": 0,
  "submitDetail": "",
  "submitTime": "",
  "isGraded": true,
  "graded": true
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|examSubmission|ExamSubmission|body|true|ExamSubmission|ExamSubmission|
|&emsp;&emsp;id|||false|integer(int64)||
|&emsp;&emsp;userId|||true|integer(int64)||
|&emsp;&emsp;examId|||true|integer(int64)||
|&emsp;&emsp;answers|||false|object||
|&emsp;&emsp;submitScore|||false|integer(int32)||
|&emsp;&emsp;submitDetail|||false|string||
|&emsp;&emsp;submitTime|||true|string(date-time)||
|&emsp;&emsp;isGraded|||true|boolean||
|&emsp;&emsp;graded|||false|boolean||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ExamSubmission|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|id||integer(int64)|integer(int64)|
|userId||integer(int64)|integer(int64)|
|examId||integer(int64)|integer(int64)|
|answers||object||
|submitScore||integer(int32)|integer(int32)|
|submitDetail||string||
|submitTime||string(date-time)|string(date-time)|
|isGraded||boolean||
|graded||boolean||


**响应示例**:
```javascript
{
	"id": 0,
	"userId": 0,
	"examId": 0,
	"answers": {},
	"submitScore": 0,
	"submitDetail": "",
	"submitTime": "",
	"isGraded": true,
	"graded": true
}
```


## 获取用户提交记录


**接口地址**:`/api/exam-submissions/user/{userId}`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>根据用户ID获取该用户的所有考试提交记录</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|userId|用户ID|path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ExamSubmissionResponse|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|id||integer(int64)|integer(int64)|
|userId||integer(int64)|integer(int64)|
|examId||integer(int64)|integer(int64)|
|userName||string||
|examTitle||string||
|examTotalScore||integer(int32)|integer(int32)|
|submissionScore||integer(int32)|integer(int32)|
|answers||object||


**响应示例**:
```javascript
[
	{
		"id": 0,
		"userId": 0,
		"examId": 0,
		"userName": "",
		"examTitle": "",
		"examTotalScore": 0,
		"submissionScore": 0,
		"answers": {}
	}
]
```


# 课程管理


## 获取所有课程


**接口地址**:`/api/courses`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>获取系统中所有课程的列表</p>



**请求参数**:


暂无


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|Course|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|id||integer(int64)|integer(int64)|
|courseName||string||
|teacherId||integer(int64)|integer(int64)|


**响应示例**:
```javascript
[
	{
		"id": 0,
		"courseName": "",
		"teacherId": 0
	}
]
```


## 创建课程


**接口地址**:`/api/courses`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>创建一个新的课程</p>



**请求示例**:


```javascript
{
  "id": 0,
  "courseName": "",
  "teacherId": 0
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|course|Course|body|true|Course|Course|
|&emsp;&emsp;id|||false|integer(int64)||
|&emsp;&emsp;courseName|||true|string||
|&emsp;&emsp;teacherId|||true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|201|Created|Course|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|id||integer(int64)|integer(int64)|
|courseName||string||
|teacherId||integer(int64)|integer(int64)|


**响应示例**:
```javascript
{
	"id": 0,
	"courseName": "",
	"teacherId": 0
}
```


## 根据ID获取课程


**接口地址**:`/api/courses/{id}`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>根据课程ID获取单个课程的详细信息</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id|课程ID|path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|Course|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|id||integer(int64)|integer(int64)|
|courseName||string||
|teacherId||integer(int64)|integer(int64)|


**响应示例**:
```javascript
{
	"id": 0,
	"courseName": "",
	"teacherId": 0
}
```


## 更新课程


**接口地址**:`/api/courses/{id}`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>根据课程ID更新课程信息</p>



**请求示例**:


```javascript
{
  "id": 0,
  "courseName": "",
  "teacherId": 0
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id|课程ID|path|true|integer(int64)||
|course|Course|body|true|Course|Course|
|&emsp;&emsp;id|||false|integer(int64)||
|&emsp;&emsp;courseName|||true|string||
|&emsp;&emsp;teacherId|||true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|Course|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|id||integer(int64)|integer(int64)|
|courseName||string||
|teacherId||integer(int64)|integer(int64)|


**响应示例**:
```javascript
{
	"id": 0,
	"courseName": "",
	"teacherId": 0
}
```


## 删除课程


**接口地址**:`/api/courses/{id}`


**请求方式**:`DELETE`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>根据课程ID删除指定课程</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id|课程ID|path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|204|No Content||


**响应参数**:


暂无


**响应示例**:
```javascript

```


## 获取教师课程


**接口地址**:`/api/courses/teacher/{id}`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>根据教师ID获取该教师的所有课程</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id|教师ID|path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|Course|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|id||integer(int64)|integer(int64)|
|courseName||string||
|teacherId||integer(int64)|integer(int64)|


**响应示例**:
```javascript
[
	{
		"id": 0,
		"courseName": "",
		"teacherId": 0
	}
]
```


# 试题管理


## 获取所有试题


**接口地址**:`/api/questions`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>获取系统中所有试题的列表</p>



**请求参数**:


暂无


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||


**响应参数**:


暂无


**响应示例**:
```javascript

```


## 创建试题


**接口地址**:`/api/questions`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>创建一个新的试题</p>



**请求示例**:


```javascript
{
  "id": 0,
  "content": "",
  "type": "",
  "options": {},
  "answer": "",
  "difficulty": "",
  "category": "",
  "createTime": "",
  "creatorId": 0
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|question|Question|body|true|Question|Question|
|&emsp;&emsp;id|||false|integer(int64)||
|&emsp;&emsp;content|||true|string||
|&emsp;&emsp;type|||true|string||
|&emsp;&emsp;options|||false|object||
|&emsp;&emsp;answer|||false|string||
|&emsp;&emsp;difficulty|||true|string||
|&emsp;&emsp;category|||true|string||
|&emsp;&emsp;createTime|||false|string(date-time)||
|&emsp;&emsp;creatorId|||true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||


**响应参数**:


暂无


**响应示例**:
```javascript

```


## 根据ID获取试题


**接口地址**:`/api/questions/{id}`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>根据试题ID获取单个试题的详细信息</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id|试题ID|path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||


**响应参数**:


暂无


**响应示例**:
```javascript

```


## 删除试题


**接口地址**:`/api/questions/{id}`


**请求方式**:`DELETE`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>根据试题ID删除指定试题</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id|试题ID|path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||


**响应参数**:


暂无


**响应示例**:
```javascript

```


## 分页获取试题


**接口地址**:`/api/questions/{page}/{size}`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>分页获取试题列表</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|page|页码|path|true|integer(int32)||
|size|每页大小|path|true|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||


**响应参数**:


暂无


**响应示例**:
```javascript

```


# 选课管理


## 获取所有选课记录


**接口地址**:`/api/course-selections`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>获取系统中所有的选课记录</p>



**请求参数**:


暂无


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|CourseSelection|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|id||integer(int64)|integer(int64)|
|studentId||integer(int64)|integer(int64)|
|courseId||integer(int64)|integer(int64)|


**响应示例**:
```javascript
[
	{
		"id": 0,
		"studentId": 0,
		"courseId": 0
	}
]
```


## 创建选课记录


**接口地址**:`/api/course-selections`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>创建一个新的选课记录</p>



**请求示例**:


```javascript
{
  "id": 0,
  "studentId": 0,
  "courseId": 0
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|courseSelection|CourseSelection|body|true|CourseSelection|CourseSelection|
|&emsp;&emsp;id|||false|integer(int64)||
|&emsp;&emsp;studentId|||true|integer(int64)||
|&emsp;&emsp;courseId|||true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|201|Created|CourseSelection|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|id||integer(int64)|integer(int64)|
|studentId||integer(int64)|integer(int64)|
|courseId||integer(int64)|integer(int64)|


**响应示例**:
```javascript
{
	"id": 0,
	"studentId": 0,
	"courseId": 0
}
```


## 删除学生选课


**接口地址**:`/api/course-selections/{courseId}/students/{studentId}`


**请求方式**:`DELETE`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>根据课程ID和学生ID删除指定的选课记录</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|courseId|课程ID|path|true|integer(int64)||
|studentId|学生ID|path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|204|No Content||


**响应参数**:


暂无


**响应示例**:
```javascript

```


## 删除选课记录


**接口地址**:`/api/course-selections/{id}`


**请求方式**:`DELETE`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>根据选课记录ID删除指定的选课记录</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id|选课记录ID|path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|204|No Content||


**响应参数**:


暂无


**响应示例**:
```javascript

```


## 获取课程选课记录


**接口地址**:`/api/course-selections/course/{id}`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>根据课程ID获取选择该课程的所有学生记录</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id|课程ID|path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|CourseSelection|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|id||integer(int64)|integer(int64)|
|studentId||integer(int64)|integer(int64)|
|courseId||integer(int64)|integer(int64)|


**响应示例**:
```javascript
[
	{
		"id": 0,
		"studentId": 0,
		"courseId": 0
	}
]
```


## 获取学生选课记录


**接口地址**:`/api/course-selections/student/{id}`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>根据学生ID获取该学生的所有选课记录</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id|学生ID|path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|CourseSelection|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|id||integer(int64)|integer(int64)|
|studentId||integer(int64)|integer(int64)|
|courseId||integer(int64)|integer(int64)|


**响应示例**:
```javascript
[
	{
		"id": 0,
		"studentId": 0,
		"courseId": 0
	}
]
```


# 用户管理


## 获取所有用户


**接口地址**:`/api/users`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>获取系统中所有用户的列表</p>



**请求参数**:


暂无


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|UserDataResponse|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|id||integer(int64)|integer(int64)|
|username||string||
|role||string||
|email||string||


**响应示例**:
```javascript
[
	{
		"id": 0,
		"username": "",
		"role": "",
		"email": ""
	}
]
```


## 创建用户


**接口地址**:`/api/users`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>创建一个新的用户账户</p>



**请求示例**:


```javascript
{
  "username": "",
  "password": "",
  "email": "",
  "role": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|createUserResponse|CreateUserResponse|body|true|CreateUserResponse|CreateUserResponse|
|&emsp;&emsp;username|||true|string||
|&emsp;&emsp;password|||true|string||
|&emsp;&emsp;email|||true|string||
|&emsp;&emsp;role|||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||


**响应参数**:


暂无


**响应示例**:
```javascript

```


## 根据ID获取用户


**接口地址**:`/api/users/{id}`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>根据用户ID获取单个用户的详细信息</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id|用户ID|path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|UserDataResponse|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|id||integer(int64)|integer(int64)|
|username||string||
|role||string||
|email||string||


**响应示例**:
```javascript
{
	"id": 0,
	"username": "",
	"role": "",
	"email": ""
}
```


## 更新用户信息


**接口地址**:`/api/users/{id}`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>根据用户ID更新用户的详细信息</p>



**请求示例**:


```javascript
{
  "id": 0,
  "username": "",
  "role": "",
  "email": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id|用户ID|path|true|integer(int64)||
|userDataResponse|UserDataResponse|body|true|UserDataResponse|UserDataResponse|
|&emsp;&emsp;id|||false|integer(int64)||
|&emsp;&emsp;username|||true|string||
|&emsp;&emsp;role|||true|string||
|&emsp;&emsp;email|||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||


**响应参数**:


暂无


**响应示例**:
```javascript

```


## 删除用户


**接口地址**:`/api/users/{id}`


**请求方式**:`DELETE`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>根据用户ID删除指定用户</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|id|用户ID|path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||


**响应参数**:


暂无


**响应示例**:
```javascript

```


## 用户登录


**接口地址**:`/api/users/login`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>用户使用用户名和密码进行登录认证</p>



**请求示例**:


```javascript
{
  "username": "",
  "password": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|loginResponse|LoginResponse|body|true|LoginResponse|LoginResponse|
|&emsp;&emsp;username|||true|string||
|&emsp;&emsp;password|||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||


**响应参数**:


暂无


**响应示例**:
```javascript

```


## 用户注册


**接口地址**:`/api/users/register`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>新用户注册账户</p>



**请求示例**:


```javascript
{
  "username": "",
  "password": "",
  "email": "",
  "role": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|createUserResponse|CreateUserResponse|body|true|CreateUserResponse|CreateUserResponse|
|&emsp;&emsp;username|||true|string||
|&emsp;&emsp;password|||true|string||
|&emsp;&emsp;email|||true|string||
|&emsp;&emsp;role|||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||


**响应参数**:


暂无


**响应示例**:
```javascript

```


## 获取所有学生


**接口地址**:`/api/users/students`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>获取系统中所有学生用户的列表</p>



**请求参数**:


暂无


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|UserDataResponse|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|id||integer(int64)|integer(int64)|
|username||string||
|role||string||
|email||string||


**响应示例**:
```javascript
[
	{
		"id": 0,
		"username": "",
		"role": "",
		"email": ""
	}
]
```


## 获取所有教师


**接口地址**:`/api/users/teachers`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>获取系统中所有教师用户的列表</p>



**请求参数**:


暂无


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|UserDataResponse|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|id||integer(int64)|integer(int64)|
|username||string||
|role||string||
|email||string||


**响应示例**:
```javascript
[
	{
		"id": 0,
		"username": "",
		"role": "",
		"email": ""
	}
]
```


# 阅卷管理


## 获取需要阅卷的提交


**接口地址**:`/api/grading/exam/{examId}/needs-grading`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>获取指定考试中需要阅卷的提交记录</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|examId|考试ID|path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|GradingDetailDto|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|submissionId||integer(int64)|integer(int64)|
|studentId||integer(int64)|integer(int64)|
|studentName||string||
|examId||integer(int64)|integer(int64)|
|examTitle||string||
|submitTime||string(date-time)|string(date-time)|
|isGraded||boolean||
|totalScore||integer(int32)|integer(int32)|
|currentScore||integer(int32)|integer(int32)|
|questions||array|QuestionAnswerDto|
|&emsp;&emsp;questionId||integer(int64)||
|&emsp;&emsp;questionContent||string||
|&emsp;&emsp;questionType||string||
|&emsp;&emsp;correctAnswer||string||
|&emsp;&emsp;studentAnswer||string||
|&emsp;&emsp;maxScore||integer(int32)||
|&emsp;&emsp;currentScore||integer(int32)||


**响应示例**:
```javascript
[
	{
		"submissionId": 0,
		"studentId": 0,
		"studentName": "",
		"examId": 0,
		"examTitle": "",
		"submitTime": "",
		"isGraded": true,
		"totalScore": 0,
		"currentScore": 0,
		"questions": [
			{
				"questionId": 0,
				"questionContent": "",
				"questionType": "",
				"correctAnswer": "",
				"studentAnswer": "",
				"maxScore": 0,
				"currentScore": 0
			}
		]
	}
]
```


## 获取考试的所有提交


**接口地址**:`/api/grading/exam/{examId}/submissions`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>获取指定考试的所有学生提交记录</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|examId|考试ID|path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|GradingDetailDto|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|submissionId||integer(int64)|integer(int64)|
|studentId||integer(int64)|integer(int64)|
|studentName||string||
|examId||integer(int64)|integer(int64)|
|examTitle||string||
|submitTime||string(date-time)|string(date-time)|
|isGraded||boolean||
|totalScore||integer(int32)|integer(int32)|
|currentScore||integer(int32)|integer(int32)|
|questions||array|QuestionAnswerDto|
|&emsp;&emsp;questionId||integer(int64)||
|&emsp;&emsp;questionContent||string||
|&emsp;&emsp;questionType||string||
|&emsp;&emsp;correctAnswer||string||
|&emsp;&emsp;studentAnswer||string||
|&emsp;&emsp;maxScore||integer(int32)||
|&emsp;&emsp;currentScore||integer(int32)||


**响应示例**:
```javascript
[
	{
		"submissionId": 0,
		"studentId": 0,
		"studentName": "",
		"examId": 0,
		"examTitle": "",
		"submitTime": "",
		"isGraded": true,
		"totalScore": 0,
		"currentScore": 0,
		"questions": [
			{
				"questionId": 0,
				"questionContent": "",
				"questionType": "",
				"correctAnswer": "",
				"studentAnswer": "",
				"maxScore": 0,
				"currentScore": 0
			}
		]
	}
]
```


## 获取提交详情


**接口地址**:`/api/grading/submission/{submissionId}`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>获取指定提交的详细信息，包括试题和答案</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|submissionId|提交ID|path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|GradingDetailDto|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|submissionId||integer(int64)|integer(int64)|
|studentId||integer(int64)|integer(int64)|
|studentName||string||
|examId||integer(int64)|integer(int64)|
|examTitle||string||
|submitTime||string(date-time)|string(date-time)|
|isGraded||boolean||
|totalScore||integer(int32)|integer(int32)|
|currentScore||integer(int32)|integer(int32)|
|questions||array|QuestionAnswerDto|
|&emsp;&emsp;questionId||integer(int64)||
|&emsp;&emsp;questionContent||string||
|&emsp;&emsp;questionType||string||
|&emsp;&emsp;correctAnswer||string||
|&emsp;&emsp;studentAnswer||string||
|&emsp;&emsp;maxScore||integer(int32)||
|&emsp;&emsp;currentScore||integer(int32)||


**响应示例**:
```javascript
{
	"submissionId": 0,
	"studentId": 0,
	"studentName": "",
	"examId": 0,
	"examTitle": "",
	"submitTime": "",
	"isGraded": true,
	"totalScore": 0,
	"currentScore": 0,
	"questions": [
		{
			"questionId": 0,
			"questionContent": "",
			"questionType": "",
			"correctAnswer": "",
			"studentAnswer": "",
			"maxScore": 0,
			"currentScore": 0
		}
	]
}
```


## 完成阅卷


**接口地址**:`/api/grading/submission/{submissionId}/complete`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>标记指定提交的阅卷为完成状态</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|submissionId|提交ID|path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||


**响应参数**:


暂无


**响应示例**:
```javascript

```


## 更新单题得分


**接口地址**:`/api/grading/submission/{submissionId}/score`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>更新指定提交中某道题的得分</p>



**请求示例**:


```javascript
{
  "questionId": 0,
  "score": 0
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|submissionId|提交ID|path|true|integer(int64)||
|updateScoreDto|UpdateScoreDto|body|true|UpdateScoreDto|UpdateScoreDto|
|&emsp;&emsp;questionId|||true|integer(int64)||
|&emsp;&emsp;score|||true|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||


**响应参数**:


暂无


**响应示例**:
```javascript

```


# AI辅助阅卷


## AI评分


**接口地址**:`/api/ai-grading/grade`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>使用AI对学生答案进行评分</p>



**请求示例**:


```javascript
{
  "考试科目": "",
  "题目": "",
  "参考答案": "",
  "题目总分": "",
  "学生答案": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|aiGradingRequest|AI评分请求|body|true|AiGradingRequest|AiGradingRequest|
|&emsp;&emsp;考试科目|||true|string||
|&emsp;&emsp;题目|||true|string||
|&emsp;&emsp;参考答案|||true|string||
|&emsp;&emsp;题目总分|||true|string||
|&emsp;&emsp;学生答案|||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ApiResponseAiGradingResponse|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||AiGradingResponse|AiGradingResponse|
|&emsp;&emsp;得分||string||
|&emsp;&emsp;评分依据||string||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": {
		"得分": "",
		"评分依据": ""
	}
}
```


## AI流式评分


**接口地址**:`/api/ai-grading/grade/stream`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`text/event-stream`


**接口描述**:<p>使用AI对学生答案进行流式评分</p>



**请求示例**:


```javascript
{
  "subject": "",
  "question": "",
  "referenceAnswer": "",
  "totalScore": 0,
  "studentAnswer": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|streamAiGradingRequest|流式AI评分请求|body|true|StreamAiGradingRequest|StreamAiGradingRequest|
|&emsp;&emsp;subject|||true|string||
|&emsp;&emsp;question|||true|string||
|&emsp;&emsp;referenceAnswer|||true|string||
|&emsp;&emsp;totalScore|||true|integer(int32)||
|&emsp;&emsp;studentAnswer|||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|SseEmitter|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|timeout||integer(int64)|integer(int64)|


**响应示例**:
```javascript
{
	"timeout": 0
}
```


## 测试AI连接


**接口地址**:`/api/ai-grading/test`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>测试与阿里云DashScope的连接</p>



**请求参数**:


暂无


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ApiResponseString|


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code||integer(int32)|integer(int32)|
|message||string||
|data||string||


**响应示例**:
```javascript
{
	"code": 0,
	"message": "",
	"data": ""
}
```