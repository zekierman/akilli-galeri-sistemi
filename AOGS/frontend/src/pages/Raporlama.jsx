import { useEffect, useState } from 'react';
import {
  BarChart, Bar, LineChart, Line, PieChart, Pie, Cell,
  XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer
} from 'recharts';
import TopBar from '../components/layout/TopBar';
import { Kart, IstatistikKarti, Input, Uyari, Buton } from '../components/ui/Kontroller';
import { raporApi } from '../api/client';
import {
  TURUNCU_PALET,
  GRAFIK_RENKLERI,
  formatTl,
  formatSayi,
  ayKisaltma,
} from '../theme/tokens';

/**
 * Raporlama ekranı.
 * Backend'den agrega rapor verilerini çeker ve Recharts ile görselleştirir.
 * Eski JavaFX'in TextArea tabanlı text raporlarının yerine, modern
 * etkileşimli grafikler kullanılır.
 *
 * v2: Renk paleti ve format yardımcıları `theme/tokens.js`'den gelir;
 * sayfa içinde hardcoded renk veya yerel format fonksiyonu yoktur.
 */
export default function Raporlama() {
  const [ozet, setOzet]                   = useState(null);
  const [aylikSatis, setAylikSatis]       = useState([]);
  const [markaSatis, setMarkaSatis]       = useState([]);
  const [odemeSekli, setOdemeSekli]       = useState([]);
  const [calisanPerf, setCalisanPerf]     = useState([]);
  const [yil, setYil]                     = useState(new Date().getFullYear());
  const [ggBaslangic, setGgBaslangic]     = useState('');
  const [ggBitis, setGgBitis]             = useState('');
  const [gelirGiderRaporu, setGelirGiderRaporu] = useState(null);
  const [hata, setHata]                   = useState(null);

  useEffect(() => {
    Promise.all([
      raporApi.ozet(),
      raporApi.markaSatis(),
      raporApi.odemeSekli(),
      raporApi.calisanPerf(),
    ])
      .then(([o, m, p, c]) => { setOzet(o); setMarkaSatis(m); setOdemeSekli(p); setCalisanPerf(c); })
      .catch(e => setHata(e.message));
  }, []);

  useEffect(() => {
    raporApi.aylikSatis(yil).then(setAylikSatis).catch(e => setHata(e.message));
  }, [yil]);

  const hesaplaGelirGider = async () => {
    if (!ggBaslangic || !ggBitis) {
      setHata("Lütfen başlangıç ve bitiş tarihlerini seçin.");
      return;
    }
    try {
      const baslangicIso = new Date(ggBaslangic).toISOString();
      const bitisIso = new Date(ggBitis + "T23:59:59").toISOString();
      const data = await raporApi.gelirGider(baslangicIso, bitisIso);
      setGelirGiderRaporu(data);
      setHata(null);
    } catch (e) {
      setHata("Gelir-gider raporu alınamadı: " + e.message);
    }
  };

  // Recharts Tooltip için tutar formatlayıcısı (TL ve adet ayrımı)
  const tooltipFormatter = (value, name) => {
    if (name === 'Tutar (₺)') return formatTl(value);
    if (typeof value === 'number') return formatSayi(value);
    return value;
  };

  return (
    <>
      <TopBar baslik="Raporlama" aciklama="İş zekâsı ve performans göstergeleri" />
      <div className="p-8 space-y-6">
        <Uyari tip="hata" mesaj={hata} onKapat={() => setHata(null)} />

        {/* Özet Kartları */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
          <IstatistikKarti baslik="Toplam Ciro" deger={formatTl(ozet?.toplamCiro)} vurguluRenk="basari" />
          <IstatistikKarti baslik="Toplam Kâr"  deger={formatTl(ozet?.toplamKar)}  vurguluRenk="birincil" />
          <IstatistikKarti baslik="Satış Adedi" deger={formatSayi(ozet?.satisAdet) ?? '—'} />
          <IstatistikKarti baslik="Stok Değeri" deger={formatTl(ozet?.stokDeger)} vurguluRenk="sari" />
        </div>

        {/* Gelir Gider Raporu */}
        <Kart>
          <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between mb-4 gap-4">
            <div>
              <h3 className="text-lg font-semibold">Gelir-Gider Analizi</h3>
              <p className="text-sm text-aogs-metin-hafif">Belirli tarih aralığındaki net durumu hesapla</p>
            </div>
            <div className="flex gap-3 items-end">
              <Input etiket="Başlangıç" type="date" value={ggBaslangic} onChange={(e) => setGgBaslangic(e.target.value)} />
              <Input etiket="Bitiş" type="date" value={ggBitis} onChange={(e) => setGgBitis(e.target.value)} />
              <Buton onClick={hesaplaGelirGider}>Hesapla</Buton>
            </div>
          </div>
          {gelirGiderRaporu && (
            <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 mt-4 bg-aogs-arkaplan p-6 rounded-xl border border-aogs-kenar">
              <div className="text-center sm:border-r border-aogs-kenar pb-4 sm:pb-0">
                <p className="text-sm font-medium text-aogs-metin-hafif mb-2">Toplam Gelir (Satışlar)</p>
                <p className="text-2xl font-bold text-aogs-birincil">{formatTl(gelirGiderRaporu.toplamGelir)}</p>
              </div>
              <div className="text-center sm:border-r border-aogs-kenar pb-4 sm:pb-0 border-t sm:border-t-0 pt-4 sm:pt-0">
                <p className="text-sm font-medium text-aogs-metin-hafif mb-2">Toplam Gider (Alışlar)</p>
                <p className="text-2xl font-bold text-aogs-uyari">{formatTl(gelirGiderRaporu.toplamGider)}</p>
              </div>
              <div className="text-center border-t sm:border-t-0 pt-4 sm:pt-0">
                <p className="text-sm font-medium text-aogs-metin-hafif mb-2">Net Kâr</p>
                <p className="text-2xl font-bold text-aogs-basari">{formatTl(gelirGiderRaporu.netKar)}</p>
              </div>
            </div>
          )}
        </Kart>

        {/* Aylık Satış Trendi */}
        <Kart>
          <div className="flex items-center justify-between mb-4">
            <div>
              <h3 className="text-lg font-semibold">Aylık Satış Trendi</h3>
              <p className="text-sm text-aogs-metin-hafif">Belirli yıldaki ay-bazlı satış adedi ve tutarı</p>
            </div>
            <div className="flex gap-2 items-center">
              <Input type="number" value={yil} onChange={(e) => setYil(parseInt(e.target.value))}
                     style={{ width: 100 }} />
            </div>
          </div>
          <ResponsiveContainer width="100%" height={300}>
            <LineChart data={aylikSatis}>
              <CartesianGrid strokeDasharray="3 3" stroke={GRAFIK_RENKLERI.izgara} />
              <XAxis dataKey="ay" stroke={GRAFIK_RENKLERI.eksen} tickFormatter={ayKisaltma} />
              <YAxis stroke={GRAFIK_RENKLERI.eksen} />
              <Tooltip formatter={tooltipFormatter} labelFormatter={ayKisaltma} />
              <Legend />
              <Line type="monotone" dataKey="adet"  stroke={GRAFIK_RENKLERI.birincil} strokeWidth={2} name="Satış Adedi" />
              <Line type="monotone" dataKey="tutar" stroke={GRAFIK_RENKLERI.ikincil}  strokeWidth={2} name="Tutar (₺)" />
            </LineChart>
          </ResponsiveContainer>
        </Kart>

        {/* İki sütun: Marka satış + Ödeme şekli */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          <Kart baslik="🏆 En Çok Satılan Markalar">
            <ResponsiveContainer width="100%" height={300}>
              <BarChart data={markaSatis}>
                <CartesianGrid strokeDasharray="3 3" stroke={GRAFIK_RENKLERI.izgara} />
                <XAxis dataKey="marka" stroke={GRAFIK_RENKLERI.eksen} />
                <YAxis stroke={GRAFIK_RENKLERI.eksen} />
                <Tooltip formatter={tooltipFormatter} />
                <Bar dataKey="adet" fill={GRAFIK_RENKLERI.birincil} radius={[8, 8, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </Kart>

          <Kart baslik="💳 Ödeme Şekli Dağılımı">
            <ResponsiveContainer width="100%" height={300}>
              <PieChart>
                <Pie data={odemeSekli} dataKey="adet" nameKey="odemeSekli"
                     cx="50%" cy="50%" outerRadius={100} label>
                  {odemeSekli.map((_, i) => (
                    <Cell key={i} fill={TURUNCU_PALET[i % TURUNCU_PALET.length]} />
                  ))}
                </Pie>
                <Tooltip formatter={tooltipFormatter} />
                <Legend />
              </PieChart>
            </ResponsiveContainer>
          </Kart>
        </div>

        {/* Çalışan Performans Tablosu */}
        <Kart baslik="👤 Çalışan Performansı" className="overflow-x-auto p-0">
          <table className="w-full text-sm">
            <thead className="bg-aogs-arkaplan border-b border-aogs-kenar">
              <tr className="text-left text-aogs-metin-hafif">
                <th className="px-4 py-3">Çalışan</th>
                <th className="px-4 py-3 text-right">Satış Adedi</th>
                <th className="px-4 py-3 text-right">Toplam Ciro</th>
                <th className="px-4 py-3 text-right">Toplam Kâr</th>
              </tr>
            </thead>
            <tbody>
              {calisanPerf.map((c) => (
                <tr key={c.calisanId} className="border-b border-aogs-kenar/50">
                  <td className="px-4 py-3 font-medium">{c.ad} {c.soyad}</td>
                  <td className="px-4 py-3 text-right">{formatSayi(c.adet)}</td>
                  <td className="px-4 py-3 text-right font-semibold text-aogs-birincil">{formatTl(c.ciro)}</td>
                  <td className="px-4 py-3 text-right text-aogs-basari">{formatTl(c.kar)}</td>
                </tr>
              ))}
              {!calisanPerf.length && (
                <tr><td colSpan={4} className="text-center py-8 text-aogs-metin-hafif">
                  Henüz çalışan satışı yok.
                </td></tr>
              )}
            </tbody>
          </table>
        </Kart>
      </div>
    </>
  );
}
