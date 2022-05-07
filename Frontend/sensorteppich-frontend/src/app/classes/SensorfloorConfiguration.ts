import { SensorfloorGroup } from './SensorfloorGroup';

export class SensorfloorConfiguration {
    private id: number;
    private userId: string;
    private carpetGroups: SensorfloorGroup[];

    public constructor() { }

    public setId(id: number) {
        this.id = id;
    }

    public setUserId(userid: string) {
        this.userId = userid;
    }

    public setCarpetGroups(groups: SensorfloorGroup[]) {
        this.carpetGroups = groups;
    }

    public getId() {
        return this.id;
    }

    public getUserId() {
        return this.userId;
    }

    public getCarpetGroups() {
        return this.carpetGroups;
    }
}