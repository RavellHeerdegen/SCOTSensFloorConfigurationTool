import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from "@angular/forms";

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { SelectUserComponent } from './component/select-user/select-user.component';
import { ConfigureSensorfloorComponent } from './component/configure-sensorfloor/configure-sensorfloor.component';
import { ConfigureObjectComponent } from './component/configure-object/configure-object.component';
import { ConfigureSelectActionComponent } from './component/configure-select-action/configure-select-action.component';
import { ChangeConfigurationComponent } from './component/change-configuration/change-configuration.component';
import { SelectObjectComponent } from './component/select-object/select-object.component';
import { OverwriteConfigurationComponent } from './component/overwrite-configuration/overwrite-configuration.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { GroupOptionsSelectionComponent } from './component/group-options-selection/group-options-selection.component';
import { HelpdialogComponent } from './component/helpdialog/helpdialog.component';

// Material Design Dependencies
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from "@angular/material/icon";
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDialogModule, MatDialogRef } from "@angular/material/dialog";
import { StylerComponent } from './component/styler/styler.component';
import { SelectTypeComponent } from './component/select-type/select-type.component';
import { MatListModule } from '@angular/material/list';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatSliderModule } from '@angular/material/slider';
import { ConfirmationdialogComponent } from './component/confirmationdialog/confirmationdialog.component';
import { MatSelectModule } from '@angular/material/select';
import { ErrorComponent } from './component/error/error.component';
import { SettingsComponent } from './component/settings/settings.component';
import { MatRadioModule } from '@angular/material/radio';
import { OverwriteConfirmationDialogComponent } from './component/overwrite-confirmation-dialog/overwrite-confirmation-dialog.component';


@NgModule({
  declarations: [
    AppComponent,
    SelectUserComponent,
    ConfigureSensorfloorComponent,
    ConfigureObjectComponent,
    GroupOptionsSelectionComponent,
    HelpdialogComponent,
    StylerComponent,
    ConfigureSelectActionComponent,
    ChangeConfigurationComponent,
    SelectTypeComponent,
    SelectObjectComponent,
    OverwriteConfigurationComponent,
    ConfirmationdialogComponent,
    ErrorComponent,
    SettingsComponent,
    OverwriteConfirmationDialogComponent
  ],
  imports: [
    BrowserModule,
    MatListModule,
    MatSliderModule,
    MatSlideToggleModule,
    FormsModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    MatButtonToggleModule,
    MatIconModule,
    MatInputModule,
    MatRadioModule,
    MatFormFieldModule,
    MatDialogModule,
    MatSelectModule,
    MatCheckboxModule
  ],
  entryComponents: [
    HelpdialogComponent
  ],
  providers: [{ provide: MatDialogRef, useValue: {} }, { provide: StylerComponent }],
  bootstrap: [AppComponent]
})
export class AppModule { }
