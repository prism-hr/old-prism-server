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
import com.zuehlke.pgadmissions.scoring.jaxb.QuestionType;

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

	@Test
	public void shouldInvalidateMalformattedMinDate() {
		CustomQuestions customQuestions = new CustomQuestions();
		List<Question> questions = customQuestions.getQuestion();
		Question question = new Question();
		String minDateForTest = "2013-004-22";
		question.setType(QuestionType.DATE);
		question.setMinDate(minDateForTest);
		questions.add(question);

		try {
			parser.validateScoringDefinition(customQuestions);
			Assert.fail();
		} catch (ScoringDefinitionParseException e) {
			Assert.assertEquals("Mulformated date " + minDateForTest
					+ ". Expected format is yyyy-mm-dd, or \"TODAY\"",
					e.getLocalizedMessage());
		}
	}

	@Test
	public void shouldValidateMaxDateWithCorrectFormat()
			throws ScoringDefinitionParseException {
		CustomQuestions customQuestions = new CustomQuestions();
		List<Question> questions = customQuestions.getQuestion();
		Question question = new Question();
		question.setType(QuestionType.DATE);
		String maxDateForTest = "2013-04-22";
		question.setMaxDate(maxDateForTest);
		questions.add(question);

		parser.validateScoringDefinition(customQuestions);
	}

	@Test
	public void shouldValidateMaxDateWithTheValueOfTODAY()
			throws ScoringDefinitionParseException {
		CustomQuestions customQuestions = new CustomQuestions();
		List<Question> questions = customQuestions.getQuestion();
		Question question = new Question();
		question.setType(QuestionType.DATE);
		String maxDateForTest = "TODAY";
		question.setMaxDate(maxDateForTest);
		questions.add(question);

		parser.validateScoringDefinition(customQuestions);
	}

	@Test
	public void shouldRequireBothMinDateAndMaxDateForDateRangeField() {
		CustomQuestions customQuestions = new CustomQuestions();
		List<Question> questions = customQuestions.getQuestion();
		Question question = new Question();
		String label = "testQuestion";
		question.setLabel(label);
		question.setType(QuestionType.DATE_RANGE);
		question.setMinDate("TODAY");
		questions.add(question);

		try {
			parser.validateScoringDefinition(customQuestions);
			Assert.fail();
		} catch (ScoringDefinitionParseException e) {
			Assert.assertEquals(
					"Both minDate and maxDate elements are required for dateRange type "
							+ label, e.getLocalizedMessage());
		}
	}

	@Test
	public void shouldRequireOptionsForDropdown() {
		CustomQuestions customQuestions = new CustomQuestions();
		List<Question> questions = customQuestions.getQuestion();
		Question question = new Question();
		String label = "testDropDown";
		question.setLabel(label);
		question.setType(QuestionType.DROPDOWN);
		questions.add(question);

		try {
			parser.validateScoringDefinition(customQuestions);
			Assert.fail();
		} catch (ScoringDefinitionParseException e) {
			Assert.assertEquals(
					"Options element is required for dropdown type " + label,
					e.getLocalizedMessage());
		}
	}

	@Before
	public void setUp() throws Exception {
		parser = new ScoringDefinitionParser();
	}

}
