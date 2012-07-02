<#import "/spring.ftl" as spring />
<h2 class="no-arrow">
	Account Details
</h2>
<div>
	<form autocomplete="off">
	
  		<#if RequestParameters.messageCode??>
      	<div class="section-info-bar"><@spring.message '${RequestParameters.messageCode}'/></div>    
      <#else>        
      	<div class="section-info-bar">Edit your account details.</div>
      </#if>
	
		<div class="row-group">
			<div class="row"> 
				<span id="email-lbl" class="plain-label">First Name</span>
				<span class="hint" data-desc="<@spring.message 'myaccount.firstName'/>"></span>
				<div class="field">	
					<input class="full" type="text" id="firstName" value="${user.firstName}" />
				</div>
				<@spring.bind "updatedUser.firstName" />
				<#list spring.status.errorMessages as error>
					<div class="field">
						<span class="invalid">${error}</span>
					</div>
				</#list>
			</div>
			
			<div class="row"> 
				<span id="email-lbl" class="plain-label">Last Name</span>
				<span class="hint" data-desc="<@spring.message 'myaccount.lastName'/>"></span>
				<div class="field">	
					<input class="full" type="text" id="lastName" value="${user.lastName}" />
				</div>
				<@spring.bind "updatedUser.lastName" />
				<#list spring.status.errorMessages as error>
					<div class="field">
						<span class="invalid">${error}</span>
					</div>
				</#list>
			</div>
			
			<div class="row"> 
				<span id="email-lbl" class="plain-label">Email</span>
				<span class="hint" data-desc="<@spring.message 'myaccount.email'/>"></span>
				<div class="field">	
					<input class="full" type="text" id="email" value="${user.email}" />
				</div>
				<@spring.bind "updatedUser.email" />
				<#list spring.status.errorMessages as error>
					<div class="field">
						<span class="invalid">${error}</span>
					</div>
				</#list>
			</div>
		</div>

		<div class="row-group">
			<h3>Change Password</h3>
			<div class="row"> 
				<span class="plain-label">Current Password</span>
				<span class="hint" data-desc="<@spring.message 'myaccount.currentPw'/>"></span>
				<div class="field">	
					<input class="full" id="currentPassword" type="password" />
				</div>
				<@spring.bind "updatedUser.password" />
				<#list spring.status.errorMessages as error>
					<div class="field">
						<span class="invalid">${error}</span>
					</div>
				</#list>
			</div>
			
			<div class="row"> 
				<span class="plain-label">New Password</span>
				<span class="hint" data-desc="<@spring.message 'myaccount.newPw'/>"></span>
				<div class="field">	
					<input class="full" id="newPassword" type="password"  />
				</div>
				<@spring.bind "updatedUser.newPassword" />
				<#list spring.status.errorMessages as error>
					<div class="field">
						<span class="invalid">${error}</span>
					</div>
				</#list>
			</div>
			
			<div class="row"> 
				<span class="plain-label">Re-enter new Password</span>
				<span class="hint" data-desc="<@spring.message 'myaccount.confirmPw'/>"></span>
				<div class="field">	
					<input class="full" id="confirmNewPass" type="password" />
				</div>
				<@spring.bind "updatedUser.confirmPassword" />
				<#list spring.status.errorMessages as error>
					<div class="field">
						<span class="invalid">${error}</span>
					</div>
				</#list>
			</div>
			
			
		</div><!-- .row-group -->
	
		<div class="buttons">						        		
<#--			<button class="clear" type="button" id="cancelMyACnt">Clear</button> -->
			<button class="blue" id="saveChanges" type="button">Submit</button>						        
		</div>
		
	</form>
</div>