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
        <div class="content-box-inner">

          <table class="data table table-striped table-condensed table-bordered table-hover appliList" id="opportunityRequestsList" border="0">
            <colgroup>
              <col style="width: 25%" />
              <col />
              <col style="width: 100px;" />
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
                  <td data-desc="This request requires your attention" class="flagred">
                  ${opportunityRequest.author.displayName}
                  </td>
                  <td class="program-title">
                    ${opportunityRequest.programTitle} 
                  </td>
                  
                  <td class="status">
                    <@spring.message 'opportunityRequestStatus.${opportunityRequest.status.name()}'/>
                  </td>
                  <td class="centre">
                    <select class="opportunityRequestActionType selectpicker" data-request-id="${opportunityRequest.id?string}">
                      <option class="title">Actions</option>
                      <option value="approve">Approve</option>
                    </select>
                  </td>
                   
                  <td class="centre">
                    ${opportunityRequest.createdDate?string("dd MMM yyyy")}
                  </td>
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
  <!-- Middle Ends --> 
  
  <#include "/private/common/global_footer.ftl"/> </div>
<!-- Wrapper Ends -->

</body>
</html>