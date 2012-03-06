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
                             
                            </div>
                            <br/>
                        </div>
