import {Component, ViewEncapsulation} from '@angular/core';

@Component({
  selector: 'operation1',
  encapsulation: ViewEncapsulation.None,
  styles: [require('./operation1.scss')],
  template: require('./operation1.html'),
})
export class Operation1 {

  constructor() {
  }
}
