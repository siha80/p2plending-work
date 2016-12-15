import {Component, ViewEncapsulation} from '@angular/core';
import {FormGroup, AbstractControl, FormBuilder, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {LoginService} from './login.service'
import 'rxjs/Rx';

@Component({
  selector: 'login',
  encapsulation: ViewEncapsulation.None,
  styles: [require('./login.scss')],
  template: require('./login.html'),
})
export class Login {

  public form:FormGroup;
  public userId:AbstractControl;
  public password:AbstractControl;
  public submitted:boolean = false;

  constructor(
    private router: Router,
    private loginService: LoginService,
    fb:FormBuilder
  ) {
    this.form = fb.group({
      'userId': ['', Validators.compose([Validators.required, Validators.minLength(4)])],
      'password': ['', Validators.compose([Validators.required, Validators.minLength(4)])]
    });

    this.userId = this.form.controls['userId'];
    this.password = this.form.controls['password'];
  }

  public onSubmit(values:Object):void {
    this.submitted = true;
    if (this.form.valid) {
      this.loginService.login(this.userId.value, this.password.value)
        .then(result => {

          if(result.resultCd == '0000'){
            this.router.navigate(['/dashboard']);
          } else if(result.resultCd == '7001') {
            if(confirm("미등록 사용자 입니다. 관리자에게 사용 승인 요청을 하시겠습니까?")){
              this.loginService.addUser(this.userId.value).then(addResult => {
                if(typeof addResult.resultCd == 'undefined') {
                  alert("사용자 등록 요청 중 에러가 발생하였습니다. 관리자에게 요청 부탁 드립니다.");
                } else {
                  alert(addResult.resultMsg);
                }

              });
            }
          } else if(typeof result.resultCd == 'undefined') {
            alert("서버와의 통신이 원할하지 않습니다.");
          } else {
            alert(result.resultMsg);
          }

        });
    }
  }
}
