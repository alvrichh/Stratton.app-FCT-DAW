/** Utilidades de presentación compartidas por varias páginas. */

/** Iniciales (máximo 2 letras) de un nombre completo, para los avatares. */
export function iniciales(nombre: string | null | undefined): string {
  const partes = String(nombre ?? '').trim().split(/\s+/).filter(Boolean);
  if (!partes.length) {
    return '?';
  }
  const letras = partes.length === 1 ? partes[0].slice(0, 2) : partes[0][0] + partes[1][0];
  return letras.toUpperCase();
}

const ROLES: Record<string, string> = {
  ADMIN: 'Administrador',
  MANAGER: 'Jefe de equipo',
  USER: 'Asesor',
};

/** Nombre legible de un rol (ADMIN → Administrador). */
export function rolLegible(rol: string | null | undefined): string {
  return ROLES[String(rol ?? '')] ?? String(rol ?? '');
}

const ESTADOS: Record<string, string> = {
  ACTIVO: 'Activo',
  PENDIENTE: 'Pendiente',
  INACTIVO: 'K.O. / Inactivo',
};

/** Nombre legible de un estado de suministro. */
export function estadoLegible(estado: string | null | undefined): string {
  return ESTADOS[String(estado ?? '')] ?? String(estado ?? '');
}
