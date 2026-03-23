/**
 * Convierte un objeto Date o String a YYYY-MM-DD.
 * Garantiza que la fecha enviada al Java sea exacta a la seleccionada por el usuario.
 */
export const formatDateForBackend = (date: Date | string | undefined): string => {
  if (!date) return '';

  // 1. Si ya es un string YYYY-MM-DD (proveniente de un <input type="date">), devolverlo tal cual.
  if (typeof date === 'string' && /^\d{4}-\d{2}-\d{2}$/.test(date)) {
    return date;
  }

  // 2. Procesar el objeto Date usando la hora local del navegador.
  const d = new Date(date);
  
  // Verificación de fecha válida para evitar "NaN-NaN-NaN"
  if (isNaN(d.getTime())) return '';

  const year = d.getFullYear();
  const month = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  
  return `${year}-${month}-${day}`;
};

/**
 * Formateador de moneda internacional (ISO 4217).
 * Usa el estándar es-ES para mostrar 1.250,50 € en lugar de 1250.50.
 */
export const formatCurrency = (amount: number | string | undefined, currency: string = 'EUR'): string => {
  // Convertimos a número por si el Backend envía el precio como String
  const numericValue = typeof amount === 'string' ? parseFloat(amount) : amount;
  const value = numericValue || 0;

  try {
    return new Intl.NumberFormat('es-ES', {
      style: 'currency',
      currency: currency.toUpperCase(), // Forzamos mayúsculas (EUR, USD)
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    }).format(value);
  } catch (error) {
    // Fallback en caso de que la moneda no sea un código ISO válido
    return `${value.toFixed(2)} ${currency}`;
  }
};