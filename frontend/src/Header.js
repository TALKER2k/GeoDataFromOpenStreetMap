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

export default class Header extends Component {
    handleLogout = () => {
        localStorage.removeItem('jwtToken');
        window.location.href = '/auth/login';
    };

    render() {
        const token = localStorage.getItem('jwtToken');

        return(
            <div>
                <Navbar expand="lg" className="bg-body-tertiary">
                    <Container>
                        <Navbar.Brand href="/HomePage">OSMVIEW</Navbar.Brand>
                        <Navbar.Toggle aria-controls="basic-navbar-nav" />
                        <Navbar.Collapse id="basic-navbar-nav">
                            <Nav className="me-auto">
                                <Nav.Link href="/gate-page">Gate service</Nav.Link>
                                <Nav.Link href="/route-page">Route service</Nav.Link>
                                {}
                                {!token && (
                                    <>
                                        <Nav.Link href="/auth/login">Sign up</Nav.Link>
                                        <Nav.Link href="/auth/register">Registration</Nav.Link>
                                    </>
                                )}
                            </Nav>
                            {}
                            {token && (
                                <Nav>
                                    <Nav.Link onClick={this.handleLogout}>Logout</Nav.Link>
                                </Nav>
                            )}
                        </Navbar.Collapse>
                    </Container>
                </Navbar>
                <Router>
                    <Routes>
                        <Route path="/gate-page" element={<GatePage/>} />
                        <Route path="/auth/register" element={<RegistrationPage/>} />
                        <Route path="/route-page" element={<RoutePage/>} />
                        <Route path="/auth/login" element={<SignUpPage/>} />
                        <Route path="/HomePage" element={<HomePage/>} />
                    </Routes>
                </Router>
            </div>
        );
    }
}
