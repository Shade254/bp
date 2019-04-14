L.mapbox.accessToken = 'pk.eyJ1Ijoic2hhZGUyNTQiLCJhIjoiY2p0Ym44b3diMG1hczQzcDhlMWhiM203OCJ9.5aB7eFT6mMTBGqt_7QSr-A';
var map = L.mapbox.map('map')
    .setView([40, -74.50], 9)
    .addLayer(L.mapbox.styleLayer('mapbox://styles/mapbox/streets-v11'));