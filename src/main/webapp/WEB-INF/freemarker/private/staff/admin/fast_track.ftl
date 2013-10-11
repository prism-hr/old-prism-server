<div class="row">
    <label id="fastTraclLabel" class="plain-label normal">Do you wish to fast-track this application?<em>*</em></label> 
    <span class="hint" data-desc="<@spring.message 'validateApp.fastTrack'/>"> </span>
    <div class="field">
        <input id="fastTrackProcessing" type="radio" name="fastTrackProcessing" value="no" <#if comment.fastTrackApplication?? && !comment.fastTrackApplication> checked="checked"</#if>/>
            No
        <input type="radio" name="fastTrackProcessing" value="yes" <#if comment.fastTrackApplication?? && comment.fastTrackApplication> checked="checked"</#if>/>
            Yes 
            
	    <@spring.bind "comment.fastTrackApplication" /> 
		<#list spring.status.errorMessages as error>  
			<div class="alert alert-error"> <i class="icon-warning-sign"></i>
            ${error}
          </div>
        </#list>
    </div>
</div>