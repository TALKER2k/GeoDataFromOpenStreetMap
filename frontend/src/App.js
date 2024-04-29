import 'bootstrap/dist/css/bootstrap.min.css';
import Button from 'react-bootstrap/Button';
import { useState, useRef, useEffect } from 'react';
import MapOL from './ol/MapOL.js';
import "./App.css";
import axios from 'axios';

function App() {
    const [city, setCity] = useState('City');
    const mapRef = useRef(null);
    const [countries, setCountries] = useState([]);
    const [cities, setCities] = useState([]);
    const [routes, setRoutes] = useState([]);
    const [buses, setBuses] = useState([]);
    const [selectedCountry, setSelectedCountry] = useState('');
    const [selectedCity, setSelectedCity] = useState('');
    const [selectedRoute, setSelectedRoute] = useState('');
    const [selectedBus, setSelectedBus] = useState('');
    const [TypesSearch, setTypesSearch] = useState([]);
    const [selectedTypeSearch, setSelectedTypeSearch] = useState('');

    useEffect(() => {
        async function fetchCountries() {
            try {
                const response = await axios.get('http://localhost:8089/api/countries');
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
        if (selectedRoute) {
            fetch(`http://localhost:8089/route/getLinesByRouteId/${selectedRoute}`, {
                    method: 'GET',
                })
                .then(response => {
                    if (response.ok) {
                        return response.json(); // Преобразуем ответ в JSON
                    } else {
                        throw new Error('Failed to fetch lines for route ');
                    }
                })
                .then(lines => {
                    // Обработка полученных данных о линиях
                    console.log('Lines for route', + ':', lines);
                    lines.forEach(coordinatesArray => {
                        const line = coordinatesArray.map(coord => ({
                          lon: coord.x,
                          lat: coord.y
                        }));
                        mapRef.current.drawLine(line);

                    })

                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('An error occurred while fetching lines for route ');
                });
        }

    }

    useEffect(() => {
        function fetchCities() {
            if (selectedCountry) {
                axios.get(`http://localhost:8089/api/countries/${selectedCountry}/cities`)
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

    const handleRouteChange = (event) => {
        setSelectedRoute(event.target.value);
    };

    const handleCityChange = (event) => {
        setSelectedCity(event.target.value);
    };

    const handleBusChange = (event) => {
        setSelectedBus(event.target.value);
    };

    const handleTypeSearchChange = (event) => {
        setSelectedTypeSearch(event.target.value);
    }

    const handleSubmit = () => {
        // Отправка выбранных идентификаторов страны и города на сервер
        console.log('Selected country id:', selectedCountry);
        console.log('Selected city id:', selectedCity);
    };

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
            <Button className="button" onClick={searchCity} variant="primary">Submit</Button>
            <Button className="button" onClick={updateGates} variant="primary">Update data</Button>
            <Button className="button" onClick={showMyLocation} variant="primary">Your location</Button>
            <select className="button" value={selectedTypeSearch} onChange={handleTypeSearchChange}>
                <option value="">Select Type search</option>
                <option value={1}>Data base</option>
                <option value={2}>OSM</option>
            </select>
            <Button className="button" onClick={updateRoutes} variant="primary">Update route</Button>
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


export default App;