<#import "/spring.ftl" as spring />
<!-- Styles for Application List Page -->
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/global_private.css' />"/>
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/staff/timeline.css' />"/>


 <table>
         	<colgroup>
              	<col style="width: 100px" />
              	<col style="width: 100px" />
              	<col style="width: 100px" />
              	<col style="width: 200px" />
              	<col />          
              	<col />
              	<col  />
        	</colgroup>
	 <thead>
	 	<tr>
	 		<th>Author</th>
	 		<th>Date</th>	 		
	 		<th>Type</th>
	 		<th>Comment</th>
	 		<th/>
	 		<th/>
	 		<th/>
	 	</tr>
 	</thead>
 <#list timelineEntities as timelineEntity>
 	<tr>
 		<td>${(timelineEntity.user.firstName)!} ${(timelineEntity.user.lastName)!}</td>
 		
 		<td>${(timelineEntity.date?string('d/M/yy HH:mm'))!}</td>
 		
 		
 		<td>${(timelineEntity.type?html)!}</td>
 		<#if timelineEntity.newStatus?? >
 		<td>Application entered ${timelineEntity.newStatus.displayValue()}</td>
 		<#else>
 		<td>${(timelineEntity.comment?html)!}</td>
 		</#if>
 		
 		<#if timelineEntity.type?? && timelineEntity.type ==  "REVIEW">
 			<td>Interview: ${(timelineEntity.willingToInterview.displayValue()?html)!}</td>
 			<td>Suitable: ${(timelineEntity.suitableCandidate.displayValue()?html)!}</td>
 			<td>Decline: ${(timelineEntity.decline.displayValue()?html)!}</td>
		<#elseif timelineEntity.type?? && timelineEntity.type ==  "INTERVIEW">
 			<td>Supervise: ${(timelineEntity.willingToSupervice.displayValue()?html)!}</td>
 			<td>Suitable: ${(timelineEntity.suitableCandidate.displayValue()?html)!}</td>
 			<td>Decline: ${(timelineEntity.decline.displayValue()?html)!}</td>
		<#else>
		<td/>
		<td/>
		<td/> 			
 		</#if>
	</tr>	 
</#list>
</table>   