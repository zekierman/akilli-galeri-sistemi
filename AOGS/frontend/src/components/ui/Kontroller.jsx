// Yeniden kullanılan UI yardımcıları — eski Factory Method'ların
// (kartOlustur, vurguluButonOlustur) React eşdeğerleri.

export function Kart({ baslik, aciklama, children, className = '' }) {
  return (
    <div className={`aogs-kart ${className}`}>
      {baslik && (
        <div className="mb-4">
          <h3 className="text-lg font-semibold text-aogs-metin">{baslik}</h3>
          {aciklama && <p className="text-sm text-aogs-metin-hafif mt-1">{aciklama}</p>}
        </div>
      )}
      {children}
    </div>
  );
}

export function IstatistikKarti({ baslik, deger, alt, vurguluRenk = 'birincil' }) {
  const renkSinif = {
    birincil: 'text-aogs-birincil',
    basari:   'text-aogs-basari',
    uyari:    'text-aogs-uyari',
    sari:     'text-aogs-uyariSari',
  }[vurguluRenk] ?? 'text-aogs-birincil';

  return (
    <div className="aogs-kart">
      <div className="text-sm text-aogs-metin-hafif">{baslik}</div>
      <div className={`text-3xl font-bold mt-2 ${renkSinif}`}>{deger}</div>
      {alt && <div className="text-xs text-aogs-metin-hafif mt-1">{alt}</div>}
    </div>
  );
}

export function Buton({ tip = 'birincil', children, ...props }) {
  const sinif = tip === 'birincil' ? 'aogs-buton-birincil' : 'aogs-buton-ikincil';
  return <button className={sinif} {...props}>{children}</button>;
}

export function Input({ etiket, hata, ...props }) {
  return (
    <div>
      {etiket && (
        <label className="block text-sm font-medium text-aogs-metin mb-1">{etiket}</label>
      )}
      <input className="aogs-input" {...props} />
      {hata && <p className="text-xs text-aogs-uyari mt-1">{hata}</p>}
    </div>
  );
}

export function Select({ etiket, secenekler = [], ...props }) {
  return (
    <div>
      {etiket && (
        <label className="block text-sm font-medium text-aogs-metin mb-1">{etiket}</label>
      )}
      <select className="aogs-input" {...props}>
        {secenekler.map((s) => (
          <option key={s.deger ?? s} value={s.deger ?? s}>{s.etiket ?? s}</option>
        ))}
      </select>
    </div>
  );
}

/** Durum rozeti — AracDurumu enum'una göre renklendirir. */
export function DurumRozeti({ durum }) {
  const stiller = {
    SATISTA:  'bg-green-100 text-green-800',
    SATILDI:  'bg-aogs-birincil/15 text-aogs-birincil',
    REZERVE:  'bg-yellow-100 text-yellow-800',
    SERVISTE: 'bg-blue-100 text-blue-800',
  };
  return (
    <span className={`aogs-rozet ${stiller[durum] ?? 'bg-gray-100 text-gray-700'}`}>
      {durum}
    </span>
  );
}

/** Basit Alert (eski JavaFX Alert popup'ı yerine, sayfa içi banner). */
export function Uyari({ tip = 'basari', mesaj, onKapat }) {
  if (!mesaj) return null;
  const sinif = {
    basari: 'bg-green-50 border-green-200 text-green-900',
    hata:   'bg-red-50 border-red-200 text-red-900',
    bilgi:  'bg-orange-50 border-orange-200 text-orange-900',
  }[tip];
  return (
    <div className={`flex items-start justify-between border rounded-lg px-4 py-3 mb-4 ${sinif}`}>
      <span>{mesaj}</span>
      {onKapat && (
        <button onClick={onKapat} className="ml-4 text-sm opacity-60 hover:opacity-100">
          ✕
        </button>
      )}
    </div>
  );
}
