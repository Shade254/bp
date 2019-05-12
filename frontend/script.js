var port = 8080;

	// flag of choosed planning mode(closed, p2p)
	var which;
	
	var fromId;
	var toId;
	var fromLat;
	var fromLon;
	var toLon;
	var toLat;

	var markerFrom;
  	var markerTo;

  	mapboxgl.accessToken = 'pk.eyJ1Ijoic2hhZGUyNTQiLCJhIjoiY2p0Ym44b3diMG1hczQzcDhlMWhiM203OCJ9.5aB7eFT6mMTBGqt_7QSr-A';

  	var map = new mapboxgl.Map({
		container: "map",
		style: "mapbox://styles/mapbox/outdoors-v9",
		center: [14.45, 50.08],
		zoom: 10
	});

  	document.getElementById("closedTourButton").click();

  	// switching tabs
	function openTab(evt, tourName) {
  		
  		// Declare all variables
  		var i, tabcontent, tablinks;
  		fromLon = null;
  		fromLat = null;
  		toLon = null;
  		toLat = null;
  		
  		document.getElementById("p2pGoal").value = "";
		document.getElementById("p2pStart").value = "";
		document.getElementById("closedStart").value = "";

  		// Get all elements with class="tabcontent" and hide them
  		tabcontent = document.getElementsByClassName("tabcontent");
  		for (i = 0; i < tabcontent.length; i++) {
    		tabcontent[i].style.display = "none";
  		}

  		// Get all elements with class="tablinks" and remove the class "active"
  		tablinks = document.getElementsByClassName("tablinks");
  		for (i = 0; i < tablinks.length; i++) {
   	 		tablinks[i].className = tablinks[i].className.replace(" active", "");
  		}

  		// Show the current tab, and add an "active" class to the button that opened the tab
  		document.getElementById(tourName).style.display = "block";
  		evt.currentTarget.className += " active";

  		if(tourName == 'closedTourTab'){
  			which = 1;
  		} else {
  			which = 0;
  		}

  		if(markerFrom != null){
  			markerFrom.remove();
  		}

  		if(markerTo != null){
  			markerTo.remove();
  		}

	}

	// load empty sources and layer styles to the map - ready for data to be filled in
 	// also loads borders of the graph from the server 
	map.on("load", function() {
		map.addSource("closedwalk", {"type":"geojson", "data":{
  			"type": "FeatureCollection",
  			"features": []
		}});

		map.addSource("usedpoint", {"type":"geojson", "data":{
  			"type": "FeatureCollection",
  			"features": []
		}});

		map.addSource("lastpoint", {"type":"geojson", "data":{
  			"type": "FeatureCollection",
  			"features": []
		}});
	
		map.addLayer({
			"id": "tours",
			"type": "line",
			"source": "closedwalk",
			"layout": {
				"line-join": "round",
				"line-cap": "round"
			},
			"paint": {
				"line-color": "#FF0000",
				"line-width": 7
			},
			"filter": ["==", "$type", "LineString"],
		});

		map.addLayer({
			"id": "turningPointLast",
			"type": "circle",
			"source": "lastpoint",
			"paint": {
				"circle-radius": 6,
				"circle-color": "#0000FF"
			},
			"filter": ["==", "$type", "Point"],
		});

		map.addLayer({
			"id": "turningPointUsed",
			"type": "circle",
			"source": "usedpoint",
			"paint": {
				"circle-radius": 6,
				"circle-color": "#00FF00"
			},
			"filter": ["==", "$type", "Point"],
		});

		map.addSource("boundingBox", {"type":"geojson", "data": {
  			"type": "FeatureCollection",
  			"features": []
		}});
	
		map.addLayer({
			"id": "borders",
			"type": "line",
			"source": "boundingBox",
			"layout": {
				"line-join": "round",
				"line-cap": "round"
			},
			"paint": {
				"line-color": "#000000",
				"line-width": 1
			},
			"filter": ["==", "$type", "Polygon"],
		});

		var borderRequest = new XMLHttpRequest();
		borderRequest.onload = function(){
			if(borderRequest.status == 200){
				var data = JSON.parse(this.response)
				map.getSource('boundingBox').setData(data)
			} else {
				console.log(this.response)
			}
		}

		borderRequest.open('GET', 'http://localhost:'+ port +'/border', true)
		borderRequest.send()
	});

	// handling basic response from the server
	// this response contains individual tours for display as well as metadata about length, roundness, etc.
	var request = new XMLHttpRequest()
	request.onload = function () {
		console.log(request.status)
		if(request.status == 200){
			var data = JSON.parse(this.response)
			console.log(data);

			var select = document.getElementById("routes");

    		for(var i = select.options.length - 1 ; i >= 0 ; i--){
        		select.remove(i);
    		}

    		for(var i = data.tours.length; i>0 ; --i) {
        		var option = document.createElement('option');
        		option.text = option.value = i;
        		select.add(option, 0);
    		}
			document.getElementById("timeInfo").innerHTML = "Response time - " + data.responseTime + " ms";

			map.getSource('closedwalk').setData(data.tours[0].path)
			map.getSource('usedpoint').setData(data.tours[0].turningPointPlanned)
			map.getSource('lastpoint').setData(data.tours[0].turningPointLast)
			document.getElementById("lengthInfo").innerHTML = "Length - " + data.tours[0].length + " m";
			document.getElementById("roundnessInfo").innerHTML = "Roundness - " + parseFloat(data.tours[0].roundness).toFixed(5);
			document.getElementById("totalInfo").innerHTML = "Total cost - " + data.tours[0].totalCost;
			select.value = 1
			
			select.onchange=function(e){
				map.getSource('closedwalk').setData(data.tours[parseInt(e.target.value)-1].path)
				map.getSource('usedpoint').setData(data.tours[parseInt(e.target.value)-1].turningPointPlanned)
				map.getSource('lastpoint').setData(data.tours[parseInt(e.target.value)-1].turningPointLast)
				document.getElementById("lengthInfo").innerHTML = "Length - " + data.tours[parseInt(e.target.value)-1].length + " m";
				document.getElementById("roundnessInfo").innerHTML = "Roundness - " + parseFloat(data.tours[parseInt(e.target.value)-1].roundness).toFixed(5);
				document.getElementById("totalInfo").innerHTML = "Total cost - " + data.tours[parseInt(e.target.value)-1].totalCost;

			}
		} else {
			console.log(this.response)
			alert("Not found, try again")
		}
	}


	// when user clicks in the map, map its location to the nearest node(on server), put marker there and set it as origin or destination
	map.on('click', function (e){

		var mapRequest = new XMLHttpRequest();
		mapRequest.onload = function(){
			if(mapRequest.status == 200){

				var data = JSON.parse(this.response)
				if(which == 1){
					fromLon = data.longitude;
					fromLat = data.latitude;
					document.getElementById("closedStart").value = parseInt(data.id);
				} else {
					if(fromLon == null && fromLat == null){
						fromLon = data.longitude;
						fromLat = data.latitude;
						document.getElementById("p2pStart").value = parseInt(data.id);
					} else if(toLon == null && toLat == null){
						toLon = data.longitude;
						toLat = data.latitude;
						document.getElementById("p2pGoal").value = parseInt(data.id);
					} else {
						fromLon = data.longitude;
						fromLat = data.latitude;
						toLon = null
						toLat = null
						document.getElementById("p2pGoal").value = "";
						document.getElementById("p2pStart").value = parseInt(data.id);
					}
				}

				if(markerFrom != null){
  					markerFrom.remove();
  				}

  				if(markerTo != null){
  					markerTo.remove();
  				}

				if(fromLat !== null && fromLon !== null){
					var el = document.createElement('div');
  					el.className = 'marker';
					markerFrom = new mapboxgl.Marker(el)
  					.setLngLat([fromLon, fromLat])
  					.addTo(map);
    			}

    			if(toLat !== null && toLon !== null){
					var el = document.createElement('div');
  					el.className = 'marker';
					markerTo = new mapboxgl.Marker(el)
  					.setLngLat([toLon, toLat])
  					.addTo(map);
    			}

			}


		}
		var url = 'http://localhost:'+port+'/map?lat=' + e.lngLat.lat + '&lon=' + e.lngLat.lng;
		mapRequest.open('GET', url, true)
		mapRequest.send()
		console.log(url)
	});



