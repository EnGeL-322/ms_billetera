import { HttpInterceptorFn } from '@angular/common/http';
import { environment } from 'src/environments/environment';

export const urlInterceptor: HttpInterceptorFn = (req, next) => {
  if (!req.url.startsWith('http')) {
    const apiUrl = environment.apiUrl;

    const modifiedReq = req.clone({
      url: `${apiUrl}/${req.url}`
    });

    return next(modifiedReq);
  }

  return next(req);
};
