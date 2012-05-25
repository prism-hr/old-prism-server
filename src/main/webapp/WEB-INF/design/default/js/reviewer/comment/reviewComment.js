$(document).ready(function(){

	
	$('#cancelReviewBtn').click(function() {
		window.location.href = "/pgadmissions/reviewFeedback?applicationId=" +  $('#applicationId').val();
	});	
	
	$('#submitReviewFeedback').click(function() {		
			$('#reviewForm').append("<input type='hidden' name='applicationId' value='" + $('#applicationId').val() + "'/>");
			$('#reviewForm').append("<input type='hidden' name='type' value='REVIEW'/>");
			$('#reviewForm').append("<input type='hidden' name='comment' value='"+ $('#review-comment').val() + "'/>");

			
			if ($('#decline:checked').length > 0) {
				$('#reviewForm').append("<input type='hidden' name='decline' value='true'/>");
				
			}
			if ($('input:radio[name=willingToInterview]:checked').length > 0) {
				
				$('#reviewForm').append("<input type='hidden' name='willingToInterview' value='"+ $('input:radio[name=willingToInterview]:checked').val() +"'/>");
			}
			if ($('input:radio[name=suitableCandidate]:checked').length > 0) {				
				$('#reviewForm').append("<input type='hidden' name='suitableCandidate' value='"+ $('input:radio[name=suitableCandidate]:checked').val() +"'/>");
			}
			
			$('#reviewForm').submit();
	});	
	
	$('#decline').click(function(){
		if($('#decline:checked').length > 0) {
			//comment field
			var newLblText = $("#comment-lbl").text();
			var starIndex = newLblText.lastIndexOf("*");
			if( starIndex > 0) {
			  newLblText = newLblText.substring(0, starIndex);
			}
			$("#comment-lbl").text(newLblText).addClass("grey-label");
			$("#review-comment").val("");
			$("#review-comment").addClass("grey-label");
			$("#review-comment").attr("disabled", "disabled");
			
			//itnerview radio
			
			var newLblText1 = $("#supervise-lbl").text();
			var starIndex1 = newLblText1.lastIndexOf("*");
			if( starIndex1 > 0) {
			  newLblText1 = newLblText1.substring(0, starIndex1);
			}
			$("#supervise-lbl").text(newLblText1).addClass("grey-label");
			$('input[name="willingToInterview"]').removeAttr("checked");

			$('input[name="willingToInterview"]').addClass("grey-label");
			$('input[name="willingToInterview"]').attr("disabled", "disabled");
		
			//suitable radio
			
			var newLblText2 = $("#suitable-lbl").text();
			var starIndex2 = newLblText2.lastIndexOf("*");
			if( starIndex2 > 0) {
			  newLblText2 = newLblText2.substring(0, starIndex2);
			}
			$("#suitable-lbl").text(newLblText2).addClass("grey-label");
			$('input[name="suitableCandidate"]').removeAttr("checked");

			$('input[name="suitableCandidate"]').addClass("grey-label");
			$('input[name="suitableCandidate"]').attr("disabled", "disabled");
		
			
			//remove validation messages
			$('span[class="invalid"]').html('');
			
		}else{
			//comment field
			$("#comment-lbl").append('<em>*</em>').removeClass("grey-label");
			$("#review-comment").removeClass("grey-label");
			$("#review-comment").removeAttr("disabled", "disabled");
			
			//supervise radio
			
			$("#supervise-lbl").append('<em>*</em>').removeClass("grey-label");
			('input[name="willingToInterview"]').removeClass("grey-label");
			('input[name="willingToInterview"]').removeAttr("disabled", "disabled");
	
			//suitable radio
			
			$("#suitable-lbl").append('<em>*</em>').removeClass("grey-label");
			$('input[name="suitableCandidate"]').removeClass("grey-label");
			$('input[name="suitableCandidate"]').removeAttr("disabled", "disabled");
	
		}
	});
	
	

	
	
});