package com.example.demo.batch;

import java.io.IOException;

import javax.persistence.EntityManagerFactory;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternUtils;

import com.example.demo.domain.Dept;
import com.example.demo.dto.TwoDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class CsvToJpaJob2 {
	
	private final ResourceLoader resourceLoader; //멀티파일을 처리하기 위해 필요

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	private final EntityManagerFactory entityManagerFactory;
	
	private int chunkSize = 10;
	
	@Bean
	public Job csvToJpaJob2_batchBuild() throws Exception {
		return jobBuilderFactory.get("csvToJpaJob2")
				.start(csvToJpaJob2_batchStep1())
				.build();
	}

	@Bean
	public Step csvToJpaJob2_batchStep1() throws Exception{
		return stepBuilderFactory.get("csvToJpaJob2_batchStep1")
				.<TwoDto,Dept>chunk(chunkSize) //<in,out>
				.reader(csvToJpaJob2_FileReader())
				.processor(csvToJpaJob2_processor())
				.writer(csvToJpaJob2_dbItemWriter())
				.faultTolerant()
				.skip(FlatFileParseException.class)
				.skipLimit(2)
				.build();
	}
	
	@Bean
	public MultiResourceItemReader<TwoDto> csvToJpaJob2_FileReader(){
		MultiResourceItemReader<TwoDto> multiResourceItemReader = new MultiResourceItemReader<>();
		
		try {
			multiResourceItemReader.setResources(
					ResourcePatternUtils.getResourcePatternResolver(this.resourceLoader).getResources(
						"classpath:sample/csvToJpaJob2/*.txt"	//classpath 소문자
					)
			);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		multiResourceItemReader.setDelegate(multifileItemReader());
		
		return multiResourceItemReader;
	}
	
	@Bean
	public FlatFileItemReader<TwoDto> multifileItemReader(){
		FlatFileItemReader<TwoDto> flatFileItemReader = new FlatFileItemReader<>();
		
		flatFileItemReader.setLineMapper((line,lineNumber)->{
			String [] lines = line.split("#");
			
			return new TwoDto(lines[0],lines[1]);
		});
		
		return flatFileItemReader;
	}
	
	@Bean
	public ItemProcessor<TwoDto, Dept> csvToJpaJob2_processor(){
		return twoDto -> new Dept(Integer.parseInt(twoDto.getOne()),twoDto.getTwo(),"기타");
	}
	
	@Bean
	public JpaItemWriter<Dept> csvToJpaJob2_dbItemWriter(){
		JpaItemWriter<Dept> jpaItemWriter = new JpaItemWriter<>();
		jpaItemWriter.setEntityManagerFactory(entityManagerFactory);//우선 디비 조회후 dept 인덱스에 대한 데이터가 있으면 업데이트로 처리가 되네 
		return jpaItemWriter;
		
	}
}
