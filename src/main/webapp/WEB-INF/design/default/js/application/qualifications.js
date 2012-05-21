$(document).ready(function()
{
  $("#acceptTermsQDValue").val("NO");
  
  if ($("#qualificationInstitution").val() == "")
  {
    $("#currentQualificationCB").attr('checked', false);
    $("#currentQualification").val("NO");
  }
  
  // ---------------------------------------------------------------------------------
  // Current qualification checkbox.
  // ---------------------------------------------------------------------------------
  if ($("#currentQualificationCB").is(":checked"))
  {
    $("#currentQualification").val("YES");
  }
  else
  {  
    $("#currentQualification").val("NO");
    $("#qualificationAwardDate").val("").attr("disabled", "disabled");
    $("#proofOfAward").val("").attr("disabled", "disabled");
  }
  
  $("input#currentQualificationCB").click(function()
  {
    if ($("#currentQualification").val() == 'YES')
    {
      // box is checked
      $("#currentQualification").val("NO"); // hidden field
      $("#qualificationAwardDate").val("").attr("disabled", "disabled");
      $("#proofOfAward").val("").attr("disabled", "disabled");
      $("#quali-grad-id").text("Expected Grade / Result / GPA").append('<em>*</em>');
      $("#quali-award-date-lb").text("Award Date").addClass("grey-label");
      $("#quali-proof-of-award-lb").text("Proof of Award (PDF)").addClass("grey-label");
    }
    else
    {    
      // box is not checked
      $("#currentQualification").val("YES"); // hidden field
      $("#qualificationAwardDate").removeAttr("disabled", "disabled");  
      $("#proofOfAward").removeAttr("disabled", "disabled");
      $("#quali-grad-id").text("Grade / Result / GPA").append('<em>*</em>');
      $("#quali-award-date-lb").append('<em>*</em>').removeClass("grey-label");
      $("#quali-proof-of-award-lb").append('<em>*</em>').removeClass("grey-label");
    }
  });
  
  // ---------------------------------------------------------------------------------
  // Edit an existing qualification.
  // ---------------------------------------------------------------------------------
  $('a[name="editQualificationLink"]').click(function()
  {
    var id = this.id;
    id = id.replace('qualification_', '');  
    $.get(
      "/pgadmissions/update/getQualification",
      {
        applicationId:   $('#applicationId').val(),
        qualificationId: id,
        message:         'edit',          
        cacheBreaker:    new Date().getTime()
      },
      function(data)
      {
        $('#qualificationsSection').html(data);
      }
    );
  });
  
  // ---------------------------------------------------------------------------------
  // Delete an existing qualification.
  // ---------------------------------------------------------------------------------
  $('a[name="deleteQualificationButton"]').click(function()
  {
    var id = $(this).attr("id").replace("qualification_", "");
    $.post(
      "/pgadmissions/deleteentity/qualification",
      {
        id: id  
      }, 
      function(data)
      {
        $('#qualificationsSection').html(data);
      }  
    );
  });
  
  $("input[name*='acceptTermsQDCB']").click(function() {
    if ($("#acceptTermsQDValue").val() =='YES'){
      $("#acceptTermsQDValue").val("NO");
    } else {  
      $("#acceptTermsQDValue").val("YES");
      $(".terms-box").attr('style','');
      $.post("/pgadmissions/acceptTerms", {  
        applicationId: $("#applicationId").val(), 
        acceptedTerms: $("#acceptTermsQDValue").val()
      },
      function(data) {
      });
    }
    });
  
  // ---------------------------------------------------------------------------------
  // Add qualification button.
  // ---------------------------------------------------------------------------------
  $('#addQualificationButton').click(function()
  {
    if ($("#acceptTermsQDValue").val() == 'NO')
    {
      //$("span[name='nonAcceptedQD']").html('You must agree to the terms and conditions');
      $(this).parent().parent().find('.terms-box').css({borderColor: 'red', color: 'red'});
    }
    else
    {
      $("span[name='nonAcceptedQD']").html('');
      postQualificationData('add');
    }
  });
  
  // ---------------------------------------------------------------------------------
  // Cancel button.
  // ---------------------------------------------------------------------------------
  $('a[name="qualificationCancelButton"]').click(function(){
    $.get("/pgadmissions/update/getQualification",
        {
          applicationId:  $('#applicationId').val(),
          message: 'cancel',          
          cacheBreaker: new Date().getTime()
        },
        function(data) {
          $('#qualificationsSection').html(data);
        }
    );
  });
  
  // ---------------------------------------------------------------------------------
  // Close button.
  // ---------------------------------------------------------------------------------
  $('#qualificationsCloseButton').click(function()
  {
    $('#qualifications-H2').trigger('click');
    return false;
  });
  
  // ---------------------------------------------------------------------------------
  // Save button.
  // ---------------------------------------------------------------------------------
  $('#qualificationsSaveButton').click(function(){
    if( $("#acceptTermsQDValue").val() =='NO'){ 
      //$("span[name='nonAcceptedQD']").html('You must agree to the terms and conditions');
      $(this).parent().parent().find('.terms-box').css({borderColor: 'red', color: 'red'});
    }
    else{
      $("span[name='nonAcceptedQD']").html('');
      postQualificationData('close');
    }
  });
  
  bindDatePicker('#qualificationStartDate');
  bindDatePicker('#qualificationAwardDate');
  addToolTips();

/*
  // open/close
  var $header  =$('#qualifications-H2');
  var $content = $header.next('div');
  $header.bind('click', function()
  {
    $content.toggle();
    $(this).toggleClass('open', $content.is(':visible'));
    return false;
  });
*/
    
  $('#uploadFields').on('change','#proofOfAward', function(event){  
    ajaxProofOfAwardDelete();
    $('#progress').html("uploading file...");
    $('#proofOfAward').attr("readonly", "readonly");
    ajaxProofOfAwardUpload();
    $('#proofOfAward').removeAttr("readonly");
  });
  
  
});

