import { Component, OnInit } from '@angular/core';
import { OpenAPEService } from 'src/app/service/open-ape.service';
import { StylerComponent } from '../styler/styler.component';
import { BackendService } from 'src/app/service/backend.service';
import { ConfigureService } from 'src/app/service/configure.service';
import { SensorfloorAction } from 'src/app/classes/SensorfloorAction';
import { SensorfloorActionParam } from 'src/app/classes/SensorfloorActionParam';
import { Router } from '@angular/router';
import { SensorfloorConfiguration } from 'src/app/classes/SensorfloorConfiguration';
import { HelpdialogComponent } from '../helpdialog/helpdialog.component';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-configure-obejct',
  templateUrl: './configure-object.component.html',
  styleUrls: ['./configure-object.component.css']
})
export class ConfigureObjectComponent implements OnInit {

  userconfiguration: SensorfloorConfiguration;
  groupname: string; // Name of the chosen group
  pluginname: string;
  plugintype: string; // Type of the plugin, i.e. light, vlc, plug
  objectname: string; // Frontendname of the chosen object
  stepon = false; // If the action on the object should get added to the actions when entering or leaving the group
  vlc_input_activated = true;

  // Light configuration
  turnonsliderOn = false; // If a lamp should get turned on
  brightnessslidervalue = 180;
  songid = 1;
  chosencolor: string = "white";

  // Socket configuration
  selectedSocketId = 1;

  // VLC configuration

  constructor(private openapeservice: OpenAPEService,
    public backendservice: BackendService,
    public configureservice: ConfigureService,
    private router: Router,
    public styler: StylerComponent,
    public helpdialog: MatDialog,
    public dialogref: MatDialogRef<HelpdialogComponent>) { }

  helptext = "You are in the process of configuring your newly created group. " +
    "Here you can configure the exact settings of your device. " +
    "Click 'Turn on' to set whether the device should turn on or off when the group is activated. " +
    "Click 'Save' to save your settings and return to the home screen."

  ngOnInit(): void {
    this.userconfiguration = this.backendservice.getUserCarpetConfiguration();
    this.groupname = this.configureservice.getChosenGroupName();
    this.pluginname = this.configureservice.getChosenPluginAsFrontendName();
    this.plugintype = this.configureservice.getChosenPluginType();
    this.objectname = this.configureservice.getChosenObjectAsFrontendName();
    this.stepon = this.configureservice.getStepOnActive();

    if (this.configureservice.getPreExistingActionChange()) // An action is about to get changed
    {
      this.adjustParametersForChange();
    }
  }

  startHelpDialog() {
    this.dialogref = this.helpdialog.open(HelpdialogComponent, {
      width: '360px',
      data: { text: this.helptext }
    });
    this.dialogref.afterClosed().subscribe(result => {
    });
  }

  helpButtonClicked() {
    this.startHelpDialog();
  }

  adjustParametersForChange() {
    let action = this.configureservice.getPreExistingActionObject();
    let params = action.getParams();

    if (action.getPluginId() == "hue_light" || action.getPluginId() == "tradfri_light") {
      // Adjust brightness, turnon slider and color
      let turnonstatus = params.find(param => param.term == "lamp_on").value; // ist true oder false
      let color = params.find(param => param.term == "color").value;
      let brightness = params.find(param => param.term == "brightness").value;
      let objectId = params.find(param => param.term == "objectId").value;

      this.configureservice.setChosenObjectId(+objectId);
      turnonstatus = turnonstatus == "true" ? true : false;

      this.turnOnSliderClicked(turnonstatus);
      this.colorButtonClicked("" + color);
      this.brightnessSliderValueChanged(Number(brightness));
    }
    else if (action.getPluginId() == "woehlke") {
      let turnonstatus = params.find(param => param.term == "newState").value;
      let socketnumber = params.find(param => param.term == "socketNumber").value;
      turnonstatus = turnonstatus == "on" ? true : false;

      this.turnOnSliderClicked(turnonstatus);
      this.changeSocketValue(Number(socketnumber));
    }
    else if (action.getPluginId() == "vlc") {
      let methodname = params.find(param => param.term == "methodname").value;
      if (methodname == "stop") {
        let turnonstatus = true;
        this.turnOnSliderClicked(turnonstatus);
      }
      else {
        let songid = params.find(param => param.term == "playlistItemId").value;
        this.songid = +songid;
      }
    }
  }

  /**
   * Registers a slider click on the turn on slider if the slider is activated and sets the new status
   * @param turnonActive the status of the slider
   */
  turnOnSliderClicked(turnonActive: boolean) {
    this.turnonsliderOn = turnonActive;
    if (this.plugintype == "vlc") {
      if (this.turnonsliderOn) {
        this.vlc_input_activated = false;
      }
      else {
        this.vlc_input_activated = true;
      }
    }
  }

  brightnessSliderValueChanged(newvalue: number) {
    this.brightnessslidervalue = newvalue;
  }

  colorButtonClicked(newvalue: string) {
    this.chosencolor = newvalue;
  }

  changeSocketValue(id: number) {
    if (id == 1) {
      this.selectedSocketId = 1;
    }
    else if (id == 2) {
      this.selectedSocketId = 2;
    }
    else if (id == 3) {
      this.selectedSocketId = 3;
    }
  }

  buttonStyle(value: string) {
    if (value == this.chosencolor) {
      return "" + value + "-selected-color-button";
    }
    else {
      return "" + value + "-color-button";
    }
  }

  cancelAction() {
    this.router.navigate(["/change-configuration"]);
  }

