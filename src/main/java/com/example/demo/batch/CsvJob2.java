package com.example.demo.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import com.example.demo.custom.CustomBeanWrapperFieldExtractor;
import com.example.demo.dto.TwoDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class CsvJob2 {

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	
	private int chunkSize = 5;
	
	@Bean
	public Job csvJob2_batchBuild() {
		return jobBuilderFactory.get("csvJob2")
				.start(csvJob2_batchStep1())
				.build();
				
	}
	
	@Bean
	public Step csvJob2_batchStep1() {
		return stepBuilderFactory.get("csvJob2_batchStep1")
				.<TwoDto,TwoDto>chunk(chunkSize)
				.reader(csvJob1_FileReader())
				.writer(csvJob1_FileWriter(new FileSystemResource("output/csvJob2_output.csv")))
				.build();
	}
	
	@Bean
	public FlatFileItemReader<TwoDto> csvJob1_FileReader(){
		FlatFileItemReader<TwoDto> flatFileItemReader = new FlatFileItemReader<>();
		flatFileItemReader.setResource(new ClassPathResource("/sample/csvJob2_input.csv"));
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
	public FlatFileItemWriter<TwoDto> csvJob1_FileWriter (Resource resource) {
		
		CustomBeanWrapperFieldExtractor<TwoDto> beanWrapperFieldExtractor = new CustomBeanWrapperFieldExtractor<>();
		beanWrapperFieldExtractor.setNames(new String[] {"one","two"});
		beanWrapperFieldExtractor.afterPropertiesSet();
		
		DelimitedLineAggregator<TwoDto> delimitedLineAggregator = new DelimitedLineAggregator<>();
		delimitedLineAggregator.setDelimiter("@");
		delimitedLineAggregator.setFieldExtractor(beanWrapperFieldExtractor);
		
		return new FlatFileItemWriterBuilder<TwoDto>().name("csvJob1_FileWriter")
				.resource(resource)
				.lineAggregator(delimitedLineAggregator)
				.build();
	}

}
