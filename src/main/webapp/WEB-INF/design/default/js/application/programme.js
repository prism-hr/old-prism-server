$(document).ready(function(){
	
	$("#awareSupervisorCB").attr('checked', false);
	$("#primarySupervisorCB").attr('checked', false);
	$("#primarySupervisor").val("NO");
	$("#awareSupervisor").val("NO");
	
	$("#supervisor_div").on("click", "a", function(){	
		$(this).parent("span").remove();
		
	});
	
	$('#programmeCancelButton').click(function(){
		$("span[class='invalid']").each(function(){
			$(this).html("");
		});
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
		if( $('#supervisorEmail').val() && $('#supervisorEmail').val() !="Email address" ){
			
		
			$('#supervisor_div').append('<span name="supervisor_span">'+ 
					"Name: " + escape($('#supervisorFirstname').val())+" "+ escape($('#supervisorLastname').val())+", Email: "+
					escape($('#supervisorEmail').val())+ ", Primary:" + escape($('#primarySupervisor').val()) +", Is supervisor aware of your application:" + escape($('#awareSupervisor').val()) +'<a class="button-delete">delete</a>'+
					'<input type="hidden" name="supervisors" value=' +"'" + '{"firstname":"' +  escape($('#supervisorFirstname').val())+ '","lastname":"' +  escape($('#supervisorLastname').val())+ '","email":"' +  escape($('#supervisorEmail').val()) + '", "primarySupervisor":"' + escape($('#primarySupervisor').val()) + '", "awareSupervisor":"' + escape($('#awareSupervisor').val()) + '"} ' + "'" + "/>"									
					+'<br/></span>');
			$('#supervisorFirstname').val('')
			$('#supervisorLastname').val('')
			$('#supervisorEmail').val('')
		}
	})
	
	$('#programmeCloseButton').click(function(){
		$('#programme-H2').trigger('click');
		return false;
	});
	
	
	  bindDatePickers();

		//open/close
		var $header  =$('#programme-H2');
		var $content = $header.next('div');
		$header.bind('click', function()
		{
		  $content.toggle();
		  $(this).toggleClass('open', $content.is(':visible'));
		  return false;
		});
		
});