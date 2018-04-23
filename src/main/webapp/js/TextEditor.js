/*
 * Initialize the iframe into a RTC
 * TODO: CSS the frame
 */
function init(id)
{
	//check for null param
	id = id || "";
	//Gets the document of iframe
	var doc = document.getElementById(id).contentDocument; 

	//Make iframe into a text editor
	doc.designMode = "on";
	doc.contentEditable = true;
	doc.write('<html><head><style>body{ margin:3px; word-wrap:break-word; word-break: break-all; }</style></head><body>CodeUtest</body></html>') ;
};
//TODO: make into a switch statement while adding more functions
function setFunction(method)
{
	switch(method){
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
	}
	
};
//Onclick to submit the form
function doSubmitForm()
{
	var iframe = document.getElementById('editor');
	var doc = iframe.contentDocument; 
	
	//for test innerhtml usage
	alert(doc.body.innerHTML);
	
	document.getElementById('inHTML').value = doc.body.innerHTML; 
	document.getElementById('form').submit();
	
	//Clears out the RTC after submit
	doc.body.innerHTML = ''; 
};
