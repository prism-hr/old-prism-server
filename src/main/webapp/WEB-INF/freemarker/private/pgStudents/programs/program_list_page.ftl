<#if model.programs?has_content>
	<#assign hasPrograms = true />
	<#assign programs = model.programs />
<#else>
	<#assign hasPrograms = false />
</#if>	

<#import "/spring.ftl" as spring />
<html>
	<head>
	       <meta name="prism.version" content="<@spring.message 'prism.version'/>" >
    	   <link rel="shortcut icon" type="text/css" href="<@spring.url '/design/default/images/favicon.ico' />"/>
			<script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
			<script type="text/javascript">
			$(document).ready(function()
			{
			 	$('button.apply').click(function() {
			    	$('#program').val(this.id);
			    	if (new String($('#'+this.id+'_deadline').html()).valueOf() === new String("12-Dec-2012").valueOf()){
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
					<td id="${program.code}_deadline"><#if program.title == "EngD Biochemical Engineering">12-Dec-2012</#if></td>										
					<td> <button id="${program.code}" class="apply">Apply now</button></td>
				</tr>
	      	</#list>
	      	</table>
	    </#if>
      	</ul>
      	<form id="applyForm" action="<@spring.url '/apply/new'/>" method="POST">
      		<input type="hidden" id="program" name="program" value=""/>
      		<input type="hidden" id="programDeadline" name="programDeadline" value=""/>
      		<input type="hidden" id="programhome" name="programhome" value=""/>
      		<input type="hidden" id="projectTitle" name="projectTitle" value=""/>
      	</form>
	</body>
</html>
