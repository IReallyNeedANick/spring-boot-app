package com.sbapp.util;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;

/**
 * <p>
 * I can not set XmlMapper as a bean because some plugins are using ObjectMapper and others XmlMapper,
 * and then we have a problem, this is way this facade is used.
 *
 */
@Component
public class ApplicationXmlMapper {

	private XmlMapper xmlMapper = new XmlMapper();

	@PostConstruct
	public void postConstruct() {
		xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
	}

	public <T> T readValue(File src, Class<T> valueType) throws IOException {
		return xmlMapper.readValue(src, valueType);
	}

	public void writeValue(File resultFile, Object value) throws IOException {
		xmlMapper.writeValue(resultFile, value);
	}

}
