$(document).ready(function(){
	
//	$("span[class='supervisorAction']").html('<a id="addSupervisorButton" class="button" style="width: 110px;">Add Supervisor</a>');
	
	$("#awareSupervisorCB").attr('checked', false);
	$("#primarySupervisorCB").attr('checked', false);
	$("#primarySupervisor").val("NO");
	$("#awareSupervisor").val("NO");
	
	
	$('a[name="deleteSupervisor"]').click( function(event){
		 event.preventDefault(); 
		 if(confirm("Are you sure you want to delete the supervisor?")){
			 $(this).parent().parent().remove();
		}
	});
//	$("#supervisor_div").on("click", "a", function(){	
//		$(this).parent("span").remove();
//		
//	});
	
	
	
	
	$('a[name="editSupervisorLink"]').click(function(){
//		$("span[class='supervisorAction']").html('');
//		$("span[class='supervisorAction']").html('<a id="updateSupervisorButton" class="button" style="width: 110px;">Update Supervisor</a>');
		var id = this.id;
		id = id.replace('supervisor_', '');
		$("#supervisorId").val(id);
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
	});
	
	
	
	$('#programmeCancelButton').click(function(){
//		$("span[class='supervisorAction']").html('');
//		$("span[class='supervisorAction']").html('<a id="addSupervisorButton" class="button" style="width: 110px;">Add Supervisor</a>');
		$("span[class='invalid']").each(function(){
			$(this).hide();
		});
	});
	
	$("input[name*='awareSupervisorCB']").click(function() {
		if ($("#awareSupervisor").val() =='YES'){
			$("#awareSupervisor").val("NO");
		} else {		
			$("#awareSupervisor").val("YES");
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
		};
		
		$.post( "/pgadmissions/programme" ,$.param(postData) +"&" + $('[input[name="supervisors"]').serialize(),
		function(data) {
			$('#programmeDetailsSection').html(data);
		});
	});
	
	$('#addSupervisorButton').on('click', function(){
		if( $('#supervisorEmail').val() && $('#supervisorEmail').val() !="Email address" ){
			
		
		$('#supervisor_div').append('<span name="supervisor_span">'+ 
					"<tr><td>" + escape($('#supervisorFirstname').val())+" "+ escape($('#supervisorLastname').val())+"</td><td>"+
					escape($('#supervisorEmail').val())+ "</td><td><td> <input type=\"radio\" name=\"primarySupervisor\" value=\""+$('#supervisorId').val()+"\"/> </td><td>" + escape($('#awareSupervisor').val()) +'</td><td><a class="button-delete"  name="deleteSupervisor">delete</a>  <a class="button-edit" name ="editUnsavedSupervisor">edit</a></td></tr>'+
					'<input type="hidden" name="supervisors" value=' +"'" + '{"id":"' +  escape($('#supervisorId').val())+ '","firstname":"' +  escape($('#supervisorFirstname').val())+ '","lastname":"' +  escape($('#supervisorLastname').val())+ '","email":"' +  escape($('#supervisorEmail').val()) +  '", "awareSupervisor":"' + escape($('#awareSupervisor').val()) + '"} ' + "'" + "/>"									
					+'<br/></span>');
		 $("input[name='sFN']").val($('#supervisorFirstname').val());
		 $("input[name='sLN']").val($('#supervisorLastname').val());
		 $("input[name='sEM']").val($('#supervisorEmail').val());
		 $("input[name='sAS']").val($('#awareSupervisor').val());
		 $('#supervisorId').val('');
		 $('#supervisorFirstname').val('');
		 $('#supervisorLastname').val('');
		 $('#supervisorEmail').val('');
		}
	});
	
	$('a[name="editUnsavedSupervisor"]').click(function(){
//		$("span[class='supervisorAction']").html('');
//		$("span[class='supervisorAction']").html('<a id="updateSupervisorButton" class="button" style="width: 110px;">Update Supervisor</a>');
		$("#supervisorFirstname").val($("input[name='sFN']").val());
		$("#supervisorLastname").val($("input[name='sLN']").val());
		$("#supervisorEmail").val($("input[name='sEM']").val());
		if ($("input[name='sEM']").val() =='YES'){
			$("#awareSupervisorCB").attr('checked', true);
			$("#awareSupervisor").val("YES");
		} else {
			$("#awareSupervisorCB").attr('checked', false);
			$("#awareSupervisor").val("NO");
		}
	});
	
	$('#updateSupervisorButton').on('click', function(){
		$.post("/pgadmissions/programme/updateSupervisor",
				{ 
					firstname: $("#supervisorFirstname").val(),
					lastname: $("#supervisorLastname").val(), 
					email: $("#supervisorEmail").val(), 
					primarySupervisor: $("#primarySupervisor").val(), 
					programmeDetailsId: $("#programmeDetailsId").val(),
					supervisorId: $("#supervisorId").val(),
					awareSupervisor: $("#awareSupervisor").val() 
				},
				function(data) {
					$('#programmeDetailsSection').html(data);
				});
	});
	
	$('#programmeCloseButton').click(function(){
		$('#programme-H2').trigger('click');
		return false;
	});
	
	
	  bindDatePicker('#startDate');

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