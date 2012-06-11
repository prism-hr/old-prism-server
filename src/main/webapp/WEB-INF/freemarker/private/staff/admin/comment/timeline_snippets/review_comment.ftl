<#if comment.decline>
<p>Declined to review.</p>
<#else>
<p>Willing to interview?</p>
	<input type="radio" disabled="disabled" <#if comment.willingToInterview?? && comment.willingToInterview?string == 'true'>checked="checked"</#if>/><label>Yes</label>
	<input type="radio" disabled="disabled" <#if comment.willingToInterview?? && comment.willingToInterview?string == 'false'>checked="checked"</#if>/><label>No</label>								
</p>
<p>Is candidate suitable for UCL?</p>
	<input type="radio" disabled="disabled" <#if comment.suitableCandidate?? && comment.suitableCandidate?string == 'true'>checked="checked"</#if>/><label>Yes</label>
	<input type="radio" disabled="disabled" <#if comment.suitableCandidate?? && comment.suitableCandidate?string == 'false'>checked="checked"</#if>/><label>No</label>								
</p>
</#if>