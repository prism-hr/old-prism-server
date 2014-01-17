<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/public/opportunities.css' />"/>
<script type="text/javascript" src="<@spring.url '/design/default/js/prospectus/create_new_opportunity.js' />"></script>
<div style="overflow:hidden; position:relative">
  <div id="pholder">
    <form id="applyForm" action="/pgadmissions/apply/new" method="POST" style="display:none;" <#if shouldOpenNewTab??>target="_blank"</#if>>
      <input type="hidden" id="program" name="program" value=""/>
      <input type="hidden" id="advert" name="advert" value=""/>
      <input type="hidden" id="project" name="project" value=""/>
    </form>
	
    <#if Request['prospectus.selectedAdvert']?has_content>
      <input type="hidden" id="prospectusSelectedAdvert" name="prospectusSelectedAdvert" value="${Request['prospectus.selectedAdvert']}"/>
    </#if>
    <header>
      <h1>Create New Oppotunity
        <a href="/pgadmissions/login" class="btn btn-primary">Go Back</a>
      </h1>
      
    </header>

    <section id="plist">
      <form action="<@spring.url "/createOpportunity" />" method="POST">      
        <fieldset>
        
          <div class="control-group">
            <label class="control-label" for="institutionCountry">Institution Country<em>*</em></label>
            <span class="hint" data-desc="<@spring.message 'education.qualifications.institutionCountry'/>"></span>
            <div class="controls">
              <select class="full selectpicker" data-live-search="true" data-size="6" id="institutionCountry" name="institutionCountry">
                <option value="">Select...</option>
                <#list countries as country>
                  <option value="${encrypter.encrypt(country.id)}"
                    <#if opportunityRequest.institutionCountry?? && opportunityRequest.institutionCountry.id == country.id> selected="selected"</#if>
                    >${country.name?html}
                  </option>
                </#list>
              </select>
              <@spring.bind "opportunityRequest.institutionCountry" />
              <#list spring.status.errorMessages as error>
                <div class="alert alert-error"> <i class="icon-warning-sign"></i>
                  ${error}
                </div>
              </#list>
            </div>
          </div>
          
          <div class="control-group">
            <label id="lbl-providerName" class="control-label" for="institution">Institution Name<em>*</em></label>
            <span class="hint grey" data-desc="<@spring.message 'education.qualifications.institutionName'/>"></span>
            <div class="controls">
              <select class="full selectpicker" disabled="disabled" data-live-search="true" data-size="6"  id="institution" name="institutionCode">
                <option value="">Select...</option>
                <#if opportunityRequest.institutionCountry??>
                  <#list institutions as inst>
                    <option value="${inst.code}" <#if opportunityRequest.institutionCode?? && opportunityRequest.institutionCode == inst.code> selected="selected"</#if>>
                      ${inst.name?html}
                    </option>
                  </#list>
                  <option value="OTHER" <#if opportunityRequest.institutionCode?? && opportunityRequest.institutionCode == "OTHER">selected="selected"</#if>>Other
                  </option>
                </#if>
              </select>
              <@spring.bind "opportunityRequest.institutionCode" />
              <#list spring.status.errorMessages as error>
                <div class="alert alert-error"> <i class="icon-warning-sign"></i>
                  ${error}
                </div>
              </#list>
            </div>
          </div>
          
          <div class="control-group">
            <label id="lbl-otherInstitutionProviderName" class="control-label" for="otherInstitutionProviderName">Please Specify<em>*</em></label>
            <span class="hint grey" data-desc="<@spring.message 'education.qualifications.subject'/>"></span>
            <div class="controls">
              <input readonly disabled="disabled" id="otherInstitution" name="otherInstitution" class="full" type="text" value="${(opportunityRequest.otherInstitution?html)!}" />
              <@spring.bind "opportunityRequest.otherInstitution" />
              <#list spring.status.errorMessages as error>
                <div class="alert alert-error"> <i class="icon-warning-sign"></i>
                  ${error}
                </div>
              </#list>
            </div>
          </div>
          
          <div class="control-group">
            <label id="programTitleLabel" class="control-label" for="programTitle">Program Title<em>*</em></label>
            <span class="hint grey" data-desc="<@spring.message 'opportunityRequest.programTitle'/>"></span>
            <div class="controls">
              <input id="programTitle" name="programTitle" class="full" type="text" value="${(opportunityRequest.programTitle?html)!}" />
              <@spring.bind "opportunityRequest.programTitle" />
              <#list spring.status.errorMessages as error>
                <div class="alert alert-error"> <i class="icon-warning-sign"></i>
                  ${error}
                </div>
              </#list>
            </div>
          </div>
          
          <div class="control-group">
            <label id="programDescriptionLabel" class="control-label" for="programDescription">Program Description<em>*</em></label>
            <span class="hint grey" data-desc="<@spring.message 'opportunityRequest.programDescription'/>"></span>
            <div class="controls">
              <textarea id="programDescription" name="programDescription" class="max" cols="70" rows="6">${(opportunityRequest.programDescription?html)!}</textarea>
              <@spring.bind "opportunityRequest.programDescription" />
              <#list spring.status.errorMessages as error>
                <div class="alert alert-error"> <i class="icon-warning-sign"></i>
                  ${error}
                </div>
              </#list>
            </div>
          </div>
          
          <#assign pendingUser = opportunityRequest.author>
          
          <div class="control-group">
            <label class="control-label" for="firstName">First Name <em>*</em></label>
            <span class="hint" data-desc="Please enter your first name."></span>
            <div class="controls">
              <input id="firstName" type="text" name="author.firstName" value='${(opportunityRequest.author.firstName?html)!""}' />
              <@spring.bind "opportunityRequest.author.firstName" />
              <#list spring.status.errorMessages as error>
                <div class="alert alert-error">
                 <i class="icon-warning-sign"></i> ${error}
               </div>
              </#list>
            </div>
          </div>
          <div class="control-group">
            <label class="control-label" for="lastName">Last Name <em>*</em></label>
            <span class="hint" data-desc="Please enter your last name."></span>
            <div class="controls">
              <input id="lastName" type="text" name="author.lastName" value='${(opportunityRequest.author.lastName?html)!""}' />
              <@spring.bind "opportunityRequest.author.lastName" />
              <#list spring.status.errorMessages as error>
                  <div class="alert alert-error"><i class="icon-warning-sign"></i> ${error}</div>
              </#list>
            </div>
          </div>
          <div class="control-group">
            <label class="control-label" for="email">Email <em>*</em></label>
            <span class="hint" data-desc="Please enter your email address."></span>
            <div class="controls">
              <input id="email" type="email" placeholder="Email Address" name="author.email" value='${(opportunityRequest.author.email?html)!""}' />
              <@spring.bind "opportunityRequest.author.email" />
              <#list spring.status.errorMessages as error>
                <div class="alert alert-error"><i class="icon-warning-sign"></i> ${error}</div>
              </#list>
            </div>
          </div>
          <div class="control-group">
            <label class="control-label" for="password">Password <em>*</em></label>
            <span class="hint" data-desc="<@spring.message 'myaccount.newPw'/>"></span>
            <div class="controls">
              <input id="password" type="password" name="author.password" placeholder="Password"/>
              <@spring.bind "opportunityRequest.author.password" />
              <#list spring.status.errorMessages as error>
                <div class="alert alert-error"><i class="icon-warning-sign"></i> ${error}</div>
              </#list>
            </div>
          </div>
          <div class="control-group">
            <label class="control-label" for="password">Confirm <em>*</em></label>
            <span class="hint" data-desc="<@spring.message 'myaccount.confirmPw'/>"></span>
            <div class="controls">
              <input id="confirmPassword" type="password" name="author.confirmPassword" placeholder="Confirm password"/>
              <@spring.bind "opportunityRequest.author.confirmPassword" />
              <#list spring.status.errorMessages as error>
                <div class="alert alert-error"> <i class="icon-warning-sign"></i> ${error}</div>
              </#list>
            </div>
          </div>
          
          <div class="control-group">
            <div class="controls">
              <button type="submit" class="btn btn-primary">Submit</button>
            </div>
          </div>
          
        </fieldset>
      
      </form>
    </section>
    <footer class="clearfix">
    	<div class="left"><a href="http://www.engineering.ucl.ac.uk" target="_blank"><img src="<@spring.url '/design/default/images/ucl-engineering.jpg'/>" alt="" /></a></div>
    	<div class="right"><a href="http://prism.ucl.ac.uk" target="_blank"><img src="<@spring.url '/design/default/images/prism_small.jpg'/>" alt="" /></a></div>
    </footer>
  </div>
</div>