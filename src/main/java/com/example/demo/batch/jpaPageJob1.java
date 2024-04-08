package com.example.demo.batch;

import javax.persistence.EntityManagerFactory;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.demo.domain.Dept;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class jpaPageJob1 {

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	private final EntityManagerFactory entityManagerFactory;
	
	private int chunkSize = 10;
	
	@Bean
	public Job JpaPageJob1_batchBuild() {
		return jobBuilderFactory.get("JpaPageJob1")
				.start(JpaPageJob1_step1())
				.build();
	}
	
	@Bean
	@JobScope
	public Step JpaPageJob1_step1() {
		return stepBuilderFactory.get("JpaPageJob1_step1")
				.<Dept,Dept>chunk(chunkSize)
				.reader(JpaPageJob1_dbItemReader())
				.writer(JpaPageJob1_printWriter())
				.build();
	}
	
	@Bean
	public JpaPagingItemReader<Dept> JpaPageJob1_dbItemReader(){
		return new JpaPagingItemReaderBuilder<Dept>()
				.name("JpaPageJob1_dbItemReader")
				.entityManagerFactory(entityManagerFactory)
				.pageSize(chunkSize)
				.queryString("SELECT d FROM Dept d ORDER BY deptNo")
				.build();
	}
	
	@Bean
	public ItemWriter<Dept> JpaPageJob1_printWriter(){
		return depts -> {
			for(Dept dept:depts) {
				log.info("deptNo: "+ dept.getDeptNo()+
						 " dName: "+dept.getDName()+
						 " loc: "+dept.getLoc());
			}
		};
	}
	
}
