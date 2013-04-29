$(document).ready(function() {
	var urlParams = getUrlVars();
	if (urlParams["rejectionIdForced"] === "true") {
		$("input[name=rejectionReason]").each(function() {
			if (!$(this).is(':checked')) {
				$(this).parent().addClass("grey-label");
				$(this).attr("disabled", "disabled");
			}
		});
	}
 });

// Read a page's GET URL variables and return them as an associative array.
function getUrlVars() {
    var vars = [], hash;
    var hashes = window.location.href.slice(window.location.href.indexOf('?') + 1).split('&');
    for (var i = 0; i < hashes.length; i++) {
        hash = hashes[i].split('=');
        vars.push(hash[0]);
        vars[hash[0]] = hash[1];
    }
    return vars;
}

