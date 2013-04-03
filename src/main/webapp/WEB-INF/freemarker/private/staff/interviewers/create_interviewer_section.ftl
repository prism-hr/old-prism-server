<#import "/spring.ftl" as spring />
<h3>Create New Interviewer</h3>
<div class="row">
  <label class="plain-label normal" for="newInterviewerFirstName">Interviewer First Name<em>*</em></label>
  <span class="hint" data-desc="<@spring.message 'assignInterviewer.firstName'/>"></span>
  <div class="field">
    <input class="full" type="text" name="newInterviewerFirstName" id="newInterviewerFirstName"  value="${(interviewer.firstName?html)!}"/>
    <@spring.bind "interviewer.firstName" />
    <#list spring.status.errorMessages as error>
    <div class="alert alert-error"> <i class="icon-warning-sign"></i>
      ${error}
    </div>
    </#list> </div>
</div>
<div class="row">
  <label class="plain-label normal" for="newInterviewerLastName">Interviewer Last Name<em>*</em></label>
  <span class="hint" data-desc="<@spring.message 'assignInterviewer.lastName'/>"></span>
  <div class="field">
    <input class="full" type="text" name="newInterviewerLastName" id="newInterviewerLastName" value="${(interviewer.lastName?html)!}"/>
    <@spring.bind "interviewer.lastName" />
    <#list spring.status.errorMessages as error>
    <div class="alert alert-error"> <i class="icon-warning-sign"></i>
      ${error}
    </div>
    </#list> </div>
</div>
<div class="row">
  <label class="plain-label normal" for="newInterviewerEmail">Interviewer Email Address<em>*</em></label>
  <span class="hint" data-desc="<@spring.message 'assignInterviewer.email'/>"></span>
  <div class="field">
    <input class="full" type="email"  name="newInterviewerEmail" id="newInterviewerEmail" value="${(interviewer.email?html)!}"/>
    <@spring.bind "interviewer.email" />
    <#list spring.status.errorMessages as error>
    <div class="alert alert-error"> <i class="icon-warning-sign"></i>
      ${error}
    </div>
    </#list> </div>
</div>
<div class="row">
  <div class="field">
    <button class="btn" type="button" id="createInterviewer">Add</button>
  </div>
</div>
<script type="text/javascript">
// k:name, v:surname, d:email
var listofcapitals = [{"k": "Juan","v" :"Mingo", "d":"shexpire@hotmail.com"},
                { "k":"Abuja","v" :"Nigeria", "d":"chel@hotmail.com"},
{  "k":"Accra","v" :"Ghana", "d":"f2@hotmail.com", "id": "100"},
{  "k":"Adamstown","v" :"Pitcairn Islands", "d":"bat@hotmail.com"},
{  "k":"Addis Ababa","v" :"Ethiopia", "d":"sdfe@hotmail.com"},];


	$(document).ready(function() {

		
		var $fname = $("#newInterviewerFirstName");
		var $lname = $("#newInterviewerLastName");
		var $email = $("#newInterviewerEmail");

		// Fname autocomplete//
	    $fname.typeaheadmap({
		"source" : listofcapitals,
		"key" : "k",
		"value" : "v",
		"email" : "d",
		"items": 8,
		"listener" : function(k, v, d) {
		    $lname.val(v);
		    $email.val(d);  
		},
		"displayer": function(that, item, highlighted) {
			var allquery = highlighted+' '+item[that.value]+' ('+item[that.email]+')';
		    if (that.value != "") {	
			    return allquery;	
		    } else {
		    return highlighted + ' (' + item[that.value] + ' )' 
		    }
		}
	    })
		// Surname autocomplete//
	    $lname.typeaheadmap({
		"source" : function(q, process) { process(listofcapitals)},
		"key" : "v",
		"value" : "k",
		"email" : "d",
		"items": 8,
		"listener" : function(k, v, d) {
		    $fname.val(v);
			$email.val(d);
		},
		"displayer": function(that, item, highlighted) {
			var allquery = item[that.value]+' '+highlighted+' ('+item[that.email]+')';
		    if (that.value != "") {
			    return allquery;			
		    } else {
		    return highlighted + ' (' + item[that.value] +' )' 
		    } 
		   }
	    })
		// Fname autocomplete//
	    $email.typeaheadmap({
		"source" : function(q, process) { process(listofcapitals)},
		"key" : "d",
		"value" : "v",
		"email" : "k",
		"items": 8,
		"listener" : function(k, v, d) {
		    $fname.val(d);
			$lname.val(v);
		},
		"displayer": function(that, item, highlighted) {
			var allquery = item[that.email]+' '+item[that.value]+' ('+highlighted+')';
		    if (that.value != "") {
			    return allquery;			
		    } else {
		    return highlighted + ' (' + item[that.value] + ' )' ;
			
		    }
		}
	    })
		
	});
    </script>
