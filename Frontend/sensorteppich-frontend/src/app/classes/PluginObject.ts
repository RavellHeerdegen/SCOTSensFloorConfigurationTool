export class PluginObject {

    private id: number;
    private name: string;

    /**
     * Constructs a new Sensorfloor-Object
     */
    public constructor() {
    }

    public setId(id: number) {
        this.id = id;
    }

    public setName(name: string) {
        this.name = name;
    }

    public getId() {
        return this.id;
    }

    public getName() {
        return this.name;
    }
}