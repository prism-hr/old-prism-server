// ===============================================================================
// PROGRAMME DETAILS section
// ===============================================================================

function enableOrDisableStartDate() {
	if ($("#studyOption").val() == "") {
		$("#startDate").attr("disabled", "disabled");
		$("#lbl_startDate").addClass("grey-label").parent().find('.hint').addClass("grey");
	} else {
		$("#startDate").removeAttr("disabled", "disabled");
		$("#lbl_startDate").removeClass("grey-label").parent().find('.hint').removeClass("grey");
	}
}

function enableOrDisableReferrerText() {
    var selectedText = $("#referrer option:selected").text(); 
	if (selectedText === "Other" || selectedText === "Other Academic Staff" || selectedText === "Other Website" || 
	        selectedText === "Newspaper/Recruitment guide/Magazine advertisement") {
		//$("#referrer-text-lbl").text($.trim($("#referrer-text-lbl").text())).append('<em>*</em>');
		$("#referrer-text-lbl").removeClass("grey-label").parent().find('.hint').removeClass("grey");
		$("#referrer_text").removeClass("grey-label");
		$("#referrer_text").removeAttr("disabled", "disabled");
		if ($("#referrer_text").val() === "Not Required") {
			$("#referrer_text").val("");
		}
	} else {
		//$("#referrer-text-lbl em").remove();
		$("#referrer-text-lbl").addClass("grey-label").parent().find('.hint').addClass("grey");
		$("#referrer_text").addClass("grey-label");
		$("#referrer_text").attr("disabled", "disabled");
		$("#referrer_text").val("Not Required");
	}
}

