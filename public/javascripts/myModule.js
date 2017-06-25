
/************************
//Add an event to a component in order to submit a form by Post
//example : addEventForsubmitForm("BDscan", "@routes.BdController.scannedBD");
//
***************************/
function addEventForsubmitForm (formSubmited,urlToFetch ){
	  console.log("addEventForsubmitForm");
	  event="click";
	  ComponentByID="submit"+formSubmited;
	  console.log("ComponentByID = "+ComponentByID);
	  document.getElementById(ComponentByID).addEventListener(event,function(evt) {
  		handleFormSubmit(evt, formSubmited, urlToFetch);
  	}  , false);	
	  
}


//Call a form by name to submit it
function handleFormSubmit(evt, formByID, url) {
	                        console.log("handleBdFormSubmit :BDscan ");
	                        console.log("evt = "+evt.type);
	                        console.log("formByName = "+formByID);
	                		submitForm (document.getElementById(formByID), url)
	                		
	                	}
//Perform the submit of a form with a POSt to an external Request 
function submitForm (formE1,urlToReach){
	
	 console.log("MCADebut =" + formE1.length);
	 var numFields =formE1.length;
	 var form =formE1;
	 var formData =new FormData();
	 var returnFetchValue="";
	  for (var i = 0; i < numFields; ++i) {
			  console.log(form[i].name + " = " + form[i].value);
			  formData.append(form[i].name, form[i].value);
			 // console.log("2MCABoucle=i" + i);
			}
	  
	
	var request = new Request(urlToReach, {
		method: 'POST',
		body: formData
		//mode: 'cors', 
		/*redirect: 'follow',
		//headers: new Headers({
		//	'Content-Type': 'text/plain'
		})*/
	});

	// Now use it!
	fetch(request).then(function(returnedValue) {
		                 returnFetchValue=returnedValue.statusText;
		                 console.log("returnedValue.status=" + returnedValue.status);
		                 
		                 return returnedValue.text();
		                
	                }).then(function (value){
	                	 console.log("MCA=" + value);
	                	 console.log("response.statusText=" + value.statusText);
	                	 console.log("returnFetchValue.statusText=" + returnFetchValue);
	                });
}

//used to convert the octet size of a picture to its size on Mo, Go or To in needed.... :) 
function bytesToSize(bytes) {
    var sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
    if (bytes == 0) return 'n/a';
    var i = parseInt(Math.floor(Math.log(bytes) / Math.log(1024)));
    if (i == 0) return bytes + ' ' + sizes[i];
    return (bytes / Math.pow(1024, i)).toFixed(1) + ' ' + sizes[i];
};