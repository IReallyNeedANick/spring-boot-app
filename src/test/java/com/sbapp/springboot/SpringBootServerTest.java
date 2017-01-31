package com.sbapp.springboot;

import com.sbapp.dao.ReportRepository;
import com.sbapp.dto.DepartmentDto;
import com.sbapp.dto.EmployeeDto;
import com.sbapp.dto.ManagerDto;
import com.sbapp.dto.TeamMemberDto;
import com.sbapp.dto.enumeration.SourceEnum;
import com.sbapp.dto.hibernate.ReportEntity;
import com.sbapp.util.ApplicationXmlMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

/**
 * This is a testing class that starts application on tomcat on random accessible port and actually tests its services.
 */
@SuppressWarnings("SpringJavaAutowiredMembersInspection")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SpringBootServerTest {

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private ReportRepository reportRepository;

	@MockBean
	private ApplicationXmlMapper applicationXmlMapper;

	/**
	 * so i can see if hello world works as it should
	 */
	@Test
	public void helloWorld() throws Exception {
		String response = restTemplate.getForObject("/helloWorld", String.class);
		assertThat(response).isEqualTo("Hello World!");
	}

	/**
	 * so I see if passing value through @ResponseBody works
	 */
	@Test
	public void helloWorldWithArg() throws Exception {
		String responseEntity = restTemplate.postForObject("/helloWorldWithArgument", "xavier", String.class);
		assertThat(responseEntity).isEqualTo("Hello xavier!");
	}

	/**
	 * this is a basic rest controller test. Test actually calls server rest method and retrieves data from database and check if it is the same.
	 * writing to file is also mocked.
	 */
	@Test
	public void positiveParseTest() throws Exception {
		String postingObject = "<?xml version=\"1.0\"?>\n" +
				"<department>\n" +
				"  <employee>\n" +
				"    <id1>100</id1>\n" +
				"    <id2>id_of_Employee</id2>\n" +
				"    <manager>\n" +
				"      <fk1>111</fk1>\n" +
				"      <fk2>Mr. Manager</fk2>\n" +
				"    </manager>\n" +
				"    <team-member>\n" +
				"      <fk1>101</fk1>\n" +
				"      <fk2>Mr. team-member</fk2>\n" +
				"    </team-member>\n" +
				"  </employee>\n" +
				"</department>\n";

		//uppercased dto representation of rest body
		ManagerDto managerDto = new ManagerDto();
		managerDto.setFk1(111);
		managerDto.setFk2("Mr. Manager".toUpperCase());
		TeamMemberDto teamMemberDto = new TeamMemberDto();
		teamMemberDto.setFk1(101);
		teamMemberDto.setFk2("Mr. team-member".toUpperCase());

		EmployeeDto employeeDto = new EmployeeDto();
		employeeDto.setId1(100);
		employeeDto.setId2("id_of_Employee".toUpperCase());
		employeeDto.setManagers(Collections.singletonList(managerDto));
		employeeDto.setTeamMembers(Collections.singletonList(teamMemberDto));

		DepartmentDto departmentDto = new DepartmentDto();
		departmentDto.setEmployees(Collections.singletonList(employeeDto));


		// set headers
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_XML);
		HttpEntity<String> entity = new HttpEntity<>(postingObject, headers);

		//CALL /parse
		ResponseEntity<ReportEntity> reportEntityResponse = restTemplate.postForEntity("/parse", entity, ReportEntity.class);
		ReportEntity responseEntity = reportEntityResponse.getBody();

		//check if /parse has tried to write into disk
		ArgumentCaptor<DepartmentDto> argument = ArgumentCaptor.forClass(DepartmentDto.class);
		verify(applicationXmlMapper).writeValue(Matchers.anyObject(), argument.capture());
		DepartmentDto outputDepartment = argument.getValue();
		assertThat(outputDepartment).isEqualToComparingFieldByFieldRecursively(departmentDto);


		//check if /parse has stored report into database
		ReportEntity reportEntity = reportRepository.findOne(responseEntity.getName());
		assertThat(reportEntity).isNotNull();

		//check if stored data is same as reported back
		assertThat(responseEntity).isEqualToComparingFieldByField(reportEntity);

		//check fields of reportEntity
		assertThat(responseEntity.getProcessTime()).isCloseTo(new Date(), 500);
		assertThat(responseEntity.getSource()).isEqualTo(SourceEnum.WEB);
		assertThat(responseEntity.isSuccess()).isTrue();

	}

	/**
	 * check that validation annotations present on objects work as they should (@NotEmpty on employee.id2)
	 */
	@Test
	public void nonValidEmployeeObject() throws Exception {
		String postingObject = "<?xml version=\"1.0\"?>\n" +
				"<department>\n" +
				"  <employee>\n" +
				"    <id1>100</id1>\n" +
				"  </employee>\n" +
				"</department>\n";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_XML);
		HttpEntity<String> entity = new HttpEntity<>(postingObject, headers);

		//CALL /parse
		ResponseEntity<LinkedHashMap> reportEntity = restTemplate.postForEntity("/parse", entity, LinkedHashMap.class);
		assertThat(reportEntity.getStatusCodeValue()).isEqualTo(400);

	}
}