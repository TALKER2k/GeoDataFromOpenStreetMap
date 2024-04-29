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
import { Stroke, Fill, Circle as CircleStyle } from 'ol/style';
import GeoJSON from 'ol/format/GeoJSON';
import Polygon from 'ol/geom/Polygon';


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

  drawRoutes() {
    // Получаем данные о маршрутах из props
    const routes = this.props.routes;

    // Рисуем каждую линию на карте
    routes.forEach(route => {
      this.drawLine(route);
    });
  }

  drawRoute(route) {
    const geojson = JSON.parse(route);

    const coordinates = geojson.coordinates;

    // Преобразуем координаты точки в EPSG:3857
    const lon = coordinates[0]; // Долгота
    const lat = coordinates[1]; // Широта
    const projectedCoord = fromLonLat([lon, lat]);

    this.centerMap(lon, lat);

    // Создаем объект Feature для точки
    const pointFeature = new Feature({
      geometry: new Point(projectedCoord),
    });

    // Определение стиля для точки
    const pointStyle = new Style({
      image: new CircleStyle({
        radius: 5,
        fill: new Fill({ color: 'red' }),
        stroke: new Stroke({ color: 'black', width: 1 }),
      }),
    });

    // Применяем стиль к объекту Feature с точкой
    pointFeature.setStyle(pointStyle);

    // Создаем источник векторного слоя, если его еще нет
    if (!this.vectorSource) {
      this.vectorSource = new VectorSource();
      const vectorLayer = new VectorLayer({
        source: this.vectorSource,
      });
      this.map.addLayer(vectorLayer);
    }

    // Добавляем объект Feature в источник векторного слоя
    this.vectorSource.addFeature(pointFeature);
  }


  drawLine(line) {
    const geojson = JSON.parse(line);

    const coordinates = geojson.coordinates;

    // Создаем массив для хранения точек линии
    const linePoints = [];

    // Проходим по каждой паре координат и преобразуем их в EPSG:3857
    for (const coord of coordinates) {
      const lon = coord[0]; // Долгота
      const lat = coord[1]; // Широта
      const projectedCoord = fromLonLat([lon, lat]);
      linePoints.push(projectedCoord);
    }

    // Создаем объект Feature для линии
    const lineFeature = new Feature({
      geometry: new LineString(linePoints),
    });

    const lineStyle = new Style({
      stroke: new Stroke({
          color: 'blue', // Устанавливаем цвет линии
          width: 2, // Устанавливаем ширину линии
      }),
  });
  
  // Применяем стиль к объекту Feature с линией
  lineFeature.setStyle(lineStyle);

    // Создаем источник векторного слоя, если его еще нет
    if (!this.vectorSource) {
      this.vectorSource = new VectorSource();
      const vectorLayer = new VectorLayer({
        source: this.vectorSource,
      });
      this.map.addLayer(vectorLayer);
    }

    // Добавляем объект Feature в источник векторного слоя
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
