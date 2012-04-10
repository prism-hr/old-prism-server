$(document).ready(function(){
	
//	$("span[class='supervisorAction']").html('<a id="addSupervisorButton" class="button" style="width: 110px;">Add Supervisor</a>');
	
	$("#awareSupervisorCB").attr('checked', false);
	$("#primarySupervisorCB").attr('checked', false);
	$("#primarySupervisor").val("NO");
	$("#awareSupervisor").val("NO");
	
	
	$('a[name="deleteSupervisor"]').click( function(event){
		 event.preventDefault(); 
		 if(confirm("Are you sure you want to delete the supervisor?")){
//			 $(this).parent().parent().remove();
			 $(this).parent("supervisor_span").remove();
		}
	});
	$("#supervisor_div").on("click", "a", function(){	
		$(this).parent("span").remove();
		
	});
	
	
	
	
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
		
		var primarySupervisor =  $("input[name='primarySupervisor']:checked").val();
		if(primarySupervisor){
			postData.primarySupervisor = primarySupervisor;
		}
		
		$.post( "/pgadmissions/programme" ,$.param(postData) +"&" + $('[input[name="supervisors"]').serialize(),
		function(data) {
			$('#programmeDetailsSection').html(data);
		});
	});
	
	$('#addSupervisorButton').on('click', function(){
		if( $('#supervisorEmail').val() && $('#supervisorEmail').val() !="Email address" ){
			
		
			$('#supervisor_div').append('<span name="supervisor_span">'+ 
					"<tr><td>" + escape($('#supervisorFirstname').val())+" "+ escape($('#supervisorLastname').val())+"</td><td>"+
					escape($('#supervisorEmail').val())+ "</td><td><td> <input type=\"radio\" name=\"primarySupervisor\" value=\""+$('#supervisorId').val()+"\"/> </td><td>" + escape($('#awareSupervisor').val()) +'</td><td><a class="button-delete">delete</a>  <a class="button-edit">edit</a></td></tr>'+
					'<input type="hidden" name="supervisors" value=' +"'" + '{"firstname":"' +  escape($('#supervisorFirstname').val())+ '","lastname":"' +  escape($('#supervisorLastname').val())+ '","email":"' +  escape($('#supervisorEmail').val()) +  '", "awareSupervisor":"' + escape($('#awareSupervisor').val()) + '"} ' + "'" + "/>"									
					+'<br/></span>');
			$('#supervisorId').val('');
			$('#supervisorFirstname').val('');
			$('#supervisorLastname').val('');
			$('#supervisorEmail').val('');
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