$(document).ready(function()
{
	$("#acceptTermsPDValue").val("NO");
	$("#addSupervisorButton").show();
	$("#awareSupervisorCB").attr('checked', false);
	$("#awareSupervisor").val("NO");
	var unsavedSupervisors = 0;
	
	var progImgCount = 0;
	
	$('#supervisors').toggle($("#supervisors tbody").children().length > 0);
	
	$("input[name*='acceptTermsPDCB']").click(function() {
		if ($("#acceptTermsPDValue").val() =='YES'){
			$("#acceptTermsPDValue").val("NO");
		} else {	
			$("#acceptTermsPDValue").val("YES");
			
			/*
			$(".terms-box").attr('style','');
			$("#prog-info-bar-div").switchClass("section-error-bar", "section-info-bar", 1);
			$("#prog-info-bar-span").switchClass("invalid-info-text", "info-text", 1);
			$("#prog-info-bar-div .row span.error-hint").remove();
			*/
			progImgCount = 0;
			
			$.post("/pgadmissions/acceptTerms", {  
				applicationId: $("#applicationId").val(), 
				acceptedTerms: $("#acceptTermsPDValue").val()
			},
			function(data) {
			});
		}
		});
	
	// Delete supervisor
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
		
		if(rowCount == 0){
			$("#supervisors").hide();
		}
	});
	
	// Edit supervisor
	$("#supervisor_div").on("click", "a[name=\"editSupervisorLink\"]", function(){
		var id = this.id;
		if(id.indexOf("us_") != -1){
			id = id.replace('us_', '');
			$("#supervisorFirstname").val($('#us_'+id+"firstname").val());
			$("#supervisorLastname").val($('#us_'+id+"lastname").val());
			$("#supervisorEmail").val($('#us_'+id+"email").val());
			if ($('#us_'+id+'aware').val() =='YES'){
				$("#awareSupervisorCB").attr('checked', true);
				$("#awareSupervisor").val("YES");
			} else {
				$("#awareSupervisorCB").attr('checked', false);
				$("#awareSupervisor").val("NO");
 			}
			$('#'+id+"_ussupervisors").val('');
		}
		else{
			id = id.replace('supervisor_', '');
			$("#supervisorId").val(id);
			$('#'+id+"_supervisors").val('');
			$("#supervisorFirstname").val($('#'+id+"_firstname").val());
			$("#supervisorLastname").val($('#'+id+"_lastname").val());
			$("#supervisorEmail").val($('#'+id+"_email").val());
			if ($('#'+id+'_aware').val() =='YES'){
				$("#awareSupervisorCB").attr('checked', true);
				$("#awareSupervisor").val("YES");
			} else {
				$("#awareSupervisorCB").attr('checked', false);
				$("#awareSupervisor").val("NO");
			}
			$('#'+id+"_supervisors").val('');
		}
			$("#addSupervisorButton").hide();
			$("#updateSupervisorButton").show();
			$(this).parent("span").remove();
			$(this).parent().parent().remove();
	});
	
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
	
	$("input[name*='awareSupervisorCB']").click(function() {
		if ($("#awareSupervisor").val() =='YES'){
			$("#awareSupervisor").val("NO");
		} else {		
			$("#awareSupervisor").val("YES");
		}
	});
	
	
	// Save programme details.
	$('#programmeSaveButton').on("click",function()
	{
		if ($("#acceptTermsPDValue").val() == 'NO')
		{ 
			$(this).parent().parent().find('.terms-box').css({borderColor: 'red', color: 'red'});
				
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
				$('#addSupervisorButton').trigger('click');
				// If there was an error submitting the non-empty form, don't continue.
				if ($('#supervisor_div .invalid').length > 0)
				{
					return false;
				}
			}
			
			postProgrammeData('close');
		}
	});	
	function validateEmail(email) { 
		var pattern = new RegExp(/^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?$/i);
	    var result = pattern.test(email);
		return result;
	} 
	
	// Add supervisor button
	$('#addSupervisorButton').on('click', function()
	{
		var errors = 0;
		
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
			$("#supervisor_div span.invalid").html('').hide();
			var aware = ($('#awareSupervisor').val() =="YES") ? 'Yes' : 'No';

			unsavedSupervisors++;
			$('table#supervisors tbody').append(
				'<tr class="' + (aware == "Yes" ? "aware" : "unaware") + '">' +
				'<td data-desc="Supervisor ' + (aware == "Yes" ? "aware" : "unaware") + ' of application">' + $('#supervisorFirstname').val() + ' '+ $('#supervisorLastname').val() + ' (' + $('#supervisorEmail').val() + ')</td>' +
				'<td>' +
				'<a class=\"button-delete\" id="usd_'+unsavedSupervisors+'" name=\"deleteSupervisor\">delete</a> ' +
				'<a class=\"button-edit\" id="us_'+unsavedSupervisors+'" name=\"editSupervisorLink\">edit</a>' +
				'<input type="hidden" id="us_'+unsavedSupervisors+'firstname" value="' + $('#supervisorFirstname').val()+'"/>'	+								
				'<input type="hidden" id="us_'+unsavedSupervisors+'lastname" value="' + $('#supervisorLastname').val()+'"/>'	+								
				'<input type="hidden" id="us_'+unsavedSupervisors+'email" value="' + $('#supervisorEmail').val()+'"/>'	+								
				'<input type="hidden" id="us_'+unsavedSupervisors+'aware" value="' + $('#awareSupervisor').val()+'"/>'	+								
				'<input type="hidden" name="suggestedSupervisors" id="'+unsavedSupervisors+'_ussupervisors" value=' +"'" + '{"id":"' +  $('#supervisorId').val()+ '","firstname":"' +  $('#supervisorFirstname').val()+ '","lastname":"' +  $('#supervisorLastname').val()+ '","email":"' +  $('#supervisorEmail').val() +  '", "awareSupervisor":"' + $('#awareSupervisor').val() + '"} ' + "'" + "/>" +
				'</td>' +
				'</tr>');
			addToolTips();
		
			$("input[name='sFN']").val($('#supervisorFirstname').val());
			$("input[name='sLN']").val($('#supervisorLastname').val());
			$("input[name='sEM']").val($('#supervisorEmail').val());
			$("input[name='sAS']").val($('#awareSupervisor').val());
			$('#supervisorId, #supervisorFirstname, #supervisorLastname, #supervisorEmail').val('');
			$("#awareSupervisorCB").attr('checked', false);
			$("#awareSupervisor").val("NO");
		}
	});

	// Update supervisor.	
	$('#updateSupervisorButton').on('click', function()
	{
		var errors = 0;
		
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
			$("span[name='superFirstname']").html('');
			$("span[name='superFirstname']").hide();
			$("span[name='superLastname']").html('');
			$("span[name='superLastname']").hide();
			$("span[name='superEmail']").html('');
			$("span[name='superEmail']").hide();
			var aware = ($('#awareSupervisor').val() == "YES") ? 'Yes' : 'No';
			
			$('table#supervisors tbody').append(
				'<tr class="' + (aware == "Yes" ? "aware" : "unaware") + '">' +
				'<td data-desc="' + (aware == "Yes" ? "Aware" : "Unaware") + ' of application">' + $('#supervisorFirstname').val() + ' '+ $('#supervisorLastname').val() + ' (' + $('#supervisorEmail').val() + ')</td>' +
				'<td>' +
				'<a class=\"button-delete\" id="usd_'+unsavedSupervisors+'" name=\"deleteSupervisor\">delete</a> ' +
				'<a class=\"button-edit\" id="us_'+unsavedSupervisors+'" name=\"editSupervisorLink\">edit</a>' +
				'<input type="hidden" id="us_'+unsavedSupervisors+'firstname" value="' + $('#supervisorFirstname').val()+'"/>'	+								
				'<input type="hidden" id="us_'+unsavedSupervisors+'lastname" value="' + $('#supervisorLastname').val()+'"/>'	+								
				'<input type="hidden" id="us_'+unsavedSupervisors+'email" value="' + $('#supervisorEmail').val()+'"/>'	+								
				'<input type="hidden" id="us_'+unsavedSupervisors+'aware" value="' + $('#awareSupervisor').val()+'"/>'	+								
				'<input type="hidden" name="suggestedSupervisors" id="'+unsavedSupervisors+'_ussupervisors" value=' +"'" + '{"id":"' +  $('#supervisorId').val()+ '","firstname":"' +  $('#supervisorFirstname').val()+ '","lastname":"' +  $('#supervisorLastname').val()+ '","email":"' +  $('#supervisorEmail').val() +  '", "awareSupervisor":"' + $('#awareSupervisor').val() + '"} ' + "'" + "/>" +
				'</td>' +
				'</tr>');

      addToolTips();			
		
			$("input[name='sFN']").val($('#supervisorFirstname').val());
			$("input[name='sLN']").val($('#supervisorLastname').val());
			$("input[name='sEM']").val($('#supervisorEmail').val());
			$("input[name='sAS']").val($('#awareSupervisor').val());
			$('#supervisorId').val('');
			$('#supervisorFirstname').val('');
			$('#supervisorLastname').val('');
			$('#supervisorEmail').val('');

			// restore add button.
			$('#updateSupervisorButton').hide();
			$('#addSupervisorButton').show();
		}
		$("#awareSupervisorCB").attr('checked', false);
		$("#awareSupervisor").val("NO");
	});
	
	
	// Close Programme Details section.
	$('#programmeCloseButton').click(function()
	{
		$('#programme-H2').trigger('click');
		return false;
	});
	
	bindDatePicker('#startDate');
	addToolTips();

});


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
		
	$.post(
		"/pgadmissions/update/editProgrammeDetails",
		$.param(postData) + "&" + $('[input[name="suggestedSupervisors"]').serialize(),
		function(data) {
			$('#programmeDetailsSection').html(data);
			markSectionError('#programmeDetailsSection');
		});
}