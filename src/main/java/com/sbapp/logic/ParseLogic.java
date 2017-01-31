package com.sbapp.logic;

import com.sbapp.configuration.Logging;
import com.sbapp.dao.ReportRepository;
import com.sbapp.dto.DepartmentDto;
import com.sbapp.dto.EmployeeDto;
import com.sbapp.dto.enumeration.SourceEnum;
import com.sbapp.dto.hibernate.ReportEntity;
import com.sbapp.util.ApplicationXmlMapper;
import com.sbapp.util.FileComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@Component
@Transactional
public class ParseLogic implements Logging {

	private final Environment environment;
	private final ReportRepository reportRepository;
	private final ApplicationXmlMapper xmlMapper;
	private final FileComponent fileComponent;

	@Autowired
	public ParseLogic(Environment environment, ReportRepository reportRepository, ApplicationXmlMapper xmlMapper, FileComponent fileComponent) {
		this.environment = environment;
		this.reportRepository = reportRepository;
		this.xmlMapper = xmlMapper;
		this.fileComponent = fileComponent;
	}

	@Scheduled(fixedDelay = 10000)
	public void processFolderFiles() {

		String inputFolderPath = environment.getProperty("process.input.folder");
		logger().info("Checking if there are any files to process in " + inputFolderPath);

		while (true) {

			File folder = fileComponent.newFile(inputFolderPath);
			File[] listOfFiles = folder.listFiles();

			if (listOfFiles == null || listOfFiles.length < 1) {
				logger().debug("No files to process.");
				return;
			}

			File fileToProcess = listOfFiles[0];
			logger().info("start to process file {}", fileToProcess.getName());

			DepartmentDto departmentDto;

			try {

				departmentDto = xmlMapper.readValue(fileToProcess, DepartmentDto.class);

			} catch (IOException e) {
				logger().error("converting file {} into java objects failed with error {}. Stopping progress", fileToProcess.getName(), e.getMessage());
				throw new RuntimeException("processing file " + fileToProcess.getAbsolutePath() + " failed", e);
			}

			processFile(departmentDto, SourceEnum.FOLDER, System.currentTimeMillis() + "-" + fileToProcess.getName());

			if (!fileToProcess.delete()) {
				//TODO we should probably delete processed file. For the scope of this assignment it seems enough :)
				logger().error("Unable to delete file {}. ");
				throw new RuntimeException("unable to delete file " + fileToProcess.getName());
			}

			logger().info("Processing file successful");
		}

	}

	public ReportEntity processWebFile(DepartmentDto departmentDto) {
		String nameOfFile = "departmentDTO" + System.currentTimeMillis();

		return processFile(departmentDto, SourceEnum.WEB, nameOfFile);
	}

	private ReportEntity processFile(DepartmentDto departmentDto, SourceEnum source, String name) {

		//1. process/convert dto
		List<EmployeeDto> employees = departmentDto.getEmployees();
		if (employees != null) {
			for (EmployeeDto employee : employees) {
				logger().debug("Processing employee pk {}, apk {}", employee.getId1(), employee.getId2());

				employee.setId2(employee.getId2().toUpperCase());

				if (employee.getManagers() != null) {
					employee.getManagers().forEach(managerDto -> managerDto.setFk2(managerDto.getFk2().toUpperCase()));
				}

				if (employee.getTeamMembers() != null) {
					employee.getTeamMembers().forEach(teamMemberDto -> teamMemberDto.setFk2(teamMemberDto.getFk2().toUpperCase()));
				}
			}
		} else {
			logger().warn("This department has no employees. Strange behaviour");
		}

		//2. serialize and store into folder
		logger().debug("Storing changed dto into output folder");

		try {

			File resultFile = fileComponent.newFile(environment.getProperty("process.output.folder") + "/" + name + ".xml");
			xmlMapper.writeValue(resultFile, departmentDto);

		} catch (IOException e) {
			//TODO again .. i cant say things things work as they should.
			logger().error("We are unable to create new file. Reverting all progress");
			throw new RuntimeException("processing file " + name + " failed", e);
		}
		logger().debug("Saving file {} successful", name);

		//3. create and return report
		return createReport(source, name);
	}

	private ReportEntity createReport(SourceEnum source, String name) {
		logger().debug("Creating report for file {}", name);
		ReportEntity reportEntity = new ReportEntity();
		reportEntity.setProcessTime(new Date());
		reportEntity.setSource(source);
		reportEntity.setSuccess(true);
		reportEntity.setName(name);

		reportRepository.save(reportEntity);
		logger().debug("Report {} creation successful", name);
		return reportEntity;
	}
}
