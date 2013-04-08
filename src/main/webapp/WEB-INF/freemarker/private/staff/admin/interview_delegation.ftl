<div class="row">
  <label class="plain-label normal" for="newInterviewerFirstName">Delegate First Name<em>*</em></label>
  <span class="hint" data-desc=""></span>
  <div class="field">
    <input class="full" type="text" name="newInterviewerFirstName" id="newInterviewerFirstName"  
    <#if delegatedInterviewer?? && delegatedInterviewer.firstName??>value="${delegatedInterviewer.firstName}"</#if>/>
  </div>
</div>
<div class="row">
  <label class="plain-label normal" for="newInterviewerLastName">Delegate Last Name<em>*</em></label>
  <span class="hint" data-desc=""></span>
  <div class="field">
    <input class="full" type="text" name="newInterviewerLastName" id="newInterviewerLastName" 
    <#if delegatedInterviewer?? && delegatedInterviewer.lastName??>value="${delegatedInterviewer.lastName}"</#if>/>
  </div>
</div>
<div class="row">
  <label class="plain-label normal" for="newInterviewerEmail">Delegate Email Address<em>*</em></label>
  <span class="hint" data-desc=""></span>
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