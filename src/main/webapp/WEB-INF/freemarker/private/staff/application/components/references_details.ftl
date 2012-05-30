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
	                
	                		<!-- All hidden input - Start -->
									<input type="hidden" id="${referee.id?string('#######')}_refereeId" value="${referee.id?string('#######')}"/>
                                    <input type="hidden" id="${referee.id?string('#######')}_firstname" value="${(referee.firstname?html)!}"/>
                                    <input type="hidden" id="${referee.id?string('#######')}_phone" value="${(referee.phoneNumber?html)!}"/>
                                    <#if referee.messenger??>
                                    	<input type="hidden" id="${referee.id?string('#######')}_messenger" value="${(referee.messenger?html)!}"/>
                                    <#else>
                                    	<input type="hidden" id="${referee.id?string('#######')}_messenger" value=" "/>
                                    </#if>
                                    <input type="hidden" id="${referee.id?string('#######')}_lastname" value="${(referee.lastname?html)!}"/>                                    
                                    <input type="hidden" id="${referee.id?string('#######')}_jobEmployer" value="${(referee.jobEmployer?html)!}"/>
                                    <input type="hidden" id="${referee.id?string('#######')}_jobTitle" value="${(referee.jobTitle?html)!}"/>
                                    <input type="hidden" id="${referee.id?string('#######')}_addressLocation" value="${(referee.addressLocation?html)!}"/>
                                    
                                    <input type="hidden" id="${referee.id?string('#######')}_addressCountry" <#if referee.addressCountry??> value="${(referee.addressCountry.name?html)!}" </#if>/>
                                    <input type="hidden" id="${referee.id?string('#######')}_lastUpdated" value="<#if referee.hasProvidedReference() > 
			                    		Provided ${(referee.reference.lastUpdated?string('dd-MMM-yyyy'))!}
			                    	<#else>
			                    		Not provided
			                    	</#if>"/>
			                    	 
			                    	 <input type="hidden" id="${referee.id?string('#######')}_reference_document_url" value="<#if referee.hasProvidedReference() && referee.reference.document?? >
			                    	 	<@spring.url '/download/reference?referenceId=${encrypter.encrypt(referee.reference.id)}'/></#if>"
			                    	 />
			                    	 <input type="hidden" id="${referee.id?string('#######')}_reference_document_name" value="<#if referee.hasProvidedReference()><#if referee.reference.document??>${referee.reference.document.fileName?html}</#if><#else>No document uploaded</#if>" />
                                     <input type="hidden" id="${referee.id?string('#######')}_email" value="${(referee.email?html)!}"/>
								
								<!-- All hidden input - End --> 
	                		
	                			<!-- Rendering part - Start -->
	                
		                	<div class="row-group">
		                
		                	        <!-- Header -->
					                <div class="admin_row">
					                	<label class="admin_header">Reference (${referee_index + 1})<#if referee.declined> - Declined</#if></label>
					                  
					                    <div class="field">
					                    	&nbsp
					                    </div>
									</div>
		                	
		                
			                  		<!-- First name -->
			                  		<div class="admin_row">
			                    		<span class="admin_row_label">First Name</span>
			                    		
			                    		<div class="field" id="ref_firstname">${(referee.firstname?html)!"Not Provided"} </div>
			                  		</div>
			                
			                  		<!-- Last name -->
			                  		<div class="admin_row">
			                    		<span class="admin_row_label">Last Name</span>
			             
			                    		<div class="field" id="ref_lastname">${(referee.lastname?html)!"Not Provided"}</div>
			                  		</div>
		                  
			                  		<!-- Employer / company name -->
			                  		<div class="admin_row">
			                    		<span class="admin_row_label">Employer</span>
			
			                    		<div class="field" id="ref_employer">${(referee.jobEmployer?html)!"Not Provided"} </div>
			                  		</div>
			                
			                  		<!-- Position title -->
			                  		<div class="admin_row">
			                    		<span class="admin_row_label">Position</span>
						
			                 			<div class="field" id="ref_position">${(referee.jobTitle?html)!"Not Provided"} </div>
			                  		</div>
		                  
			                  		<!-- Address body -->
			                  		<div class="admin_row">
			                    		<span class="admin_row_label">Address</span>
			         
			                    		<div class="field" id="ref_address_location">${(referee.addressLocation?html)!"Not Provided"} </div>
			                  		</div>
			                
			           
			                
			                  		<!-- Country -->
			                  		<div class="admin_row">
			                    		<span class="admin_row_label">Country</span>
										<div class="field" id="ref_address_country">${(referee.addressCountry.name?html)!"Not Provided"} </div>
			                  		</div>
		                	
		                
			                  		<!-- Email address -->
			                  		<div class="admin_row">
			                    		<span class="admin_row_label">Email</span>
			                  
			                    		<div class="field" id="ref_email">${(referee.email?html)!"Not Provided"} </div>
			                  		</div>
		
			                  		<!-- Telephone -->
			                  		<div class="admin_row">
			                  		    <span class="admin_row_label">Telephone</span>
			                  		
			                    		<div class="field"  id="ref_phone">${(referee.phoneNumber?html)!"Not Provided"} </div>
			                  		</div>
		
				                  	<!-- Skype address -->
				                  	<div class="admin_row">
			                    		<span class="admin_row_label">Skype</span>
			                    		
			                    		<div class="field" id="ref_messenger">${(referee.messenger?html)!"Not Provided"} </div>
			                  		</div>
		                  		
			                  		<div class="admin_row">
					                  	<span class="admin_row_label">Document</span>
					                  	<#if referee.hasProvidedReference() >
					                  		<div class="field">
					                  			<a href="<@spring.url '/download/reference?referenceId=${encrypter.encrypt(referee.reference.id)}'/>">
					                  			${referee.reference.document.fileName?html}</a>
					                  		</div>
					                  	<#else> 
					                  		<div class="field" id="referenceDocument">Not Provided</div> 
					                  	</#if>
				                    </div>		              
				                    <div class="admin_row">			        
				                    	<span class="admin_row_label">Uploaded date</span>
					                  	<#if referee.hasProvidedReference() >
					                  		<div class="field">
					                  			${(referee.reference.lastUpdated?string('dd-MMM-yyyy'))!}
					                  		</div>
					                  	<#else> 
					                  		<div class="field" id="referenceUpdated">Not Provided</div> 
					                  	</#if>
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