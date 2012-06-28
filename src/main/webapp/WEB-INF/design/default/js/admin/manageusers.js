$(document).ready(function(){

	$('#roles').focus();
	
	$('#programId').change(function(){
		 var program = $(this).val();
		 if(program== "-1"){	
			 $('#roles option').each(function(){				
				if(	$(this).val() != 'SUPERADMINISTRATOR'){
					$(this).attr("disabled", "disabled");
				}
			 });
		 }else{
			 window.location.href="/pgadmissions/manageUsers/showPage?programId=" + program;
		}
	});
	
	$('#selectedProgramForNewUser').change(function(){
		 var program = $(this).val();
		 if(program== "-1"){	
			 $('#roles option').each(function(){				
				if(	$(this).val() != 'SUPERADMINISTRATOR'){
					$(this).attr("disabled", "disabled");
				}
			 });
		 }else{
			 window.location.href="/pgadmissions/manageUsers/createNewUser?programId=" + program;
		 }
	});
	
	$('#userId').change(function(){
		 var user = $(this).val();
		 var program = $('#programId').val();
		 window.location.href="/pgadmissions/manageUsers/showPage?programId=" + program + "&userId=" + user;
	});
	
	$('a[name="removeuser"]').click( function(event){
		 event.preventDefault(); 
		 if(confirm("Are you sure you want to remove this user from ALL roles in this programme?")){
		 	var user = $(this).attr("id").replace("remove_", "");
		 	$('#selectedUser').val(user);		 
		 	$('#roles').val("");
		 	$('#programmeForm').submit();
		}
	});
});