import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { DialogData } from 'src/app/classes/DialogData';

@Component({
  selector: 'app-helpdialog',
  templateUrl: './helpdialog.component.html',
  styleUrls: ['./helpdialog.component.css']
})
export class HelpdialogComponent implements OnInit {

  constructor(public dialogRef: MatDialogRef<HelpdialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData) { }

  ngOnInit(): void {
  }

  closeDialog()
  {
    this.dialogRef.close();
  }
}
