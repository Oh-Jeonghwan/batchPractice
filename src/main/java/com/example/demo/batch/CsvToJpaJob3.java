package com.example.demo.batch;

import javax.persistence.EntityManagerFactory;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.separator.SimpleRecordSeparatorPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.ResourceLoader;

import com.example.demo.domain.Two;
import com.example.demo.dto.TwoDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class CsvToJpaJob3 {
	
	private final ResourceLoader resourceLoader; //멀티파일을 처리하기 위해 필요

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	private final EntityManagerFactory entityManagerFactory;
	
	private int chunkSize = 10;
	
	@Bean
	public Job csvToJpaJob3_batchBuild() throws Exception {
		return jobBuilderFactory.get("csvToJpaJob3")
				.start(csvToJpaJob3_batchStep1())
				.build();
	}

	@Bean
	public Step csvToJpaJob3_batchStep1() throws Exception{
		return stepBuilderFactory.get("csvToJpaJob3_batchStep1")
				.<TwoDto,Two>chunk(chunkSize) //<in,out>
				.reader(csvToJpaJob3_FileReader(null))
				.processor(csvToJpaJob3_processor())
				.writer(csvToJpaJob3_dbItemWriter())
				//.faultTolerant()
				//.skip(FlatFileParseException.class)
				//.skipLimit(2)
				.build();
	}
	
	@Bean
	@StepScope
	public FlatFileItemReader<TwoDto> csvToJpaJob3_FileReader(@Value("#{jobParameters[inFileName]}")String inFileName){
		return new FlatFileItemReaderBuilder<TwoDto>()
				.name("mulitiJob1_Reader")
				.resource(new FileSystemResource(inFileName))
				.delimited().delimiter(":")
				.names("one","two")
				.targetType(TwoDto.class)
				.recordSeparatorPolicy(
					new SimpleRecordSeparatorPolicy() {
						@Override
						public String postProcess(String record) {
							log.info("policy: " + record);
							//파서 대상이 아니면 무시해줌
							if(record.indexOf(":")==-1) {
								return null;
							}
							return record.trim();
						}
					}
//					(SimpleRecordSeparatorPolicy) postProcess(record)->{
//						log.info("policy: " + record);
//						//파서 대상이 아니면 무시해줌
//						if(record.indexOf(":")==-1) {
//							return null;
//						}
//						return record.trim();
//					}
				).build();
	}
	
	@Bean
	public ItemProcessor<TwoDto, Two> csvToJpaJob3_processor(){
		return twoDto -> new Two(twoDto.getOne(),twoDto.getTwo());
	}
	
	@Bean
	public JpaItemWriter<Two> csvToJpaJob3_dbItemWriter(){
		JpaItemWriter<Two> jpaItemWriter = new JpaItemWriter<>();
		jpaItemWriter.setEntityManagerFactory(entityManagerFactory);//우선 디비 조회후 dept 인덱스에 대한 데이터가 있으면 업데이트로 처리가 되네 
		return jpaItemWriter;
		
	}
}
