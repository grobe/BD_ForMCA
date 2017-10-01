function Mysearch(userFilter) {
		//auto filter of the list of BD into the html page
		var searchStyle = document.getElementById('search_style');
		document.getElementById('search').addEventListener('input', function() {
		  
			console.log("value = "+this.value);
		    if (!this.value) {
			searchStyle.innerHTML = "";
			console.log("value dans if = "+this.value);
			return;
		  }
		  searchStyle.innerHTML = ".searchable:not([data-index*=\"" + this.value.toLowerCase() + "\"]) { display: none; }";
		  console.log("value dans else = "+this.value.toLowerCase());
		});
		
		//display the list of collection into the field "search" filter by the one viewing the list of comics
		$( "#search" ).autocomplete({
   		  source: "/searchCollection?userFilter="+userFilter,
 	          minLength: 2,
 	          select: function( event, ui ) { 
                 console.log("listDB : select autocomplete  :"+ui.item.value);
                 document.getElementById('search').value = ui.item.value;
                 //i'm generating an event 'input' in order to be able to filter the page content thx to the listener function i have 
                 //created above.
                 var event = new Event('input', {
								    'bubbles': true,
								    'cancelable': true
								});
								
				   document.getElementById('search').dispatchEvent(event);
                 return false;
              }
 	      });
 	      

		
		return ;              // The function returns nothing
		};

		
function titleCollection(userFilter) {
			
			$( "#title" ).autocomplete({
		 		  source: "/searchCollection?userFilter="+userFilter,
			          minLength: 2,
			          select: function( event, ui ) { 
		               console.log("bdInfo : select autocomplete  :"+ui.item.value);
		               document.getElementById('search').value = ui.item.value;
		               document.getElementById('title').dispatchEvent(event);
		               return false;
		            }
			      });
		}	
		
function totaux(TotalBD,Totalcollection,DateFichierBD,DateRechercheWeb ){
	    
	   var TotalsBD = document.getElementById('TotalsBD');
	   var TotalsCollection = document.getElementById('TotalsCollection');
	   var DateMAJFichierBD = document.getElementById('DateMAJFichierBD');
	   var DateMAJRechercheWeb = document.getElementById('DateMAJRechercheWeb');
	          
		 TotalsBD.innerHTML = TotalBD;
		 TotalsCollection.innerHTML = Totalcollection;
		 DateMAJFichierBD.innerHTML = DateFichierBD;
		 DateMAJRechercheWeb.innerHTML = DateRechercheWeb;
	   
	return ;              // The function returns nothing
		};
		
		
		function getCover (coverSeeked ){ 
		       

		var imgMCA        = document.getElementById(coverSeeked);
			  
           
            var request = "/displayCover/"+coverSeeked;

            fetch(request).then(function(returnedValue) {
	                 returnFetchValue=returnedValue.status;
	                 returnFetchValueText=returnedValue.text();
	                 contentType = returnedValue.headers.get("content-type");
	                 //console.log("cover=" + contentType);
	                 //console.log("cover : returnFetchValue.status=" + returnFetchValue);
	                 //console.log("cover : returnFetchValueText=" + returnFetchValueText);
	                
	                 if (returnFetchValue!="200"){
	                	
	                
	                	 console.log("cover : error Message : " + returnedValue.statusText); 
	                 }else{                                            //to be updated to remove HArcoded :bdReturnedValues
	                	
	                	 console.log("cover : NoError on : " );
	                	 //console.log("myModule.js :NoErrorMessage : " + returnFetchValueText); 
	                 }
	                 return returnFetchValueText;
	                
           }).then(function (value){
           
           	console.log("fetch.cover.then=" + value);
           	 imgMCA.setAttribute("src", value);
           	
           });
            return ; 
			}