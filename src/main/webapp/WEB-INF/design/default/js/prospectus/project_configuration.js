$(document).ready(function(){
		bindDatePicker($("#projectAdvertClosingDateInput"));
		registerProgramSelect();
		registerDefaultClosingDateSelector();
		registerAddProjectAdvertButton();
		registerEditProjectAdvertButton();
		registerShowProjectAdvertButton();
		registerRemoveProjectAdvertButton();
		registerHasClosingDateProjectAdvertRadio();
		registerHasProjectAdministratorRadio();
		registerHasSecondarySupervisorRadio();
		registerClearButton();
		registerAutosuggest();
		clearAll();
		loadProjects();
		$('#projectsClear').hide();
		initEditorsProjects();
});
function initEditorsProjects() {
	tinymce.init({
	    selector: "#projectAdvertDescriptionText",
	    plugins: ["link wordcount"],
	    width: 480,
	    menubar: false,
	    content: "",
	    toolbar: "bold italic  | bullist numlist outdent indent | link unlink | undo redo"
	    /*setup : function(ed) {
	    	ed.on('keyup', function(e) { $('textArea#programAdvertDescriptionText').val(tinymce.get('programAdvertDescriptionText').getContent())});
	    }*/
	});
	tinymce.init({
	    selector: "#projectAdvertFundingText",
	    plugins: ["link wordcount"],
	    width: 480,
	    menubar: false,
	    content: "",
	    toolbar: "bold italic  | bullist numlist outdent indent | link unlink | undo redo"
	});
}
function registerDefaultClosingDateSelector() {
	$("#projectAdvertProgramSelect").change(function () {
		selectDefaultClosingDate();
	});
}

function registerAutosuggest(){
	autosuggest($("#primarySupervisorFirstName"), $("#primarySupervisorLastName"), $("#primarySupervisorEmail"));
	autosuggest($("#secondarySupervisorFirstName"), $("#secondarySupervisorLastName"), $("#secondarySupervisorEmail"));
	autosuggest($("#projectAdministratorFirstName"), $("#projectAdministratorLastName"), $("#projectAdministratorEmail"));
}

function registerProgramSelect() {
	$("#projectAdvertProgramSelect").change(function() {
		loadProjects();
		clearAll();
	});
}

function registerAddProjectAdvertButton(){
	$("#addProjectAdvert").bind('click', function(){
		addOrEditProjectAdvert();
	});
}

function addOrEditProjectAdvert(){
	clearProjectAdvertErrors();
	var projectAdministrator = {
			firstname : $("#projectAdministratorFirstName").val(),
			lastname : $("#projectAdministratorLastName").val(),
			email : $("#projectAdministratorEmail").val()
	};
	var primarySupervisor = {
		firstname : $("#primarySupervisorFirstName").val(),
		lastname : $("#primarySupervisorLastName").val(),
		email : $("#primarySupervisorEmail").val()	
		};
	var secondarySupervisor = {
			firstname : $("#secondarySupervisorFirstName").val(),
			lastname : $("#secondarySupervisorLastName").val(),
			email : $("#secondarySupervisorEmail").val()	
	};
	var projectId=$('#projectId').val();
	var url="/pgadmissions/prospectus/projects";
	var method='POST';
	labeltext = 'saved';
	if(projectId){
		url+='/'+projectId;
		labeltext = 'updated';
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
			administratorSpecified : projectAdvertHasAdministrator(),
			administrator : JSON.stringify(projectAdministrator),
			title : $("#projectAdvertTitleInput").val(),
			description :addBlankLinks(tinymce.get('projectAdvertDescriptionText').getContent()),
			funding :addBlankLinks(tinymce.get('projectAdvertFundingText').getContent()),
			closingDateSpecified : projectAdvertHasClosingDate(), 
			closingDate : $('#projectAdvertClosingDateInput').val(),
			active : $("input:radio[name=projectAdvertIsActiveRadio]:checked").val(),
			primarySupervisor : JSON.stringify(primarySupervisor),
			secondarySupervisorSpecified : projectAdvertHasSecondarySupervisor(), 
			secondarySupervisor : JSON.stringify(secondarySupervisor)
		}, 
		success: function(data) {
			var map = JSON.parse(data);
			if(!map['success']){
				displayErrors(map);
			} else {
				loadProjects();
				clearAll();
			}
			changeInfoBarNameProject(labeltext,true);
		},
		complete: function() {
			hideLoader();
		}
	});
}

