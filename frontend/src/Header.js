import React from 'react';
import Container from 'react-bootstrap/Container';
import Nav from 'react-bootstrap/Nav';
import Navbar from 'react-bootstrap/Navbar';
import {Route, BrowserRouter as Router, Routes} from 'react-router-dom'
import Switch, {Component} from 'react';
import Browser from "leaflet/src/core/Browser";

import GatePage from './page/GatePage'
import RegistrationPage from './page/RegistrationPage'
import RoutePage from './page/RoutePage'
import SignUpPage from './page/SignUpPage'
import HomePage from "./page/HomePage";

export default class Header extends Component{
    render() {
        return(
            <div>
                <Navbar expand="lg" className="bg-body-tertiary">
                    <Container>
                        <Navbar.Brand href="/HomePage">OSMVIEW</Navbar.Brand>
                        <Navbar.Toggle aria-controls="basic-navbar-nav" />
                        <Navbar.Collapse id="basic-navbar-nav">
                            <Nav className="me-auto">
                                <Nav.Link href="/GatePage">Gate service</Nav.Link>
                                <Nav.Link href="/RoutePage">Route service</Nav.Link>
                                <Nav.Link href="/SignUpPage">Sign up</Nav.Link>
                                <Nav.Link href="/RegistrationPage">Registration</Nav.Link>
                            </Nav>
                        </Navbar.Collapse>
                    </Container>
                </Navbar>
                <Router>
                    <Routes>
                        <Route path="/GatePage" element={<GatePage/>} />
                        <Route path="/RegistrationPage" element={<RegistrationPage/>} />
                        <Route path="/RoutePage" element={<RoutePage/>} />
                        <Route path="/SignUpPage" element={<SignUpPage/>} />
                        <Route path="/HomePage" element={<HomePage/>} />
                    </Routes>
                </Router>
            </div>
        );
    }
}
