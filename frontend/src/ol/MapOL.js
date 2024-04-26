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
import { Icon, Style } from 'ol/style';
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
    // Проверяем, изменились ли данные о маршрутах
    if (prevProps.routes !== this.props.routes) {
      this.drawRoutes();
    }
  }

  drawLine(line) {
    const coordinates = line.geom.coordinates;
    const lineString = new LineString(coordinates);
    const feature = new Feature({
      geometry: lineString,
    });

    const vectorSource = this.map.getLayers().getArray()[1].getSource();
    vectorSource.addFeature(feature);
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
      const coordinates = feature.getGeometry().getCoordinates();
      this.popup.innerHTML = `<div>Phone: ${feature.get('number')}</div><div>Name: ${feature.get('name')}</div>`;
      this.popup.style.display = 'block';
      this.popup.style.left = `${event.pixel[0]}px`;
      this.popup.style.top = `${event.pixel[1]}px`;
    } else {
      this.popup.style.display = 'none';
    }
  }

  render() {
    return <div ref={this.mapRef} style={{ height: '90vh', width: '100%' }} />;
  }
}

export default MapOL;
