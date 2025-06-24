package com.hpcl.inout.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hpcl.inout.entity.PasswordReset;
import com.hpcl.inout.repository.PasswordResetRepository;


@Service
public class PasswordResetService {
  @Autowired
  private PasswordResetRepository passwordResetRepository;
  
  public PasswordReset getPasswordResetEntity() {
    PasswordReset entity = this.passwordResetRepository.findFirstByOrderByIdDesc();
    if (entity == null)
      return new PasswordReset(); 
    return entity;
  }
  
  public void resetPassword(PasswordReset passwordResetEntity, String newPassword) {
    passwordResetEntity.setResetPassword(newPassword);
    this.passwordResetRepository.save(passwordResetEntity);
  }
}
