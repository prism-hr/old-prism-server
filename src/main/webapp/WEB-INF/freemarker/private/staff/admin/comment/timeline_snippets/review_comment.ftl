<#if comment.decline>
<p>Declined to act as reviewer on this occasion.</p>
<#else>
<h3 class="answer <#if comment.willingToInterview?? && comment.willingToInterview?string == 'true'>yes<#else>no</#if>">
	<span></span> Willing to interview?
</h3>

<h3 class="answer <#if comment.suitableCandidate?? && comment.suitableCandidate?string == 'true'>yes<#else>no</#if>">
	<span></span> Is candidate suitable for UCL?
</h3>
</#if>