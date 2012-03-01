<#-- Assignments -->

<#assign viewType = model.view>
<#assign prevComments = "false">

<#if model.applicationForm.hasComments()>
	
	<#assign comCount = model.applicationComments?size>

</#if>

<#-- Personal Details Rendering -->

<!-- Personal details -->
<section class="folding purple">
	<h2 class="empty open">
		<span class="left"></span><span class="right"></span><span class="status"></span>
		Personal Details
	</h2>
	
    <div id="personal-details-section" class="open">
		<form method="post" method = "GET">
                <input type ="hidden" id="view-type-personal-form" value="${viewType}"/>
                <input type="hidden" name="id" value="${model.applicationForm.id}"/>
                <input type="hidden" id="form-display-state" value="${formDisplayState}"/>
              	
              	<!-- Basic Details -->
              	<div>
					
					<div class="row">
                  		<label class="label">First Name</label>
                    	<div class="field">
                    		${model.applicationForm.applicant.firstName}
                    	</div>
                    </div>

                	<div class="row">
	                  	<label class="label">Last Name</label>
    	                <div class="field">
        	            	${model.applicationForm.applicant.lastName}
            	        </div>
                 	</div>

                	<div class="row">
                  		<label class="label">Gender</label>
                    	<div class="field">
                      		Male
                    	</div>
                  	</div>
                	
                	<div class="row">
                  		<label class="label">Date of Birth</label>
                    	<div class="field">39/08/92</div>
                  	</div>
                
                </div>

				<!-- Country -->
              	<div>
                	
                	<div class="row">
                  		<label class="label">Country of Birth</label>
                    	<div class="field">
                    		United Kingdom
                    	</div>
                  	</div>
                  	
                </div>

				<!-- Nationality -->
              	<div>
                	
                	<strong>Nationality</strong>
                	<div class="row">
                  		<span class="label">Country</span>
                    	<div class="field">
                      		British
                    	</div>
                  	</div>
                  	
                </div>

				<!-- Language -->
              	<div>
                	
                	<div class="row">
                  		<label class="label">Language</label>
                    	<div class="field">
                    		English
                    	</div>
                  	</div>
                	
                	<div class="row">
                  		<span class="label">Aptitude</span>
                    	<div class="field">
                      		Native Speaker
                    	</div>
                  	</div>
                
                </div>

				<!-- Visa Status -->
              	<div>
                	<strong>UK Visa</strong>
                	<div class="row">
                  		<span class="label">Type</span>
                    	<div class="field">
                      		Student
                    	</div>
                  	</div>
                	
                	<div class="row">
                  		<span class="label">Date of Issue</span>
                    	<div class="field">98/16/64</div>
                  	</div>
                	
                	<div class="row">
                  		<span class="label">Date of Expiry</span>
                    	<div class="field">98/16/65</div>
                  	</div>
                	
                	<div class="row">
                  		<span class="label">Supporting Document</span>
                    	<div class="field">
                    		...
                    	</div>
                  	</div>
                
                </div>

				<!-- Contact Details -->
              	<div>
                	
                	<strong>Contact Details</strong>
                	<div class="row">
                		<span class="label">Email</span>
                    	<div class="field">
	                    	bob@smith.com
                    	</div>
                  	</div>
                
                </div>
                
              	<div>
                	
                	<div class="row">
                		<span class="label">Telephone</span>
                   	 	<div class="field">
                    		Home 0555 555 5555
                    	</div>
                  	</div>
                
                </div>
                
              	<div>
                	
                	<div class="row">
                		<span class="label">Messenger</span>
                    	<div class="field">
                    		Skype bob@smith.com
                    	</div>
                  	</div>
                
                </div>

              	<div class="buttons" id="show-comment-button-div">
                	<a class="button blue comment-open" href="#" id="comment-button">Comment</a>
                </div>

		</form>
		
		<form id="commentForm" action= "/pgadmissions/comments/submit" method="POST">
		
		    <!-- Comment Sectiton -->
                
            <div class="comment">
                	
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
            </div>
		
			<input type ="hidden" name="id" value="${model.applicationForm.id}"/>
			<input type ="hidden" id="view-type-comment-form" value="${viewType}"/>
			<input type ="hidden" id="prev-comment-div" value="${comCount}"/>
			<input id="commentField" type="hidden" name="comment" value=""/>
			
			<p><strong>Add a comment</strong></p>
			<textarea id="comment" lass="max" rows="4" cols="70"></textarea>
                  
            <div class="buttons" id="buttons-inside-comment-div">
            	
            	<a class="button comment-close" id="comment-close-button">Close</a>
              	<a class="button blue" id="commentSubmitButton">Submit</a>
                  		
        	</div>
			
		</form>
	</div>
	
</section>