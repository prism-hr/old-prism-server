<#if comment.decline>
<p>Declined to review.</p>
<#else>
<h3>Willing to interview?</h3>
<p>
	<label><input type="radio" disabled="disabled" <#if comment.willingToInterview?? && comment.willingToInterview?string == 'true'>checked="checked"</#if>/> Yes</label>
	<label><input type="radio" disabled="disabled" <#if comment.willingToInterview?? && comment.willingToInterview?string == 'false'>checked="checked"</#if>/> No</label>
</p>

<h3>Is candidate suitable for UCL?</h3>
<p>
	<label><input type="radio" disabled="disabled" <#if comment.suitableCandidate?? && comment.suitableCandidate?string == 'true'>checked="checked"</#if>/> Yes</label>
	<label><input type="radio" disabled="disabled" <#if comment.suitableCandidate?? && comment.suitableCandidate?string == 'false'>checked="checked"</#if>/> No</label>								
</p>
</#if>