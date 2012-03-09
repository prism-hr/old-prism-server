<#import "/spring.ftl" as spring />
 <h2 class="open">
                          <span class="left"></span><span class="right"></span><span class="status"></span>
                          Employment
                        </h2>
                        <div>
                            <br/>
                            <div>
                            
                             <table cellspacing=10>
                                 <tr align=left><th>Title</th><th>From</th><th>To</th><th></th></tr>
                                <#list model.applicationForm.employmentPositions as position>
                                <tr>
                                    <td>${position.position_title}</td>
                                    <td>${position.position_startDate?string('yyyy/MM/dd')}</td>
                                    <td>${position.position_endDate?string('yyyy/MM/dd')}</td>
                                    <td><a class="button blue" type="submit" name="positionEditButton" id="position_${position.id}">Edit</a></td>
                                    <input type="hidden" id="${position.id}_positionId" value="${position.id}"/>
                                    <input type="hidden" id="${position.id}_employer" value="${position.position_employer}"/>
                                    <input type="hidden" id="${position.id}_remit" value="${position.position_remit}"/>
                                    <input type="hidden" id="${position.id}_language" value="${position.position_language}"/>
                                    <input type="hidden" id="${position.id}_positionTitle" value="${position.position_title}"/>
                                    <input type="hidden" id="${position.id}_positionStartDate" value="${position.position_startDate?string('yyyy/MM/dd')}"/>
                                    <input type="hidden" id="${position.id}_positionEndDate" value="${position.position_endDate?string('yyyy/MM/dd')}"/>
                               </tr>
                            </#list>
                            </table>
                            
                            <input type="hidden" id="positionId" name="positionId"/>
                            <table cellspacing=10>
                                <tr align=left></tr>
                                <tr><td>Employer</td>
                                <td>
                                <input type="text" id="position_employer" name="position_employer" value="${model.employmentPosition.position_employer!}"/>
                                <#if model.hasError('position_employer')>                           
                                    <span style="color:red;"><@spring.message  model.result.getFieldError('position_employer').code /></span>                           
                                </#if>
                                </td>
                                </tr>
                                
                                <tr><td>Title</td>
                                <td>
                                <input type="text" id="position_title" name="position_title" value="${model.employmentPosition.position_title!}"/>
                                <#if model.hasError('position_title')>                           
                                    <span style="color:red;"><@spring.message  model.result.getFieldError('position_title').code /></span>                           
                                </#if>
                                </td>
                                </tr>
                                
                                <tr><td>Remit</td>
                                <td>
                                <input type="text" id="position_remit" name="position_remit" value="${model.employmentPosition.position_remit!}"/>
                                <#if model.hasError('position_remit')>                           
                                    <span style="color:red;"><@spring.message  model.result.getFieldError('position_remit').code /></span>                           
                                </#if>
                                </td>
                                </tr>
                                
                                <tr><td>Start Date</td>
                                <td>
                                <input type="text" id="position_startDate" name="position_startDate" value="${(model.employmentPosition.position_startDate?string('yyyy/MM/dd'))!}"/>
                                <#if model.hasError('position_startDate')>                           
                                    <span style="color:red;"><@spring.message  model.result.getFieldError('position_startDate').code /></span>                           
                                </#if>
                                </td>
                                </tr>
                                
                                <tr><td>End Date</td>
                                <td>
                                <input type="text" id="position_endDate" name="position_endDate" value="${(model.employmentPosition.position_endDate?string('yyyy/MM/dd'))!}"/>
                                <#if model.hasError('position_endDate')>                           
                                    <span style="color:red;"><@spring.message  model.result.getFieldError('position_endDate').code /></span>                           
                                </#if>
                                </td>
                                </tr>

                                <tr><td>Language</td>
                                <td>
                                <input type="text" id="position_language" name="position_language" value="${model.employmentPosition.position_language!}"/>
                                <#if model.hasError('position_language')>                           
                                    <span style="color:red;"><@spring.message  model.result.getFieldError('position_language').code /></span>                           
                                </#if>
                                </td>
                                </tr>
                                
                                </table>
                            
                            </div>
                            <br/>
                            <div class="buttons">
                                    <a class="button blue" href="#">Close</a>
                                    <#if !model.applicationForm.isSubmitted()>
                                        <a class="button blue" type="submit" id="positionSaveButton">Save</a>
                                    </#if>    
                            </div>
                        </div>
<script type="text/javascript" src="<@spring.url '/design/default/js/application/employmentPosition.js'/>"></script>