
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
		                	 console.log("je suis succes avant par : "+this.id);
		                     //$("#graphicals_remaining").html(responseText);
		                     //$("#modal_remaining").modal("toggle");
		                	 $("#scanForm").html(responseText);
		                	// $("#scanForm").parseHTML; ///not working
		                     
		                 }
		             }) // using the done promise callback
		             .done(function(data) {
		             	
		             	//window.location.reload(true);
		                 // log data to the console so we can see
		                // console.log("done Scanner:" + data); 
		                 event.preventDefault();
		                 // here we will handle errors and validation messages
		             });
		        	
		        	
		        	
		        });
		        	
		    });
	
	
});
