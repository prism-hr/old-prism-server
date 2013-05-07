$(document).ready(function() {
	
    getInterviewersAndDetailsSections();

    getCreateInterviewersSection();

    // -----------------------------------------------------------------------------------------
    // Add interviewer
    // -----------------------------------------------------------------------------------------
    $('#assignInterviewersToAppSection').on('click', '#addInterviewerBtn', function() {
        var selectedReviewers = $('#programInterviewers').val();
        if (selectedReviewers) {
            for ( var i in selectedReviewers) {
                var id = selectedReviewers[i];
                var $option = $("#programInterviewers option[value='" + id + "']");
                var selText = $option.text();
                var category = $option.attr("category");
                $("#programInterviewers option[value='" + id + "']").addClass('selected').removeAttr('selected').attr('disabled', 'disabled');
                $("#applicationInterviewers").append('<option value="' + id + '" category="' + category + '">' + selText + '</option>');
            }
            $('#applicationInterviewers').attr("size", $('#applicationInterviewers option').size() + 1);
            resetInterviwersErrors();
        }
    });

    // -----------------------------------------------------------------------------------------
    // Create interviewer button.
    // -----------------------------------------------------------------------------------------
    $('#createinterviewersection').on('click', '#createInterviewer', function() {
        $('#ajaxloader').show();
        var postData = {
            applicationId : $('#applicationId').val(),
            firstName : $('#newInterviewerFirstName').val(),
            lastName : $('#newInterviewerLastName').val(),
            email : $('#newInterviewerEmail').val()
        };
        
        $.ajax({
            type : 'POST',
            statusCode : {
                401 : function() {
                    window.location.reload();
                },
                500 : function() {
                    window.location.href = "/pgadmissions/error";
                },
                404 : function() {
                    window.location.href = "/pgadmissions/404";
                },
                400 : function() {
                    window.location.href = "/pgadmissions/400";
                },
                403 : function() {
                    window.location.href = "/pgadmissions/404";
                }
            },
            url : "/pgadmissions/interview/createInterviewer",
            data : $.param(postData),
            success : function(data) {
                var newInterviewer;
                try {
                    newInterviewer = jQuery.parseJSON(data);
                } catch (err) {
                    $('#createinterviewersection').html(data);
                    addToolTips();
                    return;
                }
                if (newInterviewer.isNew) {
                    $('#previous').append('<option value="' + newInterviewer.id + '" category="previous" disabled="disabled">' + newInterviewer.firstname + ' ' + newInterviewer.lastname + '</option>');
                    $('#applicationInterviewers').append('<option value="' + newInterviewer.id + '">' + newInterviewer.firstname + ' ' + newInterviewer.lastname + '</option>');
                    $('#applicationInterviewers').attr("size", $('#applicationInterviewers option').size() + 1);

                } else {
                    addExistingUserToInterviewersLists(newInterviewer);
                }
                resetInterviwersErrors();
                getCreateInterviewersSection();
                addToolTips();
            },
            complete : function() {
                $('#ajaxloader').fadeOut('fast');
				addCounter();
            }
        });

    });

    // -----------------------------------------------------------------------------------------
    // Remove interviewer
    // -----------------------------------------------------------------------------------------
    $('#assignInterviewersToAppSection').on('click', '#removeInterviewerBtn', function() {
        var selectedReviewers = $('#applicationInterviewers').val();
        if (selectedReviewers) {
            for ( var i in selectedReviewers) {
                var id = selectedReviewers[i];
                $("#applicationInterviewers option[value='" + id + "']").remove();
                $("#programInterviewers option[value='" + id + "']").removeClass('selected').removeAttr('disabled');
            }
            $('#applicationInterviewers').attr("size", $('#applicationInterviewers option').size() + 1);
            resetInterviwersErrors();
        }
    });

    // -----------------------------------------------------------------------------------------
    // Submit button.
    // -----------------------------------------------------------------------------------------
    $('#moveToInterviewBtn').click(function() {
        $('#interviewsection div.alert-error').remove();
        $('#ajaxloader').show();
        var url = "/pgadmissions/interview/move";

        $('#applicationInterviewers option').each(function() {
            $('#postInterviewData').append("<input name='interviewers' type='text' value='" + $(this).val() + "'/>");
        });

        var stage = null;
        if($('input:radio[name=interviewStatus]:checked').length > 0){
        	stage = $('input:radio[name=interviewStatus]:checked').val();
        }
        
        var takenPlace = false;
        if(stage == 'TAKEN_PLACE'){
        	takenPlace = true;
        	stage = 'SCHEDULED';
        }
        
        var postData = {
            applicationId : $('#applicationId').val(),
            interviewers : '',
            timeZone : $('#timezone option:selected').attr('gmtAdjustment'),
            takenPlace : takenPlace
        };
        
        var duration = parseFloat($('#interviewDurationValue').val());
    	
    	if(!isNaN(duration)){
    		if ($('#interviewDurationUnits').val() == "hours") {
        		duration = duration *  60;
            }
    		postData.duration = duration;
    	} else {
    		postData.duration = $('#interviewDurationValue').val();
    	}
        
        if(stage != null){
        	postData.stage = stage;
        }
        
        if (stage == 'TAKEN_PLACE' || stage == 'SCHEDULED') {
        	postData.interviewTime = $('#hours').val() + ":" + $('#minutes').val();
        	postData.interviewDueDate = $('#interviewDate').val();
        }
        
        if (stage == 'SCHEDULED' || stage == 'SCHEDULING') {
        	postData.furtherDetails = $('#furtherDetails').val();
        	postData.furtherInterviewerDetails = $('#furtherInterviewerDetails').val();
        	postData.locationURL = $('#interviewLocation').val();
        }
        
        if (stage == 'SCHEDULING') {
        	// Creates time slots array.
        	var timeslots = [];
        	
        	$.each($('#interviewPossibleStartTimes tbody tr'), function (i, e) {
        		var dateValue = $(e).find('.dateValue').val();
        		
        		$.each($(this).find('input.time'), function (i, e){
        			var startTime = $(e).val();
        			
        			if (startTime.length > 0) {
        				var timeslot = {
            					dueDate: dateValue,
            					startTime: startTime
                		};

            			timeslots.push(timeslot);
        			}
        		});
        	});
        	
        	postData.timeslots = JSON.stringify(timeslots);
        }
        
        $.ajax({
            type : 'POST',
            statusCode : {
                401 : function() {
                    window.location.reload();
                },
                500 : function() {
                    window.location.href = "/pgadmissions/error";
                },
                404 : function() {
                    window.location.href = "/pgadmissions/404";
                },
                400 : function() {
                    window.location.href = "/pgadmissions/400";
                },
                403 : function() {
                    window.location.href = "/pgadmissions/404";
                }
            },
            url : url,
            data : $.param(postData) + "&" + $('input[name="interviewers"]').serialize(),
            success : function(data) {
                if (data == "OK") {
                    window.location.href = '/pgadmissions/applications?messageCode=move.interview&application=' + $('#applicationId').val();

                } else {
                    $('#temp').html(data);
                    $('#assignInterviewersToAppSection').html($('#section_1').html());
                    $('#interviewdetailsSection').html($('#section_2').html());
                    $('#temp').empty();
                    $('#postInterviewData').empty();
                    addToolTips();
                    $('#interviewDate').attr("readonly", "readonly");
                    $('#interviewDate').datepicker({
                        dateFormat : 'dd M yy',
                        changeMonth : true,
                        changeYear : true,
                        yearRange : '1900:+20'
                    });
                    
                    
                    addControlsToSelectAvailableDatesAndTimes();
                    repositionAvailableDates();
                    forceDisplayFilledTimes();
                    recoverSubmittedValues();
                    
                    var interviewStatus = $('input[name=interviewStatus]:radio');
                	interviewStatus.change(showProperInterviewArrangements);
                	interviewStatus.change();
                }
                addToolTips();
            },
            complete : function() {
                $('#ajaxloader').fadeOut('fast');
				addCounter();
            }
        });
    });
});

