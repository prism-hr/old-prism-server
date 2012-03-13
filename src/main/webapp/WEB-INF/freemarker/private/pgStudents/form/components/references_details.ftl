<#import "/spring.ftl" as spring />
 <h2 class="open">
                          <span class="left"></span><span class="right"></span><span class="status"></span>
                          References
                        </h2>
                        <div>
                            <br/>
                            <#if model.hasError('numberOfReferees')>                           
	        					<span class="invalid"><@spring.message  model.result.getFieldError('numberOfReferees').code /></span><br/>                        
	       					 </#if>
                            <div>
                            
                             <table cellspacing=10>
                                 <tr align=left><th>First Name</th><th>Surname</th><th>Relationship</th><th>Email</th></tr>
                                <#list model.applicationForm.referees as referee>
                                <tr>
                                    <td>${referee.firstname!}</td>
                                    <td>${referee.lastname!}</td>
                                    <td>${referee.relationship!}</td>
                                    <td>${referee.email!}</td>
                                    <td><a class="button blue" type="submit" name="refereeEditButton" id="referee_${referee.id!}">Edit</a></td>
                                    <input type="hidden" id="${referee.id!}_refereeId" value="${referee.id!}"/>
                                    <input type="hidden" id="${referee.id!}_firstname" value="${referee.firstname!}"/>
                                    <input type="hidden" id="${referee.id!}_lastname" value="${referee.lastname!}"/>
                                    <input type="hidden" id="${referee.id!}_relationship" value="${referee.relationship!}"/>
                                    <input type="hidden" id="${referee.id!}_jobEmployer" value="${referee.jobEmployer!}"/>
                                    <input type="hidden" id="${referee.id!}_jobTitle" value="${referee.jobTitle!}"/>
                                    <input type="hidden" id="${referee.id!}_addressLocation" value="${referee.addressLocation!}"/>
                                    <input type="hidden" id="${referee.id!}_addressPostcode" value="${referee.addressPostcode!}"/>
                                    <input type="hidden" id="${referee.id!}_addressCountry" value="${referee.addressCountry!}"/>
                                    <input type="hidden" id="${referee.id!}_email" value="${referee.email!}"/>
									<#list referee.phoneNumbersRef! as phoneNumber>
                    				<span name="${referee.id!}_hiddenPhones" style="display:none">
                   		 				${phoneNumber.telephoneType.displayValue!} ${phoneNumber.telephoneNumber!} <a class="button" id="delBtn">delete</a>
										<input type="hidden" name="phoneNumbersRef"  value='{"type" :"${phoneNumber.telephoneType!}", "number":"${phoneNumber.telephoneNumber!}"}' />								
									<br/>
									</span>
                   				 	</#list>
                   				 	<#list referee.messengersRef! as messenger>
                    				<span name="${referee.id!}_hiddenMessengers" style="display:none">
                   		 				${messenger.messengerAddress!} <a class="button" id="delBtn" >delete</a>
										<input type="hidden" name="messengersRef" value='{"address":"${messenger.messengerAddress!}"}' />								
									<br/>
									</span>
                   				 	</#list>
                               </tr>
                            </#list>
                            </table>
                            <input type="hidden" id="refereeId" name="refereeId"/>
                            <table cellspacing=10>
                                <tr align=left></tr>
                                <tr><td>First Name</td>
                                <td>
                                <input type="text" id="ref_firstname" name="ref_firstname" value="${model.referee.firstname!}"/>
                                 
                           		 <#if model.hasError('firstname')>                           
                            		<span class="invalid"><@spring.message  model.result.getFieldError('firstname').code /></span>                           
                            	</#if>
                                </td>
                                </tr>
                                
                                <tr><td>Last Name</td>
                                <td>
                                <input type="text" id="ref_lastname" name="ref_lastname" value="${model.referee.lastname!}"/>
                                 <#if model.hasError('lastname')>                           
                            		<span class="invalid"><@spring.message  model.result.getFieldError('lastname').code /></span>                           
                            	</#if>
                                </td>
                                </tr>
                                
                                <tr><td>Relationship</td>
                                <td>
                                <input type="text" id="ref_relationship" name="ref_relationship" value="${model.referee.relationship!}"/>
                                <#if model.hasError('relationship')>                           
                            		<span class="invalid"><@spring.message  model.result.getFieldError('relationship').code /></span>                           
                            	</#if>
                                </td>
                                </tr>
                                
                                <tr><td>Employer</td>
                                <td>
                                <input type="text" id="ref_employer" name="ref_employer" value="${model.referee.jobEmployer!}"/>
                                 <#if model.hasError('jobEmployer')>                           
                            		<span class="invalid"><@spring.message  model.result.getFieldError('jobEmployer').code /></span>                           
                            	</#if>
                                </td>
                                </tr>
                                
                                <tr><td>Position</td>
                                <td>
                                <input type="text" id="ref_position" name="ref_position" value="${model.referee.jobTitle!}"/>
                                <#if model.hasError('jobTitle')>                           
                            		<span class="invalid"><@spring.message  model.result.getFieldError('jobTitle').code /></span>                           
                            	</#if>
                                </td>
                                </tr>

                                <tr><td>Address Location</td>
                                <td>
                                <input type="text" id="ref_address_location" name="ref_address_location" value="${model.referee.addressLocation!}"/>
                                <#if model.hasError('addressLocation')>                           
                            		<span class="invalid"><@spring.message  model.result.getFieldError('addressLocation').code /></span>                           
                            	</#if>
                                </td>
                                </tr>
                                
                                <tr><td>Address Post Code</td>
                                <td>
                                <input type="text" id="ref_address_postcode" name="ref_address_postcode" value="${model.referee.addressPostcode!}"/>
                                <#if model.hasError('addressPostcode')>                           
                            		<span class="invalid"><@spring.message  model.result.getFieldError('addressPostcode').code /></span>                           
                            	</#if>
                                </td>
                                </tr>
                                
                                <tr><td>Address Country</td>
                                <td>
                                <input type="text" id="ref_address_country" name="ref_address_country" value="${model.referee.addressCountry!}"/>
                                <#if model.hasError('addressCountry')>                           
                            		<span class="invalid"><@spring.message  model.result.getFieldError('addressCountry').code /></span>                           
                            	</#if>
                                </td>
                                </tr>
                                
                                <tr><td>Email</td>
                                <td>
                                <input type="text" id="ref_email" name="ref_email" value="${model.referee.email!}"/>
                                <#if model.hasError('email')>                           
                            		<span class="invalid"><@spring.message  model.result.getFieldError('email').code /></span>                           
                            	</#if>
                                </td>
                                </tr>
                                
                				<tr><td>Telephone</td>
                    			<div id="phonenumbersref"  class="field">
                   				 <#list model.referee.phoneNumbers! as phoneNumber>
                    				<span name="phone_number_ref">
                   		 				${phoneNumber.telephoneType.displayValue} ${phoneNumber.telephoneNumber} <a class="button">delete</a>
										<input type="hidden" name="phoneNumbersRef" value='{"type" :"${phoneNumber.telephoneType}", "number":"${phoneNumber.telephoneNumber}"}' />								
									<br/>
									</span>
                   				 </#list>
                  					</div>
                    			<td>
                    			<select class="full" id="phoneTypeRef">
                    			 <#list model.phoneTypes as phoneType >
                      				<option value="${phoneType}">${phoneType.displayValue}</option>
                      			</#list>
                      			</select>
                      			</td>
                      			<td>
	                   		 	<input type="text" placeholder="Number" id="phoneNumberRef"/>
                     			 	<a id="addPhoneRefButton" class="button" style="width: 110px;">Add Phone</a>
                     			 	 <#if model.hasError('phoneNumbersRef')>                           
                            			<span class="invalid"><@spring.message  model.result.getFieldError('phoneNumbersRef').code /></span>                           
                            		</#if>
                     			</td>
                  				</tr>
                  				
                  				<tr><td>Messenger</td>
                    			<div id="messengersref"  class="field">
                   				 <#list model.messengers! as messenger>
                    				<span name="messenger_ref">
                   		 				${messenger.messengerAddress} <a class="button" id = "mesDel" >delete</a>
										<input type="hidden" name="messengersRef" value='{"address":"${messenger.messengerAddress}"}' />								
									<br/>
									</span>
                   				 </#list>
                  					</div>
                      			<td>
	                   		 	<input type="text" placeholder="Address" id="messengerAddressRef"/>
                     			 	<a id="addMessengerRefButton" class="button" style="width: 110px;">Add Messenger</a>
                     			</td>
                  				</tr>
                                
                                </table>
                            
                            </div>
                            <br/>
                            <div class="buttons">
                                    <a class="button blue" href="#">Close</a>
                                    <#if !model.applicationForm.isSubmitted()>
                                        <a class="button blue" type="submit" id="refereeSaveButton">Save</a>
                                    </#if>    
                            </div>
                        </div>
<script type="text/javascript" src="<@spring.url '/design/default/js/application/referee.js'/>"></script>   