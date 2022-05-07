import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest, HttpResponse, HttpHeaders, HttpClientModule, HttpParams } from '@angular/common/http';
// import { writeFile } from "fs";

class UserPreferences {
  name: string;
  preferences: UserPreference[];

  constructor() {
    this.preferences = Array<UserPreference>();
  }

  addUserPreference(preference: UserPreference) {
    this.preferences.push(preference);
  }

  setName(name: string) {
    this.name = name;
  }

  setPreferences(preferences: UserPreference[]) {
    this.preferences = preferences;
  }

  getName() {
    return this.name;
  }

  getPreferences() {
    return this.preferences;
  }
}

class UserPreference {
  term: string;
  value: string | number | boolean;
}


@Injectable({
  providedIn: 'root'
})
export class OpenAPEService {

  private currentUserAccesstoken: string;
  private loggedInUser: string;
  private contextId: string;
  private userpreferences: UserPreferences;
  private userPreferencesAsJSONString: string;
  private accesstokenSet = false;
  private preferencesLoaded = false;
  preferencesApplied = false;
  useOpenAPE = true;
  helpButtonActive = false;
  highContrastEnabled = false;

  style = {};

  constructor(private http: HttpClient) { }

  public loginUser(name: string, contextid: string) {
    this.loggedInUser = name;
    this.contextId = contextid;
  }

  public logoutUser() {
    this.loggedInUser = "";
    this.contextId = "";
    this.helpButtonActive = false;
    this.highContrastEnabled = false;
    this.accesstokenSet = false;
    this.preferencesLoaded = false;
    this.preferencesApplied = false;
  }

  public setPreferencesLoaded(status: boolean) {
    this.preferencesLoaded = status;
  }

  public getPreferencesLoaded() {
    return this.preferencesLoaded;
  }

  public getUserPreferences() {
    return this.userpreferences.preferences;
  }

  public getUserAccesstoken() {
    return this.currentUserAccesstoken;
  }

  public getContextId() {
    return this.contextId;
  }

  public getLoggedInUser() {
    return this.loggedInUser;
  }

  async initializeUserPreferences() {
    let name = this.getLoggedInUser();
    if (this.useOpenAPE) {
      await this.getUserAccesstokenREST(name, name);
    }
    await this.getUserPreferencesREST(name, name, this.getContextId());
    this.applyOpenAPEPreferencesToStyle();
  }

