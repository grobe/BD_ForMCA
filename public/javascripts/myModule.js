
/************************
//Add an event to a component in order to submit a form by Post
//example : addEventForsubmitForm("BDscan", "@routes.BdController.scannedBD");
//
***************************/
function addEventForsubmitForm (formSubmited,urlToFetch ){
	  console.log("myModule.js : addEventForsubmitForm");
	  event="click";
	  ComponentByID="submit"+formSubmited;
	  console.log("ComponentByID = "+ComponentByID);
	  document.getElementById(ComponentByID).addEventListener(event,function(evt) {
  		handleFormSubmit(evt, formSubmited, urlToFetch);
  	}  , false);	
	  
}


//Call a form by name to submit it
function handleFormSubmit(evt, formByID, url) {
	                        console.log("myModule.js : handleBdFormSubmit : "+formByID);
	                        console.log("evt = "+evt.type);
	                        console.log("formByName = "+formByID);
	                		submitForm (document.getElementById(formByID), url)
	                		
	                	}
//Perform the submit of a form with a POSt to an external Request 
function submitForm (formE1,urlToReach){
	
	 console.log("myModule.js : MCADebut =" + formE1.length);
	 var numFields =formE1.length;
	 var form =formE1;
	 var formData =new FormData();
	 var returnFetchValue="";
	 var returnFetchValueText="";
	  for (var i = 0; i < numFields; ++i) {
			  console.log(form[i].name + " = " + form[i].value);
			  formData.append(form[i].name, form[i].value);
			 // console.log("2MCABoucle=i" + i);
			}
	  
	
	var request = new Request(urlToReach, {
		method: 'POST',
		body: formData,
		 credentials: 'same-origin' // if this property is not set the Play session mechanism based on  cookie will not work !!!!
		//mode: 'cors', 
		/*redirect: 'follow',
		//headers: new Headers({
		//	'Content-Type': 'text/plain'
		})*/
	});

	// Now use it!
	fetch(request).then(function(returnedValue) {
		                 returnFetchValue=returnedValue.status;
		                 returnFetchValueText=returnedValue.text();
		                 contentType = returnedValue.headers.get("content-type");
		                 console.log("myModule.js : contentType=" + contentType);
		                 console.log("myModule.js : MCAreturnedValue.status=" + returnFetchValue);
		                 console.log("myModule.js : MCAreturnFetchValueText=" + returnFetchValueText);
		                  
		                 
		                
		                 if (returnFetchValue!="200"){
		                	
		                	 console.log("myModule.js : error on : " + formE1.name); 
		                	 console.log("myModule.js : error Message : " + returnedValue.statusText); 
		                 }else{                                            //to be updated to remove HArcoded :bdReturnedValues
		                	
		                	 console.log("myModule.js : NoError on : " + formE1.name);
		                	 //console.log("myModule.js :NoErrorMessage : " + returnFetchValueText); 
		                 }
		                 return returnFetchValueText;
		                
	                }).then(function (value){
	                	var bdReturnedValues;
	                	console.log("MCA=" + value);
	                	if (formE1.name!="login"){
	                		bdReturnedValues = document.getElementById('bdReturnedValues');
	                		 bdReturnedValues.className += " bg-success lead";
	                		console.log("myModule.js : HTML form not login="+form.name);
	                		
	                	}else{
	                		bdReturnedValues = document.getElementById('scanForm');
	                		console.log("myModule.js : HTML form login="+form.name);
	                	} 
	                	 bdReturnedValues.innerHTML=value; //"Done";
	                	 
	                	 //i'm looking for scripts into the HTML fragment injected
	                	 //if i found it/them, i execute then
	                	 var scripts = bdReturnedValues.getElementsByTagName("script");
	                	 //console.log("myModule.js :before script for"); 
	                	 for (var i = 0; i < scripts.length; ++i) {
	                		 console.log("i="+i); 
	                	    var script = scripts[i];
	                	    eval(script.innerHTML);
	                	  }
	                	 console.log("myModule.js :after script for"); 
	                	
	                	 console.log("myModule.js : response.status=" + value.status);
	                	
	                	 console.log("myModule.js : MCAreturnFetchValue.status=" + returnFetchValue);
	                });
}

//used to convert the octet size of a picture to its size on Mo, Go or To if needed.... :) 
function bytesToSize(bytes) {
    var sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
    if (bytes == 0) return 'n/a';
    var i = parseInt(Math.floor(Math.log(bytes) / Math.log(1024)));
    if (i == 0) return bytes + ' ' + sizes[i];
    return (bytes / Math.pow(1024, i)).toFixed(1) + ' ' + sizes[i];
};