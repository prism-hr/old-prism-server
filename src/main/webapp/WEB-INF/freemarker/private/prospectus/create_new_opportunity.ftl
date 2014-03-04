<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/public/opportunities.css' />"/>
<script type="text/javascript" src="<@spring.url '/design/default/js/prospectus/create_new_opportunity.js' />"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/tinymce/tinymce.min.js' />"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/tinymce/jquery.tinymce.min.js' />"></script>

<div style="overflow:hidden; position:relative">
  <div id="pholder">
    <form id="applyForm" action="/pgadmissions/apply/new" method="POST" style="display:none;" <#if shouldOpenNewTab??>target="_blank"</#if>>
      <input type="hidden" id="program" name="program" value=""/>
      <input type="hidden" id="advert" name="advert" value=""/>
      <input type="hidden" id="project" name="project" value=""/>
    </form>
	
    <header>
      <a href="/pgadmissions/login" class="btn btn-danger newOpportunityCancel">Cancel</a>
      <h1>Opportunities to Advertise?</h1>
    </header>
    	<@spring.bind "opportunityRequest.*"/>
        <#if spring.status.errorMessages?size &gt; 0>
     		<div class="alert alert-error" >
            <i class="icon-warning-sign"></i>
	    <#else>
        <div class="alert alert-info">
  			<i class="icon-info-sign"></i>
      	</#if>
		Complete the form below to advertise and manage recruitment to your own programme using our software. 
			<strong><a href="/pgadmissions/prospectus">Login</a></strong> to proceed if you are already a PRiSM user.
	     </div>
    <section id="plist">
      <form action="<@spring.url "/createOpportunity" />" method="POST">
        <fieldset>
        
        
          <div class="content-box">
          <div class="content-box-inner">
          <section class="form-rows">
          <div class="row-group">
          
            <#include "/private/prospectus/opportunity_details_part.ftl"/>
            <hr>
            <#assign pendingUser = opportunityRequest.author>
            
            <div class="row">
              <label class="plain-label" for="firstName">First Name <em>*</em></label>
              <span class="hint" data-desc="<@spring.message 'opportunityRequest.author.firstName'/>"></span>
              <div class="field">
                <input id="firstName" type="text" name="author.firstName" value='${(opportunityRequest.author.firstName?html)!""}' />
                <@spring.bind "opportunityRequest.author.firstName" />
                <#list spring.status.errorMessages as error>
                  <div class="alert alert-error">
                   <i class="icon-warning-sign"></i> ${error}
                 </div>
                </#list>
              </div>
            </div>
            <div class="row">
              <label class="plain-label" for="lastName">Last Name <em>*</em></label>
              <span class="hint" data-desc="<@spring.message 'opportunityRequest.author.lastName'/>"></span>
              <div class="field">
                <input id="lastName" type="text" name="author.lastName" value='${(opportunityRequest.author.lastName?html)!""}' />
                <@spring.bind "opportunityRequest.author.lastName" />
                <#list spring.status.errorMessages as error>
                    <div class="alert alert-error"><i class="icon-warning-sign"></i> ${error}</div>
                </#list>
              </div>
            </div>
            <div class="row">
              <label class="plain-label" for="email">Email <em>*</em></label>
              <span class="hint" data-desc="<@spring.message 'opportunityRequest.author.email'/>"></span>
              <div class="field">
                <input id="email" type="email" placeholder="Email Address" name="author.email" value='${(opportunityRequest.author.email?html)!""}' autocomplete="off" />
                <@spring.bind "opportunityRequest.author.email" />
                <#list spring.status.errorMessages as error>
                  <div class="alert alert-error"><i class="icon-warning-sign"></i> ${error}</div>
                </#list>
              </div>
            </div>
            <div class="row">
              <label class="plain-label" for="password">Password <em>*</em></label>
              <span class="hint" data-desc="<@spring.message 'myaccount.newPw'/>"></span>
              <div class="field">
                <input id="password" type="password" name="author.password" placeholder="Password" autocomplete="off"/>
                <@spring.bind "opportunityRequest.author.password" />
                <#list spring.status.errorMessages as error>
                  <div class="alert alert-error"><i class="icon-warning-sign"></i> ${error}</div>
                </#list>
              </div>
            </div>
            <div class="row">
              <label class="plain-label" for="password">Confirm <em>*</em></label>
              <span class="hint" data-desc="<@spring.message 'myaccount.confirmPw'/>"></span>
              <div class="field">
                <input id="confirmPassword" type="password" name="author.confirmPassword" placeholder="Confirm password"/>
                <@spring.bind "opportunityRequest.author.confirmPassword" />
                <#list spring.status.errorMessages as error>
                  <div class="alert alert-error"> <i class="icon-warning-sign"></i> ${error}</div>
                </#list>
              </div>
            </div>
          
            <div class="row">
              <div class="field">
                <button type="submit" class="btn btn-primary">Submit</button>
              </div>
            </div>
          
          </div>
          </section>
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