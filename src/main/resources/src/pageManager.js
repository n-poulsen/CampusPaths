import React from "react";
import * as fetch from "node-fetch";
import InputBar from "./inputBar";
import Canvas from "./canvas";

//Used to display a "loading" message while the page is loading, store state for the input bar and image,
//and handle changes to the input bar
class PageManager extends React.Component {

    // Sets to the initial state, and fetches the needed data (building names, image)
    constructor(props){
        super(props);
        this.state = {
            loadedBuildings: false,
            loadedImage: false,
            buildings: [],
            imageURL: '',
            fromBuilding: '',
            toBuilding:'',
            path: [],
            imgHeight: 2964,
            imgWidth: 4330,
            scale: 3,
        };
        this.handleBuildingChange = this.handleBuildingChange.bind(this);
        this.fetchShortestPath = this.fetchShortestPath.bind(this);
        this.resetButton = this.resetButton.bind(this);
        this.updateImage = this.updateImage.bind(this);
        this.fetchBuildings();
        this.fetchImage();
        // Ref for the canvas, which is used to tell the canvas to update
        this.childCanvas = React.createRef();
    }

    // Loads the buildings from the server
    fetchBuildings(){
        fetch('http://localhost:8080/buildings')
            .then(res => res.body)
            .then(body => {
                const reader = body.getReader();
                return new ReadableStream({
                    start(controller) {
                        return pump();
                        function pump() {
                            return reader.read().then(({ done, value }) => {
                                // When no more data needs to be consumed, close the stream
                                if (done) {
                                    controller.close();
                                    return;
                                }
                                // Enqueue the next data chunk into our target stream
                                controller.enqueue(value);
                                return pump();
                            });
                        }
                    }
                })
            })
            .then(stream => new Response(stream))
            .then(response => response.json())
            .then(data => {
                const buildingsFound = [];
                // Transforms the array of buildings found to an array containing just their short and long names
                data.forEach(index => {
                    buildingsFound.push({value: index.shortName, label: index.longName});
                });
                this.setState({
                    buildings: buildingsFound,
                    loadedBuildings: true,
                    // Sets the original start and end points of the desired path to the first two buildings in
                    // the array
                    fromBuilding: buildingsFound[0].value,
                    toBuilding: buildingsFound[1].value,
                });
            })
            .catch(err => console.error(err));
    }

    // Loads the image from the server
    fetchImage(){
        fetch('http://localhost:8080/mapImage')
            .then(res => res.body)
            .then(body => {
                const reader = body.getReader();
                return new ReadableStream({
                    start(controller) {
                        return pump();
                        function pump() {
                            return reader.read().then(({ done, value }) => {
                                // When no more data needs to be consumed, close the stream
                                if (done) {
                                    controller.close();
                                    return;
                                }
                                // Enqueue the next data chunk into our target stream
                                controller.enqueue(value);
                                return pump();
                            });
                        }
                    }
                })
            })
            .then(stream => new Response(stream))
            .then(response => response.blob())
            .then(blob => URL.createObjectURL(blob))
            .then(url => this.setState({imageURL: url, loadedImage: true}))
            .catch(err => console.error(err));
    }

    // Loads the shortest path between the two buildings selected in the input bar
    fetchShortestPath(){
        const from = this.state.fromBuilding;
        const to = this.state.toBuilding;
        const url = 'http://localhost:8080/shortestPath?b1=' + from + '&b2=' + to;
        fetch(url)
            .then(res => res.body)
            .then(body => {
                const reader = body.getReader();
                return new ReadableStream({
                    start(controller) {
                        return pump();
                        function pump() {
                            return reader.read().then(({ done, value }) => {
                                // When no more data needs to be consumed, close the stream
                                if (done) {
                                    controller.close();
                                    return;
                                }
                                // Enqueue the next data chunk into our target stream
                                controller.enqueue(value);
                                return pump();
                            });
                        }
                    }
                })
            })
            .then(stream => new Response(stream))
            .then(response => response.json())
            // Sets the path to the path found, and updates the image as a callback
            .then(json => {
                this.setState({path: this.toPixels(json)});
                this.updateImage();
            })
            .catch(err => console.error(err));
    }

    // Transforms the coordinates in the array to their scaled coordinates
    toPixels(arr){
        let scaled = [];
        arr.forEach(index =>
            scaled.push({
                origin: {x: index.origin.x/this.state.scale, y: index.origin.y/this.state.scale},
                dest: {x: index.destination.x/this.state.scale, y: index.destination.y/this.state.scale},
                dist: index.distance})
        );
        return scaled;
    }

    // Tells the canvas to update its image (the image is the base image and the path drawn on it)
    updateImage(){
        this.childCanvas.current.updatePath();
    }

    // Handles a change in the building selectors
    handleBuildingChange = prop => event => {
        this.setState({ [prop]: event.target.value });
    };

    // Resets the state to the initial state
    resetButton(){
        const b = this.state.buildings;
        // Sets the state to the initial state, and updates the image as a callback
        this.setState({
                path: [],
                fromBuilding: b[0].value,
                toBuilding: b[1].value,
            },
            this.updateImage
        );

    }

    // renders the component
    render() {
        const doneLoading = this.state.loadedBuildings && this.state.loadedImage;

        if (doneLoading){
            return (
                <div>
                    <div>
                        <InputBar
                            buildings={this.state.buildings}
                            fromBuilding={this.state.fromBuilding}
                            toBuilding={this.state.toBuilding}
                            handleBuildingChange={this.handleBuildingChange}
                            goClick={this.fetchShortestPath}
                            resetButton={this.resetButton}
                        />
                    </div>
                    <div>
                        <Canvas
                            ref={this.childCanvas}
                            imageURL={this.state.imageURL}
                            height={this.state.imgHeight/this.state.scale}
                            width={this.state.imgWidth/this.state.scale}
                            path={this.state.path}
                        />
                    </div>
                </div>
            );
        }else{
            return (
                <div>
                    Loading
                </div>
            );
        }
    }

}

export default PageManager;