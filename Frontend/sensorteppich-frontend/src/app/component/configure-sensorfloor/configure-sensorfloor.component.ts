import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute, ParamMap } from '@angular/router';
import { switchMap, filter } from 'rxjs/operators';
import { OpenAPEService } from 'src/app/service/open-ape.service';
import { DomSanitizer } from '@angular/platform-browser';
import { Tile } from "../../classes/Tile"
import { ConfigureService } from 'src/app/service/configure.service';
import { BackendService } from 'src/app/service/backend.service';
import { HelpdialogComponent } from '../helpdialog/helpdialog.component';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { StylerComponent } from '../styler/styler.component';
import { ConfirmationdialogComponent } from '../confirmationdialog/confirmationdialog.component';
import { DialogData } from "../../classes/DialogData";
import { SensorfloorAction } from 'src/app/classes/SensorfloorAction';
import { SensorfloorGroup } from 'src/app/classes/SensorfloorGroup';
import { Field } from 'src/app/classes/Field';

class EditPipeline {
  frontendgroupid: number;
  backendgroupid: number;
  groupactions: SensorfloorAction[];
  groupname: string;
  editoperations: EditOperation[];
}

class EditOperation {
  tile: Tile;
  operation: string;
}

@Component({
  selector: 'app-configure-sensorfloor',
  templateUrl: './configure-sensorfloor.component.html',
  styleUrls: ['./configure-sensorfloor.component.css']
})
export class ConfigureSensorfloorComponent implements OnInit {

  tiles: Tile[];
  selectedtiles: Tile[];
  disabledtiles: Tile[];
  groupedtiles: Tile[];
  editPipeline: EditPipeline;

  numbers0 = [0, 1, 2, 3, 4, 5, 6, 7];
  numbers1 = [8, 9, 10, 11, 12, 13, 14, 15];
  numbers2 = [16, 17, 18, 19, 20, 21, 22, 23];
  numbers3 = [24, 25, 26, 27, 28, 29, 30, 31];
  numbers4 = [32, 33, 34, 35, 36, 37, 38, 39];
  numbers5 = [40, 41, 42, 43, 44, 45, 46, 47];
  numbers6 = [48];

  isLoading = false;
  groupSelected = false;
  editModeActive = false;

  selectedTileGroupName: string;
  helptext = "Welcome to the help menu! This is the home screen. " +
    "From here you can configure your carpet according to your needs. " +
    "The grey tiles without numbers do not belong to any group yet and " +
    "are therefore not configured. Carpet tiles with the same colour and " +
    "the same letters already belong to a group. " +
    "To create a new group, click on a grey tile to choose your desired tiles. " +
    "To edit an existing group, click on a tile of the corresponding group."; // TODO fill with text of Leonie

  helptext2 = "You are in the process of creating a new group. Select any number of free (grey) tiles and click 'Create new Group' to create a new group with these tiles. Click 'Cancel' to return to the home screen."
  helptext3 = "You are currently on the home screen and have selected an existing group. " +
    "Click 'Configure' to configure the selected group. " +
    "Click 'Edit Tiles' to add new tiles to the group or delete existing tiles. " +
    "Click 'Delete group' to permanently delete the whole group. " +
    "At the bottom of the screen you can see which devices are currently associated with the group.";
  helptext4 = "You are in the process of adapting the tiles of an existing group. " +
    "Click on a free (grey) tile to add it to the group. " +
    "Click on a group tile (colored and letter) to remove it from the group. " +
    "Once you are satisfied, click 'Save' to save your settings.";

