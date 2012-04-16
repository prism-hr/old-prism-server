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
              	<div>
					
					<div class="row">
                  		<label class="label">First Name</label>
                    	<div class="field">
                    		${(applicationForm.personalDetails.firstName?html)!}
                    	</div>
                    </div>

                	<div class="row">
	                  	<label class="label">Last Name</label>
    	                <div class="field">
        	            	${(applicationForm.personalDetails.lastName?html)!}
            	        </div>
                 	</div>

                	<div class="row">
                  		<label class="label">Gender</label>
                    	<div class="field">
                      		${(applicationForm.personalDetails.gender?html)!}
                    	</div>
                  	</div>
                	
                	<div class="row">
                  		<label class="label">Date of Birth</label>
                    	<div class="field">${(applicationForm.personalDetails.dateOfBirth?string('dd-MMM-yyyy'))!}</div>
                  	</div>
                
                </div>

				<!-- Country -->
              	<div>
                	<div class="row">
                        <label class="label">Nationality</label>
                    </div>
                	<div class="row">
                  		<span class="label">Country of Birth</span>
                    	<div class="field">
                    		${(applicationForm.personalDetails.country.name?html)!}
                    	</div>
                  	</div>
               </div>   	
                  	
                  	<div>
                  	<label class="label">My Nationality</label>  
                  	<#list applicationForm.personalDetails.candidateNationalities as nationality >
                        <span name="existingCandidateNationality">
                                <div class="field">
                                    <label class="full">${nationality.name}</label>  
                                    <input type="hidden" name="candidateNationalities" value='${nationality.id}'/>
                                </div>
                        </span>                         
                      </#list>
                    </div>
                     
                 <!--Maternal guardian nationality -->
                        <div>
                     <#if (applicationForm.personalDetails.maternalGuardianNationalities?size > 0)>
                        <label class="label">Maternal Guardian Nationality</label>    
                        <#list applicationForm.personalDetails.maternalGuardianNationalities as nationality >
                            <span>
                                    <div class="field">
                                        <label class="full">${nationality.name}</label>  
                                        <input type="hidden" name="maternalGuardianNationalities" value='${nationality.id}'/>
                                    </div>
                            </span>
                          </#list>
                      <#else>
                         <span>
                            <div class="row">
                                <label class="label">Maternal Guardian Nationality</label>    
                                <div class="field">-</div>
                            </div>            
                        </span>
                      </#if>
                      </div>
                 
                  <!--Paternal guardian nationality -->
                  <div>
                    <#if (applicationForm.personalDetails.paternalGuardianNationalities?size > 0)>
                        <label class="label">Paternal Guardian Nationality</label>    
                        <#list applicationForm.personalDetails.paternalGuardianNationalities as nationality >
                            <span>
                                    <div class="field">
                                        <label class="full">${nationality.name}</label>  
                                        <input type="hidden" name="paternalGuardianNationalities" value='${nationality.id}'/>
                                    </div>
                            </span>
                        </#list>
                    <#else>
                         <span>
                            <div class="row">
                                <label class="label">Paternal Guardian Nationality</label>    
                                <div class="field">-</div>
                            </div>            
                        </span>
                      </#if>
                      </div>

				<div>
				    <div class="row">
                        <label class="label">Language</label>
                    </div>
              	     <div class="row">
						<span class="label">Is English your first language?</span>
						<div class="field">
                    		<#if applicationForm.personalDetails.englishFirstLanguage>
                    		Yes
                    		<#else>
                    		No
                    		</#if>
                    	</div>
					</div>                              
                </div>
		
				<!-- Nationality -->
              	<div>
                	
                	<label class="label">Residence</label>  
                	<div class="row">
                  		<span class="label">Country of Residence</span>
                    	<div class="field">
                      		${(applicationForm.personalDetails.residenceCountry.name?html)!}
                    	</div>
                  	</div>
                  	
                  	<div class="row">
                        <span class="label">Do you require a visa to study in the UK?</span>
                        <div class="field">
                            <#if applicationForm.personalDetails.requiresVisa>
                            Yes
                            <#else>
                            No
                            </#if>
                        </div>
                    </div> 
                  	
                </div>

				<!-- Contact Details -->
              	<div>
                	
                	<strong>Contact Details</strong>
                	<div class="row">
                		<span class="label">Email</span>
                    	<div class="field">
	                    	${(applicationForm.personalDetails.email?html)!}
                    	</div>
                  	</div>
                
              		<div class="row">
                		<span class="label">Telephone</span>
                    	<div class="field">
	                    	${(applicationForm.personalDetails.phoneNumber?html)!}
                    	</div>
                  	</div>
              		<div class="row">
                		<span class="label">Skype Name</span>
                    	<div class="field">
	                    	${(applicationForm.personalDetails.messenger?html)!}
                    	</div>
                  	</div>
                 </div>

              	<div class="buttons" id="show-comment-button-div">
              		<#if user??>
                		<a class="button blue comment-open" id="comment-button">Comments</a>
                	</#if>
                	 <a id="personalDetailsCloseButton"class="button blue">Close</a>
                </div>

		</form>
		
		<#if user??>
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
