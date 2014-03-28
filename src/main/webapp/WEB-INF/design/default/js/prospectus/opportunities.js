var buttonText;

$(document).ready(function(){
	$.each($('[id^=placeholder-]'), function() {
		var feedId = $(this).attr('id').replace("placeholder-", "");
		getAdverts(feedId);
		setClass(feedId);
		$(window).bind('resize', function() { 
	    	setClass(feedId); 
	    });
	});
});

function setHsize(feedId) {
	 var container;
	 var paddings = 32;
	 var header = $('#placeholder-' + feedId + ' header').height();
	 var footer = $('#placeholder-' + feedId + ' footer').height();
	 var isEmbed = window != window.parent;
	 if (isEmbed) {
	 	container =  $(window).height();
	 } else {
	 	container =  $('#placeholder-' + feedId).parent().parent().height();
	 }
	 var sum = container - header - footer - paddings;
	 $('#list-' + feedId).height(sum);
}

function setClass(feedId) {
	if ($('#placeholder-' + feedId).width() < 390) {
		$('#placeholder-' + feedId).addClass('small');
		buttonText = 'Read More';
	} else {
		$('#placeholder-' + feedId).removeClass('small');
		buttonText = 'Apply Now';
	}
	setHsize(feedId);
}

function getAdverts(feedId){
	var selectedAdvertId = getUrlParam("advert");
	if (selectedAdvertId !== undefined) {
		selectedAdvertId = decodeURIComponent(selectedAdvertId);
	}
	var key = getUrlParam("feedKey");
	if (key == undefined) {
		key = $('#feedKey-' + feedId).val();
	}
	var value = getUrlParam("feedKeyValue");
	if (value == undefined) {
		value = $('#feedKeyValue-' + feedId).val();
	}
	var data = {
		feedKey: key,
		feedKeyValue: value, 
		advert: selectedAdvertId
	};
	$.ajax({
		type: 'GET',
		data: data,
		url: "/pgadmissions/opportunities/embedded",
		success: function(data) {
			var map = JSON.parse(data);
			processAdverts(map.adverts, feedId);
			bindAddThisShareOverFix();
		}
	});
}

function processAdverts(adverts, feedId){
	if (adverts.length == 0) {
		$('#placeholder-' + feedId).hide();
	}
	$('#list-' + feedId + ' > li').remove();
	$.each(adverts, function(index, advert){
		var advertElement = renderAdvert(advert);
		$('#list-' + feedId + ' ul').append(advertElement);
		bindAdvertApplyButton($('#ad-'+advert.id+' button.apply'), advert);
	});
}

function bindAdvertApplyButton(button, advert){
	button.click(function() {
    	$('#advert').val(advert.id);
    	$('#applyForm').submit();
   });
}

function bindAddThisShareOverFix(){
	$('.addthis_button_facebook').mousemove(function(e){
	    $('.at-quickshare').css({
	        'top': e.pageY ,
	        'left': e.pageX
	    });
	});
}