function displayErrors(map){
	appendErrorToElementIfPresent(map['program'],$("#projectAdvertProgramDiv"));
	appendErrorToElementIfPresent(map['administrator.firstname'],$("#projectAdministratorFirstNameDiv"));
	appendErrorToElementIfPresent(map['administrator.lastname'],$("#projectAdministratorLastNameDiv"));
	appendErrorToElementIfPresent(map['administrator.email'],$("#projectAdministratorEmailDiv"));
	appendErrorToElementIfPresent(map['administrator'],$("#projectAdministratorEmailDiv"));
	appendErrorToElementIfPresent(map['title'],$("#projectAdvertTitleDiv"));
	appendErrorToElementIfPresent(map['description'],$("#projectAdvertDescriptionDiv"));
	appendErrorToElementIfPresent(map['active'],$("#projectAdvertIsActiveDiv"));
	appendErrorToElementIfPresent(map['closingDateSpecified'],$("#projectAdvertHasClosingDateDiv"));
	appendErrorToElementIfPresent(map['closingDate'],$("#projectAdvertClosingDateDiv"));
	appendErrorToElementIfPresent(map['studyPlaces'],$("#projectAdvertStudyPlacesDiv"));
	appendErrorToElementIfPresent(map['primarySupervisor.firstname'],$("#primarySupervisorFirstNameDiv"));
	appendErrorToElementIfPresent(map['primarySupervisor.lastname'],$("#primarySupervisorLastNameDiv"));
	appendErrorToElementIfPresent(map['primarySupervisor.email'],$("#primarySupervisorEmailDiv"));
	appendErrorToElementIfPresent(map['primarySupervisor'],$("#primarySupervisorEmailDiv"));
	appendErrorToElementIfPresent(map['secondarySupervisor.firstname'],$("#secondarySupervisorFirstNameDiv"));
	appendErrorToElementIfPresent(map['secondarySupervisor.lastname'],$("#secondarySupervisorLastNameDiv"));
	appendErrorToElementIfPresent(map['secondarySupervisor.email'],$("#secondarySupervisorEmailDiv"));
	appendErrorToElementIfPresent(map['secondarySupervisor'],$("#secondarySupervisorEmailDiv"));
}

