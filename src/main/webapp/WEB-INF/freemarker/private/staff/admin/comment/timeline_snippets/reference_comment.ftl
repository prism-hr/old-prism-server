<#assign role = "referee"/>
<#if !timelineObject.referee.declined>
	<#assign comment = timelineObject.referee.reference/>
</#if>    
<ul>            
	<li>                          
		<div class="box">
			<div class="title">
				<span class="icon-role ${role}" data-desc="${role?cap_first}"></span>
				<span class="name">${(timelineObject.referee.user.firstName?html)!} ${(timelineObject.referee.user.lastName?html)!}</span>
				<span class="datetime">${timelineObject.eventDate?string('dd MMM yy')} at ${timelineObject.eventDate?string('HH:mm')}</span>
			</div>	            
			<#if timelineObject.referee.declined>
				<p><em>${timelineObject.referee.user.firstName?html} ${timelineObject.referee.user.lastName?html} declined to act as referee</em></p>
			<#else>  	          
				<p><em>${(timelineObject.referee.reference.comment?html)!}</em></p>
					<#if timelineObject.referee.reference.documents?? && timelineObject.referee.reference.documents?size &gt; 0>
						<ul class="uploads">                
							<#list timelineObject.referee.reference.documents as document>
								<li><a class="uploaded-filename" href="<@spring.url '/download?documentId=${encrypter.encrypt(document.id)}'/>" target="_blank">${document.fileName?html}</a></li>
							</#list>
							</ul>
					</#if>
					<h3 class="answer <#if comment.suitableForUCL?? && comment.suitableForUCL>yes<#else>no</#if>">
						<span data-desc="<#if comment.suitableForUCL?? && comment.suitableForUCL>Yes<#else>No</#if>"></span> Is the applicant suitable for postgraduate study at UCL?
					</h3>
					<h3 class="answer <#if comment.suitableForProgramme?? && comment.suitableForProgramme>yes<#else>no</#if>">
						<span data-desc="<#if comment.suitableForProgramme?? && comment.suitableForProgramme>Yes<#else>No</#if>"></span> Is the applicant suitable for their chosen postgraduate study programme?
					</h3>
			</#if>    	        
		</div>
	</li>                
</ul>