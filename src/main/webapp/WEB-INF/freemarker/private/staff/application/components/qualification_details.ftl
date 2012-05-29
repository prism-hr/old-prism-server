<#if applicationForm.qualifications?has_content>
	<#assign hasQualifications = true>
<#else>
	<#assign hasQualifications = false>
</#if> 
 
 <#import "/spring.ftl" as spring />
			
			<h2 id="qualifications-H2" class="empty">
				<span class="left"></span><span class="right"></span><span class="status"></span>
				Qualifications
        	</h2>
            <div>
            
            	<form>
            	<#if hasQualifications>
            
					<#list applicationForm.qualifications as qualification>
							
						<!-- All hidden input - Start -->				                  	     
                        <input type="hidden" id="${qualification.id?string('#######')}_qualificationIdDP" value="${qualification.id?string('#######')}"/>
                        <input type="hidden" id="${qualification.id?string('#######')}_qualificationInstitutionCountryDP" value="${(qualification.institutionCountry.name?html)!}"/>
                        <input type="hidden" id="${qualification.id?string('#######')}_qualificationInstitutionDP" value="${(qualification.qualificationInstitution?html)!}"/>
                        <input type="hidden"  id="${qualification.id?string('#######')}_qualificationTypeDP" value="${(qualification.qualificationType?html)!}"/> 
                        <input type="hidden" id="${qualification.id?string('#######')}_qualificationSubjectDP" value="${(qualification.qualificationSubject?html)!}"/> 
                        <input type="hidden"  id="${qualification.id?string('#######')}_qualificationLanguageDP" value="${qualification.qualificationLanguage.name!}"/> 
                        <input type="hidden"  id="${qualification.id?string('#######')}_qualificationStartDateDP" value="${(qualification.qualificationStartDate?string('dd-MMM-yyyy'))!}"/>
                        <input type="hidden"  id="${qualification.id?string('#######')}_qualificationCompletedDP" value="${(qualification.completed?html)!}"/>  
                        <input type="hidden"  id="${qualification.id?string('#######')}_qualificationGradeDP" value="${(qualification.qualificationGrade?html)!}"/> 
                        <input type="hidden"  id="${qualification.id?string('#######')}_qualificationAwardDateDP" value="${(qualification.qualificationAwardDate?string('dd-MMM-yyyy'))!}"/>
                        <input type="hidden" id="${qualification.id?string('#######')}_qualdocname" value="${(qualification.proofOfAward.fileName)!}"/>
                        <input type="hidden" id="${qualification.id?string('#######')}_qualdocurl" value="/pgadmissions/download?documentId=${(encrypter.encrypt(qualification.proofOfAward.id))!}"/> 
						<input type="hidden" id="qualificationId" name="qualificationId"/>
						
						<!-- All hidden input - End --> 
	                		
	                	<!-- Rendering part - Start -->
					
						<div class="row-group">
							
							<!-- Header -->
					        <div class="admin_row">
					        	<label class="admin_header">Qualification (${qualification_index + 1})</label>
					            <div class="field">&nbsp</div>
							</div>
							
		                  	<!-- Provider -->
		                  	<div class="admin_row">
	                            <span class="admin_row_label">Institution Country</span>
	                            <div class="field" id="qualificationInstitutionCountry">${(qualification.institutionCountry.name?html)!"Not Provided"}</div>
	                        </div>
	                  	
		                	<div class="admin_row">
			                  	<span class="admin_row_label">Institution / Provider Name</span>
			                    <div class="field" id="qualificationInstitution">${(qualification.qualificationInstitution?html)!"Not Provided"} </div>
		                  	</div>
		                  
		                  	<!-- Type -->
		                	<div class="admin_row">
			                  	<span class="admin_row_label">QualificationType</span>
			                    <div class="field" id="qualificationType">${(qualification.qualificationType?html)!"Not Provided"}</div>
		             		</div>
		             		
		             		<!-- Title / Subject -->
	                        <div class="admin_row">
	                            <span class="admin_row_label">Title / Subject</span>
		                        <div class="field" id="qualificationSubject">${(qualification.qualificationSubject?html)!"Not Provided"} </div>
	                        </div>
		             		
		             		<!-- Language (in which programme was undertaken) -->
	                        <div class="admin_row">
	                            <span class="admin_row_label">Language of Study</span>
		                        <div class="field" id="qualificationLanguage">${qualification.qualificationLanguage.name!"Not Provided"} </div>
	                        </div>
	                  
		                  	<!-- Start date -->
	                		<div class="admin_row">
		                  		<span class="admin_row_label">Start Date</span>
			                    <div class="field" id="qualificationStartDate">
			                    				${(qualification.qualificationStartDate?string('dd-MMM-yyyy'))!"Not Provided"} </div>
		                 	</div>
	    	             	<div class="admin_row">
            	                <span class="admin_row_label">Has this qualification been awarded?</span>                  	
               				    <div class="field" id="qualificationCompleted">
               				    	${(qualification.completed?html)!"Not Provided"}
								</div>
	                		</div>
                  		
	                  		<!-- Qualification grade -->
	                  		<div class="admin_row">
	                    		<span class="admin_row_label">Grade / Result /GPA</span>
	                    		<div class="field" id="qualificationGrade">${(qualification.qualificationGrade?html)!"Not Provided"} </div>
	                  		</div>
                  
	                  		<!-- Award date -->
    	              		<div class="admin_row">
        	            		<span class="admin_row_label">Award Date</span>
            	        		<div class="field" id="qualificationAwardDate">
            	        				${(qualification.qualificationAwardDate?string('dd-MMM-yyyy'))!"Not Provided"} </div>
                	  		</div>

	                  		<!-- Attachment / supporting document  -->
    	              		<div class="admin_row">
        	            		<span class="admin_row_label">Proof of award (PDF)</span>
					            <div class="field" id="referenceDocument">Not Provided</div> 
                	  		</div>
                	  		
						</div>							               
					</#list>
              	<#else>
              	
						<div class="row-group">
							
		                  	<!-- Provider -->
		                  	<div class="row">
	                            <span class="admin_header">Qualification</span>
	                            <div class="field">Not Provided</div>
	                        </div>
	                  	
						</div>							               
              	
              	</#if>
              	
              	
              	
		        <div class="buttons">
					<button class="blue" id="qualificationsCloseButton" type="button">Close</button>
	            </div>

			</form>
		</div>
		
		<script type="text/javascript" src="<@spring.url '/design/default/js/application/staff/qualifications.js'/>"></script>
 