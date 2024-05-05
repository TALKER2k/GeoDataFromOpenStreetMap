import React, { Component } from 'react';
import 'ol/ol.css';
import Map from 'ol/Map';
import View from 'ol/View';
import TileLayer from 'ol/layer/Tile';
import OSM from 'ol/source/OSM';
import VectorLayer from 'ol/layer/Vector';
import VectorSource from 'ol/source/Vector';
import Feature from 'ol/Feature';
import Point from 'ol/geom/Point';
import { fromLonLat } from 'ol/proj';
import { Circle as CircleStyle, Fill, Icon, Stroke, Style } from 'ol/style';
import Overlay from 'ol/Overlay';
import './MapStyle.css';
import LineString from 'ol/geom/LineString';


class MapOL extends Component {
  constructor(props) {
    super(props);
    this.mapRef = React.createRef();
    this.map = null;
    this.popup = null;
  }

  componentDidMount() {
    const osmLayer = new TileLayer({
      preload: Infinity,
      source: new OSM(),
    });

    const vectorSource = new VectorSource();

    const vectorLayer = new VectorLayer({
      source: vectorSource,
    });

    this.map = new Map({
      target: this.mapRef.current,
      layers: [osmLayer, vectorLayer],
      view: new View({
        center: [0, 0],
        zoom: 4,
      }),
    });

    this.map.on('pointermove', this.handleMapPointerMove.bind(this));

    this.popup = document.createElement('div');
    this.popup.className = 'ol-popup';
    this.mapRef.current.appendChild(this.popup);
  }

  componentWillUnmount() {
    this.map.setTarget(null);
  }

  componentDidUpdate(prevProps) {
    if (prevProps.routes !== this.props.routes) {
      this.drawRoutes();
    }
  }

  drawRoutes() {
    const routes = this.props.routes;

    routes.forEach(route => {
      this.drawLine(route);
    });
  }

  drawRoute(route) {
    const geojson = JSON.parse(route);

    const coordinates = geojson.coordinates;

    const lon = coordinates[0];
    const lat = coordinates[1];
    const projectedCoord = fromLonLat([lon, lat]);

    this.centerMap(lon, lat);

    const pointFeature = new Feature({
      geometry: new Point(projectedCoord),
    });

    const pointStyle = new Style({
      image: new CircleStyle({
        radius: 5,
        fill: new Fill({ color: 'red' }),
        stroke: new Stroke({ color: 'black', width: 1 }),
      }),
    });

    pointFeature.setStyle(pointStyle);

    if (!this.vectorSource) {
      this.vectorSource = new VectorSource();
      const vectorLayer = new VectorLayer({
        source: this.vectorSource,
      });
      this.map.addLayer(vectorLayer);
    }

    this.vectorSource.addFeature(pointFeature);
  }


  drawLine(line) {
    const geojson = JSON.parse(line);

    const coordinates = geojson.coordinates;

    const linePoints = [];

    for (const coord of coordinates) {
      const lon = coord[0];
      const lat = coord[1];
      const projectedCoord = fromLonLat([lon, lat]);
      linePoints.push(projectedCoord);
    }

    const lineFeature = new Feature({
      geometry: new LineString(linePoints),
    });

    const lineStyle = new Style({
      stroke: new Stroke({
        color: 'blue',
        width: 12,
      }),
    });

    lineFeature.setStyle(lineStyle);

    if (!this.vectorSource) {
      this.vectorSource = new VectorSource();
      const vectorLayer = new VectorLayer({
        source: this.vectorSource,
      });
      this.map.addLayer(vectorLayer);
    }

    this.vectorSource.addFeature(lineFeature);

  }

  addMarker(lon, lat, number, name) {
    const iconStyle = new Style({
      image: new Icon({
        width: 25,
        height: 30,
        src: 'https://openlayers.org/en/latest/examples/data/icon.png',
      }),
    });

    const marker = new Feature({
      geometry: new Point(fromLonLat([lon, lat])),
      number: number,
      name: name,
    });

    marker.setStyle(iconStyle);

    marker.on('pointerenter', (event) => {
      const coordinates = event.coordinate;
      const overlay = new Overlay({
        position: coordinates,
        positioning: 'center-center',
        offset: [0, -25],
      });
      this.map.addOverlay(overlay);
    });

    marker.on('pointerleave', () => {
      this.map.getOverlays().clear();
    });

    this.map.getLayers().item(1).getSource().addFeature(marker);
    this.centerMap(lon, lat);
  }

  centerMap(lon, lat) {
    const view = this.map.getView();
    view.setCenter(fromLonLat([lon, lat]));
    view.setZoom(12);
  }

  clearMarkers() {
    const vectorLayers = this.map.getLayers().getArray().filter(layer => layer instanceof VectorLayer);
    vectorLayers.forEach(layer => {
      layer.getSource().clear();
    });
  }

  handleMapPointerMove(event) {
    const pixel = this.map.getEventPixel(event.originalEvent);
    const feature = this.map.forEachFeatureAtPixel(pixel, (feature) => feature);
    if (feature) {
      if (feature.get('number') === undefined) {
        this.popup.innerHTML = `<div>Name: bus_stop</div>`;
        this.popup.style.display = 'block';
        this.popup.style.left = `${event.pixel[0]}px`;
        this.popup.style.top = `${event.pixel[1]}px`;
      } else {
        this.popup.innerHTML = `<div>Phone: ${feature.get('number')}</div><div>Name: ${feature.get('name')}</div>`;
        this.popup.style.display = 'block';
        this.popup.style.left = `${event.pixel[0]}px`;
        this.popup.style.top = `${event.pixel[1]}px`;
      }
    } else {
      this.popup.style.display = 'none';
    }
  }


  render() {
    return <div ref={this.mapRef} style={{ height: '80vh', width: '100%' }} />;
  }
}

export default MapOL;
