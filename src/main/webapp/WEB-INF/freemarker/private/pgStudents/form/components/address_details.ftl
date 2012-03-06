<#import "/spring.ftl" as spring />
 <h2 class="open">
                          <span class="left"></span><span class="right"></span><span class="status"></span>
                          Address
                        </h2>
                        <div>
                            <br/>
                            <div>
                            <#if model.hasError('numberOfAddresses')>                           
                                    <span style="color:red;"><@spring.message  model.result.getFieldError('numberOfAddresses').code /></span><br/>                        
                            </#if>
                            <label>Saved Addresses</label>
                            <table>
                            <#list model.applicationForm.addresses as address>
                                <tr><td>- ${address.street}, ${address.city}, ${address.postCode}, ${address.country}     ${address.startDate?date}     ${address.endDate?date}</td></tr>
                            </#list>
                            </table>
                            
                            <table>
                                <tr><td>Street Name & Number<input type="text" id="street" name="street"/>
                                <#if model.hasError('street')>                           
                                    <span style="color:red;"><@spring.message  model.result.getFieldError('street').code /></span>                           
                                </#if></td></tr>
                                
                                <tr><td>Postal Code<input type="text" id="postCode" name="postCode"/>
                                <#if model.hasError('postCode')>                           
                                    <span style="color:red;"><@spring.message  model.result.getFieldError('postCode').code /></span>                           
                                </#if></td></tr>
                            
                                <tr><td>City<input type="text" id="city" name="city"/>
                                <#if model.hasError('city')>                           
                                    <span style="color:red;"><@spring.message  model.result.getFieldError('city').code /></span>                           
                                </#if></td></tr>
                            
                                <tr><td>Country<input type="text" id="country" name="country"/>
                                <#if model.hasError('country')>                           
                                    <span style="color:red;"><@spring.message  model.result.getFieldError('country').code /></span>                           
                                </#if></td></tr>
                            
                                <tr><td>Start Date<input type="text" id="startDate" name="startDate"/>
                                <#if model.hasError('startDate')>                           
                                    <span style="color:red;"><@spring.message  model.result.getFieldError('startDate').code /></span>                           
                                </#if></td></tr>
                            
                                <tr><td>End Date<input type="text" id="endDate" name="endDate"/>
                                <#if model.hasError('endDate')>                           
                                    <span style="color:red;"><@spring.message  model.result.getFieldError('endDate').code /></span>                           
                                </#if></td></tr>
                            </table>
                            
                            </div>
                            <br/>
                            <div class="buttons">
                                    <a class="button blue" href="#">Close</a>
                                    <#if !model.applicationForm.isSubmitted()>
                                        <a class="button blue" type="submit" id="addressSaveButton">Save</a>
                                    </#if>
                            </div>
                        </div>
                        
<script type="text/javascript" src="<@spring.url '/design/default/js/application/address.js'/>"></script>