  buildParams() {
    let params = {};

    if (this.plugintype == "light") {
      params["objectId"] = "" + this.configureservice.getChosenObjectId();
      params["lamp_on"] = this.turnonsliderOn ? "true" : "false";
      params["brightness"] = "" + this.brightnessslidervalue;
      params["color"] = this.chosencolor;
      return params;
    }
    else if (this.plugintype == "socket") {
      params["newState"] = this.turnonsliderOn ? "on" : "off";
      params["socketNumber"] = "" + this.selectedSocketId;
      return params;
    }
    else if (this.plugintype == "vlc") {
      if (this.turnonsliderOn) {
        params["methodName"] = "stop";
      }
      else {
        params["methodName"] = "playRadioChannel";
        params["playlistItemId"] = "" + this.songid;
      }
      return params;
    }
  }

  async saveActionWithParametersToGroup() {
    if (this.configureservice.getPreExistingActionChange()) // Just change the parameteters, no new action
    {
      let new_action = {};
      let pre_existing_action = this.configureservice.getPreExistingActionObject();

      new_action["actionId"] = pre_existing_action.getActionId();
      new_action["params"] = this.buildParams();

      await this.backendservice.changeActionParameters(new_action);

      // Adjust settings of pre existing action
      let steponactions = this.configureservice.getChosenCarpetGroup().getActionsStepOn();
      let stepoffactions = this.configureservice.getChosenCarpetGroup().getActionsStepOff();

      for (let i = 0; i < steponactions.length; i++) {
        if (steponactions[i].getActionId() == pre_existing_action.getActionId()) {
          if (pre_existing_action.getPluginId().includes("light")) // Light
          {
            let params = steponactions[i].getParams();

            let brightness = params.find(param => param.term == "brightness");
            let color = params.find(param => param.term == "color");
            let turnonstatus = params.find(param => param.term == "lamp_on");

            brightness.value = "" + this.brightnessslidervalue;
            turnonstatus.value = this.turnonsliderOn ? "true" : "false";
            color.value = this.chosencolor;
            break;
          }
          else if (pre_existing_action.getPluginId().includes("woehlke")) // Socket
          {
            let params = steponactions[i].getParams();

            let newState = params.find(param => param.term == "newState");
            let socketNumber = params.find(param => param.term == "socketNumber");

            newState.value = this.turnonsliderOn ? "on" : "off";
            socketNumber.value = "" + this.selectedSocketId;
            break;
          }
        }
      }
      for (let i = 0; i < stepoffactions.length; i++) {
        if (stepoffactions[i].getActionId() == pre_existing_action.getActionId()) {
          if (pre_existing_action.getPluginId().includes("light")) // Light
          {
            let params = stepoffactions[i].getParams();

            let brightness = params.find(param => param.term == "brightness");
            let color = params.find(param => param.term == "color");
            let turnonstatus = params.find(param => param.term == "lamp_on");

            brightness.value = "" + this.brightnessslidervalue;
            turnonstatus.value = this.turnonsliderOn ? "true" : "false";
            color.value = this.chosencolor;
            break;
          }
          else if (pre_existing_action.getPluginId().includes("woehlke")) // Socket
          {
            let params = stepoffactions[i].getParams();

            let newState = params.find(param => param.term == "newState");
            let socketNumber = params.find(param => param.term == "socketNumber");

            newState.value = this.turnonsliderOn ? "on" : "off";
            socketNumber.value = "" + this.selectedSocketId;
            break;
          }
        }
      }
      this.configureservice.setPreExistingActionChange(false);
    }
    else {
      let action = {};
      action["groupId"] = "" + this.configureservice.getChosenGroupId();
      action["actionName"] = this.objectname;
      action["pluginId"] = this.configureservice.getChosenPlugin(); //Backend name i.e. hue_light
      action["activation"] = this.stepon ? "stepOn" : "stepOff";

      action["params"] = this.buildParams();

      // REST Call to the backend
      let actionId = await this.backendservice.addActionToGroup(action);

      let sensorflooraction = new SensorfloorAction();
      sensorflooraction.setActionId(actionId);
      sensorflooraction.setActionName(action["actionName"]);
      sensorflooraction.setActivation(action["activation"]);
      sensorflooraction.setPluginId(action["pluginId"]);
      let paramskeys = Object.keys(action["params"]);
      for (let i = 0; i < paramskeys.length; i++) {
        let param = new SensorfloorActionParam();
        param.term = paramskeys[i];
        param.value = action["params"][param.term];
        sensorflooraction.getParams().push(param);
      }

      let selectedtiles = this.configureservice.getSelectedTiles();
      let originaltiles = this.configureservice.getOriginalTiles();
      for (let i = 0; i < selectedtiles.length; i++) {
        let originaltile = originaltiles.find(tile => tile.getId() == selectedtiles[i].getId());
        originaltile.getActions().push(sensorflooraction);
        selectedtiles[i].getActions().push(sensorflooraction);
      }

      // Add this action to the corresponding carpetGroup in the UserConfiguration
      let carpetgroups = this.userconfiguration.getCarpetGroups();
      for (let i = 0; i < carpetgroups.length; i++) {
        if (carpetgroups[i].getgroupID() == this.configureservice.getChosenGroupId()) {
          // Add this action to the group
          if (this.stepon) // Step on action
          {
            carpetgroups[i].getActionsStepOn().push(sensorflooraction);
          }
          else {
            carpetgroups[i].getActionsStepOff().push(sensorflooraction);
          }
          this.configureservice.setChosenCarpetGroup(carpetgroups[i]);
        }
      }
    }
    this.router.navigate(["/change-configuration"]);
  }
  styleMaster() {
    return this.styler.styleMaster();
  }
}
