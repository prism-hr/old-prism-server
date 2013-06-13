$(document).ready(function(){
		bindDatePicker($("#projectAdvertClosingDateInput"));
		registerAddProjectAdvertButton();
		registerEditProjectAdvertButton();
		registerRemoveProjectAdvertButton();
		registerHasClosingDateProjectAdvertRadio();
		registerClearButton();
		registerAutosuggest();
		clearAll();
		loadProjects();
		
});

function registerAutosuggest(){
	autosuggest($("#primarySupervisorFirstName"), $("#primarySupervisorLastName"), $("#primarySupervisorEmail"));
}

function registerAddProjectAdvertButton(){
	$("#addProjectAdvert").bind('click', function(){
		addOrEditProjectAdvert();
	});
}

function addOrEditProjectAdvert(){
	clearProjectAdvertErrors();
	var duration = {
			value : $("#projectAdvertStudyDurationInput").val(),
			unit : $("#projectAdvertStudyDurationUnitSelect").val()
		};
	var projectId=$('#projectId').val();
	var url="/pgadmissions/prospectus/projects";
	var method='POST';
	if(projectId){
		url+='/'+projectId;
	}
	showLoader();
	$.ajax({
		type: method,
		statusCode: {
			401: function() { window.location.reload(); },
			500: function() { window.location.href = "/pgadmissions/error"; },
			404: function() { window.location.href = "/pgadmissions/404"; },
			400: function() { window.location.href = "/pgadmissions/400"; },                  
			403: function() { window.location.href = "/pgadmissions/404"; }
		},
		url: url,
		data: {
			id : projectId,
			program : $("#projectAdvertProgramSelect").val(),
			title : $("#projectAdvertTitleInput").val(),
			description : $("#projectAdvertDescriptionText").val(),
			studyDuration : JSON.stringify(duration),
			funding : $("#projectAdvertFundingText").val(),
			closingDateSpecified : projectAdvertHasClosingDate(), 
			closingDate : $('#projectAdvertClosingDateInput').val(),
			active : $("input:radio[name=projectAdvertIsActiveRadio]:checked").val()
		}, 
		success: function(data) {
			var map = JSON.parse(data);
			if(!map['success']){
				displayErrors(map);
			} else {
				loadProjects();
				clearAll();
			}
		},
		complete: function() {
			hideLoader();
		}
	});
}

function displayErrors(map){
	appendErrorToElementIfPresent(map['program'],$("#projectAdvertProgramDiv"));
	appendErrorToElementIfPresent(map['title'],$("#projectAdvertTitleDiv"));
	appendErrorToElementIfPresent(map['description'],$("#projectAdvertDescriptionDiv"));
	appendErrorToElementIfPresent(map['studyDuration'],$("#projectAdvertStudyDurationDiv"));
	appendErrorToElementIfPresent(map['active'],$("#projectAdvertIsActiveDiv"));
	appendErrorToElementIfPresent(map['closingDateSpecified'],$("#projectAdvertHasClosingDateDiv"));
	appendErrorToElementIfPresent(map['closingDate'],$("#projectAdvertClosingDateDiv"));
	appendErrorToElementIfPresent(map['studyPlaces'],$("#projectAdvertStudyPlacesDiv"));
}

function registerEditProjectAdvertButton(){
	$('#projectAdvertsTable').on('click', '.button-edit', function(){
		var $row = $(this).closest('tr');
		loadProject($row);
	});	
}

function registerRemoveProjectAdvertButton(){
	$('#projectAdvertsTable').on('click', '.button-delete', function(){
		var $row = $(this).closest('tr');
		removeProject($row);
	});
}

function registerClearButton(){
	$("#projectsClear").bind('click', function(){
		clearAll();
	});
}

