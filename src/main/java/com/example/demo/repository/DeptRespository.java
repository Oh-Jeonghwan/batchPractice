package com.example.demo.repository;

import org.springframework.data.repository.CrudRepository;

import com.example.demo.domain.Dept;

public interface DeptRespository extends CrudRepository<Dept, Long>{
	
}
