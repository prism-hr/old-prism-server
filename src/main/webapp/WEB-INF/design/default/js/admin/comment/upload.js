function watchUploadComment($field)
{
	var $container = $field.parent('div.field');
	
	$("#commentUploadedDocument").on('click', '.button-delete', function() {
		deleteUploadedFileComment($(this).attr("id"));
		$(this).parent().parent().parent().remove();
	});

	$container.on('change', $field, function()
	{
		var input    = this.children[0];
		$container.addClass('posting');
		doUploadComment($(input));
		$field.removeAttr("readonly");
	});
}

// ------------------------------------------------------------------------------
// Delete an uploaded file referenced by a hidden field.
// ------------------------------------------------------------------------------
function deleteUploadedFileComment(id) {
	if (id) {
		$.ajax({
			type: 'POST',
			statusCode: {
				401: function() {
					window.location.reload();
				}
			},
			url: "/pgadmissions/delete/asyncdelete",
			data: { documentId: id }
		});
	}
}

// ------------------------------------------------------------------------------
// Upload a file from an INPUT field using AJAX.
// ------------------------------------------------------------------------------
function doUploadComment($upload_field)
{	
  var $container  = $upload_field.parent('div.field');
  var $hidden     = $container.find('span input');
  var $hfParent   = $hidden.parent();
  var $progress   = $container.find('span.progress');

	// Remove any previous error messages.
	$container.find('span.invalid').remove();

	$.ajaxFileUpload({
    url:            '/pgadmissions/documents/async',
    secureuri:      false,
    fileElementId:  $upload_field.attr('id'),	
    dataType:       'text',
    data:           { type: $upload_field.attr('data-type') },
    success: function (data)
    {		
			$container.removeClass('posting');
			if ($(data).find('span.invalid').length > 0)
			{
				// There was an uploading error.
				$container.append(data);
			}
			else if ($(data).find('input').length == 0)
			{
				// There was (probably) a server error.
				$container.append('<span class="invalid">You must upload a PDF document (2Mb). </span>');
			}
			else  
			{
				$container.addClass('uploaded');
				$("<div class=\"row\"><div class=\"field\">" + data + "</span></div>").appendTo($('#commentUploadedDocument'));
				$('#commentUploadedDocument').show();
				$('#commentDocument').show();
				$('#commentDocument').val("");
				$('.uploaded-file').show();
			}
    },
	error: function()
	{
		$container.append('<span class="invalid">Upload failed; please retry.</span>');
	}
  });
}


$(document).ready(function() {
	watchUploadComment($('#commentDocument'));
	
	$('#referenceSaveButton').click(function()
	{
		if (!validateReference())
		{
			return false;
		}
		
		var message = 'Please confirm that you are satisfied with your comments. <b>You will not be able to change them.</b>';
		var onOk    = function()
		{
			$('#documentUploadForm').submit();
		};
		var onCancel = function(){};
		
		modalPrompt(message, onOk, onCancel);
		return false;
	});
});

function validateReference()
{
	var errors = 0;
	$('#reviewForm span.invalid').remove();
	
	if ($('#comment').val() == '')
	{
		$('#comment').after('<span class="invalid">You must make an entry.</span>');
		errors++;
	}

	if ($('input[name="suitableForUCL"]:checked').length == 0)
	{
		$('#field-issuitableucl').append('<span class="invalid">You must make a selection.</span>');
		errors++;
	}

	if ($('input[name="suitableForProgramme"]:checked').length == 0)
	{
		$('#field-issuitableprog').append('<span class="invalid">You must make a selection.</span>');
		errors++;
	}
	
	return (errors == 0);
}