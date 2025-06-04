package ccc.sypw.onlineExamSystem.util

import ccc.sypw.onlineExamSystem.dto.ExamSubmissionResponse
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.stereotype.Component
import java.io.ByteArrayOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Excel导出工具类
 */
@Component
class ExcelExportUtils {

    /**
     * 导出考试成绩到Excel
     * @param submissions 考试提交记录列表
     * @param examTitle 考试标题
     * @return Excel文件的字节数组
     */
    fun exportExamScoresToExcel(submissions: List<ExamSubmissionResponse>, examTitle: String): ByteArray {
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("考试成绩")
        
        var rowNum = 0
        
        // 创建标题行
        val titleRow = sheet.createRow(rowNum++)
        val titleCell = titleRow.createCell(0)
        titleCell.setCellValue("$examTitle - 考试成绩统计")
        
        // 合并标题单元格
        sheet.addMergedRegion(org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 6))
        
        // 空行
        rowNum++
        
        // 创建表头
        val headerRow = sheet.createRow(rowNum++)
        val headers = arrayOf("序号", "学生姓名", "学生ID", "考试总分", "获得分数", "得分率", "阅卷状态")
        
        headers.forEachIndexed { index, header ->
            val cell = headerRow.createCell(index)
            cell.setCellValue(header)
        }
        
        // 填充数据
        submissions.forEachIndexed { index, submission ->
            val dataRow = sheet.createRow(rowNum++)
            
            // 序号
            dataRow.createCell(0).setCellValue((index + 1).toDouble())
            
            // 学生姓名
            dataRow.createCell(1).setCellValue(submission.userName ?: "未知")
            
            // 学生ID
            dataRow.createCell(2).setCellValue(submission.userId.toDouble())
            
            // 考试总分
            dataRow.createCell(3).setCellValue((submission.examTotalScore ?: 0).toDouble())
            
            // 获得分数
            dataRow.createCell(4).setCellValue((submission.submissionScore ?: 0).toDouble())
            
            // 得分率
            val totalScore = submission.examTotalScore ?: 1
            val submissionScore = submission.submissionScore ?: 0
            val rate = if (totalScore > 0) (submissionScore.toDouble() / totalScore * 100) else 0.0
            dataRow.createCell(5).setCellValue(String.format("%.2f%%", rate))
            
            // 阅卷状态
            dataRow.createCell(6).setCellValue(if (submission.isGraded) "已阅卷" else "未阅卷")
        }
        
        // 自动调整列宽
        for (i in 0 until headers.size) {
            sheet.autoSizeColumn(i)
        }
        
        // 添加统计信息
        rowNum++
        val statsRow = sheet.createRow(rowNum)
        val statsCell = statsRow.createCell(0)
        val totalStudents = submissions.size
        val gradedStudents = submissions.count { it.isGraded }
        val avgScore = if (submissions.isNotEmpty()) {
            submissions.mapNotNull { it.submissionScore }.average()
        } else 0.0
        
        statsCell.setCellValue("统计信息: 总人数: $totalStudents, 已阅卷: $gradedStudents, 平均分: ${String.format("%.2f", avgScore)}")
        sheet.addMergedRegion(org.apache.poi.ss.util.CellRangeAddress(rowNum, rowNum, 0, 6))
        
        // 转换为字节数组
        val outputStream = ByteArrayOutputStream()
        workbook.write(outputStream)
        workbook.close()
        
        return outputStream.toByteArray()
    }
    
    /**
     * 生成Excel文件名
     * @param examTitle 考试标题
     * @return 文件名
     */
    fun generateFileName(examTitle: String): String {
        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
        return "scores_$timestamp.xlsx"
    }
}