$(document).ready(function()
{
	enableOrDisableStartDate();
	enableOrDisableReferrerText();
	
	$("#referrer").bind('change', function() {
		enableOrDisableReferrerText();
	});
	
	
	// -------------------------------------------------------------------------------
	// Get the date from the programe.
	// -------------------------------------------------------------------------------
	$("#studyOption").bind('change', function() {
		$.ajax({
			type: 'GET',
			statusCode: { 
				401: function() {window.location.reload(); },
				500: function() { window.location.href = "/pgadmissions/error"; },
				404: function() { window.location.href = "/pgadmissions/404"; },
				400: function() { window.location.href = "/pgadmissions/400"; },				  
				403: function() { window.location.href = "/pgadmissions/404"; }
			  },
			  url:"/pgadmissions/update/getProgrammeStartDate",
			  data: {
				  applicationId: $('#applicationId').val(),
				  studyOption: $('#studyOption').val(),
				  cacheBreaker: new Date().getTime()					
			  },
			  success: function(data) {
				  $('#startDate').val(data);
				  enableOrDisableStartDate();
			  }
		});
	});
	
	$("#acceptTermsPDValue").val("NO");
	$("#addSupervisorButton").show();
	var unsavedSupervisors = 0;
	
	var progImgCount = 0;
	
	var currentRel = 0; // currently edited supervisor row
	
	$('#supervisors').toggle($("#supervisors tbody").children().length > 0);
	

	// -------------------------------------------------------------------------------
	// "Accept terms" checkbox.
	// -------------------------------------------------------------------------------
	$("input[name*='acceptTermsPDCB']").click(function()
	{
		if ($("#acceptTermsPDValue").val() =='YES')
		{
			$("#acceptTermsPDValue").val("NO");
		}
		else
		{	
			$("#acceptTermsPDValue").val("YES");
			progImgCount = 0;
		}
	});
	

	// -------------------------------------------------------------------------------
	// Delete supervisor button.
	// -------------------------------------------------------------------------------
	$("#supervisor_div").on("click", "a[name=\"deleteSupervisor\"]", function()
	{	
		var id = this.id;
		if(id.indexOf("usd_") != -1)
		{
			id = id.replace('usd_', '');
			$('#'+id+"_ussupervisors").val('');
		}
		else
		{
			id = id.replace('supervisorDelete_', '');
			$('#'+id+"_supervisors").val('');
		}
		$(this).parent("span").remove();
		$(this).parent().parent().remove();
		$(this).parent().parent().html('');
		
		var rowCount = $("#supervisors tbody tr").length;
		
		if (rowCount == 0)
		{
			$("#supervisors").hide();
		}

		// Remove validation errors.
		$('#supervisor_div div.alert-error').remove();
		
		$("#supervisorId").val('');
		$("#supervisorFirstname").val('');
		$("#supervisorLastname").val('');
		$("#supervisorEmail").val('');
		$("input[name='awareSupervisor']").removeAttr('checked');
		
		$("#addSupervisorButton").show();
		$("#updateSupervisorButton").hide();

	});
	
	
	// -------------------------------------------------------------------------------
	// Add supervisor button.
	// -------------------------------------------------------------------------------
	$('#addSupervisorButton').on('click', function()
	{
		// Hide error messages.
		var errors = 0;
		$("#supervisor_div div.alert-error").remove();
		
		if ($('#supervisorFirstname').val() == "")
		{
			$('#supervisorFirstname').after('<div class="alert alert-error"><i class="icon-warning-sign"></i> You must make an entry. <div>').show();
			errors++;
		} else if (!validateStringChars($('#supervisorFirstname').val())) 
		{
			$('#supervisorFirstname').after('<div class="alert alert-error"><i class="icon-warning-sign"></i> You must only enter valid characters.</div>').show();
			errors++;
		} else if (!validateStringLength($('#supervisorFirstname').val(), 30)) 
		{
			$('#supervisorFirstname').after('<div class="alert alert-error"><i class="icon-warning-sign"></i> A maximum of 30 characters are allowed.</div>').show();
			errors++;
		}		
		
		if ($('#supervisorLastname').val() == "")
		{
			$('#supervisorLastname').after('<div class="alert alert-error"><i class="icon-warning-sign"></i> You must make an entry.</div>').show();
			errors++;
		} else if (!validateStringChars($('#supervisorLastname').val())) 
		{
			$('#supervisorLastname').after('<div class="alert alert-error"><i class="icon-warning-sign"></i> You must only enter valid characters.</div>').show();
			errors++;
		} else if (!validateStringLength($('#supervisorLastname').val(), 40)) 
		{
			$('#supervisorLastname').after('<div class="alert alert-error"><i class="icon-warning-sign"></i> A maximum of 40 characters are allowed.</div>').show();
			errors++;
		}	
		
		if ($("input[name='awareSupervisor']:checked").val() == undefined)
		{
			$('#awareNo').parent().after('<div class="alert alert-error"><i class="icon-warning-sign"></i> You must make a selection.</div>').show();
			errors++;
		}	
		if (!validateEmail($('#supervisorEmail').val()))
		{
			$('#supervisorEmail').after('<div class="alert alert-error"><i class="icon-warning-sign"></i> You must enter a valid email address.</div>').show();
			errors++;
		} else if (!validateStringLength($('#supervisorEmail').val(), 255)) 
		{
			$('#supervisorEmail').after('<div class="alert alert-error"><i class="icon-warning-sign"></i> A maximum of 255 characters are allowed.</div>').show();
			errors++;
		}
		
		if (errors == 0)
		{
			$("#supervisors").show();
			var isAware = $("input[name='awareSupervisor']:checked").val(); 
			var awareState = (isAware == "YES" ? "aware" : "unaware");

			var jsonString = JSON.stringify({
			    id: $('#supervisorId').val(),
			    firstname: $('#supervisorFirstname').val(),
			    email: $('#supervisorEmail').val(),
			    awareSupervisor: isAware
			  });
			
			unsavedSupervisors++;
			$('table#supervisors tbody').append(
				'<tr class="' + awareState + '" rel="'+ unsavedSupervisors +'">' +
				'<td data-desc="Supervisor ' + awareState + ' of application">' + $('#supervisorFirstname').val() + ' '+ $('#supervisorLastname').val() + ' (' + $('#supervisorEmail').val() + ')</td>' +
				'<td>' +
				'<a class="button-edit" data-desc="Edit" id="us_'+unsavedSupervisors+'" name="editSupervisorLink">edit</a> ' +
				'<a class="button-delete" data-desc="Delete" id="usd_'+unsavedSupervisors+'" name="deleteSupervisor">delete</a>' +
				'<input type="hidden" name="sId" id="us_'+unsavedSupervisors+'_supervisorId" value="" />' +
				'<input type="hidden" name="sFN" id="us_'+unsavedSupervisors+'firstname" value="' + $('#supervisorFirstname').val()+'"/>'	+								
				'<input type="hidden" name="sLN" id="us_'+unsavedSupervisors+'lastname" value="' + $('#supervisorLastname').val()+'"/>'	+								
				'<input type="hidden" name="sEM" id="us_'+unsavedSupervisors+'email" value="' + $('#supervisorEmail').val()+'"/>'	+								
				'<input type="hidden" name="sAS" id="us_'+unsavedSupervisors+'aware" value="' + isAware+'"/>'	+								
                '<input type="hidden" name="suggestedSupervisors" id="'+unsavedSupervisors+'_ussupervisors" value=' +"'" + '{"id":"' +  $('#supervisorId').val()+ '","firstname":"' +  $('#supervisorFirstname').val().replace("'", "\\u0027") + '","lastname":"' +  $('#supervisorLastname').val().replace("'", "\\u0027") + '","email":"' +  $('#supervisorEmail').val().replace("'", "\\u0027") +  '", "awareSupervisor":"' + isAware + '"} ' + "'" + "/>" +
				'</td>' +
				'</tr>');
			addToolTips();
		
			$('#supervisorId, #supervisorFirstname, #supervisorLastname, #supervisorEmail').val('');
			$("input[name='awareSupervisor']").removeAttr('checked');
		}
		else
		{
			$('#acceptTermsPDCB').prop('checked', false);
			markSectionError('#programmeDetailsSection');
		}
	});


	// -------------------------------------------------------------------------------
	// Edit supervisor button.
	// -------------------------------------------------------------------------------
	$("table#supervisors").on("click", ".button-edit", function()
	{
		var $edit_row = $(this).parent().parent('tr');
		currentRel		= $edit_row.attr('rel');
		
		var s_id				= $('input[name="sId"]', $edit_row).val();
		var s_firstname = $('input[name="sFN"]', $edit_row).val();
		var s_lastname	= $('input[name="sLN"]', $edit_row).val();
		var s_email			= $('input[name="sEM"]', $edit_row).val();
		var s_aware			= $('input[name="sAS"]', $edit_row).val();
		
		// Remove validation errors.
		$('#supervisor_div div.alert-error').remove();
		
		$("#supervisorId").val(s_id);
		$("#supervisorFirstname").val(s_firstname);
		$("#supervisorLastname").val(s_lastname);
		$("#supervisorEmail").val(s_email);
		$("input[name='awareSupervisor']").val([s_aware]);

		// Show all other edit buttons (as something hides the current edit button).
		$('table#supervisors .button-edit').show();
		
		$("#addSupervisorButton").hide();
		$("#updateSupervisorButton").show();
	});
	
	
	// -------------------------------------------------------------------------------
	// Update supervisor button.
	// -------------------------------------------------------------------------------
	$('#updateSupervisorButton').on('click', function()
	{
		// Hide error messages.
		var errors = 0;
		$("#supervisor_div div.alert-error").remove();
		
		$("#supervisors").show();
		if ($('#supervisorFirstname').val() == "")
		{
			$('#supervisorFirstname').after('<div class="alert alert-error"><i class="icon-warning-sign"></i> You must make an entry.</div>').show();
			errors++;
		} else if (!validateStringChars($('#supervisorFirstname').val())) 
		{
			$('#supervisorFirstname').after('<div class="alert alert-error"><i class="icon-warning-sign"></i> You must only enter valid characters.</div>').show();
			errors++;
		} else if (!validateStringLength($('#supervisorFirstname').val(), 30)) 
		{
			$('#supervisorFirstname').after('<div class="alert alert-error"><i class="icon-warning-sign"></i> A maximum of 30 characters are allowed.</div>').show();
			errors++;
		}		
		
		if ($('#supervisorLastname').val() == "")
		{
			$('#supervisorLastname').after('<div class="alert alert-error"><i class="icon-warning-sign"></i> You must make an entry.</div>').show();
			errors++;
		} else if (!validateStringChars($('#supervisorLastname').val())) 
		{
			$('#supervisorLastname').after('<div class="alert alert-error"><i class="icon-warning-sign"></i> You must only enter valid characters.</div>').show();
			errors++;
		} else if (!validateStringLength($('#supervisorLastname').val(), 40)) 
		{
			$('#supervisorLastname').after('<div class="alert alert-error"><i class="icon-warning-sign"></i> A maximum of 40 characters are allowed.</div>').show();
			errors++;
		}	
		
		if ($("input[name='awareSupervisor']:checked").val() == undefined)
		{
			$('#awareNo').parent().after('<div class="alert alert-error"><i class="icon-warning-sign"></i> You must make a selection.</div>').show();
			errors++;
		}
		
		if (!validateEmail($('#supervisorEmail').val()))
		{
			$('#supervisorEmail').after('<div class="alert alert-error"><i class="icon-warning-sign"></i> You must enter a valid email address.</div>').show();
			errors++;
		} else if (!validateStringLength($('#supervisorEmail').val(), 255)) 
		{
			$('#supervisorEmail').after('<div class="alert alert-error"><i class="icon-warning-sign"></i> A maximum of 255 characters are allowed.</div>').show();
			errors++;
		}
		
		if (errors == 0)
		{
			var isAware = $("input[name='awareSupervisor']:checked").val(); 
			var awareState = (isAware == "YES" ? "aware" : "unaware");
			
			$('table#supervisors tbody tr[rel="'+ currentRel +'"]').replaceWith(
					'<tr class="' + awareState + '" rel="'+ currentRel +'">' +
					'<td data-desc="' + awareState + ' of application">' + $('#supervisorFirstname').val() + ' '+ $('#supervisorLastname').val() + ' (' + $('#supervisorEmail').val() + ')</td>' +
					'<td>' +
					'<a class="button-edit" data-desc="Edit" id="us_'+currentRel+'" name="editSupervisorLink">edit</a> ' +
					'<a class="button-delete" data-desc="Delete" id="usd_'+currentRel+'" name="deleteSupervisor">delete</a>' +
					'<input name="sId" type="hidden" id="us_'+currentRel+'_supervisorId" value="' + $('#supervisorId').val()+'"/>'	+								
					'<input name="sFN" type="hidden" id="us_'+currentRel+'firstname" value="' + $('#supervisorFirstname').val()+'"/>'	+								
					'<input name="sLN" type="hidden" id="us_'+currentRel+'lastname" value="' + $('#supervisorLastname').val()+'"/>'	+								
					'<input name="sEM" type="hidden" id="us_'+currentRel+'email" value="' + $('#supervisorEmail').val()+'"/>'	+								
					'<input name="sAS" type="hidden" id="us_'+currentRel+'aware" value="' + isAware +'"/>'	+								
					'<input type="hidden" name="suggestedSupervisors" id="'+currentRel+'_ussupervisors" value=' +"'" + '{"id":"' +  $('#supervisorId').val() + '","firstname":"' +  $('#supervisorFirstname').val().replace("'", "\\u0027") + '","lastname":"' +  $('#supervisorLastname').val().replace("'", "\\u0027") + '","email":"' +  $('#supervisorEmail').val().replace("'", "\\u0027") +  '", "awareSupervisor":"' + isAware + '"} ' + "'" + "/>" +
					'</td>' +
					'</tr>'
			);
			
			currentRel = 0;

			addToolTips();			
		
			$('#supervisorId, #supervisorFirstname, #supervisorLastname, #supervisorEmail').val('');

			// restore add button.
			$('#updateSupervisorButton').hide();
			$('#addSupervisorButton').show();
			
			$("input[name='awareSupervisor']").removeAttr('checked');
		}
		else
		{
			markSectionError('#programmeDetailsSection');
		}
		
	});


	// -------------------------------------------------------------------------------
	// Save programme details.
	// -------------------------------------------------------------------------------
	$('#programmeSaveButton').on("click",function()
	{
		$("span[name='nonAcceptedPD']").html('');
		
		// Check for a "dirty" supervisor form.
		if (!isFormEmpty('#supervisor_div'))
		{
			$('#addSupervisorButton:visible, #updateSupervisorButton:visible').trigger('click'); // either add or update will be visible
			// If there was an error submitting the non-empty form, don't continue.
			if ($('#supervisor_div .alert-error:visible').length > 0)
			{
				return false;
			}
		}
		
		if ($("#acceptTermsPDValue").val() == 'NO')
		{ 
			postProgrammeData('open');
			addToolTips();
		}
		else
		{
			postProgrammeData('close');
		}
	});	


	// -------------------------------------------------------------------------------
	// Validation of an email address.
	// -------------------------------------------------------------------------------
	function validateEmail(email)
	{ 
		//var pattern = new RegExp(/^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/);
	    var pattern = new RegExp("[A-Za-z0-9!#-'\\*\\+\\-/=\\?\\^_`\\{-~]+(\\.[A-Za-z0-9!#-'\\*\\+\\-/=\\?\\^_`\\{-~]+)*@[A-Za-z0-9!#-'\\*\\+\\-/=\\?\\^_`\\{-~]+(\\.[A-Za-z0-9!#-'\\*\\+\\-/=\\?\\^_`\\{-~]+)*");
		var result = pattern.test(email);
		return result;
	} 
	
	// -------------------------------------------------------------------------------
	// Validation of supervisor first and last name (characters)
	// -------------------------------------------------------------------------------
	function validateStringChars(name) 
	{
		var pattern = new RegExp("^[\x00-\xFF]{1,1024}$");
		var result = pattern.test(name);
		return result;
	}

	// -------------------------------------------------------------------------------
	// Validation of supervisor first and last name (length)
	// -------------------------------------------------------------------------------
	function validateStringLength(name, length) 
	{
		if (name.length > length) {
			return false;
		}
		return true;
	}
	
	// -------------------------------------------------------------------------------
	// Close Programme Details section.
	// -------------------------------------------------------------------------------
	$('#programmeCloseButton').click(function()
	{
		$('#programme-H2').trigger('click');
		return false;
	});

	
	// -------------------------------------------------------------------------------
	// Cancel Programme Details button.
	// -------------------------------------------------------------------------------
	$('#programmeCancelButton').click(function(){
		$("#addSupervisorButton").show();
		$("#updateSupervisorButton").hide();
		$.ajax({
			 type: 'GET',
			 statusCode: {
				  401: function() {
					  window.location.reload();
				  },
				  500: function() {
					  window.location.href = "/pgadmissions/error";
				  },
				  404: function() {
					  window.location.href = "/pgadmissions/404";
				  },
				  400: function() {
					  window.location.href = "/pgadmissions/400";
				  },				  
				  403: function() {
					  window.location.href = "/pgadmissions/404";
				  }
			  },
				url:"/pgadmissions/update/getProgrammeDetails",
				data:{
					applicationId:  $('#applicationId').val(),					
					cacheBreaker: new Date().getTime()					
				},
				success: function(data) {
					$('#programmeDetailsSection').html(data);
				}
		});
	});
	

	// -------------------------------------------------------------------------------
	// Clear Programme Details button.
	// -------------------------------------------------------------------------------
	$('#programmeClearButton').click(function()
	{
		$('#programmeDetailsSection > div').append('<div class="ajax" />');
		loadProgrammeSection(true);
	});

	
	bindDatePicker('#startDate');
	addToolTips();
});


