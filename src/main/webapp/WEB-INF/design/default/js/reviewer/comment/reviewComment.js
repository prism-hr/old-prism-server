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
			$("#suitableRB_true").addClass("grey-label");
			$("#suitableRB_true").attr("disabled", "disabled");
			$("#suitableRB_false").addClass("grey-label");
			$("#suitableRB_false").attr("disabled", "disabled");
			
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
		}
	});
	
	
});