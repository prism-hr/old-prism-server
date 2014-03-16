<li>                          
	<div class="box">
		<div class="title">
	    	<span class="icon-role ${role}" data-desc="${(comment.getTooltipMessage(role)?html)!}"></span>
	    	<span class="name">${(comment.user.firstName?html)!} ${(comment.user.lastName?html)!}</span> <span class="commented">commented:</span>
	    	<span class="datetime">${comment.date?string('dd MMM yy')} at ${comment.date?string('HH:mm')}</span>
	  	</div>
	  	<h3 class="answer <#if comment.transferSucceeded?? && comment.transferSucceeded?string == 'true'>yes<#else>no</#if>">
  			<span data-desc="<#if comment.transferSucceeded?? && comment.transferSucceeded?string == 'true'>Yes<#else>No</#if>"></span>
  			<#if comment.transferSucceeded?? && comment.transferSucceeded?string == 'true'>
  				Application export succeeded.
  			<#else>
  				Application export failed. Download the <a href='/pgadmissions/download/transferErrorReport?transferErrorId=${comment.applicationFormTransferError.id?c}' target='_blank'>error report</a>.
  			</#if>
  		</h3>
	</div>
</li>