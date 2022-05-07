import { Injectable } from '@angular/core';
import { Tile } from '../classes/Tile';
import { SensorfloorGroup } from '../classes/SensorfloorGroup';
import { SensorfloorAction } from '../classes/SensorfloorAction';

@Injectable({
  providedIn: 'root'
})
export class ConfigureService {

  private selectedTiles: Tile[];
  private originalTiles: Tile[];
  private chosenCarpetGroup: SensorfloorGroup;
  private pre_existing_action_object: SensorfloorAction;

  private stepOnActive = false;
  private pre_existing_action_change = false;

  private onEntry = true;


  private chosenPlugin = "";
  private chosenPluginAsFrontendName = "";
  private chosenObjectName = "";
  private chosenObjectAsFrontendName = "";
  private chosenGroupName = "";
  private chosenPluginType = "";

  private chosenObjectId = -1;
  private chosenGroupId = 0;

  constructor() { }

  public setOnEntry(status: boolean)
  {
    this.onEntry = status;
  }

  public getOnEntry()
  {
    return this.onEntry;
  }

  public setPreExistingActionObject(obj: SensorfloorAction)
  {
    this.pre_existing_action_object = obj;
  }

  public getPreExistingActionObject() {
    return this.pre_existing_action_object;
  }

  public setPreExistingActionChange(status: boolean) {
    this.pre_existing_action_change = status;
  }

  public getPreExistingActionChange() {
    return this.pre_existing_action_change;
  }

  public setChosenCarpetGroup(group: SensorfloorGroup) {
    this.chosenCarpetGroup = group;
  }

  public getChosenCarpetGroup() {
    return this.chosenCarpetGroup;
  }

  public setChosenPluginType(type: string) {
    this.chosenPluginType = type;
  }

  public getChosenPluginType() {
    return this.chosenPluginType;
  }

  public setChosenObjectAsFrontendName(name: string) {
    this.chosenObjectAsFrontendName = name;
  }

  public getChosenObjectAsFrontendName() {
    return this.chosenObjectAsFrontendName;
  }

  public setChosenGroupName(name: string) {
    this.chosenGroupName = name;
  }

  public getChosenGroupName() {
    return this.chosenGroupName;
  }

  public setChosenGroupId(id: number) {
    this.chosenGroupId = id;
  }

  public getChosenGroupId() {
    return this.chosenGroupId;
  }

  public setChosenObjectId(id: number) {
    this.chosenObjectId = id;
  }

  public setChosenObjectName(name: string) {
    this.chosenObjectName = name;
  }

  public getChosenObjectId() {
    return this.chosenObjectId;
  }

  public getChosenObjectName() {
    return this.chosenObjectName;
  }

  public setSelectedTiles(tiles: Tile[]) {
    this.selectedTiles = tiles;
  }

  public getSelectedTiles() {
    return this.selectedTiles;
  }

  public setOriginalTiles(tiles: Tile[]) {
    this.originalTiles = tiles;
  }

  public getOriginalTiles() {
    return this.originalTiles;
  }

  public setStepOnActive(status: boolean) {
    this.stepOnActive = status;
  }

  public getStepOnActive() {
    return this.stepOnActive;
  }

  public setChosenPlugin(chosenplugin: string) {
    this.chosenPlugin = chosenplugin;
    switch (this.chosenPlugin) {
      case "hue_light":
        this.chosenPluginAsFrontendName = "Philips HUE";
        this.setChosenPluginType("light");
        break;
      case "tradfri_light":
        this.chosenPluginAsFrontendName = "IKEA Tradfri";
        this.setChosenPluginType("light");
        break;
      case "woehlke":
        this.chosenPluginAsFrontendName = "Smart Socket";
        this.setChosenPluginType("socket");
        break;
      case "vlc":
        this.chosenPluginAsFrontendName = "VLC";
        this.setChosenPluginType("vlc");
        break;
    }
  }

  public getChosenPlugin() {
    return this.chosenPlugin;
  }

  public getChosenPluginAsFrontendName() {
    return this.chosenPluginAsFrontendName;
  }

  public deleteSelectedTilesFromGroup() {
    for (let i = 0; i < this.selectedTiles.length; i++) {
      this.selectedTiles[i].setGrouped(false);
      this.selectedTiles[i].setFrontendGroupid(-1);
      this.selectedTiles[i].setGroupname("");
    }
  }
}
