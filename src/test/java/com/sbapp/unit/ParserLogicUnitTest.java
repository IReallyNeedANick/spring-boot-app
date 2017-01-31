package com.sbapp.unit;

import com.sbapp.dao.ReportRepository;
import com.sbapp.dto.TeamMemberDto;
import com.sbapp.dto.hibernate.ReportEntity;
import com.sbapp.logic.ParseLogic;
import com.sbapp.util.ApplicationXmlMapper;
import com.sbapp.util.FileComponent;
import com.sbapp.dto.DepartmentDto;
import com.sbapp.dto.EmployeeDto;
import com.sbapp.dto.ManagerDto;
import com.sbapp.dto.enumeration.SourceEnum;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.springframework.core.env.Environment;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class ParserLogicUnitTest {

	//departmentDTO pks
	private int pk1 = 100;
	private String apk1 = "hundred";
	private int pk2 = 101;
	private String apk2 = "hundred und eins";
	private int pk3 = -99;
	private String apk3 = "manager_from_different_dimension";


	@Test
	public void restTempTEst() throws Exception {
//		new RestTemplateBuilder("localhost:8080")

	}

	/**
	 * basic test of web call method.
	 */
	@Test
	public void positiveWebCall() throws Exception {
		// mock variables
		Environment mockEnvironment = mock(Environment.class);
		ReportRepository reportRepository = mock(ReportRepository.class);
		FileComponent fileComponent = mock(FileComponent.class);

		DepartmentDto departmentDto = createBasicDepartmentDto();

		ApplicationXmlMapper xmlMapper = mock(ApplicationXmlMapper.class);


		//call test
		ParseLogic parseLogic = new ParseLogic(mockEnvironment, reportRepository, xmlMapper, fileComponent);
		ReportEntity reportEntity = parseLogic.processWebFile(departmentDto);

		//assert department DTO
		ArgumentCaptor<DepartmentDto> argument = ArgumentCaptor.forClass(DepartmentDto.class);
		verify(xmlMapper).writeValue(Matchers.<File>anyObject(), argument.capture());
		DepartmentDto outputDepartment = argument.getValue();

		assertBasicDepartment(outputDepartment);

		//assert reportEntity
		ArgumentCaptor<ReportEntity> reportEntityToSave = ArgumentCaptor.forClass(ReportEntity.class);
		verify(reportRepository).save(reportEntityToSave.capture());
		ReportEntity reportEntityStoredToDB = reportEntityToSave.getValue();
		assertThat(reportEntityStoredToDB).isEqualToComparingFieldByField(reportEntity);

		assertThat(reportEntity.getProcessTime()).isCloseTo(new Date(), 500);
		assertThat(reportEntity.getSource()).isEqualTo(SourceEnum.WEB);
		assertThat(reportEntity.isSuccess()).isTrue();
	}


	/**
	 * this is a basic scheduling scenario test.
	 * scheduling method tries to find if there are any files to process and finds one file.
	 * Consequentially it tries to process this file. :)
	 */
	@Test
	public void positiveScheduledCall() throws Exception {

		//1. mock environment for getting properties (where to put file)
		Environment mockEnvironment = mock(Environment.class);
		String unitTestMockInputPath = "unit_test_mock_input_path";
		when(mockEnvironment.getProperty("process.input.folder")).thenReturn(unitTestMockInputPath);

		//2. mock files so when scheduler will try to find them he will first find one and then none.
		FileComponent fileComponent = mock(FileComponent.class);

		File inputFolder = mock(File.class);

		File fileToProcess = mock(File.class);
		when(fileToProcess.delete()).thenReturn(true);
		String nameOfFileToProcess = "nameOfFIle.xml";
		when(fileToProcess.getName()).thenReturn(nameOfFileToProcess);
		when(fileToProcess.getAbsolutePath()).thenReturn("opt/bla/absolute/NameOfFIle.xml");


		File[] listOfFiles = new File[1];
		listOfFiles[0] = fileToProcess;

		when(inputFolder.listFiles()).thenReturn(listOfFiles, new File[0]);

		when(fileComponent.newFile(anyString())).thenReturn(inputFolder, mock(File.class));

		//3. create basic department DTO
		DepartmentDto departmentDto = createBasicDepartmentDto();

		ApplicationXmlMapper xmlMapper = mock(ApplicationXmlMapper.class);
		when(xmlMapper.readValue(fileToProcess, DepartmentDto.class)).thenReturn(departmentDto);

		//4. mock repository so when we will try to save ReportEntity it will be saved through this repository
		ReportRepository reportRepository = mock(ReportRepository.class);


		//5. create logic and execute test
		ParseLogic parseLogic = new ParseLogic(mockEnvironment, reportRepository, xmlMapper, fileComponent);

		parseLogic.processFolderFiles();

		//6. check if department was properly changed (some values uppercased).
		ArgumentCaptor<DepartmentDto> argument = ArgumentCaptor.forClass(DepartmentDto.class);
		verify(xmlMapper).writeValue(Matchers.<File>anyObject(), argument.capture());
		DepartmentDto outputDepartment = argument.getValue();

		assertThat(departmentDto).isEqualToComparingFieldByField(outputDepartment);

		assertBasicDepartment(outputDepartment);

		//7. check if report was properly saved into database
		ArgumentCaptor<ReportEntity> reportEntityToSave = ArgumentCaptor.forClass(ReportEntity.class);
		verify(reportRepository).save(reportEntityToSave.capture());
		ReportEntity reportEntity = reportEntityToSave.getValue();

		assertThat(reportEntity.getProcessTime()).isCloseTo(new Date(), 500);
		assertThat(reportEntity.getSource()).isEqualTo(SourceEnum.FOLDER);
		assertThat(reportEntity.isSuccess()).isTrue();
		assertThat(reportEntity.getName()).endsWith(nameOfFileToProcess);
	}

	@Test
	@Ignore("TODO")
	public void negativeScheduledCall() throws Exception {

	}

	/**
	 * basic department object looks like this =
	 * - employee1 (pk1, apk1)
	 * -- manager = null
	 * -- team member = (employee2)
	 * <p>
	 * - employee2 (pk2, apk2)
	 * -- manager = (employee1, employee3(pk3, apk3))
	 * -- team-member =null
	 */
	private DepartmentDto createBasicDepartmentDto() {
		//3. create department so it will be returned from mapper
		DepartmentDto departmentDto = new DepartmentDto();


		EmployeeDto employeeDto1 = new EmployeeDto();
		employeeDto1.setId1(pk1);
		employeeDto1.setId2(apk1);

		TeamMemberDto teamMemberDto = new TeamMemberDto();
		teamMemberDto.setFk1(pk2);
		teamMemberDto.setFk2(apk2);

		employeeDto1.setTeamMembers(Collections.singletonList(teamMemberDto));

		EmployeeDto employeeDto2 = new EmployeeDto();
		employeeDto2.setId1(pk2);
		employeeDto2.setId2(apk2);

		ManagerDto managerDto = new ManagerDto();
		managerDto.setFk1(pk1);
		managerDto.setFk2(apk1);

		ManagerDto managerDto1 = new ManagerDto();
		managerDto1.setFk1(pk3);
		managerDto1.setFk2(apk3);

		employeeDto2.setManagers(Arrays.asList(managerDto, managerDto1));

		departmentDto.setEmployees(Arrays.asList(employeeDto1, employeeDto2));

		return departmentDto;
	}


	private void assertBasicDepartment(DepartmentDto departmentDto) {
		assertThat(departmentDto.getEmployees().size()).isEqualTo(2);

		EmployeeDto outputEmployee1 = departmentDto.getEmployees().get(0);
		assertThat(outputEmployee1.getId1()).isEqualTo(pk1);
		assertThat(outputEmployee1.getId2()).isEqualTo(apk1.toUpperCase());
		assertThat(outputEmployee1.getManagers()).isNull();
		List<TeamMemberDto> outputTeamMembersOfEmployee1 = outputEmployee1.getTeamMembers();
		assertThat(outputTeamMembersOfEmployee1.size()).isEqualTo(1);

		TeamMemberDto outputTeamMemberOfEmployee1 = outputTeamMembersOfEmployee1.get(0);
		assertThat(outputTeamMemberOfEmployee1.getFk1()).isEqualTo(pk2);
		assertThat(outputTeamMemberOfEmployee1.getFk2()).isEqualTo(apk2.toUpperCase());


		EmployeeDto outputEmployee2 = departmentDto.getEmployees().get(1);
		assertThat(outputEmployee2.getId1()).isEqualTo(pk2);
		assertThat(outputEmployee2.getId2()).isEqualTo(apk2.toUpperCase());

		assertThat(outputEmployee2.getTeamMembers()).isNull();
		List<ManagerDto> outputEmployee2Managers = outputEmployee2.getManagers();
		assertThat(outputEmployee2Managers.size()).isEqualTo(2);
		ManagerDto outputEmployee2manager1 = outputEmployee2Managers.get(0);

		assertThat(outputEmployee2manager1.getFk1()).isEqualTo(pk1);
		assertThat(outputEmployee2manager1.getFk2()).isEqualTo(apk1.toUpperCase());

		ManagerDto outputEmployee2manager2 = outputEmployee2Managers.get(1);
		assertThat(outputEmployee2manager2.getFk1()).isEqualTo(pk3);
		assertThat(outputEmployee2manager2.getFk2()).isEqualTo(apk3.toUpperCase());
	}
}
