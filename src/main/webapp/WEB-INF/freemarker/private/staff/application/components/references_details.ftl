<#if applicationForm.referees?has_content>
	<#assign hasReferees = true>
<#else>
	<#assign hasReferees = false>
</#if> 
 
<#import "/spring.ftl" as spring />

			<h2 id="referee-H2" class="empty">
				<span class="left"></span><span class="right"></span><span class="status"></span>
        		References
       		</h2>
			<div class="open">
				<form>				
            	<#if hasReferees>
	                <#list applicationForm.referees as referee>
		                	<div class="sub_section_amdin">
		                
		                	        <!-- Header -->
					                <div class="admin_row">
					                	<label class="admin_header">Reference (${referee_index + 1})</label>
					                  
					                    <div class="field">
					                    	&nbsp
					                    </div>
									</div>
		                	
		                
			                  		<!-- First name -->
			                  		<div class="admin_row">
			                    		<span class="admin_row_label">First Name</span>
			                    		
			                    		<div class="field" id="ref_firstname">${(referee.firstname?html)!} </div>
			                  		</div>
			                
			                  		<!-- Last name -->
			                  		<div class="admin_row">
			                    		<span class="admin_row_label">Last Name</span>
			             
			                    		<div class="field" id="ref_lastname">${(referee.lastname?html)!}</div>
			                  		</div>
		                  
			                  		<!-- Employer / company name -->
			                  		<div class="admin_row">
			                    		<span class="admin_row_label">Employer</span>
			
			                    		<div class="field" id="ref_employer">${(referee.jobEmployer?html)!} </div>
			                  		</div>
			                
			                  		<!-- Position title -->
			                  		<div class="admin_row">
			                    		<span class="admin_row_label">Position</span>
						
			                 			<div class="field" id="ref_position">${(referee.jobTitle?html)!} </div>
			                  		</div>
		                  
			                  		<!-- Address body -->
			                  		<div class="admin_row">
			                    		<span class="admin_row_label">Address</span>
			         
			                    		<div class="field" id="ref_address_location">${(referee.addressLocation?html)!} </div>
			                  		</div>
			                
			           
			                
			                  		<!-- Country -->
			                  		<div class="admin_row">
			                    		<span class="admin_row_label">Country</span>
										<div class="field" id="ref_address_country">${(referee.addressCountry.name?html)!} </div>
			                  		</div>
		                	
		                
			                  		<!-- Email address -->
			                  		<div class="admin_row">
			                    		<span class="admin_row_label">Email</span>
			                  
			                    		<div class="field" id="ref_email">${(referee.email?html)!} </div>
			                  		</div>
		
			                  		<!-- Telephone -->
			                  		<div class="admin_row">
			                  		    <span class="admin_row_label">Telephone</span>
			                  		
			                    		<div class="field"  id="ref_phone">${(referee.phoneNumber?html)!} </div>
			                  		</div>
		
				                  	<!-- Skype address -->
				                  	<div class="admin_row">
			                    		<span class="admin_row_label">Skype</span>
			                    		
			                    		<div class="field" id="ref_messenger">${(referee.messenger?html)!} </div>
			                  		</div>
		                  		
			                  		<div class="admin_row">
					                  	<span class="admin_row_label">Document</span>
					                  
					                    <div class="field" id="referenceDocument">	&nbsp; </div>
				                    </div>		              
				                    <div class="admin_row">			        
				                    	<span class="admin_row_label">Uploaded date</span>	                   		          
					                   <div class="field" id="referenceUpdated">&nbsp; </div>
				                    </div>
				               
			                	</div>

		            </#list>
              	</#if>

					<div class="buttons">
		            	<button class="blue" type="button" value="close" id="refereeCloseButton">Close</button>
		            </div>
				</form>		       
			</div>
              	
            <script type="text/javascript" src="<@spring.url '/design/default/js/application/staff/referee.js'/>"></script>