function recoverSubmittedValues() {
	var durationInMinutes = parseInt($('#submittedInterviewDuration').val());
	
	if (durationInMinutes > 0) {
		if (durationInMinutes % 30 === 0) {
			$('#interviewDurationValue').val(durationInMinutes / 60);
			$('#interviewDurationUnits option[value=hours]').attr('selected', 'selected');
		}
		else {
			$('#interviewDurationValue').val(durationInMinutes);
			$('#interviewDurationUnits option[value=minutes]').attr('selected', 'selected');
		}
	}
	
	var submittedTimezone = $('#submittedInterviewDuration').val();
	
	if (submittedTimezone.length > 0) {
		$('#timezone option[text=' + submittedTimezone + ']').attr('selected', 'selected');
	}
}

function getInterviewersAndDetailsSections() {
    $('#ajaxloader').show();

    $.mask.definitions['H'] = "[0-2]";
    $.mask.definitions['h'] = "[0-9]";
    $.mask.definitions['M'] = "[0-5]";
    $.mask.definitions['m'] = "[0-9]";

    var url = "/pgadmissions/interview/interviewers_section";

    $.ajax({
        type : 'GET',
        statusCode : {
            401 : function() {
                window.location.reload();
            },
            500 : function() {
                window.location.href = "/pgadmissions/error";
            },
            404 : function() {
                window.location.href = "/pgadmissions/404";
            },
            400 : function() {
                window.location.href = "/pgadmissions/400";
            },
            403 : function() {
                window.location.href = "/pgadmissions/404";
            }
        },
        url : url + "?applicationId=" + $('#applicationId').val(),
        success : function(data) {
            $('#temp').html(data);
            $('#assignInterviewersToAppSection').html($('#section_1').html());
            
            $('#interviewdetailsSection').html($('#section_2').html());
            
            $('#temp').empty();

            addToolTips();
            $('#interviewDate').attr("readonly", "readonly");
            $('#interviewDate').datepicker({
                dateFormat : 'dd M yy',
                changeMonth : true,
                changeYear : true,
                yearRange : '1900:+20'
            });
            
            addControlsToSelectAvailableDatesAndTimes();
            
            // Interview status.
        	var interviewStatus = $('input[name=interviewStatus]:radio');
        	
        	interviewStatus.change(showProperInterviewArrangements);
        	interviewStatus.change();
        },
        complete : function() {
            $('#ajaxloader').fadeOut('fast');
			addCounter();
        }
    });
}

