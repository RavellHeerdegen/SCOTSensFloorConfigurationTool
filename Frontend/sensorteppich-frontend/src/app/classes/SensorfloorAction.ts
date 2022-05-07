import { SensorfloorActionParam } from './SensorfloorActionParam';

export class SensorfloorAction {
    private actionName: string;
    private actionId: number;
    private pluginId: string;
    private activation: string;
    private params: SensorfloorActionParam[];

    public constructor() {
        this.params = new Array<SensorfloorActionParam>();
    }

    public getActionName(): string {
        return this.actionName;
    }
    public setActionName(value: string) {
        this.actionName = value;
    }

    public getActionId(): number {
        return this.actionId;
    }
    public setActionId(value: number) {
        this.actionId = value;
    }

    public getPluginId(): string {
        return this.pluginId;
    }
    public setPluginId(value: string) {
        this.pluginId = value;
    }

    public getActivation(): string {
        return this.activation;
    }
    public setActivation(value: string) {
        this.activation = value;
    }

    public getParams(): SensorfloorActionParam[] {
        return this.params;
    }
    public setParams(value: SensorfloorActionParam[]) {
        this.params = value;
    }
}


