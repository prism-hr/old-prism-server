$(document).ready(function(){
		bindDatePicker($("#projectAdvertClosingDateInput"));
		bindAddProjectAdvertButtonAction();
});

function bindAddProjectAdvertButtonAction(){
	$("#addProjectAdvert").bind('click', function(){
		clearProjectAdvertErrors();
		
		var duration = {
				value : $("#projectAdvertStudyDurationInput").val(),
				unit : $("#projectAdvertStudyDurationUnitSelect").val()
			};
		var active = $("input:radio[name=projectAdvertIsActiveRadio]:checked").val();
		
		$('#ajaxloader').show();
		var url="/pgadmissions/prospectus/project/add";
		$.ajax({
			type: 'POST',
			statusCode: {
				401: function() { window.location.reload(); },
				500: function() { window.location.href = "/pgadmissions/error"; },
				404: function() { window.location.href = "/pgadmissions/404"; },
				400: function() { window.location.href = "/pgadmissions/400"; },                  
				403: function() { window.location.href = "/pgadmissions/404"; }
			},
			url: url,
			data: {
				program: $("#projectAdvertProgramSelect").val(),
				id: $('#projectAdvertClosingDateId').val(),
				title:$("#projectAdvertTitleInput").val(),
				description:$("#projectAdvertDescriptionText").val(),
				studyDuration:JSON.stringify(duration),
				funding:$("#projectAdvertFundingText").val(),
				active:active,
				closingDate : $('#projectAdvertClosingDateInput').val(),
				studyPlaces : $('#projectAdvertStudyPlacesInput').val()
				
			}, 
			success: function(data) {
				var map = JSON.parse(data);
				if(!map['success']){
					if(map['program']){
						$("#projectAdvertProgramDiv").append(getErrorMessageHTML(map['program']));
					}
					if(map['title']){
						$("#projectAdvertTitleDiv").append(getErrorMessageHTML(map['title']));
					}
					if(map['description']){
						$("#projectAdvertDescriptionDiv").append(getErrorMessageHTML(map['description']));
					}
					if(map['studyDuration']){
						$("#projectAdvertStudyDurationDiv").append(getErrorMessageHTML(map['studyDuration']));
					}
					if(map['active']){
						$("#projectAdvertIsActiveDiv").append(getErrorMessageHTML(map['active']));
					}
					if(map['closingDate']){
						$("#projectAdvertClosingDateDiv").append(getErrorMessageHTML(map['closingDate']));
					}
					if(map['studyPlaces']){
						$("#projectAdvertStudyPlacesDiv").append(getErrorMessageHTML(map['studyPlaces']));
					}
				}
			},
			complete: function() {
				$('#ajaxloader').fadeOut('fast');
			}
		});
	});
}

function clearProjectAdvertErrors(){
	$("#projectAdvertDiv .error").remove();
}