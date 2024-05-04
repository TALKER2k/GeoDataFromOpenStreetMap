import React from 'react';
import {Container} from 'react-bootstrap';
import {animated, useSpring} from 'react-spring';

function HomePage() {
    const styles = useSpring({
        from: { backgroundColor: 'lightblue' },
        to: async (next) => {
            while (true) {
                await next({ backgroundColor: 'lightgreen' });
                await next({ backgroundColor: 'lightblue' });
            }
        },
        config: { duration: 3000 },
    });

    return (
        <animated.div style={{ ...styles, minHeight: '100vh' }}>
            <Container fluid className="jumbotron text-center">
                <h1 className="display-1 display-xxl">OSMVIEW</h1>
            </Container>
        </animated.div>
    );
}

export default HomePage;