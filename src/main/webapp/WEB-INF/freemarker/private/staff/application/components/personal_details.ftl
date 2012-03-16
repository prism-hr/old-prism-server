<#-- Assignments -->

<#assign viewType = model.view>
<#assign prevComments = "false">

<#if model.applicationForm.hasComments()>
	
	<#assign comCount = model.applicationComments?size>

</#if>

<#-- Personal Details Rendering -->

<!-- Personal details -->
<section class="folding purple">
	<h2 id="personalDetails-H2" class="empty open">
		<span class="left"></span><span class="right"></span><span class="status"></span>
		Personal Details
	</h2>

    <div id="personal-details-section" class="open">
		<form method="post" method = "GET">
                <input type ="hidden" id="view-type-personal-form" value="${viewType}"/>
                <input type="hidden" name="id" value="${model.applicationForm.id?string("######")}"/>
                <input type="hidden" id="form-display-state" value="${formDisplayState}"/>
              	
              	<!-- Basic Details -->
              	<div>
					
					<div class="row">
                  		<label class="label">First Name</label>
                    	<div class="field">
                    		${model.applicationForm.personalDetails.firstName!}
                    	</div>
                    </div>

                	<div class="row">
	                  	<label class="label">Last Name</label>
    	                <div class="field">
        	            	${model.applicationForm.personalDetails.lastName!}
            	        </div>
                 	</div>

                	<div class="row">
                  		<label class="label">Gender</label>
                    	<div class="field">
                      		${model.applicationForm.personalDetails.gender!}
                    	</div>
                  	</div>
                	
                	<div class="row">
                  		<label class="label">Date of Birth</label>
                    	<div class="field">${(model.applicationForm.personalDetails.dateOfBirth?string('dd-MMM-yyyy'))!}</div>
                  	</div>
                
                </div>

				<!-- Country -->
              	<div>
                	
                	<div class="row">
                  		<label class="label">Country of Birth</label>
                    	<div class="field">
                    		${model.applicationForm.personalDetails.country.name!}
                    	</div>
                  	</div>
                  	
                </div>

				<!-- Nationality -->
              	<div>
                	
                	<strong>Residence</strong>
                	<div class="row">
                  		<span class="label">Country</span>
                    	<div class="field">
                      		${model.applicationForm.personalDetails.residenceCountry.name!}
                    	</div>
                  	</div>
                  	<div class="row">
                        <span class="label">Status</span>
                        <div class="field">
                            ${model.applicationForm.personalDetails.residenceStatus.displayValue!}
                        </div>
                    </div>
                  	
                </div>
                
                <!-- candidate nationality -->
				<div>    

             
                  	  <#list model.applicationForm.personalDetails.candidateNationalities as nationality >
                  	  	<span name="existingCandidateNationality">
                  	  	 	<div class="row">
                  	  	 		<label class="label">Nationality</label>    
                  				<div class="field">
                  					<label class="full">${nationality.country.name}</label>  
                  	  				<input type="hidden" name="candidateNationalities" value='${nationality.asJson}'/>
                  	  				<#if nationality.primary><label>Primary nationality</label>"</#if>                  	  		 
                  	  			</div>
                  	  		</div>
                  	  	</span>                  		
                  	  </#list>
                
                 </div>
                 
                 <!--Maternal guardian nationality -->
                 <div>
                 <#list model.applicationForm.personalDetails.maternalGuardianNationalities as nationality >
                  	  	<span>
                  	  		<div class="row">
                  	  	 		<label class="label">Maternal Guardian Nationality</label>    
                  				<div class="field">
                  					<label class="full">${nationality.country.name}</label>  
                  	  				<input type="hidden" name="maternalGuardianNationalities" value='${nationality.asJson}'/>
                  	  				<#if nationality.primary><label>Primary nationality</label></#if>                  	  
                  	  			</div>
                  	  		</div>            
                  	  	</span>
                  	  </#list>
                 </div>
                 
                  <!--Paternal guardian nationality -->
                 <div>
                  <#list model.applicationForm.personalDetails.paternalGuardianNationalities as nationality >
                  	  	<span>
                  	  		<div class="row">
                  	  	 		<label class="label">Paternal Guardian Nationality</label>    
                  				<div class="field">
                  					<label class="full">${nationality.country.name}</label>  
                  	  				<input type="hidden" name="paternalGuardianNationalities" value='${nationality.asJson}'/>
                  	  				<#if nationality.primary><label>Primary nationality</label></#if>                  	  
                  	  			</div>
                  	  		</div>            
                  	  	</span>
                  	  </#list>
                 </div>
				<!-- Language -->
              	<div>
                	
                	 <#list model.applicationForm.personalDetails.languageProficiencies as prof >
                  	  	<span>
                  	  		<div class="row">
                  	  	 		<label class="label">Language</label>    
                  				<div class="field">
                  					<label class="full"> ${prof.language.name}</label>                  	  		
                  	  				<#if prof.primary>Primary language</#if>              
                  	  			</div>
                  	  			<span class="label">Aptitude</span>    
                  				<div class="field">
                  					<label class="full"> ${prof.aptitude.displayValue}</label>    
                  	  			</div>
                  	  		</div>   
                           
                  	  	</span>
                  	  </#list>
                
                </div>

				<!-- Contact Details -->
              	<div>
                	
                	<strong>Contact Details</strong>
                	<div class="row">
                		<span class="label">Email</span>
                    	<div class="field">
	                    	${model.applicationForm.personalDetails.email!}
                    	</div>
                  	</div>
                
                </div>
                
              	<div>
                	
                	  <#list model.applicationForm.personalDetails.phoneNumbers! as phoneNumber>          
						<span>
                  	  		<div class="row">
                  	  	 		<span class="label">Telephone</span>    
                  				<div class="field">  ${phoneNumber.telephoneType.displayValue} ${phoneNumber.telephoneNumber} </div>                  	  			
                  	  		</div>                                
                  	  	</span>
                    </#list>
                
                </div>
                
              	<div>
                	 <#list model.applicationForm.personalDetails.messengers as messenger >
                  	  	<span>
                  	  		<div class="row">
                  	  	 		<span class="label">Skype</span>    
                  				<div class="field">
                  					<label class="full">${messenger.messengerAddress}</label>                   	  				
                  	  			</div>                  	  			
                  	  		</div>  
   
                  	  	</span>
                  	  </#list>
                </div>

              	<div class="buttons" id="show-comment-button-div">
                	<a class="button blue comment-open" href="#" id="comment-button">Comment</a>
                	 <a id="personalDetailsCloseButton"class="button blue">Close</a>
                </div>

		</form>
		
		<form id="commentForm" action= "/pgadmissions/comments/submit" method="POST">
		
		    <!-- Comment Sectiton -->
                
            <div class="comment">
                <#--	
                   <#if model.applicationForm.hasComments()>
	                   	<#assign prevComments = "true">
	                	<div class="previous">
	                    	<strong>Previous comments</strong>
	                    	 <ul>
	                    	<#list model.applicationComments as comment>
								<li>
									<strong>${comment.user.username}</strong>
									<span>${comment.comment}</span>
								</li>
							</#list>
							 </ul>
	                  	</div>
                  <#else>
                  		<#assign prevComments = "false">
                  </#if>
                <hr />
                -->
            </div>
		
			<#--
			<input type ="hidden" name="id" value="${model.applicationForm.id?string("######")}"/>
			<input type ="hidden" id="view-type-comment-form" value="${viewType}"/>
			<input type ="hidden" id="prev-comment-div" value="${comCount}"/>
			<input id="commentField" type="hidden" name="comment" value=""/>
			-->
			<p><strong>Add a comment</strong></p>
			<textarea id="comment" lass="max" rows="4" cols="70"></textarea>
                  
            <div class="buttons" id="buttons-inside-comment-div">
            	
            	<a class="button comment-close" id="comment-close-button">Close</a>
              	<a class="button blue" id="commentSubmitButton">Submit</a>
                  		
        	</div>
			
		</form>
	</div>
			<script type="text/javascript" src="<@spring.url '/design/default/js/libraries.js'/>"></script>
		<script type="text/javascript" src="<@spring.url '/design/default/js/application/common.js'/>"></script>
		<script type="text/javascript" src="<@spring.url '/design/default/js/application/personalDetails.js'/>"></script>
</section>