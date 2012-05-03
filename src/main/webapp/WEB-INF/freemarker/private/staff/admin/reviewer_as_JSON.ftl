<#macro json_string string>${string?js_string?replace("\\'", "\'")?replace("\\>", ">")}</#macro>
{
"id" : "<@json_string "${newReviewer.id}"/>",
"firstName" : "<@json_string "${newReviewer.firstName}"/>",
"lastName" : "<@json_string "${newReviewer.lastName}"/>",
}