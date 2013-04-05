function autosuggest($fname, $lname, $email) {

	$fname.typeaheadmap({
		source : function(query, process) {
			return $.getJSON("/pgadmissions/autosuggest/allUsers/firstname/" + $fname.val(), function(data) {
				return process(data);
			});
		},
		matcher : function() {
			return true;
		},
		key : "k",
		value : "v",
		email : "d",
		items : 8,
		listener : function(k, v, d) {
			$lname.val(v);
			$email.val(d);
		},
		displayer : function(that, item, highlighted) {
			var allquery = highlighted + ' ' + item[that.value] + ' ('+ item[that.email] + ')';
			if (that.value != "") {
				return allquery;
			} else {
				return highlighted + ' (' + item[that.value] + ' )';
			}
		}
	});

	$lname.typeaheadmap({
		source : function(query, process) {
			return $.getJSON("/pgadmissions/autosuggest/allUsers/lastname/" + $lname.val(), function(data) {
				return process(data);
			});
		},
		matcher : function() {
			return true;
		},
		key : "v",
		value : "k",
		email : "d",
		items : 8,
		listener : function(k, v, d) {
			$fname.val(v);
			$email.val(d);
		},
		displayer : function(that, item, highlighted) {
			var allquery = item[that.value] + ' ' + highlighted + ' ('+ item[that.email] + ')';
			if (that.value != "") {
				return allquery;
			} else {
				return highlighted + ' (' + item[that.value] + ' )';
			}
		}
	});

	$email.typeaheadmap({
		source : function(query, process) {
			return $.getJSON("/pgadmissions/autosuggest/allUsers/email/" + $email.val(), function(data) {
				return process(data);
			});
		},
		matcher : function() {
			return true;
		},
		key : "d",
		value : "v",
		email : "k",
		items : 8,
		listener : function(k, v, d) {
			$fname.val(d);
			$lname.val(v);
		},
		displayer : function(that, item, highlighted) {
			var allquery = item[that.email] + ' ' + item[that.value] + ' ('+ highlighted + ')';
			if (that.value != "") {
				return allquery;
			} else {
				return highlighted + ' (' + item[that.value] + ' )';

			}
		}
	});
}