// collect info from input fields and send it to the server
		function submitClick(){
			var minL, maxL, start, goal, factor, strict, tours, method;
			if(which == 1){
				minL = parseInt(document.getElementById("closedMin").value)
				maxL = parseInt(document.getElementById("closedMax").value)
				start = parseInt(document.getElementById("closedStart").value)
				factor = parseFloat(document.getElementById("closedFactor").value)
				strict = document.getElementById("closedStrict").value
				tours = parseInt(document.getElementById("closedCount").value)
			} else {
				minL = parseInt(document.getElementById("p2pMin").value)
				maxL = parseInt(document.getElementById("p2pMax").value)
				start = parseInt(document.getElementById("p2pStart").value)
				goal = parseInt(document.getElementById("p2pGoal").value)
				factor = parseFloat(document.getElementById("p2pFactor").value)
				strict = document.getElementById("p2pStrict").value
				tours = parseInt(document.getElementById("p2pCount").value)
			}

			method = document.getElementById("method_select").checked

			var url;


			if(which == 1){
				url = 'http://localhost:'+port+'/closed?start='+start+'&minLength='+minL+'&maxLength='+maxL;
			} else {
				url = 'http://localhost:'+port+'/p2p?start='+start+'&goal='+goal+'&minLength='+minL+'&maxLength='+maxL;
			}
			
			if(!isNaN(factor)){
				url = url + '&factor=' + factor
			}

			if(!isNaN(strict)){
				url = url + '&strict=' + strict
			}

			if(!isNaN(tours)){
				url = url + '&tours=' + tours
			}

			url = url + '&method=' + method


			console.log(url)
			request.open('GET', url, true)
			request.send()
	}