// -------------------------------------------------------------------------------
// Post programme details.
// -------------------------------------------------------------------------------
function postProgrammeData(message)
{
	var acceptedTheTerms;
	if ($("#acceptTermsPDValue").val() == 'NO'){
		acceptedTheTerms = false;
	}
	else{
		acceptedTheTerms = true;
	}
	
	if ($("#referrer_text").val() === "Not Required") {
		$("#referrer_text").val("");
	}
		
	var postData = {
			programmeName: $("#programmeName").val(),
			projectName: $("#projectName").val(), 
			studyOption: $("#studyOption").val(), 
			startDate: $("#startDate").val(),
			sourcesOfInterest: $("#referrer").val(),
			sourcesOfInterestText: $("#referrer_text").val(),
			application: $("#appId1").val(),
			application: $('#applicationId').val(),
			applicationId: $('#applicationId').val(),
			suggestedSupervisors: "",
			acceptedTerms: acceptedTheTerms,
			message: message
		};

	$('#programmeDetailsSection > div').append('<div class="ajax" />');
		
	$.ajax({
		type: 'POST',
		 statusCode: {
			  401: function() {
				  window.location.reload();
			  },
			  500: function() {
				  window.location.href = "/pgadmissions/error";
			  },
			  404: function() {
				  window.location.href = "/pgadmissions/404";
			  },
			  400: function() {
				  window.location.href = "/pgadmissions/400";
			  },				  
			  403: function() {
				  window.location.href = "/pgadmissions/404";
			  }
		  },
		url:"/pgadmissions/update/editProgrammeDetails",
		data:$.param(postData) + "&" + $('[input[name="suggestedSupervisors"]').serialize(),
		success: function(data)
			{
				$('#programmeDetailsSection').html(data);
			
				var errorCount = $('#programmeDetailsSection .alert-error:visible').length;
				if (message == 'close' && errorCount == 0)
				{
					// Close the section only if there are no errors.
					$('#programme-H2').trigger('click');
				}
				if(errorCount >0){
					
					markSectionError('#programmeDetailsSection');
					
				}
			},
    complete: function()
    {
      $('#programmeDetailsSection div.ajax').remove();
    }
	});
}