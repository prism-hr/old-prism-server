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
                            <#if model.hasError('numberOfContactAddresses')>                           
                                    <span style="color:red;"><@spring.message  model.result.getFieldError('numberOfContactAddresses').code /></span><br/>                        
                            </#if>
                            
                             <table cellspacing=10>
                                 <tr align=left><th>Address</th><th>From</th><th>To</th></tr>
                                <#list model.applicationForm.addresses as address>
                                <tr>
                                    <td>${address.location}, ${address.postCode}</td>
                                    <td>${address.startDate?string('yyyy/MM/dd')}</td>
                                    <td>${address.endDate?string('yyyy/MM/dd')}</td>
                               </tr>
                            </#list>
                            </table>
                            
                            <table cellspacing=10>
                                <tr align=left></tr>
                                <tr><td>Street Name & Number</td>
                                <td>
                                <input type="text" id="location" name="location" value="${model.address.location!}"/>
                                <#if model.hasError('location')>                           
                                    <span style="color:red;"><@spring.message  model.result.getFieldError('location').code /></span>                           
                                </#if>
                                </td>
                                </tr>
                                
                                <tr>
                                <td>Postal Code</td>
                                <td><input type="text" id="postCode" name="postCode" value="${model.address.postCode!}"/>
                                <#if model.hasError('postCode')>                           
                                    <span style="color:red;"><@spring.message  model.result.getFieldError('postCode').code /></span>                           
                                </#if>
                                </td>
                                </tr>
                            
                                <tr>
                                <td>Country</td>
                                <td><input type="text" id="country" name="country" value="${model.address.country!}"/>
                                <#if model.hasError('country')>                           
                                    <span style="color:red;"><@spring.message  model.result.getFieldError('country').code /></span>                           
                                </#if>
                                </td>
                                </tr>
                            
                                <tr>
                                <td>Start Date</td>
                                <td><input type="text" id="startDate" name="startDate" value="${(model.address.startDate?string('yyyy/MM/dd'))!}"/>
                                <#if model.hasError('startDate')>                           
                                    <span style="color:red;"><@spring.message  model.result.getFieldError('startDate').code /></span>                           
                                </#if>
                                </td>
                                </tr>
                            
                                <tr>
                                <td>End Date</td>
                                <td><input type="text" id="endDate" name="endDate" value="${(model.address.endDate?string('yyyy/MM/dd'))!}"/>
                                <#if model.hasError('endDate')>                           
                                    <span style="color:red;"><@spring.message  model.result.getFieldError('endDate').code /></span>                           
                                </#if>
                                </td>
                                </tr>
                                
                                <tr>
                                <td>Reason for living here</td>
                                <td><input type="text" id="purpose" name="purpose" value="${model.address.purpose!}"/>
                                <#if model.hasError('purpose')>                           
                                    <span style="color:red;"><@spring.message  model.result.getFieldError('purpose').code /></span>                           
                                </#if>
                                </td>
                                </tr>
                            
                            </table>
                            <label>This is my contact address</label>
                            <label><input type="radio" name="isCA" value="YES"/> Yes</label>
                            <label><input type="radio" name="isCA" value="NO"/> No</label>
                            <input type="hidden" id="contactAddress" name="contactAddress"/>
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