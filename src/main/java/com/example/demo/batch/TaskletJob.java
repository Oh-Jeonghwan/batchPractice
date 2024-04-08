package com.example.demo.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class TaskletJob {
	
	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	
	@Bean
	public Job taskletJob_batchBuild() {
		return jobBuilderFactory.get("taskletJob")
				.start(taskletJob_step1())
				.next(taskletJob_step2(null))
				.build();
	}
	
	@Bean
	public Step taskletJob_step1() {
		return stepBuilderFactory.get("taskletJob_step1")
				.tasklet((stepContribution,chunkContext)->{ 
				/*StepContribution: Step의 실행 과정에서 기여(contribution)를 나타내는 객체입니다.
				StepContribution은 Step의 상태를 변경하고, Step의 완료 여부를 나타내는 데 사용됩니다.
				주로 Step에서 생성된 아이템 수, 아이템 처리 결과 등을 추적하는 데 사용됩니다.
				
				ChunkContext:Chunk(청크) 단위의 실행을 관리하는 컨텍스트입니다.
				ChunkContext는 청크의 시작과 끝을 추적하고, 청크 실행 중 발생하는 상태를 관리하는 데 사용됩니다.
				주로 Step의 특정 청크의 상태를 추적하거나 청크 간의 상태를 공유하는 데 사용됩니다.
					 * */
					log.info("-> job -> step1");
					log.info(">>>>>>>>>>>>>>>>>>"+stepContribution.toString());
					log.info(">>>>>>>>>>>>>>>>>>"+chunkContext.toString());
					return RepeatStatus.FINISHED;
				}).build();
	}
	
	@Bean
	@JobScope
	public Step taskletJob_step2(@Value("#{jobParameters[date]}") String date) {
		return stepBuilderFactory.get("taskletJob_step2")
				.tasklet((stepContribution,chunkContext)->{ 
					log.info("-> step1 -> step2" + date);
					log.info(">>>>>>>>>>>>>>>>>>"+stepContribution.toString());
					log.info(">>>>>>>>>>>>>>>>>>"+chunkContext.toString());
					return RepeatStatus.FINISHED;
				}).build();
	}
}