function clearAll(){
	$("#projectAdvertProgramSelect").val("");
	$("#projectAdvertTitleInput").val("");
	$("#projectAdvertDescriptionText").val("");
	$("#projectAdvertStudyDurationInput").val("");
	$("#projectAdvertStudyDurationUnitSelect").val("");
	$("#projectAdvertFundingText").val("");
	$("#projectAdvertHasClosingDateRadioYes").prop("checked", false);
	$("#projectAdvertHasClosingDateRadioNo").prop("checked", true);
	$("#projectAdvertClosingDateInput").val("");
	$("#projectAdvertIsActiveRadioYes").prop("checked", false);
	$("#projectAdvertIsActiveRadioNo").prop("checked", false);
	$('#projectId').val("");
	$('#addProjectAdvert').text("Add");
	clearProjectAdvertErrors();
}

function registerHasClosingDateProjectAdvertRadio(){
	$("#projectAdvertHasClosingDateDiv [name='projectAdvertHasClosingDateRadio']").each(function () { 
		$(this).bind('change', function(){
			checkProjectClosingDate();
		});
	});
}

function checkProjectClosingDate(){
	if(projectAdvertHasClosingDate()){
		$('#projectAdvertClosingDateInput').removeAttr('disabled');
	}
	else{
		$('#projectAdvertClosingDateInput').val("");
		$('#projectAdvertClosingDateInput').attr('disabled','disabled');		
	}
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

function loadProject(advertRow) {
	projectId = advertRow.attr("project-id");
	showLoader();
	$.ajax({
		type: 'GET',
		statusCode: {
	        401: function() { window.location.reload(); },
	        500: function() { window.location.href = "/pgadmissions/error"; },
	        404: function() { window.location.href = "/pgadmissions/404"; },
	        400: function() { window.location.href = "/pgadmissions/400"; },                  
	        403: function() { window.location.href = "/pgadmissions/404"; }
		},
		url:"/pgadmissions/prospectus/projects/" + projectId,
		data: {
		}, 
		success: function(data) {
			var project = JSON.parse(data);
			fillProjectAdvertForm(project);
        },
        complete: function() {
        	hideLoader();
        }
    });
}

function fillProjectAdvertForm(project){
	clearProjectAdvertErrors();
	var advert = project.advert;
	
	$("#projectAdvertProgramSelect").val(project.program);
	
	$("#projectAdvertTitleInput").val(advert.title);
	$("#projectAdvertDescriptionText").val(advert.description);
	
	var durationOfStudyInMonths=advert.studyDuration;
	if(durationOfStudyInMonths%12==0) {
		$("#projectAdvertStudyDurationInput").val((durationOfStudyInMonths/12).toString());
		$("#projectAdvertStudyDurationUnitSelect").val('Years');
	} else {
		$("#projectAdvertStudyDurationInput").val(durationOfStudyInMonths.toString());
		$("#projectAdvertStudyDurationUnitSelect").val('Months');
	}
	
	$("#projectAdvertFundingText").val(advert.funding);
	
	if(project.closingDate) {
		$("#projectAdvertHasClosingDateRadioYes").prop("checked", true);
		$("#projectAdvertClosingDateInput").val(formatProjectClosingDate(new Date(project.closingDate)));
	} else {
		$("#projectAdvertHasClosingDateRadioNo").prop("checked", true);
		$("#projectAdvertClosingDateInput").val("");
	}
	
	if(advert.active) {
		$("#projectAdvertIsActiveRadioYes").prop("checked", true);
	} else {
		$("#projectAdvertIsActiveRadioNo").prop("checked", true);
	}
	
	$('#projectId').val(projectId);
	$('#addProjectAdvert').text("Edit");
}

function loadProjects(){
	var url="/pgadmissions/prospectus/projects";
	showLoader();
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
			hideLoader();
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

function projectAdvertHasClosingDate(){
	return $("input:radio[name='projectAdvertHasClosingDateRadio']:checked").val()=="true";
}

function formatProjectClosingDate(date) {
	return $.datepicker.formatDate('d M yy', date); 
}

function showLoader(){
	$('#ajaxloader').show();
}

function hideLoader(){
	$('#ajaxloader').fadeOut('fast');
}