function registerEditProjectAdvertButton(){
	$('#projectAdvertsTable').on('click', '.button-edit', function(){
		var $row = $(this).closest('tr');
		loadProject($row);
	});	
}
function registerShowProjectAdvertButton(){
	$('#projectAdvertsTable').on('click', '.button-show', function(){
		var $row = $(this).closest('tr');
		loadProject($row);
		$('html, body').animate({ scrollTop: $("#resourcesProject").offset().top}, 300);
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
	$("#projectAdvertTitleInput").val("");
	$("#projectAdvertDescriptionText").val("");
	$("#projectAdvertFundingText").val("");
	$("#projectAdvertHasClosingDateRadioYes").prop("checked", false);
	$("#projectAdvertHasClosingDateRadioNo").prop("checked", true);
	$("#projectAdvertClosingDateInput").val("");
	$("#projectAdvertIsActiveRadioYes").prop("checked", false);
	$("#projectAdvertIsActiveRadioNo").prop("checked", false);
	$('#projectId').val("");
	$('#addProjectAdvert').text("Add Project");
	$('#projectAdvertLinkToApply').val('');
	$('#projectAdvertButtonToApply').val('');
	loadDefaultPrimarySupervisor();
	$("#projectAdvertHasAdministratorRadioYes").prop("checked", false);
	$("#projectAdvertHasAdministratorRadioNo").prop("checked", true);
	checkProjectAdministrator();
	$("#projectAdvertHasSecondarySupervisorRadioYes").prop("checked", false);
	$("#projectAdvertHasSecondarySupervisorRadioNo").prop("checked", true);
	checkSecondarySupervisor();
	clearProjectAdvertErrors();
	$('#projectsClear').hide();
	$('html, body').animate({ scrollTop: $('body').offset().top}, 300);
	initEditorsProjects();
	addCounter();
}

function registerHasClosingDateProjectAdvertRadio(){
	$("#projectAdvertHasClosingDateDiv [name='projectAdvertHasClosingDateRadio']").each(function () { 
		$(this).bind('change', function(){
			checkProjectClosingDate();
		});
	});
	$("#projectAdvertHasClosingDateDiv [name='projectAdvertHasClosingDateRadio']").change(function () { 
		selectDefaultClosingDate();
	});
}

function selectDefaultClosingDate() {
	if ($('#projectAdvertProgramSelect :selected').text()!='Select...' && $("#projectAdvertHasClosingDateDiv [name='projectAdvertHasClosingDateRadio']")[0].checked) {
		var closingDate = $('#closingDate').val();
		if (closingDate != 'null') {
			$('#projectAdvertClosingDateInput').val(closingDate);
		}
		else {
			$('#projectAdvertClosingDateInput').val('');
		}
	}
	else {
		$('#projectAdvertClosingDateInput').val('');
	}
}

function registerHasProjectAdministratorRadio(){
	$("#projectAdvertHasAdministratorDiv [name='projectAdvertHasAdministratorRadio']").each(function () { 
		$(this).bind('change', function(){
			checkProjectAdministrator();
		});
	});
}

function registerHasSecondarySupervisorRadio(){
	$("#projectAdvertHasSecondarySupervisorDiv [name='projectAdvertHasSecondarySupervisorRadio']").each(function () { 
		$(this).bind('change', function(){
			checkSecondarySupervisor();
		});
	});
}

function checkProjectClosingDate(){
	if(projectAdvertHasClosingDate()){
		$('#projectAdvertClosingDateInput').removeAttr('disabled');
		$('#projectAdvertClosingDateInput').parent().parent().find('label').removeClass("grey-label").parent().find('.hint').removeClass("grey");
	}
	else{
		$('#projectAdvertClosingDateInput').val("");
		$('#projectAdvertClosingDateInput').attr('disabled','disabled');	
		$('#projectAdvertClosingDateInput').parent().parent().find('label').addClass("grey-label").parent().find('.hint').addClass("grey");
	}
}

function checkProjectAdministrator(){
	var projectAdministratorFields = $('#projectAdministratorDiv input:text');
	if(projectAdvertRadioHasValue('projectAdvertHasAdministratorRadio')){
		projectAdministratorFields.each(function (){
			enableField($(this));
		});
	}
	else{
		projectAdministratorFields.each(function (){
			clearAndDisable($(this));
		});
	}
}

function checkSecondarySupervisor(){
	var secondarySupervisorFields = $('#secondarySupervisorDiv input:text');
	if(projectAdvertRadioHasValue('projectAdvertHasSecondarySupervisorRadio')){
		secondarySupervisorFields.each(function (){
			enableField($(this));
		});
	}
	else{
		secondarySupervisorFields.each(function (){
			clearAndDisable($(this));
		});
	}
}

function enableField(input){
	input.removeAttr('disabled').removeAttr('readonly');
	$(input).parent().parent().find('label').removeClass("grey-label").parent().find('.hint').removeClass("grey");
}
function clearAndDisable(input){
	input.val("");
	input.attr('disabled','disabled');
	$(input).parent().parent().find('label').addClass("grey-label").parent().find('.hint').addClass("grey");
}

function removeProject(projectRow) {
	project = projectRow.attr("project-id");
	if($('#projectId').val()==project){
		clearAll();
	}
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
        	if ($('#projectAdvertsTable tbody tbody tr').length == 0) {
				$('#projectAdvertsDiv').hide();
			}
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
			var map = JSON.parse(data);
			fillProjectAdvertForm(map);
			$('#projectsClear').show();
        },
        complete: function() {
        	hideLoader();
        	addCounter();
        }
    });
}

function fillProjectAdvertForm(data){
	project = data['project'];
	
	clearProjectAdvertErrors();
	var advert = project.advert;
	
	displayProjectAdministrator(project.administrator);
	
	$("#projectAdvertTitleInput").val(advert.title);
	$("#projectAdvertDescriptionText").val(advert.description);

	tinymce.get('projectAdvertDescriptionText').setContent(advert.description);

	
	$("#projectAdvertFundingText").val(advert.funding);

	tinymce.get('projectAdvertFundingText').setContent(advert.funding);
	
	if(project.closingDate) {
		$("#projectAdvertHasClosingDateRadioYes").prop("checked", true);
		$("#projectAdvertClosingDateInput").val(formatProjectClosingDate(new Date(project.closingDate)));
	} else {
		$("#projectAdvertHasClosingDateRadioNo").prop("checked", true);
		$("#projectAdvertClosingDateInput").val("");
	}
	checkProjectClosingDate();
	
	if(advert.active) {
		$("#projectAdvertIsActiveRadioYes").prop("checked", true);
	} else {
		$("#projectAdvertIsActiveRadioNo").prop("checked", true);
	}
	displayPrimarySupervisor(project.primarySupervisor);
	displaySecondarySupervisor(project.secondarySupervisor);
	$('#projectId').val(projectId);
	$('#addProjectAdvert').text("Edit Project");
	$('#projectAdvertLinkToApply').val(data['linkToApply']);
	$('#projectAdvertButtonToApply').val(data['buttonToApply']);
}

