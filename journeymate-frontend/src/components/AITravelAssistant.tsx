import { useState } from 'react';
import { Sparkles, X, Send, Bot, Loader2, FileText } from 'lucide-react';
import html2pdf from 'html2pdf.js';

const renderFormattedText = (text: string) => {
  if (!text) return null;

  return text.split('\n').map((line, index) => {
    if (line.startsWith('###')) {
      return (
        <h3 key={index} className="text-base font-black text-teal-900 mt-6 mb-2 border-b border-teal-100 pb-1">
          {line.replace('###', '').trim()}
        </h3>
      );
    }

    const parts = line.split(/(\*\*.*?\*\*)/g);
    const formattedLine = parts.map((part, i) => {
      if (part.startsWith('**') && part.endsWith('**')) {
        return (
          <strong key={i} className="font-black text-teal-800 bg-teal-100/50 px-1 rounded">
            {part.slice(2, -2)}
          </strong>
        );
      }
      return part;
    });

    return line.trim() === '' ? (
      <br key={index} />
    ) : (
      <p key={index} className="mb-3 text-teal-900/80">
        {formattedLine}
      </p>
    );
  });
};

export const AITravelAssistant = () => {
  const [isOpen, setIsOpen] = useState(false);
  const [mode, setMode] = useState<'recommend' | 'plan'>('recommend');
  const [preference, setPreference] = useState('');
  const [budget, setBudget] = useState('');
  const [query, setQuery] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [recommendation, setRecommendation] = useState('');

  const handleDownloadPDF = () => {
    if (!recommendation) return;

    const element = document.createElement('div');
    element.style.padding = '40px';
    element.style.width = '170mm';
    element.style.backgroundColor = '#ffffff';
    element.style.color = '#134e4a';
    element.style.fontFamily = 'Arial, sans-serif';

    // 1. LIMPIEZA DEL TEXTO (Quitar saludos y preguntas finales)
    const lines = recommendation.split('\n');

    // Encontramos el índice de la última línea que realmente es contenido (no pregunta)
    let lastValidIndex = lines.length;
    for (let i = lines.length - 1; i >= 0; i--) {
      const line = lines[i].toLowerCase();
      // Si la línea tiene un signo de interrogación o frases de cierre, seguimos subiendo
      if (line.includes('?') ||
        line.includes('¿te gustaría') ||
        line.includes('cuéntame') ||
        line.includes('quieres que')) {
        lastValidIndex = i;
      } else if (line.trim() !== '') {
        // En cuanto encontramos una línea con texto real que no es pregunta, paramos de borrar
        break;
      }
    }

    const contentHtml = lines
      .slice(0, lastValidIndex) // Cortamos lo que sobra al final
      .filter(line => {
        const cleanLine = line.trim();
        // Filtro de saludos iniciales
        const isGreeting =
          cleanLine.toLowerCase().startsWith('aquí tienes') ||
          cleanLine.toLowerCase().startsWith('¡hola') ||
          cleanLine.toLowerCase().startsWith('claro') ||
          cleanLine.toLowerCase().startsWith('perfecto');

        return !isGreeting;
      })
      .map(line => {
        if (line.startsWith('###')) {
          return `<h2 style="color:#0d9488; border-bottom:1px solid #ccfbf1; padding-bottom:5px; margin-top:20px; font-family:Arial; font-size: 18px;">${line.replace(/###/g, '').trim()}</h2>`;
        }
        if (line.trim() === '') return '<br/>';
        const formattedLine = line.replace(/\*\*(.*?)\*\*/g, '<b style="color:#115e59;">$1</b>');
        return `<p style="margin-bottom:8px; line-height:1.5; font-family:Arial; font-size: 14px;">${formattedLine}</p>`;
      })
      .join('');

    // 2. ESTRUCTURA DEL DOCUMENTO
    element.innerHTML = `
    <div style="border-bottom: 2px solid #134e4a; margin-bottom: 20px; padding-bottom: 10px; text-align: center;">
      <h1 style="margin:0; color:#134e4a; font-family:Arial; font-size: 22px;">Plan de Viaje Oficial</h1>
      <p style="margin:5px 0 0 0; color:#0d9488; font-size:10px; font-family:Arial; font-weight:bold; letter-spacing:1px;">GENERADO POR JOURNEYMATE AI</p>
    </div>
    <div style="padding: 10px;">
      ${contentHtml}
    </div>
    <div style="margin-top:40px; text-align:center; color:#999; font-size:10px; border-top:1px solid #eee; padding-top:10px;">
      Este itinerario fue creado de forma personalizada para tu viaje. ¡Buen viaje!
    </div>
  `;

    // 3. CONFIGURACIÓN (CON AS CONST)
    const opt = {
      margin: 15,
      filename: 'Itinerario_Limpio_JourneyMate.pdf',
      image: { type: 'jpeg' as const, quality: 0.98 },
      html2canvas: { scale: 2, useCORS: true, backgroundColor: '#ffffff' },
      jsPDF: { unit: 'mm' as const, format: 'a4' as const, orientation: 'portrait' as const }
    };

    setTimeout(() => {
      html2pdf().set(opt).from(element).save();
    }, 100);
  };

  const handleAskAI = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    setRecommendation('');

    try {
      const url = mode === 'recommend'
        ? `http://localhost:8080/api/v1/ai/recommend?pref=${encodeURIComponent(preference)}&budget=${encodeURIComponent(budget)}`
        : `http://localhost:8080/api/v1/ai/itinerary?query=${encodeURIComponent(query)}`;

      const response = await fetch(url);
      const text = await response.text();
      setRecommendation(text);
    } catch {
      setRecommendation("Se me ha perdido el mapa... Inténtalo de nuevo.");
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="fixed bottom-6 right-6 z-50">
      {isOpen && (
        <div className="absolute bottom-20 right-0 w-80 md:w-[450px] bg-white rounded-3xl shadow-2xl border border-teal-100 overflow-hidden flex flex-col transition-all">

          <div className="bg-teal-900 p-4">
            <div className="flex justify-between items-center mb-3">
              <div className="flex items-center gap-2">
                <Sparkles className="text-teal-400" size={18} />
                <span className="text-white font-black text-xs uppercase tracking-widest">JourneyMate AI</span>
              </div>
              <X className="text-teal-300 cursor-pointer hover:rotate-90 transition-transform" onClick={() => setIsOpen(false)} />
            </div>

            <div className="flex bg-teal-800/40 p-1 rounded-xl">
              <button
                onClick={() => {
                  setMode('recommend');
                  setRecommendation('');
                }}
                className={`flex-1 py-1.5 text-[10px] font-black rounded-lg transition-all ${mode === 'recommend' ? 'bg-teal-500 text-white shadow-lg' : 'text-teal-300'}`}
              >
                DESCUBRIR
              </button>
              <button
                onClick={() => {
                  setMode('plan');
                  setRecommendation('');
                }}
                className={`flex-1 py-1.5 text-[10px] font-black rounded-lg transition-all ${mode === 'plan' ? 'bg-teal-500 text-white shadow-lg' : 'text-teal-300'}`}
              >
                ITINERARIO
              </button>
            </div>
          </div>

          <div className="p-5 h-[350px] overflow-y-auto bg-teal-50/20 relative">

            {recommendation && !isLoading && (
              <button
                onClick={handleDownloadPDF}
                className="absolute top-4 right-4 bg-teal-600 text-white px-3 py-2 rounded-xl shadow-lg hover:bg-teal-500 transition-all flex items-center gap-2 z-10 animate-in fade-in zoom-in"
              >
                <FileText size={14} />
                <span className="text-[10px] font-black uppercase tracking-widest">PDF</span>
              </button>
            )}

            {isLoading ? (
              <div className="flex flex-col items-center justify-center h-full gap-3 text-teal-600">
                <Loader2 className="animate-spin" size={32} />
                <p className="text-[10px] font-black uppercase tracking-widest">Trazando ruta...</p>
              </div>
            ) : recommendation ? (
              <div
                id="itinerary-content"
                style={{ backgroundColor: 'white', color: '#0f4447' }}
                className="prose prose-sm leading-relaxed font-medium pt-8 px-6"
              >
                <div className="mb-6 border-b-2 border-teal-500 pb-2">
                  <h1 style={{ color: '#115e59' }} className="text-2xl font-bold">Mi Plan de Viaje</h1>
                  <p style={{ color: '#0d9488' }} className="text-xs uppercase tracking-widest font-bold">JourneyMate AI</p>
                </div>
                {renderFormattedText(recommendation)}
              </div>
            ) : (
              <div className="text-center text-teal-800/40 mt-10">
                <Bot size={40} className="mx-auto mb-3 opacity-20" />
                <p className="text-xs font-bold uppercase tracking-widest">
                  {mode === 'recommend' ? '¿A dónde quieres ir hoy?' : 'Cuéntame y te haré un plan detallado'}
                </p>
              </div>
            )}
          </div>

          <form onSubmit={handleAskAI} className="p-4 bg-white border-t border-teal-100 space-y-3">
            {mode === 'recommend' ? (
              <div className="flex gap-2">
                <input
                  type="text"
                  placeholder="Preferencia (ej. Playa)"
                  className="flex-[2] bg-teal-50 rounded-xl px-4 py-3 text-xs outline-none border border-teal-100 focus:border-teal-300 transition-colors"
                  value={preference}
                  onChange={(e) => setPreference(e.target.value)}
                />
                <input
                  type="text"
                  placeholder="Presupuesto (ej. Bajo)"
                  className="flex-1 bg-teal-50 rounded-xl px-4 py-3 text-xs outline-none border border-teal-100 focus:border-teal-300 transition-colors"
                  value={budget}
                  onChange={(e) => setBudget(e.target.value)}
                />
                <button
                  type="submit"
                  disabled={!preference || !budget}
                  className="bg-teal-600 text-white p-3 rounded-xl hover:bg-teal-500 disabled:opacity-50 transition-colors"
                >
                  <Send size={18} />
                </button>
              </div>
            ) : (
              <div className="flex gap-2">
                <input
                  type="text"
                  placeholder="Ej: 7 días en Madrid para 4 personas..."
                  className="flex-grow bg-teal-50 rounded-xl px-4 py-3 text-xs outline-none border border-teal-100 focus:border-teal-300 transition-colors"
                  value={query}
                  onChange={(e) => setQuery(e.target.value)}
                />
                <button
                  type="submit"
                  disabled={!query}
                  className="bg-teal-600 text-white p-3 rounded-xl hover:bg-teal-500 disabled:opacity-50 transition-colors"
                >
                  <Send size={18} />
                </button>
              </div>
            )}
          </form>
        </div>
      )}

      <button
        onClick={() => setIsOpen(!isOpen)}
        className="w-16 h-16 rounded-full bg-teal-900 text-white flex items-center justify-center shadow-2xl hover:scale-110 transition-transform active:scale-95"
      >
        {isOpen ? <X /> : <Bot size={28} />}
      </button>
    </div>
  );
};