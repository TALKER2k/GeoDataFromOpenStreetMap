import Container from 'react-bootstrap/Container';
import Nav from 'react-bootstrap/Nav';
import Navbar from 'react-bootstrap/Navbar';
import NavDropdown from 'react-bootstrap/NavDropdown';
import Form from 'react-bootstrap/Form';
import 'bootstrap/dist/css/bootstrap.min.css';
import Button from 'react-bootstrap/Button';
import InputGroup from 'react-bootstrap/InputGroup';
import ol from "ol/dist/ol";
import { useEffect, useState, useRef } from 'react';
import MapOL from './ol/MapOL.js';

function App() {
    const [city, setCity] = useState('asas');
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
                    data.forEach(gate => {
                        if (mapRef.current !== null) { // Добавляем проверку на null
                            const coordinates = [gate.latitude, gate.longitude];
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

    }
    return (
        <div className="App">
            <div>Ошибка:</div>
            <input name="city" onChange={(e) => setCity(e.target.value)} />
            <Button onClick={searchCity} variant="primary">Search city</Button>
            <Button onClick={updateGates} variant="primary">Update data</Button>
            <Button onClick={showMyLocation} variant="primary">Your location</Button>
            <MapOL ref={mapRef} />
        </div>
    );
}


export default App;
