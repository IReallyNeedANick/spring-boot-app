package com.sbapp.util;

import org.springframework.stereotype.Component;

import java.io.File;

/**
 * this component lives exclusively so we can write unit tests easier.
 */
@Component
public class FileComponent {

	public File newFile(String name) {
		return new File(name);
	}
}
