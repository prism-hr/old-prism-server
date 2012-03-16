$(document).ready(function(){


	$('#addressCloseButton').click(function(){
		$('#address-H2').trigger('click');
		return false;
	});
	
	if($("#addressContactAddress").val() == ''){
		$("#addressContactAddress").val("NO");
	}
	
	$('#addressSaveAndAddButton').click(function(){
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
	
	$('#addressSaveAndCloseButton').click(function(){
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
	
	$('a[name="addressCancelButton"]').click(function(){
		$("#addressId").val("");
		$("#addressLocation").val("");
		$("#addressPostCode").val("");
		$("#addressCountry").val("");
		$("#addressStartDate").val("");
		$("#addressEndDate").val("");
		$("#addressPurpose").val("");
		$("#addressContactAddress").val("");
		$("#isCA").attr('checked', false);
	});
	
	
	$("input[name*='isCA']").click(function() {
		if ($("#addressContactAddress").val() =='YES'){
			$("#addressContactAddress").val("NO");
		} else {		
			$("#addressContactAddress").val("YES");
		}

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
		$("#addressCountry").val($('#'+id+"_countryDP").val());
		$("#addressContactAddress").val($('#'+id+"_contactAddressDP").val());
		if ($("#addressContactAddress").val() =='YES'){
			$("#isCA").attr('checked', true);
		} else {
			$("#isCA").attr('checked', false);
		}
	});

	  bindDatePickers();

		//open/close
		var $header  =$('#address-H2');
		var $content = $header.next('div');
		$header.bind('click', function()
		{
		  $content.toggle();
		  $(this).toggleClass('open', $content.is(':visible'));
		  return false;
		});
		
		
});