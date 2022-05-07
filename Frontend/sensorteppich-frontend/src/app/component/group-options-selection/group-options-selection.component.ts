import { Component, OnInit } from '@angular/core';
import { OpenAPEService } from 'src/app/service/open-ape.service';
import { ConfigureService } from 'src/app/service/configure.service';
import { Router } from '@angular/router';
import { StylerComponent } from '../styler/styler.component';

@Component({
  selector: 'app-group-options-selection',
  templateUrl: './group-options-selection.component.html',
  styleUrls: ['./group-options-selection.component.css']
})
export class GroupOptionsSelectionComponent implements OnInit {

  helptext = "You are in the process of creating a new group." +
    "Click on Enter group name and type in a name for the group, e.g. Kitchen." +
    "Click Configure to set which devices should be linked to the group." +
    "Click Copy configuration from other group to copy the exact settings" +
    "and devices from another group.";


  constructor(
    private openapeservice: OpenAPEService,
    private configureservice: ConfigureService,
    private router: Router,
    public styler: StylerComponent
  ) { }

  ngOnInit(): void {
  }

  addDevices() {
    this.router.navigate(["/devices-overview"]);
  }

  changeDevices() {
  }

  deleteTiles() {
    // TODO tiles have to get deleted even in the database
    // Delete functionality from tiles
    this.configureservice.deleteSelectedTilesFromGroup();
  }

  navigateBackToHome() {
    this.router.navigate(["/configure-sensorfloor"]);
  }

  styleMaster() {
    return this.styler.styleMaster();
  }
}
