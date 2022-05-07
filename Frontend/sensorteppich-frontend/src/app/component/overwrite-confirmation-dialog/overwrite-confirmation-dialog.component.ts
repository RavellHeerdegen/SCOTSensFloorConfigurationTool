import { Component, OnInit } from '@angular/core';
import { ConfigureService } from 'src/app/service/configure.service';

@Component({
  selector: 'app-overwrite-confirmation-dialog',
  templateUrl: './overwrite-confirmation-dialog.component.html',
  styleUrls: ['./overwrite-confirmation-dialog.component.css']
})
export class OverwriteConfirmationDialogComponent implements OnInit {

  groupname: string;
  configureService: ConfigureService;

  constructor(
    public configureservice: ConfigureService) {
    this.configureService = configureservice;
  }

  ngOnInit(): void {
    this.groupname = this.configureService.getChosenGroupName();
  }
}
