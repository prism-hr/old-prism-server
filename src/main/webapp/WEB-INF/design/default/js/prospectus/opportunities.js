$(document).ready(function(){
	var buttonText;
	hideAdverts();
	getAdverts();
	setClass();
	$(window).bind('resize', function() { 
    	setClass(); 
    });
});
function setHsize() {
	 var paddings = 32;
	 var header = $('#pholder header').height();
	 var footer = $('#pholder footer').height();
	 var isEmbed = window != window.parent;
	 if (isEmbed) {
	 	var container =  $(window).height();
	 } else {
	 	var container =  $('#pholder').parent().parent().height();
	 }
	 var sum = container - header - footer - paddings;
	 $('#plist').height(sum);
}
function setClass() {
	if ($('#pholder').width() < 390) {
		$('#pholder').addClass('small');
		buttonText = 'Read More';
	} else {
		$('#pholder').removeClass('small');
		buttonText = 'Apply Now';
	}
	setHsize();
}
function hideAdverts(){
	$('#pHolder').hide();
}

function showAdverts(){
	$('#pHolder').show();	
}

function getAdverts(){
	var selectedAdvertId= getUrlParam("advert");
	if(selectedAdvertId !== undefined && selectedAdvertId != "undefined"){
		selectedAdvertId = decodeURIComponent(selectedAdvertId);
	}
	var feedId = $('#feedId').val();
	var user = $('#user').val();
	var upi = $('#upi').val();
	if (feedId != undefined || user != undefined || upi != undefined) {
		var data = {
			feedId : feedId,
			user : user,
			upi : upi
		};
		// standalone adverts for feed
		$.ajax({
			type: 'GET',
			data: data,
			url: "/pgadmissions/opportunities/feeds",
			success: function(data) {
				processAdverts(data.adverts);
				highlightSelectedAdvert();
				bindAddThisShareOverFix();
			}
		});
	} else {
		$.ajax({
			type: 'GET',
			statusCode: {
				401: function() { window.location.reload(); },
				500: function() { window.location.href = "/pgadmissions/error"; },
				404: function() { window.location.href = "/pgadmissions/404"; },
				400: function() { window.location.href = "/pgadmissions/400"; },                  
				403: function() { window.location.href = "/pgadmissions/404"; }
			},
			data: {
				advert: selectedAdvertId,
	        }, 
			url: "/pgadmissions/opportunities/activeOpportunities",
			success: function(data) {
				var map = JSON.parse(data);
				processAdverts(map.adverts);
				highlightSelectedAdvert();
				bindAddThisShareOverFix();
			},
			complete: function() {
			}
		});
		
	}
}

function processAdverts(adverts){
	// hide if no adverts in the feed
	if (adverts.length == 0) {
		$('#pholder').hide();
	}
	$('#plist > li').remove();
	$.each(adverts, function(index, advert){
		var advertElement = renderAdvert(advert);
		$('#plist ul').append(advertElement);
		bindAdvertApplyButton($('#ad-'+advert.id+' button.apply'), advert);
	});
}

function highlightSelectedAdvert(){
	var selectedAdvertId=$('#prospectusSelectedAdvert');
	if(selectedAdvertId){
		var selectedAdvert=$('#ad-'+selectedAdvertId.val());
		selectedAdvert.addClass('selected');
		selectedAdvert.parent().prepend(selectedAdvert);
	}
}

function bindAdvertApplyButton(button,advert){
	button.click(function() {
    	$('#program').val(advert.programCode);
    	$('#advert').val(advert.id);
    	if(advert.projectId){
    		$('#project').val(advert.projectId);
    	}
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
	if(advert.type == 'program') {
		psupervisor = '';
		ssupervisor = '';
	} else if (advert.type == 'project') {
		psupervisor = '<div class="psupervisor"><p>'+advert.primarySupervisor.firstname + ' ' + advert.primarySupervisor.lastname + ' (PI)';
		if (advert.secondarySupervisorSpecified) {
			ssupervisor = '<span class="ssupervisor">, '+ advert.secondarySupervisor.firstname + ' ' + advert.secondarySupervisor.lastname+'</span></p></div>'; 
		} else {
			ssupervisor = '</p></div>';
		}
		
	}
	if ($('#pContainer').length > 0) {
		popupbuttons = '<a class="addthis_button_facebook"></a>'+
			'<a class="addthis_button_twitter"></a>'+
			'<a class="addthis_button_google_plusone_share"></a>'+
			'<a class="addthis_button_linkedin"></a>'+
			'<a class="addthis_button_expanded"></a>'+
			'<a class="addthis_counter addthis_bubble_style"></a>'+
			'<script type="text/javascript" src="http://s7.addthis.com/js/300/addthis_widget.js#pubid=xa-51af252068c85125"></script>'
	} else {
		popupbuttons = '<a href="http://api.addthis.com/oexchange/0.8/offer?url='+getAdvertUrl(advert.id)+'&title='+advert.title+'" target="_blank" title="View more services"><img src="//s7.addthis.com/static/btn/v2/lg-share-en.gif" alt="Share"/></a>'
	}
	return '<li class="'+ advert.type+' item '+ selectedClass +'" id="ad-'+advert.id+'">'+
	'<div class="pdetails clearfix">'+
		'<h3>'+advert.title+'</h3>'+
		psupervisor +
		ssupervisor +
		'<div class="pdescription"><p>'+advert.description+'</p></div>'+
		funding	+	
		'<div class="cdate">'+closingDateString(advert.closingDate)+'</div>'+
		'<div class="duration">Study duration: <span>'+durationOfStudyString(advert.studyDuration)+'</span></div>'+
	'</div>'+
	'<div class="pactions clearfix">'+
		'<div class="social">'+
			'<!-- AddThis Button BEGIN -->'+
			'<div class="addthis_toolbox addthis_default_style addthis_16x16_style" addthis:description="'+advert.description+'" addthis:title="'+advert.title+'" addthis:url="'+getAdvertUrl(advert.id)+'">'+
			popupbuttons +
			'</div>'+
			'<!-- AddThis Button END -->'+
		'</div>'+
		'<div class="applyBox">'+
			'<a href="mailto:'+advert.email+'?subject=Question About:'+advert.title+'" class="question">Ask a question</a>'+
			'<button id="'+advert.programCode+'" class="btn btn-primary apply">'+buttonText+'</button>'+
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

function getAdvertUrl(advertId){
	return window.location.protocol +"//" +window.location.host + '/pgadmissions/register' + "?advert="+advertId;
}

function getUrlParam(name){
    var results = new RegExp('[\\?&amp;]' + name + '=([^&amp;#]*)').exec(window.location.href);
    if(results!=null && results.length>0){
    	return results[1];	
    }
    return undefined;
}