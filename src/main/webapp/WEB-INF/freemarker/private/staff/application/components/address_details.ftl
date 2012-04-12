<#if applicationForm.addresses?has_content>
	<#assign hasAddresses = true>
<#else>
	<#assign hasAddresses = false>
</#if> 
 
<#import "/spring.ftl" as spring />

	<h2 id="address-H2" class="empty">
		<span class="left"></span><span class="right"></span><span class="status"></span>
	    Address
	</h2>
	    
	<div>
	
        	<form>
				
				<input type="hidden" id="addressId" name="addressId"/>
            	<div>
                	<h3>Address</h3>
                  
                  	<!-- Address body -->
                  	<div class="row">
                    	<span class="label">Current Address</span>
                    	<div class="field">
                    	 ${(applicationForm.currentAddress.location?html)!}
                    	</div>
                  	</div>

                  	<!-- Country -->
                  	<div class="row">
                    	<span class="label">Country</span>
	                    <div class="field">${(applicationForm.currentAddress.country.name?html)!}               
						</div>
					</div>
				</div>
					
				<div>	
					                   <!-- Address body -->
                    <div class="row">
                        <span class="label">Contact Address</span>    
                        <div class="field">
                      ${(applicationForm.contactAddress.location?html)!}	
                        </div>
                    </div>

                    <!-- Country -->
                    <div class="row">
                        <span class="label">Country</span>
                        <div class="field">
                        	${(applicationForm.contactAddress.country.name?html)!}                          
                        </div>
                    </div>
					
				</div>

                <div class="buttons">
                  	<button id="addressCloseButton" class="blue" type="button">Close</button>
                </div>

			</form>
	</div>
	<script type="text/javascript" src="<@spring.url '/design/default/js/libraries.js'/>"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/application/common.js'/>"></script>

<script type="text/javascript" src="<@spring.url '/design/default/js/application/address.js'/>"></script>