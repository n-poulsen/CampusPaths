import React from "react";

// Canvas is used to display the image, both the campus map and
// the path, if any, that was found between two buildings
class Canvas extends React.Component {

    // Creates a ref for the canvas, which lets us draw on it
    constructor(props){
        super(props);
        this.canvas = React.createRef();
    }

    // Once the component mounted, display the image on it
    componentDidMount(){
        const canvas = this.canvas.current;
        const ctx = canvas.getContext("2d");
        const img = new Image();
        const w = this.props.width;
        const h = this.props.height;
        img.src = this.props.imageURL;
        img.onload = function() {
            ctx.drawImage(img, 0, 0, w, h);
        };
    }

    // When called, will draw the image and the shortest path on the canvas
    updatePath(){
        const canvas = this.canvas.current;
        const ctx = canvas.getContext("2d");
        ctx.clearRect(0, 0, canvas.width, canvas.height);
        const img = new Image();
        const w = this.props.width;
        const h = this.props.height;
        img.src = this.props.imageURL;
        const path = this.props.path;
        img.onload = function() {
            ctx.drawImage(img, 0, 0, w, h);
            ctx.beginPath();
            if (path.length > 0){
                ctx.fillStyle="#FF0000";
                ctx.fillRect(path[0].origin.x, path[0].origin.y, 6, 6);
                ctx.fillRect(path[path.length - 1].dest.x, path[path.length - 1].dest.y, 6, 6);
                ctx.moveTo(path[0].origin.x, path[0].origin.y);
                for (var i = 0; i < path.length; ++i){
                    ctx.lineTo(path[i].dest.x, path[i].dest.y);
                }
                ctx.strokeStyle = "red";
            }
            ctx.stroke();
            ctx.moveTo(0, 0);
        };
    }

    // renders the component
    render(){
        return (
            <div>
                <canvas
                    ref={this.canvas}
                    width={this.props.width}
                    height={this.props.height}
                />
            </div>
        );
    }

}

export default Canvas;