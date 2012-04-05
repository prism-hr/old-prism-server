$(document).ready(function(){


	$('#addressCloseButton').click(function(){
		$('#address-H2').trigger('click');
		return false;
	});
	
	$('a[name="deleteButton"]').click( function(){	
		$(this).parent("form").submit();
	});
	
	$('#addressSaveAndAddButton').click(function(){
		$.post("/pgadmissions/update/editAddress", { 
			addressLocation: $("#addressLocation").val(),
			addressCountry: $("#addressCountry").val(), 
			id: $("#id").val(), 
			appId: $("#appId").val(),
			addressId: $("#addressId").val(),
			add:"Add"
		},
		function(data) {
			$('#addressSection').html(data);
		});
	});
	
	$('#addressSaveAndCloseButton').click(function(){
		$.post("/pgadmissions/update/editAddress", { 
			addressLocation: $("#addressLocation").val(),
			addressCountry: $("#addressCountry").val(), 
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
		$("#addressCountry").val("");
		$("span[class='invalid']").each(function(){
			$(this).hide();
		});
	});
	
	
	$('a[name="addressEditButton"]').click(function(){

		var id = this.id;
		id = id.replace('address_', '');
		$("#addressId").val($('#'+id+"_addressIdDP").val());
		$("#addressLocation").val($('#'+id+"_locationDP").val());
		$("#addressCountry").val($('#'+id+"_countryDP").val());
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