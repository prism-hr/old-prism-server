<script type="text/javascript" src="<@spring.url '/design/default/js/prospectus/program_configuration.js' />"></script>
<section class="form-rows">
      <h2>Manage Programmes</h2>
      <div id="programAdvertDiv">
          <form>
              <input type="hidden" id="programAdvertForceCreatingNewInstitution" value="false" />
              <input type="hidden" id="programAdvertProgramLocked" value="false">
              <input type="hidden" id="programAdvertIsCustom" value="false" />
              
              <div class="row-group">
              	<div class="row" id="programAdvertSelectProgramDiv">
                  <label for="programAdvertProgramSelect" class="plain-label">Select Programme<em>*</em></label> <span class="hint" data-desc="<@spring.message 'prospectus.programme'/>"></span>
                  <div class="field forLabel">
                      <select id="programAdvertProgramSelect" class="max selectpicker" data-live-search="true" data-size="6" >
                          <option value="">Select...</option>
                          <#list programmes as programme>
                            <option value="${programme.code}"<#if program?? && programme.code == program.code>selected</#if> > ${programme.title?html}</option>
                          </#list>
                      </select>
                      <a id="newProgammeButton" class="btn btn-success">New Programme</a>
                      <a id="cancelNewProgramBtn" class="btn btn-danger" style="display:none">Cancel</a>
                  </div>
                </div>
                <div id="programAdvertNewProgramDiv" style="display:none">
                  <label for="programAdvertProgramSelect" class="plain-label">Create new program</label>
                </div>
              </div>
              <div class="row-group" id="institutionGroup">
                <h3>Program information</h3>
                <div class="infoBar alert alert-info" id="infoBarInstitution">
                  <i class="icon-info-sign"></i>Specify your program information here.
                </div>

              	<div class="row" id="programAdvertProgramNameDiv">
                  <label for="programAdvertProgramName" class="plain-label">Programme name<em>*</em></label> <span class="hint" data-desc="<@spring.message 'prospectus.programme'/>"></span>
                  <div class="field forLabel">
                    <input id="programAdvertProgramName" name="title" class="input-xxlarge" type="text">
                  </div>
                </div>
                                
                <div class="row" id="programAdvertProgramTypeDiv">
                  <label id="programAdvertProgramTypeLabel" class="plain-label" for="programAdvertProgramType">Program Type<em>*</em></label>
                  <span class="hint" data-desc="<@spring.message 'opportunityRequest.programType'/>"></span>
                  <div class="field">
                    <select id="programAdvertProgramType">
                      <option value="">Select...</option>
                      <#list programTypes as programType>
                        <option value="${programType.id.name()}">${(programType.id.displayValue?html)!}</option>
                      </#list>
                    </select>
                  </div>
                </div>
                
                <div class="row" id="programAdvertAtasRequiredDiv">
                  <label id="programAdvertAtasRequiredLabel" class="plain-label" for="atasRequired">Does the programme require ATAS?<em>*</em></label>
                  <span class="hint" data-desc="<@spring.message 'opportunityRequest.atasRequired'/>"></span>
                  <div class="field">
                    <label><input type="radio" name="programAdvertAtasRequired" value="true" id="programAdvertAtasRequired_true" /> Yes</label>
                    <label><input type="radio" name="programAdvertAtasRequired" value="false" id="programAdvertAtasRequired_false" /> No</label>
                  </div>
                </div>

                <div class="row" id="programAdvertInstitutionCountryDiv">
                  <label class="plain-label" for="programAdvertInstitutionCountry">Institution Country<em>*</em></label>
                  <span class="hint" data-desc="<@spring.message 'opportunityRequest.institutionCountry'/>"></span>
                  <div class="field">
                    <select class="full selectpicker" data-live-search="true" data-size="6" id="programAdvertInstitutionCountry">
                      <option value="">Select...</option>
                      <#list countries as country>
                        <option value="${encrypter.encrypt(country.id)}">
                          ${country.name?html}
                        </option>
                      </#list>
                    </select>
                  </div>
                </div>
                
                <div class="row" id="programAdvertInstitutionDiv">
                  <label class="plain-label" for="programAdvertInstitution">Institution Name<em>*</em></label>
                  <span class="hint" data-desc="<@spring.message 'opportunityRequest.institutionName'/>"></span>
                  <div class="field">
                    <select class="full selectpicker" data-live-search="true" data-size="6"  id="programAdvertInstitution">
                      <option value="">Select...</option>
                      <optgroup id="userInstitutions" label="Your Institutions" />
                      <optgroup id="otherInstitutions" label="Other Institutions"></optgroup>
                      <option value="OTHER">Other</option>
                    </select>
                  </div>
                </div>
                
                
                <div class="row" id="programAdvertInstitutionOtherNameDiv">
                  <label class="plain-label" for="programAdvertInstitutionOtherName">Please Specify<em>*</em></label>
                  <span class="hint" data-desc="<@spring.message 'opportunityRequest.otherInstitution'/>"></span>
                  <div class="field">
                    <input id="programAdvertInstitutionOtherName" class="full" type="text" autocomplete="off" />
                  </div>
                </div>

              </div>
              <div class="row-group" id="advertGroup">
                <h3>Advert</h3>
                <div class="infoBar alert alert-info" id="infoBarProgram">
                  <i class="icon-info-sign"></i> Manage the advert for your programme here.
                </div>
                <div class="row" id="programAdvertDescriptionDiv">
                  <label for="programAdvertDescriptionText" class="plain-label">Description<em>*</em></label> <span class="hint" data-desc="<@spring.message 'prospectus.description'/>"></span>
                  <div class="field">
                    <textarea id="programAdvertDescriptionText" class="max" rows="6" cols="150"></textarea>
                  </div>
                </div>
                <div class="row" id="programAdvertStudyDurationDiv">
                  <label for="programAdvertStudyDurationInput" class="plain-label">Duration of Study<em>*</em></label>
                  <span class="hint" data-desc="<@spring.message 'prospectus.durationOfStudy'/>"></span>
                  <div class="field">
                    <input class="numeric input-small" type="text" size="4" id="programAdvertStudyDurationInput" />
                    <select id="programAdvertStudyDurationUnitSelect" class="input-small">
                      <option value="">Select...</option>
                      <option value="MONTHS">Months</option>
                      <option value="YEARS">Years</option>
                    </select>
                  </div>
                </div>
                <div class="row" id="programAdvertFundingDiv">
                  <label for="programAdvertFundingText" class="plain-label">Funding Information</label> <span class="hint" data-desc="<@spring.message 'prospectus.fundingInformation'/>"></span>
                  <div class="field">
                    <textarea id="programAdvertFundingText" class="max" rows="6" cols="150"></textarea>
                  </div>
                </div>
                <div class="row" id="programAdvertIsActiveDiv">
                  <label class="plain-label">Are you currently accepting applications?<em>*</em></label>
                  <span class="hint" data-desc="<@spring.message 'prospectus.acceptingApplications'/>"></span>
                  <div class="field">
                    <input id="programAdvertIsActiveRadioYes" type="radio" name="switch" value="true">
                      Yes
                    </input>
                    <input id="programAdvertIsActiveRadioNo" type="radio" name="switch" value="false">
                      No
                    </input>
                  </div>
                </div>
                
                <div class="row" id="programAdvertStudyOptionsDiv">
                  <label class="plain-label" for="programAdvertStudyOptionsSelect">Study Options<em>*</em></label>
                  <span class="hint" data-desc="<@spring.message 'opportunityRequest.studyOptions'/>"></span>
                  <div class="field">
                    <select multiple size="3" class="full" id="programAdvertStudyOptionsSelect">
                      <#list studyOptions as studyOption>
                        <option value="${studyOption.id}">
                          ${studyOption.name?html}
                        </option>
                      </#list>
                    </select>
                  </div>
                </div>
                
                <div class="row" id="programAdvertAdvertisingDeadlineYearDiv">
                  <label class="plain-label" for="advertisingDeadlineYear">Advertise deadline<em>*</em></label>
                  <span class="hint" data-desc="<@spring.message 'opportunityRequest.advertisingDeadlineYear'/>"></span>
                  <div class="field">
                    <select class="full" id="programAdvertAdvertisingDeadlineYear">
                      <option value="">Select...</option>
                      <#list advertisingDeadlines as advertisingDeadline>
                        <option value="${advertisingDeadline?c}">
                          30 September ${advertisingDeadline?c}
                        </option>
                      </#list>                
                    </select>
                  </div>
                </div>
                
                <div class="row">
                	<div class="field">
                	  <button class="btn btn-primary" type="button" id="programAdvertSave">Save</button>
                	</div>
                </div>
              </div>
              <div class="row-group">
                  <h3>Closing Dates</h3>
                  <div id="infodates" class="alert alert-info">
                      <i class="icon-info-sign"></i> Manage closing dates for your programme here.
                  </div>
                  <div class="tableContainer table table-condensed ">
                    <table id="programAdvertClosingDates" class="table table-striped table-condensed table-hover table-bordered" style="display: none;">
                    <colgroup>
                    <col />
                      <col style="width: 30px;" />
                    <col style="width: 30px;" />
                    </colgroup>
                    <tbody>
                    
                    </tbody>
                    </table>
                  </div>
              </div>
              <div class="row-group" id="programAdvertClosingDateGroup">
                <input type="hidden" id="programAdvertClosingDateId"/>
                <div class="row" id="programAdvertClosingDateDiv">
                  <label for="closingDate" class="plain-label">Closing Date<em>*</em></label>
                  <span class="hint" data-desc="<@spring.message 'prospectus.closingDate'/>"></span>
                  <div class="field">
                    <input type="text" id="programAdvertClosingDateInput" class="full date"/>
                  </div>
                </div>
                <div class="row" id="programAdvertStudyPlacesDiv">
                  <label for="programAdvertStudyPlacesInput" class="plain-label">Study Places</label> 
                  <span class="hint" data-desc="<@spring.message 'prospectus.studyPlaces'/>"></span>
                  <div class="field">
                      <input class="numeric input-small" type="text" size="4" id="programAdvertStudyPlacesInput" />
                  </div>
                </div>
                <div class="field">
                  <button id="addProgramAdvertClosingDate" role="button" class="btn btn-primary">Add</button>
                </div>
              </div>                                        
              <div class="row-group">
                <h3>Resources</h3>
                <div id="infoResources" class="alert alert-info">
                    <i class="icon-info-sign"></i> Embed these resources to provide applicants with links to apply for your programme.
                </div>
                <div class="row">
                    <label for="programAdvertLinkToApply" class="plain-label">Link to Apply</label> <span class="hint" data-desc="<@spring.message 'prospectus.linkToApply'/>"></span>
                    <div class="field">
                        <input id="programAdvertLinkToApply" name="project" class="input-xxlarge" type="text" role="textbox" aria-haspopup="true" readonly>
                    </div>
                </div>
                <div class="row">
                    <label for="programAdvertButtonToApply" class="plain-label">Button to Apply</label> <span class="hint" data-desc="<@spring.message 'prospectus.buttonToApply'/>"></span>
                    <div class="field">
                        <textarea id="programAdvertButtonToApply" class="input-xxlarge" rows="6" cols="150" readonly></textarea>
                    </div>
                </div>
              </div>
          </form>
      </div>
  </section>