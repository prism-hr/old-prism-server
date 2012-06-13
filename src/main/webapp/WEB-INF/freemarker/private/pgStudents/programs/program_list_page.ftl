<#if model.programs?has_content>
	<#assign hasPrograms = true />
	<#assign programs = model.programs />
<#else>
	<#assign hasPrograms = false />
</#if>	

<#import "/spring.ftl" as spring />
<html>
	<head>
			<script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
			<script type="text/javascript">
			$(document).ready(function()
			{
			 	$('button.apply').click(function() {
			    	$('#program').val(this.id);
			    	if ($('#'+this.id+'_deadline').html() == "12-Dec-2012"){
			    		$('#programDeadline').val("12-Dec-2012");
			    	}
			    	$('#applyForm').submit();
			   });
			
			});
				
			</script>
	</head>
   <body id="bodyId">   
		<h2>Programs</h2>			
		<ul>
		<#if hasPrograms>
			<table>
				<tr>
					<td> Code </td>
					<td> Title </td>					
					<td> Batch Deadline </td>					
					<td/>
				</tr>
			<#list programs as program>
				<tr id = "${program.code}" > 
					<td> ${program.code} </td>
					<td> ${program.title} </td>										
					<td id="${program.id?string("######")}_deadline"> <#if program.title == "EngD Biochemical Engineering"> 12-Dec-2012 </#if></td>										
					<td> <button id="${program.id?string("######")}" class="apply">Apply now</button></td>
				</tr>
	      	</#list>
	      	</table>
	    </#if>
      	</ul>
      	<form id="applyForm" action="<@spring.url '/apply/new'/>" method="POST">
      		<input type="hidden" id="program" name="program" value=""/>
      		<input type="hidden" id="programDeadline" name="programDeadline" value=""/>
      	</form>
	</body>
</html>