function renderAdvert(advert){
	var psupervisor = '';
	var ssupervisor = '';
	var funding = '';
	var selectedClass = (advert.selected) ? "selected" : "";
	if(advert.funding){
		funding = '<div class="fdescription"><p><strong>Funding Information</strong>: '+advert.funding+'</p></div>';
	}
	if(advert.advertType == 'PROGRAM') {
		psupervisor = '';
		ssupervisor = '';
	} else if (advert.advertType == 'PROJECT') {
		psupervisor = '<div class="psupervisor"><p>'+advert.primarySupervisorFirstName + ' ' + advert.primarySupervisorLastName + ' (PI)';
		if (advert.secondarySupervisorFirstname != undefined) {
			ssupervisor = '<span class="ssupervisor">, '+ advert.secondarySupervisorFirstName + ' ' + advert.secondarySupervisorLastName+'</span></p></div>'; 
		} else {
			ssupervisor = '</p></div>';
		}
		
	}
	var studyDuration = durationOfStudyString(advert.studyDuration);
	var addThisDescription =  studyDuration.replace("s", "").toLowerCase()+" research study programme delivered by UCL Engineering at London's global University. " +
		"Click to find out more about the programme and apply for your place.";
	
	if ($('#pContainer').length > 0) {
		popupbuttons = '<a class="addthis_button_facebook" addthis:url="'+getAdvertUrl(advert)+'" addthis:title="'+advert.title+'" addthis:description="'+addThisDescription+'"></a>'+
			'<a class="addthis_button_twitter" addthis:url="'+getAdvertUrl(advert)+'" addthis:title="'+advert.title+'" addthis:description="'+addThisDescription+'"></a>'+
			'<a class="addthis_button_google_plusone_share"></a>'+
			'<a class="addthis_button_linkedin" addthis:url="'+getAdvertUrl(advert)+'" addthis:title="'+advert.title+'" addthis:description="'+addThisDescription+'"></a>'+
			'<a class="addthis_button_expanded"></a>'+
			'<a class="addthis_counter addthis_bubble_style"></a>'+
			'<script type="text/javascript" src="s7.addthis.com/js/300/addthis_widget.js#pubid=xa-51af252068c85125"></script>';
	} else {
		popupbuttons = '<a href="api.addthis.com/oexchange/0.8/offer?url='+getAdvertUrl(advert)+'&title='+advert.title+'" target="_blank" title="View more services"><img src="//s7.addthis.com/static/btn/v2/lg-share-en.gif" alt="Share"/></a>';
	}
	
	return '<li class="'+ advert.advertType.toLowerCase() +' item '+ selectedClass +'" id="ad-'+advert.id+'">'+
		'<div class="pdetails clearfix">'+
			'<h3>'+advert.title+'</h3>'+
			psupervisor +
			ssupervisor +
			'<div class="pdescription"><p>'+advert.description+'</p></div>'+
			funding	+	
			'<div class="cdate">'+closingDateString(advert.closingDate)+'</div>'+
			'<div class="duration">Study duration: <span>'+ studyDuration +'</span></div>'+
		'</div>'+
		'<div class="pactions clearfix">'+
			'<div class="social">'+
				'<!-- AddThis Button BEGIN -->'+
				'<div class="addthis_toolbox addthis_default_style addthis_16x16_style" addthis:url="'+getAdvertUrl(advert)+'" addthis:title="'+advert.title+'" addthis:description="'+addThisDescription+'">'+
				popupbuttons +
				'</div>'+
				'<!-- AddThis Button END -->'+
			'</div>'+
			'<div class="applyBox">'+
				'<a href="mailto:'+advert.primarySupervisorEmail+'?subject=Question About:'+advert.title+'" class="question">Enquire</a>'+ 
				'<button id="'+advert.id+'" class="btn btn-primary apply">'+buttonText+'</button>' +
			'</div>'+
		'</div>'+
	'</li>';
}

function closingDateString(closingDate){
	if(closingDate == null){
		return "No closing date";
	}
	return 'Closing date: <span>'+formatDate(closingDate)+'</span>';
}

function formatDate(dateString) {
	var months = ['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'];
	var formattedDate = new Date(dateString);
	var d = formattedDate.getDate();
	var m =  formattedDate.getMonth();
	var y = formattedDate.getFullYear();
	
	return d + " " + months[m] + " " + y;
}

function durationOfStudyString(studyDuration){
	if(studyDuration=='undefined'){
		return "---";
	}
	var normalizedDuration = studyDuration;
	var normalizedUnit = "Month";
	if(studyDuration%12==0){
		normalizedDuration = studyDuration/12;
		normalizedUnit = "Year";
	}
	var pluralSuffix= (normalizedDuration>1)?"s":"";
	return normalizedDuration+" "+ normalizedUnit+ pluralSuffix;
}

function getAdvertUrl (advert) {
	return window.location.protocol +"//" +window.location.host + '/pgadmissions/register' + "?advert=" + advert.id;
}

function getUrlParam(name) {
    var results = new RegExp('[\\?&;]' + name + '=([^&;#]*)').exec(window.location.href);
    if(results!=null && results.length>0){
    	return results[1];	
    }
    return undefined;
}