$(document).ready(function(){
		bindDatePicker($("#closingDate"));
		bindAddClosingDateButtonAction();
		bindSaveButtonAction();
		bindProgramSelectChangeAction();
		bindClosingDatesActions();
		generalTabing();
		checkDates();
});

function bindProgramSelectChangeAction(){
	$("select#programme").bind('change', function() {
		clearPreviousErrors();
		var programme_code= $("#programme").val();
		if(programme_code==""){
			clearAll();
		}
		else{
			getAdvertData(programme_code);
			getClosingDatesData(programme_code);
		}
	});
}

function getClosingDatesData(program_code){
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
        },
        complete: function() {
        }
    });
}

function refreshClosingDates(closingDates){
	$('#closingDates tr').remove();
	jQuery.each(closingDates, function(index, closingDate) {
		appendClosingDateRow(closingDate);
	});
	sortClosingDates();
	checkDates();
}
function checkDates() {
	if ($('#closingDates td').length == 0) {
		$('#closingDates').hide();
	} else {
		$('#closingDates').show();
	}
}
function bindAddClosingDateButtonAction(){
	$("#addClosingDate").bind('click', function(){
		clearPreviousErrors();
		$('#ajaxloader').show();
		var btnAction = $("#addClosingDate").text();
		var update = btnAction.indexOf("Update") !== -1; 
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
				program: $("#programme").val(),
				id: $('#closingDateId').val(),
				closingDate : $('#closingDate').val(),
				studyPlaces : $('#studyPlaces').val()
			}, 
			success: function(data) {
				var map = JSON.parse(data);
				if(!map['programClosingDate']){
					if(map['program']){
						$("#program").append(getErrorMessageHTML(map['program']));
					}
					if(map['closingDate']){
						$("#closingDateRow").append(getErrorMessageHTML(map['closingDate']));
					}
					if(map['studyPlaces']){
						$("#studyPlacesRow").append(getErrorMessageHTML(map['studyPlaces']));
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
	$('#closingDates tbody').append(
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
    var $table = $('#closingDates');
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
	$('#closingDates').on('click', '.button-edit', function(){
		var $row = $(this).closest('tr');
		editDate($row);
	});
	$('#closingDates').on('click', '.button-delete', function(){
		var $row = $(this).closest('tr');
		removeClosingDate($row, $row.find("#cdr-id").val());
	});
}

function editDate(row){
	clearClosingDate();
	$('#closingDateId').val(row.find("#cdr-id").val());
	$('#closingDate').val(row.find("#cdr-closingDate").val());
	var placesValue = row.find("#cdr-studyPlaces").val();
	if(placesValue!="undefined"){
		$('#studyPlaces').val(placesValue);
	}
	$('#addClosingDate').text("Update Closing Date");
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
        	programCode: $("#programme").val(),
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
	$("#buttonToApply").val(map['buttonToApply']);
	$("#linkToApply").val(map['linkToApply']);
}

function updateProgramSection(advert){
	if(advert){
		setTextAreaValue($("#programmeDescription"),advert['description']);
    	setTextAreaValue($("#programmeFundingInformation"),advert['fundingInformation']);
		
		var durationOfStudyInMonths=advert['durationOfStudyInMonth'];
		if(durationOfStudyInMonths%12==0){
			$("#programmeDurationOfStudy").val((durationOfStudyInMonths/12).toString());
			$("#timeUnit").val('Years');
		}else{
			$("#programmeDurationOfStudy").val(durationOfStudyInMonths.toString());
			$("#timeUnit").val('Months');
		}
		
		if(advert['isCurrentlyAcceptingApplications']){$("#currentlyAcceptingApplicationYes").prop("checked", true);}
		else{$("#currentlyAcceptingApplicationNo").prop("checked", true);}
	}else{
		clearAdvert();
	}
}

function bindSaveButtonAction(){
	$("#save-go").bind('click', function(){
		clearPreviousErrors();
		var duration = {
			value : $("#programmeDurationOfStudy").val(),
			unit : $("#timeUnit").val()
		};
		var acceptApplications;
		if($("#currentlyAcceptingApplicationYes").prop("checked")){acceptApplications="true";}
		else if($("#currentlyAcceptingApplicationNo").prop("checked")){acceptApplications="false";}
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
				programCode: $("#programme").val(),
				description:$("#programmeDescription").val(),
				durationOfStudyInMonth:JSON.stringify(duration),
				fundingInformation:$("#programmeFundingInformation").val(),
				isCurrentlyAcceptingApplications:acceptApplications
			}, 
			success: function(data) {
				var map = JSON.parse(data);
				if(!map['success']){
					if(map['program']){
						$("#program").append(getErrorMessageHTML(map['program']));
					}
					if(map['description']){
						$("#description").append(getErrorMessageHTML(map['description']));
					}
					if(map['durationOfStudyInMonth']){
						$("#durationOfStudyInMonth").append(getErrorMessageHTML(map['durationOfStudyInMonth']));
					}
					if(map['isCurrentlyAcceptingApplications']){
						$("#isCurrentlyAcceptingApplications").append(getErrorMessageHTML(map['isCurrentlyAcceptingApplications']));
					}
				}
			},
			complete: function() {
				$('#ajaxloader').fadeOut('fast');
			}
		});

	});
}

function clearAdvert(){
	setTextAreaValue($("#programmeDescription"),"");
	setTextAreaValue($("#programmeFundingInformation"),"");
	$("#programmeDurationOfStudy").val("");
	$("#timeUnit").val("");
	$("#programmeFundingInformation").val("");
	$("#currentlyAcceptingApplicationYes").prop("checked", false);
	$("#currentlyAcceptingApplicationNo").prop("checked", false);
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
	clearPreviousErrors();
	clearAdvert();
	$("#buttonToApply").val("");
	$("#linkToApply").val("");
	clearClosingDate();
}

function clearClosingDate(){
	$("#closingDateId").val("");
	$("#closingDate").val("");
	$("#studyPlaces").val("");
	$('#addClosingDate').text("Add Closing Date");
	checkDates();
}

function getErrorMessageHTML(message){
	return "<div class=\"row error\"><div class=\"field\"><div class=\"alert alert-error\"><i class=\"icon-warning-sign\"></i> "+message+"</div></div></div>";
}

function clearPreviousErrors(){
	$(".error").remove();
}
