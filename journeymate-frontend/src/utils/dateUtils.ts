export const formatDateForBackend = (date: Date | string | undefined): string => {
  if (!date) return '';

  if (typeof date === 'string' && /^\d{4}-\d{2}-\d{2}$/.test(date)) {
    return date;
  }

  const d = new Date(date);
  
  if (isNaN(d.getTime())) return '';

  const year = d.getFullYear();
  const month = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  
  return `${year}-${month}-${day}`;
};


export const formatCurrency = (amount: number | string | undefined, currency: string = 'EUR'): string => {
  const numericValue = typeof amount === 'string' ? parseFloat(amount) : amount;
  const value = numericValue || 0;

  try {
    return new Intl.NumberFormat('es-ES', {
      style: 'currency',
      currency: currency.toUpperCase(),
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    }).format(value);
  } catch (error) {
    return `${value.toFixed(2)} ${currency}`;
  }
};

export const formatTimeForBackend = (time1?: string, time2?: string): string => {
  const t = (time1 || time2 || "").trim();
  
  if (!t) return "10:00:00"; 
  
  if (/^\d{2}:\d{2}$/.test(t)) {
    return `${t}:00`;
  }
  
  if (/^\d{2}:\d{2}:\d{2}$/.test(t)) {
    return t;
  }

  return "10:00:00";
};