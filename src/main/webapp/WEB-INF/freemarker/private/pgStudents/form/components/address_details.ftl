<#import "/spring.ftl" as spring />
 <h2 class="open">
                          <span class="left"></span><span class="right"></span><span class="status"></span>
                          Address
                        </h2>
                        <div>
                            <br/>
                            <div>
                            <#if model.hasError('numberOfAddresses')>                           
                                    <p style="color:red;"><@spring.message  model.result.getFieldError('numberOfAddresses').code /></p>                        
                            </#if>
                            <#if model.hasError('numberOfContactAddresses')>                           
                                    <p style="color:red;"><@spring.message  model.result.getFieldError('numberOfContactAddresses').code /></p>                      
                            </#if>
                            
                             <table cellspacing=10>
                                 <tr align=left><th>Address</th><th>From</th><th>To</th><th></th></tr>
                                <#list model.applicationForm.addresses as address>
                                <tr>
                                    <td>${address.id}, ${address.location}, ${address.postCode}</td>
                                    <td>${address.startDate?string('yyyy/MM/dd')}</td>
                                    <td>${(address.endDate?string('yyyy/MM/dd'))!}</td>
                                    <td><a class="button blue" type="submit" name="addressEditButton" id="address_${address.id}">Edit</a></td>
                                    <input type="hidden" id="${address.id}_addressIdDP" value="${address.id}"/>
                                    <input type="hidden" id="${address.id}_locationDP" value="${address.location}"/>
                                    <input type="hidden" id="${address.id}_postCodeDP" value="${address.postCode}"/>
                                    <input type="hidden" id="${address.id}_countryDP" value="${address.country}"/>
                                    <input type="hidden" id="${address.id}_startDateDP" value="${address.startDate?string('yyyy/MM/dd')}"/>
                                    <input type="hidden" id="${address.id}_endDateDP" value="${(address.endDate?string('yyyy/MM/dd'))!}"/>
                                    <input type="hidden" id="${address.id}_purposeDP" value="${address.purpose}"/>
                                    <input type="hidden" id="${address.id}_contactAddressDP" value="${address.contactAddress}"/>
                               </tr>
                            </#list>
                            </table>
                            
                            <input type="hidden" id="addressId" name="addressId"/>
                            <table cellspacing=10>
                                <tr align=left></tr>
                                <tr><td>Street Name & Number</td>
                                <td>
                                <input type="text" id="addressLocation" name="addressLocation" value="${model.address.addressLocation!}"/>
                                <#if model.hasError('addressLocation')>                           
                                    <span style="color:red;"><@spring.message  model.result.getFieldError('addressLocation').code /></span>                           
                                </#if>
                                </td>
                                </tr>
                                
                                <tr>
                                <td>Postal Code</td>
                                <td><input type="text" id="addressPostCode" name="addressPostCode" value="${model.address.addressPostCode!}"/>
                                <#if model.hasError('addressPostCode')>                           
                                    <span style="color:red;"><@spring.message  model.result.getFieldError('addressPostCode').code /></span>                           
                                </#if>
                                </td>
                                </tr>
                            
                                <tr>
                                <td>Country</td>
                                <td>
                                <select name="addressCountry" id="addressCountry">
                                    <#list model.countries as country>
                                        <option value="${country.name}">${country.name}</option>               
                                    </#list>
                                <select>
                                <#if model.hasError('addressCountry')>                           
                                    <span style="color:red;"><@spring.message  model.result.getFieldError('addressCountry').code /></span>                           
                                </#if>
                                </td>
                                </tr>
                            
                                <tr>
                                <td>Start Date</td>
                                <td><input type="text" id="addressStartDate" name="addressStartDate" value="${(model.address.addressStartDate?string('yyyy/MM/dd'))!}"/>
                                <#if model.hasError('addressStartDate')>                           
                                    <span style="color:red;"><@spring.message  model.result.getFieldError('addressStartDate').code /></span>                           
                                </#if>
                                </td>
                                </tr>
                            
                                <tr>
                                <td>End Date</td>
                                <td><input type="text" id="addressEndDate" name="addressEndDate" value="${(model.address.addressEndDate?string('yyyy/MM/dd'))!}"/>
                                <#if model.hasError('addressEndDate')>                           
                                    <span style="color:red;"><@spring.message  model.result.getFieldError('addressEndDate').code /></span>                           
                                </#if>
                                </td>
                                </tr>
                                
                                <tr>
                                <td>Reason for living here</td>
                                <td><input type="text" id="addressPurpose" name="addressPurpose" value="${model.address.addressPurpose!}"/>
                                <#if model.hasError('addressPurpose')>                           
                                    <span style="color:red;"><@spring.message  model.result.getFieldError('addressPurpose').code /></span>                           
                                </#if>
                                </td>
                                </tr>
                            
                            </table>
                            <label>This is my contact address</label>
                            <label><input type="radio" name="isCA" id="radioYES" value="YES"/> Yes</label>
                            <label><input type="radio" name="isCA" id="radioNO" value="NO"/> No</label>
                            <input type="hidden" id="addressContactAddress" name="addressContactAddress"/>
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