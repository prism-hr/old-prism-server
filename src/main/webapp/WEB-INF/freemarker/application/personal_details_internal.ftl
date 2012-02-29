<#-- Assignments -->

<#-- Personal Details Rendering -->

<!-- Personal details -->
<section class="folding purple">
	<h2 class="empty open">
		<span class="left"></span><span class="right"></span><span class="status"></span>
		Personal Details
	</h2>
	
    <div id="personal-details-section" class="open">
		<form method="post" action="<@spring.url '/???'/>" method = "GET">
                
                <input type="hidden" name="id" value="${model.applicationForm.id}"/>
                <input type="hidden" id="form-view-state" value="${formViewState}"/>
              	
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
                
                <!-- Comment Sectiton -->
                
                <div class="comment">
                	
                	<div class="previous">
                    	<strong>Previous comments</strong>
	                    <ul>
	                      	<li>
	                      		<strong>Jane Smith</strong>
	                        	<span>Lorem ipsum dolor sit amet</span>
	                      	</li>
	                      	<li>
	                      		<strong>Jane Smith</strong>
	                        	<span>It is a long established fact that a reader will be distracted by the readable content of a 
	                        	page when looking at its layout. The point of using Lorem Ipsum is that it has a more-or-less normal 
	                        	distribution of letters, as opposed to using 'Content here, content here', making it look like readable 
	                        	English. Many desktop publishing packages and web page editors now use Lorem Ipsum as their default 
	                        	model text, and a search for 'lorem ipsum' will uncover many web sites still in their infancy. Various 
	                        	versions have evolved over the years, sometimes by accident, sometimes on purpose 
	                        	(injected humour and the like).</span>
	                      	</li>
	                      	<li>
	                      		<strong>Jim Frankl</strong>
	                        	<span>Capsicum lorem ipsum dolor sit amet</span>
	                      	</li>
	                    </ul>
                  	</div>
                	
                	<hr />
                  
                	<p><strong>Add a comment</strong></p>
                  	
                  	<textarea class="max" rows="4" cols="70"></textarea>
                  
                  	<div class="buttons">
                  		<a class="button comment-close">Close</a>
                  		<button type="submit" class="blue">Submit</button>
                  	</div>
                  	
                </div>

              	<div class="buttons">
                	<a class="button blue comment-open" href="#">Comment</a>
                </div>

		</form>
		
	</div>
	
</section>