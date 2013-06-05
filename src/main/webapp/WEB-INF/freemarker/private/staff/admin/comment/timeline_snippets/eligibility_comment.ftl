<#assign role = "admitter"/>
<#setting locale = "en_US">
<#assign comment = timelineObject.comment/>
<ul>            
	<li>                          
		<div class="box">
               <div class="title">
		       <span class="icon-role ${role}" data-desc="${(comment.getTooltipMessage(role)?html)!}"></span>
		       <span class="name">${(comment.user.firstName?html)!} ${(comment.user.lastName?html)!}</span> <span class="commented">commented:</span>
		       <span class="datetime" data-desc="Date">${comment.date?string('dd MMM yy')} at ${comment.date?string('HH:mm')}</span>
		     </div>
		     <div class="textContainer"><p><em>${(comment.comment?html?replace("\n", "<br>"))!}</em></p></div>
		     
		     <#if comment.documents?? && comment.documents?size &gt; 0>
	  		   <ul class="uploads">                
	  		     <#list comment.documents as document>
	  		       <li><a class="uploaded-filename" href="<@spring.url '/download?documentId=${encrypter.encrypt(document.id)}'/>" target="_blank">${document.fileName?html}</a></li>
	  			 </#list>
	  		   </ul>
	  	     </#if>			
			 <h3 class="answer <#if comment.qualifiedForPhd??>${comment.qualifiedForPhd?string?lower_case}</#if>">
				<span data-desc="<#if comment.qualifiedForPhd??>${comment.qualifiedForPhd?capitalize}<#else>Unsure</#if>"></span>Is the applicant qualified for PhD entry to UCL?
			</h3>
	
			<h3 class="answer <#if comment.englishCompentencyOk??>${comment.englishCompentencyOk?string?lower_case}</#if>">
				<span data-desc="<#if comment.englishCompentencyOk??>${comment.englishCompentencyOk?capitalize}<#else>Unsure</#if>"></span>Does the applicant meeting the minimum required standard of English Language competence?
			</h3>
	
			<h3 class="answer <#if comment.homeOrOverseas??>${comment.homeOrOverseas?string?lower_case}</#if>">
				<span data-desc="<#if comment.homeOrOverseas??>${comment.homeOrOverseas?capitalize}<#else>Unsure</#if>"></span>What is the applicant's fee status?
			</h3>
		</div>
	</li>                
</ul>