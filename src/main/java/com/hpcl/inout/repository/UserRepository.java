package com.hpcl.inout.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.hpcl.inout.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> 
{
    List<User> findByRole(String role);
    
    User findByUserName(String userName);
}
