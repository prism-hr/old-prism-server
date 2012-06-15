// ===============================================================================
// PROGRAMME DETAILS section
// ===============================================================================

$(document).ready(function()
{
	
	if ($("#startDate").val() === ""){
		var today = new Date();
		var mm = today.getMonth(); 
		var yyyy = today.getFullYear();
		var year;
//		mm=10; uncomment to test 
		if(mm >= 9){
			year = yyyy +1;
		}else{
			year = yyyy;
		}
		var firstDayInNextYearsOctober = new Date();
		firstDayInNextYearsOctober.setFullYear(year, 9, 1);
		 while (firstDayInNextYearsOctober.getDay() !== 1) { //while not monday
			 	firstDayInNextYearsOctober.setDate(firstDayInNextYearsOctober.getDate() + 1);
		 }
		 var formattedDate = firstDayInNextYearsOctober.getDate() + "-" + firstDayInNextYearsOctober.getMonth()+1 + "-" + firstDayInNextYearsOctober.getFullYear();
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
			
			// Remove any styling from the terms box.
			//$('#programmeDetailsSection .terms-box').attr('style','');
			/*
			$("#prog-info-bar-div").switchClass("section-error-bar", "section-info-bar", 1);
			$("#prog-info-bar-span").switchClass("invalid-info-text", "info-text", 1);
			$("#prog-info-bar-div .row span.error-hint").remove();
			*/

			progImgCount = 0;
			
			$.post("/pgadmissions/acceptTerms", {  
				applicationId: $("#applicationId").val(), 
				acceptedTerms: $("#acceptTermsPDValue").val()
			},
			function(data) {}
			);
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
	});
	
	
	// -------------------------------------------------------------------------------
	// Add supervisor button.
	// -------------------------------------------------------------------------------
	$('#addSupervisorButton').on('click', function()
	{
		// Hide error messages.
		var errors = 0;
		$("#supervisor_div span.invalid").html('').hide();
		
		if ($('#supervisorFirstname').val() == "")
		{
			$("span[name='superFirstname']").html('First name cannot be empty.').show();
			errors++;
		}		
		if ($('#supervisorLastname').val() == "")
		{
			$("span[name='superLastname']").html('Last name cannot be empty.').show();
			errors++;
		}		
		if (!validateEmail($('#supervisorEmail').val()))
		{
			$("span[name='superEmail']").html('Email is not valid.').show();
			errors++;
		}
		
		if (errors == 0)
		{
			//replaceWithLoader($(this));
			$("#supervisors").show();
			var isAware = $("input[name='awareSupervisor']:checked").val(); 
			var awareState = (isAware == "YES" ? "aware" : "unaware");

			unsavedSupervisors++;
			$('table#supervisors tbody').append(
				'<tr class="' + awareState + '" rel="'+ unsavedSupervisors +'">' +
				'<td data-desc="Supervisor ' + awareState + ' of application">' + $('#supervisorFirstname').val() + ' '+ $('#supervisorLastname').val() + ' (' + $('#supervisorEmail').val() + ')</td>' +
				'<td>' +
				'<a class=\"button-edit\" id="us_'+unsavedSupervisors+'" name=\"editSupervisorLink\">edit</a>' +
				'<a class=\"button-delete\" id="usd_'+unsavedSupervisors+'" name=\"deleteSupervisor\">delete</a> ' +
				'<input type="hidden" name="sId" id="us_'+unsavedSupervisors+'_supervisorId" value="'+unsavedSupervisors+'" />' +
				'<input type="hidden" name="sFN" id="us_'+unsavedSupervisors+'firstname" value="' + $('#supervisorFirstname').val()+'"/>'	+								
				'<input type="hidden" name="sLN" id="us_'+unsavedSupervisors+'lastname" value="' + $('#supervisorLastname').val()+'"/>'	+								
				'<input type="hidden" name="sEM" id="us_'+unsavedSupervisors+'email" value="' + $('#supervisorEmail').val()+'"/>'	+								
				'<input type="hidden" name="sAS" id="us_'+unsavedSupervisors+'aware" value="' + isAware+'"/>'	+								
				'<input type="hidden" name="suggestedSupervisors" id="'+unsavedSupervisors+'_ussupervisors" value=' +"'" + '{"id":"' +  $('#supervisorId').val()+ '","firstname":"' +  $('#supervisorFirstname').val()+ '","lastname":"' +  $('#supervisorLastname').val()+ '","email":"' +  $('#supervisorEmail').val() +  '", "awareSupervisor":"' + isAware + '"} ' + "'" + "/>" +
				'</td>' +
				'</tr>');
			addToolTips();
		
			$('#supervisorId, #supervisorFirstname, #supervisorLastname, #supervisorEmail').val('');
			$("input[name='awareSupervisor']").val(["NO"]);
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
		
		//$('input[name="suggestedSupervisors"]', $edit_row).val('');
		
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
			$("span[name='superFirstname']").html('First name cannot be empty.').show();
			errors++;
		}		
		if ($('#supervisorLastname').val() == "")
		{
			$("span[name='superLastname']").html('Last name cannot be empty.').show();
			errors++;
		}		
		if (!validateEmail($('#supervisorEmail').val()))
		{
			$("span[name='superEmail']").html('Email is not valid.').show();
			errors++;
		}
		
		if (errors == 0)
		{
			//replaceWithLoader($(this));
			
			/*
			$("span[name='superFirstname']").html('');
			$("span[name='superFirstname']").hide();
			$("span[name='superLastname']").html('');
			$("span[name='superLastname']").hide();
			$("span[name='superEmail']").html('');
			$("span[name='superEmail']").hide();
			*/
			var isAware = $("input[name='awareSupervisor']:checked").val(); 
			var awareState = (isAware == "YES" ? "aware" : "unaware");
			
			$('table#supervisors tbody tr[rel="'+ currentRel +'"]').replaceWith(
					'<tr class="' + awareState + '" rel="'+ currentRel +'">' +
					'<td data-desc="' + awareState + ' of application">' + $('#supervisorFirstname').val() + ' '+ $('#supervisorLastname').val() + ' (' + $('#supervisorEmail').val() + ')</td>' +
					'<td>' +
					'<a class=\"button-edit\" id="us_'+currentRel+'" name=\"editSupervisorLink\">edit</a>' +
					'<a class=\"button-delete\" id="usd_'+currentRel+'" name=\"deleteSupervisor\">delete</a> ' +
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
		
			/*
			$("input[name='sFN']").val($('#supervisorFirstname').val());
			$("input[name='sLN']").val($('#supervisorLastname').val());
			$("input[name='sEM']").val($('#supervisorEmail').val());
			$("input[name='sAS']").val($('#awareSupervisor').val());
			*/

			$('#supervisorId').val('');
			$('#supervisorFirstname').val('');
			$('#supervisorLastname').val('');
			$('#supervisorEmail').val('');

			// restore add button.
			$('#updateSupervisorButton').hide();
			$('#addSupervisorButton').show();
		}
		$("input[name='awareSupervisor']").val(["NO"]);
	});


	// -------------------------------------------------------------------------------
	// Save programme details.
	// -------------------------------------------------------------------------------
	$('#programmeSaveButton').on("click",function()
	{
		if ($("#acceptTermsPDValue").val() == 'NO')
		{ 
			$('#programmeDetailsSection .terms-box').css({borderColor: 'red', color: 'red'});
				
			$("#prog-info-bar-div").switchClass("section-info-bar", "section-error-bar", 1);
			$("#prog-info-bar-span").switchClass("info-text", "invalid-info-text", 1);
				
			if (progImgCount == 0)
			{
				$("#prog-info-bar-div .row").prepend('<span class=\"error-hint\" data-desc=\"Please provide all mandatory fields in this section.\"></span>');
				progImgCount = progImgCount + 1;
			}
			addToolTips();
		}
		else
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
		$.get("/pgadmissions/update/getProgrammeDetails",
				{
					applicationId:  $('#applicationId').val(),					
					cacheBreaker: new Date().getTime()					
				},
				function(data) {
					$('#programmeDetailsSection').html(data);
				}
		);
	});
	

	bindDatePicker('#startDate');
	addToolTips();
});


// -------------------------------------------------------------------------------
// Post programme details.
// -------------------------------------------------------------------------------
function postProgrammeData(message)
{
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
			message: message
		};

	$('#programmeDetailsSection > div').append('<div class="ajax" />');
		
	$.post(
		"/pgadmissions/update/editProgrammeDetails",
		$.param(postData) + "&" + $('[input[name="suggestedSupervisors"]').serialize(),
		function(data)
		{
			$('#programmeDetailsSection').html(data);
			$('#programmeDetailsSection div.ajax').remove();
			markSectionError('#programmeDetailsSection');
			
			if (message == 'close')
			{
				// Close the section only if there are no errors.
				var errorCount = $('#programmeDetailsSection .invalid:visible').length;
				if (errorCount == 0)
				{
					$('#programme-H2').trigger('click');
				}
			}
		});
}