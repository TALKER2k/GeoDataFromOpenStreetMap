import React, { useEffect, useRef, useState } from 'react'
import axios from "axios";
import Button from "react-bootstrap/Button";
import MapOL from "../ol/MapOL";

function RoutePage() {
    const [routes, setRoutes] = useState([]);
    const [selectedCountry, setSelectedCountry] = useState('');
    const [selectedRoute, setSelectedRoute] = useState('');
    const [selectedBus, setSelectedBus] = useState('');
    const [position, setPosition] = useState(null);
    const [intervalId, setIntervalId] = useState(null);
    const mapRef = useRef(null);
    const [countries, setCountries] = useState([]);
    const [cities, setCities] = useState([]);
    const [selectedCity, setSelectedCity] = useState('');
    const [selectedTypeSearch, setSelectedTypeSearch] = useState('');
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

    useEffect(() => {
        function fetchCities() {
            if (selectedCity) {
                axios.get(`http://localhost:8089/route/getAllRouteByOSM/${selectedCity}`, {
                    headers: {
                        Authorization: `Bearer ${token}`
                    }
                })
                    .then(response => {
                        console.log('Routes:', response.data);
                        setRoutes(response.data);
                    })
                    .catch(error => {
                        console.error('Error fetching Routes:', error);
                    });
            }
        }

        fetchCities();
    }, [selectedCity]);

    // useEffect(() => {
    //     async function fetchRoutes() {
    //         try {
    //             const response = await axios.get('http://localhost:8089/route/getAllRoutes', {
    //                 headers: {
    //                   Authorization: `Bearer ${token}`
    //                 }
    //               });
    //             setRoutes(response.data);
    //         } catch (error) {
    //             console.error('Error fetching routes:', error);
    //         }
    //     }

    //     fetchRoutes();
    // }, []);

    // function showRoutes() {
    //     mapRef.current.clearMarkers();
    //     if (selectedRoute) {
    //         fetch(`http://localhost:8089/route/getLinesByRouteId/${selectedRoute}`, {
    //             method: 'GET',
    //             headers: {
    //                 Authorization: `Bearer ${token}`
    //               }
    //         })
    //             .then(response => {
    //                 if (response.ok) {
    //                     return response.json();
    //                 } else {
    //                     throw new Error('Failed to fetch lines for route ');
    //                 }
    //             })
    //             .then(lines => {
    //                 console.log('Lines for route', + ':', lines);
    //                 lines.forEach(line => {
    //                     mapRef.current.drawLine(line);
    //                 })
    //             })
    //             .catch(error => {
    //                 console.error('Error:', error);
    //                 alert('An error occurred while fetching lines for route ');
    //             });

    //         fetch(`http://localhost:8089/route/getPointsByRouteId/${selectedRoute}`, {
    //             method: 'GET',
    //             headers: {
    //                 Authorization: `Bearer ${token}`
    //               }
    //         })
    //             .then(response => {
    //                 if (response.ok) {
    //                     return response.json();
    //                 } else {
    //                     throw new Error('Failed to fetch lines for route ');
    //                 }
    //             })
    //             .then(routes => {
    //                 console.log('Lines for route', + ':', routes);
    //                 routes.forEach(route => {
    //                     mapRef.current.drawRoute(route);
    //                 })
    //             })
    //             .catch(error => {
    //                 console.error('Error:', error);
    //                 alert('An error occurred while fetching lines for route ');
    //             });
    //     }


    // }

    function showRoutes() {
        mapRef.current.clearMarkers();
        if (selectedRoute) {
            fetch(`http://localhost:8089/route/getLineByRouteIdByOsm/${selectedRoute}`, {
                method: 'GET',
                headers: {
                    Authorization: `Bearer ${token}`
                }
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Failed to fetch lines for route');
                }
                return response.json();
            })
            .then(lines => {
                console.log('Lines for route:', lines);
                lines.forEach(line => {
                    mapRef.current.drawLine(line);
                });
        
                // Теперь делаем второй запрос
                return fetch(`http://localhost:8089/route/getPointsByRouteIdByOsm/${selectedRoute}`, {
                    method: 'GET',
                    headers: {
                        Authorization: `Bearer ${token}`
                    }
                });
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Failed to fetch points for route');
                }
                return response.json();
            })
            .then(routes => {
                console.log('Routes for route:', routes);
                routes.forEach(route => {
                    mapRef.current.drawRoute(route);
                });
            })
            .catch(error => {
                console.error('Error:', error);
                alert('An error occurred while fetching data for route');
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

    const handleCityChange = (event) => {
        setSelectedCity(event.target.value);
    };

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
            <select className="button" value={selectedRoute} onChange={handleRouteChange}>
                <option value="">Select route</option>
                {routes.map(route => (
                    <option key={route.id} value={route.id}>
                        {route.ref + ' - ' + route.name}
                    </option>
                ))}
            </select>
            <Button className="button" onClick={showRoutes} variant="primary">Submit</Button>
            <Button className="button" onClick={showMyLocation} variant="primary">My location</Button>
            <Button className="button" onClick={updateRoutes} variant="primary">Update data routes</Button>
            <Button className="button" onClick={showRoutes} variant="primary">Show route</Button>

            <MapOL ref={mapRef} />
        </div>
    );
}

export default RoutePage