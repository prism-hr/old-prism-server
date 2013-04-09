<div class="row">
	<label id="delegateLabel" class="plain-label normal">Delegate interview administration<em>*</em></label> 
	<span class="hint" data-desc="<@spring.message 'validateApp.delegate'/>"> </span>
    <div class="field">
    	<input id="delegateProcessing" type="radio" name="switch" value="no"
        	<#if delegate?? && !delegate>checked="checked"</#if> />
               No
        <input type="radio" name="switch" value="yes" 
            <#if delegate?? && delegate>checked="checked"</#if> />
               Yes 
	</div>
</div>

<div class="row">
  <label id="delegateFirstNameLabel" class="plain-label normal" for="newInterviewerFirstName">Delegate First Name<em>*</em></label>
  <span class="hint" data-desc="<@spring.message 'validateApp.delegateFirstName'/>"></span>
  <div class="field">
    <input class="full" type="text" name="newInterviewerFirstName" id="newInterviewerFirstName"  
    <#if delegatedInterviewer?? && delegatedInterviewer.firstName??>value="${delegatedInterviewer.firstName}"</#if>/>
  </div>
</div>
<div class="row">
  <label id="delegateLastNameLabel" class="plain-label normal" for="newInterviewerLastName">Delegate Last Name<em>*</em></label>
  <span class="hint" data-desc="<@spring.message 'validateApp.delegateLastName'/>"></span>
  <div class="field">
    <input class="full" type="text" name="newInterviewerLastName" id="newInterviewerLastName" 
    <#if delegatedInterviewer?? && delegatedInterviewer.lastName??>value="${delegatedInterviewer.lastName}"</#if>/>
  </div>
</div>
<div class="row">
  <label id="delegateEmailLabel" class="plain-label normal" for="newInterviewerEmail">Delegate Email Address<em>*</em></label>
  <span class="hint" data-desc="<@spring.message 'validateApp.delegateEmail'/>"></span>
  <div class="field">
    <input class="full" type="email"  name="newInterviewerEmail" id="newInterviewerEmail" 
    <#if delegatedInterviewer?? && delegatedInterviewer.email??>value="${delegatedInterviewer.email}"</#if>/>
  </div>
</div>
<script type="text/javascript" src="<@spring.url '/design/default/js/autosuggest.js'/>"></script>
<script type="text/javascript">
  $(document).ready(function() {
        autosuggest($("#newInterviewerFirstName"), $("#newInterviewerLastName"), $("#newInterviewerEmail"));
  });
</script>