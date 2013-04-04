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
	$(document).ready(function() {

		var $fname = $("#newInterviewerFirstName");
		var $lname = $("#newInterviewerLastName");
		var $email = $("#newInterviewerEmail");

		// Firstname autocomplete//
	    $fname.typeaheadmap({
		source : function (query, process) {
                    return $.getJSON("/pgadmissions/user/autosuggest/firstname/" + $fname.val(), function (data) {
                        return process(data);
                    });
		},
		matcher : function() { return true},
		key: "k",
		value: "v",
		email: "d",
		items: 8,
		listener: function(k, v, d) {
		    $lname.val(v);
		    $email.val(d);  
		},
		displayer: function(that, item, highlighted) {
			var allquery = highlighted+' '+item[that.value]+' ('+item[that.email]+')';
		    if (that.value != "") {	
			    return allquery;	
		    } else {
		    return highlighted + ' (' + item[that.value] + ' )' 
		    }
		}
	    })
	    
		// Lastname autocomplete//
	    $lname.typeaheadmap({
		source : function (query, process) {
                    return $.getJSON("/pgadmissions/user/autosuggest/lastname/" + $lname.val(), function (data) {
                        return process(data);
                    });
        },
        matcher : function() { return true},
		key : "v",
		value : "k",
		email : "d",
		items: 8,
		listener : function(k, v, d) {
		    $fname.val(v);
			$email.val(d);
		},
		displayer: function(that, item, highlighted) {
			var allquery = item[that.value]+' '+highlighted+' ('+item[that.email]+')';
		    if (that.value != "") {
			    return allquery;			
		    } else {
		    return highlighted + ' (' + item[that.value] +' )' 
		    } 
		   }
	    })
		
		// Email autocomplete//
	    $email.typeaheadmap({
		source : function (query, process) {
                    return $.getJSON("/pgadmissions/user/autosuggest/email/" + $email.val(), function (data) {
                        return process(data);
                    });
        },
        matcher : function() { return true},
		key : "d",
		value : "v",
		email : "k",
		items: 8,
		listener : function(k, v, d) {
		    $fname.val(d);
			$lname.val(v);
		},
		displayer: function(that, item, highlighted) {
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
