var address = 'http://localhost:8080';
//var address = 'http://test.umotional.net/tour-planner';

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

  	var data;

  	mapboxgl.accessToken = 'pk.eyJ1Ijoic2hhZGUyNTQiLCJhIjoiY2p0Ym44b3diMG1hczQzcDhlMWhiM203OCJ9.5aB7eFT6mMTBGqt_7QSr-A';

  	var map = new mapboxgl.Map({
		container: "map",
		style: "mapbox://styles/mapbox/outdoors-v9",
		center: [14.45, 50.08],
		zoom: 10
	});

  	document.getElementById("closedTourButton").click();
	document.getElementById("loader").style.display="none";
	//document.getElementById("select-based-flipswitch").value="second";

	function selectTour(idx){
		map.getSource('closedwalk').setData(data.tours[idx].path)
		map.getSource('usedpoint').setData(data.tours[idx].turningPointPlanned)
		map.getSource('lastpoint').setData(data.tours[idx].turningPointLast)
	}

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
		

		map.addSource("alltours", {"type":"geojson", "data":{
  			"type": "FeatureCollection",
  			"features": []
		}});

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
			"id": "tours1",
			"type": "line",
			"source": "alltours",
			"layout": {
				"line-join": "round",
				"line-cap": "round"
			},
			"paint": {
				"line-color": "#A9A9A9",
				"line-width": 5
			},
			"filter": ["==", "$type", "LineString"],
		});


		map.addLayer({
			"id": "tours",
			"type": "line",
			"source": "closedwalk",
			"layout": {
				"line-join": "round",
				"line-cap": "round"
			},
			"paint": {
				"line-color": "#556B2F",
				"line-width": 9
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

		borderRequest.open('GET', address + '/border', true)
		borderRequest.send()
	});

	// handling basic response from the server
	// this response contains individual tours for display as well as metadata about length, roundness, etc.
	var request = new XMLHttpRequest()
	request.onload = function () {
		document.getElementById("loader").style.display="none";
		console.log(request.status)
		var button = document.getElementById("submitBtn");
		button.disabled = false;
		button.value = 'Submit';

		if(request.status == 200){
			data = JSON.parse(this.response)
			console.log(data);

			var display = document.getElementById("routes1");

			document.getElementById("timeInfo").innerHTML = "Response time - " + data.responseTime + " ms";

    		for(var i = display.getElementsByTagName("li").length - 1 ; i >= 0 ; i--){
        		display.removeChild(display.getElementsByTagName("li")[i]);
    		}

    		var bestTours = 8;
    		data.tours = data.tours.slice(0, bestTours);


    		var allFeatures = [];

    		for(var i = data.tours.length; i>0 ; --i) {
        		var href = document.createElement("a");
        		href.setAttribute("href", "#");
        		href.setAttribute("class", "ui-btn ui-btn-icon-right ui-icon-carat-r");
        		href.setAttribute("onclick", "selectTour(" + (data.tours.length-i) + ")");
        		var option = document.createElement("li");
        		var tourH = document.createElement("h2");
        		var par = document.createElement("p");
        		
        		var text = document.createTextNode("Tour number " + (data.tours.length-i));
				tourH.appendChild(text);
        		
        		var kmLength = (data.tours[data.tours.length-i].length/1000).toFixed(2);
        		var round = parseFloat(data.tours[data.tours.length-i].roundness).toFixed(4);
        		var meanCost = ((data.tours[data.tours.length-i].totalCost)/data.tours[data.tours.length-i].length).toFixed(4);
        		text = document.createTextNode("L: " +kmLength+" km, R: " + round + ", C: " + meanCost);

        		par.appendChild(text);

        		href.appendChild(tourH);
        		href.appendChild(par);
        		option.appendChild(href);
        		display.appendChild(option);
        		allFeatures.push.apply(allFeatures,data.tours[data.tours.length-i].path.features);
    		}

			var collection = {
    			features: allFeatures,
    			type: 'FeatureCollection'
			};
    		map.getSource("alltours").setData(collection);


			map.getSource('closedwalk').setData(data.tours[0].path)
			map.getSource('usedpoint').setData(data.tours[0].turningPointPlanned)
			map.getSource('lastpoint').setData(data.tours[0].turningPointLast)
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
		var url = address + '/map?lat=' + e.lngLat.lat + '&lon=' + e.lngLat.lng;
		mapRequest.open('GET', url, true)
		mapRequest.send()
		console.log(url)
	});



// collect info from input fields and send it to the server
		function submitClick(e){
			e.preventDefault();

			var minL, maxL, start, goal, factor, strict, tours, method;
			if(which == 1){
				start = parseInt(document.getElementById("closedStart").value)
			} else {
				start = parseInt(document.getElementById("p2pStart").value)
				goal = parseInt(document.getElementById("p2pGoal").value)
			}

			minL = parseInt(document.getElementById("length-min").value)
			maxL = parseInt(document.getElementById("length-max").value)
			//strict = parseFloat(document.getElementById("strictInput").value)
			//tours = parseInt(document.getElementById("numInput").value)
			//factor = parseInt(document.getElementById("factorInput").value)
			//method=document.getElementById("select-based-flipswitch").value
			factor = 600;
			strict = 0.8;
			method = "second";
			tours = 20;



			var url;

			if(which == 1){
				url = address + '/closed?start='+start+'&minLength='+minL+'&maxLength='+maxL;
			} else {
				url = address + '/p2p?start='+start+'&goal='+goal+'&minLength='+minL+'&maxLength='+maxL;
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

			
			if(method == "first"){
				url = url + '&method=' + 'false'
			} else {
				url = url + '&method=' + 'true'
			}


			console.log(url)

			request.open('GET', url, true)
			request.send()
			document.getElementById("loader").style.display="block";
			return false;
	}
