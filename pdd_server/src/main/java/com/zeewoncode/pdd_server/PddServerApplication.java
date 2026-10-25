package com.zeewoncode.pdd_server;

import com.zeewoncode.utils.FullBeanNameGenerator;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(scanBasePackages = "com.zeewoncode")
@MapperScan("com.zeewoncode.pdd_server.mapper")
@ComponentScan(basePackages = "com.zeewoncode", nameGenerator = FullBeanNameGenerator.class)
public class PddServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(PddServerApplication.class, args);
	}

}
