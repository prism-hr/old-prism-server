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
                            	<td><a class="button blue" id="qualification_${qualification.id}" name ="editQualificationLink"> Edit<a/></td>
                             	<input type="hidden" id="${qualification.id}_qualId" value="${qualification.id}"/></tr>
                             	 <input type="hidden" id="${qualification.id}_q_provider" value="${qualification.institution!}"/> 
                           		 <input type="hidden" id="${qualification.id}_q_name" value="${qualification.name_of_programme!}"/> 
                             	<input type="hidden"  id="${qualification.id}_q_start_date" value="${(qualification.start_date?string('yyyy/MM/dd'))!}"/> 
                             	<input type="hidden" id="${qualification.id}_q_term_reason" value="${qualification.termination_reason!}"/> 
                             	<input type="hidden"  id="${qualification.id}_q_term_date" value="${(qualification.termination_date?string('yyyy/MM/dd'))!}"/> 
                           	 <input type="hidden"  id="${qualification.id}_q_country" value="${qualification.country!}"/> 
                            	 <input type="hidden"  id="${qualification.id}_q_language" value="${qualification.language_of_study!}"/> 
                            <input type="hidden"  id="${qualification.id}_q_level" value="${qualification.level!}"/> 
                             <input type="hidden"  id="${qualification.id}_q_Type" value="${qualification.qualification_type!}"/> 
                             <input type="hidden"  id="${qualification.id}_q_grade" value="${qualification.grade!}"/> 
                             <input type="hidden"  id="${qualification.id}_q_score" value="${qualification.score!}"/> 
                             <input type="hidden"  id="${qualification.id}_q_award_date" value="${(qualification.award_date?string('yyyy/MM/dd'))!}"/> 
                             <input type="hidden"  id="${qualification.id}_q_attachment" value=""/>
                            
                            </#list>
                             </table>	
                                
                           
                              <table cellspacing=10>
                            <tr><td>
                           	Provider
                           	</td> 
                           	<td><input type="text" id="q_provider" name="institution" value="${model.qualification.institution!}"/> 
                           	<#if model.hasError('institution')>                    		
                    			<span style="color:red;"><@spring.message  model.result.getFieldError('institution').code /></span>                    		
                    		</#if>
                    		</td>
                    		</tr>
                    		
                    		<tr><td>
                            Name 
                            </td> 
                           	<td><input type="text" id="q_name" name="name_of_programme" value="${model.qualification.name_of_programme!}"/> 
                            <#if model.hasError('name_of_programme')>                    		
                    			<span style="color:red;"><@spring.message  model.result.getFieldError('name_of_programme').code /></span>                    		
                    		</#if>
							</td>
                    		</tr>
                    		
                    		<tr><td>
                            Start Date
							</td> 
                           	<td><input type="text"  id="q_start_date" name="start_date" value="${(model.qualification.start_date?string('yyyy/MM/dd'))!}"/> 
                            <#if model.hasError('start_date')>                    		
                    			<span style="color:red;"><@spring.message  model.result.getFieldError('start_date').code /></span>                    		
                    		</#if>
                    		</td>
                    		</tr>
                    		
                    		<tr><td>
                            Language 
                            </td> 
                           	<td><input type="text"  id="q_language" name="language_of_study" value="${model.qualification.language_of_study!}"/> 
                            <#if model.hasError('language_of_study')>                    		
                    			<span style="color:red;"><@spring.message  model.result.getFieldError('language_of_study').code /></span>                    		
                    		</#if>
                    		</td>
                    		</tr>
                    		
                            <tr>
                           	<td>Level 
                           	</td> 
                           	<td>
                            <input type="text"  id="q_level" name="level" value="${model.qualification.level!}"/> 
                            <#if model.hasError('level')>                    		
                    			<span style="color:red;"><@spring.message  model.result.getFieldError('level').code /></span>                    		
                    		</#if>
                    		</td>
                    		</tr>
                    		
                    		<tr>
                           	<td>Type 
                           	</td> 
                           	<td>
                            <input type="text"  id="q_Type"  name="qualification_type" value="${model.qualification.qualification_type!}"/> 
                            <#if model.hasError('qualification_type')>                    		
                    			<span style="color:red;"><@spring.message  model.result.getFieldError('qualification_type').code /></span>                    		
                    		</#if>
                    		</td>
                    		</tr>
                    		
                    		<tr>
                           	<td>Grade</td> 
                           	<td>
                             <input type="text"  id="q_grade" name="grade" value="${model.qualification.grade!}"/> 
                            <#if model.hasError('grade')>                    		
                    			<span style="color:red;"><@spring.message  model.result.getFieldError('grade').code /></span>                    		
                    		</#if>
                    		</td>
                    		</tr>
                    		<tr> 
                           	<td>Score </td> 
                           	<td>
                            <input type="text"  id="q_score" name="score" value="${model.qualification.score!}"/> 
                            <#if model.hasError('score')>                    		
                    			<span style="color:red;"><@spring.message  model.result.getFieldError('score').code /></span>                    		
                    		</#if>
                    		</td>
                    		</tr>
                    		<tr>
                           	<td>Award Date </td> 
                           	<td>
                            <input type="text"  id="q_award_date" name="award_date" value="${(model.qualification.award_date?string('yyyy/MM/dd'))!}"/> 
                            </td>
                    		</tr>
                    		<tr>
                    		<tr>
                           	<td>QualId </td> 
                           	<td>
                            <input type="text"  id="qualId" name="qualId" value="${model.qualification.qualId!}"/> 
                            </td>
                    		</tr>
                    		<tr>
                           	<td>Attachment </td> 
                           	<td>
                            <input type="text"  id="q_attachment" value=""/> 
                            </td>
                    		</tr>
                             </table>
                            <div class="buttons">
                                    <a class="button blue" href="#">Close</a>
                                    <#if !model.applicationForm.isSubmitted()>
                                    <a class="button blue" type="submit" id="qualificationsSaveButton">Save</a>
                                    </#if>
                            </div>
                        </div>
<script type="text/javascript" src="<@spring.url '/design/default/js/application/qualifications.js'/>"></script>

