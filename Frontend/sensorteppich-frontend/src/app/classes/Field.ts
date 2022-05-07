export class Field {
    private x: number;
    private y: number;

    public constructor() { }

    public setX(x: number) {
        this.x = x;
    }

    public setY(y: number) {
        this.y = y;
    }

    public getX() {
        return this.x;
    }

    public getY() {
        return this.y;
    }
}