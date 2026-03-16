/**
 * Convierte un objeto Date de JS a String YYYY-MM-DD sin problemas de zona horaria.
 */
export const formatDateForBackend = (date: Date | string): string => {
  const d = new Date(date);
  const year = d.getFullYear();
  const month = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`; // Siempre devuelve YYYY-MM-DD local
};

/**
 * Formateador de moneda único para toda la app.
 */
export const formatCurrency = (amount: number, currency: string = 'EUR') => {
  return new Intl.NumberFormat('es-ES', {
    style: 'currency',
    currency: currency,
    minimumFractionDigits: 2, // Fuerza 2 decimales siempre
    maximumFractionDigits: 2,
  }).format(amount);
};