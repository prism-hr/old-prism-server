 <h2 class="open">
                          <span class="left"></span><span class="right"></span><span class="status"></span>
                          Employment 
                        </h2>
                        <div>
                            <br/>
                            <div>
                             
                            <table cellspacing=10>
                                 <tr align=left><th>Employer</th><th>Title</th><th>Remit</th>
                                 <th>Start Date</th><th>End Date</th><th>Language</th></tr>
                                <#list model.applicationForm.employmentPositions as position>
                                <tr>
                                    <td>${position.position_employer}</td>
                                    <td>${position.position_title}</td>
                                    <td>${position.position_remit}</td>
                                    <td>${position.position_startDate?string('yyyy/MM/dd')}</td>
                                    <td>${position.position_endDate?string('yyyy/MM/dd')}</td>
                                    <td>${position.position_language}</td>
                               </tr>
                            </#list>
                            </table>
                             
                            </div>
                            <br/>
                        </div>
