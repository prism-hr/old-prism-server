// ===============================================================================
// PROGRAMME DETAILS section
// ===============================================================================

$(document).ready(function()
{
	
	// -------------------------------------------------------------------------------
	// Get the date for the first Monday in the upcoming October.
	// -------------------------------------------------------------------------------
	if ($("#startDate").val() === "")
	{
		var today = new Date();
		var mm    = today.getMonth(); 
		var yyyy  = today.getFullYear();
		var year;
//		mm=10; uncomment to test 
		if (mm >= 9)
		{
			year = yyyy +1;
		}
		else
		{
			year = yyyy;
		}
		var firstDayInNextYearsOctober = new Date();
		firstDayInNextYearsOctober.setFullYear(year, 9, 1);
		while (firstDayInNextYearsOctober.getDay() !== 1) // while not monday
		{
			firstDayInNextYearsOctober.setDate(firstDayInNextYearsOctober.getDate() + 1);
		}
		var formattedDate = firstDayInNextYearsOctober.getDate() + "-Oct-" + firstDayInNextYearsOctober.getFullYear();
		$("#startDate").val(formattedDate);
	}
	
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
		
		console.log(rowCount);
		
		if (rowCount == 0)
		{
			$("#supervisors").hide();
		}

		// Remove validation errors.
		$('#supervisor_div span.invalid').remove();
		
		$("#supervisorId").val('');
		$("#supervisorFirstname").val('');
		$("#supervisorLastname").val('');
		$("#supervisorEmail").val('');
		$("input[name='awareSupervisor']").removeAttr('checked');
	});
	
	
	// -------------------------------------------------------------------------------
	// Add supervisor button.
	// -------------------------------------------------------------------------------
	$('#addSupervisorButton').on('click', function()
	{
		// Hide error messages.
		var errors = 0;
		$("#supervisor_div span.invalid").remove();
		
		if ($('#supervisorFirstname').val() == "")
		{
			$('#supervisorFirstname').after('<span class="invalid">You must make an entry.</span>').show();
			errors++;
		}		
		if ($('#supervisorLastname').val() == "")
		{
			$('#supervisorLastname').after('<span class="invalid">You must make an entry.</span>').show();
			errors++;
		}		
		if ($("input[name='awareSupervisor']:checked").val() == undefined)
		{
			$('#awareNo').parent().after('<span class="invalid">You must make a selection.</span>').show();
			errors++;
		}	
		if (!validateEmail($('#supervisorEmail').val()))
		{
			$('#supervisorEmail').after('<span class="invalid">You must enter a valid email address.</span>').show();
			errors++;
		}
		
		if (errors == 0)
		{
			$("#supervisors").show();
			var isAware = $("input[name='awareSupervisor']:checked").val(); 
			var awareState = (isAware == "YES" ? "aware" : "unaware");

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
				'<input type="hidden" name="suggestedSupervisors" id="'+unsavedSupervisors+'_ussupervisors" value=' +"'" + '{"id":"' +  $('#supervisorId').val()+ '","firstname":"' +  $('#supervisorFirstname').val()+ '","lastname":"' +  $('#supervisorLastname').val()+ '","email":"' +  $('#supervisorEmail').val() +  '", "awareSupervisor":"' + isAware + '"} ' + "'" + "/>" +
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
		$('#supervisor_div span.invalid').remove();
		
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
		$('table#supervisors span.invalid').html('').hide();
		
		$("#supervisors").show();
		if ($('#supervisorFirstname').val() == "")
		{
			$('#supervisorFirstname').after('<span class="invalid">You must make an entry.</span>').show();
			errors++;
		}		
		if ($('#supervisorLastname').val() == "")
		{
			$('#supervisorLastname').after('<span class="invalid">You must make an entry.</span>').show();
			errors++;
		}		
		if ($("input[name='awareSupervisor']:checked").val() == undefined)
		{
			$('#awareNo').parent().after('<span class="invalid">You must make a selection.</span>').show();
			errors++;
		}	
		if (!validateEmail($('#supervisorEmail').val()))
		{
			$('#supervisorEmail').after('<span class="invalid">You must enter a valid email address.</span>').show();
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
					'<input type="hidden" name="suggestedSupervisors" id="'+currentRel+'_ussupervisors" value=' +"'" + '{"id":"' +  $('#supervisorId').val()+ '","firstname":"' +  $('#supervisorFirstname').val()+ '","lastname":"' +  $('#supervisorLastname').val()+ '","email":"' +  $('#supervisorEmail').val() +  '", "awareSupervisor":"' + isAware + '"} ' + "'" + "/>" +
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
			if ($('#supervisor_div .invalid:visible').length > 0)
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
		var pattern = new RegExp(/^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?$/i);
		var result = pattern.test(email);
		return result;
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
		
	var postData = {
			programmeName: $("#programmeName").val(),
			projectName: $("#projectName").val(), 
			studyOption: $("#studyOption").val(), 
			startDate: $("#startDate").val(),
			referrer: $("#referrer").val(),
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
			  }
		  },
		url:"/pgadmissions/update/editProgrammeDetails",
		data:$.param(postData) + "&" + $('[input[name="suggestedSupervisors"]').serialize(),
		success: function(data)
			{
				$('#programmeDetailsSection').html(data);
			
				var errorCount = $('#programmeDetailsSection .invalid:visible').length;
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