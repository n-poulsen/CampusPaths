import React from "react";
import TextField from "@material-ui/core/TextField/TextField";
import MenuItem from "@material-ui/core/MenuItem/MenuItem";
import Button from "@material-ui/core/Button/Button";
import { withStyles } from '@material-ui/core/styles';

// An Input bar consists of two selectors for the user to choose his path's starting and end points,
// as well as a button to compute and show the shortest path between the two selected buildings, and one
// to reset the app to its initial state
class InputBar extends React.Component {

    // Renders an input bar
    render() {
        const { classes } = this.props;
        return (
            <div>
                <TextField
                    className={classes.textField}
                    select
                    variant="outlined"
                    label="Path start"
                    margin='dense'
                    value={this.props.fromBuilding}
                    onChange={this.props.handleBuildingChange('fromBuilding')}
                >
                    {this.props.buildings.map(option => (
                        <MenuItem key={option.value} value={option.value}>
                            {option.label}
                        </MenuItem>
                    ))}
                </TextField>
                <TextField
                    className={classes.textField}
                    select
                    variant="outlined"
                    label="Path end"
                    margin='dense'
                    value={this.props.toBuilding}
                    onChange={this.props.handleBuildingChange('toBuilding')}
                >
                    {this.props.buildings.map(option => (
                        <MenuItem key={option.value} value={option.value}>
                            {option.label}
                        </MenuItem>
                    ))}
                </TextField>
                <Button
                    className={classes.button}
                    variant="contained"
                    size="large"
                    color="primary"
                    onClick={this.props.goClick}
                >
                    Go
                </Button>
                <Button
                    className={classes.button}
                    variant="contained"
                    size="large"
                    color="primary"
                    onClick={this.props.resetButton}
                >
                    Reset
                </Button>
            </div>
        );
    }
}

// The styles to apply to the input bar
const styles = theme => ({
    button: {
        margin: theme.spacing.unit,
    },
    textField: {
        marginLeft: theme.spacing.unit,
        marginRight: theme.spacing.unit,
        marginBottom: theme.spacing.unit,
        width: 200,
    },
});

export default withStyles(styles)(InputBar);
