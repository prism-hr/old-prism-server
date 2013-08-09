package com.zuehlke.pgadmissions.scoring;

import java.io.File;
import java.io.StringReader;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.scoring.jaxb.CustomQuestions;
import com.zuehlke.pgadmissions.scoring.jaxb.Question;
import com.zuehlke.pgadmissions.scoring.jaxb.QuestionType;

@Component
public class ScoringDefinitionParser {

	private static final String OPTIONS_ELEMENT_MISSING_MESSAGE_TEMPLATE = "Options element is required for dropdown type %s";

//	private static final String DATE_RANGE_ERROR_MESSAGE_TEMPLATE = "Both minDate and maxDate elements are required for dateRange type %s";

	public static final String TODAY = "TODAY";

	public static final String DATE_FORMAT = "\\d{4}-[0-1]\\d-[1-3]\\d";

	private Unmarshaller unmarshaller;

	private final static String MALFORMATTED_DATE_ERROR_MESSAGE_TEMPLATE = "Malformated date %s. Expected format is yyyy-mm-dd, or \"TODAY\"";

	public ScoringDefinitionParser() throws Exception {
		File file = new DefaultResourceLoader().getResource(
				"classpath:scoringConfig.xsd").getFile();

		SchemaFactory schemaFactory = SchemaFactory
				.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = schemaFactory.newSchema(file);

		JAXBContext jc = JAXBContext.newInstance(CustomQuestions.class);
		unmarshaller = jc.createUnmarshaller();
		unmarshaller.setSchema(schema);
	}

	public CustomQuestions parseScoringDefinition(String definitionXml)
			throws ScoringDefinitionParseException {
		CustomQuestions scoringDefinition = null;
		try {
			scoringDefinition = (CustomQuestions) unmarshaller
					.unmarshal(new StringReader(definitionXml));
		} catch (JAXBException e) {
			throw new ScoringDefinitionParseException(e.getLinkedException()
					.getLocalizedMessage(), e);
		}
		validateScoringDefinition(scoringDefinition);
		return scoringDefinition;
	}

	public void validateScoringDefinition(CustomQuestions scoringDefinition)
			throws ScoringDefinitionParseException {
		List<Question> questions = scoringDefinition.getQuestion();
		for (Question question : questions) {
			validateDateType(question);
//			validateDateRangeType(question);
			validateDropDownType(question);
		}
	}

	private void validateDropDownType(Question question)
			throws ScoringDefinitionParseException {
		if (question.getType().equals(QuestionType.DROPDOWN)) {
			if (question.getOptions() == null) {
				throw new ScoringDefinitionParseException(String.format(
						OPTIONS_ELEMENT_MISSING_MESSAGE_TEMPLATE,
						question.getLabel()));
			}
		}
	}

	private void validateDateType(Question question)
			throws ScoringDefinitionParseException {
		if (question.getType().equals(QuestionType.DATE)) {
			String maxDate = question.getMaxDate();
			String minDate = question.getMinDate();
			if (maxDate != null) {
				validateDate(maxDate);
			}
			if (minDate != null) {
				validateDate(minDate);
			}
		}

	}

//	private void validateDateRangeType(Question question)
//			throws ScoringDefinitionParseException {
//		if (question.getType().equals(QuestionType.DATE_RANGE)) {
//			String minDate = question.getMinDate();
//			String maxDate = question.getMaxDate();
//			if (minDate == null || maxDate == null) {
//				throw new ScoringDefinitionParseException(String.format(
//						DATE_RANGE_ERROR_MESSAGE_TEMPLATE, question.getLabel()));
//			}
//			validateDate(minDate);
//			validateDate(maxDate);
//		}
//
//	}

	private void validateDate(String date)
			throws ScoringDefinitionParseException {
		if (date != null && !date.matches(DATE_FORMAT) && !(date.equals(TODAY))) {
			throw new ScoringDefinitionParseException(String.format(
					MALFORMATTED_DATE_ERROR_MESSAGE_TEMPLATE, date));
		}
	}

}
