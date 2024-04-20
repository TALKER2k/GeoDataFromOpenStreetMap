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
    const [selectedCountry, setSelectedCountry] = useState('');
    const [selectedCity, setSelectedCity] = useState('');

    useEffect(() => {
        async function fetchCountries() {
            try {
                const response = await axios.get('http://localhost:8080/api/countries');
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
                axios.get(`http://localhost:8080/api/countries/${selectedCountry}/cities`)
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

    const handleSubmit = () => {
        // Отправка выбранных идентификаторов страны и города на сервер
        console.log('Selected country id:', selectedCountry);
        console.log('Selected city id:', selectedCity);
    };

    function searchCity() {
        fetch('http://localhost:8080/settings_gates/getAllGates?city=' + selectedCity, {
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
            <MapOL ref={mapRef} />
        </div>
    );
}


export default App;