$(document).ready(function(){
	$("#addSupervisorButton").show();
	$("#awareSupervisorCB").attr('checked', false);
	$("#awareSupervisor").val("NO");
	var unsavedSupervisors = 0;
	
	
	$("#supervisor_div").on("click", "a[name=\"deleteSupervisor\"]", function(){	
		if(confirm("Are you sure you want to delete the supervisor?")){
			var id = this.id;
			if(id.indexOf("usd_") != -1){
				id = id.replace('usd_', '');
				$('#'+id+"_ussupervisors").val('');
			}
			else{
				id = id.replace('supervisorDelete_', '');
				$('#'+id+"_supervisors").val('');
			}
			$(this).parent("span").remove();
			$(this).parent().parent().remove();
			$(this).parent().parent().html('');
		}
	
	});
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
	
	
	$('#programmeSaveButton').on("click",function(){
		postProgrammeData('close');
	});
	function validateEmail(email) { 
		var pattern = new RegExp(/^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?$/i);
	    var result = pattern.test(email);
		return result;
	} 
	
	$('#addSupervisorButton').on('click', function(){
		if( $('#supervisorFirstname').val() ==""  || $('#supervisorFirstname').val() =="First Name" ){
			$("span[name='superFirstname']").html('First name cannot be empty');
			$("span[name='superFirstname']").show();

		}
		if( $('#supervisorLastname').val() ==""  || $('#supervisorLastname').val() =="Last Name" ){
			$("span[name='superLastname']").html('Last name cannot be empty');
			$("span[name='superLastname']").show();

		}
		if( !validateEmail($('#supervisorEmail').val())){
			$("span[name='superEmail']").html('Email is not valid');
			$("span[name='superEmail']").show();

		}
		if( validateEmail($('#supervisorEmail').val()) && $('#supervisorEmail').val() && $('#supervisorEmail').val() !="Email address" &&
				$('#supervisorFirstname').val() && $('#supervisorFirstname').val() !="First Name"&&
				$('#supervisorLastname').val() && $('#supervisorLastname').val() !="Last Name"){
			
		
			$("span[name='superFirstname']").html('');
			$("span[name='superFirstname']").hide();
			$("span[name='superLastname']").html('');
			$("span[name='superLastname']").hide();
			$("span[name='superEmail']").html('');
			$("span[name='superEmail']").hide();
			var aware = "";
			if($('#awareSupervisor').val() =="YES"){
				aware = "Yes";
			}
			else{
				aware = "No";
			}
			unsavedSupervisors++;
			$('table.data-table tbody').append(
					'<tr>' +
					'<td >' + $('#supervisorFirstname').val()+' '+ $('#supervisorLastname').val()+'</td>' +
					'<td >'+ $('#supervisorEmail').val()+ '</td>' +
					'<td >' + aware +'</td>'+
					'<td><a class=\"button-delete\" id="usd_'+unsavedSupervisors+'" name=\"deleteSupervisor\">delete</a> <a class=\"button-edit\" id="us_'+unsavedSupervisors+'" name=\"editSupervisorLink\">edit</a> </td>' +
					'</tr>'+
					'<input type="hidden" id="us_'+unsavedSupervisors+'firstname" value="' + $('#supervisorFirstname').val()+'"/>'	+								
					'<input type="hidden" id="us_'+unsavedSupervisors+'lastname" value="' + $('#supervisorLastname').val()+'"/>'	+								
					'<input type="hidden" id="us_'+unsavedSupervisors+'email" value="' + $('#supervisorEmail').val()+'"/>'	+								
					'<input type="hidden" id="us_'+unsavedSupervisors+'aware" value="' + $('#awareSupervisor').val()+'"/>'	+								
					
					'<input type="hidden" name="supervisors" id="'+unsavedSupervisors+'_ussupervisors" value=' +"'" + '{"id":"' +  $('#supervisorId').val()+ '","firstname":"' +  $('#supervisorFirstname').val()+ '","lastname":"' +  $('#supervisorLastname').val()+ '","email":"' +  $('#supervisorEmail').val() +  '", "awareSupervisor":"' + $('#awareSupervisor').val() + '"} ' + "'" + "/>");
		
		
		 $("input[name='sFN']").val($('#supervisorFirstname').val());
		 $("input[name='sLN']").val($('#supervisorLastname').val());
		 $("input[name='sEM']").val($('#supervisorEmail').val());
		 $("input[name='sAS']").val($('#awareSupervisor').val());
		 $('#supervisorId').val('');
		 $('#supervisorFirstname').val('');
		 $('#supervisorLastname').val('');
		 $('#supervisorEmail').val('');
		 $("#awareSupervisorCB").attr('checked', false);
		 $("#awareSupervisor").val("NO");
		}
	});
	
	$('#updateSupervisorButton').on('click', function(){
		if( $('#supervisorFirstname').val() ==""  || $('#supervisorFirstname').val() =="First Name" ){
			$("span[name='superFirstname']").html('First name cannot be empty');
			$("span[name='superFirstname']").show();
			
		}
		if( $('#supervisorLastname').val() ==""  || $('#supervisorLastname').val() =="Last Name" ){
			$("span[name='superLastname']").html('Last name cannot be empty');
			$("span[name='superLastname']").show();
			
		}
		if( $('#supervisorEmail').val() ==""  || $('#supervisorEmail').val() =="Email address" || !validateEmail($('#supervisorEmail').val())){
			$("span[name='superEmail']").html('Email is not valid');
			$("span[name='superEmail']").show();
			
		}
		if( $('#supervisorEmail').val() && $('#supervisorEmail').val() !="Email address" &&
				$('#supervisorFirstname').val() && $('#supervisorFirstname').val() !="First Name"&&
				$('#supervisorLastname').val() && $('#supervisorLastname').val() !="Last Name"){
			
			
			$("span[name='superFirstname']").html('');
			$("span[name='superFirstname']").hide();
			$("span[name='superLastname']").html('');
			$("span[name='superLastname']").hide();
			$("span[name='superEmail']").html('');
			$("span[name='superEmail']").hide();
			var aware = "";
			if($('#awareSupervisor').val() =="YES"){
				aware = "Yes";
			}
			else{
				aware = "No";
			}
			
			$('table.data-table tbody').append(
					'<tr>' +
					'<td>' + $('#supervisorFirstname').val()+' '+ $('#supervisorLastname').val()+'</td>' +
					'<td>'+ $('#supervisorEmail').val()+ '</td>' +
					'<td>' + aware +'</td>'+
					'<td><a class=\"button-delete\" id="usd_'+unsavedSupervisors+'" name=\"deleteSupervisor\">delete</a> <a class=\"button-edit\" id="us_'+unsavedSupervisors+'" name=\"editSupervisorLink\">edit</a> </td>' +
					'</tr>'+
					'<input type="hidden" id="us_'+unsavedSupervisors+'firstname" value="' + $('#supervisorFirstname').val()+'"/>'	+								
					'<input type="hidden" id="us_'+unsavedSupervisors+'lastname" value="' + $('#supervisorLastname').val()+'"/>'	+								
					'<input type="hidden" id="us_'+unsavedSupervisors+'email" value="' + $('#supervisorEmail').val()+'"/>'	+								
					'<input type="hidden" id="us_'+unsavedSupervisors+'aware" value="' + $('#awareSupervisor').val()+'"/>'	+								
					
					'<input type="hidden" name="supervisors" id="'+unsavedSupervisors+'_ussupervisors" value=' +"'" + '{"id":"' +  $('#supervisorId').val()+ '","firstname":"' +  $('#supervisorFirstname').val()+ '","lastname":"' +  $('#supervisorLastname').val()+ '","email":"' +  $('#supervisorEmail').val() +  '", "awareSupervisor":"' + $('#awareSupervisor').val() + '"} ' + "'" + "/>");
			
		
			$("input[name='sFN']").val($('#supervisorFirstname').val());
			$("input[name='sLN']").val($('#supervisorLastname').val());
			$("input[name='sEM']").val($('#supervisorEmail').val());
			$("input[name='sAS']").val($('#awareSupervisor').val());
			$('#supervisorId').val('');
			$('#supervisorFirstname').val('');
			$('#supervisorLastname').val('');
			$('#supervisorEmail').val('');
		}
		$("#awareSupervisorCB").attr('checked', false);
		$("#awareSupervisor").val("NO");
	});
	
//	$('#updateSupervisorButton').on('click', function(){
//		
//		$.post("/pgadmissions/programme/supervisors/updateSupervisor",
//				{ 
//					firstname: $("#supervisorFirstname").val(),
//					lastname: $("#supervisorLastname").val(), 
//					email: $("#supervisorEmail").val(), 
//					primarySupervisor: $("#primarySupervisor").val(), 
//					programmeDetailsId: $("#programmeDetailsId").val(),
//					supervisorId: $("#supervisorId").val(),
//					awareSupervisor: $("#awareSupervisor").val() 
//				},
//				function(data) {
//					$('#programmeDetailsSection').html(data);
//				});
//	});
	
	$('#programmeCloseButton').click(function(){
		$('#programme-H2').trigger('click');
		return false;
	});
	
	
	  bindDatePicker('#startDate');
	  addToolTips();
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
			supervisors: "",
			message: message
				
		};
		
		$.post( "/pgadmissions/update/editProgrammeDetails" ,$.param(postData) +"&" + $('[input[name="supervisors"]').serialize(),
		function(data) {
			$('#programmeDetailsSection').html(data);
		});
}