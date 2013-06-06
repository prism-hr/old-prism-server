$(document).ready(function(){
		bindDatePicker($("#programAdvertClosingDateInput"));
		bindAddClosingDateButtonAction();
		bindSaveButtonAction();
		bindProgramSelectChangeAction();
		bindClosingDatesActions();
		getProgramData();
		checkDates();
});

function bindProgramSelectChangeAction(){
	$("#programAdvertProgramSelect").bind('change', function() {
		getProgramData();
		
	});
}

function getProgramData(){
	clearProgramAdvertErrors();
	var programme_code= $("#programAdvertProgramSelect").val();
	if(programme_code==""){
		clearAll();
	}
	else{
		getAdvertData(programme_code);
		getClosingDatesData(programme_code);
	}
}

function getClosingDatesData(program_code){
	clearClosingDate();
	$.ajax({
        type: 'GET',
        statusCode: {
                401: function() { window.location.reload(); },
                500: function() { window.location.href = "/pgadmissions/error"; },
                404: function() { window.location.href = "/pgadmissions/404"; },
                400: function() { window.location.href = "/pgadmissions/400"; },                  
                403: function() { window.location.href = "/pgadmissions/404"; }
        },
        url:"/pgadmissions/prospectus/programme/getClosingDates",
        data: {
        	programCode: program_code,
        }, 
        success: function(data) {
        	var map = JSON.parse(data);
        	refreshClosingDates(map['closingDates']);
			checkIfErrors();
        },
        complete: function() {
        }
    });
}

function refreshClosingDates(closingDates){
	$('#programAdvertClosingDates tr').remove();
	jQuery.each(closingDates, function(index, closingDate) {
		appendClosingDateRow(closingDate);
	});
	sortClosingDates();
	checkDates();
}

function checkDates() {
	if ($('#programAdvertClosingDates td').length == 0) {
		$('#programAdvertClosingDates').hide();
	} else {
		$('#programAdvertClosingDates').show();
	}
}

function bindAddClosingDateButtonAction(){
	$("#addProgramAdvertClosingDate").bind('click', function(){
		clearProgramAdvertClosingDateErrors();
		$('#ajaxloader').show();
		var btnAction = $("#addProgramAdvertClosingDate").text();
		var update = btnAction.indexOf("Edit") !== -1; 
		var url="/pgadmissions/prospectus/programme/addClosingDate";
		if(update){
			url = "/pgadmissions/prospectus/programme/updateClosingDate";
		}
		
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
				program: $("#programAdvertProgramSelect").val(),
				id: $('#programAdvertClosingDateId').val(),
				closingDate : $('#programAdvertClosingDateInput').val(),
				studyPlaces : $('#programAdvertStudyPlacesInput').val()
			}, 
			success: function(data) {
				var map = JSON.parse(data);
				if(!map['programClosingDate']){
					if(map['program']){
						$("#programAdvertProgramDiv").append(getErrorMessageHTML(map['program']));
					}
					if(map['closingDate']){
						$("#programAdvertClosingDateDiv").append(getErrorMessageHTML(map['closingDate']));
					}
					if(map['studyPlaces']){
						$("#programAdvertStudyPlacesDiv").append(getErrorMessageHTML(map['studyPlaces']));
					}
				}
				else{
					if(update){
						replaceClosingDateRow(map['programClosingDate']);
					}
					else{
						appendClosingDateRow(map['programClosingDate']);
					}
					clearClosingDate();
					sortClosingDates();
					checkDates();
				}
				checkIfErrors();
			},
			complete: function() {
				$('#ajaxloader').fadeOut('fast');
			}
		});
	});
}

function replaceClosingDateRow(closingDate){
	$('#cdr-'+closingDate.id).html(closingDateTd(closingDate));
}

function appendClosingDateRow(closingDate){
	$('#programAdvertClosingDates tbody').append(
			'<tr>'+
			'<td id="cdr-'+closingDate.id+'">'+
				closingDateTd(closingDate)+
			'</td>'+
			'<td>'+
				'<button class="button-edit" type="button" data-desc="Edit">Edit</button>'+
			'</td>'+
			'<td>'+
			'<button class="button-delete" type="button" data-desc="Remove">Remove</button>'+
			'</td>'+
		'</tr>'	
	);
}

