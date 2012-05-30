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
					<td/>
				</tr>
			<#list programs as program>
				<tr id = "${program.code}" > 
					<td> ${program.code} </td>
					<td> ${program.title} </td>										
					<td> <button id="${program.id?string("######")}" class="apply">Apply now</button></td>
				</tr>
	      	</#list>
	      	</table>
	    </#if>
      	</ul>
      	<form id="applyForm" action="<@spring.url '/apply/new'/>" method="POST">
      		<input type="hidden" id="program" name="program" value=""/>
      	</form>
	</body>
</html>
