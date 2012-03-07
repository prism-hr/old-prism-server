 <h2 class="open">
                          <span class="left"></span><span class="right"></span><span class="status"></span>
                          Address
                        </h2>
                        <div>
                            <br/>
                            <div>
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
                            </div>
                            <br/>
                        </div>