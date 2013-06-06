$(document).ready(function(){
		bindDatePicker($("#projectAdvertClosingDateInput"));
		registerAddProjectAdvertButton();
		registerEditProjectAdvertButton();
		registerRemoveProjectAdvertButton();
		
		loadAdverts();
});

function registerAddProjectAdvertButton(){
	$("#addProjectAdvert").bind('click', function(){
		clearProjectAdvertErrors();
		
		var duration = {
				value : $("#projectAdvertStudyDurationInput").val(),
				unit : $("#projectAdvertStudyDurationUnitSelect").val()
			};
		var active = $("input:radio[name=projectAdvertIsActiveRadio]:checked").val();
		
		$('#ajaxloader').show();
		var url="/pgadmissions/prospectus/projects";
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
				} else {
					loadAdverts();
				}
			},
			complete: function() {
				$('#ajaxloader').fadeOut('fast');
			}
		});
	});
}

function registerEditProjectAdvertButton(){
	$('#projectAdvertsTable').on('click', '.button-edit', function(){
		var $row = $(this).closest('tr');
		loadProjectAdvert($row);
	});	
}

function registerRemoveProjectAdvertButton(){
	$('#projectAdvertsTable').on('click', '.button-delete', function(){
		var $row = $(this).closest('tr');
		removeProject($row);
	});
}

function removeProject(projectRow) {
	project = projectRow.attr("project-id");
	$.ajax({
        type: 'DELETE',
        statusCode: {
                401: function() { window.location.reload(); },
                500: function() { window.location.href = "/pgadmissions/error"; },
                404: function() { window.location.href = "/pgadmissions/404"; },
                400: function() { window.location.href = "/pgadmissions/400"; },                  
                403: function() { window.location.href = "/pgadmissions/404"; }
        },
        url:"/pgadmissions/prospectus/projects/" + project,
        data: {
        }, 
        success: function(data) {
        	projectRow.remove();
        },
        complete: function() {
        }
    });
}

function loadProjectAdvert(advertRow) {
	project = advertRow.attr("project-id");
	$.ajax({
		type: 'GET',
		statusCode: {
	        401: function() { window.location.reload(); },
	        500: function() { window.location.href = "/pgadmissions/error"; },
	        404: function() { window.location.href = "/pgadmissions/404"; },
	        400: function() { window.location.href = "/pgadmissions/400"; },                  
	        403: function() { window.location.href = "/pgadmissions/404"; }
		},
		url:"/pgadmissions/prospectus/projects/" + project,
		data: {
		}, 
		success: function(data) {
			clearProjectAdvertErrors();
			var project = JSON.parse(data);
			var advert = project.advert;
			$("#projectAdvertTitleInput").val(advert.title);
			$("#projectAdvertDescriptionText").val(advert.description);
			
			var durationOfStudyInMonths=advert.studyDuration;
			if(durationOfStudyInMonths%12==0){
				$("#projectAdvertStudyDurationInput").val((durationOfStudyInMonths/12).toString());
				$("#projectAdvertStudyDurationUnitSelect").val('Years');
			}else{
				$("#projectAdvertStudyDurationInput").val(durationOfStudyInMonths.toString());
				$("#projectAdvertStudyDurationUnitSelect").val('Months');
			}
			
			$("#projectAdvertFundingText").val(advert.funding);
			
			if(advert.active){
				$("#projectAdvertIsActiveRadioYes").prop("checked", true);
			} else {
				$("#projectAdvertIsActiveRadioNo").prop("checked", true);
			}
			
			$('#addProjectAdvert').text("Edit");
        },
        complete: function() {
        }
    });
}

function loadAdverts(){
	$('#ajaxloader').show();
	var url="/pgadmissions/prospectus/projects";
	$.ajax({
		type: 'GET',
		statusCode: {
			401: function() { window.location.reload(); },
			500: function() { window.location.href = "/pgadmissions/error"; },
			404: function() { window.location.href = "/pgadmissions/404"; },
			400: function() { window.location.href = "/pgadmissions/400"; },                  
			403: function() { window.location.href = "/pgadmissions/404"; }
		},
		url: url,
		data: {
		}, 
		success: function(data) {
			var projects = JSON.parse(data);
			displayProjectList(projects);
		},
		complete: function() {
			$('#ajaxloader').fadeOut('fast');
		}
	});
}

function clearProjectAdvertErrors(){
	$("#projectAdvertDiv .error").remove();
}

function displayProjectList(projects){
	$('#projectAdvertsTable tbody').empty();
	if(projects.length == 0){
		$("#projectAdvertsDiv").hide();
	} else {
		$("#projectAdvertsDiv").show();
		$.each(projects, function(index, project) {
			appendProjectRow(project);
		});				
	}
}

function appendProjectRow(project){
	$('#projectAdvertsTable tbody').append(
		'<tr project-id="' + project.id + '">' +
			'<td>' + 
				project.advert.title +
			'</td>' +
			'<td>' +
				'<button class="button-edit" type="button" data-desc="Edit">Edit</button>' +
			'</td>' +
			'<td>' +
				'<button class="button-delete" type="button" data-desc="Remove">Remove</button>' +
			'</td>' +
		'</tr>'	
	);
}