  groupcounter = 0;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private configureService: ConfigureService,
    private backendservice: BackendService,
    private sanitizer: DomSanitizer,
    public openapeservice: OpenAPEService,
    public styler: StylerComponent,
    public helpdialog: MatDialog,
    public confirmationdialog: MatDialog,
    public dialogref: MatDialogRef<HelpdialogComponent | ConfirmationdialogComponent>
  ) { }

  ngOnInit(): void {
    // Start spinner
    this.isLoading = true;

    // Check if the user is logged in and preferences are already loaded
    if (this.openapeservice.getLoggedInUser() != "") {
      this.initializeUserPreferences(); // load all openAPE preferences
    }

    this.initializeTilesArrays();

    // Stop spinner
    this.isLoading = false;
  }
  public getTiles() {
    return this.tiles;
  }

  async initializeUserPreferences() {
    if (!this.openapeservice.getPreferencesLoaded()) {
      await this.openapeservice.initializeUserPreferences();
      this.openapeservice.preferencesApplied = true;
    }
  }

  startHelpDialog() {
    if (this.selectedtiles.length != 0 && this.groupSelected != true) {
      this.dialogref = this.helpdialog.open(HelpdialogComponent, {
        width: '360px',
        data: { text: this.helptext2 }
      });
      this.dialogref.afterClosed().subscribe(result => {
      });
    } else if (this.selectedtiles.length != 0 && this.groupSelected == true && !this.editModeActive) {
      this.dialogref = this.helpdialog.open(HelpdialogComponent, {
        width: '360px',
        data: { text: this.helptext3 }
      });
      this.dialogref.afterClosed().subscribe(result => {
      });
    } else if (this.editModeActive) {
      this.dialogref = this.helpdialog.open(HelpdialogComponent, {
        width: '360px',
        data: { text: this.helptext4 }
      });
      this.dialogref.afterClosed().subscribe(result => {
      });
    } else {
      this.dialogref = this.helpdialog.open(HelpdialogComponent, {
        width: '360px',
        data: { text: this.helptext }
      });
      this.dialogref.afterClosed().subscribe(result => {
      });
    }
  }

  startConfirmationDialog(text, additional_info, mode) {
    let selectedtiles = this.selectedtiles;

    this.dialogref = this.confirmationdialog.open(ConfirmationdialogComponent, {
      width: '250px',
      data: { text: text, confirmed: false, additional_info_view: additional_info, additional_info_return: "", mode: mode }
    });

    this.dialogref.afterClosed().subscribe(result => {
      let jsonresult = JSON.parse(JSON.stringify(result));
      if (jsonresult["mode"] === "create" && jsonresult["confirmed"]) {
        this.createNewGroupObserver(jsonresult)
      }
      else if (jsonresult["mode"] === "delete" && jsonresult["confirmed"]) {
        this.deleteGroup(selectedtiles);
      }
    })
  }

  async createNewGroupObserver(result) {
    if (result["confirmed"]) {
      let groupname = result["additional_info_return"];
      let sensorfloorgroup = this.buildSensorfloorGroup(groupname);
      let backendgroupid = await this.backendservice.createNewGroup(sensorfloorgroup); // Returns a backend groupid

      // Set backendid for selected tiles
      for (let i = 0; i < this.selectedtiles.length; i++) {
        let originalTile = this.tiles.find(tile => tile.getId() == this.selectedtiles[i].getId());
        this.groupTile(originalTile, this.groupcounter, sensorfloorgroup["groupName"], new Array<SensorfloorAction>(), backendgroupid);
        this.groupTile(this.selectedtiles[i], this.groupcounter, sensorfloorgroup["groupName"], new Array<SensorfloorAction>(), backendgroupid);
      }

      // IMPORTANT, groupcounter is higher now, for new groups
      this.groupcounter++;

      // Set selected tiles in configure sevrie for further process
      // TODO lieber eine Funktion draus machen -> saveSelectedGroupInService()
      this.configureService.setSelectedTiles(this.selectedtiles);
      this.configureService.setChosenGroupName(this.configureService.getSelectedTiles()[0].getGroupname());
      this.configureService.setChosenGroupId(backendgroupid);
      this.configureService.setOriginalTiles(this.tiles);

      // Add group to userConfiguration
      let userconfiguration = this.backendservice.getUserCarpetConfiguration();
      let sensorfloorgroupFrontend = this.buildSensorFloorGroupFrontend(backendgroupid, sensorfloorgroup["groupName"], sensorfloorgroup);
      userconfiguration.getCarpetGroups().push(sensorfloorgroupFrontend);
      this.configureService.setChosenCarpetGroup(sensorfloorgroupFrontend);
      this.router.navigate(["/configure-select-action"]);
    }
  }

  buildSensorfloorGroup(groupname) {
    let sensorfloorgroup = {};
    sensorfloorgroup["groupName"] = groupname;
    sensorfloorgroup["fields"] = []
    sensorfloorgroup["actionsStepOn"] = []
    sensorfloorgroup["actionsStepOff"] = []

    for (let i = 0; i < this.selectedtiles.length; i++) {
      let sensorfloorfield = {}
      sensorfloorfield["x"] = this.selectedtiles[i].getX();
      sensorfloorfield["y"] = this.selectedtiles[i].getY();
      sensorfloorgroup["fields"].push(sensorfloorfield);
    }
    return sensorfloorgroup;
  }

  buildSensorFloorGroupFrontend(groupid, groupname, sensorfloorgroup) {
    let sensorfloorgroupFrontend = new SensorfloorGroup();
    sensorfloorgroupFrontend.setgroupID(groupid);
    sensorfloorgroupFrontend.setGroupName(groupname);
    for (let i = 0; i < sensorfloorgroup["fields"].length; i++) {
      let field = new Field();
      field.setX(sensorfloorgroup["fields"][i]["x"]);
      field.setY(sensorfloorgroup["fields"][i]["y"]);
      sensorfloorgroupFrontend.getFields().push(field);
    }
    return sensorfloorgroupFrontend;
  }

  /**
   * Initializes all tiles with the given user configuration
   */
  async initializeTilesArrays() {
    this.tiles = new Array<Tile>();
    this.selectedtiles = new Array<Tile>();
    let x_coordinate = 1;
    let y_coordinate = 1;

    for (let i = 0; i < 49; i++) {
      let tile = new Tile(i, x_coordinate, y_coordinate, -1);
      y_coordinate++;
      if (y_coordinate == 9) {
        x_coordinate++;
        y_coordinate = 1;
      }
      this.tiles.push(tile);
    }

    let userconfig = await this.backendservice.getUserCarpetConfigurationRefreshed(this.openapeservice.getLoggedInUser());
    if (userconfig.getCarpetGroups().length > 0) {
      let userconfiggroups = userconfig.getCarpetGroups();
      for (let i = 0; i < userconfiggroups.length; i++) {
        this.groupcounter++;
        let groupid = userconfiggroups[i].getgroupID();
        let groupname = userconfiggroups[i].getGroupName();
        let fields = userconfiggroups[i].getFields();
        for (let j = 0; j < fields.length; j++) {
          let tile = this.tiles.find(tile => tile.getX() == fields[j].getX() && tile.getY() == fields[j].getY());
          this.groupTile(tile, this.groupcounter, groupname, userconfiggroups[i].getActionsStepOn().concat(userconfiggroups[i].getActionsStepOff()), groupid);
        }
      }
    }
    this.selectedtiles = new Array<Tile>();
    this.disabledtiles = new Array<Tile>();
    this.groupedtiles = new Array<Tile>();
    this.selectedTileGroupName = "";
  }

  changeUser() {
    this.isLoading = false;
    this.openapeservice.logoutUser();
    this.router.navigate(["/"]);
  }

  /**
   * If an user decides to select an existing group and wants to configure it
   */
  configureGroup() {

    // TODO Funktionsaufruf -> saveSelectedGroupInService()
    this.configureService.setSelectedTiles(this.selectedtiles);
    this.configureService.setChosenGroupName(this.configureService.getSelectedTiles()[0].getGroupname());
    this.configureService.setChosenGroupId(this.configureService.getSelectedTiles()[0].getBackendGroupid());
    this.configureService.setOriginalTiles(this.tiles);

    let sensorfloorgroup = this.buildSensorfloorGroup(this.configureService.getSelectedTiles()[0].getGroupname());
    let sensorfloorgroupFrontend = this.buildSensorFloorGroupFrontend(this.configureService.getSelectedTiles()[0].getBackendGroupid(),
      this.configureService.getSelectedTiles()[0].getGroupname(), sensorfloorgroup);

    let actions = this.configureService.getSelectedTiles()[0].getActions();

    let stepOnActions = new Array<SensorfloorAction>();
    let stepOffActions = new Array<SensorfloorAction>();

    for (let i = 0; i < actions.length; i++) {
      if (actions[i].getActivation() == "stepOn") {
        stepOnActions.push(actions[i]);
      }
      else {
        stepOffActions.push(actions[i]);
      }
    }
    sensorfloorgroupFrontend.setActionsStepOn(stepOnActions);
    sensorfloorgroupFrontend.setActionsStepOff(stepOffActions);
    this.configureService.setChosenCarpetGroup(sensorfloorgroupFrontend);
    this.router.navigate(["/configure-select-action"]);
  }

  /**
   * Gets called if a user interacts with the tiles in edit mode
   */
  editTiles() {
    if (!this.editModeActive) {
      this.editModeActive = true;
      this.editPipeline = new EditPipeline();
      this.editPipeline.editoperations = new Array<EditOperation>();
      this.editPipeline.groupactions = this.selectedtiles[0].getActions();
      this.editPipeline.frontendgroupid = this.selectedtiles[0].getFrontendGroupid();
      this.editPipeline.groupname = this.selectedtiles[0].getGroupname();
      this.editPipeline.backendgroupid = this.selectedtiles[0].getBackendGroupid();
      for (let i = 0; i < this.tiles.length; i++) {
        if (!this.tiles[i].getGrouped() && this.tiles[i].getFrontendGroupid() != this.editPipeline.frontendgroupid) {
          this.enableTile(this.tiles[i]);
        }
      }
    }
    else {
      this.editModeActive = false;
      for (let i = 0; i < this.tiles.length; i++) {
        if (!this.tiles[i].getGrouped() && this.tiles[i].getFrontendGroupid() != this.editPipeline.frontendgroupid) {
          this.disableTile(this.tiles[i]);
        }
      }
    }
  }

  helpButtonClicked() {
    this.startHelpDialog();
  }

  createNewGroup() {
    this.startConfirmationDialog("You're in the process of creating a new group, please name your new group", "create_group", "create");
  }

  startDeleteConfirmationDialog() {
    this.startConfirmationDialog("You're about to delete the group " + this.selectedtiles[0].getGroupname() + " are you sure?", "delete_group", "delete");
  }

  async deleteGroup(selectedtiles: Tile[]) {
    let frontendgroupid = selectedtiles[0].getFrontendGroupid();
    let groupname = selectedtiles[0].getGroupname();
    let backendgroupid = selectedtiles[0].getBackendGroupid();
    let user = this.openapeservice.getLoggedInUser();

    // Ungroup and unselect all selected tiles
    for (let i = 0; i < selectedtiles.length; i++) {
      this.ungroupTile(selectedtiles[i]);
      this.unselectTile(selectedtiles[i]);
    }

    //Delete group in user configuration
    this.backendservice.getUserCarpetConfiguration().setCarpetGroups(this.backendservice.getUserCarpetConfiguration().getCarpetGroups().filter(group => group.getgroupID() != backendgroupid));

    // Save the changes
    this.saveEditing();
    await this.backendservice.deleteGroup(backendgroupid);
    await this.backendservice.getUserCarpetConfigurationRefreshed(user);
  }

  saveEditing() {

    if (this.editPipeline != null && this.editPipeline.editoperations.length > 0) {
      let groupid = this.editPipeline.backendgroupid;
      let newgrouptiles = this.getAllTilesOfAGroup(groupid);
      let fields = [];
      for (let i = 0; i < newgrouptiles.length; i++) {
        let fieldobject = new Field();
        fieldobject.setX(newgrouptiles[i].getX());
        fieldobject.setY(newgrouptiles[i].getY());
        fields.push(fieldobject);
      }
      let removetiles = [];
      for (let i = 0; i < this.editPipeline.editoperations.length; i++) {
        if (this.editPipeline.editoperations[i].operation == "remove") {
          this.ungroupTile(this.editPipeline.editoperations[i].tile);
          let fieldobject = new Field();
          fieldobject.setX(this.editPipeline.editoperations[i].tile.getX());
          fieldobject.setY(this.editPipeline.editoperations[i].tile.getY());
          removetiles.push(fieldobject);
        }
        else {
          this.groupTile(this.editPipeline.editoperations[i].tile, this.editPipeline.frontendgroupid, this.editPipeline.groupname, this.editPipeline.groupactions, this.editPipeline.backendgroupid); // TODO Add also the objects to it
          let fieldobject = new Field();
          fieldobject.setX(this.editPipeline.editoperations[i].tile.getX());
          fieldobject.setY(this.editPipeline.editoperations[i].tile.getY());
          fields.push(fieldobject);
        }
      }
      let newfields = [];
      for (let i = 0; i < fields.length; i++) {
        let yes = false;
        for (let j = 0; j < removetiles.length; j++) {
          if (fields[i].getX() == removetiles[j].getX() && fields[i].getY() == removetiles[j].getY()) {
            yes = true;
          }
        }
        if (!yes) {
          newfields.push(fields[i]);
        }
      }

      // Build new group object for rest call
      let newgroupObject = {};
      newgroupObject["groupId"] = groupid;
      newgroupObject["fields"] = newfields;
      this.changeGroupTiles(newgroupObject);
    }

    this.editModeActive = false;
    this.groupSelected = false;
    this.selectedTileGroupName = "";

    if (this.editPipeline != undefined) {
    }

    for (let i = 0; i < this.tiles.length; i++) {
      if (this.tiles[i].getSelected()) {
        this.unselectTile(this.tiles[i]);
      }
      else {
        this.enableTile(this.tiles[i]);
      }
    }
  }

  abortEditing() {
    this.editModeActive = false;
    this.groupSelected = false;

    for (let i = 0; i < this.editPipeline.editoperations.length; i++) {
      if (this.editPipeline.editoperations[i].operation == "add") {
        let tile = this.tiles.find(tile => tile.getId() == this.editPipeline.editoperations[i].tile.getId());
        this.ungroupTile(tile);
      }
      else // remove
      {
        let tile = this.tiles.find(tile => tile.getId() == this.editPipeline.editoperations[i].tile.getId());
        this.groupTile(tile, this.editPipeline.frontendgroupid, this.editPipeline.groupname, this.editPipeline.groupactions, this.editPipeline.backendgroupid);
      }
    }

    for (let i = 0; i < this.tiles.length; i++) {
      if (this.tiles[i].getSelected()) {
        this.unselectTile(this.tiles[i]);
      }
      if (this.tiles[i].getDisabled()) {
        this.enableTile(this.tiles[i]);
      }
    }
  }

  getAllTilesOfAGroup(id: number) {
    let selectedtiles = new Array<Tile>();
    for (let i = 0; i < this.tiles.length; i++) {
      if (this.tiles[i].getBackendGroupid() == id) {
        selectedtiles.push(this.tiles[i]);
      }
    }
    return selectedtiles;
  }

  async changeGroupTiles(newgroup) {
    let groupid = newgroup["groupId"];
    let fields = newgroup["fields"];
    let carpetconfiguration = this.backendservice.getUserCarpetConfiguration();
    let new_configuration_fields = []
    for (let i = 0; i < this.tiles.length; i++) {
      for (let k = 0; k < fields.length; k++) {
        if (this.tiles[i].getX() == fields[k].getX() && this.tiles[i].getY() == fields[k].getY()) {
          // Field is a new user config field
          new_configuration_fields.push(this.tiles[i]);
          break;
        }
      }
    }

    // Take the new fields and set them to the corresponding carpet configuration group
    for (let i = 0; i < carpetconfiguration.getCarpetGroups().length; i++) {
      if (carpetconfiguration.getCarpetGroups()[i].getgroupID() == groupid) {
        carpetconfiguration.getCarpetGroups()[i].setFields(new_configuration_fields);
      }
    }

    // Change the configuration in backend
    await this.backendservice.changeGroup(newgroup);
  }

  /**
   * Manages the click event for tiles
   * @param id id of the selected tile
   */
  tileClicked(id: number) {
    let chosentile = this.tiles.find(tile => tile.getId() == id);
    let groupid = chosentile.getBackendGroupid();

    // Check grouped ###############################
    if (chosentile.getGrouped()) // Is in a group of tiles
    {
      if (this.editModeActive && this.groupSelected) // Edit mode active and group is selected
      {
        // Check if its the last tile of this group
        if (this.selectedtiles.length == 1) {
          this.startDeleteConfirmationDialog();
        }
        else {
          this.unselectTile(chosentile);
          this.ungroupTile(chosentile);

          let editoperation = new EditOperation();
          editoperation.operation = "remove";
          editoperation.tile = chosentile;

          // Lösche alle Operationen zu diesem Objekt die schon drinnen sind
          this.editPipeline.editoperations = this.editPipeline.editoperations.filter(operation => operation.tile.getId() != chosentile.getId());
          this.editPipeline.editoperations.push(editoperation);
        }
      }
      else {
        // Set group name for UI
        if (!chosentile.getSelected()) {
          this.selectedTileGroupName = chosentile.getGroupname();
          this.groupSelected = true;
          this.selectTile(chosentile);
          for (let i = 0; i < this.tiles.length; i++) {
            if (this.tiles[i].getBackendGroupid() != groupid) {
              this.disableTile(this.tiles[i]); // Disable all tiles not in the same group
            }
            else {
              this.selectTile(this.tiles[i]);
            }
          }
        }
        else {
          this.unselectTile(chosentile);
          for (let i = 0; i < this.tiles.length; i++) {
            if (this.tiles[i].getBackendGroupid() == chosentile.getBackendGroupid()) {
              this.unselectTile(this.tiles[i]);
            }
          }
          if (this.selectedtiles.length == 0) {
            for (let i = 0; i < this.tiles.length; i++) {
              if (this.tiles[i].getDisabled()) {
                this.enableTile(this.tiles[i]);
              }
            }
            this.groupSelected = false;
            // Unset group name for UI
            this.selectedTileGroupName = "";
          }
        }
      }
    }
    else // Tile is not in a group
    {
      if (this.editModeActive && this.groupSelected) { // Edit mode is active cos it got clicked and group is selected

        // Selected tile is not in the group
        if (!chosentile.getSelected()) {
          this.selectTile(chosentile);
          let editoperation = new EditOperation();
          editoperation.operation = "add";
          editoperation.tile = chosentile;
          this.editPipeline.editoperations = this.editPipeline.editoperations.filter(operation => operation.tile.getId() != chosentile.getId());
          this.editPipeline.editoperations.push(editoperation);
        }
        else {
          this.unselectTile(chosentile);
          let editoperation = new EditOperation();
          editoperation.operation = "remove";
          editoperation.tile = chosentile;
          this.editPipeline.editoperations = this.editPipeline.editoperations.filter(operation => operation.tile.getId() != chosentile.getId());
          this.editPipeline.editoperations.push(editoperation);
        }
      }
      else {
        // Unset group name for UI
        this.selectedTileGroupName = "";
        if (!chosentile.getSelected()) {
          this.selectTile(chosentile);
          for (let i = 0; i < this.tiles.length; i++) {
            if (this.tiles[i].getGrouped()) {
              this.disableTile(this.tiles[i]);
            }
          }
        }
        else {
          this.unselectTile(chosentile);
        }
        if (this.selectedtiles.length == 0) {
          for (let i = 0; i < this.tiles.length; i++) {
            if (this.tiles[i].getGrouped()) {
              this.enableTile(this.tiles[i]);
            }
          }
        }
      }
    }
  }

  disableTile(tile: Tile) {
    tile.setDisabled(true);
    if (!this.disabledtiles.find(tileinarray => tileinarray.getId() == tile.getId())) {
      this.disabledtiles.push(tile);
    }
  }

  enableTile(chosentile: Tile) {
    chosentile.setDisabled(false);
    if (this.disabledtiles.find(tileinarray => tileinarray.getId() == chosentile.getId())) {
      if (this.disabledtiles.length == 1) {
        this.disabledtiles.pop();
      }
      else {
        this.disabledtiles = this.disabledtiles.filter(tile => tile.getId() != chosentile.getId());
      }
    }
  }

  selectTile(chosentile: Tile) {
    chosentile.setSelected(true);
    if (!this.selectedtiles.find(tile => tile.getId() == chosentile.getId())) {
      this.selectedtiles.push(chosentile);
    }
  }

  unselectTile(chosentile: Tile) {
    chosentile.setSelected(false);
    if (this.selectedtiles.find(tileinarray => tileinarray.getId() == chosentile.getId())) {
      if (this.selectedtiles.length == 1) {
        this.selectedtiles.pop()
      }
      else {
        this.selectedtiles = this.selectedtiles.filter(tile => tile.getId() != chosentile.getId());
      }
    }
  }

  groupTile(chosentile: Tile, frontendgroupid: number, groupname: string, groupactions, backendgroupid) {
    chosentile.setGrouped(true);
    chosentile.setFrontendGroupid(frontendgroupid);
    chosentile.setBackendGroupid(backendgroupid);
    chosentile.setGroupname(groupname);

    if (/\s/.test(groupname)) {
      let splittedgroupname = groupname.split(" ");
      chosentile.setGroupNameShort(splittedgroupname[0][0].toUpperCase() + splittedgroupname[1][0].toUpperCase());
    }
    else {
      chosentile.setGroupNameShort(groupname.slice(0, 2).toUpperCase());
    }
    chosentile.setActions(groupactions);
  }
  ungroupTile(chosentile: Tile) {
    chosentile.setGrouped(false);
    chosentile.setFrontendGroupid(-1);
    chosentile.setBackendGroupid(-1);
    chosentile.setGroupname("");
    chosentile.setGroupNameShort("");
    chosentile.setActions(new Array<SensorfloorAction>());
  }

  // Styling tiles if attributes change
  tileStyle(tile: Tile, frontendgroupid: number) {
    if (tile.getDisabled() && tile.getGrouped()) {
      return "tile-disabled";
    }
    else if (tile.getSelected() && tile.getGrouped()) {
      return "tile-selected";
    }
    else if (tile.getSelected()) {
      return "tile-selected";
    }
    else if (tile.getDisabled()) {
      return "tile-disabled";
    }
    else if (tile.getGrouped()) {
      return "tiles-group-" + frontendgroupid;
    }
    else {
      return "tile-default";
    }

    // sanitize the style expression
    // return this.sanitizer.bypassSecurityTrustStyle(style);
  }

  styleMaster() {
    return this.styler.styleMaster();
  }

  navigateToOpenAPESettings() {
    this.router.navigate(["/settings"]);
  }
}
