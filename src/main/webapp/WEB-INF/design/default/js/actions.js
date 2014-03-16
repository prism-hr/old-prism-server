$(document).ready(function() {
    $('#programme-details .selectpicker').selectpicker();
    $(document).on('change', 'select.actionType', function() {
    	
        var name = this.name;
        var id = name.substring(5).replace(']','');
        
        var skip = false;
        
        switch ($(this).val()) {
        case 'emailApplicant':
        	$('#ajaxloader').fadeOut('fast');
        	$(this).val($("select.actionType option:first").val());
        	var subject = "Question Regarding UCL Prism Application " + $(this).attr('data-applicationnumber');
        	var email = $(this).attr('data-email');
        	window.location.href ="mailto:" + email + "?subject=" + subject;
        	skip = true;
        	break;
        }
    	
        if (skip) {
        	return;
        }
        
		$('#ajaxloader').show();
            switch ($(this).val()) {
            case 'VIEW':
            case 'VIEW_EDIT':
            case 'CORRECT_APPLICATION':
                window.location.href = "/pgadmissions/application?view=view&applicationId="+ id;
                break;
            case 'COMMENT':
                window.location.href = "/pgadmissions/comment?applicationId="+ id;
                break;
            case 'PROVIDE_REFERENCE':
                window.location.href = "/pgadmissions/referee/addReferences?applicationId="+ id;
                break;
            case 'ASSIGN_REVIEWERS':
            case 'ASSIGN_INTERVIEWERS':
            case 'ASSIGN_SUPERVISORS':
            case 'COMPLETE_VALIDATION_STAGE':
            case 'COMPLETE_REVIEW_STAGE':
            case 'COMPLETE_INTERVIEW_STAGE':
            case 'COMPLETE_APPROVAL_STAGE':
                window.location.href = "/pgadmissions/progress/getPage?applicationId="+ id;
                break;
            case 'MOVE_TO_DIFFERENT_STAGE':
                window.location.href = "/pgadmissions/progress/getPage?applicationId="+ id + "&action=abort";
                break;
            case 'PROVIDE_REVIEW':
                window.location.href = "/pgadmissions/reviewFeedback?applicationId="+ id;
                break;
            case 'PROVIDE_INTERVIEW_FEEDBACK':
                window.location.href = "/pgadmissions/interviewFeedback?applicationId="+ id;
                break;
            case 'PROVIDE_INTERVIEW_AVAILABILITY':
            	window.location.href = "/pgadmissions/interviewVote?applicationId="+ id;
            	break;
            case 'CONFIRM_INTERVIEW_ARRANGEMENTS':
            	window.location.href = "/pgadmissions/interviewConfirm?applicationId="+ id;
            	break;
            case 'CONFIRM_PRIMARY_SUPERVISION':
                window.location.href = "/pgadmissions/confirmSupervision?applicationId="+ id;
                break;
            case 'CONFIRM_ELIGIBILITY':
            	window.location.href = "/pgadmissions/admitter/confirmEligibility?applicationId=" + id;
            	break;
            case 'CONFIRM_REJECTION':
            	window.location.href = "/pgadmissions/rejectApplication?applicationId=" + id;
            	break;
            case 'CONFIRM_OFFER_RECOMMENDATION':
            	window.location.href = "/pgadmissions/offerRecommendation?applicationId=" + id;
            	break;
            case 'WITHDRAW':
                var message = 'Are you sure you want to withdraw the application? <b>You will not be able to submit a withdrawn application.</b>';
                var onOk = function() {
                    $.ajax({
                        type : 'POST',
                        statusCode : {
                            401 : function() {window.location.reload();},
                            500 : function() {window.location.href = "/pgadmissions/error";},
                            404 : function() {window.location.href = "/pgadmissions/404";},
                            400 : function() {window.location.href = "/pgadmissions/400";},
                            403 : function() {window.location.href = "/pgadmissions/404";}
                        },
                        url : "/pgadmissions/withdraw",
                        data : {
                            applicationId : id
                        },
                        success : function(data) {
                            window.location.href = "/pgadmissions/applications?messageCode=application.withdrawn&application="+ id;
                        },
                        complete : function() {
                            $('#ajaxloader').fadeOut('fast');
                        }
                    });    
                };
                var onCancel = function() {
                	$('.actionType').val('Actions');
					$('#ajaxloader').fadeOut('fast');
                };
                modalPrompt(message, onOk, onCancel);
                break;
            }
        });
});