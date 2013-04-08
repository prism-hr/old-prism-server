<div class="row">
  <label class="plain-label normal" for="newInterviewerFirstName">Delegate First Name<em>*</em></label>
  <span class="hint" data-desc=""></span>
  <div class="field">
    <input class="full" type="text" name="newInterviewerFirstName" id="newInterviewerFirstName"  value=""/>
  </div>
</div>
<div class="row">
  <label class="plain-label normal" for="newInterviewerLastName">Delegate Last Name<em>*</em></label>
  <span class="hint" data-desc=""></span>
  <div class="field">
    <input class="full" type="text" name="newInterviewerLastName" id="newInterviewerLastName" value=""/>
  </div>
</div>
<div class="row">
  <label class="plain-label normal" for="newInterviewerEmail">Delegate Email Address<em>*</em></label>
  <span class="hint" data-desc=""></span>
  <div class="field">
    <input class="full" type="email"  name="newInterviewerEmail" id="newInterviewerEmail" value=""/>
  </div>
</div>
<script type="text/javascript" src="<@spring.url '/design/default/js/autosuggest.js'/>"></script>
<script type="text/javascript">
  $(document).ready(function() {
        autosuggest($("#newInterviewerFirstName"), $("#newInterviewerLastName"), $("#newInterviewerEmail"));
  });
</script>