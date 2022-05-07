import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute, ParamMap } from '@angular/router';
import { OpenAPEService } from 'src/app/service/open-ape.service';
import { BackendService } from 'src/app/service/backend.service';

@Component({
  selector: 'app-select-user',
  templateUrl: './select-user.component.html',
  styleUrls: ['./select-user.component.css']
})
export class SelectUserComponent implements OnInit {

  private contextIdAchim = "5ed0d8d8e5ed8c4fdfd26f35"
  private contextIdElisabeth = "5ed0ccbde5ed8c4fdfd26f32"
  private contextIdHans = "5ed0d92de5ed8c4fdfd26f36"

  isLoading = false;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private openapeservice: OpenAPEService,
    private backendservice: BackendService
  ) { }

  ngOnInit(): void {
    this.loadAllAvailableObjects();
  }

  async loadAllAvailableObjects() {
    this.isLoading = true;
    await this.backendservice.loadAllAvailableObjects();
    this.isLoading = false;
  }

  async getUserCarpetConfiguration(name: string) {
    this.isLoading = true;
    await this.backendservice.loadUserConfiguration(name);
    this.isLoading = false;
  }

  async navigateByUsername(name: string) {
    // TODO remove this line
    this.openapeservice.useOpenAPE = true;

    if (name == "Achim") {
      this.openapeservice.loginUser(name, this.contextIdAchim);
    }

    if (name == "Elisabeth") {
      this.openapeservice.loginUser(name, this.contextIdElisabeth);
    }

    if (name == "Hans") {
      this.openapeservice.loginUser(name, this.contextIdHans);
    }

    // Backend call to identify and load user config
    await this.getUserCarpetConfiguration(name).then(data => {
      this.router.navigate(["/configure-sensorfloor"]);
    });
  }
}
