import React, {useState} from "react";
import {Button, Form} from "react-bootstrap";
import './css/FormPage.css';

const SignUpPage = () => {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [token, setToken] = useState('');

    const handleSubmit = (e) => {
        e.preventDefault();

        const loginFormDto = {
            username,
            password
        };

        fetch("http://localhost:8089/auth/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(loginFormDto)
        })
            .then(response => response.json())
            .then(data => {
                console.log("Успешный вход:", data);
                alert("Вход выполнен успешно.");
                localStorage.setItem("jwtToken", data.accessToken);
                window.location.href = '/HomePage';
            })
            .catch(error => {
                console.error("Ошибка при входе:", error);
                alert("Произошла ошибка при входе.");
            });
    };

    const handleLogin = () => {
        window.location.href = '/HomePage';
    };

    return (
        <div className="signin">
            <Form onSubmit={handleSubmit}>
                <Form.Group controlId="username">
                    <Form.Label>Логин</Form.Label>
                    <Form.Control type="text" value={username} onChange={e => setUsername(e.target.value)}
                    placeholder="Your username" />
                </Form.Group>

                <Form.Group className="password-label" controlId="password">
                    <Form.Label>Пароль</Form.Label>
                    <Form.Control type="password" value={password} onChange={e => setPassword(e.target.value)}
                    placeholder="Your password" />
                </Form.Group>

                <Button className="button-signin" variant="primary" type="submit">
                    Войти
                </Button>
            </Form>
        </div>
    );
};

export default SignUpPage;