function checktoDisableProjet() {
	if ($("#projectAdvertProgramSelect").val() != "") {
		$(".projectGroup label").removeClass("grey-label").parent().find('.hint').removeClass("grey");
		$(".projectGroup input, .projectGroup textarea, .projectGroup select").removeAttr("readonly", "readonly");
		$(".projectGroup input, .projectGroup textarea, .projectGroup select, .projectGroup button").removeAttr("disabled", "disabled");
		$("#addProjectAdvert").removeClass("disabled");
		$("#projectAdvertClosingDateInput, #secondarySupervisorFirstName, #secondarySupervisorLastName, #secondarySupervisorEmail, #projectAdministratorFirstName, #projectAdministratorLastName, #projectAdministratorEmail").attr("disabled", "disabled").attr("readonly", "readonly");
		if ($("#primarySupervisorDiv").hasClass('isAdmin')) {
			$("#primarySupervisorFirstName, #primarySupervisorLastName, #primarySupervisorEmail").removeAttr("disabled").removeAttr("readonly");
			$("#primarySupervisorDiv label").removeClass("grey-label").parent().find('.hint').removeClass("grey");
		} else {
			$("#primarySupervisorFirstName, #primarySupervisorLastName, #primarySupervisorEmail").attr("disabled", "disabled").attr("readonly", "readonly");
			$("#primarySupervisorDiv label").addClass("grey-label").parent().find('.hint').addClass("grey");
		}
		/* Exceptions */
		
		$("#projectAdvertClosingDateDiv label").addClass("grey-label").parent().find('.hint').addClass("grey");
		$('#secondarySupervisorFirstNameLabel').addClass("grey-label").parent().find('.hint').addClass("grey");
		$('#secondarySupervisorLastNameLabel').addClass("grey-label").parent().find('.hint').addClass("grey");
		$('#secondarySupervisorEmailLabel').addClass("grey-label").parent().find('.hint').addClass("grey");
		$('#projectAdministratorFirstNameLabel').addClass("grey-label").parent().find('.hint').addClass("grey");
		$('#projectAdministratorLastNameLabel').addClass("grey-label").parent().find('.hint').addClass("grey");
		$('#projectAdministratorEmailLabel').addClass("grey-label").parent().find('.hint').addClass("grey");
	} else {
		$(".projectGroup label").addClass("grey-label").parent().find('.hint').addClass("grey");
		$(".projectGroup input, .projectGroup textarea, .projectGroup select, #projectAdvertHasSecondarySupervisorRadioYes, #projectAdvertHasSecondarySupervisorRadioNo").attr("readonly", "readonly");
		$(".projectGroup input, .projectGroup textarea, .projectGroup select, .projectGroup button, #projectAdvertHasSecondarySupervisorRadioYes, #projectAdvertHasSecondarySupervisorRadioNo").attr("disabled", "disabled");
		$("#addProjectAdvert").addClass("disabled");
		clearAll();
	}
}
function changeInfoBarNameProject(text,advertUpdated) {
	errors = $('.alert-error:visible').length;
	if (errors > 0) {
		$('.infoBar').removeClass('alert-info').addClass('alert-error').find('i').removeClass('icon-info-sign').addClass('icon-warning-sign');
		$('html, body').animate({ scrollTop: $("#projectConfiguration").offset().top}, 300);
	} else {
		$('.infoBar').removeClass('alert-error').addClass('alert-info').find('i').removeClass('icon-warning-sign').addClass('icon-info-sign');
		if (advertUpdated) {
			infohtml = "<i class='icon-ok-sign'></i> Your advert for your project has been "+text+".";
			if ($('#infoBarproject').hasClass('alert-info')) {
				$('#infoBarproject').addClass('alert-success').removeClass('alert-info').html(infohtml);
			} else {
				$('#infoBarproject').html(infohtml);
			}
		} else {
			if (text != "Select...") {
				infohtml = "<i class='icon-info-sign'></i> Manage the adverts and closing dates for your project here.";
			} else {
				infohtml =  "<i class='icon-info-sign'></i> Manage the adverts and closing dates for your project here.";
			}
			if ($('#infoBarproject').hasClass('alert-success')) {
				$('#infoBarproject').addClass('alert-info').removeClass('alert-success').html(infohtml);
			} else {
				$('#infoBarproject').html(infohtml);
			}
		}
	}
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
			 programCode : $("#projectAdvertProgramSelect").val()
		}, 
		success: function(data) {
			var projects = JSON.parse(data['projects']);
			$('#closingDate').val(data['closingDate']);
			var programme_name= $("#projectAdvertProgramSelect option:selected").text();
			displayProjectList(projects);
			checktoDisableProjet();
			clearProjectAdvertErrors();
			changeInfoBarNameProject(programme_name,false);
			$('#projectsClear').hide();
		},
		complete: function() {
			hideLoader();
			addCounter();
		}
	});
}

