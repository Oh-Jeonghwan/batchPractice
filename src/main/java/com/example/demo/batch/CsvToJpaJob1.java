package com.example.demo.batch;

import javax.persistence.EntityManagerFactory;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.example.demo.domain.Dept;
import com.example.demo.dto.TwoDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class CsvToJpaJob1 {

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	private final EntityManagerFactory entityManagerFactory;
	
	private int chunkSize = 10;
	
	@Bean
	public Job csvToJpaJob1_batchBuild() throws Exception {
		return jobBuilderFactory.get("csvToJpaJob1")
				.start(csvToJpaJob1_batchStep1())
				.build();
	}

	@Bean
	public Step csvToJpaJob1_batchStep1() throws Exception{
		return stepBuilderFactory.get("csvToJpaJob1_batchStep1")
				.<TwoDto,Dept>chunk(chunkSize) //<in,out>
				.reader(csvToJpaJob1_FileReader())
				.processor(csvTojpaJob1_processor())
				.writer(csvToJpaJob1_dbItemWriter())
				.build();
	}
	
	@Bean
	public FlatFileItemReader<TwoDto> csvToJpaJob1_FileReader(){
		FlatFileItemReader<TwoDto> flatFileItemReader = new FlatFileItemReader<>();
		flatFileItemReader.setResource(new ClassPathResource("/sample/csvJob1_input.csv"));
		flatFileItemReader.setLinesToSkip(1);//첫번째 줄 컬럼은 생략해서 읽어온다.
		
		DefaultLineMapper<TwoDto> dtoDefaultLineMapper = new DefaultLineMapper<>(); //DefaultLineMapper는 이러한 텍스트 파일을 읽고 각 레코드를 도메인 객체로 매핑하는 데 필요한 구성을 제공합니다.
		
		DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer(); //행열로 구분된 파일은 delimiter(구분자)로 데이터 추출할 수 있도록
		delimitedLineTokenizer.setNames("onew","two");
		delimitedLineTokenizer.setDelimiter(":");
		
		BeanWrapperFieldSetMapper<TwoDto> beanWrapperFieldSetMapper = new BeanWrapperFieldSetMapper<>();
		beanWrapperFieldSetMapper.setTargetType(TwoDto.class);
		
		dtoDefaultLineMapper.setLineTokenizer(delimitedLineTokenizer);
		dtoDefaultLineMapper.setFieldSetMapper(beanWrapperFieldSetMapper);
		flatFileItemReader.setLineMapper(dtoDefaultLineMapper);
		
		return flatFileItemReader;
	}
	
	@Bean
	public ItemProcessor<TwoDto, Dept> csvTojpaJob1_processor(){
		return twoDto -> new Dept(Integer.parseInt(twoDto.getOne()),twoDto.getTwo(),"기타");
	}
	
	@Bean
	public JpaItemWriter<Dept> csvToJpaJob1_dbItemWriter(){
		JpaItemWriter<Dept> jpaItemWriter = new JpaItemWriter<>();
		jpaItemWriter.setEntityManagerFactory(entityManagerFactory);//우선 디비 조회후 dept 인덱스에 대한 데이터가 있으면 업데이트로 처리가 되네 
		return jpaItemWriter;
		
	}
}
