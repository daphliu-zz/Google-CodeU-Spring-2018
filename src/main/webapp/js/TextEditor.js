function init(id){
	//check for null param
	id = id || "";
	alert(id);
	//Gets the document of iframe
	var iframe = document.getElementById(id);
	var doc = iframe.contentDocument; 

	//Make iframe into a text editor
	doc.designMode = "on";
	doc.contentEditable = true;
	doc.write('<html><head><style>body{ margin:3px; word-wrap:break-word; word-break: break-all; }</style></head><body>CodeUtest</body></html>') ;
};
//TODO: make into a switch statement;
function setBold(id)
{
	var currWin = document.getElementById(id).contentWindow;
	currWin.document.execCommand("Bold", false, null);
	currWin.focus();
};
