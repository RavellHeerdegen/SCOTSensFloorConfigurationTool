import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { OpenAPEService } from 'src/app/service/open-ape.service';
import { StylerComponent } from '../styler/styler.component';
import { BackendService } from 'src/app/service/backend.service';
import { ConfigureService } from 'src/app/service/configure.service';
import { PluginObject } from 'src/app/classes/PluginObject';
import { HelpdialogComponent } from '../helpdialog/helpdialog.component';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

class SensorfloorObjectStringMatcher {
  public constructor() { }

  backend_name: string;
  frontend_name: string;
}

@Component({
  selector: 'app-select-object',
  templateUrl: './select-object.component.html',
  styleUrls: ['./select-object.component.css']
})
export class SelectObjectComponent implements OnInit {

  groupname: string;
  plugin: string;
  plugin_objects: PluginObject[];
  pluginFrontendName: string;
  selectedObject = ""; // Frontendname of an object
  objects: SensorfloorObjectStringMatcher[];

  constructor(private router: Router,
    private openapeservice: OpenAPEService,
    public backendservice: BackendService,
    public configureservice: ConfigureService,
    public styler: StylerComponent,
    public helpdialog: MatDialog,
    public dialogref: MatDialogRef<HelpdialogComponent>) { }

  helptext = "You are in the process of configuring your newly created group. " +
    "Click on the device you want to configure."

  ngOnInit(): void {
    this.groupname = this.configureservice.getChosenGroupName();
    this.plugin = this.configureservice.getChosenPlugin();
    this.pluginFrontendName = this.configureservice.getChosenPluginAsFrontendName();
    this.loadObjects();
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

  loadObjects() {
    this.objects = new Array<SensorfloorObjectStringMatcher>();
    this.plugin_objects = new Array<PluginObject>();
    this.plugin_objects = this.backendservice.getAvailableObjects().find(plugin => plugin.getPluginId() === this.plugin).getObjects();
    for (let i = 0; i < this.plugin_objects.length; i++) {

      // Build string matcher object for frontend visualization
      let sensorfloorobjectstringmatcherObject = new SensorfloorObjectStringMatcher();
      sensorfloorobjectstringmatcherObject.backend_name = this.plugin_objects[i].getName();
      if (sensorfloorobjectstringmatcherObject.backend_name.includes("_")) {
        let splitted_name = sensorfloorobjectstringmatcherObject.backend_name.split("_");
        sensorfloorobjectstringmatcherObject.frontend_name = splitted_name[0].charAt(0).toUpperCase() + splitted_name[0].slice(1) + " " + splitted_name[1].charAt(0).toUpperCase() + splitted_name[1].slice(1);
      }
      else {
        sensorfloorobjectstringmatcherObject.frontend_name = sensorfloorobjectstringmatcherObject.backend_name.toUpperCase();
      }
      this.objects.push(sensorfloorobjectstringmatcherObject);
    }
  }

  selectObject(name: string) {
    this.selectedObject = name; // Frontend name of an object
    let selectedbackendname = this.objects.find(object => object.frontend_name == this.selectedObject).backend_name;
    this.configureservice.setChosenObjectName(selectedbackendname);
    this.configureservice.setChosenObjectAsFrontendName(this.selectedObject);
    let selectedObjectId = this.plugin_objects.find(object => object.getName() == selectedbackendname).getId();
    this.configureservice.setChosenObjectId(selectedObjectId);
    this.navigateToConfigure();
  }

  navigateToConfigure() {
    this.router.navigate(["/configure-object"])
  }

  cancelAction() {
    this.router.navigate(["/select-type"]);
  }

  navigateBackToHome() {
    this.router.navigate(["/configure-sensorfloor"]);
  }
}
