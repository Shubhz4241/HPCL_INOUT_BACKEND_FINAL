package com.hpcl.inout.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.math.BigInteger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DriverService 
{
	@Autowired
	private EntityManager entityManager;

	@Transactional(readOnly = true)
	public Long getDriverTotalRecordCount() {
		String query = "SELECT (SELECT COUNT(*) FROM packed WHERE full_name IS NOT NULL) + (SELECT COUNT(*) FROM bulk WHERE full_name IS NOT NULL) + (SELECT COUNT(*) FROM transportor WHERE full_name IS NOT NULL) AS total_records";
		return executeCountQuery(query);
	}

	private Long executeCountQuery(String query) {
		Query nativeQuery = this.entityManager.createNativeQuery(query);
		Object result = nativeQuery.getSingleResult();
		if (result instanceof BigInteger)
			return Long.valueOf(((BigInteger)result).longValue()); 
		if (result instanceof Long)
			return (Long)result; 
		throw new IllegalStateException("Unexpected result type: " + result.getClass());
	}
}