  async getUserAccesstokenREST(name: string, password: string) {
    const body = new HttpParams()
      .set('username', name)
      .set('password', password)
      .set('grant_type', 'password');
    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/x-www-form-urlencoded',
        "grant_type": "password",
        "username": name,
        "password": password
      })
    };

    await this.http.post("https://openape.gpii.eu/token", body, httpOptions).toPromise().then(data => {
      this.currentUserAccesstoken = data["access_token"];
      this.accesstokenSet = true;
    });
  }

  async updateUserContext(data) {
    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        "Authorization": this.getUserAccesstoken()
      })
    };

    await this.http.put("https://openape.gpii.eu/api/user-contexts/" + this.contextId, data, httpOptions).toPromise().then(data => {
    });
  }

  async getUserPreferencesREST(name: string, password: string, contextid: string) {
    if (this.getUserAccesstoken() != "" && this.getUserAccesstoken() != null) {
      const httpOptions = {
        headers: new HttpHeaders({
          'Content-Type': 'application/json',
          "Authorization": this.getUserAccesstoken()
        })
      };
      await this.http.get("https://openape.gpii.eu/api/user-contexts/" + contextid, httpOptions).toPromise().then(data => {
        this.userpreferences = new UserPreferences();

        let dataAsJSONObject = JSON.parse(JSON.stringify(data));
        this.userpreferences.setName(dataAsJSONObject.default.name);
        let preferenceKeys = Object.keys(dataAsJSONObject.default.preferences);
        for (let i = 0; i < preferenceKeys.length; i++) {
          let preference = new UserPreference();
          if (preferenceKeys[i].includes("sh2020SensFloor")) {
            preference.term = preferenceKeys[i].split("/")[5];
          }
          else {
            preference.term = preferenceKeys[i].split("/")[3];
          }
          preference.value = dataAsJSONObject.default.preferences[preferenceKeys[i]];
          this.userpreferences.addUserPreference(preference);
        }
        // -----------------------------------------------------------
        // Additional CSS preferences unrelated to the user-preferences
        let preference = new UserPreference();
        preference.term = "height";
        preference.value = "100%";
        this.userpreferences.addUserPreference(preference);

        // Additional CSS preferences END ----------------------------
        this.setPreferencesLoaded(true);
      })
    }
    else {
      this.userpreferences = new UserPreferences();
      this.userpreferences.setName("default");

      let preference1 = new UserPreference();
      preference1.term = "fontSize";
      preference1.value = 15;

      let preference2 = new UserPreference();
      preference2.term = "sh2020SensFloor_helpUI";
      preference2.value = true;

      let preference3 = new UserPreference();
      preference3.term = "height";
      preference3.value = "100%";

      let preference4 = new UserPreference();
      preference4.term = "sh2020SensFloor_nightmode";
      preference4.value = false;

      this.userpreferences.addUserPreference(preference1);
      this.userpreferences.addUserPreference(preference2);
      this.userpreferences.addUserPreference(preference3);
      this.userpreferences.addUserPreference(preference4);

      this.setPreferencesLoaded(true);
    }

  }

  applyOpenAPEPreferencesToStyle() {
    let preferences = this.getUserPreferences();
    let style = {};
    for (let i = 0; i < preferences.length; i++) {
      switch (preferences[i].term) {
        case "fontSize":
          {
            if (preferences[i].value <= 12) // x-small
            {
              style["font-size"] = "x-small";
            }
            else if (preferences[i].value > 12 && preferences[i].value <= 14) {
              style["font-size"] = "small";
            }
            else if (preferences[i].value > 14 && preferences[i].value <= 16) {
              style["font-size"] = "medium";
            }
            else if (preferences[i].value > 16 && preferences[i].value <= 20) {
              style["font-size"] = "large";
            }
            else if (preferences[i].value > 20 && preferences[i].value <= 23) {
              style["font-size"] = "larger";
            }
            else {
              style["font-size"] = "x-large";
            }
            break;
          }
        case "sh2020SensFloor_helpUI":
          {
            if (preferences[i].value) {
              console.log("Help button activated");
              style["sh2020SensFloor_helpUI"] = true;
              this.helpButtonActive = true;
            }
            else
            {
              console.log("Help button activated");
              style["sh2020SensFloor_helpUI"] = false;
              this.helpButtonActive = false;
            }
            break;
          }
        case "highContrastEnabled":
          {
            if (preferences[i].value) {
              this.highContrastEnabled = true;
              console.log("High contrast enabled");
              let themestyle = preferences.find(pref => pref.term == "highContrastTheme");
              if (themestyle.value == "black-white") {
                style["background"] = "black";
                style["color"] = "white";
              }
            }
            break;
          }
        case "height":
          style["height"] = "100%";
          break;

        case "sh2020SensFloor_nightmode":
          style["sh2020SensFloor_nightmode"] = preferences[i].value;
          if (preferences[i].value) {
            style["background"] = "#333333";
            style["color"] = "white";
          }
          else {
            style["background"] = "white";
            style["color"] = "black";
          }
          break;
      }

    }
    this.style = style;
    this.preferencesApplied = true;
  }

  changeProperty(nameOfProperty, newValue) {
    if (nameOfProperty == "sh2020SensFloor_nightmode") {
      if (newValue) {
        this.style["sh2020SensFloor_nightmode"] = true;
        this.style["background"] = "#333333";
        this.style["color"] = "white";
      }
      else {
        this.style["sh2020SensFloor_nightmode"] = false;
        this.style["background"] = "white";
        this.style["color"] = "black";
      }
    }
    else if (nameOfProperty == "font-size") {
      this.style["font-size"] = newValue;
    }
    else if (nameOfProperty == "sh2020SensFloor_helpUI")
    {
      if (newValue)
      {
        this.style["sh2020SensFloor_helpUI"] = newValue;
        this.helpButtonActive = newValue;
      }
      else
      {
        this.style["sh2020SensFloor_helpUI"] = newValue;
        this.helpButtonActive = newValue;
      }
    }
  }
}
