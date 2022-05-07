import { Component, OnInit } from '@angular/core';
import { OpenAPEService } from 'src/app/service/open-ape.service';

@Component({
  selector: 'app-styler',
  templateUrl: './styler.component.html',
  styleUrls: ['./styler.component.css']
})
export class StylerComponent implements OnInit {

  constructor(private openapeservice: OpenAPEService) { }

  ngOnInit(): void {
  }

  styleMaster() {
    if (this.openapeservice.preferencesApplied) {
      return this.openapeservice.style;
    }
    else {
      return "";
    }
  }

  buttonStyle() {
    if (this.openapeservice.highContrastEnabled) {
      return "high-contrast-button";
    }
    else {
      return "";
    }
  }
}
