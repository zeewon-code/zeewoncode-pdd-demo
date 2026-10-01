package com.zeewoncode.pdd_server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.zeewoncode")
@MapperScan("com.zeewoncode.pdd_server.mapper")
public class PddServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(PddServerApplication.class, args);
	}

}
