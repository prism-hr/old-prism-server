<!DOCTYPE HTML>
<#import "/spring.ftl" as spring />
<html>

  <head>
  
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <title>UCL Postgraduate Admissions</title>

    <!-- Always force latest IE rendering engine (even in intranet) & Chrome Frame -->
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
    
    <!-- Styles for Application List Page -->
    <link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/global_private.css' />"/>
    <link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/private/application_list.css' />"/>
		<link rel="stylesheet" type="text/css" href="<@spring.url '/design/default/css/modal_window.css' />"/>
    <!-- Styles for Application List Page -->

    <!--[if lt IE 9]>
    <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->
  
      <script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>      
      <script type="text/javascript" src="<@spring.url '/design/default/js/libraries.js' />"></script>
      <script type="text/javascript" src="<@spring.url '/design/default/js/script.js'/>"></script>
      <script type="text/javascript" src="<@spring.url '/design/default/js/applicationList/formActions.js'/>"></script>
      <script type="text/javascript" src="<@spring.url '/design/default/js/scrollpagination.js' />"></script>
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
      
        <#include "/private/common/parts/nav_with_user_info.ftl"/>
        <@header/>
 
        <!-- Main content area. -->
        <article id="content" role="main">
					<#include "/private/common/modal_window.ftl">
          
          <!-- content box -->
          <input type="hidden" id="appList" name="appList" />
          <div class="content-box">
            <div class="content-box-inner">
          
              <#if message??>
              <div class="section-info-bar">${(message?html)!}</div>
              </#if>
              <#if RequestParameters.messageCode??>              
	              <#if messageApplication??>
	              	<#assign args = ["${messageApplication.applicationNumber}"] />
	              	<div class="section-info-bar"><@spring.messageArgs  '${RequestParameters.messageCode}' args /></div>
	              <#else>
	              	<div class="section-info-bar"><@spring.message '${RequestParameters.messageCode}' /></div>
	              </#if>             
              </#if>
              
            
							<div id="table-bar">
							
								<!-- Download button. -->
								<#if (applications?size > 0)>
								<a target="_blank" name="downloadAll" id="downloadAll" data-desc="Download selected">Download</a>
								</#if>
				
								<!-- Search/filter box. -->
								<div id="search-box"> 
									<input type="text" id="searchTerm" name="searchTerm" placeholder="Filter by..." />
									<select name="searchCategory" id="searchCategory">
										<option value="">Column...</option>
										<#list searchCategories as category>
										<option value="${category}">${category.displayValue()}</option>               
										</#list>
									</select>
									<button class="blue" type="button" id="search-go">Go</button>
									<button type="button" id="search-reset">Clear</button>
								</div>
								
							</div>
            
              <table class="data" border="0" >
                <colgroup>
                  <col style="width: 46px" />
                  <col style="width: 20%" />
                  <col />
                  <col style="width: 66px;" />
                  <col />
                  <col style="width: 90px" />
                </colgroup>
                <thead>
                  <tr>
                    <th class="centre" scope="col">
                      <input type="checkbox" name="select-all" data-desc="Select to download all" id="select-all" />
                      <input type="hidden" id="sort-column" name="sort-column" value="APPLICATION_DATE" />
                      <input type="hidden" id="sort-order" name="sort-order" value="DESCENDING" />
                      <input type="hidden" id="block-index" name="block-index" value="1" />
                    </th>
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
              
            </div><!-- .content-box-inner -->
          </div><!-- .content-box -->
              
        </article>
      </div>
      <!-- Middle Ends -->
      
      <#include "/private/common/global_footer.ftl"/>
      
    </div>
    <!-- Wrapper Ends -->
       
  </body>
</html>
