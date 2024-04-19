import 'bootstrap/dist/css/bootstrap.min.css';
import Button from 'react-bootstrap/Button';
import { useState, useRef } from 'react';
import MapOL from './ol/MapOL.js';
import "./App.css";

function App() {
    const [city, setCity] = useState('City');
    const mapRef = useRef(null);

    function searchCity() {
        fetch('http://localhost:8080/settings_gates/getAllGates?city=' + city, {
            method: 'GET',
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(data => {
                if (data === null) {
                    alert('No results found for the city: ' + city);
                } else {
                    mapRef.current.clearMarkers();
                    data.forEach(gate => {
                        if (mapRef.current !== null) {
                            mapRef.current.addMarker(gate.latitude, gate.longitude, gate.phoneNumber, gate.name);
                        } else {
                            console.error('Map reference is null');
                        }
                    });
                }
            })
            .catch(error => {
                console.error('Error:', error);
            });
    }
    
    function updateGates() {
        fetch('http://localhost:8080/settings_gates/update', {
            method: 'GET',
        })
            .then(response => {
                if (response.ok) {
                    alert('Gates location updated successfully!');
                } else {
                    alert('Failed to update gates location.');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('An error occurred while updating gates location.');
            });
    }
    function showMyLocation() {
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition((position) => {
              const { latitude, longitude } = position.coords;
              mapRef.current.addMarker(position.coords.longitude, position.coords.latitude);
              mapRef.current.centerMap(longitude, latitude);
            }, (error) => {
              console.error('Error getting current location:', error);
            });
          } else {
            console.error('Geolocation is not supported by this browser.');
          }
    }
    return (
        <div className="App">
            <span>City:  </span>
            <input name="city" onChange={(e) => setCity(e.target.value)} />
            <Button className="button" onClick={searchCity} variant="primary">Search city</Button>
            <Button className="button" onClick={updateGates} variant="primary">Update data</Button>
            <Button className="button" onClick={showMyLocation} variant="primary">Your location</Button>
            <MapOL ref={mapRef} />
        </div>
    );
}


export default App;