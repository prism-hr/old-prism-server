<#-- Assignments -->

<#if view??>
	<#assign viewType = view>
<#else>
	<#assign viewType = 'open'>
</#if>
<#assign prevComments = "false">

<#if applicationForm.hasComments()>
	
	<#assign comCount = applicationForm.applicationComments?size>
<#else> 
	<#assign comCount = 0 >
</#if>

<#-- Personal Details Rendering -->

<!-- Personal details -->
	<h2 id="personalDetails-H2" class="tick">
		<span class="left"></span><span class="right"></span><span class="status"></span>
		Personal Details
	</h2>

    <div id="personal-details-section" class="open">
		<form method="post" method = "GET">
            <input type ="hidden" id="view-type-personal-form" value="${viewType}"/>
            <input type="hidden" name="id" value="${applicationForm.id?string("######")}"/>
            <input type="hidden" id="form-display-state" value="${formDisplayState!}"/>
              	
            <!-- Basic Details -->
			<div class="sub_section_amdin">
					
				<div class="admin_row">
                  		<label class="admin_row_label">First Name</label>
                    	<div class="field">
                    		${(applicationForm.personalDetails.firstName?html)!"Not Provided"}
                    	</div>
                </div>

                <div class="admin_row">
	                  	<label class="admin_row_label">Last Name</label>
    	                <div class="field">
        	            	${(applicationForm.personalDetails.lastName?html)!"Not Provided"}
            	        </div>
                </div>

                <div class="admin_row">
                  		<label class="admin_row_label">Gender</label>
                    	<div class="field">
                      		${(applicationForm.personalDetails.gender?html)!"Not Provided"}
                    	</div>
                </div>
                	
                <div class="admin_row">
                  		<label class="admin_row_label">Date of Birth</label>
                    	<div class="field">${(applicationForm.personalDetails.dateOfBirth?string('dd-MMM-yyyy'))!"Not Provided"}</div>
                </div>
                
				<!-- Country -->
                <div class="admin_row">
                  	<span class="admin_row_label">Country of Birth</span>
                    <div class="field">
                    	${(applicationForm.personalDetails.country.name?html)!"Not Provided"}
                    </div>
				</div>
                
                <!-- My Nationality -->  	
                	<#if (applicationForm.personalDetails.candidateNationalities?size > 0)>
                  		<#list applicationForm.personalDetails.candidateNationalities as nationality >
                  			<#assign index_k = nationality_index>
	                  		<#if (index_k == 0)>
	                  			<div class="admin_row">
                					<label class="admin_row_label">My Nationality</label>
	                    			<span name="existingCandidateNationality">
	                        			<div class="field">
	                            			<label class="full">${nationality.name!"Not Provided"}</label>  
	                                		<input type="hidden" name="candidateNationalities" value='${nationality.id}'/>
	                            		</div>
	                       			</span>
	                       		</div>
	                       	<#else>
	                       		<div class="admin_row">
	                    			<span name="existingCandidateNationality">
	                        			<div class="field">
	                            			<label class="full">${nationality.name!"Not Provided"}</label>  
	                                		<input type="hidden" name="candidateNationalities" value='${nationality.id}'/>
	                            		</div>
	                       			</span>
	                       		</div>
	                       	</#if>                         
	                    </#list>
                    <#else>
                    	<div class="admin_row">
                			<label class="admin_row_label">My Nationality</label>
                    		<div class="field">Not Provided</div>
                    	</div>
                    </#if>
                
                 
                 <!--Maternal guardian nationality -->   
                	<#if (applicationForm.personalDetails.maternalGuardianNationalities?size > 0)>
                  		<#list applicationForm.personalDetails.maternalGuardianNationalities as nationality >
                  			<#assign index_i = nationality_index>
	                  		<#if (index_i == 0)>
	                  			<div class="admin_row">
                					<label class="admin_row_label">Maternal Guardian Nationality</label>
	                    			<span name="existingCandidateNationality">
	                        			<div class="field">
	                            			<label class="full">${nationality.name!"Not Provided"}</label>  
	                                		<input type="hidden" name="candidateNationalities" value='${nationality.id}'/>
	                            		</div>
	                       			</span>
	                       		</div>
	                       	<#else>
	                       		<div class="admin_row">
	                    			<span name="existingCandidateNationality">
	                        			<div class="field">
	                            			<label class="full">${nationality.name!"Not Provided"}</label>  
	                                		<input type="hidden" name="candidateNationalities" value='${nationality.id}'/>
	                            		</div>
	                       			</span>
	                       		</div>
	                       	</#if>                         
	                    </#list>
                    <#else>
                    	<div class="admin_row">
                			<label class="admin_row_label">Maternal Guardian Nationality</label>
                    		<div class="field">Not Provided</div>
                    	</div>
                    </#if>
                    
                  <!--Paternal guardian nationality -->
                	<#if (applicationForm.personalDetails.paternalGuardianNationalities?size > 0)>
                  		<#list applicationForm.personalDetails.paternalGuardianNationalities as nationality >
                  			<#assign index_j = nationality_index>
	                  		<#if (index_j == 0)>
	                  			<div class="admin_row">
                					<label class="admin_row_label">Paternal Guardian Nationality</label>
	                    			<span name="existingCandidateNationality">
	                        			<div class="field">
	                            			<label class="full">${nationality.name!"Not Provided"}</label>  
	                                		<input type="hidden" name="candidateNationalities" value='${nationality.id}'/>
	                            		</div>
	                       			</span>
	                       		</div>
	                       	<#else>
	                       		<div class="admin_row">
	                    			<span name="existingCandidateNationality">
	                        			<div class="field">
	                            			<label class="full">${nationality.name!"Not Provided"}</label>  
	                                		<input type="hidden" name="candidateNationalities" value='${nationality.id}'/>
	                            		</div>
	                       			</span>
	                       		</div>
	                       	</#if>                         
	                    </#list>
                    <#else>
                    	<div class="admin_row">
                			<label class="admin_row_label">Paternal Guardian Nationality</label>
                    		<div class="field">Not Provided</div>
                    	</div>
                    </#if>                  
                  

				<div class="admin_row">
					<span class="admin_row_label">Is English your first language?</span>
					<div class="field">
                    	<#if applicationForm.personalDetails.englishFirstLanguage>Yes
                    	<#else>No</#if>
                    </div>
				</div>                              
		
				<!-- Nationality -->
                <div class="admin_row">
                  	<span class="admin_row_label">Country of Residence</span>
                    <div class="field">
                      	${(applicationForm.personalDetails.residenceCountry.name?html)!"Not Provided"}
                    </div>
                </div>
                  	
                <div class="admin_row">
                	<span class="admin_row_label">Do you require a visa to study in the UK?</span>
                    <div class="field">
                    	<#if applicationForm.personalDetails.requiresVisa>
                        	Yes
                        <#else>No</#if>
                    </div>
                </div> 
                  	
				<!-- Contact Details -->
                <div class="admin_row">
                	<span class="admin_row_label">Email</span>
                    <div class="field">
	                    ${(applicationForm.personalDetails.email?html)!"Not Provided"}
                    </div>
                </div>
                
              	<div class="admin_row">
                	<span class="admin_row_label">Telephone</span>
                    <div class="field">
	                	${(applicationForm.personalDetails.phoneNumber?html)!"Not Provided"}
                    </div>
                </div>
              	
				<div class="admin_row">
                	<span class="admin_row_label">Skype Name</span>
                    <div class="field">
	                    ${(applicationForm.personalDetails.messenger?html)!"Not Provided"}
                    </div>
                </div>

            	<div class="admin_row">
              		<label class="admin_row_label">Ethnicity</label>
                	<div class="field">
                	   ${(applicationForm.personalDetails.ethnicity?html)!"Not Provided"}
               	 	</div>
              	</div>
            	<div class="admin_row">
              		<label class="admin_row_label">Disability</label>
                	<div class="field">
                	   ${(applicationForm.personalDetails.disability?html)!"Not Provided"}
               	 	</div>
              	</div>
            </div>
            
            <div class="buttons" id="show-comment-button-div">
              		<#if !user.isRefereeOfApplicationForm(applicationForm)>
                		<a class="button blue comment-open" id="comment-button">Comments</a>
                	</#if>
                	 <a id="personalDetailsCloseButton"class="button blue">Close</a>
            </div>
			
		</form>
		
		<#if !user.isRefereeOfApplicationForm(applicationForm)>
			<form id="commentForm" action= "/pgadmissions/comments/submit" method="POST">
			
			    <!-- Comment Sectiton -->
	                
	            <div class="comment">
	               	
	               	<#if applicationForm.hasComments() && user??>
		                   	<#assign prevComments = "true">
		                	<div class="previous">
		                    	<strong>Previous comments</strong>
		                    	 <ul>
		                    	<#list applicationForm.getVisibleComments(user) as comment>
									<li>
										<strong>${comment.user.username?html}</strong>
										<span>${comment.comment?html}</span>
									</li>
								</#list>
								 </ul>
		                  	</div>
	                  <#else>
	                  		<#assign prevComments = "false">
	                  </#if>
	                <hr />
	             
	            </div>
			
				
				<input type ="hidden" name="id" value="${applicationForm.id?string("######")}"/>
				<input type ="hidden" id="view-type-comment-form" value="${viewType}"/>
				<input type ="hidden" id="prev-comment-div" value="${comCount}"/>
				<input id="commentField" type="hidden" name="comment" value=""/>
		
				<p><strong>Add a comment</strong></p>
				<textarea id="comment" lass="max" rows="4" cols="70"></textarea>
	                  
	            <div class="buttons" id="buttons-inside-comment-div">
	            	
	            	<a class="button comment-close" id="comment-close-button">Close Comments</a>
	              	<a class="button blue" id="commentSubmitButton">Submit Comment</a>
	                  		
	        	</div>
				
			</form>
		</#if>
	</div>
		<script type="text/javascript" src="<@spring.url '/design/default/js/libraries.js'/>"></script>
		<script type="text/javascript" src="<@spring.url '/design/default/js/application/common.js'/>"></script>
		<script type="text/javascript" src="<@spring.url '/design/default/js/application/personalDetails.js'/>"></script>
