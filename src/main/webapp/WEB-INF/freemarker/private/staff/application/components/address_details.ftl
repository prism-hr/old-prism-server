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
                                    <td>${address.location}, ${address.postCode}, ${address.country}</td>
                                    <td>${address.startDate?string('dd-MMM-yyyy')}</td>
                                    <td>${address.endDate?string('dd-MMM-yyyy')}</td>
                               </tr>
                            </#list>
                            </table>
                            </div>
                            <br/>
                        </div>