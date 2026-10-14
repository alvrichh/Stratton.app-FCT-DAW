import { Directive, Input } from '@angular/core';
import { AbstractControl, NG_VALIDATORS, ValidationErrors, Validator } from '@angular/forms';

/**
 * Validador de fecha máxima para <input type="date"> con ngModel
 * (Angular solo aplica el atributo max a los inputs numéricos).
 * Uso: <input type="date" [appMaxDate]="'2024-12-31'" ...>
 */
@Directive({
  selector: 'input[type=date][appMaxDate][ngModel]',
  standalone: true,
  providers: [{ provide: NG_VALIDATORS, useExisting: MaxDateDirective, multi: true }],
})
export class MaxDateDirective implements Validator {
  @Input() appMaxDate = '';

  validate(control: AbstractControl): ValidationErrors | null {
    const valor = control.value as string | null;
    if (!valor || !this.appMaxDate) {
      return null;
    }
    return valor > this.appMaxDate ? { maxDate: { max: this.appMaxDate, actual: valor } } : null;
  }
}
