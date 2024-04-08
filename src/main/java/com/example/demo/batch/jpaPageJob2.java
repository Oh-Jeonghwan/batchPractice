package com.example.demo.batch;

import javax.persistence.EntityManagerFactory;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.demo.domain.Dept;
import com.example.demo.domain.Dept2;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class jpaPageJob2 {

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	private final EntityManagerFactory entityManagerFactory;
	
	private int chunkSize = 10;
	
	@Bean
	public Job JpaPageJob2_batchBuild() {
		return jobBuilderFactory.get("JpaPageJob2")
				.start(JpaPageJob2_step1())
				.build();
	}
	
	@Bean
	@JobScope
	public Step JpaPageJob2_step1() {
		return stepBuilderFactory.get("JpaPageJob2_step1")
				.<Dept,Dept2>chunk(chunkSize)
				.reader(JpaPageJob2_dbItemReader())
				.processor(JpaPageJob2_process())
				.writer(JpaPageJob2_dbItemtWriter())
				.build();
	}
	
	@Bean
	public JpaPagingItemReader<Dept> JpaPageJob2_dbItemReader(){
		return new JpaPagingItemReaderBuilder<Dept>()
				.name("JpaPageJob2_dbItemReader")
				.entityManagerFactory(entityManagerFactory)
				.pageSize(chunkSize)
				.queryString("SELECT d FROM Dept d ORDER BY deptNo")
				.build();
	}
	
	private ItemProcessor<Dept,Dept2> JpaPageJob2_process(){
		return dept -> {
			return new Dept2(dept.getDeptNo(),"NEW_"+dept.getDName(),"NEW_"+dept.getLoc());
		};
	}
	
	@Bean
	public JpaItemWriter<Dept2> JpaPageJob2_dbItemtWriter(){
		JpaItemWriter<Dept2> jpaItemWriter = new JpaItemWriter<>();
		jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
		return jpaItemWriter;
	}
	
}
