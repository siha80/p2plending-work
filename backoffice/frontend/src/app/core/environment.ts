///<reference path="../../../node_modules/@types/node/index.d.ts"/>

import { LocationStrategy, HashLocationStrategy } from '@angular/common';

let PROVIDERS: any[] = [
  { provide: LocationStrategy, useClass: HashLocationStrategy }
];

if (process.env.ENV === 'production') {
  // Production
  PROVIDERS = [
    ...PROVIDERS,
    { provide: 'baseUrl', useValue: 'http://localhost:3000/' }
  ];
} else {
  // Development
  PROVIDERS = [
    ...PROVIDERS,
    { provide: 'baseUrl', useValue: 'http://localhost:3000/' }
  ];
}

export const ENV_PROVIDERS = [
  ...PROVIDERS
];
