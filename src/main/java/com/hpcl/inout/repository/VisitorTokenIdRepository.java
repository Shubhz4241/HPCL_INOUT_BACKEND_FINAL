package com.hpcl.inout.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hpcl.inout.entity.VisitorTokenId;


@Repository
public interface VisitorTokenIdRepository extends JpaRepository<VisitorTokenId, Long> {
  VisitorTokenId findFirstByOrderByIdDesc();
  Optional<VisitorTokenId> findByCurrSrNo(Long currSrNo);
//  VisitorTokenId findById(String id);
}