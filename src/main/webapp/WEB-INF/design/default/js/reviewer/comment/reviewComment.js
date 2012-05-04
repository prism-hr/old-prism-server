$(document).ready(function(){

	$("#declineValue").val("NO");
	
	$.get("/pgadmissions/comments/view",
			{
				id:  $('#applicationId').val(),				
				cacheBreaker: new Date().getTime() 
			},
			function(data) {
				$('#timeline').html(data);
			}
	);
	
	$('#cancelReviewBtn').click(function() {
		window.location.href = "/pgadmissions/reviewFeedback?applicationId=" +  $('#applicationId').val();
	});	
	
	$('#submitReviewFeedback').click(function() {
			var application = $('#applicationId').val();
			var willingSupervise = null;
			if ($('#willingRB_true:checked').val() !== undefined) {
				willingSupervise = 'YES';
			}
			if ($('#willingRB_false:checked').val() !== undefined) {
				willingSupervise = 'NO';
			}
			var isSuitable = null;
			if ($('#suitableRB_true:checked').val() !== undefined) {
				isSuitable = 'YES';
			}
			if ($('#suitableRB_false:checked').val() !== undefined) {
				isSuitable = 'NO';
			}
			var postParams = {
				applicationId: application,
				type: 'REVIEW',
				comment: $('#review-comment').val(),
				decline: $("#declineValue").val()				
			};
			if(isSuitable){
				postParams.suitableCandidate= isSuitable;
			}
			if(willingSupervise){
				postParams.willingToSupervice= willingSupervise;
			}
			$.post(
					"/pgadmissions/reviewFeedback",
					postParams,
					function(data) {
						$('#reviewcommentsectopm').html(data);
					}
				);
	});	
	
	$("input[name*='declineCB']").click(function() {
		if ($("#declineValue").val() =='YES'){
			$("#declineValue").val("NO");
		} else {	
			$("#declineValue").val("YES");
		}
		});
	
	$("input[name$='declineCB']").click(function() {
		if($('#declineValue').val() == 'YES') {
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
			
			//supervise radio
			
			var newLblText1 = $("#supervise-lbl").text();
			var starIndex1 = newLblText1.lastIndexOf("*");
			if( starIndex1 > 0) {
			  newLblText1 = newLblText1.substring(0, starIndex1);
			}
			$("#supervise-lbl").text(newLblText1).addClass("grey-label");
			$('input[name="willingRB"]')[0].checked = false;
			$('input[name="willingRB"]')[1].checked = false;

			$("#willingRB_true").addClass("grey-label");
			$("#willingRB_true").attr("disabled", "disabled");
			$("#willingRB_false").addClass("grey-label");
			$("#willingRB_false").attr("disabled", "disabled");
			
			//suitable radio
			
			var newLblText2 = $("#suitable-lbl").text();
			var starIndex2 = newLblText2.lastIndexOf("*");
			if( starIndex2 > 0) {
			  newLblText2 = newLblText2.substring(0, starIndex2);
			}
			$("#suitable-lbl").text(newLblText2).addClass("grey-label");
			$('input[name="suitableRB"]')[0].checked = false;
			$('input[name="suitableRB"]')[1].checked = false;
			$("#suitableRB_true").addClass("grey-label");
			$("#suitableRB_true").attr("disabled", "disabled");
			$("#suitableRB_false").addClass("grey-label");
			$("#suitableRB_false").attr("disabled", "disabled");
			
			//remove validation messages
			$('span[class="invalid"]').html('');
			
		} else {
			//comment field
			$("#comment-lbl").append('<em>*</em>').removeClass("grey-label");
			$("#review-comment").removeClass("grey-label");
			$("#review-comment").removeAttr("disabled", "disabled");
			
			//supervise radio
			
			$("#supervise-lbl").append('<em>*</em>').removeClass("grey-label");
			$("#willingRB_true").removeClass("grey-label");
			$("#willingRB_true").removeAttr("disabled", "disabled");
			$("#willingRB_false").removeClass("grey-label");
			$("#willingRB_false").removeAttr("disabled", "disabled");
			
			//suitable radio
			
			$("#suitable-lbl").append('<em>*</em>').removeClass("grey-label");
			$("#suitableRB_true").removeClass("grey-label");
			$("#suitableRB_true").removeAttr("disabled", "disabled");
			$("#suitableRB_false").removeClass("grey-label");
			$("#suitableRB_false").removeAttr("disabled", "disabled");
		};
			
	});
	
	
});