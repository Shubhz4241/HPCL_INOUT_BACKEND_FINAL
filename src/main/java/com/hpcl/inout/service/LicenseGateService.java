package com.hpcl.inout.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hpcl.inout.entity.Licensegate;
import com.hpcl.inout.repository.LicensegateRepository;

@Service
public class LicenseGateService {
	@Autowired
	private LicensegateRepository licensegateRepository;

	public void saveDetailsToLicenseGate(String details, String name, String uniqueId, String mobile, String address,
			Long ofcid, String department, String sub_department, String firmName, String contractor) {
		Licensegate licensegate = new Licensegate();
		licensegate.setDetails(details);
		licensegate.setOfcid(ofcid);
		licensegate.setName(name);
		licensegate.setUniqueId(uniqueId);
		licensegate.setMobile(mobile);
		licensegate.setAddress(address);
		licensegate.setDepartment(department);
		licensegate.setSub_department(sub_department);
		licensegate.setContractor(contractor);
		licensegate.setFirmName(firmName);

		this.licensegateRepository.save(licensegate);
	}

	public List<Licensegate> getAllLicensegateDetails() {
		return this.licensegateRepository.findAll();
	}

	public Licensegate findByUniqueId(String uniqueId) {
		return this.licensegateRepository.findByUniqueId(uniqueId);
	}

	public void updateDetailsToLicenseGate(Licensegate licensegate) {
		licensegate.setExitDateTime(new Date());
		this.licensegateRepository.save(licensegate);
	}

