function getScores(container) {
	
	var scores = new Array();
	container.find("div.score-row").each(function(){
		var questionType = $(this).find("input.question-type").val();
		var question = $(this).find("input.question").val();
		var required = $(this).find("input.question-required").val();
		
		var score = { 
				questionType : questionType,
				question : question,
				required : required
		};

		if(questionType == "TEXT"){
			textResponse = $(this).find("input.text-input").val();
			score.textResponse = textResponse;
		} else if(questionType == "TEXTAREA"){
			textResponse = $(this).find("textarea.textarea-input").val();
			score.textResponse = textResponse;
		} else if(questionType == "DATE"){
			dateResponse = $(this).find("input.date-input").val();
			if(dateResponse != ""){
				score.dateResponse = dateResponse;
			}
		} else if(questionType == "DATE_RANGE"){
			dateResponse = $(this).find("input.date-input").val();
			if(dateResponse != ""){
				score.dateResponse = dateResponse;
			}
			secondDateResponse = $(this).find("input.second-date-input").val();
			if(secondDateResponse != ""){
				score.secondDateResponse = secondDateResponse;
			}
		} else if(questionType == "DROPDOWN"){
			values = $(this).find("select.dropdown-input").val() || [];
			if(typeof values == 'string'){
				values = [ values ];
			}
			score.textResponse = values.join('|');
		} else if(questionType == "RATING"){
			value = $(this).find("input.rating-input").val();
			if(value!=""){
				score.ratingResponse = value;
			}
		} 
			
		scores.push(score);
	});
	return JSON.stringify(scores);
}

function registerBindPickers(container){
	container.find("input.date").each(function(){
		bindDatePicker($(this));
	});
}