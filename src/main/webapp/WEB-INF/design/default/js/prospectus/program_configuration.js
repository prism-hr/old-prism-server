$(document).ready(function(){
		bindDatePicker($("#programAdvertClosingDateInput"));
		bindAddClosingDateButtonAction();
		bindSaveButtonAction();
		bindProgramSelectChangeAction();
		bindClosingDatesActions();
		getProgramData();
		checkDates();
		initEditors();
		$('.selectpicker').selectpicker();
});
function initEditors() {
	tinymce.init({
	    selector: "#programAdvertDescriptionText",
	    plugins: ["link wordcount"],
	    width: 480,
	    height : 180,
	    menubar: false,
	    content: "",
	    toolbar: "bold italic  | bullist numlist outdent indent | link unlink | undo redo"
	  /*setup : function(ed) {
	    	ed.on('keyup', function(e) { $('textArea#programAdvertDescriptionText').val(tinymce.get('programAdvertDescriptionText').getContent())});
	    }*/
	});
	tinymce.init({
	    selector: "#programAdvertFundingText",
	    plugins: ["link wordcount"],
	    width: 480,
	    height : 180,
	    menubar: false,
	    content: "",
	    toolbar: "bold italic  | bullist numlist outdent indent | link unlink | undo redo"
	});
	tinymce.init({
		selector : "#projectAdvertDescriptionText",
	    plugins: ["link wordcount"],
	    width: 480,
	    height : 180,
	    menubar: false,
	    content: "",
	    toolbar: "bold italic  | bullist numlist outdent indent | link unlink | undo redo"
	});
	tinymce.init({
		selector : "#projectAdvertFundingText",
	    plugins: ["link wordcount"],
	    width: 480,
	    height : 180,
	    menubar: false,
	    content: "",
	    toolbar: "bold italic  | bullist numlist outdent indent | link unlink | undo redo"
	});
}
function bindProgramSelectChangeAction(){
	$("#programAdvertProgramSelect").bind('change', function() {
		getProgramData();
	});
}
function checktoDisable() {
	if ($("#programAdvertProgramSelect").val() != "") {
		$("#advertGroup label, #programAdvertClosingDateGroup label").removeClass("grey-label").parent().find('.hint').removeClass("grey");
		$("#advertGroup input, #advertGroup textarea, #advertGroup select, #programAdvertClosingDateGroup input").removeAttr("readonly", "readonly");
		$("#advertGroup input, #advertGroup textarea, #advertGroup select, #advertGroup button, #programAdvertClosingDateGroup input").removeAttr("disabled", "disabled");
		$("#programAdvertClosingDateGroup a").removeClass("disabled");
	} else {
		$("#advertGroup label, #programAdvertClosingDateGroup label").addClass("grey-label").parent().find('.hint').addClass("grey");
		$("#advertGroup input, #advertGroup textarea, #advertGroup select, #programAdvertClosingDateGroup input").attr("readonly", "readonly");
		$("#advertGroup input, #advertGroup textarea, #advertGroup select, #advertGroup button, #programAdvertClosingDateGroup input").attr("disabled", "disabled");
		$("#programAdvertClosingDateGroup a").addClass("disabled");
		$("#advertGroup input, #advertGroup textarea, #programAdvertClosingDateGroup input, #programAdvertLinkToApply, #programAdvertButtonToApply").val('');
		$("#programAdvertIsActiveRadioYes, #programAdvertIsActiveRadioNo").prop('checked', false);
		$("#programAdvertClosingDates").css("display", "none");
		$("#programAdvertDescriptionText").text('1000 Characters left');
	}
}
function getProgramData(){
	clearProgramAdvertErrors();
	var programme_code= $("#programAdvertProgramSelect").val();
	var programme_name= $("#programAdvertProgramSelect option:selected").text();
	checktoDisable();
	changeInfoBarName(programme_name,false);
	if(programme_code==""){
		clearAll();
	}
	else{
		getAdvertData(programme_code);
		getClosingDatesData(programme_code);
	}
	
}
function changeInfoBarName(text,advertUpdated) {
	if (advertUpdated) {
		var programme_name = $("#programAdvertProgramSelect option:selected").text();
		infohtml = "<i class='icon-ok-sign'></i> Your advert for <b>"+programme_name+"</b> has been "+text+".";
		if ($('.infoBar').hasClass('alert-info')) {
			$('.infoBar').addClass('alert-success').removeClass('alert-info').html(infohtml);
		} else {
			$('.infoBar').html(infohtml);
		}
	} else {
		if (text != "Select...") {
			infohtml = "<i class='icon-info-sign'></i> Manage the advert for: <b>"+text+"</b>.";
			infodate = "<i class='icon-info-sign'></i> Manage closing dates for: <b>"+text+"</b>.";
			inforesource = "<i class='icon-info-sign'></i> Embed these resources to provide applicants with links to apply for: <b>"+text+"</b>."; 
		} else {
			infohtml =  "<i class='icon-info-sign'></i> Manage the advert for your programme here.";
			infodate = "<i class='icon-info-sign'></i> Manage closing dates for your programme here.";
			inforesource = "<i class='icon-info-sign'></i> Embed these resources to provide applicants with links to apply for your programme."; 
		}
		$('#infodates').html(infodate);
		$('#infoResources').html(inforesource);
		if ($('.infoBar').hasClass('alert-success')) {
			$('.infoBar').addClass('alert-info').removeClass('alert-success').html(infohtml);
		} else {
			$('.infoBar').html(infohtml);
		}
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
		saveClosingDate();
	});
}
function saveClosingDate(){
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
	var date = formatProgramClosingDate(new Date(closingDate.closingDate));
	var studyPlaces ="";
	if(closingDate.studyPlaces > 0){	
		studyPlaces = " ("+closingDate.studyPlaces+" Places)";
	}
	return date  + studyPlaces+
	'<input id="cdr-id" type="hidden" value="'+closingDate.id+'"/>'+
	'<input id="cdr-closingDate" type="hidden" value="'+date+'"/>'+
	'<input id="cdr-studyPlaces" type="hidden" value="'+closingDate.studyPlaces+'"/>';
}

