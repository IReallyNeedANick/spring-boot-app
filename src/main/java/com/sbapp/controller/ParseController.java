package com.sbapp.controller;

import com.sbapp.dto.DepartmentDto;
import com.sbapp.dto.hibernate.ReportEntity;
import com.sbapp.logic.ParseLogic;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;

@Api(value = "Basic Spring MVC parse controller")
@RestController
public class ParseController {

	private final ParseLogic parseLogic;

	@Autowired
	public ParseController(ParseLogic parseLogic) {
		this.parseLogic = parseLogic;
	}


	@ApiOperation(
			value = "Hello world",
			notes = "Basic, minimal hello world for very basic testing",
			response = String.class,
			httpMethod = "GET"
	)
	@RequestMapping(path = "/helloWorld", method = RequestMethod.GET)
	public String helloWorld() {
		return "Hello World!";
	}

	@ApiOperation(
			value = "Hello world Post method with input argument",
			notes = "Augmented hello world. For testing if POST request works with some additional complications",
			response = String.class,
			httpMethod = "POST"
	)
	@RequestMapping(path = "/helloWorldWithArgument", method = RequestMethod.POST)
	public ResponseEntity<String> helloWorldWithArgument(@ApiParam(value = "tell us who are we saying hello to?")
	                                                     @RequestBody String whoAreWeGreeting) {
		String greetingMessage = "Hello " + whoAreWeGreeting + "!";

		return new ResponseEntity<>(greetingMessage, HttpStatus.OK);
	}

	@ApiOperation(
			value = "method for processing department",
			notes = "This is a simple process rest method as specified in the document. " +
					"This is the method that was specified in the assignment.",
			response = ReportEntity.class,
			httpMethod = "POST",
			produces = "application/json"
	)
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = "Invalid departmentDto supplied. Please validate departmentDto"),
			@ApiResponse(code = 500, message = "Internal server error. Probably some IO exception")}
	)
	@RequestMapping(path = "/parse", consumes = "application/xml", method = RequestMethod.POST)
	public ResponseEntity<ReportEntity> parse(@ApiParam(value = "department object that contains all employees", required = true)
	                                          @Valid
	                                          @RequestBody DepartmentDto departmentDto/*, BindingResult bindingResults*/) throws IOException {

		ReportEntity reportEntity = parseLogic.processWebFile(departmentDto);

		return new ResponseEntity<>(reportEntity, HttpStatus.OK);
	}
}
