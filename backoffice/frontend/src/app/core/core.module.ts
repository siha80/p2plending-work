import { NgModule, ModuleWithProviders, Optional, SkipSelf } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ENV_PROVIDERS } from './environment';
import { AuthService } from './auth.service';
import { AuthGuard } from './auth.guard';
import { SecureApi } from './secure.api';

@NgModule({
  imports: [CommonModule]
})
export class CoreModule {
  static forRoot(): ModuleWithProviders {
    return {
      ngModule: CoreModule,
      providers: [ENV_PROVIDERS, AuthService, AuthGuard, SecureApi]
    };
  }

  constructor( @Optional() @SkipSelf() parentModule: CoreModule) {
    if (parentModule) {
      throw new Error(
        'CoreModule is already loaded. Import it in the AppModule only');
    }
  }
}
