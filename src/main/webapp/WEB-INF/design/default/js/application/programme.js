$(document).ready(function(){
	$("#addSupervisorButton").show();
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
//		if(confirm("Are you sure you want to delete the supervisor?")){
//			$(this).parent("span").remove();
//		}
	
//	});
	
	
	
	
	$('a[name="editSupervisorLink"]').click(function(){
		$("#addSupervisorButton").hide();
		$("#updateSupervisorButton").show();
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
		$("#addSupervisorButton").show();
		$("#updateSupervisorButton").hide();
		$.get("/pgadmissions/update/getProgrammeDetails",
				{
					applicationId:  $('#applicationId').val()					
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
	
	
	$('#programmeSaveButton').on("click",function(){
		postProgrammeData('close');
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
			
		
		$('table.data-table tbody').append('<span name="supervisor_span">'+ 
					"<tr><td>" + $('#supervisorFirstname').val()+" "+ $('#supervisorLastname').val()+"</td><td>"+
					$('#supervisorEmail').val()+ "</td><td>" + $('#awareSupervisor').val() +'</td><td><a class="button-delete"  name="deleteSupervisor">delete</a> </td></tr>'+
					'<input type="hidden" name="supervisors" value=' +"'" + '{"id":"' +  $('#supervisorId').val()+ '","firstname":"' +  $('#supervisorFirstname').val()+ '","lastname":"' +  $('#supervisorLastname').val()+ '","email":"' +  $('#supervisorEmail').val() +  '", "awareSupervisor":"' + $('#awareSupervisor').val() + '"} ' + "'" + "/>"									
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

function postProgrammeData(message){
	var postData = {
			programmeName: $("#programmeName").val(),
			projectName: $("#projectName").val(), 
			studyOption: $("#studyOption").val(), 
			startDate: $("#startDate").val(),
			referrer: $("#referrer").val(),
			application: $("#appId1").val(),
			programmeDetailsId: $("#programmeDetailsId").val(),
			application: $('#applicationId').val(),
			applicationId: $('#applicationId').val(),
			supervisors: ""
		};
		
		var primarySupervisor =  $("input[name='primarySupervisor']:checked").val();
		if(primarySupervisor){
			postData.primarySupervisorId = primarySupervisor;
		}
		
		$.post( "/pgadmissions/update/editProgrammeDetails" ,$.param(postData) +"&" + $('[input[name="supervisors"]').serialize(),
		function(data) {
			$('#programmeDetailsSection').html(data);
		});
}