function closingDateTd(closingDate){
	var date = formatDate(new Date(closingDate.closingDate));
	var studyPlaces ="";
	if(closingDate.studyPlaces > 0){	
		studyPlaces = " ("+closingDate.studyPlaces+" Places)";
	}
	return date  + studyPlaces+
	'<input id="cdr-id" type="hidden" value="'+closingDate.id+'"/>'+
	'<input id="cdr-closingDate" type="hidden" value="'+date+'"/>'+
	'<input id="cdr-studyPlaces" type="hidden" value="'+closingDate.studyPlaces+'"/>';
}

function formatDate(date) {
	return $.datepicker.formatDate('d M yy', date);
}

function sortClosingDates() {
    var $table = $('#programAdvertClosingDates');
    var $rows = $('tbody > tr',$table);
    $rows.sort(function(a, b){
    	var keyA = new Date($('#cdr-closingDate',a).val());
        var keyB = new Date($('#cdr-closingDate',b).val());
        return (keyA > keyB) ? 1 : (keyA == keyB) ? 0 : -1;
    });
    $.each($rows, function(index, row){
      $table.append(row);
    });
}

function bindClosingDatesActions(){
	$('#programAdvertClosingDates').on('click', '.button-edit', function(){
		var $row = $(this).closest('tr');
		editDate($row);
	});
	$('#programAdvertClosingDates').on('click', '.button-delete', function(){
		var $row = $(this).closest('tr');
		removeClosingDate($row, $row.find("#cdr-id").val());
	});
}

function editDate(row){
	clearClosingDate();
	$('#programAdvertClosingDateId').val(row.find("#cdr-id").val());
	$('#programAdvertClosingDateInput').val(row.find("#cdr-closingDate").val());
	var placesValue = row.find("#cdr-studyPlaces").val();
	if(placesValue!="undefined"){
		$('#programAdvertStudyPlacesInput').val(placesValue);
	}
	$('#addProgramAdvertClosingDate').text("Edit");
	$('#programAdvertClosingDateHeading').text("Edit Closing Date");
	
}

function removeClosingDate(row, id){
	$.ajax({
        type: 'POST',
        statusCode: {
                401: function() { window.location.reload(); },
                500: function() { window.location.href = "/pgadmissions/error"; },
                404: function() { window.location.href = "/pgadmissions/404"; },
                400: function() { window.location.href = "/pgadmissions/400"; },                  
                403: function() { window.location.href = "/pgadmissions/404"; }
        },
        url:"/pgadmissions/prospectus/programme/removeClosingDate",
        data: {
        	programCode: $("#programAdvertProgramSelect").val(),
        	closingDateId: id
        }, 
        success: function(data) {
        	var map = JSON.parse(data);
        	if(map['removedDate']){
        		row.remove();
				checkDates();
        	}
        },
        complete: function() {
        }
    });
}

function getAdvertData(programme_code){
	$.ajax({
        type: 'GET',
        statusCode: {
                401: function() { window.location.reload(); },
                500: function() { window.location.href = "/pgadmissions/error"; },
                404: function() { window.location.href = "/pgadmissions/404"; },
                400: function() { window.location.href = "/pgadmissions/400"; },                  
                403: function() { window.location.href = "/pgadmissions/404"; }
        },
        url:"/pgadmissions/prospectus/programme/getAdvertData",
        data: {
        	programCode: programme_code,
        }, 
        success: function(data) {
        	var map = JSON.parse(data);
        	updateAdvertSection(map);
        	updateProgramSection(map['advert']);
        },
        complete: function() {
        }
    });
}
function updateAdvertSection(map){
	$("#programAdvertButtonToApply").val(map['buttonToApply']);
	$("#programAdvertLinkToApply").val(map['linkToApply']);
}

