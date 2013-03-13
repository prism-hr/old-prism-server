$(document).ready(function() {
    
	showReferenceSection();
    
	// --------------------------------------------------------------------------------
	// "SAVE AND CLOSE" BUTTON
	// --------------------------------------------------------------------------------
	$('#saveAndClose').click(function()
	{
		window.location.href = '/pgadmissions/applications';
	});
    
});

function showReferenceSection() {
    $('section.folding h2').each(function() {
        if ($(this).attr('id') === "referee-H2") {
            $(this).addClass('open');
            $(this).next('div').show();
        } else {
            $(this).removeClass('open');
            $(this).next('div').hide();
        }
    });
}