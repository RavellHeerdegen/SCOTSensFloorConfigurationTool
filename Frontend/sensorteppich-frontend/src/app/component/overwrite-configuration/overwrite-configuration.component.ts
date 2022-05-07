import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute, ParamMap } from '@angular/router';
import { BackendService } from 'src/app/service/backend.service';
import { OpenAPEService } from 'src/app/service/open-ape.service';
import { StylerComponent } from '../styler/styler.component';
import { ConfigureService } from 'src/app/service/configure.service';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { group } from '@angular/animations';
import { SensorfloorGroup } from 'src/app/classes/SensorfloorGroup';
import { SensorfloorAction } from 'src/app/classes/SensorfloorAction';
import { OverwriteConfirmationDialogComponent } from '../overwrite-confirmation-dialog/overwrite-confirmation-dialog.component';
import { HelpdialogComponent } from '../helpdialog/helpdialog.component';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-overwrite-configuration',
  templateUrl: './overwrite-configuration.component.html',
  styleUrls: ['./overwrite-configuration.component.css']
})
export class OverwriteConfigurationComponent implements OnInit {

  groupname: string;
  groupId: number;
  group: SensorfloorGroup;
  configureService: ConfigureService;
  selected = 'nothing';

  groupList: string[] = [];

  constructor(
    private router: Router,
    private backendservice: BackendService,
    private openapeservice: OpenAPEService,
    public configureservice: ConfigureService,
    public dialog: MatDialog,
    public helpdialog: MatDialog,
    public dialogref: MatDialogRef<HelpdialogComponent>,
    public styler: StylerComponent) {
    this.configureService = configureservice;
  }

  helptext = "You are in the process of configuring your group. " +
    "Here you can apply the settings of another group to your selected group. " +
    "Click on the drop-down-button to choose one of the displayed groups. Then click on 'Overwrite' to apply the settings. " +
    "After that confirm your choice to continue." +
    "Click on 'Cancel' to return to the group configuration."

  ngOnInit(): void {
    this.groupname = this.configureService.getChosenGroupName();
    this.groupId = this.configureService.getChosenGroupId();
    let userconfiguration = this.backendservice.getUserCarpetConfiguration();
    let groups = userconfiguration.getCarpetGroups();

    for (let i = 0; i < groups.length; i++) {
      if (this.groupId != groups[i].getgroupID()) {
        if (groups[i].getGroupName().length > 0) {
          this.groupList.push(groups[i].getGroupName());
        }
      }
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

  openDialog() {
    const dialogRef = this.dialog.open(OverwriteConfirmationDialogComponent);
    let user = this.openapeservice.getLoggedInUser();
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.overwriteConfiguration();
        this.doRedirect(user);
      }
    });
  }
  async doRedirect(user) {
    await this.backendservice.getUserCarpetConfigurationRefreshed(user);
    setTimeout(() => {
      this.router.navigate(["/configure-sensorfloor"]);
    }, 1000);
  }
  styleMaster() {
    return this.styler.styleMaster();
  }

  async overwriteConfiguration() {
    this.groupname = this.configureService.getChosenGroupName();
    this.groupId = this.configureService.getChosenGroupId();
    this.group = this.configureService.getChosenCarpetGroup();
    let userconfiguration = this.backendservice.getUserCarpetConfiguration();
    let groups = userconfiguration.getCarpetGroups();
    let user = this.openapeservice.getLoggedInUser();

    for (let i = 0; i < groups.length; i++) {
      if (this.groupId != groups[i].getgroupID()) {
        if (groups[i].getGroupName().length > 0) {
          if (groups[i].getGroupName() == this.selected) {
            for (const action of this.group.getActionsStepOn()) {
              await this.backendservice.removeAction(action.getActionId());
            }
            for (const action of this.group.getActionsStepOff()) {
              await this.backendservice.removeAction(action.getActionId());
            }
            await this.backendservice.getUserCarpetConfigurationRefreshed(user);
            this.group.setActionsStepOn(this.saveActionStep(groups[i].getActionsStepOn(), "" + this.group.getgroupID()))
            this.group.setActionsStepOff(this.saveActionStep(groups[i].getActionsStepOff(), "" + this.group.getgroupID()))
            for (const action of this.group.getActionsStepOn()) {
              await this.backendservice.addActionToGroup(action);
            }
            for (const action of this.group.getActionsStepOff()) {
              await this.backendservice.addActionToGroup(action);
            }
            await this.backendservice.getUserCarpetConfigurationRefreshed(user);
          }
        }
      }
    }
  }

  saveActionStep(actions: Array<SensorfloorAction>, groupId: string) {
    var newActions = new Array()
    actions.forEach(action => {
      var stepAction = {}
      stepAction["groupId"] = groupId;
      stepAction["actionName"] = action.getActionName()
      stepAction["pluginId"] = action.getPluginId()
      stepAction["activation"] = action.getActivation()
      stepAction["params"] = {}
      action.getParams().forEach(actionParam => {
        stepAction["params"][actionParam.term] = actionParam.value
      });
      newActions.push(stepAction);
    });
    return newActions;
  }
  cancelAction() {
    this.router.navigate(["/configure-select-action"]);
  }
  sleep(milliseconds) {
    return new Promise(resolve => setTimeout(resolve, milliseconds));
  }
}