	public List<Licensegate> findByEntryDateTimeBetweenOrderByDetailsForOperationLicenseGate() {
		LocalDateTime currentDateTime = LocalDateTime.now();
		Date currentDate = Date.from(currentDateTime.atZone(ZoneId.systemDefault()).toInstant());
		LocalDateTime startOfDay = currentDateTime.toLocalDate().atStartOfDay();
		LocalDateTime endOfDay = startOfDay.plusDays(1L);
		Date startDate = Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
		Date endDate = Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant());
		return this.licensegateRepository.findByEntryDateTimeBetweenOrderByDetailsForOperation(startDate, endDate);
	}

	public Long countByEntryDateTimeBetweenForOperationLicenseGate() {
		LocalDateTime currentDateTime = LocalDateTime.now();
		Date startDate = Date
				.from(currentDateTime.toLocalDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
		Date endDate = Date.from(
				currentDateTime.plusDays(1L).toLocalDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
		return this.licensegateRepository.countByEntryDateTimeBetweenForOperation(startDate, endDate);
	}

	public List<Licensegate> getAllLicensegateDetailsForCurrentDay() {
		LocalDateTime currentDateTime = LocalDateTime.now();
		Date currentDate = Date.from(currentDateTime.atZone(ZoneId.systemDefault()).toInstant());
		LocalDateTime startOfDay = currentDateTime.toLocalDate().atStartOfDay();
		LocalDateTime endOfDay = startOfDay.plusDays(1L);
		Date startDate = Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
		Date endDate = Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant());
		return this.licensegateRepository.findByEntryDateTimeBetweenOrderByDetails(startDate, endDate);
	}

	public Long getCountOfLicensegateDetailsForCurrentDay() {
		LocalDateTime currentDateTime = LocalDateTime.now();
		Date currentDate = Date.from(currentDateTime.atZone(ZoneId.systemDefault()).toInstant());
		LocalDateTime startOfDay = currentDateTime.toLocalDate().atStartOfDay();
		LocalDateTime endOfDay = startOfDay.plusDays(1L);
		Date startDate = Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
		Date endDate = Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant());
		return this.licensegateRepository.countByEntryDateTimeBetween(startDate, endDate);
	}

	public List<Licensegate> findByEntryDateTimeBetweenOrderByDetailsForProjectLicensegate() {
		LocalDateTime currentDateTime = LocalDateTime.now();
		Date currentDate = Date.from(currentDateTime.atZone(ZoneId.systemDefault()).toInstant());
		LocalDateTime startOfDay = currentDateTime.toLocalDate().atStartOfDay();
		LocalDateTime endOfDay = startOfDay.plusDays(1L);
		Date startDate = Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
		Date endDate = Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant());
		return this.licensegateRepository.findByEntryDateTimeBetweenOrderByDetailsForProject(startDate, endDate);
	}

	public Long countByEntryDateTimeBetweenForProjectLicensegate() {
		LocalDateTime currentDateTime = LocalDateTime.now();
		Date startDate = Date
				.from(currentDateTime.toLocalDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
		Date endDate = Date.from(
				currentDateTime.plusDays(1L).toLocalDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
		return this.licensegateRepository.countByEntryDateTimeBetweenForProject(startDate, endDate);
	}

	public List<Licensegate> findByEntryDateTimeBetweenOrderByDetailsForVisitorLicensegate() {
		LocalDateTime currentDateTime = LocalDateTime.now();
		Date currentDate = Date.from(currentDateTime.atZone(ZoneId.systemDefault()).toInstant());
		LocalDateTime startOfDay = currentDateTime.toLocalDate().atStartOfDay();
		LocalDateTime endOfDay = startOfDay.plusDays(1L);
		Date startDate = Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
		Date endDate = Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant());
		return this.licensegateRepository.findByEntryDateTimeBetweenOrderByDetailsForVisitor(startDate, endDate);
	}

	public Long countByEntryDateTimeBetweenForVisitorLicensegate() {
		LocalDateTime currentDateTime = LocalDateTime.now();
		Date startDate = Date
				.from(currentDateTime.toLocalDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
		Date endDate = Date.from(
				currentDateTime.plusDays(1L).toLocalDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
		return this.licensegateRepository.countByEntryDateTimeBetweenForVisitor(startDate, endDate);
	}

	public List<Licensegate> generateReportDataLicenseGate(String fromDate, String toDate, String department,
			String subDepartment, String name) {
		LocalDateTime startDateTime = LocalDateTime.parse(fromDate + "T00:00");
		LocalDateTime endDateTime = LocalDateTime.parse(toDate + "T23:59");
		Date startDate = Date.from(startDateTime.atZone(ZoneId.systemDefault()).toInstant());
		Date endDate = Date.from(endDateTime.atZone(ZoneId.systemDefault()).toInstant());

		List<String> filters = new LinkedList<>();
		if (startDate != null)
			filters.add(" AND li._date_time >= '" + fromDate + "'");
		if (endDate != null)
			filters.add(" AND li.exit_date_time <= '" + toDate + "'");
		if (department != null && !department.isEmpty())
			filters.add(" AND li.department = '" + department + "'");
		if (subDepartment != null && !subDepartment.isEmpty())
			filters.add(" AND li.sub_department = '" + subDepartment + "'");

		String finalQuery = "SELECT * FROM Licensegate li WHERE li.unique_id IS NOT NULL ";
		for (String filter : filters) {
			finalQuery = finalQuery + filter;
		}

		if (!"All".equalsIgnoreCase(name)) {
			finalQuery = finalQuery + " AND LOWER(li.name) LIKE LOWER(CONCAT('%', '" + name + "', '%'))";
		}

		System.out.println("QUERY: " + finalQuery);

		if ("All".equals(subDepartment)) {
			return this.licensegateRepository.findByEntryDateTimeBetweenAndDepartmentOrderByDetails(startDate, endDate,
					department);
		}

		return null;
	}

	public void updateNullExiteTime() {
		List<Licensegate> nullExitDateTimeList = this.licensegateRepository.findByExitDateTimeIsNull();
		for (Licensegate license : nullExitDateTimeList)
			license.setExitDateTime(new Date());
		this.licensegateRepository.saveAll(nullExitDateTimeList);
	}
}
