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
		}
			
		scores.push(score);
	});
	return JSON.stringify(scores);
}