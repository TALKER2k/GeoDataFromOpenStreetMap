import React, { useState } from 'react';
import { Form, Button } from 'react-bootstrap';

function SignUpPage() {
    const [login, setLogin] = useState('');
    const [password, setPassword] = useState('');

    const handleLoginChange = (e) => {
        setLogin(e.target.value);
    };

    const handlePasswordChange = (e) => {
        setPassword(e.target.value);
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        // Здесь можно выполнить логику входа, например, отправить данные на сервер
        fetch(`http://localhost:8089/auth/login/${login}&${password}`, {
            method: 'POST',
        })
        console.log('Email:', login);
        console.log('Password:', password);
        // Очищаем поля после отправки формы
        setLogin('');
        setPassword('');
    };

    return (
        <div>
            <h2>Enter</h2>
            <Form>
                <Form.Group onSubmit={handleSubmit} controlId="Login">
                    <Form.Label>Login</Form.Label>
                    <Form.Control
                        type="login"
                        placeholder="Input login"
                        value={login}
                        onChange={handleLoginChange} />
                </Form.Group>

                <Form.Group controlId="Password">
                    <Form.Label>Password</Form.Label>
                    <Form.Control
                        type="password"
                        placeholder="Input password"
                        value={password}
                        onChange={handlePasswordChange} />
                </Form.Group>

                <Button variant="primary" type="submit">
                    Sign in
                </Button>
            </Form>
        </div>
    );
}

export default SignUpPage;