import {
  ApplicationConfig,
  provideZoneChangeDetection,
  importProvidersFrom,
} from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import { icons } from './icons-provider';
import { provideNzIcons } from 'ng-zorro-antd/icon';
import { en_US, provideNzI18n } from 'ng-zorro-antd/i18n';
import { registerLocaleData } from '@angular/common';
import en from '@angular/common/locales/en';
import { FormsModule } from '@angular/forms';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { AutoRefreshTokenService, createInterceptorCondition, INCLUDE_BEARER_TOKEN_INTERCEPTOR_CONFIG, IncludeBearerTokenCondition, includeBearerTokenInterceptor, provideKeycloak, UserActivityService, withAutoRefreshToken } from 'keycloak-angular';
import { createMongoAbility, PureAbility } from '@casl/ability';

registerLocaleData(en);

const urlCondition = createInterceptorCondition<IncludeBearerTokenCondition>({
  urlPattern: /^(http:\/\/localhost:8085)(\/.*)?$/i,
  bearerPrefix: 'Bearer'
});

export const appConfig: ApplicationConfig = {
  providers: [
    provideKeycloak({
      config: {
        url: 'http://localhost:9080',
        realm: 'admin-management-system',
        clientId: 'admin-management-front-client'
      },
      initOptions: {
        //onLoad: 'check-sso',
        onLoad: "login-required",
        //silentCheckSsoRedirectUri: window.location.origin + '/public/silent-check-sso.html'
      },
      features: [
        withAutoRefreshToken({
          onInactivityTimeout: 'logout',
          sessionTimeout: 60000
        })
      ],
    }),
    provideZoneChangeDetection({ eventCoalescing: true }),

    provideRouter(routes),
    provideNzIcons(icons),
    provideNzI18n(en_US),
    importProvidersFrom(FormsModule),
    provideAnimationsAsync(),
    
    provideHttpClient(
      withInterceptors([
        includeBearerTokenInterceptor
      ])
    ),
    {
      provide: INCLUDE_BEARER_TOKEN_INTERCEPTOR_CONFIG,
      useValue: [
        urlCondition
      ]
    },
    {
      provide: PureAbility,
      useValue : createMongoAbility()
    },
    AutoRefreshTokenService,
    UserActivityService
  ],
};


