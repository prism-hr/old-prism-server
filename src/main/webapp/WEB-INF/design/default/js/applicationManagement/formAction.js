$(document).ready(function() {
	$("input[name*='decision']").click(function() {
		var verb = "";
		if($(this).val() == 'APPROVED'){
			verb = "APPROVE";
		}else{
			verb= "REJECT";
		}			
			
		if(confirm("Are you sure you want to " + verb +" this application?")){
			$('#approvalForm').submit();
		}
	});	
		
		
});