 <h2 class="open">
                          <span class="left"></span><span class="right"></span><span class="status"></span>
                          Employment 
                        </h2>
                        <div>
                            <br/>
                            <div>
                             
                            <table cellspacing=10>
                                 <tr align=left><th>Type</th><th>Value</th><th>Award Date</th></tr>
                                <#list model.applicationForm.employmentPositions as position>
                                <tr>
                                    <td>${position_employer}</td>
                                    <td>${position_title}</td>
                                    <td>${position_remit}</td>
                                    <td>${position_startDate?string('yyyy/MM/dd')}</td>
                                    <td>${position_endDate?string('yyyy/MM/dd')}</td>
                                    <td>${position_language}</td>
                               </tr>
                            </#list>
                            </table>
                             
                            </div>
                            <br/>
                        </div>
