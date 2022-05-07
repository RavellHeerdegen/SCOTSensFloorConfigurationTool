import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute, ParamMap } from '@angular/router';
import { OpenAPEService } from 'src/app/service/open-ape.service';
import { StylerComponent } from '../styler/styler.component';
import { BackendService } from 'src/app/service/backend.service';
import { ConfigureService } from 'src/app/service/configure.service';
import { HelpdialogComponent } from '../helpdialog/helpdialog.component';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-configure-select-action',
  templateUrl: './configure-select-action.component.html',
  styleUrls: ['./configure-select-action.component.css']
})
export class ConfigureSelectActionComponent implements OnInit {

  groupname: string;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private openapeservice: OpenAPEService,
    public backendservice: BackendService,
    public configureservice: ConfigureService,
    public styler: StylerComponent,
    public helpdialog: MatDialog,
    public dialogref: MatDialogRef<HelpdialogComponent>) { }

  helptext = "You are in the process of creating a new group. " +
    "Click 'Change configuration' to set which devices should be linked to the group. " +
    "Click 'Overwrite configuration' to copy the exact settings and devices from another group."

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

  changeConfiguration() {
    this.configureservice.setOnEntry(true);
    this.router.navigate(["/change-configuration"]);
  }

  overwriteConfiguration() {
    this.router.navigate(["/overwrite-configuration"]);
  }

  cancelAction() {
    this.router.navigate(["/configure-sensorfloor"]);
  }

  styleMaster() {
    return this.styler.styleMaster();
  }
}
