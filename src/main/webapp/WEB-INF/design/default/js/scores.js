
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
		} 
		/*else if(questionType == "DATE_RANGE"){
			dateResponse = $(this).find("input.date-input").val();
			if(dateResponse != ""){
				score.dateResponse = dateResponse;
			}
			secondDateResponse = $(this).find("input.second-date-input").val();
			if(secondDateResponse != ""){
				score.secondDateResponse = secondDateResponse;
			}
		}*/
		 else if(questionType == "DROPDOWN"){
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
/* Start rating system*/
$(document).ready(function() {
	 $items = $('.rating-list li i');
	 $($items).click(function() {
		 rating('select', $(this) );
    });
	$($items).hover(function() {
         rating('hover', $(this));
    });
	$items.mouseout(function(){
	    rating('out', $(this));
	});
	rating('check', $(this));
});

function rating(type, item){
	if (type == 'select') {
	    value = item.parent().index();
		// value for input
		item.closest('.field').find('.rating-input').val(value);
		// clear selected starts
		
		item.closest('.rating-list').find('li i.icon-star').removeClass('icon-star, hover').addClass('icon-star-empty');
		
		// select starts
		for (var i = 1; i < value+1; i++) {
		    var it = 'li:nth-child('+ (i+1) +')';
			item.closest('.rating-list').find(it).find('i').removeClass('icon-star-empty').addClass('icon-star hover'); 
		}
		if (value == 0) {
		  item.addClass('hover'); 
		} else {
		  item.closest('.rating-list').find('li i.icon-thumbs-down').removeClass('hover');
		}
	}
	if (type == 'hover') {
	    value = item.parent().index();
		item.closest('.rating-list').find('li i.icon-star-empty').removeClass('hover');
		// hover previous starts
		for (var i = 1; i < value+1; i++) {
		    var it = 'li:nth-child('+ (i+1) +')';
			item.closest('.rating-list').find(it).find('i').addClass('hover'); 
		}
	}
	if (type == 'out') {
	    value = item.parent().index();
	    item.closest('.rating-list').find('li i.icon-star-empty').removeClass('hover');
	}
	if (type == 'check'){
	    
	    $('.rating-list').each(function() {
	        cvalue = $(this).parent().find('.rating-input').val();
			if (cvalue != ''){
				if (cvalue == 0) {
					$(this).find('li:first-child').find('i').addClass('hover'); 
				} else {
					numb = parseInt(cvalue)+1;
					for (var i = 1; i < numb; i++) {
						 var it = 'li:nth-child('+ (i+1) +')';
						 $(this).find(it).find('i').removeClass('icon-star-empty').addClass('icon-star hover'); 
					}
				}
			}
        });
	}
}