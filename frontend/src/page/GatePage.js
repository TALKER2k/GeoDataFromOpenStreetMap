import React, { Component, useEffect, useRef, useState } from 'react'
import Button from "react-bootstrap/Button";
import axios from "axios";
import MapOL from "../ol/MapOL";

function GatePage() {
    const mapRef = useRef(null);
    const [countries, setCountries] = useState([]);
    const [cities, setCities] = useState([]);
    const [selectedCountry, setSelectedCountry] = useState('');
    const [selectedCity, setSelectedCity] = useState('');
    const [selectedTypeSearch, setSelectedTypeSearch] = useState('');
    const [position, setPosition] = useState(null);
    const [intervalId, setIntervalId] = useState(null);
    const token = localStorage.getItem("jwtToken");

    useEffect(() => {
        async function fetchCountries() {
            try {
                const response = (await axios.get('http://localhost:8089/api/countries', {
                    headers: {
                      Authorization: `Bearer ${token}`
                    }
                  }));
                console.log('Countries:', response.data);
                setCountries(response.data);    
                console.log('Страны:', response.data);
                console.log('Страны после установки:', countries);
            } catch (error) {
                console.error('Error fetching countries:', error);
            }
        }

        fetchCountries();
    }, []);



    useEffect(() => {
        function fetchCities() {
            if (selectedCountry) {
                axios.get(`http://localhost:8089/api/countries/${selectedCountry}/cities`, {
                    headers: {
                      Authorization: `Bearer ${token}`
                    }
                  })
                    .then(response => {
                        console.log('Cities:', response.data);
                        setCities(response.data);
                    })
                    .catch(error => {
                        console.error('Error fetching cities:', error);
                    });
            }
        }

        fetchCities();
    }, [selectedCountry]);


    const handleCountryChange = (event) => {
        setSelectedCountry(event.target.value);
    };


    const handleCityChange = (event) => {
        setSelectedCity(event.target.value);
    };


    const handleTypeSearchChange = (event) => {
        setSelectedTypeSearch(event.target.value);
    }


    function createRequestSearchCity(request) {
        fetch(request, {
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
                    alert('No results found for the city: ' + selectedCity);
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

    function searchCity() {
        const dbRequest = 'http://localhost:8089/settings_gates/getAllGatesByDB?city=' + selectedCity;
        const osmRequest = 'http://localhost:8089/settings_gates/getAllGatesByOSM?city=' + selectedCity;
        if (selectedTypeSearch === '1') {
            createRequestSearchCity(dbRequest);
        } else {
            createRequestSearchCity(osmRequest);
        }
    }

    function updateGates() {
        fetch('http://localhost:8089/settings_gates/update', {
            method: 'GET',
            headers: {
                Authorization: `Bearer ${token}`
              }
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

    function startTracking() {
        const id = setInterval(() => {
            fetchPosition();
        }, 5000);
        setIntervalId(id);
    }

    function stopTracking() {
        clearInterval(intervalId);
        setIntervalId(null);
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

    function handleCheckGatesAround() {
        fetch('http://localhost:8089/gps/checkGatesAround', {
            headers: {
                Authorization: `Bearer ${token}`
              }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                console.log('Request to /checkGatesAround successful');
            })
            .catch(error => {
                console.error('There was a problem with the request:', error);
            });
    }

    return (
        <div className="App">
            <select className="button" value={selectedCountry} onChange={handleCountryChange}>
                {Array.isArray(countries) && countries.map(country => (
                    <option key={country.countryId} value={country.countryId}>
                        {country.name}
                    </option>
                ))}
            </select>
            <select className="button" value={selectedCity} onChange={handleCityChange}>
                <option value="">Select city</option>
                {cities.map(city => (
                    <option key={city.cityId} value={city.cityId}>
                        {city.name}
                    </option>
                ))}
            </select>
            <Button className="button" onClick={handleCheckGatesAround} variant="primary">Open Gate Around</Button>
            <Button className="button" onClick={searchCity} variant="primary">Show lift gates</Button>
            <Button className="button" onClick={updateGates} variant="primary">Update data for lift gates</Button>
            <Button className="button" onClick={showMyLocation} variant="primary">My location</Button>
            <select className="button" value={selectedTypeSearch} onChange={handleTypeSearchChange}>
                <option value="">Select Type search</option>
                <option value={1}>Data base</option>
                <option value={2}>OSM</option>
            </select>
            <Button className="button" onClick={startTracking} variant="primary">Start Tracking</Button>
            <Button className="button" onClick={stopTracking} variant="danger">Stop Tracking</Button>
            <MapOL ref={mapRef} />
        </div>
    );
}

export default GatePage;

