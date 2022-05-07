import { Field } from './Field';
import { SensorfloorAction } from './SensorfloorAction';


export class SensorfloorGroup {
    private groupID: number;
    private groupName: string;
    private fields: Field[];
    private actionsStepOn: SensorfloorAction[];
    private actionsStepOff: SensorfloorAction[];

    public constructor() {
        this.fields = new Array<Field>();
        this.actionsStepOn = new Array<SensorfloorAction>();
        this.actionsStepOff = new Array<SensorfloorAction>();
    }

    public getgroupID(): number {
        return this.groupID;
    }
    public setgroupID(value: number) {
        this.groupID = value;
    }

    public getGroupName(): string {
        return this.groupName;
    }
    public setGroupName(value: string) {
        this.groupName = value;
    }

    public getFields(): Field[] {
        return this.fields;
    }
    public setFields(value: Field[]) {
        this.fields = value;
    }

    public getActionsStepOn(): SensorfloorAction[] {
        return this.actionsStepOn;
    }
    public setActionsStepOn(value: SensorfloorAction[]) {
        this.actionsStepOn = value;
    }

    public getActionsStepOff(): SensorfloorAction[] {
        return this.actionsStepOff;
    }
    public setActionsStepOff(value: SensorfloorAction[]) {
        this.actionsStepOff = value;
    }
}