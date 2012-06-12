<#if comment.decline>
<p>Declined to act as reviewer on this occasion.</p>
<#else>
<h3>Willing to interview?</h3>
<ul>
	<li<#if comment.willingToInterview?? && comment.willingToInterview?string != 'true'> class="grey-label"</#if>>Yes</li>
	<li<#if comment.willingToInterview?? && comment.willingToInterview?string != 'false'> class="grey-label"</#if>>No</li>
</ul>

<h3>Is candidate suitable for UCL?</h3>
<ul>
	<li<#if comment.suitableCandidate?? && comment.suitableCandidate?string != 'true'> class="grey-label"</#if>>Yes</li>
	<li<#if comment.suitableCandidate?? && comment.suitableCandidate?string != 'false'> class="grey-label"</#if>>No</li>
</ul>
</#if>