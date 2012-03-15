$(document).ready(function(){
	
	$("#awareSupervisorCB").attr('checked', false);
	$("#primarySupervisorCB").attr('checked', false);
	$("#primarySupervisor").val("NO");
	$("#awareSupervisor").val("NO");
	
	$("#supervisor_div").on("click", "a", function(){	
		$(this).parent("span").remove();
		
	});
	
	$("input[name*='awareSupervisorCB']").click(function() {
		if ($("#awareSupervisor").val() =='YES'){
			$("#awareSupervisor").val("NO");
		} else {		
			$("#awareSupervisor").val("YES");
		}

	});
	
	$("input[name*='primarySupervisorCB']").click(function() {
		if ($("#primarySupervisor").val() =='YES'){
			$("#primarySupervisor").val("NO");
		} else {		
			$("#primarySupervisor").val("YES");
		}

	});
	
	$('#programmeSaveButton').on("click",function(){
		var postData = {
			programmeName: $("#programmeName").val(),
			projectName: $("#projectName").val(), 
			studyOption: $("#studyOption").val(), 
			startDate: $("#startDate").val(),
			referrer: $("#referrer").val(),
			application: $("#appId1").val(),
			programmeDetailsId: $("#programmeDetailsId").val(),
			supervisors: ""
		}
		
		$.post( "/pgadmissions/programme" ,$.param(postData) +"&" + $('[input[name="supervisors"]').serialize(),
		function(data) {
			$('#programmeDetailsSection').html(data);
		});
	});
	
	$('#addSupervisorButton').on('click', function(){
		if($('#supervisorEmail').val() =="Email adderss" || $('#supervisorEmail').val().trim()== ''){
			alert("Please enter an email address");
			return;
		}
		$('#supervisor_div').append('<span name="supervisor_span">'+ 
				$('#supervisorEmail').val()+ ", Primary:" + $('#primarySupervisor').val() +", Aware:" + $('#awareSupervisor').val() +'<a class="button">delete</a>'+
				'<input type="hidden" name="supervisors" value=' +"'" + '{"email":"' +  $('#supervisorEmail').val()+ '", "primarySupervisor":"' + $('#primarySupervisor').val() + '", "awareSupervisor":"' + $('#awareSupervisor').val()+ '"} ' + "'" + "/>"									
				+'<br/></span>');
	})
	
	$('#programmeCloseButton').click(function(){
		$('#programme-H2').trigger('click');
		return false;
	});
	
});