import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { HttpClientModule } from "@angular/common/http"
import { SelectUserComponent } from './component/select-user/select-user.component';
import { ConfigureSensorfloorComponent } from './component/configure-sensorfloor/configure-sensorfloor.component';
import { ConfigureObjectComponent } from './component/configure-object/configure-object.component';
import { GroupOptionsSelectionComponent } from './component/group-options-selection/group-options-selection.component';
import { ConfigureSelectActionComponent } from './component/configure-select-action/configure-select-action.component';
import { ChangeConfigurationComponent } from './component/change-configuration/change-configuration.component';
import { OverwriteConfigurationComponent } from './component/overwrite-configuration/overwrite-configuration.component';
import { SelectTypeComponent } from './component/select-type/select-type.component';
import { SelectObjectComponent } from './component/select-object/select-object.component';
import { ErrorComponent } from './component/error/error.component';
import { SettingsComponent } from './component/settings/settings.component';

const routes: Routes = [
  { path: "", component: SelectUserComponent },
  { path: "error", component: ErrorComponent },
  { path: "404", component: SelectUserComponent },
  { path: "configure-sensorfloor", component: ConfigureSensorfloorComponent },
  { path: "configure-object", component: ConfigureObjectComponent },
  { path: "configure-select-action", component: ConfigureSelectActionComponent },
  { path: "change-configuration", component: ChangeConfigurationComponent },
  { path: "overwrite-configuration", component: OverwriteConfigurationComponent },
  { path: "select-type", component: SelectTypeComponent },
  { path: "select-object", component: SelectObjectComponent },
  { path: "group-options-selection", component: GroupOptionsSelectionComponent },
  { path: "settings", component: SettingsComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes), HttpClientModule],
  exports: [RouterModule]
})
export class AppRoutingModule { }
