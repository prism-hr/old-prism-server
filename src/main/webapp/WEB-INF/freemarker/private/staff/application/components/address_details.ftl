 <h2 class="open">
                          <span class="left"></span><span class="right"></span><span class="status"></span>
                          Address
                        </h2>
                        <div>
                            <br/>
                            <div>
                            <table>
                            <#list model.applicationForm.addresses as address>
                                <tr><td>- ${address.street}, ${address.city}, ${address.postCode}, ${address.country}     ${address.startDate?date}     ${address.endDate?date}</td></tr>
                            </#list>
                            </table>
                            </div>
                            <br/>
                        </div>