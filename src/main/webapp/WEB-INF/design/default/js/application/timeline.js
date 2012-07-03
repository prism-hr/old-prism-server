$(document).ready(function()
{
	
	$.ajax({
		 type: 'GET',
		 statusCode: {
			  401: function() {
				  window.location.reload();
			  }
		  },
			url:"/pgadmissions/comments/view",
			data:{
				id:  $('#applicationId').val(),				
				cacheBreaker: new Date().getTime() 
			},
			success:function(data) {
				$('#timeline').html(data);				
			}
	});
		
	
});