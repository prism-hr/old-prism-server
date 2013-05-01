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
  <label id="delegateFirstNameLabel" class="plain-label normal" for="delegateFirstName">Delegate First Name<em>*</em></label>
  <span class="hint" data-desc="<@spring.message 'validateApp.delegateFirstName'/>"></span>
  <div class="field">
    <input class="full" type="text" name="delegateFirstName" id="delegateFirstName"  
    <#if delegatedInterviewer?? && delegatedInterviewer.firstName??>value="${delegatedInterviewer.firstName}"</#if>/>
  </div>
</div>
<div class="row">
  <label id="delegateLastNameLabel" class="plain-label normal" for="delegateLastName">Delegate Last Name<em>*</em></label>
  <span class="hint" data-desc="<@spring.message 'validateApp.delegateLastName'/>"></span>
  <div class="field">
    <input class="full" type="text" name="delegateLastName" id="delegateLastName" 
    <#if delegatedInterviewer?? && delegatedInterviewer.lastName??>value="${delegatedInterviewer.lastName}"</#if>/>
  </div>
</div>
<div class="row">
  <label id="delegateEmailLabel" class="plain-label normal" for="delegateEmail">Delegate Email Address<em>*</em></label>
  <span class="hint" data-desc="<@spring.message 'validateApp.delegateEmail'/>"></span>
  <div class="field">
    <input class="full" type="email"  name="delegateEmail" id="delegateEmail" 
    <#if delegatedInterviewer?? && delegatedInterviewer.email??>value="${delegatedInterviewer.email}"</#if>/>
  </div>
</div>
<script type="text/javascript" src="<@spring.url '/design/default/js/autosuggest.js'/>"></script>
<script type="text/javascript">
  $(document).ready(function() {
        autosuggest($("#delegateFirstName"), $("#delegateLastName"), $("#delegateEmail"));
  });
</script>