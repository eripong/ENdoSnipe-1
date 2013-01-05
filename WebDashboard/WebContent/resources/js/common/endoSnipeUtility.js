ENdoSnipe.Utility = {};
ENdoSnipe.Utility.makeLogo = function(id, title) {
	var idName = 'logo';
	$("#" + id).append('<div id="' + idName + '" class="contentHeader" ></div>');
	$('#' + idName).append(
			'<h1>' + title + '</h1>');	
	$('#' + idName)
			.append(
					'<div class="logo"></div>');
};