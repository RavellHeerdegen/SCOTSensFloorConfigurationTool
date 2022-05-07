import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { DialogData } from 'src/app/classes/DialogData';

@Component({
  selector: 'app-confirmationdialog',
  templateUrl: './confirmationdialog.component.html',
  styleUrls: ['./confirmationdialog.component.css']
})
export class ConfirmationdialogComponent implements OnInit {

  additional_info_return: string | number | boolean;

  constructor(
    public dialogRef: MatDialogRef<ConfirmationdialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData) { }

  ngOnInit(): void {
  }

  onCancelClick(): void {
    let return_data = { confirmed: false, additional_info_return: this.additional_info_return, mode: "" };
    this.dialogRef.close(return_data);
  }

  onYesClick(): void {
    let return_data = {};
    if (this.data["mode"] == "create") {
      return_data = { confirmed: true, additional_info_return: this.additional_info_return, mode: "create" };
    }
    else if (this.data["mode"] == "delete") {
      return_data = { confirmed: true, additional_info_return: this.additional_info_return, mode: "delete" };
    }
    this.dialogRef.close(return_data);
  }
}
