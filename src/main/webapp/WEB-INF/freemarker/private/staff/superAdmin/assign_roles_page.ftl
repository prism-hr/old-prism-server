<#if usersInRoles?has_content>
	<#assign hasUsers = true>
<#else>
	<#assign hasUsers = false>
</#if>

<!DOCTYPE HTML>

<#import "/spring.ftl" as spring />
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta name="prism.version" content="<@spring.message 'prism.version'/>" >
<title>UCL Postgraduate Admissions</title>

<!-- Always force latest IE rendering engine (even in intranet) & Chrome Frame -->
<meta http-equiv="X-UA-Compatible" content="IE=9,chrome=1" />
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/global_private.css' />"/>
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/application.css' />"/>
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/staff/superadmin.css' />"/>
<link rel="shortcut icon" type="text/css" href="<@spring.url '/design/default/images/favicon.ico' />"/>
<script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/bootstrap.min.css' />"/>
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/font-awesome.min.css' />"/>
<script type="text/javascript" src="<@spring.url '/design/default/js/bootstrap.min.js' />"></script>

<!--[if lt IE 9]>
		<script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
		<![endif]-->

</head>

<body>
<div id="wrapper"> <#include "/private/common/global_header.ftl"/> 
  
  <!-- Middle. -->
  <div id="middle"> <#include "/private/common/parts/nav_with_user_info_toggle.ftl"/>
    <@header activeTab="users"/>
    <!-- Main content area. -->
    <article id="content" role="main">
      <div class="content-box">
        <div class="content-box-inner">
          <form id ="programmeForm" name="programmeForm" action="/pgadmissions/manageUsers/updateUserRoles" method="POST">
            <h1>Add Existing programme users and assign roles</h1>
            <p>Please add users to the programme.<br>
              You can select one or more roles for the user.</p>
            <section class="form-rows">
              <div>
                <div class="row-group">
                  <div class="row programme">
                    <label>Select programme</label>
                    <select name="selectedProgram" id="programId">
                      <option value="">Please select a program</option>
                      <option value="-1">All programs</option>
                      <#list programs as program>" <option value='${program.code}'<#if selectedProgram?? && selectedProgram.id == program.id >selected = "selected"</#if>>
                      ${program.title}
                      </option>
                      </#list>
                    </select>
                    <@spring.bind "updateUserRolesDTO.selectedProgram" />
                    <#list spring.status.errorMessages as error>
                    <div class="alert alert-error"> <i class="icon-warning-sign"></i>
                      ${error}
                    </div>
                    </#list> </div>
                </div>
                
                <!-- // EXISTING USERS -->
                <div class="row-group"> 
                  
                  <!-- Left side -->
                  <div class="left-column">
                    <div class="row"> 
                    <label for="selectedUser">Please choose a user</label>
                      <select id="selectedUser" name="selectedUser">
                        <option value="">Please choose a user</option>
                        <#list availableUsers as user> <option value="${encrypter.encrypt(user.id)}"
																<#if selectedUser?? && selectedUser.id == user.id >
																 selected = "selected" </#if> <#if !user.enabled>class="pending"</#if>>
                        ${user.firstName?html}
                        ${user.lastName?html}
                         (
                        ${user.email?html}
                        )
                        </option>
                        </#list>
                      </select>
                    </div>
                    <div class="row"> or <a href="/pgadmissions/manageUsers/createNewUser<#if selectedProgram??>?programId=${selectedProgram.code}</#if>" >add a new user</a> 
                    <@spring.bind "updateUserRolesDTO.selectedUser" />
                    <#list spring.status.errorMessages as error>
                   <div class="alert alert-error"> <i class="icon-warning-sign"></i> ${error} </div>
                    </#list></div>
                     </div>
                  
                  <!-- Right side -->
                  <div class="right-column">
                    <div class="row"> 
                    <label for="roles">Role(s) in application process</label>
                      <select multiple size="6" id="roles" name="selectedAuthorities">
                        <#list authorities as authority> <option value="${authority}" <#if selectedUser?? && selectedUser.isInRoleInProgram(authority, selectedProgram)>selected="selected" </#if>>
                        ${authority}
                        </option>
                        </#list>
                      </select>
                      <@spring.bind "updateUserRolesDTO.selectedAuthorities" />
                      <#list spring.status.errorMessages as error>
                      <div class="alert alert-error"> <i class="icon-warning-sign"></i> ${error} </div>
                      </#list> </div>
                  </div>
                </div>
                <div class="buttons">
                  <button class="btn" type="submit" value="adduser" >Add / update user</button>
                </div>
              </div>
            </section>
          </form>
          <hr>
          <table class="data table table-striped table-condensed table-bordered table-hover" border="0">
            <colgroup>
            <col style="width: 220px;" />
            <col style="width: auto;" />
            <col style="width: 200px;" />
            <col style="width: 100px;" />
            </colgroup>
            <thead>
              <tr>
                <th scope="col">Email address</th>
                <th scope="col">Name</th>
                <th scope="col">Role(s)</th>
                <th scope="col">Action</th>
              </tr>
            </thead>
            <tbody>
            <#list usersInRoles as userInRole>
              <tr 
            <#if !userInRole.enabled>class="pending"</#if>>
            
              <td scope="col">${userInRole.email?html}</td>
              <td scope="col">${userInRole.firstName?html}
                ${userInRole.lastName?html}</td>
              <td scope="col">${userInRole.getAuthoritiesForProgramAsString(selectedProgram)}</td>
              <td scope="col"><a data-desc="Edit" href="<@spring.url '/manageUsers/showPage?programId=${selectedProgram.code}&userId=${encrypter.encrypt(userInRole.id)}'/>">Edit</a> / <a href="#" name="removeuser" data-desc="Remove" id="remove_${encrypter.encrypt(userInRole.id)}">Remove</a></td>
            </tr>
            </#list>
              </tbody>
            
          </table>
        </div>
        <!-- .content-box-inner --> 
      </div>
      <!-- .content-box --> 
      
    </article>
  </div>
  <#include "/private/common/global_footer.ftl"/> </div>

<!-- Scripts --> 
<script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script> 
<script type="text/javascript" src="<@spring.url '/design/default/js/admin/manageusers.js' />"></script> 
<script type="text/javascript" src="<@spring.url '/design/default/js/libraries.js' />"></script> 
<script type="text/javascript" src="<@spring.url '/design/default/js/script.js' />"></script> 
<script type="text/javascript" src="<@spring.url '/design/default/js/help.js' />"></script>
</body>
</html>
