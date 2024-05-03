import 'bootstrap/dist/css/bootstrap.min.css';
import Button from 'react-bootstrap/Button';
import { useState, useRef, useEffect } from 'react';
import MapOL from './ol/MapOL.js';
import Header from './Header.js';
import "./App.css";
import axios from 'axios';

function App() {

    return (
        <div className="App">
            <Header />
        </div>
    );
}


export default App;