import { Injectable } from '@angular/core';
import { SensorfloorObject } from '../classes/SensorfloorObject';
import { HttpClient, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest, HttpResponse, HttpHeaders, HttpClientModule, HttpParams } from '@angular/common/http';
import { PluginObject } from '../classes/PluginObject';
import { SensorfloorConfiguration } from '../classes/SensorfloorConfiguration';
import { SensorfloorGroup } from '../classes/SensorfloorGroup';
import { Field } from '../classes/Field';
import { SensorfloorAction } from '../classes/SensorfloorAction';
import { SensorfloorActionParam } from '../classes/SensorfloorActionParam';

@Injectable({
  providedIn: 'root'
})
export class BackendService {

  basepath = "/";
  private availableObjects: Array<SensorfloorObject>;
  private userCarpetConfiguration: SensorfloorConfiguration;
  constructor(private http: HttpClient) {
  }

  public getAvailableObjects() {
    return this.availableObjects;
  }

  public getUserCarpetConfiguration() {
    return this.userCarpetConfiguration;
  }

  public async getUserCarpetConfigurationRefreshed(name: string) {
    await this.loadUserConfiguration(name);
    return this.userCarpetConfiguration;
  }

  async addActionToGroup(action) {
    let actionId;

    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })
    };

    await this.http.post(this.basepath + "action", action, httpOptions).toPromise().then(data => {
      actionId = data;
    });
    return actionId;
  }

  async changeActionParameters(new_action_config) {
    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })
    };

    await this.http.put(this.basepath + "action", new_action_config, httpOptions).toPromise().then(data => {
    });
  }

  async loadUserConfiguration(name: string) {

    await this.http.get(this.basepath + "config" + "?userId=" + name).toPromise().then(data => {
      let jsonconfiguration = JSON.parse(JSON.stringify(data));
      let userconfiguration = new SensorfloorConfiguration();
      userconfiguration.setId(jsonconfiguration["id"]);
      userconfiguration.setUserId(jsonconfiguration["userID"]);
      userconfiguration.setCarpetGroups(new Array<SensorfloorGroup>());

      for (let i = 0; i < jsonconfiguration["carpetGroups"].length; i++) {
        // Create Sensorfloor groups
        let group = new SensorfloorGroup();
        group.setgroupID(jsonconfiguration["carpetGroups"][i].id);
        group.setGroupName(jsonconfiguration["carpetGroups"][i].groupName);

        // Create all fields
        for (let j = 0; j < jsonconfiguration["carpetGroups"][i].fields.length; j++) {
          let field = new Field();
          field.setX(jsonconfiguration["carpetGroups"][i].fields[j].x);
          field.setY(jsonconfiguration["carpetGroups"][i].fields[j].y);
          group.getFields().push(field);
        }

        // Create all step on actions
        for (let j = 0; j < jsonconfiguration["carpetGroups"][i].actionsStepOn.length; j++) {
          let action = new SensorfloorAction();
          action.setActionName(jsonconfiguration["carpetGroups"][i].actionsStepOn[j].actionName);
          action.setActionId(jsonconfiguration["carpetGroups"][i].actionsStepOn[j].id);
          action.setPluginId(jsonconfiguration["carpetGroups"][i].actionsStepOn[j].pluginID);
          action.setActivation("stepOn");
          let paramkeys = Object.keys(jsonconfiguration["carpetGroups"][i].actionsStepOn[j].parameters);

          // Create all step on action params
          for (let k = 0; k < paramkeys.length; k++) {
            let param = new SensorfloorActionParam();
            param.term = paramkeys[k];
            param.value = jsonconfiguration["carpetGroups"][i].actionsStepOn[j].parameters[paramkeys[k]];
            action.getParams().push(param);
          }
          group.getActionsStepOn().push(action);
        }

        for (let j = 0; j < jsonconfiguration["carpetGroups"][i].actionsStepOff.length; j++) {
          let action = new SensorfloorAction();
          action.setActionName(jsonconfiguration["carpetGroups"][i].actionsStepOff[j].actionName);
          action.setActionId(jsonconfiguration["carpetGroups"][i].actionsStepOff[j].id);
          action.setPluginId(jsonconfiguration["carpetGroups"][i].actionsStepOff[j].pluginID);
          action.setActivation("stepOff");
          let paramkeys = Object.keys(jsonconfiguration["carpetGroups"][i].actionsStepOff[j].parameters);

          // Create all step on action params
          for (let k = 0; k < paramkeys.length; k++) {
            let param = new SensorfloorActionParam();
            param.term = paramkeys[k];
            param.value = jsonconfiguration["carpetGroups"][i].actionsStepOff[j].parameters[paramkeys[k]];
            action.getParams().push(param);
          }
          group.getActionsStepOff().push(action);
        }
        userconfiguration.getCarpetGroups().push(group);
      }
      this.userCarpetConfiguration = userconfiguration;
    });
  }

  async loadAllAvailableObjects() {
    this.availableObjects = new Array<SensorfloorObject>();
    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })
    };

    await this.http.get(this.basepath + "objects", httpOptions).toPromise().then(data => {
      let dataAsJSONObject = JSON.parse(JSON.stringify(data));
      for (let i = 0; i < dataAsJSONObject.length; i++) {
        let sensorfloorobject = new SensorfloorObject(); // Plugin
        sensorfloorobject.setObjects(new Array<PluginObject>());
        sensorfloorobject.setPluginId(dataAsJSONObject[i].pluginId);
        for (let j = 0; j < dataAsJSONObject[i].objects.length; j++) {
          let object = new PluginObject(); // Objects like Front lamp, back lamp
          object.setId(dataAsJSONObject[i].objects[j].id);
          object.setName(dataAsJSONObject[i].objects[j].name);
          sensorfloorobject.getObjects().push(object);
        }
        this.availableObjects.push(sensorfloorobject);
      }
    });
  }

  async removeAction(actionid: number) {
    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      }),
      body: {
        actionId: actionid
      }
    };
    await this.http.delete(this.basepath + "action", httpOptions).toPromise().then(data => {
    });
  }

  async createNewGroup(group): Promise<number> {
    let jsongroup = JSON.stringify(group);
    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })
    };
    let groupid;
    await this.http.post(this.basepath + "group", jsongroup, httpOptions).toPromise().then(data => {
      // returns the groupid which we can give the selected tiles now
      groupid = data;
    })
    return groupid;
  }

  async changeGroup(group) {
    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })
    };
    await this.http.put(this.basepath + "group", group, httpOptions).toPromise().then(data => {
    });
  }

  async deleteGroup(groupid: number) {
    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      }),
      body: {
        groupId: groupid
      }
    };

    await this.http.delete(this.basepath + "group", httpOptions).toPromise().then(data => {
    });
  }
}