function clearProjectAdvertErrors(){
	$("#projectAdvertDiv .error").remove();
	$('#infoBarproject').removeClass('alert-error').addClass('alert-info').find('i').removeClass('icon-warning-sign').addClass('icon-info-sign');
}

function clearSecondarySupervisor(){
	$('#secondarySupervisorDiv input:text').each(function (){
		$(this).val("");
	});
}

function displayProjectList(projects){
	$('#projectAdvertsTable tbody tbody').empty();
	if(projects.length == 0){
		$("#projectAdvertsDiv").hide();
	} else {
		$("#projectAdvertsDiv").show();
		$.each(projects, function(index, project) {
			appendProjectRow(project);
		});	
		addToolTips();			
	}
}

function loadDefaultPrimarySupervisor(){
	var url="/pgadmissions/prospectus/projects/defaultPrimarySupervisor";
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
			var primarySupervisor = JSON.parse(data);
			displayPrimarySupervisor(primarySupervisor);
		},
		complete: function() {
			hideLoader();
		}
	});
}

function displayPrimarySupervisor(supervisor){
	setValue($('#primarySupervisorFirstName'), supervisor.firstname);
	setValue($('#primarySupervisorLastName'), supervisor.lastname);
	setValue($('#primarySupervisorEmail'), supervisor.email);
}

function displayProjectAdministrator(administrator){
	if(administrator) {
		setValue($('#projectAdministratorFirstName'), administrator.firstname);
		setValue($('#projectAdministratorLastName'), administrator.lastname);
		setValue($('#projectAdministratorEmail'), administrator.email);
		$("#projectAdvertHasAdministratorRadioYes").prop("checked", true);
	} else {
		$("#projectAdvertHasAdministratorRadioNo").prop("checked", true);
	}
	checkProjectAdministrator();
}

function displaySecondarySupervisor(supervisor){
	if(supervisor) {
		setValue($('#secondarySupervisorFirstName'), supervisor.firstname);
		setValue($('#secondarySupervisorLastName'), supervisor.lastname);
		setValue($('#secondarySupervisorEmail'), supervisor.email);
		$("#projectAdvertHasSecondarySupervisorRadioYes").prop("checked", true);
	} else {
		$("#projectAdvertHasSecondarySupervisorRadioNo").prop("checked", true);
	}
	checkSecondarySupervisor();
}

function setValue(element,value){
	element.val(value);
}
function appendProjectRow(project){
	$('#projectAdvertsTable tbody tbody').append(
		'<tr project-id="' + project.id + '">' +
			'<td>' + 
				project.advert.title +
			'</td>' +
			'<td>' +
				'<button class="button-show button-hint" type="button" data-desc="Get Advertising Resources">Show</button>' +
			'</td>' +
			'<td>' +
				'<button class="button-edit button-hint" type="button" data-desc="Edit Advert">Edit</button>' +
			'</td>' +
			'<td>' +
				'<button class="button-delete button-hint" type="button" data-desc="Delete Advert">Remove</button>' +
			'</td>' +
		'</tr>'	
	);
}	
function projectAdvertHasClosingDate(){
	return projectAdvertRadioHasValue('projectAdvertHasClosingDateRadio');
}

function projectAdvertHasAdministrator(){
	return projectAdvertRadioHasValue('projectAdvertHasAdministratorRadio');
}

function projectAdvertHasSecondarySupervisor(){
	return projectAdvertRadioHasValue('projectAdvertHasSecondarySupervisorRadio');
}

function projectAdvertRadioHasValue(radioName){
	return $("input:radio[name='"+radioName+"']:checked").val()=="true";
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