$(document).ready(function()
{
	bindDatePicker('#batchdeadline');
	
	$('#cancelBadge').click(function(){
		$('#programme').val('');
		$('#project').val('');
		$('#programhome').val('');
		$('#batchdeadline').val('');
		$('#html').val('');
		$('#badge').attr("src", "");
	});
	
 	$('#programme').change(updateBadge);
 	$('#project').change(updateBadge);
 	$('#programhome').change(updateBadge);
 	$('#batchdeadline').change(updateBadge);
 	
});
	
function updateBadge(){
	$.get(
 		"/pgadmissions/badge/html",
		{
			program : $('#programme').val(),	
			project: $('#project').val(),
			programhome: $('#programhome').val(),
			batchdeadline: $('#batchdeadline').val(),			
			cacheBreaker: new Date().getTime() 
		},
		function(data) {
			$('#html').html(data);
		}
	);	
	$('#badge').attr("src", "/pgadmissions/badge/html?program=" + $('#programme').val() 
	+ "&project=" +$('#project').val()
	+ "&programhome=" +$('#programhome').val()
	+ "&batchdeadline=" +$('#batchdeadline').val()
	+ "&disable=true");
};				