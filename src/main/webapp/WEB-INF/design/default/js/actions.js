
$(document).ready(function()
{
	// --------------------------------------------------------------------------------
	// APPLICATION ACTIONS
	// --------------------------------------------------------------------------------
	$(document).on('change', 'select.actionType', function()
	{
		
		// Display a loading prompt, even though what follows isn't AJAX.
		$('div.content-box-inner').css({ position: 'relative' })
						 									.append('<div class="ajax" />');

		var name = this.name;
		var id = name.substring(5).replace(']', '');

		switch ($(this).val())
		{
			case 'view':
				window.location.href = "/pgadmissions/application?view=view&applicationId=" + id;
				break;
			case 'assignReviewer':
				window.location.href = "/pgadmissions/review/assignReviewers?applicationId=" + id;
				break;
			case 'assignInterviewer':
				window.location.href = "/pgadmissions/interview/assignInterviewers?applicationId=" + id;
				break;
			case 'comment':
				window.location.href = "/pgadmissions/comment?applicationId=" + id;
				break;
			case 'reference':
				window.location.href = "/pgadmissions/referee/addReferences?applicationId=" + id;
				break;
			case 'validate':
				window.location.href = "/pgadmissions/progress/getPage?applicationId=" + id;
				break;
			case 'review':
				window.location.href = "/pgadmissions/reviewFeedback?applicationId=" + id;
				break;
			case 'interviewFeedback':
				window.location.href = "/pgadmissions/interviewFeedback?applicationId=" + id;
				break;
			case 'restartApproval':
				window.location.href = "/pgadmissions/approval/moveToApproval?applicationId=" + id;
				break;
			case 'progress':
				window.location.href = "/pgadmissions/viewprogress?applicationId=" + id;
				break;
			case 'withdraw':
				
				var message  = 'Are you sure you want to withdraw the application? <b>You will not be able to submit a withdrawn application.</b>';
				var onOk     = function()
				{
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
						url: "/pgadmissions/withdraw",
						data: {
							applicationId: id
						}, 
						success: function(data)
						{
							window.location.href = "/pgadmissions/applications?messageCode=application.withdrawn&application=" + id;
						},
						complete: function()
						{
							$('div.content-box-inner div.ajax').remove();
						}
					});
			
				};
				var onCancel = function()
				{
					$('div.content-box-inner div.ajax').remove();
				};
				
				modalPrompt(message, onOk, onCancel);
				break;
		}
	});
});