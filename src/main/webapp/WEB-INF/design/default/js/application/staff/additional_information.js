$(document).ready(function()
{
  
  // ---------------------------------------------------------------------------------
  // Cancel button.
  // ---------------------------------------------------------------------------------
  $('a[name="informationCancelButton"]').click(function()
  {
    $("#additionalInformation").val("");
  });

  // ---------------------------------------------------------------------------------
  // Close button.
  // ---------------------------------------------------------------------------------
  $('#additionalCloseButton').click(function()
  {
    $('#additional-H2').trigger('click');
    return false;
  });
  
  // ---------------------------------------------------------------------------------
  // Save button.
  // ---------------------------------------------------------------------------------
  $('#informationSaveButton').click(function()
  {
    var hasConvictions = null;
    if ($('#convictionRadio_true:checked').val() !== undefined)
    {
      hasConvictions = true;
    }
    if ($('#convictionRadio_false:checked').val() !== undefined)
    {
      hasConvictions = false;
    }

    $.post(
      "/pgadmissions/update/editAdditionalInformation",
      { 
        informationText: $("#informationText").val(),
        convictions:     hasConvictions,
        convictionsText: $("#convictionsText").val(),
        applicationId:   $('#applicationId').val(),
        application:     $('#applicationId').val(),
        message:         'close'
      },
      function(data)
      {
        $('#additionalInformationSection').html(data);
      }
    );
  });
  
  /*
  //open/close
  var $header  =$('#additional-H2');
  var $content = $header.next('div');
  $header.bind('click', function()
  {
    $content.toggle();
    $(this).toggleClass('open', $content.is(':visible'));
    return false;
  });
  */
  
});