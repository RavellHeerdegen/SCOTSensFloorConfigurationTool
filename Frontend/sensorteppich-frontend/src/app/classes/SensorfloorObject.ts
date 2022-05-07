import { PluginObject } from './PluginObject';

export class SensorfloorObject {
    private pluginId: string;
    private objects: PluginObject[];

    /**
     * Constructs a new Sensorfloor-Object
     */
    public constructor() {
    }

    public setPluginId(id: string) {
        this.pluginId = id;
    }

    public setObjects(objects: PluginObject[]) {
        this.objects = objects;
    }

    public getPluginId() {
        return this.pluginId;
    }

    public getObjects() {
        return this.objects;
    }
}
