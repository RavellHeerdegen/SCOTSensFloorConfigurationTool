import { Component, OnInit } from '@angular/core';
import { OpenAPEService } from 'src/app/service/open-ape.service';
import { StylerComponent } from '../styler/styler.component';
import { BackendService } from 'src/app/service/backend.service';
import { ConfigureService } from 'src/app/service/configure.service';
import { SensorfloorConfiguration } from 'src/app/classes/SensorfloorConfiguration';
import { Router } from '@angular/router';
import { SensorfloorGroup } from 'src/app/classes/SensorfloorGroup';
import { SensorfloorAction } from 'src/app/classes/SensorfloorAction';
import { HelpdialogComponent } from '../helpdialog/helpdialog.component';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-change-configuration',
  templateUrl: './change-configuration.component.html',
  styleUrls: ['./change-configuration.component.css']
})
export class ChangeConfigurationComponent implements OnInit {

  groupname: string;
  selectedAction: SensorfloorAction;
  selectedCarpetgroup: SensorfloorGroup;
  steponactions: SensorfloorAction[];
  stepoffactions: SensorfloorAction[];
  stepon: boolean;

  constructor(
    public backendservice: BackendService,
    public configureservice: ConfigureService,
    private openapeservice: OpenAPEService,
    private router: Router,
    public styler: StylerComponent,
    public helpdialog: MatDialog,
    public dialogref: MatDialogRef<HelpdialogComponent>) { }

  helptext = "You are in the process of configuring your newly created group. " +
    "Here you can first select whether an action should happen when you step " +
    "on the corresponding group ('On Entry') or when you leave the group " +
    "('On Exit'). " +
    "Click on the '+' to link new devices to your group. " +
    "Click on the '-' to delete already linked devices for that group. " +
    "Click 'Done' to return to the home screen and finish the group configuration."

  ngOnInit(): void {
    this.groupname = this.configureservice.getChosenGroupName();

    this.selectedCarpetgroup = this.configureservice.getChosenCarpetGroup();
    this.stepoffactions = this.selectedCarpetgroup.getActionsStepOff();
    this.steponactions = this.selectedCarpetgroup.getActionsStepOn();

    this.stepon = this.configureservice.getOnEntry();
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

  styleMaster() {
    return this.styler.styleMaster();
  }

  showStepOnActionsList()
  {
    this.configureservice.setOnEntry(true);
    this.stepon = true;
  }

  showStepOffActionsList()
  {
    this.configureservice.setOnEntry(false);
    this.stepon = false;
  }

  selectAction(action: SensorfloorAction) {
    if (this.selectedAction == action) {
      this.selectedAction = null;
    }
    else {
      this.selectedAction = action;
    }
  }

  removeObject() {
    // Remove action from frontend carpet configuration object
    let stepOnStatus = this.selectedAction.getActivation();

    if (stepOnStatus) // its a step on action
    {
      if (this.steponactions.length == 1) {
        this.steponactions = new Array<SensorfloorAction>();
        this.selectedCarpetgroup.setActionsStepOn(this.steponactions);
        this.configureservice.setChosenCarpetGroup(this.selectedCarpetgroup);
      }
      else {
        this.steponactions = this.steponactions.filter(action => action.getActionId() != this.selectedAction.getActionId());
        this.selectedCarpetgroup.setActionsStepOn(this.steponactions);
        this.configureservice.setChosenCarpetGroup(this.selectedCarpetgroup);
      }
    }
    else // Step off action
    {
      if (this.stepoffactions.length == 1) {
        this.stepoffactions = new Array<SensorfloorAction>();
        this.selectedCarpetgroup.setActionsStepOff(this.stepoffactions);
        this.configureservice.setChosenCarpetGroup(this.selectedCarpetgroup);
      }
      else {
        this.stepoffactions = this.stepoffactions.filter(action => action.getActionId() != this.selectedAction.getActionId());
        this.selectedCarpetgroup.setActionsStepOff(this.stepoffactions);
        this.configureservice.setChosenCarpetGroup(this.selectedCarpetgroup);
      }
    }

    this.removeActionInBackend(this.selectedAction.getActionId());
    this.selectedAction = null;
  }

  async removeActionInBackend(actionid: number) {
    let user = this.openapeservice.getLoggedInUser();
    await this.backendservice.removeAction(actionid);
    await this.backendservice.getUserCarpetConfigurationRefreshed(user);
  }

  changeObject() {
    this.configureservice.setChosenPlugin(this.selectedAction.getPluginId());
    this.configureservice.setChosenObjectName(this.selectedAction.getActionName());
    this.configureservice.setChosenObjectAsFrontendName(this.selectedAction.getActionName());
    this.configureservice.setPreExistingActionChange(true);
    this.configureservice.setPreExistingActionObject(this.selectedAction);
    this.router.navigate(["/configure-object"]);
  }

  addObject() {
    this.configureservice.setStepOnActive(this.stepon);
    this.router.navigate(["/select-type"]);
  }

  cancelAction() {
    this.router.navigate(["/configure-sensorfloor"]);
  }
}