function updateProgramSection(advert){
	if(advert){
		setTextAreaValue($("#programAdvertDescriptionText"),advert['description']);
    	setTextAreaValue($("#programAdvertFundingText"),advert['funding']);
		
		var durationOfStudyInMonths=advert['studyDuration'];
		if(durationOfStudyInMonths%12==0){
			$("#programAdvertStudyDurationInput").val((durationOfStudyInMonths/12).toString());
			$("#programAdvertStudyDurationUnitSelect").val('Years');
		}else{
			$("#programAdvertStudyDurationInput").val(durationOfStudyInMonths.toString());
			$("#programAdvertStudyDurationUnitSelect").val('Months');
		}
		
		if(advert['active']){$("#programAdvertIsActiveRadioYes").prop("checked", true);}
		else{$("#programAdvertIsActiveRadioNo").prop("checked", true);}
	}else{
		clearAdvert();
	}
}

function bindSaveButtonAction(){
	$("#programAdvertSave").bind('click', function(){
		clearProgramAdvertErrors();
		var duration = {
			value : $("#programAdvertStudyDurationInput").val(),
			unit : $("#programAdvertStudyDurationUnitSelect").val()
		};
		var acceptApplications;
		if($("#programAdvertIsActiveRadioYes").prop("checked")){acceptApplications="true";}
		else if($("#programAdvertIsActiveRadioNo").prop("checked")){acceptApplications="false";}
		else {acceptApplications="";}
		$('#ajaxloader').show();
		$.ajax({
			type: 'POST',
			statusCode: {
				401: function() { window.location.reload(); },
				500: function() { window.location.href = "/pgadmissions/error"; },
				404: function() { window.location.href = "/pgadmissions/404"; },
				400: function() { window.location.href = "/pgadmissions/400"; },                  
				403: function() { window.location.href = "/pgadmissions/404"; }
			},
			url:"/pgadmissions/prospectus/programme/saveProgramAdvert",
			data: {
				programCode: $("#programAdvertProgramSelect").val(),
				description:$("#programAdvertDescriptionText").val(),
				studyDuration:JSON.stringify(duration),
				funding:$("#programAdvertFundingText").val(),
				active:acceptApplications
			}, 
			success: function(data) {
				var map = JSON.parse(data);
				if(!map['success']){
					if(map['program']){
						$("#programAdvertProgramDiv").append(getErrorMessageHTML(map['program']));
					}
					if(map['description']){
						$("#programAdvertDescriptionDiv").append(getErrorMessageHTML(map['description']));
					}
					if(map['studyDuration']){
						$("#programAdvertStudyDurationDiv").append(getErrorMessageHTML(map['studyDuration']));
					}
					if(map['active']){
						$("#programAdvertIsActiveDiv").append(getErrorMessageHTML(map['active']));
					}
				}
				checkIfErrors();
			},
			complete: function() {
				$('#ajaxloader').fadeOut('fast');
			}
		});

	});
}

function clearAdvert(){
	setTextAreaValue($("#programAdvertDescriptionText"),"");
	setTextAreaValue($("#programAdvertFundingText"),"");
	$("#programAdvertStudyDurationInput").val("");
	$("#programAdvertStudyDurationUnitSelect").val("");
	$("#programAdvertFundingText").val("");
	$("#programAdvertIsActiveRadioYes").prop("checked", false);
	$("#programAdvertIsActiveRadioNo").prop("checked", false);
}

function setTextAreaValue(textArea, value){
	textArea.val(value);
	triggerKeyUp(textArea);
}

function triggerKeyUp(element) {
	var keyup = jQuery.Event("keyup");
	element.trigger(keyup);
}

function clearAll(){
	clearProgramAdvertErrors();
	clearAdvert();
	$("#programAdvertButtonToApply").val("");
	$("#programAdvertLinkToApply").val("");
	clearClosingDate();
	$('#programAdvertClosingDates tr').remove();
	checkDates();
}

function clearClosingDate(){
	$("#programAdvertClosingDateId").val("");
	$("#programAdvertClosingDateInput").val("");
	$("#programAdvertStudyPlacesInput").val("");
	$('#addProgramAdvertClosingDate').text("Add");
	$('#programAdvertClosingDateHeading').text("Add Closing Date");
	clearProgramAdvertClosingDateErrors();
}

function clearProgramAdvertClosingDateErrors(){
	$('#programAdvertClosingDateGroup .error').remove();
}

function clearProgramAdvertErrors(){
	$("#programAdvertDiv .error").remove();
	$('.infoBar').removeClass('alert-error').addClass('alert-info').find('i').removeClass('icon-warning-sign').addClass('icon-info-sign');
	checkIfErrors();
}
