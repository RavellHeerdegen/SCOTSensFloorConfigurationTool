import { SensorfloorAction } from './SensorfloorAction';

export class Tile {
    private id: number;
    private x_coordinate: number;
    private y_coordinate: number;
    private disabled: boolean;
    private selected: boolean;
    private grouped: boolean;
    private frontendgroupid: number;
    private backendgroupid: number;
    private groupname: string;
    private groupnameShort: string;
    private actions: SensorfloorAction[];

    /**
     * Constructs a new Sensorfloor-Tile and sets all attributes like selected or disabled to false
     */
    public constructor(id: number, x: number, y: number, backendgroupid: number) {
        this.setId(id);
        this.setX(x);
        this.setY(y);
        this.setDisabled(false);
        this.setSelected(false);
        this.setGrouped(false);
        this.setFrontendGroupid(-1);
        this.setBackendGroupid(backendgroupid);
        this.setGroupname("");
        this.setGroupNameShort("");
        this.setActions(new Array<SensorfloorAction>());
    }

    public setId(id: number) {
        this.id = id;
    }

    public setX(x: number) {
        this.x_coordinate = x;
    }

    public setY(y: number) {
        this.y_coordinate = y;
    }

    public setDisabled(status: boolean) {
        this.disabled = status;
    }

    public setSelected(status: boolean) {
        this.selected = status;
    }

    public setGrouped(status: boolean) {
        this.grouped = status;
    }

    public setFrontendGroupid(id: number) {
        this.frontendgroupid = id;
    }

    public setBackendGroupid(id: number) {
        this.backendgroupid = id;
    }

    public setGroupname(newgroup: string) {
        this.groupname = newgroup;
    }

    public setGroupNameShort(name: string) {
        this.groupnameShort = name;
    }

    public setActions(actions: SensorfloorAction[]) {
        this.actions = actions;
    }

    public getX() {
        return this.x_coordinate;
    }

    public getY() {
        return this.y_coordinate;
    }

    public getId() {
        return this.id;
    }

    public getDisabled() {
        return this.disabled;
    }

    public getSelected() {
        return this.selected;
    }

    public getGrouped() {
        return this.grouped;
    }

    public getFrontendGroupid() {
        return this.frontendgroupid;
    }

    public getBackendGroupid() {
        return this.backendgroupid;
    }

    public getGroupname() {
        return this.groupname;
    }

    public getGroupNameShort() {
        return this.groupnameShort;
    }

    public getActions() {
        return this.actions;
    }
}