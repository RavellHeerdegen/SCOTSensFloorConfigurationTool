import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { ConfigureService } from 'src/app/service/configure.service';
import { BackendService } from 'src/app/service/backend.service';
import { DomSanitizer } from '@angular/platform-browser';
import { OpenAPEService } from 'src/app/service/open-ape.service';
import { StylerComponent } from '../styler/styler.component';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { HelpdialogComponent } from '../helpdialog/helpdialog.component';
import { ConfirmationdialogComponent } from '../confirmationdialog/confirmationdialog.component';

@Component({
  selector: 'app-settings',
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.css']
})
export class SettingsComponent implements OnInit {

  style = {};
  fontsize: 'x-small' | 'small' | "medium" | "large" | "x-large" | string = 'medium';
  fontsizeInInt = 0;
  legacy_fontsize: 'x-small' | 'small' | "medium" | "large" | "x-large" | string = 'medium';
  legacy_activateNightmode = false;
  legacy_helpui = false;

  activateNightmode = false;
  activateHelpUI = false;

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
    public dialogref: MatDialogRef<HelpdialogComponent | ConfirmationdialogComponent>) { }

  ngOnInit(): void {
    this.style = this.openapeservice.style;
    this.fontsize = this.style["font-size"];
    this.activateNightmode = this.style["sh2020SensFloor_nightmode"];
    this.activateHelpUI = this.style["sh2020SensFloor_helpUI"];
    this.nightModeSliderClicked(this.activateNightmode);
    this.helpUISliderClicked(this.activateHelpUI);

    this.legacy_fontsize = this.style["font-size"];
    this.legacy_activateNightmode = this.style["sh2020SensFloor_nightmode"];
    this.legacy_helpui = this.style["sh2020SensFloor_helpUI"];

    this.parseFontsizeStringToFontsizeInt(this.fontsize);
  }

  saveOpenAPEChanges() {
    // TODO call an OpenAPE um Profil zu aktualisieren
    if (this.openapeservice.useOpenAPE) {
      // Call to openape
      let userpreferences = {};

      userpreferences["default"] = {};
      userpreferences["default"]["name"] = this.openapeservice.getLoggedInUser();
      userpreferences["default"]["preferences"] = {};
      userpreferences["default"]["preferences"]["https://terms.gpii.eu/api/record/sh2020SensFloor_nightmode"] = this.activateNightmode;
      userpreferences["default"]["preferences"]["https://terms.gpii.eu/api/record/sh2020SensFloor_helpUI"] = this.activateHelpUI;
      userpreferences["default"]["preferences"]["http://terms.gpii.eu/fontSize"] = this.fontsizeInInt;

      this.openapeservice.updateUserContext(userpreferences);
    }

    // Nach dem Speichern in OpenAPE
    this.router.navigate(["/configure-sensorfloor"]);
  }

  cancelChanges() {
    this.openapeservice.changeProperty("font-size", this.legacy_fontsize);
    this.openapeservice.changeProperty("sh2020SensFloor_nightmode", this.legacy_activateNightmode);
    this.openapeservice.changeProperty("sh2020SensFloor_helpUI", this.legacy_helpui);

    this.router.navigate(["/configure-sensorfloor"]);
  }

  styleMaster() {
    return this.styler.styleMaster();
  }

  fontSizeClicked(newvalue: string) {
    this.parseFontsizeStringToFontsizeInt(newvalue);
    this.fontsize = newvalue;
    this.openapeservice.changeProperty("font-size", this.fontsize);
  }

  parseFontsizeStringToFontsizeInt(newvalue: string) {
    if (newvalue == "x-small") {
      this.fontsizeInInt = 12;
    }
    else if (newvalue == "small") {
      this.fontsizeInInt = 14;
    }
    else if (newvalue == "medium") {
      this.fontsizeInInt = 16;
    }
    else if (newvalue == "large") {
      this.fontsizeInInt = 20;
    }
    else if (newvalue == "larger") {
      this.fontsizeInInt = 23;
    }
    else if (newvalue == "x-large") {
      this.fontsizeInInt = 26;
    }
  }

  nightModeSliderClicked(newstate: boolean) {
    this.activateNightmode = newstate;
    this.openapeservice.changeProperty("sh2020SensFloor_nightmode", this.activateNightmode);
  }

  helpUISliderClicked(newstate: boolean)
  {
    this.activateHelpUI = newstate;

    this.openapeservice.changeProperty("sh2020SensFloor_helpUI", this.activateHelpUI);
  }
}
