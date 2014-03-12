<!DOCTYPE HTML>
<#import "/spring.ftl" as spring />
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
        <title>UCL Postgraduate Admissions</title>
        <link href="<@spring.url '/design/default/css/bootstrap.min.css' />" rel="stylesheet">
    </head>
    <body>
	   <script type="text/javascript" src="<@spring.url '/design/default/js/jquery.min.js' />"></script>
	   <#if feedkey??><input type="hidden" id="feedKey" value="${feedKey}"/></#if>
	   <#if feedKeyValue??><input type="hidden" id="feedKeyValue" value="${feedKeyValue}"/></#if> 
       <#include "/private/prospectus/adverts.ftl"/>
    </body>
</html>
