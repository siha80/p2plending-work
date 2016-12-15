import {Component, ViewEncapsulation} from '@angular/core';

@Component({
  selector: 'report1',
  encapsulation: ViewEncapsulation.None,
  styles: [require('./report1.scss')],
  template: require('./report1.html'),
})
export class Report1 {

  constructor() {
  }
}
