$(document).ready(function() {
    
    showQualificationAndReferenceSection();
    
	// --------------------------------------------------------------------------------
	// "SAVE AND CLOSE" BUTTON
	// --------------------------------------------------------------------------------
	$('#saveAndClose').click(function()
	{
		window.location.href = '/pgadmissions/applications';
	});
    
});

function showQualificationAndReferenceSection() {
    $('section.folding h2').each(function() {
        if ($(this).attr('id') === "qualifications-H2" || $(this).attr('id') === "referee-H2") {
            $(this).addClass('open');
            $(this).next('div').show();
        } else {
            $(this).removeClass('open');
            $(this).next('div').hide();
        }
    });
}