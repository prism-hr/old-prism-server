<div class="row">
    <label id="fastTraclLabel" class="plain-label normal">Do you wish to fast-track this application?<em>*</em></label> 
    <span class="hint" data-desc="<@spring.message 'validateApp.fastTrack'/>"> </span>
    <div class="field">
        <input id="fastTrackProcessing" type="radio" name="fastTrackProcessing" value="no" />
            No
        <input type="radio" name="fastTrackProcessing" value="yes" />
            Yes 
        <#if fastTrackMissing??>
        	<div class="alert alert-error">
        		<i class="icon-warning-sign"></i>
        		You must make a selection.
        	</div>
        </#if>
    </div>
</div>