$(document).ready(
		function() {

			$('#changeStateButton').click(

					function() {
//						
//						if ($('#status').val() == 'APPROVAL' ||  $('#status').val() == 'APPROVED' ||  $('#status').val() == 'REJECTED' || $('#status').val() == 'REVIEW') {
//							saveComment();
//							return;
//						}
//
						if ($('#status').val() == 'INTERVIEW') {
							if ($('#appliationAdmin').length == 0
									|| $('#appliationAdmin').val() == '') {
								saveComment();
								return;
							} else {
								$('#delegateForm').submit();
							}

						}
						
						saveComment();
						return;
					});

			$('#status').change(function() {
				if ($('#status').val() == 'INTERVIEW') {
					$('#appliationAdmin').removeAttr('disabled');
				} else {
					$('#appliationAdmin')
				}
			});

			$('#notifyRegistryButton').click(function()
			{
				$('#notifyRegistryButton').attr('disabled', 'disabled');
				$('#notifyRegistryButton').removeClass("blue");
				$('body').css('cursor', 'wait');
				$.ajax({
					type: 'POST',
					 statusCode: {
						  401: function() {
							  window.location.reload();
						  }
					  },
					url:"/pgadmissions/registryHelpRequest",
					data:{
						applicationId : $('#applicationId').val()
					},
					success:function(data)
					{
						$('#emailMessage').html(data);
						$('#notifyRegistryButton').removeAttr('disabled');
						$('#notifyRegistryButton').addClass("blue");
						$('body').css('cursor', 'auto');
						//$('#timelineBtn').trigger('click');
						// Go to application list.
						window.location.href = '/pgadmissions/applications';
					}
				});
				
				return false;
			});
		});

function saveComment() {
	$('#nextStatus').val($('#status').val());

	$('#commentField').val($('#comment').val());

	if ($('input:radio[name=qualifiedForPhd]:checked').length > 0) {
		$('#stateChangeForm').append(
				'<input type="hidden" name="qualifiedForPhd" value="'
						+ $('input:radio[name=qualifiedForPhd]:checked').val()
						+ '"/>');
	}
	if ($('input:radio[name=englishCompentencyOk]:checked').length > 0) {
		$('#stateChangeForm').append(
				'<input type="hidden" name="englishCompentencyOk" value="'
						+ $('input:radio[name=englishCompentencyOk]:checked')
								.val() + '"/>');

	}
	if ($('input:radio[name=homeOrOverseas]:checked').length > 0) {
		$('#stateChangeForm').append(
				'<input type="hidden" name="homeOrOverseas" value="'
						+ $('input:radio[name=homeOrOverseas]:checked').val()
						+ '"/>');
	}
	$('input[name="documents"]').each(function(){
		$('#stateChangeForm').append(
				'<input type="hidden" name="documents" value="'
						+ $(this).val()
						+ '"/>');
	});
	 $('#stateChangeForm').submit();

}

