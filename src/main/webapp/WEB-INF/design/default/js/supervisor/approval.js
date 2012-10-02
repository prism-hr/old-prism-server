$(document).ready(function()
{
	getSupervisorsSection();
	getCreateSupervisorsSection();
	
	// -----------------------------------------------------------------------------------------
	// Add supervisor
	// -----------------------------------------------------------------------------------------
	$('#assignSupervisorsToAppSection').on('click', '#addSupervisorBtn', function(){	
		var selectedSupervisors = $('#programSupervisors').val();
		if (selectedSupervisors)
		{
			
			$('#programSupervisors').each(function(index)
			{
				var id = $(this).attr("value");
				
				var $option = $("#programSupervisors option[value='" + id + "']");
	
				if (!$option.hasClass('selected'))
				{
					var selText = $option.text();
					var category = $option.attr("category");
					$("#programSupervisors option[value='" + id + "']").addClass('selected')
																													 .removeAttr('selected')
																													 .attr('disabled', 'disabled');
					$("#applicationSupervisors").append('<option value="'+ id +'" category="' + category + '">'+ selText + '</option>');
				}
			});
			$('#applicationSupervisors').attr("size", $('#applicationSupervisors option').size() + 1);
			 resetSupervisorsErrors();
		}
	});
	
	
	// -----------------------------------------------------------------------------------------
	// Remove supervisor
	// -----------------------------------------------------------------------------------------
	$('#assignSupervisorsToAppSection').on('click', '#removeSupervisorBtn', function(){
	
		var selectedSupervisors = $('#applicationSupervisors').val();
		if (selectedSupervisors)
		{
			$('#applicationSupervisors').each(function(index)
			{
				var id = $(this).attr("value");
				var selText = $("#applicationSupervisors option[value='" + id + "']").text();
				$("#applicationSupervisors option[value='" + id + "']").remove();
				$("#programSupervisors option[value='" + id + "']").removeClass('selected')
																												 .removeAttr('disabled');
			});
			$('#applicationSupervisors').attr("size", $('#applicationSupervisors option').size() + 1);
			resetSupervisorsErrors();
		}
	});


	// -----------------------------------------------------------------------------------------
	// Create a new supervisor
	// -----------------------------------------------------------------------------------------

	$('#createsupervisorsection').on('click','#createSupervisor', function()
	{
		$('#createsupervisorsection').append('<div class="ajax" />');
		var postData = {
			applicationId : $('#applicationId').val(),
			firstName: $('#newSupervisorFirstName').val(),
			lastName: $('#newSupervisorLastName').val(),
			email: $('#newSupervisorEmail').val()				
		}
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
			url:"/pgadmissions/approval/createSupervisor",
			data:	$.param(postData),
			success: function(data)
			{	
				var newSuperviosr;
				try{
					newSuperviosr = jQuery.parseJSON(data);	
				}catch(err){
					
					$('#createsupervisorsection').html(data);
					addToolTips();
					return;
				}
				if(newSuperviosr.isNew){
					$('#previous').append('<option value="' + $('#applicationId').val() + '|' + newSuperviosr.id + '" category="previous" disabled="disabled">' +
							newSuperviosr.firstname + ' ' + newSuperviosr.lastname+ '</option>');
					$('#applicationSupervisors').append('<option value="' + $('#applicationId').val() + '|' + newSuperviosr.id + '">' +
							newSuperviosr.firstname + ' ' + newSuperviosr.lastname+ '</option>');
					$('#applicationSupervisors').attr("size", $('#applicationSupervisors option').size() + 1);
					
					
				}else{
					addExistingUserToSupervisorsLists(newSuperviosr);
				}
				resetSupervisorsErrors();
				getCreateSupervisorsSection();		
				
			},
		      complete: function()
		      {
				 $('#createsupervisorsection div.ajax').remove();
		      }
		});
		
	});

	

	$('#moveToApprovalBtn').click(function()
	{
		
		$('#approvalsection').append('<div class="ajax" />');
		var url = "/pgadmissions/approval/move";
	
		$('#applicationSupervisors option').each(function(){	
			$('#postApprovalData').append("<input name='supervisors' type='hidden' value='" +  $(this).val() + "'/>");
		});
		
		var postData = {
				applicationId : $('#applicationId').val(),
				supervisors: ''
		}
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
			url: url,
			data:	$.param(postData) + "&" + $('input[name="supervisors"]').serialize(),
			success: function(data)
			{	
				if(data == "OK"){					
					window.location.href = '/pgadmissions/applications?messageCode=move.approval&application=' + $('#applicationId').val();
				
				}else{
					$('#assignSupervisorsToAppSection').html(data);
					$('#postApprovalData').html('');
				}
				addToolTips();
			},
		      complete: function()
		      {
					$('#approvalsection div.ajax').remove();
		      }
		});
	});
		
		
	
});



function getSupervisorsSection(){
	$('#approvalsection').append('<div class="ajax" />');
	
	var url  = "/pgadmissions/approval/supervisors_section";

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
		url: url +"?applicationId=" + $('#applicationId').val(), 
		success: function(data)
		{
			$('#assignSupervisorsToAppSection').html(data);			
			addToolTips();			
		},
	    complete: function()
	    {
			$('#approvalsection div.ajax').remove();
	    }
	});
}


function getCreateSupervisorsSection(){
	$('#createsupervisorsection').append('<div class="ajax" />');
	
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
		url:"/pgadmissions/approval/create_supervisor_section?applicationId=" + $('#applicationId').val(), 
		success: function(data)
		{
			$('#createsupervisorsection div.ajax').remove();
			$('#createsupervisorsection').html(data);
		}
	});
}

function addExistingUserToSupervisorsLists(newSupervisor){
	
	if($('#applicationSupervisors option[value="' + $('#applicationId').val() + '|' + newSupervisor.id + '"]').length > 0){
		
		
		return;
	}
	

	if($('#default option[value="' + $('#applicationId').val() + '|' + newSupervisor.id + '"]').length > 0){		
		$('#default option[value="' + $('#applicationId').val() + '|' + newSupervisor.id + '"]').attr("selected", 'selected');		
		$('#addSupervisorBtn').trigger('click');
		
		return;
	}	

	if($('#previous option[value="' + $('#applicationId').val() + '|' + newSupervisor.id + '"]').length > 0){
		$('#previous option[value="' + $('#applicationId').val() + '|' + newSupervisor.id + '"]').attr("selected", 'selected');		
		$('#addSupervisorBtn').trigger('click');
		
		return;
	}
	
	$('#previous').append('<option value="' + $('#applicationId').val() + '|' + newSupervisor.id + '" category="previous" disabled="disabled">' +
			newSupervisor.firstname + ' ' + newSupervisor.lastname+ '</option>');
	$('#applicationSupervisors').append('<option value="' + $('#applicationId').val() + '|' + newSupervisor.id + '">' +
			newSupervisor.firstname + ' ' + newSupervisor.lastname+ '</option>');
	$('#applicationSupervisors').attr("size", $('#applicationSupervisors option').size() + 1);
	
}

function resetSupervisorsErrors(){
	if( $('#applicationSupervisors option').size() > 0){
		$('#supervisorsErrorSpan').remove();
	}
}