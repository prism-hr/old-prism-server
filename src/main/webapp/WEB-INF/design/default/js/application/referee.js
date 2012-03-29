$(document).ready(function(){
	
	$('#refereeCloseButton').click(function(){
		$('#referee-H2').trigger('click');
		return false;
	});
	
	$('a[name="deleteButton"]').click( function(){	
		$(this).parent("form").submit();
	});
	
	$("#phonenumbersref").on("click", "a", function(){	
		$(this).parent("div").parent("div").parent("span").remove();
		
	});
	
	$("#messengersref").on("click", "a", function(){	
		$(this).parent("div").parent("div").parent("span").remove();
		
	});
	
	$('#delBtn').on('click', function(){
		$(this).parent("span").remove();
	});
	
	$('#refereeSaveButton').click(function(){
	
		//phonenumbers
		if( $('#phoneNumberRef').val() != "Number" && $('#phoneNumberRef').val() != ''){	
			var html = "<input type='hidden' name='phoneNumbers' value='{" + '"type":"' +  $('#phoneTypeRef').val()+ '", "number":"' + $('#phoneNumberRef').val()+ '"}'+  "'/>";
			$('#phonenumbersref').append(html);
			
		}
		
		
		var postData ={ 
				firstname: $("#ref_firstname").val(),
				lastname: $("#ref_lastname").val(), 
				relationship: $("#ref_relationship").val(), 
				jobEmployer: $("#ref_employer").val(), 
				jobTitle: $("#ref_position").val(), 
				messenger: $("#ref_messenger").val(), 
				addressLocation: $("#ref_address_location").val(), 
				addressPostcode: $("#ref_address_postcode").val(), 
				addressCountry: $("#ref_address_country").val(), 
				email: $("#ref_email").val(), 
				application: $("#appId").val(),
				refereeId: $("#refereeId").val(),
				phoneNumbers: ""
			}
		
		$.post( "/pgadmissions/update/refereeDetails" ,
				$.param(postData) + "&" + $('#phonenumbersref input[name="phoneNumbers"]').serialize(),
				function(data) {   $('#referencesSection').html(data);  }
			);
	});

$('#refereeSaveAndAddButton').click(function(){
	
	//phonenumbers
	if( $('#phoneNumberRef').val() != "Number" && $('#phoneNumberRef').val() != ''){	
		var html = "<input type='hidden' name='phoneNumbers' value='{" + '"type":"' +  $('#phoneTypeRef').val()+ '", "number":"' + $('#phoneNumberRef').val()+ '"}'+  "'/>";
		$('#phonenumbersref').append(html);
		
	}
	
	var postData ={ 
			firstname: $("#ref_firstname").val(),
			lastname: $("#ref_lastname").val(), 
			relationship: $("#ref_relationship").val(), 
			jobEmployer: $("#ref_employer").val(), 
			jobTitle: $("#ref_position").val(), 
			addressLocation: $("#ref_address_location").val(), 
			addressPostcode: $("#ref_address_postcode").val(), 
			messenger: $("#ref_messenger").val(), 
			addressCountry: $("#ref_address_country").val(), 
			email: $("#ref_email").val(), 
			application: $("#appId").val(),
			refereeId: $("#refereeId").val(),
			phoneNumbers: "",
			add:"add"
	}
	$.post( "/pgadmissions/update/refereeDetails" , $.param(postData) + "&" + $('#phonenumbersref input[name="phoneNumbers"]').serialize(),
			
			function(data) {
		$('#referencesSection').html(data);
	});
});

$('a[name="refereeCancelButton"]').click(function(){
	$("#refereeId").val("");
	$("#ref_firstname").val("");
	$("#ref_lastname").val("");
	$("#ref_relationship").val("");
	$("#ref_employer").val("");
	$("#ref_position").val("");
	$("#ref_address_location").val("");
	$("#ref_address_postcode").val("");
	$("#ref_address_country").val("");
	$("#ref_email").val("");
	$("#ref_address_country").val("");
	$("#ref_messenger").val("");
	$('#phonenumbersref').html("");
	$("span[class='invalid']").each(function(){
		$(this).html("");
	});
});

$('a[name="refereeEditButton"]').click(function(){
	var id = this.id;
	id = id.replace('referee_', '');
	$("#refereeId").val($('#'+id+"_refereeId").val());
	$("#ref_firstname").val($('#'+id+"_firstname").val());
	$("#ref_lastname").val($('#'+id+"_lastname").val());
	$("#ref_relationship").val($('#'+id+"_relationship").val());
	$("#ref_employer").val($('#'+id+"_jobEmployer").val());
	$("#ref_position").val($('#'+id+"_jobTitle").val());
	$("#ref_messenger").val($('#'+id+"_messenger").val());
	$("#ref_address_location").val($('#'+id+"_addressLocation").val());
	$("#ref_address_postcode").val($('#'+id+"_addressPostcode").val());
	$("#ref_address_country").val($('#'+id+"_addressCountry").val());
	$("#ref_email").val($('#'+id+"_email").val());
	
	$('#phonenumbersref').html("");
	$("span[name='"+id+"_hiddenPhones']").each(function(){
		$('#phonenumbersref').append('<span name="phone_number_ref">'+ $(this).html() + '</span>');
	});
	if($("#referenceUpdated")){
		$("#referenceUpdated").html($('#'+id+"_lastUpdated").val());
	}

	if($("#referenceDocument")){
		if($('#'+id+"_reference_document_url").val()){
			$("#referenceDocument").html("<a href='" + $('#'+id+"_reference_document_url").val() + "'>" + $('#'+id+"_reference_document_name").val() +  "</a>");
		}else{
			$("#referenceDocument").html( $('#'+id+"_reference_document_name").val());
		}
	}
});


$('#addPhoneRefButton').on('click', function(){
	if($('#phoneNumberRef').val() !="Number" && $('#phoneNumberRef').val()!= ''){
		var html = ''+
			'<span  name="phone_number_ref">'+
	  	  	'	<div class="row">'+
	  	  	' 		<span class="label">Telephone</span>  '+  
	  		'		<div class="field">'+
	  		'			<label class="half">' + $('#phoneTypeRef option:selected').text() +'</label>'+
	  		'			<label class="half">'+   $('#phoneNumberRef').val() +'</label> '+
	  	  	'			<a class="button-delete">Delete</a>'+           
	  	  	'		</div>'+	  	  			
	  	  	'	</div>'+   
			'<input type="hidden" name="phoneNumbers" value=' +"'" + '{"type":"' +  $('#phoneTypeRef').val()+ '", "number":"' + $('#phoneNumberRef').val()+ '"} ' + "'" + "/>"	+  
			'</span>';
	
		$('#phonenumbersref').append(html);
		
		$('#phoneNumberRef').val('');
	}
})

// To make uncompleted functionalities disable.
$(".disabledEle").attr("disabled", "disabled");

//open/close
var $header  =$('#referee-H2');
var $content = $header.next('div');
$header.bind('click', function()
{
  $content.toggle();
  $(this).toggleClass('open', $content.is(':visible'));
  return false;
});

});