function formatProgramClosingDate(date) {
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
	var linkToApply = map['linkToApply'];
	var titleSeleted = $("#programAdvertProgramSelect option:selected").text();
	var sharethisvar = 'http://api.addthis.com/oexchange/0.8/offer?url='+linkToApply+'&title='+titleSeleted;

	$("#programAdvertButtonToApply").val(map['buttonToApply']);
	$("#modalButtonToApply").val(map['buttonToApply']);
	
	$("#programAdvertLinkToApply").val(linkToApply);
	$("#modalLinkToApply").val(linkToApply);
	
	$('#sharethis').prop("href", sharethisvar);

}

function updateProgramSection(advert){
	if(advert){
		setTextAreaValue($("#programAdvertDescriptionText"),advert['description']);
    	setTextAreaValue($("#programAdvertFundingText"),advert['funding']);
    	$("#programAdvertId").val(advert.id);
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
		saveAdvert();
	});
}

function saveAdvert(){
	clearProgramAdvertErrors();
	var duration = {
		value : $("#programAdvertStudyDurationInput").val(),
		unit : $("#programAdvertStudyDurationUnitSelect").val()
	};
	var acceptApplications="";
	if($("#programAdvertIsActiveRadioYes").prop("checked")){acceptApplications="true";}
	else if($("#programAdvertIsActiveRadioNo").prop("checked")){acceptApplications="false";}

	var update = isAdvertLoaded(); 
	var url="/pgadmissions/prospectus/programme/saveProgramAdvert";
	labeltext = 'saved';
	if(update){
		url = "/pgadmissions/prospectus/programme/editProgramAdvert";
		labeltext = 'updated';
	}

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
		url: url,
		data: {
			programCode: $("#programAdvertProgramSelect").val(),
			description:addBlankLinks(tinymce.get('programAdvertDescriptionText').getContent()),
			studyDuration:JSON.stringify(duration),
			funding:tinymce.get('programAdvertFundingText').getContent(),
			active:acceptApplications,
			id: $("#programAdvertId").val()
		}, 
		success: function(data) {
			var map = JSON.parse(data);
			if(!map['advertId']){
				if(map['program']){
					$("#programAdvertProgramDiv").append(getErrorMessageHTML(map['program']));
				}
				if(map['description']){
					$("#programAdvertDescriptionDiv").append(getErrorMessageHTML(map['description']));
				}
				if(map['funding']) {
					$("#programAdvertFundingDiv").append(getErrorMessageHTML(map['funding']));
				}
				if(map['studyDuration']){
					$("#programAdvertStudyDurationDiv").append(getErrorMessageHTML(map['studyDuration']));
				}
				if(map['active']){
					$("#programAdvertIsActiveDiv").append(getErrorMessageHTML(map['active']));
				}
				checkIfErrors();
			}
			else{
				$('#programAdvertId').val(map['advertId']);
				advertUpdated = true;
				changeInfoBarName(labeltext,true);
			}
			
		},
		complete: function() {
			$('#ajaxloader').fadeOut('fast');
			// Display modal
			$('#resourcesModal').modal('show');
		}
	});
}

function isAdvertLoaded(){
	var advertId=$("#programAdvertId").val();
	return  advertId !== undefined && advertId != "";
}

function clearAdvert(){
	$("#programAdvertDescriptionText").val("");
	$("#programAdvertFundingText").val("");
	$("#programAdvertId").val("");
	$("#programAdvertStudyDurationInput").val("");
	$("#programAdvertStudyDurationUnitSelect").val("");
	$("#programAdvertIsActiveRadioYes").prop("checked", false);
	$("#programAdvertIsActiveRadioNo").prop("checked", false);
	tinyMCE.execCommand("mceRepaint");
}

function setTextAreaValue(textArea, value){
	textArea.val(value);
	triggerKeyUp(textArea);
	idselect = textArea.attr('id');
	tinymce.get(idselect).setContent(value);
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
	$("#programAdvertDescriptionText").val("");
	$("#programAdvertClosingDateId").val("");
	$("#programAdvertClosingDateInput").val("");
	$("#programAdvertStudyPlacesInput").val("");
	$('#addProgramAdvertClosingDate').text("Add");
	$('#programAdvertClosingDateHeading').text("Add Closing Date");
	clearProgramAdvertClosingDateErrors();
}

function clearProgramAdvertClosingDateErrors(){
	$('#programAdvertClosingDateGroup .error').remove();
	clearInfoBarWarning();
}

function clearProgramAdvertErrors(){
	$("#programAdvertDiv .error").remove();
	clearInfoBarWarning();
}

function clearInfoBarWarning(){
	$('.infoBar').removeClass('alert-error').addClass('alert-info').find('i').removeClass('icon-warning-sign').addClass('icon-info-sign');
	checkIfErrors();
}
