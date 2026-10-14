import { Directive, ElementRef, HostListener } from '@angular/core';

/**
 * Al pulsar Enter en un campo de formulario, pasa el foco al siguiente campo
 * en lugar de enviar el formulario (sustituye al antiguo focusNextInput() global).
 */
@Directive({
  selector: 'form[appEnterNext]',
  standalone: true,
})
export class EnterNextDirective {
  constructor(private el: ElementRef<HTMLFormElement>) {}

  @HostListener('keydown.enter', ['$event'])
  onEnter(event: KeyboardEvent): void {
    const target = event.target as HTMLElement;
    if (!target || target.tagName === 'TEXTAREA' || target.tagName === 'BUTTON') {
      return;
    }
    const campos = Array.from(this.el.nativeElement.querySelectorAll<HTMLElement>('input, select, textarea'))
      .filter(c => !(c as HTMLInputElement).disabled);
    const indice = campos.indexOf(target);
    if (indice >= 0 && indice < campos.length - 1) {
      event.preventDefault();
      campos[indice + 1].focus();
    }
  }
}
