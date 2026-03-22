export async function askAI(message: string) {
  try {
    // Fíjate que no haya espacios extra en la URL
    const res = await fetch(
      `http://localhost:8080/api/v1/ai/recommend?pref=${encodeURIComponent(message)}`
    );

    if (!res.ok) {
      return "La IA está descansando ahora mismo, prueba en un momento.";
    }

    return await res.text();
  } catch (error) {
    return "Error: No puedo conectar con el servidor de JourneyMate.";
  }
}