package com.zuehlke.pgadmissions.controllers.factory;

import java.util.List;

import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.zuehlke.pgadmissions.domain.Score;
import com.zuehlke.pgadmissions.scoring.ScoringDefinitionParseException;
import com.zuehlke.pgadmissions.scoring.jaxb.Question;

@Component
public class ScoreFactory {
	public List<Score> createScores(List<Question> questions)
			throws ScoringDefinitionParseException {
		List<Score> scores = Lists.newArrayListWithExpectedSize(questions
				.size());

		for (Question question : questions) {
			Score score = new Score();
			score.setQuestion(question.getLabel());
			score.setQuestionType(question.getType());
			score.setOriginalQuestion(question);
			scores.add(score);
		}
		return scores;
	}
}
