$(document).ready(function(){
	$('#addressSaveButton').click(function(){
		$.post("/pgadmissions/update/editAddress", { 
			addressLocation: $("#addressLocation").val(),
			addressPostCode: $("#addressPostCode").val(), 
			addressCountry: $("#addressCountry").val(), 
			addressStartDate: $("#addressStartDate").val(),
			addressEndDate: $("#addressEndDate").val(), 
			addressPurpose: $("#addressPurpose").val(), 
			addressContactAddress: $("#addressContactAddress").val(),
			id: $("#id").val(), 
			appId: $("#appId").val(),
			addressId: $("#addressId").val()
		},
		function(data) {
			$('#addressSection').html(data);
		});
	});
	
	$("input[name*='isCA']").click(function() {
		var verb = "";
		if($(this).val() == 'YES'){
			verb = "YES";
		}else{
			verb= "NO";
		}
		$("#contactAddress").val(verb);

	});
	
	$('a[name="addressEditButton"]').click(function(){
		var id = this.id;
		id = id.replace('address_', '');
		$("#addressId").val($('#'+id+"_addressIdDP").val());
		$("#addressLocation").val($('#'+id+"_locationDP").val());
		$("#addressPostCode").val($('#'+id+"_postCodeDP").val());
		$("#addressStartDate").val($('#'+id+"_startDateDP").val());
		$("#addressEndDate").val($('#'+id+"_endDateDP").val());
		$("#addressPurpose").val($('#'+id+"_purposeDP").val());
		$("#addressCountry").val("Romania");
		$("#addressContactAddress").val("NO");
	});

});