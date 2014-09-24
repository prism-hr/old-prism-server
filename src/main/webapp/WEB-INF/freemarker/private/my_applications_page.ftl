<!DOCTYPE HTML>
<#import "/spring.ftl" as spring /> <#setting locale = "en_US">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta name="prism.version" content="<@spring.message 'prism.version'/>" >
<title>UCL Postgraduate Admissions</title>

<!-- Always force latest IE rendering engine (even in intranet) & Chrome Frame -->
<meta http-equiv="X-UA-Compatible" content="IE=9,chrome=1" />

<!-- Styles for Application List Page -->
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/global_private.css' />" />
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/application_list.css' />" />
<link rel="shortcut icon" type="text/css" href="<@spring.url '/design/default/images/favicon.ico' />"/>

<!-- Styles for Application List Page -->

<!--[if lt IE 9]>
    <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
<![endif]-->


<script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>

<script type="text/javascript" src="<@spring.url '/design/default/js/libraries.js' />"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/script.js'/>"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/applicationList/formActions.js'/>"></script>
<script type="text/javascript" src="<@spring.url '/design/default/js/actions.js'/>"></script>
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/bootstrap.min.css' />"/>
<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/font-awesome.min.css' />"/>
<script type="text/javascript" src="<@spring.url '/design/default/js/bootstrap.min.js' />"></script>

</head>

<!--[if IE 9]>
  <body class="ie9">
<![endif]-->
<!--[if lt IE 9]>
  <body class="old-ie">
<![endif]-->
<!--[if (gte IE 9)|!(IE)]><!-->
<body class="applist">
<!--<![endif]--> 
 <!-- Modal Start-->
  <div id="resourcesModal" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-header">
      <h3 id="myModalLabel">Application Submitted!</h3>
    </div>
    <div class="modal-body">
      <p><strong>Now share it with your friends in social networks</strong> 
       <a href="#" target="_blank" id="sharethis" title="View more services" style="margin-left: 6px;"><img src="//s7.addthis.com/static/btn/v2/lg-share-en.gif" alt="Share"/></a>
      </p>
      <p><strong>Or just add this link</strong> in emails and on websites.</p>
      <input type="text" readonly="" aria-haspopup="true" role="textbox" class="input-xxlarge" id="modalLinkToApply">
    </div>
    <div class="modal-footer">
      <button class="btn" data-dismiss="modal" aria-hidden="true">Close</button>
    </div>
  </div>
  <!-- Modal End-->
