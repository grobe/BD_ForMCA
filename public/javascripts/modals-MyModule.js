
$(function() {


	//modal Display Scan of the book
	
	$(document.body).on('click', 'a[id*="onshow_scanner"]' ,function(){
		
		 console.log("je suis appelé 1 par onshow_scanner : "+this.id);
		        $('#myScanModal').one('show.bs.modal', function() {
		        	console.log("je suis appelé !#onshow_scanner");
		        	console.log("je suis appelé 2 par : "+this.id);
		        	
		        	 $.ajax({
		                 url: "/scan?test=mca&toto=tut",
		                 context: document.body,
		                 success: function(responseText) {
		                	 console.log("modals-MyModule.js : je suis succes avant : ");
		                  
		                	 $("#scanForm").html(responseText);
		                	
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
			
	    	console.log("modals-MyModule.js : je suis appelé 1 par : onshow_scanner");
	    	window.location.reload(true);
	  
	        	
	    });
	   
	   //login modal (for add feature)
	   $(document.body).on('click', 'a[id*="onshow_addBD"]' ,function(){
		     var action = $(this).attr("data-action");
		     console.log("je suis appelé action : "+action)
			 console.log("je suis appelé 1 par onshow_addBD : "+this.id);
			        $('#myLoginModal').one('show.bs.modal', function() {
			        	console.log("je suis appelé !#myLoginModal");
			        	console.log("je suis appelé 2 par : "+this.id);
			        	
			        	 $.ajax({
			                 url: "/login?callBackURL="+encodeURIComponent(action),
			                 xhrFields: {// properties mandatory in order to keep the play sessionID available
			                	 withCredentials: true
			                 },
			                 crossDomain: true,
			                 context: document.body,
			                 success: function(responseText) {
			                	 console.log("modals-MyModule.js : je suis succes avant : ");
			                  
			                	 $("#loginForm").html(responseText);
			                
			                	 console.log("modals-MyModule.js : je suis succes aprés ");
			                 }
			             }) // using the done promise callback
			             .done(function(data) {
			            	 console.log("modals-MyModule.js : done ");
			            	 $('#myLoginModal').modal().hide();
			             	//window.location.reload(true);
			                 // log data to the console so we can see
			                //console.log("done Scanner:" + data); 
			                // event.preventDefault();
			                 // here we will handle errors and validation messages
			             });
			        	
			        	
			        	
			        });
			        	
			    });
		
		   $('#myLoginModal').on('hidden.bs.modal', function() {
				
		    	console.log("modals-MyModule.js : je suis appelé 1 par : myLoginModal.hidden.bs.modal");
		    	window.location.reload(true);
		  
		        	
		    });
		
	
});
