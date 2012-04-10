$(document).ready(function(){
	
	$("span[class='supervisorAction']").html('<a id="addSupervisorButton" class="button" style="width: 110px;">Add Supervisor</a>');
	
	$("#awareSupervisorCB").attr('checked', false);
	$("#primarySupervisorCB").attr('checked', false);
	$("#primarySupervisor").val("NO");
	$("#awareSupervisor").val("NO");
	
	$("#supervisor_div").on("click", "a", function(){	
		$(this).parent("span").remove();
		
	});
	
	$('#programmeCancelButton').click(function(){
		$("span[class='supervisorAction']").html('<a id="addSupervisorButton" class="button" style="width: 110px;">Add Supervisor</a>');
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
					"<tr><td>" + escape($('#supervisorFirstname').val())+" "+ escape($('#supervisorLastname').val())+"</td><td>"+
					escape($('#supervisorEmail').val())+ "</td><td><td> <input type=\"radio\" name=\"primarySupervisor\" /> </td><td>" + escape($('#awareSupervisor').val()) +'</td><td><a class="button-delete">delete</a>  <a class="button-edit">edit</a></td></tr>'+
					'<input type="hidden" name="supervisors" value=' +"'" + '{"id":"' +  escape($('#supervisorId').val())+ '","firstname":"' +  escape($('#supervisorFirstname').val())+ '","lastname":"' +  escape($('#supervisorLastname').val())+ '","email":"' +  escape($('#supervisorEmail').val()) + '", "primarySupervisor":"' + escape($('#primarySupervisor').val()) + '", "awareSupervisor":"' + escape($('#awareSupervisor').val()) + '"} ' + "'" + "/>"									
					+'<br/></span>');
			$('#supervisorId').val('');
			$('#supervisorFirstname').val('');
			$('#supervisorLastname').val('');
			$('#supervisorEmail').val('');
		}
	});
	
	$('a[name="editSupervisorLink"]').click(function(){
		$("span[class='supervisorAction']").html('<a id="updateSupervisorButton" class="button" style="width: 110px;">Update Supervisor</a>');
		var id = this.id;
		id = id.replace('supervisor_', '');
		$("#supervisorId").val(id);
		$("#supervisorFirstname").val($('#'+id+"_firstname").val());
		$("#supervisorLastname").val($('#'+id+"_lastname").val());
		$("#supervisorEmail").val($('#'+id+"_email").val());
		if ($('#'+id+'_positionCompleted').val() =='YES'){
			$("#awareSupervisorCB").attr('checked', true);
			$("#awareSupervisor").val("YES");
		} else {
			$("#awareSupervisorCB").attr('checked', false);
			$("#awareSupervisor").val("NO");
		}
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
		
		
	// Supervisor data table
		$('.data-table').hide();
		
		$('#addSupervisor').click(function(){
			
			var $copiedForm = $('#supervisor-form').clone();
			var $firstName =  $copiedForm.find('input[name=firstName]').val();
			var $surname =  $copiedForm.find('input[name=surname]').val();
			var $email = $copiedForm.find('input[name=email]').val();
			var $isAware = $copiedForm.find('input[name=isAware]').is(':checked');
			
			if($firstName != "" && $surname != "" && $email != ""){
				
				var $fullname = $firstName + " " + $surname;
				
				var $emptyRow = '<tr class="tableRow">' 
					+'<td id="fullname">'+$fullname+'</td>'
					+'<td id="email">'+$email+'</td>'
					+'<td><input type="radio" name="isPrimary"/></td>'
					+'<td id="aware">'+$isAware+'</td>'	
					+'<td>'+'<a class="deleteButton" name="deleteButton">delete</a>'
					+'&nbsp;&nbsp;'+'<a class="editButton" name="editButton">edit</a>'+'</td>'
					+'</tr>';
				
				//Append the copeid row in the table body
				$('.data-table').show();
				$('table.data-table tbody').append($emptyRow);
				
			}else{
				$('.data-table').hide();
				alert("Please enter valid input!");
			}
			
			//clear the fields
			$(this).parent().find('input[type=text], input[type=checkbox]').each(function(){
				$(this).val("");
				
				if($(this).is(':checked') == true){
					$(this).prop("checked", false);
				}
			});
			
			$copiedForm.find('input[name=firstName]').focus();
			
			return false;			
		});
			
		$('.deleteButton').live('click',function(){
			$(this).parent().parent().remove();
			//TODO: remove the table structure after deleting the last row
		});
		
		$('.editButton').live('click',function(){
			
			// selectors
			//$('#supervisor-form input[name=firstName]').val("000900");
			//$('#supervisor-form').find('input[name=firstName]').val("000900");
			
			var $copiedRow = $(this).parent().parent();
			
			var $firstName =  $copiedRow.find('td#fullname').text().split(' ')[0];
			var $surname =  $copiedRow.find('td#fullname').text().split(' ')[1];
			var $email = $copiedRow.find('td#email').text();

			$('#supervisor-form').find('input[name=firstName]').val($firstName);
			$('#supervisor-form').find('input[name=surname]').val($surname);
			$('#supervisor-form').find('input[name=email]').val($email);
			if($copiedRow.find('td#aware').text() == "true"){
				$('#supervisor-form').find('input[name=isAware]').attr('checked',true);	
			}else{
				$('#supervisor-form').find('input[name=isAware]').attr('checked', false);
			}
		});

});