// ---------------------------------------------------------------------------------
// Submitting qualification data.
// ---------------------------------------------------------------------------------
function postQualificationData(message)
{
  $.post("/pgadmissions/update/editQualification",
  {  
    qualificationSubject:      $("#qualificationSubject").val(), 
    qualificationInstitution: $("#qualificationInstitution").val(), 
    qualificationType:        $("#qualificationType").val(),
    qualificationGrade:        $("#qualificationGrade").val(),
    qualificationScore:        $("#qualificationScore").val(),
    qualificationStartDate:    $("#qualificationStartDate").val(),
    qualificationLanguage:    $("#qualificationLanguage").val(),
    qualificationAwardDate:    $("#qualificationAwardDate").val(),
    completed:                $("#currentQualification").val(),      
    qualificationId:          $("#qualificationId").val(),
    applicationId:            $('#applicationId').val(),
    application:              $('#applicationId').val(),
    institutionCountry:        $('#institutionCountry').val(),
    proofOfAward:              $('#document_PROOF_OF_AWARD').val(),
    message:                  message
  },
  function(data)
  {
    $('#qualificationsSection').html(data);
  });
}


function ajaxProofOfAwardDelete(){
  
  if($('#profOfAwardId') && $('#profOfAwardId').val() && $('#profOfAwardId').val() != ''){
    $.post("/pgadmissions/delete/asyncdelete",
      {
        documentId: $('#profOfAwardId').val()
        
      }        
    );

  }
}
function ajaxProofOfAwardUpload()
{  
  
  $("#progress").ajaxStart(function(){
    $(this).show();
  })
  .ajaxComplete(function(){
    $(this).hide();
    $('#progress').html("");
    
  });

  $.ajaxFileUpload
  (
    {
      url:'/pgadmissions/documents/async',
      secureuri:false,
      
      fileElementId:'proofOfAward',  
      dataType:'text',
      data:{type:'PROOF_OF_AWARD'},
      success: function (data)
      {    
        $('#qualUploadedDocument').html(data);
        $('#qualUploadedDocument').show();
        
      }
    }
  )

}