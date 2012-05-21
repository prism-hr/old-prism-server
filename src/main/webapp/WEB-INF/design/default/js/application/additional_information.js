$(document).ready(function(){
  $("#acceptTermsAIDValue").val("NO");
  
  limitTextArea();
  
  $('#additionalCloseButton').click(function(){
    $('#additional-H2').trigger('click');
    return false;
  });
  
  $('a[name="informationCancelButton"]').click(function(){
    $.get("/pgadmissions/update/getAdditionalInformation",
        {
          applicationId:  $('#applicationId').val(),
          message: 'cancel',          
          cacheBreaker: new Date().getTime() 
        },
        function(data) {
          $('#additionalInformationSection').html(data);
        }
    );
  });
  
  $("input[name$='convictionRadio']").click(function() {
    if($(this).val() == 'TRUE') {
      $("#convictions-details-lbl").append('<em>*</em>').removeClass("grey-label");
      $("#convictionsText").removeClass("grey-label");
      $("#convictionsText").removeAttr("disabled", "disabled");
    } else {
      var newLblText = $("#convictions-details-lbl").text();
      var starIndex = newLblText.lastIndexOf("*");
      if( starIndex > 0) {
        newLblText = newLblText.substring(0, starIndex);
      }
      $("#convictions-details-lbl").text(newLblText).addClass("grey-label");
      $("#convictionsText").val("");
      $("#convictionsText").addClass("grey-label");
      $("#convictionsText").attr("disabled", "disabled");
    }
  });
  
  $("input[name*='acceptTermsAIDCB']").click(function() {
    if ($("#acceptTermsAIDValue").val() =='YES'){
      $("#acceptTermsAIDValue").val("NO");
    } else {
      $("#acceptTermsAIDValue").val("YES");
      $(".terms-box").attr('style','');
      $.post("/pgadmissions/acceptTerms", {  
        applicationId: $("#applicationId").val(), 
        acceptedTerms: $("#acceptTermsAIDValue").val()
      },
      function(data) {
      });
    }
    });
  
  
  $('#informationSaveButton').click(function(){
    var hasConvictions = null;
    if ($('#convictionRadio_true:checked').val() !== undefined) {
      hasConvictions = true;
    }
    if ($('#convictionRadio_false:checked').val() !== undefined) {
      hasConvictions = false;
    }
    if( $("#acceptTermsAIDValue").val() =='NO'){ 
      //$("span[name='nonAcceptedAID']").html('You must agree to the terms and conditions');
      $(this).parent().parent().find('.terms-box').css({borderColor: 'red', color: 'red'});
    }
    else{

      $("span[name='nonAcceptedAID']").html('');
      $.post("/pgadmissions/update/editAdditionalInformation", { 
        informationText: $("#informationText").val(),
        convictions: hasConvictions,
        convictionsText: $("#convictionsText").val(),
        applicationId:  $('#applicationId').val(),
        application:  $('#applicationId').val(),
        message:'close'
      },
      
      function(data) {
        $('#additionalInformationSection').html(data);
      });
    }

    
  });
  
  addToolTips();
  //open/close
  var $header  =$('#additional-H2');
  var $content = $header.next('div');
  $header.bind('click', function()
  {
    $content.toggle();
    $(this).toggleClass('open', $content.is(':visible'));
    return false;
  });
  
});