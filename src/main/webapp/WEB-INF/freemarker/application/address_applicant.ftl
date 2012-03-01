<#import "/spring.ftl" as spring />
 <h2 class="open">
                          <span class="left"></span><span class="right"></span><span class="status"></span>
                          Address
                        </h2>
                        <div>
                            <br/>
                            <div>
                            <textarea id="address" name="address" cols="45" rows="7">${model.address.address!}</textarea>
                            <#if model.hasError('address')>                           
                                <span style="color:red;"><@spring.message  model.result.getFieldError('address').code /></span>                           
                            </#if>
                            
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