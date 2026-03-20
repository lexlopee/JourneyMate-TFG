import { useState } from 'react';
import { Sparkles, X, Send, Bot, Loader2, Map, Wallet } from 'lucide-react';

export const AITravelAssistant = () => {
  const [isOpen, setIsOpen] = useState(false);
  const [preference, setPreference] = useState('');
  const [budget, setBudget] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [recommendation, setRecommendation] = useState('');

  const handleAskAI = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!preference || !budget) return;

    setIsLoading(true);
    setRecommendation('');

    try {
      // Ajusta la URL base según dónde corra tu Spring Boot (ej: http://localhost:8080)
      const response = await fetch(`http://localhost:8080/api/v1/ai/recommend?pref=${encodeURIComponent(preference)}&budget=${encodeURIComponent(budget)}`);
      
      if (!response.ok) throw new Error('Error en la API');
      
      const text = await response.text();
      setRecommendation(text);
    } catch (error) {
      console.error("Error al consultar la IA:", error);
      setRecommendation("Ups, mi cerebro de IA está tomando un descanso. Intenta de nuevo más tarde.");
    } finally {
      setIsLoading(false);
    }
  };

  // Función para renderizar el texto de Gemini limpiando los asteriscos **
  const formatAIResponse = (text: string) => {
    return text.split('\n').map((line, index) => {
      if (!line.trim()) return <br key={index} />;
      // Limpiamos la negrita de markdown de forma básica para que se vea limpio
      const cleanLine = line.replace(/\*\*/g, '');
      return <p key={index} className="mb-2 text-sm">{cleanLine}</p>;
    });
  };

  return (
    <div className="fixed bottom-6 right-6 z-50">
      {/* VENTANA DEL CHAT */}
      {isOpen && (
        <div className="absolute bottom-20 right-0 w-80 md:w-96 bg-white/90 rounded-3xl shadow-2xl border border-teal-100 overflow-hidden flex flex-col transform transition-all animate-fade-in">
          
          {/* Cabecera */}
          <div className="bg-teal-900 p-4 text-white flex justify-between items-center">
            <div className="flex items-center gap-2">
              <Sparkles size={18} className="text-teal-300" />
              <span className="font-black tracking-widest uppercase text-sm">JourneyMate AI</span>
            </div>
            <button onClick={() => setIsOpen(false)} className="text-teal-300 hover:text-white transition-colors">
              <X size={20} />
            </button>
          </div>

          {/* Área de Mensajes */}
          <div className="p-5 flex-1 min-h-[250px] max-h-[400px] overflow-y-auto bg-teal-50/50">
            
            {/* Mensaje Inicial */}
            <div className="flex gap-3 mb-6">
              <div className="w-8 h-8 rounded-full bg-teal-100 flex items-center justify-center flex-shrink-0 text-teal-600">
                <Bot size={18} />
              </div>
              <div className="bg-white/90 p-3 rounded-2xl rounded-tl-none border border-teal-100 shadow-sm text-sm text-teal-900">
                ¡Hola! Soy tu asistente de viajes inteligente. Cuéntame qué tipo de viaje buscas y con qué presupuesto, y te daré una recomendación experta.
              </div>
            </div>

            {/* Respuesta de la IA */}
            {isLoading && (
              <div className="flex gap-3 mb-6 animate-pulse">
                <div className="w-8 h-8 rounded-full bg-teal-100 flex items-center justify-center flex-shrink-0 text-teal-600">
                  <Loader2 size={18} className="animate-spin" />
                </div>
                <div className="bg-white/90 p-3 rounded-2xl rounded-tl-none border border-teal-100 shadow-sm text-sm text-teal-600 italic">
                  Analizando destinos perfectos para ti...
                </div>
              </div>
            )}

            {recommendation && !isLoading && (
              <div className="flex gap-3 mb-6">
                <div className="w-8 h-8 rounded-full bg-teal-500 flex items-center justify-center flex-shrink-0 text-white shadow-md">
                  <Sparkles size={16} />
                </div>
                <div className="bg-white/90 p-4 rounded-2xl rounded-tl-none border border-teal-200 shadow-md text-teal-900">
                  {formatAIResponse(recommendation)}
                </div>
              </div>
            )}
          </div>

          {/* Formulario de Entrada */}
          <form onSubmit={handleAskAI} className="p-4 bg-white/90 border-t border-teal-100 space-y-3">
            <div className="flex items-center gap-2 bg-teal-50 rounded-xl px-3 py-2 border border-teal-100">
              <Map size={16} className="text-teal-500" />
              <input 
                type="text" 
                placeholder="Ej: Playa y relax, Aventura..." 
                className="bg-transparent w-full text-sm outline-none text-teal-900 placeholder-teal-400"
                value={preference}
                onChange={(e) => setPreference(e.target.value)}
                disabled={isLoading}
              />
            </div>
            <div className="flex gap-2">
              <div className="flex-1 flex items-center gap-2 bg-teal-50 rounded-xl px-3 py-2 border border-teal-100">
                <Wallet size={16} className="text-teal-500" />
                <input 
                  type="text" 
                  placeholder="Ej: Bajo, Medio, Lujo..." 
                  className="bg-transparent w-full text-sm outline-none text-teal-900 placeholder-teal-400"
                  value={budget}
                  onChange={(e) => setBudget(e.target.value)}
                  disabled={isLoading}
                />
              </div>
              <button 
                type="submit" 
                disabled={isLoading || !preference || !budget}
                className="bg-teal-600 text-white p-3 rounded-xl hover:bg-teal-500 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
              >
                <Send size={18} />
              </button>
            </div>
          </form>

        </div>
      )}

      {/* BOTÓN FLOTANTE PARA ABRIR EL CHAT */}
      <button 
        onClick={() => setIsOpen(!isOpen)}
        className={`w-14 h-14 rounded-full flex items-center justify-center shadow-2xl transition-all duration-300 hover:scale-110 active:scale-95 ${isOpen ? 'bg-red-400 text-white' : 'bg-teal-900 text-white'}`}
      >
        {isOpen ? <X size={24} /> : <Bot size={28} />}
      </button>
    </div>
  );
};