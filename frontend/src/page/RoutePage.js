import React, {useEffect, useRef, useState} from 'react'
import axios from "axios";
import Button from "react-bootstrap/Button";
import MapOL from "../ol/MapOL";

function RoutePage(){
    const mapRef = useRef(null);
    const [routes, setRoutes] = useState([]);
    const [selectedCountry, setSelectedCountry] = useState('');
    const [selectedRoute, setSelectedRoute] = useState('');
    const [selectedBus, setSelectedBus] = useState('');
    const [position, setPosition] = useState(null);
    const [intervalId, setIntervalId] = useState(null);


    useEffect(() => {
        async function fetchRoutes() {
            try {
                const response = await axios.get('http://localhost:8089/route/getAllRoutes');
                setRoutes(response.data);
            } catch (error) {
                console.error('Error fetching countries:', error);
            }
        }

        fetchRoutes();
    }, []);

    function showRoutes() {
        mapRef.current.clearMarkers();
        if (selectedRoute) {
            fetch(`http://localhost:8089/route/getLinesByRouteId/${selectedRoute}`, {
                method: 'GET',
            })
                .then(response => {
                    if (response.ok) {
                        return response.json();
                    } else {
                        throw new Error('Failed to fetch lines for route ');
                    }
                })
                .then(lines => {
                    console.log('Lines for route', + ':', lines);
                    lines.forEach(line => {
                        mapRef.current.drawLine(line);
                    })
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('An error occurred while fetching lines for route ');
                });

            fetch(`http://localhost:8089/route/getPointsByRouteId/${selectedRoute}`, {
                method: 'GET',
            })
                .then(response => {
                    if (response.ok) {
                        return response.json();
                    } else {
                        throw new Error('Failed to fetch lines for route ');
                    }
                })
                .then(routes => {
                    console.log('Lines for route', + ':', routes);
                    routes.forEach(route => {
                        mapRef.current.drawRoute(route);
                    })
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('An error occurred while fetching lines for route ');
                });
        }


    }


    const handleCountryChange = (event) => {
        setSelectedCountry(event.target.value);
    };

    const handleRouteChange = (event) => {
        setSelectedRoute(event.target.value);
    };


    const handleBusChange = (event) => {
        setSelectedBus(event.target.value);
    };

    function updateRoutes() {
        fetch('http://localhost:8089/route/updateBDRouteBus', {
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
                mapRef.current.addMarker(position.coords.longitude, position.coords.latitude, "", "");
                mapRef.current.centerMap(longitude, latitude);
            }, (error) => {
                console.error('Error getting current location:', error);
            });
        } else {
            console.error('Geolocation is not supported by this browser.');
        }
    }

    function fetchPosition() {
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition((pos) => {
                setPosition(pos.coords);
            }, (error) => {
                console.error('Error getting current location:', error);
            });
        } else {
            console.error('Geolocation is not supported by this browser.');
        }
    }

    return (
        <div className="App">
            <Button className="button" onClick={showMyLocation} variant="primary">My location</Button>
            <Button className="button" onClick={updateRoutes} variant="primary">Update data routes</Button>
            <select className="button" value={selectedRoute} onChange={handleRouteChange}>
                {Array.isArray(routes) && routes.map(route => (
                    <option key={route.id} value={route.id}>
                        {route.ref + ' - ' + route.from}
                    </option>
                ))}
            </select>
            <Button className="button" onClick={showRoutes} variant="primary">Show route</Button>

            <MapOL ref={mapRef} />
        </div>
    );
}

export default RoutePage