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
				
            	<#if hasReferees>
	            	<table class="existing">
	                	
	                	<colgroup>
		                  	<col style="width: 30px" />
		                  	<col style="width: 160px" />
		                  	<col style="width: 160px" />
		                  	<col style="width: 260px" />
		                  	<col />
		                  	<col style="width: 30px" />
	                	</colgroup>
	                
	                	<thead>
	                  		<tr>
			                    <th colspan="2">First name</th>
			                    <th>Surname</th>
			                    <th>Job Title</th>
			                    <th>Email</th>
			                    <th>Reference</th>
		                  	</tr>
	                	</thead>
	                	
	                	<tbody>
	                		<#list applicationForm.referees as referee>
			                  	<tr>
				                    <td><a class="row-arrow" name="refereeEditButton" id="referee_${referee.id?string('#######')}">-</a></td>
				                    <td>${(referee.firstname?html)!}</td>
				                    <td>${(referee.lastname?html)!}</td>
				                    <td>${(referee.jobTitle?html)!}</td>
				                    <td>${(referee.email?html)!}</td>
				                    <td>
					                    <#if referee.hasProvidedReference() ><a href="<@spring.url '/download/reference?referenceId=${referee.reference.id?string("#######")}'/>">${referee.reference.document.fileName?html}</a><#else> - </#if>
					                 </td>
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
			                    	 	<@spring.url '/download/reference?referenceId=${referee.reference.id?string("#######")}'/></#if>"
			                    	 />
			                    	 <input type="hidden" id="${referee.id?string('#######')}_reference_document_name" value="<#if referee.hasProvidedReference()><#if referee.reference.document??>${referee.reference.document.fileName?html}</#if><#else>No document uploaded</#if>" />
                                    <input type="hidden" id="${referee.id?string('#######')}_email" value="${(referee.email?html)!}"/>
									
									

			                  	</tr>
		                  	</#list>
	                	</tbody>
	              	
	              	</table>
              	</#if>
              	
              	<input type="hidden" id="refereeId" name="refereeId"/>
              	
              	<form>
                
                	<div>
                
                  		<!-- First name -->
                  		<div class="row">
                    		<span class="label">First Name</span>
                    	
                    		<div class="field" id="ref_firstname">&nbsp; </div>
                  		</div>
                
                  		<!-- Last name -->
                  		<div class="row">
                    		<span class="label">Last Name</span>
             
                    		<div class="field" id="ref_lastname">&nbsp; </div>
                  		</div>
                  	</div>	
                


                	<div>
                  
                  		<!-- Employer / company name -->
                  		<div class="row">
                    		<span class="label">Employer</span>

                    		<div class="field" id="ref_employer">&nbsp; </div>
                  		</div>
                
                  		<!-- Position title -->
                  		<div class="row">
                    		<span class="label">Position</span>
			
                 			<div class="field" id="ref_position">&nbsp; </div>
                  		</div>
                	</div>

                	<div>
                	
                  		<p><strong>Contact Details</strong></p>
                  
                  		<!-- Address body -->
                  		<div class="row">
                    		<span class="label">Address</span>
         
                    		<div class="field" id="ref_address_location">&nbsp; </div>
                  		</div>
                
           
                
                  		<!-- Country -->
                  		<div class="row">
                    		<span class="label">Country</span>
							<div class="field" id="ref_address_country">&nbsp; </div>
                  		</div>
                	
                	</div>

                	<div>
                  	
                  		<p><strong>Contact Details</strong></p>
                
                  		<!-- Email address -->
                  		<div class="row">
                    		<span class="label">Email</span>
                  
                    		<div class="field" id="ref_email">&nbsp; </div>
                  		</div>

                  		<!-- Telephone -->
                  		<div class="row">
                  		    <span class="label">Telephone</span>
                  		
                    		<div class="field"  id="ref_phone">&nbsp; </div>
                  		</div>

	                  	<!-- Skype address -->
	                  	<div class="row">
                    		<span class="label">Skype</span>
                    		
                    		<div class="field" id="ref_messenger">&nbsp; </div>
                  		</div>
                  		
                	</div>
						<div>
	                  	     <p><strong>Reference</strong></p>      
	                  		<div class="row">
			                  	<span class="label">Document</span>
			                  
			                    <div class="field" id="referenceDocument">	&nbsp; </div>
		                    </div>		              
		                    <div class="row">			        
		                    	<span class="label">Uploaded date</span>	                   		          
			                   <div class="field" id="referenceUpdated">&nbsp; </div>
		                    </div>
		               
	                	</div>
	                	
                	<div class="buttons">
                  		<button class="blue" type="button" value="close" id="refereeCloseButton">Close</button>
                	</div>

				</form>
            
            </div>
            <script type="text/javascript" src="<@spring.url '/design/default/js/application/staff/referee.js'/>"></script>