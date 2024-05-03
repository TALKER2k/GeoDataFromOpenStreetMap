import React from 'react'
import { Form, Button } from 'react-bootstrap';
function RegistrationPage() {
    return (
        <div>
            <h2>Регистрация</h2>
            <Form>
                <Form.Group controlId="formBasicEmail">
                    <Form.Label>Электронная почта</Form.Label>
                    <Form.Control type="email" placeholder="Введите адрес электронной почты" />
                </Form.Group>

                <Form.Group controlId="formBasicPassword">
                    <Form.Label>Пароль</Form.Label>
                    <Form.Control type="password" placeholder="Введите пароль" />
                </Form.Group>

                <Form.Group controlId="formBasicPasswordConfirm">
                    <Form.Label>Подтвердите пароль</Form.Label>
                    <Form.Control type="password" placeholder="Подтвердите пароль" />
                </Form.Group>

                <Button variant="primary" type="submit">
                    Зарегистрироваться
                </Button>
            </Form>
        </div>
    );
}

export default RegistrationPage;