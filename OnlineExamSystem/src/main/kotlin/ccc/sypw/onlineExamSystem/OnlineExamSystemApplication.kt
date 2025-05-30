package ccc.sypw.onlineExamSystem

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.ServletComponentScan

//@SpringBootApplication (exclude = [DataSourceAutoConfiguration::class])
@SpringBootApplication
@ServletComponentScan
class OnlineExamSystemApplication
fun main(args: Array<String>) {
	runApplication<OnlineExamSystemApplication>(*args)
}
