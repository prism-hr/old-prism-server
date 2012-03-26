package com.zuehlke.pgadmissions.controllers;

import java.io.IOException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.lowagie.text.DocumentException;


public class PrintControllerTest {

	private PrintController controller;
	
	@Ignore
	@Test
	public void printPage() throws IOException, DocumentException {
		controller.printPage(null);
	}
	
	@Before
	public void setUp(){
		controller = new PrintController();
	}
	
}
