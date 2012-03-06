<#import "/spring.ftl" as spring />
 <h2 class="open">
                          <span class="left"></span><span class="right"></span><span class="status"></span>
                          Funding
                        </h2>
                        <div>
                            <br/>
                            <div>
                            
                             <table cellspacing=10>
                                 <tr align=left><th>Type</th><th>Value</th><th>Award Date</th></tr>
                                <#list model.applicationForm.fundings as funding>
                                <tr>
                                    <td>${funding.type}</td>
                                    <td>${funding.value}</td>
                                    <td>${funding.awardDate?string('yyyy/MM/dd')}</td>
                               </tr>
                            </#list>
                            </table>
                            
                            <table cellspacing=10>
                                <tr align=left></tr>
                                <tr><td>Type</td>
                                <td>
                                <input type="text" id="fundingType" name="fundingType" value="${model.funding.fundingType!}"/>
                                <#if model.hasError('fundingType')>                           
                                    <span style="color:red;"><@spring.message  model.result.getFieldError('fundingType').code /></span>                           
                                </#if>
                                </td>
                                </tr>
                                
                                <tr><td>Value</td>
                                <td>
                                <input type="text" id="fundingValue" name="fundingValue" value="${model.funding.fundingValue!}"/>
                                <#if model.hasError('fundingValue')>                           
                                    <span style="color:red;"><@spring.message  model.result.getFieldError('fundingValue').code /></span>                           
                                </#if>
                                </td>
                                </tr>
                                
                                <tr><td>Description</td>
                                <td>
                                <input type="text" id="fundingDescription" name="fundingDescription" value="${model.funding.fundingDescription!}"/>
                                <#if model.hasError('fundingDescription')>                           
                                    <span style="color:red;"><@spring.message  model.result.getFieldError('fundingDescription').code /></span>                           
                                </#if>
                                </td>
                                </tr>
                                
                                <tr><td>Award Date</td>
                                <td>
                                <input type="text" id="fundingAwardDate" name="fundingAwardDate" value="${(model.funding.fundingAwardDate?string('yyyy/MM/dd'))!}"/>
                                <#if model.hasError('fundingAwardDate')>                           
                                    <span style="color:red;"><@spring.message  model.result.getFieldError('fundingAwardDate').code /></span>                           
                                </#if>
                                </td>
                                </tr>
                                
                                </table>
                            
                            </div>
                            <br/>
                            <div class="buttons">
                                    <a class="button blue" href="#">Close</a>
                                    <#if !model.applicationForm.isSubmitted()>
                                        <a class="button blue" type="submit" id="fundingSaveButton">Save</a>
                                    </#if>    
                            </div>
                        </div>
<script type="text/javascript" src="<@spring.url '/design/default/js/application/funding.js'/>"></script>