<#import "/spring.ftl" as spring />
 <h2 class="open">
                          <span class="left"></span><span class="right"></span><span class="status"></span>
                          References
                        </h2>
                        <div>
                            <br/>
                            <div>
                            
                             <table cellspacing=10>
                                 <tr align=left><th>First Name</th><th>Surname</th><th>Relationship</th><th>Email</th></tr>
                                <#list model.applicationForm.referees as referee>
                                <tr>
                                    <td>${referee.firstname}</td>
                                    <td>${referee.lastname}</td>
                                    <td>${referee.relationship}</td>
                                    <td>${referee.email}</td>
                                    <td><a class="button blue" type="submit" name="refereeEditButton" id="referee_${referee.id}">Edit</a></td>
                                    <input type="hidden" id="${referee.id}_refereeId" value="${referee.id}"/>
                                    <input type="hidden" id="${referee.id}_firstname" value="${referee.firstname}"/>
                                    <input type="hidden" id="${referee.id}_lastname" value="${referee.lastname}"/>
                                    <input type="hidden" id="${referee.id}_relationship" value="${referee.relationship}"/>
                                    <input type="hidden" id="${referee.id}_jobEmployer" value="${referee.jobEmployer}"/>
                                    <input type="hidden" id="${referee.id}_jobTitle" value="${referee.jobTitle}"/>
                                    <input type="hidden" id="${referee.id}_addressLocation" value="${referee.addressLocation}"/>
                                    <input type="hidden" id="${referee.id}_addressPostcode" value="${referee.addressPostcode}"/>
                                    <input type="hidden" id="${referee.id}_addressCountry" value="${referee.addressCountry}"/>
                                    <input type="hidden" id="${referee.id}_email" value="${referee.email}"/>
                                    <input type="hidden" id="${referee.id}_messengers" value="${referee.messengers}"/>
                                    <input type="hidden" id="${referee.id}_telephones" value="${referee.telephones}"/>
                               </tr>
                            </#list>
                            </table>
                            
                            <input type="hidden" id="refereeId" name="refereeId"/>
                            <table cellspacing=10>
                                <tr align=left></tr>
                                <tr><td>First Name</td>
                                <td>
                                <input type="text" id="ref_firstname" name="ref_firstname" value="${model.referee.firstname!}"/>
                                </td>
                                </tr>
                                
                                <tr><td>Last Name</td>
                                <td>
                                <input type="text" id="ref_lastname" name="ref_lastname" value="${model.referee.lastname!}"/>
                                </td>
                                </tr>
                                
                                <tr><td>Relationship</td>
                                <td>
                                <input type="text" id="ref_relationship" name="ref_relationship" value="${model.referee.relationship!}"/>
                                </td>
                                </tr>
                                
                                <tr><td>Employer</td>
                                <td>
                                <input type="text" id="ref_employer" name="ref_employer" value="${model.referee.jobEmployer!}"/>
                                </td>
                                </tr>
                                
                                <tr><td>Position</td>
                                <td>
                                <input type="text" id="ref_position" name="ref_position" value="${model.referee.jobTitle!}"/>
                                </td>
                                </tr>

                                <tr><td>Address Location</td>
                                <td>
                                <input type="text" id="ref_address_location" name="ref_address_location" value="${model.referee.addressLocation!}"/>
                                </td>
                                </tr>
                                
                                <tr><td>Address Post Code</td>
                                <td>
                                <input type="text" id="ref_address_postcode" name="ref_address_postcode" value="${model.referee.addressPostcode!}"/>
                                </td>
                                </tr>
                                
                                <tr><td>Address Country</td>
                                <td>
                                <input type="text" id="ref_address_country" name="ref_address_country" value="${model.referee.addressCountry!}"/>
                                </td>
                                </tr>
                                
                                <tr><td>Email</td>
                                <td>
                                <input type="text" id="ref_email" name="ref_email" value="${model.referee.email!}"/>
                                </td>
                                </tr>
                                <table id="telephones">
                                <#list model.referee.telephones as telephone>
                                <tr><td>Telephone Type</td>
                                <td>
                                <input type="text" id="ref_telephone_type" name="ref_telephone_type" value="${telephone.telephoneType!}"/>
                                </td>
                                </tr>                                
                                <tr><td>Telephone Type</td>
                                <td>
                                <input type="text" id="ref_telephone_number" name="ref_telephone_number" value="${telephone.telephoneNumber!}"/>
                                </td>
                                </tr>                                
                                </#list>
                                </table>
                                 <a class="button blue" id="addTelephoneButton" href="#">Add Phone</a>
                                
                                <table id="messengers">
                                <#list model.referee.messengers as messenger>
                                <tr><td>Messenger Type</td>
                                <td>
                                <input type="text" id="ref_messenger_type" name="ref_messenger_type" value="${messenger.messengerType!}"/>
                                </td>
                                </tr>    
                                                            
                                <tr><td>Messenger Address</td>
                                <td>
                                <input type="text" id="ref_messenger_number" name="ref_messenger_address" value="${messenger.messengerAddress!}"/>
                                </td>
                                </tr>                                
                                </#list>
                                </table>
                                 <a class="button blue" id="addMessengerButton" href="#">Add Messenger</a>
                                
                                </table>
                            
                            </div>
                            <br/>
                            <div class="buttons">
                                    <a class="button blue" href="#">Close</a>
                                    <#if !model.applicationForm.isSubmitted()>
                                        <a class="button blue" type="submit" id="refereeSaveButton">Save</a>
                                    </#if>    
                            </div>
                        </div>
<script type="text/javascript" src="<@spring.url '/design/default/js/application/referee.js'/>"></script>    