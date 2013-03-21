$(document).ready(function() {
    // -----------------------------------------------------------------------------------------
    // Add reviewer
    // -----------------------------------------------------------------------------------------
    $('#addReviewerBtn').click(function() {
        var selectedReviewers = $('#programReviewers').val();
        if (selectedReviewers) {
            for ( var i in selectedReviewers) {
                var id = selectedReviewers[i];
                var $option = $("#programReviewers option[value='" + id + "']");
                var selText = $option.text();
                var category = $option.attr("category");
                $("#programReviewers option[value='" + id + "']").addClass('selected').removeAttr('selected').attr('disabled', 'disabled');
                $("#applicationReviewers").append('<option value="' + id + '" category="' + category + '">' + selText + '</option>');
            }
            $('#applicationReviewers').attr("size", $('#applicationReviewers option').size() + 1);
            resetReviewersErrors();
        }
    });

    // -----------------------------------------------------------------------------------------
    // Remove reviewer
    // -----------------------------------------------------------------------------------------
    $('#removeReviewerBtn').click(function() {
        var selectedReviewers = $('#applicationReviewers').val();
        if (selectedReviewers) {
            for ( var i in selectedReviewers) {
                var id = selectedReviewers[i];
                $("#applicationReviewers option[value='" + id + "']").remove();
                $("#programReviewers option[value='" + id + "']").removeClass('selected').removeAttr('disabled');
            }
            $('#applicationReviewers').attr("size", $('#applicationReviewers option').size() + 1);
        }
        resetReviewersErrors();
    });
});