
$(function() {


	//modal Display Scan of the book
	
	$(document.body).on('click', 'a[id*="onshow_scanner"]' ,function(){
		
		 console.log("je suis appelé 1 par onshow_scanner : "+this.id);
		        $('#myScanModal').one('show.bs.modal', function() {
		        	console.log("je suis appelé !#onshow_scanner");
		        	console.log("je suis appelé 2 par : "+this.id);
		        	
		        	 $.ajax({
		                 url: "/scan",
		                 context: document.body,
		                 success: function(responseText) {
		                	 console.log("modals-MyModule.js : je suis succes avant : ");
		                     //$("#graphicals_remaining").html(responseText);
		                     //$("#modal_remaining").modal("toggle");
		                	 $("#scanForm").html(responseText);
		                	// $("#scanForm").parseHTML; ///not working
		                	 console.log("modals-MyModule.js : je suis succes aprés ");
		                 }
		             }) // using the done promise callback
		             .done(function(data) {
		             	
		             	//window.location.reload(true);
		                 // log data to the console so we can see
		                //console.log("done Scanner:" + data); 
		                 //event.preventDefault();
		                 // here we will handle errors and validation messages
		             });
		        	
		        	
		        	
		        });
		        	
		    });
	
	   $('#myScanModal').on('hidden.bs.modal', function() {
			
	    	console.log("modals-MyModule.js : je suis appelé 1 par : closeFormOnCall");
	    	window.location.reload(true);
	  
	        	
	    });
	
	
});
