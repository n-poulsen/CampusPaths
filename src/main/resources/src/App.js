import React, { Component } from 'react';
import CssBaseline from '@material-ui/core/CssBaseline';
import './App.css';
import PageManager from "./pageManager";


class App extends Component {
    render() {
        return (
            <React.Fragment>
                <CssBaseline />
                {
                    <div className="App">
                        <header className="App-header">
                            <h1>Campus Paths</h1>
                            <PageManager />
                        </header>
                    </div>
                }
            </React.Fragment>
        );
    }
}

export default App;
