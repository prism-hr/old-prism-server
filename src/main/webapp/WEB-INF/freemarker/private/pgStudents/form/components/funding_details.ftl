<#import "/spring.ftl" as spring />
 <h2 class="open">
                          <span class="left"></span><span class="right"></span><span class="status"></span>
                          Funding
                        </h2>
                        <div>
                            <br/>
                            <div>
                            <textarea id="funding" name="funding" cols="45" rows="7">${model.funding.funding!}</textarea>
                             <#if model.hasError('funding')>                           
                                <span style="color:red;"><@spring.message  model.result.getFieldError('funding').code /></span>                           
                            </#if>
                            
                            <#if model.hasError('funding.funding')>                           
                                <span style="color:red;"><@spring.message  model.result.getFieldError('funding.funding').code /></span>                           
                            </#if>
                            
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