<!-- Wrapper Starts -->
<div id="wrapper"> <#include "/private/common/global_header.ftl"/> 
  
  <!-- Middle Starts -->
  <div id="middle"> <#include "/private/common/parts/nav_with_user_info_toggle.ftl"/>
    <@header/>
    
    <!-- Main content area. -->
    <article id="content" role="main"> 
      
      <!-- content box -->
      <input type="hidden" id="appList" name="appList" />
      <div class="content-box">
        <div class="content-box-inner"> <#if alertDefinition??>
          <#if alertDefinition.type??>
            <#if alertDefinition.type.name() == "INFO"> 
            <div class="alert alert-info"> 
            	<i class="icon-info-sign"></i>
            <#elseif alertDefinition.type.name() == "WARNING">
            <div class="alert">  
            	<i class="icon-warning-sign"></i> </#if>
            </#if>
            <#if alertDefinition.title??> 
            <strong>${alertDefinition.title}.</strong> </#if>
            <#if alertDefinition.description??>
            ${alertDefinition.description}
            </#if> </div>
          </#if>
          
          <#if message??>
          <div class="alert alert-info"> <i class="icon-info-sign"></i> ${(message?html)!}</div>
          </#if> 
          <#if RequestParameters.messageCode??> 
          	<#if messageApplication??> 
            <#assign args = ["${messageApplication.applicationNumber}"] />
          <div class="alert alert-info"> <i class="icon-info-sign"></i>
            <@spring.messageArgs '${RequestParameters.messageCode}' args />
            <a href='/pgadmissions/application?view=view&applicationId=${messageApplication.applicationNumber}'> <i class="icon-reply"></i> Re-open the application.</a>
          </div>
          <#else>
          <div class="alert alert-info"> <i class="icon-info-sign"></i>
            <@spring.message '${RequestParameters.messageCode}' />
          </div>
          </#if> </#if>
          <div id="table-bar"> 
           <div id="prefilterBox" style="display:none">
          	<div class="filterOperators">
             <span>Show applications matching</span> <div id="operatorSwitch" class="switch switch-mini" data-on-label="ANY" data-off-label="ALL">
                <input id="useDisjunctionId" type="checkbox" <#if filtering.useDisjunction> checked="checked" </#if> />
             </div>
             <span>of the following filters.</span>
            </div>
           
            </div>
            <div id="search-box" class="clearfix"> 
            
            <div class="actions">
            
          	<!-- Download button. --> 
    		<div class="btn-group">
                <button class="btn dropdown-toggle" data-toggle="dropdown"> Downloads <span class="caret"></span></button>
                <ul class="dropdown-menu">
                    <!--li><a href="#" id="search-report-html">Report in HTML</a></li>
                    <li><a href="#" id="search-report-json">JSON</a></li-->
                    <li><a href="#" id="search-report-csv">Spreadsheet Report</a></li>
                    <li><a href="#" id="search-report-summary">Summary Report</a></li>
      				<li class="divider"></li>
                    <li><a href="#" name="downloadAll" id="downloadAll"><i class="icon-download-alt"></i> Download  PDF</a></li>
                </ul>
              </div>
             
            <input type="hidden" id="searchPredicatesMap" name="searchPredicatesMap" value='${searchPredicatesMap}' />
            <input type="hidden" id="applicationStatusValues" name="applicationStatusValues" 
              value='<#list applicationStatusValues as value>${value.displayValue()}<#if value_has_next>,</#if></#list>'
            />
          </div>
            
            <#list filtering.filters as filter> 
              <!-- Search/filter box. -->
              <div class="filter" id="filter_${filter_index}">
                <select class="selectCategory" name="searchCategory" id="searchCategory_${filter_index}">
                  <option value="">Column...</option>
                  <#list searchCategories as category>
                    <option <#if filter.searchCategory = category>selected="selected"</#if> value="${category}">${category.displayValue()}</option>
                  </#list>
                </select>
                <select class="selectPredicate" name="searchPredicate" id="searchPredicate_${filter_index}">
                  <#list filter.searchCategory.availablePredicates as predicate>
                    <option <#if filter.searchPredicate = predicate>selected="selected"</#if> value="${predicate}">${predicate.displayValue()}</option>
                  </#list>
                </select>

                <#if filter.searchCategory == "APPLICATION_STATUS">
                  <select id="searchTerm_${filter_index}" class="filterInput selector" name="filterInput" >
                    <#list applicationStatusValues as value>
                      <option value="${value.displayValue()}"
                        <#if filter.searchTerm = value.displayValue()>selected</#if>                      
                      >${value.displayValue()}</option>
                    </#list>
                  </select>
                <#elseif filter.searchCategory == "LAST_EDITED_DATE" || filter.searchCategory == "SUBMISSION_DATE" || filter.searchCategory == "CLOSING_DATE">
                  <input id="searchTerm_${filter_index}" class="filterInput half date" type="text" name="searchTerm" value="${filter.searchTerm}" placeholder="Filter by..." />
                <#else>
                  <input id="searchTerm_${filter_index}" class="filterInput" type="text" name="searchTerm" value="${filter.searchTerm}" placeholder="Filter by..." />
                </#if>
                <button class="btn remove btn-inverse" title="Remove filter"><i class="icon icon-minus"></i></button>
                <button class="btn add btn-inverse" title="Add filter"><i class="icon icon-plus"></i></button>
              </div>
              </#list> 
              
              <#if filtering.filters?size==0>
              <!-- New search/filter box. -->
              <div class="filter" id="filter">
                <select class="selectCategory" name="searchCategory" id="searchCategory">
                  <option value="">Column...</option>
                  <#list searchCategories as category>
                  <option value="${category}">
                  ${category.displayValue()}
                  </option>	
                  </#list>
                </select>
                <select class="selectPredicate" name="searchPredicate" id="searchPredicate_new">
                </select>
                <input class="filterInput" type="text" id="searchTerm_new" name="searchTerm" value="" placeholder="Filter by..." />		
                 <button class="btn remove btn-inverse" title="Remove filter"><i class="icon icon-minus"></i></button>
                <button class="btn add btn-inverse" title="Add filter"><i class="icon icon-plus"></i></button>
              </div>
              </#if>
              
                  <div class="btn-actions">
                      <div class="btn-group">
                        <button class="btn btn-success" id="search-go">Filter</button>
                        <button class="btn btn-success dropdown-toggle" data-toggle="dropdown"><span class="caret"></span></button>
                        <ul class="dropdown-menu">
                            <li><a href="#" id="storeFiltersBtn">Save as Default Filter</a></li>
                            <li><a href="<@spring.url '/applications?applyFilters=default'/>">Load Default Filter</a></li>
                           	<li><a href="<@spring.url '/applications?applyFilters=urgent'/>">Display Urgent Applications</a></li>
                           	<li><a href="<@spring.url '/applications?applyFilters=active'/>">Display Active Applications</a></li>
                        </ul>
                      </div>
                     <button class="btn btn-info" type="button" id="search-reset">Clear</button>
                  </div>
            </div>
               
          </div>
          <table class="data table table-striped table-condensed table-bordered table-hover appliList" id="appliList" border="0">
            <colgroup>
            <col style="width: 46px" />
            <col style="width: 25%" />
            <col />
            <col style="width: 64px" />
            <col style="width: 80px;" />
            <col style="width: 130px;" />
            <col style="width: 80px" />
            </colgroup>
            <thead>
              <tr>
                <th class="centre" scope="col"><input type="checkbox" name="select-all" data-desc="<@spring.message 'myApps.toggleAll'/>" id="select-all" />
                  <input type="hidden" id="sort-column" name="sort-column" value="${filtering.sortCategory}" />
                  <input type="hidden" id="sort-order" name="sort-order" value="${filtering.order}" />
                  <input type="hidden" id="block-index" name="block-index" value="1" /></th>
                  <input type="hidden" id="latest-considered-flag-index" name="latest-considered-flag-index" value="0" /></th>
                <#if !user.isInRole('APPLICANT')>
                <th class="sortable" scope="col" id="APPLICANT_NAME" onclick="sortList(this)">Applicant</th>
                <#else>
                <th scope="col">Application #</th>
                </#if>
                <th class="sortable" scope="col" id="PROGRAMME_NAME" onclick="sortList(this)">Programme</th>
                <th class="sortable" scope="col" id="RATING" onclick="sortList(this)">Rating</th>
                <th class="sortable" scope="col" id="APPLICATION_STATUS" onclick="sortList(this)" class="header-text-center">Status</th>
                <th scope="col">Actions</th>
                <th class="sortable" scope="col" id="APPLICATION_DATE" onclick="sortList(this)">Submitted</th>
              </tr>
            </thead>
            <tbody id="applicationListSection">
            </tbody>
          </table>
          <div id="loadMoreApplicationsTable" border="0"> <a id="loadMoreApplications" class="proceed-link btn btn-large btn-block btn-primary" href="javascript:void(0);">Fetch More Applications</a> </div>
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