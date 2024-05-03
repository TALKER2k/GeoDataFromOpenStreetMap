import React, { useState } from "react";
import { Form, Button, Table, Placeholder } from "react-bootstrap";
import './css/FormPage.css';

const RegistrationPage = () => {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");

    const handleSubmit = (e) => {
        e.preventDefault();

        if (password !== confirmPassword) {
            alert("Пароли не совпадают.");
            return;
        }

        const registrationFormDto = {
            username,
            password
        };

        fetch("http://localhost:8089/auth/register", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(registrationFormDto)
        })
            .then(response => response.json())
            .then(data => {
                console.log("Успешная регистрация:", data);
                alert("Регистрация прошла успешно.");
            })
            .catch(error => {
                console.error("Ошибка при регистрации:", error);
                alert("Произошла ошибка при регистрации.");
            });
    };

    return (
        <div className="register">
            <Table>
                <tbody>
                    <tr>
                        <td>
                            <Form.Label>Логин</Form.Label>
                        </td>
                        <td>
                            <Form.Control type="text" value={username} onChange={e => setUsername(e.target.value)} 
                            placeholder="Your login" />
                        </td>
                    </tr>

                    <tr>
                        <td>
                            <Form.Label>Пароль</Form.Label>
                        </td>
                        <td>
                            <Form.Control type="password" value={password} onChange={e => setPassword(e.target.value)}
                            placeholder="Your password" />
                        </td>
                    </tr>

                    <tr>
                        <td>
                            <Form.Label>Подтверждение пароля</Form.Label>
                        </td>
                        <td>
                            <Form.Control type="password" value={confirmPassword} onChange={e => setConfirmPassword(e.target.value)}
                            placeholder="Confirm password" />
                        </td>
                    </tr>

                    <tr>
                        <td colSpan={2}>
                            <Button variant="primary" type="submit" onClick={handleSubmit}>
                                Зарегистрироваться
                            </Button>
                        </td>
                    </tr>
                </tbody>
            </Table>
        </div>
    );
};

export default RegistrationPage;