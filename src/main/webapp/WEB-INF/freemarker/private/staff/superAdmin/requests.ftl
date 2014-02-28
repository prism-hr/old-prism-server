<!DOCTYPE HTML>
<#import "/spring.ftl" as spring />
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta name="prism.version" content="<@spring.message 'prism.version'/>">
<title>UCL Postgraduate Admissions</title>

<!-- Always force latest IE rendering engine (even in intranet) & Chrome Frame -->
<meta http-equiv="X-UA-Compatible" content="IE=9,chrome=1" />

<!-- Styles for Application List Page -->
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/global_private.css' />" />
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/application_list.css' />" />

<link rel="shortcut icon" type="text/css" href="<@spring.url '/design/default/images/favicon.ico' />" />

<!-- Styles for Application List Page -->

<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/jquery-ui-1.8.23.custom.css' />" />

<!--[if lt IE 9]>
		<script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
		<![endif]-->

<script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/libraries.js' />"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/script.js' />"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/superAdmin/requests.js' />"></script>
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/bootstrap.min.css' />" />
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/font-awesome.min.css' />" />
<script type="text/javascript" src="<@spring.url '/design/default/js/bootstrap.min.js' />"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/underscore-min.js' />"></script>
</head>

<!--[if IE 9]>
	<body class="ie9">
	<![endif]-->
<!--[if lt IE 9]>
	<body class="old-ie">
	<![endif]-->
<!--[if (gte IE 9)|!(IE)]><!-->
<body>
<!--<![endif]--> 
<!-- Modal -->
<div id="previewModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
  <div class="modal-header">
    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
    <h3 id="previewModalLabel"></h3>
  </div>
  <div id="previewModalContent" class="modal-body"> 
    <!-- Modal content --> 
    
  </div>
  <div class="modal-footer">
    <button class="btn" data-dismiss="modal" aria-hidden="true">Close</button>
  </div>
</div>
<!-- Wrapper Starts -->
<div id="wrapper"> <#include "/private/common/global_header.ftl"/> 
  
  <!-- Middle Starts -->
  <div id="middle"> <#include "/private/common/parts/nav_with_user_info_toggle.ftl"/>
    <@header activeTab="requests"/>
    <!-- Main content area. -->
    <article id="content" role="main"> 
      
      <!-- content box -->
      <div class="content-box">
        <div class="content-box-inner requestbox">
          <!-- searchbox -->
          <div id="table-bar">
            <div id="search-box" class="clearfix"> 

              <!-- Search/filter box. -->
              <div class="filter">
                <select class="selectCategory" name="searchCategory" id="searchCategory">
                  <option value="">Column...</option>
                </select>
                <select class="selectPredicate" name="searchPredicate" id="searchPredicate_">
                 <option value="">Column...</option>
                </select>
                <input type="text" placeholder="Filter by..." value="" name="searchTerm" id="searchTerm_new" class="filterInput">
                <button class="btn remove btn-inverse" title="Remove filter"><i class="icon icon-minus"></i></button>
                <button class="btn add btn-inverse" title="Add filter"><i class="icon icon-plus"></i></button>
  
              </div>
  
              <div class="btn-actions">
                <div class="btn-group">
                  <button id="search-go" class="btn btn-success enabled">Filter</button>
                  <button data-toggle="dropdown" class="btn btn-success dropdown-toggle"><span class="caret"></span></button>
                  <ul class="dropdown-menu">
                    <li><a id="storeFiltersBtn" href="#">Save as Default Filter</a></li>
                    <li><a href="/pgadmissions/applications?applyFilters=default">Load Default Filter</a></li>
                    <li><a href="/pgadmissions/applications?applyFilters=urgent">Display Urgent Applications</a></li>
                    <li><a href="/pgadmissions/applications?applyFilters=active">Display Active Applications</a></li>
                  </ul>
                </div>
                <button id="search-reset" type="button" class="btn btn-info">Clear</button>
              </div>

            </div>
          </div>

          <table class="data table table-striped table-condensed table-bordered table-hover appliList" id="opportunityRequestsList" border="0">
            <colgroup>
              <col style="width: 25%" />
              <col />
              <col style="width: 80px;" />
              <col style="width: 130px;" />
              <col style="width: 80px" />
            </colgroup>
            <thead>
              <tr>
                <th class="sortable" scope="col" id="AUTHOR" onclick="sortList(this)">Author</th>
                <th class="sortable" scope="col" id="PROGRAM_TITLE" onclick="sortList(this)">Programme</th>
                <th class="sortable" scope="col" id="STATUS" onclick="sortList(this)">Status</th>
                <th scope="col">Actions</th>
                <th class="sortable" scope="col" id="CREATED_DATE" onclick="sortList(this)">Created</th>
              </tr>
            </thead>
            
            <tbody>
              
              <#list opportunityRequests as opportunityRequest>
                <tr id="row_${opportunityRequest.id?string}" class="applicationRow" >
                  <#if opportunityRequest.status == "NEW">
                    <td data-desc="This request requires your attention" class="flagred applicant-name">
                    <i class="icon-bell-alt"></i>
                  <#else>
                    <td class="applicant-name flaggreen">
                    <i class="icon-coffee"></i>
                  </#if>
                  ${opportunityRequest.author.displayName}
                  </td>
                  <td class="program-title">
                    ${opportunityRequest.programTitle!opportunityRequest.sourceProgram.title} 
                  </td>
                  <td class="status">
                    <#if opportunityRequest.status == "NEW">
                      <#if opportunityRequest.type == "CHANGE">
                        <span data-desc="Change Request" class="icon-status withdrawn">Change Request</span>
                      <#else>
                        <span data-desc="New Request" class="icon-status review">New Request</span>
                      </#if>
                    <#elseif opportunityRequest.status == "REJECTED">
                      <span data-desc="Rejected" class="icon-status rejected">Rejected</span>
                    <#elseif opportunityRequest.status == "APPROVED">
                      <span data-desc="Approved" class="icon-status offer-recommended">Approved</span>
                    <#elseif opportunityRequest.status == "REVISED">
                      <span data-desc="Revised" class="icon-status validation">Revised</span>
                    </#if>
                  </td>
                  <td class="centre">
                    <select class="opportunityRequestActionType selectpicker actionType" data-request-id="${opportunityRequest.id?string}" data-email="${opportunityRequest.author.email}" data-program="${opportunityRequest.programTitle!opportunityRequest.sourceProgram.title}">
                      <option class="title">Actions</option>
                      <#if user.isInRole('SUPERADMINISTRATOR') && opportunityRequest.status != "APPROVED">
                        <option value="approve">Review</option>
                      <#elseif opportunityRequest.status == "REJECTED">
                        <option value="view">Revise</option>
                      <#else>
                        <option value="view">View</option>
                      </#if>
                        <option value="email">Email Requester</option>
                    </select>
                  </td>
                  <td class="centre">
                    ${opportunityRequest.createdDate?string("dd MMM yyyy")}
                  </td>
                </tr>
              </#list>
            
            </tbody>
          </table>
          <div border="0" id="loadMoreRequestsTable"> <a href="javascript:void(0);" class="proceed-link btn btn-large btn-block btn-primary" id="loadMoreApplications">Fetch More Requests</a> </div>
        </div>
        <!-- .content-box-inner --> 
      </div>
      <!-- .content-box --> 
      
    </article>
  </div>
  <!-- Middle Ends --> 
  
  <#include "/private/common/global_footer.ftl"/> </div>
<!-- Wrapper Ends -->

</body>
</html>