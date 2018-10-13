package com.shihai.reservation.red.spider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;


@ComponentScan(basePackages={"com.shihai.reservation"})
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class,MongoAutoConfiguration.class})
@EnableScheduling
public class RedSpiderMain {
	private static Logger logger = LoggerFactory
			.getLogger(RedSpiderMain.class);

	public static void main(String[] args){
		
		SpringApplication.run(RedSpiderMain.class, args);
		
	}
}

