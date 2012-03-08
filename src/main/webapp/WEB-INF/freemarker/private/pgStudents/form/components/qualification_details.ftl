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
                               	${qualification.qualificationType}
                               </td>
                            	<td>${qualification.id}, ${qualification.qualificationGrade}</td>
                            	<td>${qualification.qualificationInstitution}</td>
                            	<td>${(qualification.qualificationAwardDate?string('yyyy/MM/dd'))!}</td>
                            	<td><a class="button blue" type="submit" id="qualification_${qualification.id}" name ="editQualificationLink"> Edit<a/></td>
                             	<input type="hidden" id="${qualification.id}_qualificationIdDP" value="${qualification.id}"/>
                             	 <input type="hidden" id="${qualification.id}_qualificationInstitutionDP" value="${qualification.qualificationInstitution!}"/> 
                           		 <input type="hidden" id="${qualification.id}_qualificationProgramNameDP" value="${qualification.qualificationProgramName!}"/> 
                             	<input type="hidden"  id="${qualification.id}_qualificationStartDateDP" value="${(qualification.qualificationStartDate?string('yyyy/MM/dd'))!}"/> 
                            	 <input type="hidden"  id="${qualification.id}_qualificationLanguageDP" value="${qualification.qualificationLanguage!}"/> 
                            <input type="hidden"  id="${qualification.id}_qualificationLevelDP" value="${qualification.qualificationLevel!}"/> 
                             <input type="hidden"  id="${qualification.id}_qualificationTypeDP" value="${qualification.qualificationType!}"/> 
                             <input type="hidden"  id="${qualification.id}_qualificationGradeDP" value="${qualification.qualificationGrade!}"/> 
                             <input type="hidden"  id="${qualification.id}_qualificationScoreDP" value="${qualification.qualificationScore!}"/> 
                             <input type="hidden"  id="${qualification.id}_qualificationAwardDateDP" value="${(qualification.qualificationAwardDate?string('yyyy/MM/dd'))!}"/> 
                            
                            </tr>
                            </#list>
                             </table>	
                                
                            <input type="hidden" id="qualificationId" name="qualificationId"/>
                            <table cellspacing=10>
                            <tr><td>
                           	Provider
                           	</td> 
                           	<td><input type="text" id="qualificationInstitution" name="qualificationInstitution" value="${model.qualification.qualificationInstitution!}"/> 
                           	<#if model.hasError('qualificationInstitution')>                    		
                    			<span style="color:red;"><@spring.message  model.result.getFieldError('qualificationInstitution').code /></span>                    		
                    		</#if>
                    		</td>
                    		</tr>
                    		
                    		<tr><td>
                            Name 
                            </td> 
                           	<td><input type="text" id="qualificationProgramName" name="qualificationProgramName" value="${model.qualification.qualificationProgramName!}"/> 
                            <#if model.hasError('qualificationProgramName')>                    		
                    			<span style="color:red;"><@spring.message  model.result.getFieldError('qualificationProgramName').code /></span>                    		
                    		</#if>
							</td>
                    		</tr>
                    		
                    		<tr><td>
                            Start Date
							</td> 
                           	<td><input type="text"  id="qualificationStartDate" name="qualificationStartDate" value="${(model.qualification.qualificationStartDate?string('yyyy/MM/dd'))!}"/> 
                            <#if model.hasError('qualificationStartDate')>                    		
                    			<span style="color:red;"><@spring.message  model.result.getFieldError('qualificationStartDate').code /></span>                    		
                    		</#if>
                    		</td>
                    		</tr>
                    		
                    		<tr><td>
                            Language 
                            </td> 
                           	<td><input type="text"  id="qualificationLanguage" name="qualificationLanguage" value="${model.qualification.qualificationLanguage!}"/> 
                            <#if model.hasError('qualificationLanguage')>                    		
                    			<span style="color:red;"><@spring.message  model.result.getFieldError('qualificationLanguage').code /></span>                    		
                    		</#if>
                    		</td>
                    		</tr>
                    		
                            <tr>
                           	<td>Level 
                           	</td> 
                           	<td>
                            <input type="text"  id="qualificationLevel" name="qualificationLevel" value="${model.qualification.qualificationLevel!}"/> 
                            <#if model.hasError('qualificationLevel')>                    		
                    			<span style="color:red;"><@spring.message  model.result.getFieldError('qualificationLevel').code /></span>                    		
                    		</#if>
                    		</td>
                    		</tr>
                    		
                    		<tr>
                           	<td>Type 
                           	</td> 
                           	<td>
                            <input type="text"  id="qualificationType" name="qualificationType" value="${model.qualification.qualificationType!}"/> 
                            <#if model.hasError('qualificationType')>                    		
                    			<span style="color:red;"><@spring.message  model.result.getFieldError('qualificationType').code /></span>                    		
                    		</#if>
                    		</td>
                    		</tr>
                    		
                    		<tr>
                           	<td>Grade</td> 
                           	<td>
                             <input type="text"  id="qualificationGrade" name="qualificationGrade" value="${model.qualification.qualificationGrade!}"/> 
                            <#if model.hasError('qualificationGrade')>                    		
                    			<span style="color:red;"><@spring.message  model.result.getFieldError('qualificationGrade').code /></span>                    		
                    		</#if>
                    		</td>
                    		</tr>
                    		<tr> 
                           	<td>Score </td> 
                           	<td>
                            <input type="text"  id="qualificationScore" name="qualificationScore" value="${model.qualification.qualificationScore!}"/> 
                            <#if model.hasError('qualificationScore')>                    		
                    			<span style="color:red;"><@spring.message  model.result.getFieldError('qualificationScore').code /></span>                    		
                    		</#if>
                    		</td>
                    		</tr>
                    		<tr>
                           	<td>Award Date </td> 
                           	<td>
                            <input type="text"  id="qualificationAwardDate" name="qualificationAwardDate" value="${(model.qualification.qualificationAwardDate?string('yyyy/MM/dd'))!}"/> 
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

