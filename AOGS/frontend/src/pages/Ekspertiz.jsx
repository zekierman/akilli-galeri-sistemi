import { useEffect, useState } from 'react';
import TopBar from '../components/layout/TopBar';
import { Kart, Buton, Input, Select, Uyari } from '../components/ui/Kontroller';
import { ekspertizApi, aracApi, calisanApi } from '../api/client';

const PARCALAR = ['Motor', 'Şasi', 'Kaput', 'Bagaj', 'Sağ Çamurluk', 'Sol Çamurluk',
                  'Ön Tampon', 'Arka Tampon', 'Sağ Kapı', 'Sol Kapı', 'Tavan'];

export default function Ekspertiz() {
  const [araclar, setAraclar] = useState([]);
  const [calisanlar, setCalisanlar] = useState([]);
  const [ekspertizler, setEkspertizler] = useState([]);
  const [hata, setHata] = useState(null);
  const [basari, setBasari] = useState(null);

  const [aracId, setAracId] = useState('');
  const [calisanId, setCalisanId] = useState('');
  const [degerlendirme, setDegerlendirme] = useState('');
  const [parcaDurumlari, setParcaDurumlari] = useState({});
  const [duzenlenenId, setDuzenlenenId] = useState(null);

  useEffect(() => {
    aracApi.liste().then(setAraclar).catch(e => setHata(e.message));
    calisanApi.liste().then(setCalisanlar).catch(e => console.error("Çalışanlar çekilemedi"));
    yenile();
  }, []);

  const yenile = () => ekspertizApi.liste().then(setEkspertizler).catch(e => setHata(e.message));

  const submit = async (e) => {
    e.preventDefault();
    setHata(null); setBasari(null);
    if (!aracId) { setHata('Araç seçiniz.'); return; }
    
    // Jackson polimorfizm serileştirmesi için @type alanını bulup eklememiz gerekiyor.
    const secilenArac = araclar.find(a => a.aracId === aracId);
    const aracTipi = secilenArac ? secilenArac['@type'] : 'OTOMOBIL';

    try {
      if (duzenlenenId) {
        await ekspertizApi.guncelle(duzenlenenId, {
          arac: { aracId, "@type": aracTipi }, 
          calisan: calisanId ? { calisanId } : null,
          genelDegerlendirme: degerlendirme, 
          parcaDurumlari
        });
        setBasari('Ekspertiz raporu güncellendi.');
        setDuzenlenenId(null);
      } else {
        await ekspertizApi.ekle({
          arac: { aracId, "@type": aracTipi }, 
          calisan: calisanId ? { calisanId } : null,
          genelDegerlendirme: degerlendirme, 
          parcaDurumlari
        });
        setBasari('Ekspertiz raporu kaydedildi.');
      }
      setAracId(''); setCalisanId(''); setDegerlendirme(''); setParcaDurumlari({});
      yenile();
    } catch (err) { setHata(err.message); }
  };

  const sil = async (id) => {
    if (!window.confirm('Bu raporu silmek istediğinize emin misiniz?')) return;
    try {
      await ekspertizApi.sil(id);
      setBasari('Ekspertiz başarıyla silindi.');
      yenile();
    } catch (err) {
      setHata(err.message);
    }
  };

  const duzenleModunaGec = (e) => {
    setDuzenlenenId(e.ekspertizId);
    setAracId(e.arac?.aracId || '');
    setCalisanId(e.calisan?.calisanId || '');
    setDegerlendirme(e.genelDegerlendirme || '');
    setParcaDurumlari(e.parcaDurumlari || {});
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const iptalEt = () => {
    setDuzenlenenId(null);
    setAracId(''); setCalisanId(''); setDegerlendirme(''); setParcaDurumlari({});
    setHata(null);
  };

  const parcaTikla = (parca) => {
    const seviyeler = ['HASARSIZ', 'BOYALI', 'DEGISEN', 'AGIR_HASARLI'];
    const mevcut = parcaDurumlari[parca];
    let yeniSeviye = null;

    if (!mevcut) {
      yeniSeviye = 'HASARSIZ';
    } else {
      const idx = seviyeler.indexOf(mevcut);
      if (idx >= 0 && idx < seviyeler.length - 1) {
        yeniSeviye = seviyeler[idx + 1];
      }
    }

    const yeni = { ...parcaDurumlari };
    if (yeniSeviye) yeni[parca] = yeniSeviye;
    else delete yeni[parca];
    setParcaDurumlari(yeni);
  };

  const getRenkStr = (parca) => {
    const h = parcaDurumlari[parca];
    if (h === 'HASARSIZ') return { fill: '#22c55e80', stroke: '#22c55e' };
    if (h === 'BOYALI') return { fill: '#eab30880', stroke: '#eab308' };
    if (h === 'DEGISEN') return { fill: '#f9731680', stroke: '#f97316' };
    if (h === 'AGIR_HASARLI') return { fill: '#ef444480', stroke: '#ef4444' };
    return { fill: '#374151', stroke: '#4b5563' };
  };

  const SvgParca = ({ parca, d }) => {
    const r = getRenkStr(parca);
    return (
      <g onClick={() => parcaTikla(parca)} className="cursor-pointer group">
        <path
          d={d}
          fill={r.fill}
          stroke={r.stroke}
          strokeWidth="2"
          className="transition-all duration-300 group-hover:brightness-125 hover:stroke-white"
        />
        {/* Parça ismini araç üzerine yazmak karışık durabilir, o yüzden tooltip veya seçili listesi eklenebilir */}
      </g>
    );
  };

  return (
    <>
      <TopBar baslik="Ekspertiz" aciklama="Dijital ekspertiz raporu oluştur ve geçmişi gör" />
      <div className="p-8 grid grid-cols-1 xl:grid-cols-2 gap-6">
        <Kart baslik={duzenlenenId ? "Ekspertiz Düzenle" : "Yeni Ekspertiz"}>
          <Uyari tip="hata"   mesaj={hata}   onKapat={() => setHata(null)} />
          <Uyari tip="basari" mesaj={basari} onKapat={() => setBasari(null)} />
          <form onSubmit={submit} className="space-y-4">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <Select etiket="Araç *"
                      secenekler={[{ deger: '', etiket: '— Araç seçin —' },
                                    ...araclar.map(a => ({ deger: a.aracId, etiket: `${a.plakaNo} — ${a.marka} ${a.model}` }))]}
                      value={aracId} onChange={(e) => setAracId(e.target.value)} required disabled={!!duzenlenenId} />
                      
              <Select etiket="Ekspertizi Yapan Çalışan"
                      secenekler={[{ deger: '', etiket: '— Çalışan seçin (Opsiyonel) —' },
                                    ...calisanlar.map(c => ({ deger: c.calisanId, etiket: `${c.ad} ${c.soyad} (${c.pozisyon})` }))]}
                      value={calisanId} onChange={(e) => setCalisanId(e.target.value)} />
            </div>

            <div className="border-t border-aogs-kenar pt-4 mt-4">
              <div className="flex justify-between items-start lg:items-center mb-6">
                <div>
                  <p className="text-sm font-medium">İnteraktif Araç Şeması</p>
                  <p className="text-[10px] text-aogs-metin-hafif mt-1">Parçalara tıklayarak durumu belirleyin</p>
                </div>
                <div className="text-[10px] flex flex-wrap gap-1 w-[120px] justify-end font-medium">
                  <span className="px-1.5 py-0.5 bg-green-500/10 text-green-600 rounded border border-green-500/50">Hasarsız</span>
                  <span className="px-1.5 py-0.5 bg-yellow-500/10 text-yellow-600 rounded border border-yellow-500/50">Boyalı</span>
                  <span className="px-1.5 py-0.5 bg-orange-500/10 text-orange-600 rounded border border-orange-500/50">Değişen</span>
                  <span className="px-1.5 py-0.5 bg-red-500/10 text-red-600 rounded border border-red-500/50">Ağır</span>
                </div>
              </div>

              <div className="flex flex-col lg:flex-row gap-8 items-center justify-center p-6 bg-white rounded-xl border border-aogs-kenar shadow-sm">
                
                {/* SVG Araç */}
                <svg viewBox="0 0 200 450" className="w-full max-w-[220px] drop-shadow-2xl">
                  {/* Şasi Zemin */}
                  <rect x="25" y="30" width="150" height="390" rx="40" fill="#1f2937" stroke="#374151" strokeWidth="2" />

                  {/* Tekerlekler */}
                  <rect x="15" y="80" width="14" height="45" rx="4" fill="#030712" />
                  <rect x="171" y="80" width="14" height="45" rx="4" fill="#030712" />
                  <rect x="15" y="300" width="14" height="45" rx="4" fill="#030712" />
                  <rect x="171" y="300" width="14" height="45" rx="4" fill="#030712" />

                  <SvgParca parca="Ön Tampon" d="M 40 40 Q 100 15 160 40 L 165 55 Q 100 45 35 55 Z" />
                  <SvgParca parca="Kaput" d="M 55 60 L 145 60 L 135 140 L 65 140 Z" />
                  <SvgParca parca="Sol Çamurluk" d="M 31 60 L 50 60 L 60 140 L 26 140 Z" />
                  <SvgParca parca="Sağ Çamurluk" d="M 169 60 L 150 60 L 140 140 L 174 140 Z" />

                  {/* Camlar */}
                  <path d="M 40 145 L 160 145 L 140 170 L 60 170 Z" fill="#0f172a" />
                  <path d="M 60 270 L 140 270 L 160 295 L 40 295 Z" fill="#0f172a" />

                  <SvgParca parca="Tavan" d="M 65 175 L 135 175 L 135 265 L 65 265 Z" />
                  <SvgParca parca="Sol Kapı" d="M 26 145 L 50 145 L 50 295 L 26 295 Z" />
                  <SvgParca parca="Sağ Kapı" d="M 174 145 L 150 145 L 150 295 L 174 295 Z" />
                  <SvgParca parca="Bagaj" d="M 30 300 L 170 300 L 165 375 L 35 375 Z" />
                  <SvgParca parca="Arka Tampon" d="M 35 380 L 165 380 L 160 395 Q 100 410 40 395 Z" />
                </svg>

                {/* Seçili Parçaların Listesi (Görseli desteklemek için) */}
                <div className="flex-1 w-full max-w-[250px] flex flex-col gap-2">
                  <h4 className="text-xs font-semibold border-b border-aogs-kenar pb-2 mb-2 text-aogs-metin">
                    Mekanik Aksamlar <span className="text-[9px] text-aogs-metin-hafif font-normal ml-1">(Tıklayarak Değiştir)</span>
                  </h4>
                  <div className="grid grid-cols-2 gap-2 mb-4">
                    <button type="button" onClick={() => parcaTikla('Motor')} className={`flex flex-col items-center justify-center py-3 rounded border transition-all cursor-pointer ${parcaDurumlari['Motor'] ? 'bg-aogs-birincil/5 border-aogs-birincil text-aogs-birincil' : 'bg-gray-50 border-aogs-kenar text-aogs-metin hover:bg-gray-100'}`}>
                      <span className="text-xs font-semibold mb-1">Motor</span>
                      <span className={`font-bold inline-block px-2 py-0.5 rounded text-[10px] ${parcaDurumlari['Motor'] === 'BOYALI' ? 'bg-yellow-500/10 text-yellow-600 border border-yellow-500/50' : parcaDurumlari['Motor'] === 'DEGISEN' ? 'bg-orange-500/10 text-orange-600 border border-orange-500/50' : parcaDurumlari['Motor'] === 'AGIR_HASARLI' ? 'bg-red-500/10 text-red-600 border border-red-500/50' : parcaDurumlari['Motor'] === 'HASARSIZ' ? 'bg-green-500/10 text-green-600 border border-green-500/50' : 'bg-gray-200 text-gray-500'}`}>
                        {parcaDurumlari['Motor'] || 'Seçilmedi'}
                      </span>
                    </button>
                    <button type="button" onClick={() => parcaTikla('Şasi')} className={`flex flex-col items-center justify-center py-3 rounded border transition-all cursor-pointer ${parcaDurumlari['Şasi'] ? 'bg-aogs-birincil/5 border-aogs-birincil text-aogs-birincil' : 'bg-gray-50 border-aogs-kenar text-aogs-metin hover:bg-gray-100'}`}>
                      <span className="text-xs font-semibold mb-1">Şasi</span>
                      <span className={`font-bold inline-block px-2 py-0.5 rounded text-[10px] ${parcaDurumlari['Şasi'] === 'BOYALI' ? 'bg-yellow-500/10 text-yellow-600 border border-yellow-500/50' : parcaDurumlari['Şasi'] === 'DEGISEN' ? 'bg-orange-500/10 text-orange-600 border border-orange-500/50' : parcaDurumlari['Şasi'] === 'AGIR_HASARLI' ? 'bg-red-500/10 text-red-600 border border-red-500/50' : parcaDurumlari['Şasi'] === 'HASARSIZ' ? 'bg-green-500/10 text-green-600 border border-green-500/50' : 'bg-gray-200 text-gray-500'}`}>
                        {parcaDurumlari['Şasi'] || 'Seçilmedi'}
                      </span>
                    </button>
                  </div>

                  <h4 className="text-xs font-semibold border-b border-aogs-kenar pb-2 mb-2 text-aogs-metin">Hasarlı Dış Parçalar</h4>
                  <ul className="text-xs space-y-1 max-h-[200px] overflow-y-auto pr-2">
                    {Object.entries(parcaDurumlari).filter(([p,v]) => p !== 'Motor' && p !== 'Şasi' && v !== 'HASARSIZ').length === 0 && (
                      <li className="text-aogs-metin-hafif">Tüm parçalar sağlam veya seçilmedi.</li>
                    )}
                    {Object.entries(parcaDurumlari).filter(([p,v]) => p !== 'Motor' && p !== 'Şasi' && v !== 'HASARSIZ').map(([p, v]) => (
                      <li key={p} className="flex justify-between items-center py-1.5 border-b border-aogs-kenar/50 text-aogs-metin">
                        <span>{p}</span>
                        <span className={`px-2 py-0.5 rounded border text-[10px] font-bold ${v === 'BOYALI' ? 'bg-yellow-500/10 text-yellow-600 border-yellow-500/50' : v === 'DEGISEN' ? 'bg-orange-500/10 text-orange-600 border-orange-500/50' : 'bg-red-500/10 text-red-600 border-red-500/50'}`}>{v}</span>
                      </li>
                    ))}
                  </ul>
                </div>

              </div>
            </div>

            <Input etiket="Genel Değerlendirme"
                   value={degerlendirme} onChange={(e) => setDegerlendirme(e.target.value)} />
            <div className="flex gap-2 pt-2">
              <Buton type="submit" className="flex-1">{duzenlenenId ? "Güncelle" : "Ekspertizi Kaydet"}</Buton>
              {duzenlenenId && <Buton type="button" tip="ikincil" onClick={iptalEt}>İptal</Buton>}
            </div>
          </form>
        </Kart>

        <Kart baslik="Ekspertiz Geçmişi" className="overflow-x-auto p-0">
          <table className="w-full text-sm">
            <thead className="bg-aogs-arkaplan border-b border-aogs-kenar">
              <tr className="text-left text-aogs-metin-hafif">
                <th className="px-4 py-3">Tarih</th>
                <th className="px-4 py-3">Araç</th>
                <th className="px-4 py-3">Uzman</th>
                <th className="px-4 py-3 text-right">Puan</th>
                <th className="px-4 py-3">İşlem</th>
              </tr>
            </thead>
            <tbody>
              {ekspertizler.map((e) => (
                <tr key={e.ekspertizId} className="border-b border-aogs-kenar/50">
                  <td className="px-4 py-3 text-xs">{new Date(e.ekspertizTarihi).toLocaleString('tr-TR')}</td>
                  <td className="px-4 py-3">{e.arac?.plakaNo ?? '-'}</td>
                  <td className="px-4 py-3 text-xs">{e.calisan ? `${e.calisan.ad} ${e.calisan.soyad.charAt(0)}.` : '-'}</td>
                  <td className="px-4 py-3 text-right font-semibold text-aogs-birincil">
                    {Number(e.ekspertizPuani).toFixed(1)} / 100
                  </td>
                  <td className="px-4 py-3 flex gap-3">
                    <button onClick={() => duzenleModunaGec(e)} className="text-aogs-birincil hover:underline text-xs font-medium">Düzenle</button>
                    <button onClick={() => sil(e.ekspertizId)} className="text-aogs-uyari hover:underline text-xs">Sil</button>
                  </td>
                </tr>
              ))}
              {!ekspertizler.length && (
                <tr><td colSpan={4} className="text-center py-8 text-aogs-metin-hafif">Ekspertiz yok.</td></tr>
              )}
            </tbody>
          </table>
        </Kart>
      </div>
    </>
  );
}
