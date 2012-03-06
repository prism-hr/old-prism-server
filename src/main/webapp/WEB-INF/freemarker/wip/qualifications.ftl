 <#import "/spring.ftl" as spring />
 <h2 class="open">
                          <span class="left"></span><span class="right"></span><span class="status"></span>
                          Qualifications
                        </h2>
                        <div>
                            <br/>
                            <table>
 							<tr>
 								<td>
                             	Type
                               </td>
                            	<td>Grade</td>
                            	<td>Institution</td>
                            	<td>Award Date</td>
                            	<td/>
 							</tr>
							<#list model.applicationForm.qualifications as qualification >
							<tr>
 								<td>
                               	${qualification.qualification_type}
                               </td>
                            	<td>${qualification.grade}</td>
                            	<td>${qualification.institution}</td>
                            	<td>${(qualification.award_date?string('yyyy/MM/dd'))!}</td>
                            	<td><a id="qualification_${qualification.id}" name ="editQualificationLink"> Edit<a/></td>
                             	<input type="hidden" name="qualId" id="${qualification.id}_qualId" value="${qualification.id}"/></tr>
                             	    <input type="text" id="${qualification.id}_q_provider" value="${qualification.institution!}"/> 
                            <input type="hidden" id="${qualification.id}_q_name" value="${qualification.name_of_programme!}"/> 
                             <input type="hidden"  id="${qualification.id}_q_start_date" value="${(qualification.start_date?string('yyyy/MM/dd'))!}"/> 
                             <input type="hidden" id="${qualification.id}_q_term_reason" value="${qualification.termination_reason!}"/> 
                             <input type="hidden"  id="${qualification.id}_q_term_date" value="${(qualification.termination_date?string('yyyy/MM/dd'))!}"/> 
                            <input type="hidden"  id="${qualification.id}_q_country" value="${qualification.country!}"/> 
                             <input type="hidden"  id="${qualification.id}_q_language" value="${qualification.language_of_study!}"/> 
                            <input type="hidden"  id="${qualification.id}_q_level" value="${qualification.level!}"/> 
                             <input type="hidden"  id="${qualification.id}_q_type" value="${qualification.type!}"/> 
                             <input type="hidden"  id="${qualification.id}_q_grade" value="${qualification.grade!}"/> 
                             <input type="hidden"  id="${qualification.id}_q_score" value="${qualification.score!}"/> 
                             <input type="hidden"  id="${qualification.id}_q_award_date" value=""/> <br/>
                             <input type="hidden"  id="${qualification.id}_q_attachment" value=""/> <br/>
                            
                            </#list>
                             </table>	
                           	Provider <input type="text" id="q_provider" value="${model.qualification.institution!}"/> 
                           	<#if model.hasError('institution')>                    		
                    			<span style="color:red;"><@spring.message  model.result.getFieldError('institution').code /></span>                    		
                    		</#if><br/>
                            Name <input type="text" id="q_name" value="${model.qualification.name_of_programme!}"/> 
                            <#if model.hasError('name_of_programme')>                    		
                    			<span style="color:red;"><@spring.message  model.result.getFieldError('name_of_programme').code /></span>                    		
                    		</#if><br/>
                            Start Date <input type="text"  id="q_start_date" value="${(model.qualification.start_date?string('yyyy/MM/dd'))!}"/> 
                            <#if model.hasError('start_date')>                    		
                    			<span style="color:red;"><@spring.message  model.result.getFieldError('start_date').code /></span>                    		
                    		</#if><br/>
                            Termination Reason <input type="text" id="q_term_reason" value="${model.qualification.termination_reason!}"/> 
                            <#if model.hasError('termination_reason')>                    		
                    			<span style="color:red;"><@spring.message  model.result.getFieldError('termination_reason').code /></span>                    		
                    		</#if><br/>
                            Termination Date <input type="text"  id="q_term_date" value="${(model.qualification.termination_date?string('yyyy/MM/dd'))!}"/> 
                            <#if model.hasError('termination_date')>                    		
                    			<span style="color:red;"><@spring.message  model.result.getFieldError('termination_date').code /></span>                    		
                    		</#if><br/>
                            Country <input type="text"  id="q_country" value="${model.qualification.country!}"/> 
                            <#if model.hasError('country')>                    		
                    			<span style="color:red;"><@spring.message  model.result.getFieldError('country').code /></span>                    		
                    		</#if><br/>
                            Language <input type="text"  id="q_language" value="${model.qualification.language_of_study!}"/> 
                            <#if model.hasError('language_of_study')>                    		
                    			<span style="color:red;"><@spring.message  model.result.getFieldError('language_of_study').code /></span>                    		
                    		</#if><br/>
                            Level <input type="text"  id="q_level" value="${model.qualification.level!}"/> 
                            <#if model.hasError('level')>                    		
                    			<span style="color:red;"><@spring.message  model.result.getFieldError('level').code /></span>                    		
                    		</#if><br/>
                            Type <input type="text"  id="q_type" value="${model.qualification.type!}"/> 
                            <#if model.hasError('type')>                    		
                    			<span style="color:red;"><@spring.message  model.result.getFieldError('type').code /></span>                    		
                    		</#if><br/>
                            Grade <input type="text"  id="q_grade" value="${model.qualification.grade!}"/> 
                            <#if model.hasError('grade')>                    		
                    			<span style="color:red;"><@spring.message  model.result.getFieldError('grade').code /></span>                    		
                    		</#if><br/>
                            Score <input type="text"  id="q_score" value="${model.qualification.score!}"/> 
                            <#if model.hasError('score')>                    		
                    			<span style="color:red;"><@spring.message  model.result.getFieldError('score').code /></span>                    		
                    		</#if><br/>
                            Award Date <input type="text"  id="q_award_date" value=""/> <br/>
                            Attachment <input type="text"  id="q_attachment" value=""/> <br/>
                            
                            <div class="buttons">
                                    <a class="button blue" href="#">Close</a>
                                    <a class="button blue" type="submit" id="qualificationsSaveButton">Save</a>
                            </div>
                        </div>
<script type="text/javascript" src="<@spring.url '/design/default/js/application/qualifications.js'/>"></script>

