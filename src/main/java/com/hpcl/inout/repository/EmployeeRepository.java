package com.hpcl.inout.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.hpcl.inout.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
  Employee findByUniqueId(String paramString);
}
