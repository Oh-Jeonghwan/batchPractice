package com.example.demo.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

import com.example.demo.repository.DeptRespository;

@SpringBootTest
public class testDeptRepository {
	
	@Autowired
	DeptRespository deptRepository;
	
	@Test
	@Commit
	public void dept01() {
		for(int i=0; i<101; i++) {
			deptRepository.save(new Dept(i,"dName_"+String.valueOf(i),"loc_"+String.valueOf(i)));
		}
	}
}
