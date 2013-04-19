package com.zuehlke.pgadmissions.scoring;

import static junit.framework.Assert.assertEquals;

import java.io.File;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.zuehlke.pgadmissions.scoring.jaxb.CustomQuestions;
import com.zuehlke.pgadmissions.scoring.jaxb.Question;

public class ScoringDefinitionParserTest {

	private ScoringDefinitionParser parser;

	@Test
	public void shouldValidateSingleText() throws Exception {
		File f = new File("src/test/resources/scoring/simpleText.xml");
		String xmlContent = Files.toString(f, Charsets.UTF_8);

		CustomQuestions scoreDefinition = parser
				.parseScoringDefinition(xmlContent);

		List<Question> questions = scoreDefinition.getQuestion();
		assertEquals(1, questions.size());
	}

	@Test
	public void shouldInvalidateSingleTextWithoutLabel() throws Exception {
		File f = new File(
				"src/test/resources/scoring/simpleTextWithoutLabel.xml");
		String xmlContent = Files.toString(f, Charsets.UTF_8);

		try {
			parser.parseScoringDefinition(xmlContent);
			Assert.fail();
		} catch (ScoringDefinitionParseException e) {
			Assert.assertEquals(
					"cvc-complex-type.2.4.a: Invalid content was found starting with element 'required'. One of '{label}' is expected.",
					e.getLocalizedMessage());
		}
	}

	@Test
	public void shouldInvalidateSingleTextWithoutRequiredField()
			throws Exception {
		File f = new File(
				"src/test/resources/scoring/simpleTextWithoutRequiredField.xml");
		String xmlContent = Files.toString(f, Charsets.UTF_8);

		try {
			parser.parseScoringDefinition(xmlContent);
			Assert.fail();
		} catch (ScoringDefinitionParseException e) {
			Assert.assertEquals(
					"cvc-complex-type.2.4.b: The content of element 'question' is not complete. One of '{required}' is expected.",
					e.getLocalizedMessage());
		}
	}

	@Before
	public void setUp() throws Exception {
		parser = new ScoringDefinitionParser();
	}

}
