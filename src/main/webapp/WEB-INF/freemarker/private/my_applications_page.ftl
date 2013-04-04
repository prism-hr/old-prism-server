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
<script type="text/javascript" src="<@spring.url '/design/default/js/scrollpagination.js' />"></script>
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
<body>
    <!--<![endif]-->

    <!-- Wrapper Starts -->
    <div id="wrapper">

        <#include "/private/common/global_header.ftl"/>

        <!-- Middle Starts -->
        <div id="middle">

            <#include "/private/common/parts/nav_with_user_info_toggle.ftl"/> <@header/>

            <!-- Main content area. -->
            <article id="content" role="main">

                <!-- content box -->
                <input type="hidden" id="appList" name="appList" />
                <div class="content-box">
                    <div class="content-box-inner">

                        <#if alertDefinition??>
                            <div class="alert">
                                
                                <#if alertDefinition.type??>
                                    <#if alertDefinition.type.name() == "INFO"> 
                                        <i class="icon-info-sign"></i>
                                    <#elseif alertDefinition.type.name() == "WARNING"> 
                                        <i class="icon-warning-sign"></i>
                                    </#if>
                                </#if>
                                    
                                <#if alertDefinition.title??> 
                                    <strong>${alertDefinition.title}</strong>
                                </#if>
                                <#if alertDefinition.description??> 
                                    <p>${alertDefinition.description}</p>
                                </#if>
                            </div>
                        </#if>

                        <#if message??>
                        <div class="alert alert-info">
         				<i class="icon-info-sign"></i> 
                       		${(message?html)!}</div>
                        </#if> <#if RequestParameters.messageCode??> <#if messageApplication??> <#assign args = ["${messageApplication.applicationNumber}"] />
                        <div class="alert alert-info">
                          <i class="icon-info-sign"></i> 
                          <@spring.messageArgs '${RequestParameters.messageCode}' args /></div>
                        <#else>
                        <div class="alert alert-info">
          					<i class="icon-info-sign"></i> <@spring.message '${RequestParameters.messageCode}' /></div>
                        </#if> </#if>

                        <div id="table-bar">
                            <!-- Download button. -->
                            <a target="_blank" name="downloadAll" id="downloadAll" data-desc="<@spring.message 'myApps.downloadAll'/>">Download</a>
                            
                            <input type="hidden" id="searchPredicatesMap" name="searchPredicatesMap" value="$(searchPredicatesMap)" />
                            
                            <div id="search-box">
                                <#list filters as filter>
                                    <!-- Search/filter box. -->
                                    <div class="filter" id="filter_${filter_index}">
                                        <select class="selectCategory" name="searchCategory" id="searchCategory_${filter_index}">
                                            <option>Column...</option>
                                            <#list searchCategories as category>
                                                <option <#if filter.searchCategory = category>selected="selected"</#if> value="${category}">${category.displayValue()}
                                                </option>
                                            </#list>
                                        </select>
                                        
                                        <select class="selectPredicate" name="searchPredicate" id="searchPredicate_${filter_index}">
                                            <option>Choose...</option>
                                            <#list filter.searchCategory.availablePredicates as predicate>
                                                <option <#if filter.searchPredicate = predicate>selected="selected"</#if> value="${predicate}">${predicate.displayValue()}
                                                </option>
                                            </#list>
                                        </select>                                        
                                        
                                        <input class="filterInput" type="text" id="searchTerm_${filter_index}" name="searchTerm" value="${filter.searchTerm}" placeholder="Filter by..." /> 
                                        
                                        <button class="btn remove">Remove</button>
                                        <button class="btn add">Add</button>
                                     </div>
                                </#list>
                                
                                <!-- New search/filter box. -->
                                <div class="filter" id="filter">
                                    <select class="selectInput" name="searchCategory" id="searchCategory">
                                        <option value="">Column...</option>
                                        <#list searchCategories as category>
                                            <option value="${category}">${category.displayValue()}
                                            </option>
                                        </#list>
                                    </select>
                                    
                                    <select class="selectPredicate" name="searchPredicate" id="searchPredicate_new">
                                        <option>Choose...</option>
                                    </select>  
                                    
                                    <input class="filterInput" type="text" id="searchTerm_new" name="searchTerm" value="" placeholder="Filter by..." /> 
                                    <button class="btn remove">Remove</button>
                                    <button class="btn add">Add</button>
                                 </div>
                                
                                <button class="btn btn-primary" type="button" id="search-go">Go</button>
                                <button class="btn" type="button" id="search-reset">Clear</button>
                                <button class="btn" id="storeFiltersBtn">Store</button>
                                
                                <a class="btn" href="<@spring.url '/applications?reloadFilters=true'/>">Load</a>
                            </div>

                        </div>

                        <table class="data table table-striped table-condensed table-bordered table-hover" id="appliList" border="0">
                            <colgroup>
                                <col style="width: 46px" />
                                <col style="width: 20%" />
                                <col />
                                <col style="width: 60px;" />
                                <col style="width: 130px;" />
                                <col style="width: 80px" />
                            </colgroup>
                            <thead>
                                <tr>
                                    <th class="centre" scope="col"><input type="checkbox" name="select-all" data-desc="<@spring.message 'myApps.toggleAll'/>" id="select-all" /> <input type="hidden" id="sort-column" name="sort-column" value="APPLICATION_DATE" /> <input type="hidden" id="sort-order" name="sort-order" value="DESCENDING" /> <input type="hidden" id="block-index" name="block-index" value="1" /></th> 
                                    <#if !user.isInRole('APPLICANT')>
                                    	<th class="sortable" scope="col" id="APPLICANT_NAME" onclick="sortList(this)">Applicant</th>
                                    <#else>
                                    	<th scope="col">Application #</th>
                                    </#if>
                                    <th class="sortable" scope="col" id="PROGRAMME_NAME" onclick="sortList(this)">Programme</th>
                                    <th class="sortable" scope="col" id="APPLICATION_STATUS" onclick="sortList(this)" class="header-text-center">Status</th>
                                    <th scope="col">Actions</th>
                                    <th class="sortable" scope="col" id="APPLICATION_DATE" onclick="sortList(this)">Submitted</th>
                                </tr>
                            </thead>
                            <tbody id="applicationListSection">
                            </tbody>
                        </table>
                        
                        <div id="loadMoreApplicationsTable" border="0">
                            <a id="loadMoreApplications" class="proceed-link btn btn-large btn-block btn-primary" href="javascript:void(0);">Display next 50 Applications</a>
                        </div>

                    </div>
                    <!-- .content-box-inner -->
                </div>
                <!-- .content-box -->

            </article>
        </div>
        <!-- Middle Ends -->

        <#include "/private/common/global_footer.ftl"/>

    </div>
    <!-- Wrapper Ends -->

</body>
</html>