function addControlsToSelectAvailableDatesAndTimes() {
	// $.datepicker.formatDate('dd/mm/yy');
	$.datepicker._defaults.dateFormat = 'dd/mm/yy';
	$('#availableDatesPicker').multiDatesPicker({
    	maxPicks: 10,
    	dateFormat: 'dd/mm/yy',
    	// beforeShowDay: $.datepicker.noWeekends,
    	onSelect: function(dateText, inst) {
    		var calendar = $(this);
    		var dates = calendar.multiDatesPicker('getDates');
    		
    		var parts = dateText.match(/(\d{1,2})\/(\d{1,2})\/(\d{4})/);
    		var selectedDate = new Date(parts[3], parts[2] - 1, parts[1]);
    		
    		if (dates.indexOf(dateText) >= 0) {
    			addAvailableDate(calendar, selectedDate);
    		}
    		else {
    			removeAvailableDate(calendar, selectedDate);
    		}
    	}
    });

	setPossibleStartTimesVisibility($('#availableDatesPicker'));
    
    $('#interviewPossibleStartTimes .add-column').click(function () {
    	var hideAddColumnLink = false;
    	$.each($('#interviewPossibleStartTimes tr'), function(i, e) {
    		$(this).find('.time-hidden:first').removeClass('time-hidden');
    		
    		if (i == 0 && $(this).find('.time-hidden:first').length == 0) {
    			hideAddColumnLink = true;
    		}
    	});
    	
    	if (hideAddColumnLink) {
    		$(this).hide();
    	}
    });
}

function setPossibleStartTimesVisibility(calendar) {
	var shouldBeVisible = calendar.multiDatesPicker('getDates').length > 0;
	
	if (shouldBeVisible) {
		$('#interviewPossibleStartTimes').show();
	}
	else {
		$('#interviewPossibleStartTimes').hide();
	}
}

function removeAvailableDate(calendar, date) {
	var dateText = dateToDMY(date);
	var dateTextId = stringToSlug(dateText).split('-').join('');

	$('#interviewPossibleStartTimes table').find('#' + dateTextId).closest('tr').remove();

	setPossibleStartTimesVisibility(calendar);
}

function addAvailableDate(calendar, date, times) {
	var dateText = dateToDMY(date);
	var dateTextId = stringToSlug(dateText).split('-').join('');
	
	var tr = $('#interviewPossibleStartTimes').find('#' + dateTextId).closest('tr');
	
	if (tr.length > 0) {
		// If the date is already added, just leave.
		return;
	}
	
	var dateLine = $('<tr></tr>').appendTo($('#interviewPossibleStartTimes tbody'));
	
	var dateCell = $('<td></td>').addClass('suggested-date').appendTo(dateLine);
	
	var dateValueFormatted = dateToDMY(date);
	
	$('<input />', {
		id: dateTextId,
		name: dateTextId,
		type: 'hidden'
	}).val(dateValueFormatted).addClass('dateValue').appendTo(dateCell);
	
	var dateFormatted = $('<span></span>').appendTo(dateCell);
	dateFormatted.text(date.toLocaleString('en-GB', {weekday: "long", year: "numeric", month: "long", day: "numeric"}));
	
	for (var i = 0; i < $('#interviewPossibleStartTimes thead th').length - 2; i++) {
		var timeCell = $('<td></td>', {}).appendTo(dateLine);
		
		// Gets first invisible column so that new dates have the same number of visible time options.
		var firstHiddenElement = $('#interviewPossibleStartTimes thead .time-hidden').first();
		var hiddenElementsStartIndex =  $('#interviewPossibleStartTimes thead tr').children().index(firstHiddenElement);
		
		if (hiddenElementsStartIndex != -1 && (i + 2 > hiddenElementsStartIndex)) {
			timeCell.addClass('time-hidden');
		}
		
		var className = 'time-' + (i + 1);
		var time = $('<input />', {
			name: className,
			type: 'text'
		}).addClass('time').addClass(className);
		
		time.appendTo(timeCell);
		
		time.mask('Hh:Mm');
	}
	
	var removeCell = $('<td></td>').addClass('remove-column').appendTo(dateLine);
	
	var removeButton = $('<a></a>', { href: 'javascript:void(0);' })
		.addClass('remove-date')
		.attr('data-desc', 'Specify the interview anticipated duration')
		.html('<i class="icon-trash icon-large"></i>')
		.appendTo(removeCell);
	
	applyTooltip(removeButton);
	
	removeButton.click(function () {    	
		calendar.multiDatesPicker('toggleDate', dateText);
		$(this).closest('tr').remove();
		setPossibleStartTimesVisibility(calendar);
	});

	setPossibleStartTimesVisibility(calendar);
}

