function Mysearch() {
		
		var searchStyle = document.getElementById('search_style');
		document.getElementById('search').addEventListener('input', function() {
		  if (!this.value) {
			searchStyle.innerHTML = "";
			return;
		  }
		  searchStyle.innerHTML = ".searchable:not([data-index*=\"" + this.value.toLowerCase() + "\"]) { display: none; }";
		});
		
		return ;              // The function returns nothing
		};
		
		
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