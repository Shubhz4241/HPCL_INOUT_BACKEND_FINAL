package com.hpcl.inout.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.hpcl.inout.entity.PasswordReset;


@Repository
public interface PasswordResetRepository extends CrudRepository<PasswordReset, Long> 
{
	PasswordReset findFirstByOrderByIdDesc();
}
