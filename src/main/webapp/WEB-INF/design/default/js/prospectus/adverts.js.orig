$(document).ready(function(){
	hideAdverts();
	getAdverts();
});

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
		url: "/pgadmissions/adverts/activeAdverts",
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

function processAdverts(adverts){
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
	var selectedClass = (advert.selected) ? "selected" : "";
	return '<li class="'+ advert.type+' item '+ selectedClass +'" id="ad-'+advert.id+'">'+
	'<div class="pdetails">'+
		'<h3>'+advert.title+'</h3>'+
		'<div class="cdate">'+closingDateString(advert.closingDate)+'</div>'+
		'<div class="duration">Study duration: <span>'+durationOfStudyString(advert.studyDuration)+'</span></div>'+
		'<div class="pdescription"><p>'+advert.description+'</p></div>'+
		'<div class="fdescription"><p>'+advert.funding+'</p></div>'+
	'</div>'+
	'<div class="pactions clearfix">'+
		'<div class="social">'+
			'<!-- AddThis Button BEGIN -->'+
			'<div class="addthis_toolbox addthis_default_style addthis_16x16_style" addthis:description="'+advert.description+'" addthis:title="'+advert.title+'" addthis:url="'+getAdvertUrl(advert.id)+'">'+
			'<a class="addthis_button_facebook"></a>'+
			'<a class="addthis_button_twitter"></a>'+
			'<a class="addthis_button_google_plusone_share"></a>'+
			'<a class="addthis_button_linkedin"></a>'+
			'<a class="addthis_button_expanded"></a>'+
			'<a class="addthis_counter addthis_bubble_style"></a>'+
			'</div>'+
			'<script type="text/javascript" src="http://s7.addthis.com/js/300/addthis_widget.js#pubid=xa-51af252068c85125"></script>'+
			'<!-- AddThis Button END -->'+
		'</div>'+
		'<div class="applyBox">'+
			'<a href="mailto:'+advert.email+'?subject=Question About:'+advert.title+'" class="question">Ask a question</a>'+
			'<button id="'+advert.programCode+'" class="btn btn-primary apply">Apply Now</button>'+
			'<a href="'+getAdvertUrl(advert.id)+'" class="btn btn-primary readmore">Read More</a>'+
		'</div>'+
	'</div>'+
'</li>';
}

function closingDateString(closingDate){
	if(closingDate === undefined || closingDate=="undefined"){
		return "No closing date";
	}
	return 'Closing date: <span>'+formatDate(closingDate)+'</span>';
}

function formatDate(dateString) {
	return $.datepicker.formatDate('dd M yy', new Date(dateString));
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
	return window.location.protocol +"//" +window.location.host + window.location.pathname + "?advert="+advertId;
}

function getUrlParam(name){
    var results = new RegExp('[\\?&amp;]' + name + '=([^&amp;#]*)').exec(window.location.href);
    if(results!=null && results.length>0){
    	return results[1];	
    }
    return undefined;
}