import {Component, ViewEncapsulation} from '@angular/core';
import {FormGroup, AbstractControl, FormBuilder} from '@angular/forms';

import {BoMenuService} from './bomenu.service'
import {LocalDataSource} from 'ng2-smart-table';

@Component({
  selector: 'bomenu',
  encapsulation: ViewEncapsulation.None,
  styles: [require('./bomenu.scss')],
  template: require('./bomenu.html'),
})
export class BoMenu {

  public searchForm:FormGroup;
  public menuId:AbstractControl;
  public menuNm:AbstractControl;
  public path:AbstractControl;
  public submitted:boolean = false;

  query: string = '';

  settings = {
    mode:'external',
    hideSubHeader:true,
    pager:{
      perPage:'10'
    },
    actions:{
      add:false,
      edit:false,
      delete:false
    },
    noDataMessage:'데이터가 없습니다.',
    columns: {
      menuId: {
        title: '메뉴아이디',
        type: 'number'
      },
      menuNm: {
        title: '메뉴명',
        type: 'string'
      },
      fullPath: {
        title: '경로',
        type: 'string'
      },
      level: {
        title: '레벨',
        type: 'number'
      }
    }
  };

  source: LocalDataSource = new LocalDataSource();

  constructor(
    private boMenuService: BoMenuService,
    fb:FormBuilder
  ) {
    this.searchForm = fb.group({
      'menuId': '',
      'menuNm': '',
      'path': ''
    });

    this.menuId = this.searchForm.controls['menuId'];
    this.menuNm = this.searchForm.controls['menuNm'];
    this.path = this.searchForm.controls['path'];

    this.getMenuList(this.searchForm.value);
  }

  public getMenuList(values:Object):void {
    this.submitted = true;
    this.boMenuService.getMenuList(values)
      .then(result => {
        this.source.load(result);
      });

  }
}