function addAvailableTime(calendar, date, time) {
	var dateText = dateToDMY(date);
	var dateTextId = stringToSlug(dateText).split('-').join('');
	var tr = $('#interviewPossibleStartTimes').find('#' + dateTextId).closest('tr');
	
	if (tr.length > 0) {
		var timeInput = tr.find('input:text').filter(function() { return $(this).val() == ""; }).first();
		timeInput.val(time);		
	}

	setPossibleStartTimesVisibility(calendar);
}

function forceDisplayFilledTimes() {
	var maxInputsFilled = 0;
	$.each($('#interviewPossibleStartTimes tbody tr'), function (i, e) {
		var temp = $(e).find('input:text').filter(function () { return $(this).val() != ""; }).length;
		
		if (temp > maxInputsFilled) {
			maxInputsFilled = temp;
		}
	});
	
	$.each($('#interviewPossibleStartTimes tr'), function (i, e) {
		var cells = $(e).children();
		for (var i = 0; i < maxInputsFilled && i < cells.length; i++) {
			$(cells[i + 1]).removeClass('time-hidden');
		}
	});
}

function getCreateInterviewersSection() {
    $('#ajaxloader').show();

    $.ajax({
        type : 'GET',
        statusCode : {
            401 : function() {
                window.location.reload();
            },
            500 : function() {
                window.location.href = "/pgadmissions/error";
            },
            404 : function() {
                window.location.href = "/pgadmissions/404";
            },
            400 : function() {
                window.location.href = "/pgadmissions/400";
            },
            403 : function() {
                window.location.href = "/pgadmissions/404";
            }
        },
        url : "/pgadmissions/interview/create_interviewer_section?applicationId=" + $('#applicationId').val(),
        success : function(data) {
            $('#createinterviewersection').html(data);
        },
        complete : function() {
            $('#ajaxloader').fadeOut('fast');
        }
    });
}

function addExistingUserToInterviewersLists(newInterviewer) {

    if ($('#applicationInterviewers option[value="' + newInterviewer.id + '"]').length > 0) {

        return;
    }

    if ($('#default option[value="' + newInterviewer.id + '"]').length > 0) {
        $('#default option[value="' + newInterviewer.id + '"]').attr("selected", 'selected');
        $('#addInterviewerBtn').trigger('click');

        return;
    }

    if ($('#previous option[value="' + newInterviewer.id + '"]').length > 0) {
        $('#previous option[value="' + newInterviewer.id + '"]').attr("selected", 'selected');
        $('#addInterviewerBtn').trigger('click');

        return;
    }

    $('#previous').append('<option value="' + newInterviewer.id + '" category="previous" disabled="disabled">' + newInterviewer.firstname + ' ' + newInterviewer.lastname + '</option>');
    $('#applicationInterviewers').append('<option value="' + newInterviewer.id + '">' + newInterviewer.firstname + ' ' + newInterviewer.lastname + '</option>');
    $('#applicationInterviewers').attr("size", $('#applicationInterviewers option').size() + 1);

}

function resetInterviwersErrors() {
    if ($('#applicationInterviewers option').size() > 0) {
        $('#interviewersErrorSpan').remove();
    }
}

function showProperInterviewArrangements() {
	var interviewStatus = $('input[name=interviewStatus]:radio:checked').val();
	
	$('.interview-happened').hide();
	$('.interview-scheduled').hide();
	$('.interview-to-schedule').hide();
	
	switch (interviewStatus) {
		case 'TAKEN_PLACE':
			$('.interview-happened').show();
			break;
		case 'SCHEDULED':
			$('.interview-scheduled').show();
			break;
		case 'SCHEDULING':
			$('.interview-to-schedule').show();
			setPossibleStartTimesVisibility($('#interviewPossibleStartTimes'));
			break;
	}
}