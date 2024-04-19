
import Container from 'react-bootstrap/Container';
import Nav from 'react-bootstrap/Nav';
import Navbar from 'react-bootstrap/Navbar';
import NavDropdown from 'react-bootstrap/NavDropdown';
import Form from 'react-bootstrap/Form';
import 'bootstrap/dist/css/bootstrap.min.css';
import Button from 'react-bootstrap/Button';
import InputGroup from 'react-bootstrap/InputGroup';
import ol from "ol/dist/ol";

function App() {
  return (
    <div className="App">
        <InputGroup className="mb-3">
            <InputGroup.Text id="inputGroup-sizing-default">
                enter city
            </InputGroup.Text>
            <Form.Control
                aria-label="Default"
                aria-describedby="inputGroup-sizing-default"
            />
        </InputGroup>
        <Button onclick={searchCity()} variant="primary">Search city</Button>
        <Button onclick={updateGates()} variant="primary">Update data</Button>
        <Button onclick={showMyLocation()} variant="primary">Your location</Button>
    </div>
  );
}

function searchCity(){

}
function updateGates(){

}
function showMyLocation(){

}

export default App;
