import { Component, OnInit } from '@angular/core';
import { BackendService } from 'src/app/service/backend.service';
import { StylerComponent } from '../styler/styler.component';
import { ConfigureService } from 'src/app/service/configure.service';
import { Router } from '@angular/router';
import { HelpdialogComponent } from '../helpdialog/helpdialog.component';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-select-type',
  templateUrl: './select-type.component.html',
  styleUrls: ['./select-type.component.css']
})
export class SelectTypeComponent implements OnInit {

  groupname: string;

  constructor(
    public configureservice: ConfigureService,
    public backendservice: BackendService,
    private router: Router,
    public styler: StylerComponent,
    public helpdialog: MatDialog,
    public dialogref: MatDialogRef<HelpdialogComponent>
  ) { }

  helptext = "You are in the process of configuring your newly created group. " +
    "Click on the device type you want to associate with the group. Afterwards " +
    "you can configure the exact device and for example set the color of your lamp."

  ngOnInit(): void {
    this.groupname = this.configureservice.getChosenGroupName();
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

  selectObject(pluginname: string) {
    this.configureservice.setChosenPlugin(pluginname);
    this.router.navigate(["/select-object"]);
  }

  cancelAction() {
    this.router.navigate(["/change-configuration"]);
  }
}
