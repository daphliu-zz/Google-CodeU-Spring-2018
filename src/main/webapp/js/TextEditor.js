/*
 * Initialize the iframe into a RTC
 * TODO: CSS the frame
 */
function init(id) {
	// check for null param
	id = id || "";
	// Gets the document of iframe
	var doc = document.getElementById(id).contentDocument;

	// Make iframe into a text editor
	doc.designMode = "on";
	doc.contentEditable = true;
};
// TODO: make into a switch statement while adding more functions
function setFunction(method) {
	switch (method) {
	case 'Bold':
		var currWin = document.getElementById('editor').contentWindow;
		currWin.document.execCommand("Bold", false, null);
		currWin.focus();
		break;
	case 'Italic':
		var currWin = document.getElementById('editor').contentWindow;
		currWin.document.execCommand("Italic", false, null);
		currWin.focus();
		break;
	case 'Underline':
		var currWin = document.getElementById('editor').contentWindow;
		currWin.document.execCommand("Underline", false, null);
		currWin.focus();
		break;
	// TODO: resize the img file if too large
	case 'InsertIMG':
		var currWin = document.getElementById('editor').contentWindow;
		var fl = document.getElementById("btn_file");
		var file = null;
		var url = null;
		var img = new Image;
		var files = fl.files;
		if (files && files.length > 0) {
			file = files[0];
			try {
				var reader = new FileReader();
				reader.onload = function(e) {
					var url = e.target.result;
					var img = '<img src="' + url + '"/>';
					currWin.document.execCommand('insertHTML', false, img);
					currWin.focus();
				}
				reader.readAsDataURL(file);
			} catch (e) {
			}
		}
		break;
	}
	;
};
// Onclick to submit the form
function doSubmitForm() {
	var iframe = document.getElementById('editor');
	var doc = iframe.contentDocument;

	document.getElementById('inHTML').value = doc.body.innerHTML;
	document.getElementById('form').submit();

	// Clears out the RTC after submit
	doc.body.innerHTML = '';
};
